/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.client.charts.window.ChartWindowView;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.common.comp.BreadCrumbMenuItem;
import com.ericsson.eniq.events.ui.client.common.widget.EventGridView;
import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.ToolBarURLChangeDataType;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.ericsson.eniq.events.ui.client.events.ChangeChartGridEventHandler;
import com.ericsson.eniq.events.ui.client.events.SucessResponseEvent;
import com.ericsson.eniq.events.ui.client.events.SucessResponseEventHandler;
import com.google.gwt.http.client.Response;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.common.client.json.MetaDataParserUtils.GRID_DISPLAY;
import static com.ericsson.eniq.events.ui.client.common.Constants.DISPLAY_TYPE_PARAM;
import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;
import static com.ericsson.eniq.events.ui.client.common.Constants.OUT_BOUND_CHART_DISPLAY_PARAM;
import static com.ericsson.eniq.events.ui.client.common.Constants.OUT_BOUND_GRID_DISPLAY_PARAM;

/**
 * 
 * 
 * For Toolbar menu item (View) on floating window resulting in change to contained window.
 * This is graph to graph, grid to grid, chart drilldown to grid all rolled up in one
 * glorious master piece. 
 * 
 * Handle request to change chart (e.g. from roaming by country to roaming by operator)
 * form window tool-bar item when all changes are charts, i.e used for ChartWindowPresenter. 
 * 
 * Also required to be registered by grids when use can use a view menu 
 * to switch from a chart to a grid and vice versa or simply from one grid to a new grid   
 * 
 * Vital that eventID of new window (e.g. ROAMING_BY_COUNTRY) 
 * can be used to pick up chartMetaData in meta data file 
 * When new chart to display is the same type (bar replaces bar) then 
 * we can just replace URL, changing metadata 
 * assuming meta data eventID can be use to look up new chartMetaData, and refresh 
 * 
 * 
 * @author eeicmsy
 * @since June 2010
 *
 */

public class ChartGridChangeEventHandler implements ChangeChartGridEventHandler {

    private static final Logger LOGGER = Logger.getLogger(ChartGridChangeEventHandler.class.getName());

    /* There count control added to gui yet so hfor now it is added via glassfish (services) params.
    / When complete there should be a UI control we know we have which tells us to pick up the count param, 
     * we can then use perhaps --  urlInfo.addOutBoundParameter("count=", "20");
     */

    @SuppressWarnings("unchecked")
    private final BaseWindowPresenter win;

    private MultipleInstanceWinId multiWinId = null;

    private final EventBus eventBus;

    /* access exposed for junit */
    boolean wasFormallyAGrid;

    private final ServerSuccessResponseHandler waitingForServerHandler = new ServerSuccessResponseHandler();

    /**
     * Handle changing display of a window though View menu item selection on the window.
     * So used for changing to same chart times (e.g. roaming charts), or changing 
     * from charts to grids
     * 
     * @param eventBus        The MVP singleton event bus
     * @param winPresenter    ChartWindowPresenter or EventGridViewPresenter
     */
    @SuppressWarnings("unchecked")
    public ChartGridChangeEventHandler(final EventBus eventBus, final BaseWindowPresenter winPresenter) {

        this.win = winPresenter;
        this.eventBus = eventBus;
    }

    @Override
    public void handleChangeGridChart(final MultipleInstanceWinId multiWinID, final EventType eventID,
                                      final ToolBarURLChangeDataType urlInfo, final String chartClickDrillInfo, String searchFieldVal) {

        // guard
        if (!getMultipleInstanceWinId().isThisWindowGuardCheck(multiWinID)) {
            return;
        }

        urlInfo.keepExistingRegularParams(win.getSearchURLParameters());
        win.setIsDrillDown(false);

        if (urlInfo.getTempTimeInfoDataType() == null){
            urlInfo.replaceTimeDrillParams(win.getWindowTimeDate());
        }else{
            urlInfo.replaceTimeParams(urlInfo.getTempTimeInfoDataType());
        }


        final boolean isChangingToSameTypeOfChart = isChart(urlInfo.windowType)
                && win.getDisplayType().equals(urlInfo.displayType);

        final boolean isChangingToAGrid = isGrid(urlInfo.windowType) && (urlInfo.displayType).equals(GRID_DISPLAY);

        if (isChangingToSameTypeOfChart) {

            changeToSameTypeOfChart(eventID, urlInfo);

        } else if (isChangingToAGrid) {

            changeToGrid(eventID, urlInfo, searchFieldVal + " : " + chartClickDrillInfo);

        } else {

            // TODO 
            /* e.g. changing from a say a bar chart to another kind of chart from the view menu */
            LOGGER.log(Level.INFO, "Functionality not included to date to switch graph types from window view menu");
            //XXX Window.alert("Functionality not included");
        }

    }

    private MultipleInstanceWinId getMultipleInstanceWinId() {

        if (multiWinId == null) {
            // multi-instance window will never change search field
            multiWinId = win.getMultipleInstanceWinId();
        }
        return multiWinId;
    }

    public  String getSearchFieldVal(){
        return (getMultipleInstanceWinId().getSearchInfo() != null) ? getMultipleInstanceWinId().getSearchInfo().searchFieldVal : EMPTY_STRING;
    }

    /* 
     * e.g. swap horizontal chart for horizontal chart,
     * or roaming chart for operator chart, etc 
     */
    private void changeToSameTypeOfChart(final EventType eventID, final ToolBarURLChangeDataType urlInfo) {
        final boolean isDisplayGrid = isDisplayGrid();
        if (isDisplayGrid) {
            //Should clean the bread crumbs so that request sends data as per widgetSpecificParams. TR-HO21508.
            win.clearCurrentBreadCrumbMenuItem();
        }
        // in case was display=grid following toggle
        urlInfo.addOutBoundParameter(DISPLAY_TYPE_PARAM, OUT_BOUND_CHART_DISPLAY_PARAM);

        // extra to cater for view menu changing default time component value window launched with 
        // (if null reverts time  to last saved)
        win.setTempTimeData(urlInfo.getTempTimeInfoDataType());

        refreshWinSameDisplayTypeNewURL(eventID, urlInfo);

        /* if coming from being a grid previously  */
        if (isDisplayGrid) {
            /* we will have to delay handling the graph to grid conversion until
             * we have the new data - otherwise we would see a chart attempting to use 
             * the drill down data (following by the correct chart), when choose from the 
             * view menu (e.g. terminal groups functionality)
             */
            wasFormallyAGrid = true;
            final GridInfoDataType columns = ((EventGridView) win.getView()).getColumns();
            // not GRID_COLUMN_VIEW or GRID_VIEW in EventGridPresenter 
            // (avoiding double launch button remove by ChangeGridViewEvent fire)
            if (columns != null) {
                columns.gridType = "ABOUT_TO_BE_CHART";
            }
            registerHandlerForSuccessServerResult();
        }
    }

    /*Returns true if the current display is grid.*/
    private boolean isDisplayGrid() {
        return win.getView() instanceof EventGridView;
    }

    /* drill down on charts,  or changing from chart to grid from view menu, or changing from 
     * grid to new grid from window view menu 
     */
    private void changeToGrid(final EventType eventID, final ToolBarURLChangeDataType urlInfo,
            final String elementClickedForTitleBar) {
        /* force new call on the toggle by nullifying existing response */

        win.setResponseObj(null);
        win.clearCurrentBreadCrumbMenuItem();
        /* Update displayed grid's breadcrumb url to ensure correct URL is picked when user clicked 
           View menu option instead of Navigator button after a drill */
        final BreadCrumbMenuItem breadCrumb = win.getCurrentBreadCrumbMenuItem();
        if (breadCrumb != null) {

            breadCrumb.setURL(urlInfo.url);
        }

        win.setWsURL(urlInfo.url);
        win.setWindowType(MetaMenuItemDataType.Type.GRID); // so returns grid

        urlInfo.addOutBoundParameter(DISPLAY_TYPE_PARAM, OUT_BOUND_GRID_DISPLAY_PARAM);

        /* (will be a change if grid being launched direct from view menu instead of from chart element click) */

        final String eventIdStr = (eventID == null ? EMPTY_STRING : eventID.toString());

        this.win.setFixedQueryId(urlInfo.drillDownWindowType != null ? urlInfo.drillDownWindowType : eventIdStr);

        if (win.getView() instanceof ChartWindowView) {
            final ChartWindowView chartDisplay = (ChartWindowView) win.getView();

            /* drill down from a chart element - to a grid with new server call 
             * (different response than toggle as nullified the response object above
             * but reusing toggle functionality - reset a new URL on the toggle launcher*/
            chartDisplay.changeToggleWindowLauncher(eventBus, urlInfo);

            /* pass the element clicked through to the new window (grid) title bar */
            win.handleGraphToGridToggle(urlInfo.toolbarType, elementClickedForTitleBar);

            win.setCurrentToolBarType(urlInfo.toolbarType);
            win.initializeToolbar(true);

        } else {
            /* displaying new grid from window view menu when your a grid already 
             * (e.g. Terminal Analysis)*/

            /* display not in BaseWindow #getResusableURLParams at this time */

            refreshWinSameDisplayTypeNewURL(eventID, urlInfo);
            win.setCurrentToolBarType(urlInfo.toolbarType);
            win.initializeToolbar(true);
        }

    }

    /*
     * Change a line chart to another line chart, or 
     * horizontal bar to another horizontal bar
     */
    private void refreshWinSameDisplayTypeNewURL(final EventType eventID, final ToolBarURLChangeDataType urlInfo) {

        // reset for new metadata (leave same windowId)
        final String eventIDStr = eventID.toString();

        win.setWsURL(urlInfo.url);
        win.setFixedQueryId(eventIDStr);
        win.setMaxRowsParam(urlInfo.maxRowsParam);

        // but don't want to loose search field params
        if (win.getView() instanceof IExtendedWidgetDisplay) {
            ((IExtendedWidgetDisplay) win.getView()).updateWidgetSpecificURLParams(urlInfo.getWidgetSpecificParams());
            win.initializeWidgit(eventIDStr); // assume meta data rigged up for this

            win.handleWindowRefresh();
        }

    }

    private boolean isChart(final String windowType) {
        return isWinType(MetaMenuItemDataType.Type.CHART, windowType);
    }

    private boolean isGrid(final String windowType) {
        return isWinType(MetaMenuItemDataType.Type.GRID, windowType)
                || isWinType(MetaMenuItemDataType.Type.RANKING, windowType);
    }

    private boolean isWinType(final MetaMenuItemDataType.Type compareType, final String windowTypeStr) {
        final MetaMenuItemDataType.Type winType = MetaMenuItemDataType.convertType(windowTypeStr);
        return winType == compareType;
    }

    /**
     * Before we toggle data from grid back into a graph (drill from graph to grid scenario 
     * where grid is grid from "old URL" graph drilldown and graph is required graph to plot in same window
     * with new URL (e.g. new view menu item chosen in say terminal groups) 
     * (i.e. so did work with server when were a grid and now want
     * to reuse toggle code to turn that data into a graph) - but need to wait until result back 
     * from server or will end up attempting to draw a graph with the drilldown grids response object
     * 
     * (so wait or user sees incorrectly plotted graph (using drilled grid data), followed by corrected one)
     */
    private final class ServerSuccessResponseHandler implements SucessResponseEventHandler {

        @Override
        public void handleResponse(final MultipleInstanceWinId multiWinID, final String requestData,
                final Response response) {

            // guards 
            if (!ChartGridChangeEventHandler.this.getMultipleInstanceWinId().isThisWindowGuardCheck(multiWinID)) {
                return;
            }

            if (wasFormallyAGrid) { // now becoming a chart
                win.handleGraphToGridToggle(null, EMPTY_STRING);

            }

        }
    }

    /* extracted for junit */
    void registerHandlerForSuccessServerResult() {
        win.registerHandler(eventBus.addHandler(SucessResponseEvent.TYPE, waitingForServerHandler));
    }

}
