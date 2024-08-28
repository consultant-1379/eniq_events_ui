/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.events.handlers;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;
import static com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.common.widget.*;
import com.ericsson.eniq.events.ui.client.common.widget.dialog.PropertiesDialog;
import com.ericsson.eniq.events.ui.client.datatype.*;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.ericsson.eniq.events.ui.client.events.*;
import com.ericsson.eniq.events.ui.client.main.*;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.web.bindery.event.shared.EventBus;

/**
 * class to handle generic events raised by user events on the window toolBar buttons and (toolbar) menu items
 */
public class WindowToolBarItemEvent implements WindowToolbarEventHandler { // NOPMD by eeicmsy on 27/04/10 07:11

    private final static Logger LOGGER = Logger.getLogger(WindowToolBarItemEvent.class.getName());

    private final IMetaReader metaReader = MainEntryPoint.getInjector().getMetaReader();

    private EventType eventID = null;

    private IExtendedWidgetDisplay viewRef = null;

    private EventBus eventBus = null;

    private Component menuItem = null;

    private ToolBarURLChangeDataType urlInfo = null;

    private final Map<String, RecurErrorWindowHelper> recurErrWindowHelpers = new HashMap<String, RecurErrorWindowHelper>();

    private ExportToCSVHelper exportToCSVHelper;

    @Override
    public void handleToolBarEvent(final IExtendedWidgetDisplay vRef, final EventType eID, final EventBus eBus, final Component mItem,
                                   final ToolBarURLChangeDataType urlInfo) {
        this.viewRef = vRef;
        this.eventID = eID;
        this.eventBus = eBus;
        this.menuItem = mItem;
        this.urlInfo = urlInfo;

        handleToolbarEvent();
    }

    private void handleToolbarEvent() { // NOPMD by eeicmsy on 27/04/10 07:11

        switch (eventID) {
            case BACK:
                getDrillDownNavigator().handleBack();
                break;
            case FORWARD:
                getDrillDownNavigator().handleForward();
                break;
            case NAVIGATION:
                getDrillDownNavigator().handleBreadcrumb(menuItem);
                break;
            case EXPORT:
                getExportToCSVHelper().exportDataToCSV(viewRef);
                break;
            case TIME:
                displayTimeDialog();
                break;
            case PROPERTIES:
                displayPropertiesWindow();
                break;
            case KPI:
                displayKPIWindow();
                break;
            case KPI_CS:
                displayKPICSWindow();
                break;
            case SHOW_HIDE_CHART_LEGEND:
                fireShowOrHideLegend();
                break;
            case REFRESH:
                fireRefresh();
                break;
            case TOGGLE_TO_GRID:
                fireToggleToGrid();
                break;
            case SUBSCRIBER_DETAILS_TABLE:
                displayGridDialog(SD_MENU_ITEM);
                break;
            case SUBSCRIBER_DETAILS_TABLE_PTMSI:
                displayGridDialog(SD_MENU_ITEM_PTMSI);
                break;
            case SUBSCRIBER_DETAILS_TABLE_CS:
                displayGridDialog(CS_SD_MENU_ITEM);
                break;
            case SUBSCRIBER_DETAILS_TABLE_MSISDN_CS:
                displayGridDialog(CS_SD_MENU_ITEM_MSISDN);
                break;
            case SUBSCRIBER_DETAILS_TABLE_WCDMA_CFA:
                displayGridDialog(WCDMA_CFA_SD_MENU_ITEM);
                break;
            case SAC: // same for CS and PS at moment it seems
                displayGridDialog(SAC_MENU_ITEM);
                break;
            case RECUR_ERR_SUBSCRIBER:
            case RECUR_ERR_NETWORK:
                displayRecurringFailedEventsWindow();
                break;
            case CAUSE_CODE_TABLE_CC:
            case CAUSE_CODE_TABLE_SCC:
            case CS_CAUSE_CODE_TABLE_CC:
            case CS_CAUSE_CODE_TABLE_SCC:
            case CAUSE_CODE_TABLE_CC_WCDMA:
            case CAUSE_CODE_TABLE_SCC_WCDMA:
            case DISCONNECTION_CODE_TABLE_DC_WCDMA:
            case CAUSE_CODE_TABLE_CC_WCDMA_HFA:
            case CAUSE_CODE_TABLE_SCC_WCDMA_HFA:
            case LTE_CALL_FAILURE_CAUSE_CODE_TABLE_CC:
            case LTE_HANDOVER_FAILURE_CAUSE_CODE_TABLE_CC:
            case GSM_CAUSE_CODE_TABLE_CC:
            case GSM_CAUSE_CODE_TABLE_SCC:
                displayCauseCodeTablesWindow();
                break;
            /* WCDMA CFA Subscriber Overview drilled chart back to Summary Chart */
            case SUB_BI_FAILED_EVENTS_CFA:
                /*
                 * subscriber overview (business intel) charts toggled from view menu (terminals is a grid). Extra than other charts because want to
                 * change time component
                 */
            case SUB_BI_FAILED_EVENTS:
            case SUB_BI_APN_USAGE:
            case SUB_BI_CELL:
            case SUB_BI_ROUTING_AREA:
            case SUB_BI_BUSY_DAY:
            case SUB_BI_BUSY_HOUR:
            case SUB_BI_TERMINALS:
            case SUB_BI_TAU:
            case SUB_BI_HANDOVER:
            case CS_SUB_BI_FAILED_EVENTS:
            case CS_SUB_BI_BUSY_HOUR:
            case CS_SUB_BI_BUSY_DAY:
            case CS_SUB_BI_BUSY_CELL:
            case CS_SUB_BI_BUSY_TERMINALS:
                fireChangeChartGridDisplayedForSubBI();
                break;
            /* roaming charts toggled from view menu */
            case ROAMING_BY_OPERATOR:
            case ROAMING_BY_COUNTRY:
            case TERMINAL_GA_MOST_POPULAR:
            case TERMINAL_GA_MOST_POPULAR_SUMMARY:
            case TERMINAL_GA_MOST_ATTACHED_FAILURES:
            case TERMINAL_GA_MOST_PDP_SESSION_SETUP_FAILURES:
            case TERMINAL_GA_PDP_SESSION_STATS:
            case TERMINAL_GA_MOST_MOBILITY_ISSUES:
            case TERMINAL_GA_MOST_SWAPPED_TO:
            case TERMINAL_GA_MOST_SWAPPED_FROM:
            case TERMINAL_GA_HIGHEST_DATAVOLUME_SUMMARY:
            case CS_TERMINAL_GA_MOST_POPULAR:
            case CS_TERMINAL_GA_MOST_ATTACHED_FAILURES:
            case CS_TERMINAL_GA_MOST_PDP_SESSION_SETUP_FAILURES:
            case CS_TERMINAL_GA_MOST_MOBILITY_ISSUES:
                /* Terminal Analysis (all grids) from view menus */
            case TERMINAL_MOST_POPULAR:
            case TERMINAL_MOST_POPULAR_SUMMARY:
            case TERMINAL_MOST_ATTACHED_FAILURES:
            case TERMINAL_MOST_PDP_SESSION_SETUP_FAILURES:
            case TERMINAL_PDP_SESSION_STATS:
            case TERMINAL_MOST_MOBILITY_ISSUES:
            case TERMINAL_MOST_SWAPPED_TO:
            case TERMINAL_MOST_SWAPPED_FROM:
            case TERMINAL_HIGHEST_DATAVOLUME_VIEW:
            case TERMINAL_ANALYSIS_RAN_WCDMA_CALLFAILURE_MOST_SETUP_FAILURES:
            case TERMINAL_ANALYSIS_RAN_WCDMA_CALLFAILURE_MOST_DROPS:
            case TERMINAL_ANALYSIS_RAN_WCDMA_HFA_SOHO:
            case TERMINAL_ANALYSIS_RAN_WCDMA_HFA_HSDSCH:
            case TERMINAL_ANALYSIS_RAN_WCDMA_HFA_IFHO:
            case TERMINAL_ANALYSIS_RAN_WCDMA_HFA_IRAT:
            case CS_TERMINAL_MOST_POPULAR:
            case CS_TERMINAL_MOST_ATTACHED_FAILURES:
            case CS_TERMINAL_MOST_PDP_SESSION_SETUP_FAILURES:
            case CS_TERMINAL_MOST_MOBILITY_ISSUES:
                /* Terminal Group Analysis Charts from View menus */
            case TERMINAL_GROUP_ANALYSIS_RAN_WCDMA_CFA_CALL_DROPS:
            case TERMINAL_GROUP_ANALYSIS_RAN_WCDMA_CFA_CALL_SETUP_FAILURES:
            case TERMINAL_GROUP_ANALYSIS_RAN_WCDMA_HFA_SOHO:
            case TERMINAL_GROUP_ANALYSIS_RAN_WCDMA_HFA_HSDSCH:
            case TERMINAL_GROUP_ANALYSIS_RAN_WCDMA_HFA_IFHO:
            case TERMINAL_GROUP_ANALYSIS_RAN_WCDMA_HFA_IRAT:
                fireChangeChartGridDisplayed();
                break;
        }
    }

    /*
     * need to create new instance of DrillDownNavPresenter cannot reuse as window can be closed and reopened
     */
    private NavigationHelper getDrillDownNavigator() {

        return new NavigationHelper(viewRef, eventBus);

    }

    /*
     * Lazy instantiate ExportToCSVHelper
     */
    private ExportToCSVHelper getExportToCSVHelper() {
        if (exportToCSVHelper == null) {
            exportToCSVHelper = new ExportToCSVHelper();
        }
        return exportToCSVHelper;
    }

    /*
     * presents the Time Dialog window to enable end users to provide the time parameters to pass to the server call
     */
    private void displayTimeDialog() {

        if (!isSearchParameterInWindowValid()) {
            MessageDialog.get().show(MISSING_INPUT_DATA, NEED_SEARCH_FIELD_MESSAGE, MessageDialog.DialogType.WARNING);
            return;
        }

        new TimeParameterDialogPresenter<IExtendedWidgetDisplay>(viewRef, eventBus);
    }

    /*
     * presents a Dialog window with grid
     */
    private void displayGridDialog(final String menuItemId) {
        final MetaMenuItemDataType winData = createMetaMenuItemDataType(menuItemId);
        final VerticalGridColumnHeaders keyValues = createVerticalGridColumnHeaders(menuItemId);
        final TwoColumnGridDialogPresenter<IExtendedWidgetDisplay> gridDialogPresenter = createGridDialogPresenter(winData, keyValues);

        // record can remain as null - e.g. subscriber details
        ModelData record = null;
        if (viewRef instanceof IEventGridView) {
            record = ((IEventGridView) viewRef).getGridRecordSelected();
        }
        gridDialogPresenter.requestGridData(record);
    }

    /**
     * extracted out to help get class under unit test
     * 
     * @param winData
     * @param keyValues
     * 
     * @return
     */
    TwoColumnGridDialogPresenter<IExtendedWidgetDisplay> createGridDialogPresenter(final MetaMenuItemDataType winData,
                                                                                   final VerticalGridColumnHeaders keyValues) {
        return new TwoColumnGridDialogPresenter<IExtendedWidgetDisplay>(viewRef, eventBus, winData, keyValues);
    }

    /*
     * display recurring failed events picking up parameters from the selected row
     */
    private void displayRecurringFailedEventsWindow() {
        final ModelData record = ((IEventGridView) viewRef).getGridRecordSelected();

        if (record == null) {
            // ideally button is disabled if no row selected but in case not
            displayRowSelectError("Recurring Failed Events");
        } else {
            getRecurErrorHelper().displayRecurringFailedEventsWindow(record, ((IEventGridView) viewRef));

        }
    }

    /*
     * RecurrError helper One helper per eventID and tab id to avoid any conflicts when using window across tabs, i.e. when want to launch recur error
     * from a grid in ranking tab, you don't want the same window and menu items in the subscriber tab getting updated
     * 
     * @return helper for recur error window handling
     */
    private RecurErrorWindowHelper getRecurErrorHelper() {

        final IWorkspaceController workspaceController = viewRef.getWorkspaceController();
        final String tabOwnerId = workspaceController.getTabOwnerId();

        final String key = tabOwnerId + eventID;
        if (!recurErrWindowHelpers.containsKey(key)) {
            recurErrWindowHelpers.put(key, new RecurErrorWindowHelper(((IEventGridView) viewRef), eventID, eventBus));
        }
        return recurErrWindowHelpers.get(key);

    }

    /*
     * display property window (for rows on grid only) - see cast
     */
    private void displayPropertiesWindow() {

        final ModelData record = ((IEventGridView) viewRef).getGridRecordSelected();
        if (record == null) {
            // surely button is disabled if no row selected but in case not
            displayRowSelectError("Properties");
        } else {
            new PropertiesDialog((IEventGridView) viewRef);
        }
    }

    /*
     * valids that the window that raised the toolbar event has the valid search paramters provided NOTE menu task bar search field does not have to
     * be same as searchData owned by window (we are checking against search data owned by the window)
     */
    private boolean isSearchParameterInWindowValid() {
        if (viewRef.getViewSettings().isSearchFieldUser()) {
            final SearchFieldDataType winParameters = viewRef.getPresenter().getSearchData();

            /* check that user has required params provided */
            if (winParameters == null || winParameters.searchFieldVal.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /*
     * Different from other multi-instance windows, as launched from buttom from an existing window. KPI button press code. Display KPI window for
     * node. (Button is disabled after click)
     */
    private void displayKPIWindow() {

        final MetaMenuItemDataType kpiWinData = createKPIMetaMenuItemDataType();
        createKPIWindow(kpiWinData);

    }

    private void displayKPICSWindow() {
        final MetaMenuItemDataType kpiWinData = createKPICSMetaMenuItemDataType();
        createKPIWindow(kpiWinData);
    }

    private void createKPIWindow(MetaMenuItemDataType kpiWinData) {
        /*
         * search data can NOT be #menuTaskBar.getSearchComponentValue(), i.e. we must support KPI button working from drilldowned to windows and new
         * windows launched from hyperlinks (whose windows have a completely different search data input than the search components value)
         */

        // presenter has most current search data
        final IBaseWindowPresenter presenter = viewRef.getPresenter();
        final SearchFieldDataType searchFieldData = presenter.getSearchData();

        /* special case for KPI not to pass "key=SUM" */
        searchFieldData.clean();

        final ContentPanel constrainArea = viewRef.getParentWindow().getConstraintArea();

        /* set up the presenter with a run time id */

        /*
         * allowing to read a fixed id from meta data so we can get chart settings whilst still being able to get window uniqueness for server
         * callbacks (the fixed id will be what is used to identify meta data (graph configs))
         */
        final String fixedId = kpiWinData.id;
        /**
         * Need to change win id of metadata because AbstractWindowLauncher will reread MetaData and will get the wrong metadata with the current id.
         * Using instance type here for the id, it doesnt need to be unique. This will also be used as an index to find the category in which to dock
         * the window
         */
        kpiWinData.setWinId(kpiWinData.id);
        final MetaMenuItem generatedMetaMenuItem = new MetaMenuItem(kpiWinData);
        final AbstractWindowLauncher launcher = createChartLauncher(generatedMetaMenuItem, viewRef.getWorkspaceController(), constrainArea);
        launcher.setFixedQueryId(fixedId);
        // search data - not always menu task bar search component value
        launcher.launchWindowWithPresetSearchData(searchFieldData, new TimeInfoDataType(viewRef.getPresenter().getWindowTimeDate()), false);
    }

    /*
     * Display Cause Code tables showing all cause codes. Subcause Code tables showing all subcause codes.
     * 
     * Ensure no duplcates - if a table is up on display already, being it to front
     */
    @SuppressWarnings("incomplete-switch")
    private void displayCauseCodeTablesWindow() {

        final IWorkspaceController workspaceController = viewRef.getWorkspaceController();
        final ContentPanel constrainArea = viewRef.getParentWindow().getConstraintArea();
        MetaMenuItem generatedMetaMenuItem = null;

        switch (eventID) {
            case CAUSE_CODE_TABLE_CC:
                generatedMetaMenuItem = createMetaMenuItem(CC_MENU_ITEM);
                break;
            case CAUSE_CODE_TABLE_SCC:
                generatedMetaMenuItem = createMetaMenuItem(SCC_MENU_ITEM);
                break;
            case CS_CAUSE_CODE_TABLE_CC:
                generatedMetaMenuItem = createMetaMenuItem(CS_CC_MENU_ITEM);
                break;
            case CS_CAUSE_CODE_TABLE_SCC:
                generatedMetaMenuItem = createMetaMenuItem(CS_SCC_MENU_ITEM);
                break;
            case CAUSE_CODE_TABLE_CC_WCDMA:
                generatedMetaMenuItem = createMetaMenuItem(CCWCDMAMenuItem);
                break;
            case CAUSE_CODE_TABLE_SCC_WCDMA:
                generatedMetaMenuItem = createMetaMenuItem(SCCWCDMAMenuItem);
                break;
            case DISCONNECTION_CODE_TABLE_DC_WCDMA:
                generatedMetaMenuItem = createMetaMenuItem(DCWCDMAMenuItem);
                break;
            case CAUSE_CODE_TABLE_CC_WCDMA_HFA:
                generatedMetaMenuItem = createMetaMenuItem(CCWCDMAHFAMenuItem);
                break;
            case CAUSE_CODE_TABLE_SCC_WCDMA_HFA:
                generatedMetaMenuItem = createMetaMenuItem(SCCWCDMAHFAMenuItem);
                break;
            case LTE_CALL_FAILURE_CAUSE_CODE_TABLE_CC:
                generatedMetaMenuItem = createMetaMenuItem(CCLTECFAMenuItem);
                break;
            case LTE_HANDOVER_FAILURE_CAUSE_CODE_TABLE_CC:
                generatedMetaMenuItem = createMetaMenuItem(CCLTEHFAMenuItem);
                break;
            case GSM_CAUSE_CODE_TABLE_CC:
                generatedMetaMenuItem = createMetaMenuItem(CCGSMMenuItem);
                break;
            case GSM_CAUSE_CODE_TABLE_SCC:
                generatedMetaMenuItem = createMetaMenuItem(SCCGSMMenuItem);
                break;
        }
        if (generatedMetaMenuItem != null) {

            final BaseWindow win = workspaceController.getWindow(generatedMetaMenuItem.getID());
            if (win == null) {

                final AbstractWindowLauncher launcher = createGridLauncher(generatedMetaMenuItem, workspaceController, constrainArea);

                // TODO: consider to pass WindowsState as the last parameter (it is especially required if it was
                // drilled down from another window)
                launcher.launchWindow(new TimeInfoDataType(viewRef.getPresenter().getWindowTimeDate()), false, EMPTY_STRING); // Specific to cause code table don't display/time/combo

            } else {
                /* window exists already (could do more here if window minimsed to button in menutask bar perhaps) */
                win.toFront();
            }
        } else {
            LOGGER.warning("Internal error method caled incorrrectly");
        }

    }

    /*
     * Toggle visibility of legend on a chart
     * 
     * @see com.ericsson.eniq.events.ui.client.charts.ChartPresenter
     */
    private void fireShowOrHideLegend() {
        final MultipleInstanceWinId currentId = viewRef.getPresenter().getMultipleInstanceWinId();
        eventBus.fireEvent(new HideShowChartElementEvent(currentId));
    }

    /*
     * When refresh button press when in upper toolbar (i.e. charts), as apposed to refresh in grid paging toolbar
     * 
     * @see com.ericsson.eniq.events.ui.client.common.comp.BaseWindowPresenter
     */
    private void fireRefresh() {
        // search field data owned by grid  can be changing so not caching
        final MultipleInstanceWinId currentId = viewRef.getPresenter().getMultipleInstanceWinId();
        eventBus.fireEvent(new RefreshWindowEvent(currentId));
    }

    /*
     * change the display of data from a Graph to a Grid
     * 
     * @see com.ericsson.eniq.events.ui.client.common.comp.BaseWindowPresenter
     */
    private void fireToggleToGrid() {
        final MultipleInstanceWinId currentId = viewRef.getPresenter().getMultipleInstanceWinId();
        eventBus.fireEvent(new GraphToGridEvent(currentId));
    }

    /*
     * Change to a new chart from view menu on window (e.g. if eventId indicates to change, could be changing from "roam by operator" to
     * "roam by country", or a graph to a new server call grid etc)
     * 
     * NOTE: it is vital that the eventID name for the new window matches the winId (queryId) in meta data.
     * 
     * @see com.ericsson.eniq.events.ui.client.charts.ChartPresenter
     * 
     * @see com.ericsson.eniq.events.ui.client.common.widget.EventGridPresenter
     * 
     * @see com.ericsson.eniq.events.ui.client.events.handlers.ChartGridChangeEventHandler
     */
    private void fireChangeChartGridDisplayed() {

        final MultipleInstanceWinId currentId = viewRef.getPresenter().getMultipleInstanceWinId();
        eventBus.fireEvent(new ChangeChartGridEvent(currentId, eventID, urlInfo, EMPTY_STRING));
    }

    /*
     * Extra for event ids that require a change to time component parameter prior to displaying new window. Also terminal grid is a special case
     */
    private void fireChangeChartGridDisplayedForSubBI() {

        /*
         * i.e. of course this means if change the time yourself on this eventIDs it going to revert back to these defaults when toggle back but what
         * can you do (maybe not revert if current time greater than default?)
         */
        switch (eventID) {

            case SUB_BI_BUSY_DAY:
            case CS_SUB_BI_BUSY_DAY:
                urlInfo.setTempTimeInfoDataType(DEFAULT_SUB_BI_BUSY_DAY_TIME_DATA);

                break;
            case CS_SUB_BI_BUSY_HOUR:
                urlInfo.setTempTimeInfoDataType(DEFAULT_SUB_BI_BUSY_HOUR_TIME_DATA);

                break;
            default: {
                urlInfo.setTempTimeInfoDataType(null);

            }
        }
        fireChangeChartGridDisplayed();
    }

    /*
     * Display specific warning dialog for row selection
     * 
     * @param contextText text to go into method messages
     */
    private void displayRowSelectError(final String contextText) {
        final MessageDialog d = new MessageDialog();

        final String sMessage = "Please select the row you wish to view in the " + contextText + " Window, before selecting the " + contextText
                + " Button";
        final String sTitle = viewRef.getViewSettings().getText() + DASH + contextText;

        d.show(sTitle, sMessage, MessageDialog.DialogType.WARNING);
    }

    //////////////////   junit

    /* create a new instance of a gridLauncher class */
    private AbstractWindowLauncher createGridLauncher(final MetaMenuItem item, final IWorkspaceController workspaceController,
                                                      final ContentPanel containingPanel) {
        return new GridLauncher(item, eventBus, containingPanel, workspaceController);
    }

    /* create a new instance of a gridLauncher class */
    private AbstractWindowLauncher createChartLauncher(final MetaMenuItem item, final IWorkspaceController workspaceController,
                                                       final ContentPanel containingPanel) {
        return new ChartLauncher(item, eventBus, containingPanel, workspaceController);
    }

    TimeInfoDataType getViewRefTimeData() {
        return viewRef.getTimeData();
    }

    /* extracted for junit */
    MetaMenuItemDataType createKPIMetaMenuItemDataType() {
        return metaReader.getKPIMetaMenuItemDataType();
    }

    MetaMenuItemDataType createKPICSMetaMenuItemDataType() {
        return metaReader.getKPICSMetaMenuItemDataType();
    }

    MetaMenuItem createMetaMenuItem(final String menuItemId) {
        return metaReader.getMetaMenuItemFromID(menuItemId);
    }

    MetaMenuItemDataType createMetaMenuItemDataType(final String menuItemId) {
        return metaReader.getMetaMenuItemDataType(menuItemId);
    }

    VerticalGridColumnHeaders createVerticalGridColumnHeaders(final String menuItemId) {
        return metaReader.getVerticalGridColumnHeaders(menuItemId);
    }

}
