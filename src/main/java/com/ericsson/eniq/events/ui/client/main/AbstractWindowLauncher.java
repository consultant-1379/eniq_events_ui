/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.main;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.ToolBarStateManager.BottomToolbarType;
import com.ericsson.eniq.events.ui.client.common.comp.*;
import com.ericsson.eniq.events.ui.client.common.widget.AbstractBaseWindowDisplay;
import com.ericsson.eniq.events.ui.client.common.widget.EniqWindow;
import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.PresetResponseDisplayDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay;
import com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceUtils;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;

import java.util.List;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;

/**
 * Generic window launching for chart and grid windows. This can also hold and
 * set preset window information when available (it will be available when
 * toggling from a graph to a grid as server data will already have been
 * fetched)
 * 
 * @author eeicmsy
 * @since May 2010
 * 
 * @see com.ericsson.eniq.events.ui.client.main.ChartLauncher
 * @see com.ericsson.eniq.events.ui.client.main.GridLauncher
 * 
 */
public abstract class AbstractWindowLauncher extends SelectionListener<MenuEvent> { // NOPMD by eeicmsy on 18/07/11 11:03

    private static final int MESSAGE_DIALOG_MIN_WIDTH = 225;

    private final static String TOOLBAR_DVTP_GRID = "TOOLBAR_DVTP_GRID";

    protected final IWorkspaceController workspaceController;

    public MetaMenuItem item;

    protected final ContentPanel containingPanel;

    /* can be null */
    private String fixedQueryId = null;

    /*
     * Preset info including a response object. Set in advance when already have
     * information (desirable to have this for toggling between grid and
     * graphs))
     */
    private PresetResponseDisplayDataType presetInfo;

    private boolean isLaunchingFromHyperLink;

    private final IMetaReader metaReader = MainEntryPoint.getInjector().getMetaReader();

    /** ecarsea - for finding search data specific to this window. Should not need to do this in future. Window should be created here with its associated search data
     * and we should not ever need to call the workspaceController to get it again, but im putting it in in case of breaking something down in the depths.
     * This field will only be used by workspaces to get search data for this window **/
    protected String windowId = "";

    private final MultipleInstanceWinId multiWinId;

    /**
     * Construct generic selection listener for main menu menuitem responsible
     * for launching charts ro grids
     * 
     * @param item
     *            Menu item selected with details for server calls required to
     *            populate grid
     * @param containingPanel
     *            Center panel where launched window will be constrained to.
     * @param workspaceController Controller with container panel which will "own" the launched window i.e. Menu Task Bar
     */
    public AbstractWindowLauncher(final MetaMenuItem item, final ContentPanel containingPanel,
            final IWorkspaceController workspaceController) {
        this(item, containingPanel, workspaceController, "");

    }

    /**
     * Construct generic selection listener for main menu menuitem responsible
     * for launching charts ro grids
     * 
     * @param item
     *            Menu item selected with details for server calls required to
     *            populate grid
     * @param containingPanel
     *            Center panel where launched window will be constrained to.
     * @param workspaceController Controller with container panel which will "own" the launched window i.e. Menu Task Bar
     */
    public AbstractWindowLauncher(final MetaMenuItem item, final ContentPanel containingPanel,
            final IWorkspaceController workspaceController, String windowId) {

        this.item = item;
        this.containingPanel = containingPanel;
        this.workspaceController = workspaceController;
        this.windowId = windowId;
        multiWinId = createMultipleInstanceWinId();
    }

    /**
     * Utility for use ONLY when using this class to launch grids from charts
     * and vice versa, i.e. use the previous response object from when you were
     * a grid to launch the chart and vice versa. This class will toggle upper
     * and lower toolbars to suit new window type prior to re-launch.
     * 
     * @param presetInfo
     *            Server Response object for a successful call
     */
    public void handleGraphToGridToggleInfo(final PresetResponseDisplayDataType presetInfo,
            final boolean shouldResetMeta, final String toolbarType) {

        setPresetResponseDisplayData(presetInfo);

        /*
         * do this before any new view creation (as view sets bottom toolbar).
         * Re-using same meta menu item in case of toggle (to preserve some kind
         * of state)
         */
        if (shouldResetMeta) {
            item.reset();
        }
        item.toggleToolBarType();

        /** ecarsea - 25-10-2011 Window type is set to grid in chart window view as well as toggled here so ends up returning the window type
         * to chart when toggling to a grid. **/
        item.toggleWindowType();
        /** ecarsea - 25-10-2011 All charts are set to Plain Bottom ToolBar within their view, so just toggle when grid and plain. Cant check if window type
         * is grid unfortunately as it is toggled twice for this situation **/
        if (item.getCurrentBottomToolBarType().equals(BottomToolbarType.PLAIN)) {
            item.toggleBottomToolBarType();
        }

        if (toolbarType != null && !toolbarType.isEmpty()) {
            item.setCurrentToolBarType(toolbarType);
        }
    }

    /**
     * Utility for setting for a new window's location and response object using
     * old window's data
     * 
     * @param presetInfo
     *            Server Response object for a successful call
     */
    public void setPresetResponseDisplayData(final PresetResponseDisplayDataType presetInfo) {
        this.presetInfo = presetInfo;
    }

    /**
     * Utility to pass fixed query Id into new window creation
     * 
     * @param fixedQueryId
     *            - If null is passed (or this method is not called) will use
     *            the queryId in BaseWindowPresenter. We have the queryId
     *            windows (same as winId) to identify grid or chart, but passing
     *            it in as a parameter here too because of multiple window
     *            instances scenario (a multiple instance chart being toggled to
     *            a table where the queryId has become a composite of queryId
     *            and search field data), i.e. to ensure fixed one is used when
     *            reading metadata for new grid
     * 
     * @see com.ericsson.eniq.events.ui.client.common.comp.BaseWindowPresenter
     */
    public void setFixedQueryId(final String fixedQueryId) {
        this.fixedQueryId = fixedQueryId;
    }

    /**
     * Utility to pass window url into toggling from graph to grid.
     * 
     * @param url
     */
    public void setWsURL(final String url) {
        item.setWsURL(url);
    }

    /**
     * Utility (hack) to reset upper toggle toolbar (e.g. for situation where
     * have chart toggles to grids and chart drilldowns active so there is more
     * than one potential toggle toolbar type being used, whilst keeping a
     * "state" with menu item
     */
    public void resetUpperToggleToolBar() {
        item.setTempToggleToolBarType(null);
    }

    /**
     * Create window display
     * 
     * @param multiWinId
     *            - multiple win support id - containing search field data (not
     *            neccessarly the search field value currently in the
     *            workspaceController (i.e. we may be using data from a previous window
     *            in a toggle scenario)
     * @param windowState state of window
     * @return view part (for MVP pattern) representing window display
     * 
     */
    public abstract AbstractBaseWindowDisplay createView(MultipleInstanceWinId multiWinId, final WindowState windowState);

    /**
     * when we arer launching a window with pre-response data (a toggle) we are
     * simulating it by closing an old window and replacing it which a new one.
     * This method is here (hack) to clean up any adverse affected that may have
     */
    public abstract void handleEnablingForReLaunch();

    /**
     * Create window presenter
     * 
     * @param view
     *            associated view with the presenter
     * @param winId, id of this window. associated with new workspace launch           
     * @return presenter part (for MVP pattern) representing window display
     */
    public abstract BaseWindowPresenter<? extends IExtendedWidgetDisplay> createPresenter(
            final AbstractBaseWindowDisplay view, final MultipleInstanceWinId winId);

    /**
     * Launch a window Using default time (with preset data if available)
     *
     * @see #launchWindow(com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType, boolean, String, com.ericsson.eniq.events.ui.client.common.comp.WindowState)
     */
    public void launchWindow(final boolean isToggling) {
        final String timeValueFromMetaData = item.getTimeValue();

        if (ONE_WEEK_MS_TIME_PARAMETER.equals(timeValueFromMetaData)) {
            launchWindow(TimeInfoDataType.DEFAULT_ONE_WEEK_TIME_DATA, isToggling);
        } else if (ONE_DAY_MS_TIME_PARAMETER.equals(timeValueFromMetaData)) {
            launchWindow(TimeInfoDataType.DEFAULT_ONE_DAY_TIME_DATA, isToggling);
        } else if (TWELVE_HOURS_MS_TIME_PARAMETER.equals(timeValueFromMetaData)) {
            launchWindow(TimeInfoDataType.DEFAULT_TWELVE_HOURS_TIME_DATA, isToggling);
        } else if (SIX_HOURS_MS_TIME_PARAMETER.equals(timeValueFromMetaData)) {
            launchWindow(TimeInfoDataType.DEFAULT_SIX_HOURS_TIME_DATA, isToggling);
        } else if (TWO_HOURS_MS_TIME_PARAMETER.equals(timeValueFromMetaData)) {
            launchWindow(TimeInfoDataType.DEFAULT_TWO_HOURS_TIME_DATA, isToggling);
        } else if (ONE_HOUR_MS_TIME_PARAMETER.equals(timeValueFromMetaData)) {
            launchWindow(TimeInfoDataType.DEFAULT_ONE_HOUR_TIME_DATA, isToggling);
        } else if (THIRTY_MINS_MS_TIME_PARAMETER.equals(timeValueFromMetaData)) {
            launchWindow(TimeInfoDataType.DEFAULT_THIRTY_MINS_TIME_DATA, isToggling);
        } else if (FIFTEEN_MINS_MS_TIME_PARAMETER.equals(timeValueFromMetaData)) {
            launchWindow(TimeInfoDataType.DEFAULT_FIFTEEN_MINS_TIME_DATA, isToggling);
        } else {
            launchWindow(TimeInfoDataType.DEFAULT, isToggling);
        }
    }

    /**
     * @see #launchWindow(com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType, boolean, String, com.ericsson.eniq.events.ui.client.common.comp.WindowState)
     */
    public void launchWindow(final TimeInfoDataType initialTime, final boolean isToggling) {
        // TODO: consider to pass WindowsState as the last parameter to launchWindow(...): it is especially required
        // if it was drilled down from another window
        this.launchWindow(initialTime, isToggling, EMPTY_STRING);
    }

    /**
     * @see #launchWindow(com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType, boolean, String, com.ericsson.eniq.events.ui.client.common.comp.WindowState)
     */
    public void launchWindow(final TimeInfoDataType initialTime, final boolean isToggling,
            final String elementClickedForTitleBar) {
        this.launchWindow(initialTime, isToggling, elementClickedForTitleBar, null);
    }

    /**
     * Called when launching a grid/chart from a chart. The clicked item on the chart behaves like a hyperlink/drilldown.
     * @param initialTime
     * @param isToggling
     * @param elementClickedForTitleBar
     * @param windowState
     */
    public void launchWindowFromChart(final TimeInfoDataType initialTime, final boolean isToggling,
                                      final String elementClickedForTitleBar, WindowState windowState){
        isLaunchingFromHyperLink = true;
        this.launchWindow(initialTime, isToggling, elementClickedForTitleBar, windowState);
    }

    /**
     * Called when launching a grid from a wizard.
     * @param initialTime
     * @param searchVal
     * @param isToggling
     * @param windowState
     */
    public void launchWindowFromWizard(final TimeInfoDataType initialTime, final SearchFieldDataType searchVal, final boolean isToggling, WindowState windowState){
        isLaunchingFromHyperLink = true;
        initLaunchWindow(searchVal, initialTime, isToggling, EMPTY_STRING, windowState);
    }


    /**

    /**
    * Launch window with a initial time
    *
    * @param initialTime
    *            if need to launch window with a specific time that
    * @param isToggling
    *            true to maintain state of toolbars, etc. (we are creating a
    *            new window - to convert say from chart to grid - but want to
    *            maintain states from old window)
    * @param elementClickedForTitleBar an element clicked for title bar
    * @param windowState state of window
    */
    public void launchWindow(final TimeInfoDataType initialTime, final boolean isToggling,
            final String elementClickedForTitleBar, WindowState windowState) {
        /* presetInfo will be null most of time (unless toggling) */
        //        final SearchFieldDataType searchVal = (presetInfo == null) ? workspaceController.getSearchComponentValue()
        //                : presetInfo.searchFieldValue;
        /*Checking whether menu item is search field dependent. Ranking windows dont require any search fields.*/
        SearchFieldDataType searchVal;

        //Need to re-read fresh metadata. This method is only called when a NEW window is launched,
        // so it's a good point to refresh "item" with original metadata.
        String widgetSpecificParams = item.getWidgetSpecificParams();
        String currentToolBarType = item.getCurrentToolBarType();
        String wsURL = item.getWsURL();
        MetaMenuItem itemTemp = getFreshMetaMenuItem(item);
        if (itemTemp != null)
            item = itemTemp;

        item.setWidgetSpecificParams(widgetSpecificParams);
        item.setCurrentToolBarType(currentToolBarType);
        item.setWsURL(wsURL);

        if (item.isSearchFieldUser()) {

            searchVal = (presetInfo == null) ? workspaceController.getSearchComponentValue(windowId)
                    : presetInfo.searchFieldValue;

        } else {

            final String[] urlParms = new String[] { (TYPE_PARAM + INPUT), "=" };

            searchVal = new SearchFieldDataType("", urlParms, INPUT, null, false, "", null, false);
        }

        initLaunchWindow(searchVal, initialTime, isToggling, elementClickedForTitleBar, windowState);
    }

    /**
     * Utility when call to launch a window (play button press) when already in
     * a toggled state we will want to update the search field at same time as
     * toggling back to expected start up state
     *
     * @see #launchWindow(com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType, boolean, String, com.ericsson.eniq.events.ui.client.common.comp.WindowState)
     */
    public void launchWindow(final TimeInfoDataType initialTime, final SearchFieldDataType searchVal,
            final boolean isToggling, WindowState windowState) {
        launchWindow(initialTime, searchVal, isToggling, EMPTY_STRING, windowState);
    }

    /**
     * @see #launchWindow(com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType, com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType, boolean, com.ericsson.eniq.events.ui.client.common.comp.WindowState)
     */
    public void launchWindow(final TimeInfoDataType initialTime, final SearchFieldDataType searchVal,
            final boolean isToggling) {
        launchWindow(initialTime, searchVal, isToggling, EMPTY_STRING);
    }

    /**
     * @see #launchWindow(com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType, boolean, String, com.ericsson.eniq.events.ui.client.common.comp.WindowState)
     */
    public void launchWindow(final TimeInfoDataType initialTime, final SearchFieldDataType searchVal,
            final boolean isToggling, final String elementClickedForTitleBar, WindowState windowState) {
        isLaunchingFromHyperLink = false; // should not be necessary (but if
        // some one cached the launcher)
        initLaunchWindow(searchVal, initialTime, isToggling, elementClickedForTitleBar, windowState);
    }

    /**
     * @see #launchWindow(com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType, boolean, String, com.ericsson.eniq.events.ui.client.common.comp.WindowState)
     */
    public void launchWindow(final TimeInfoDataType initialTime, final SearchFieldDataType searchVal,
            final boolean isToggling, final String elementClickedForTitleBar) {
        isLaunchingFromHyperLink = false; // should not be necessary (but if
        // some one cached the launcher)
        initLaunchWindow(searchVal, initialTime, isToggling, elementClickedForTitleBar, null);
    }

    /**
     * Traditional used for hyperlink.
     * 
     * Note menu task bar search value is not always the correct one to use,
     * i.e. may want to use windows owned searchData at time of creating new
     * window
     * 
     * Launch a window with the provided search data split out like this as
     * launching from a hyperlink has the potential to deviate from standard
     * window launches
     * 
     * NOW ALWAY HAVE TO USE THIS METHOD WHEN LAUNCH FROM HPERLINK
     * 
     * @param searchVal
     *            search data to pass to new window (not neccessary the cureent
     *            search data on the menu taskbar)
     * @param initialTime
     *            time data to pass to new window
     * @param toggle
     *            flag to determine if the meta needs to be read fresh from the
     *            meta source
     */
    public void launchWindowWithPresetSearchData(final SearchFieldDataType searchVal,
            final TimeInfoDataType initialTime, final boolean toggle) {

        isLaunchingFromHyperLink = true; // NOW ALWAY HAVE TO USE THIS METHOD
        // WHEN LAUNCH FROM HPERLINK

        initLaunchWindow(searchVal, initialTime, toggle, EMPTY_STRING, null);

    }

    /*
    * Attempt to produce MetaMenuItem which is unaffected by previous opening of the window. Can go right back to
    * cached UIMetaData data for Menu TaskBar MenuItems otherwise using a new constructor (unless toggling - which is
    * not as safe)
    */
    private MetaMenuItem getFreshMetaMenuItem(final MetaMenuItem meta) {
        return metaReader.getMetaMenuItemFromID(meta.getID());
    }

    public void launchWindowWithPresetSearchData(final SearchFieldDataType searchVal,
            final TimeInfoDataType initialTime, final boolean toggle, final String elementClickedForTitleBar) {
        isLaunchingFromHyperLink = true;
        initLaunchWindow(searchVal, initialTime, toggle, elementClickedForTitleBar, null);
    }

    /*
     * launch a window with the provided search field data
     */
    private void initLaunchWindow(final SearchFieldDataType searchVal, TimeInfoDataType timeSelectionData, // NOPMD by eeicmsy on 03/08/11 17:11
            final boolean isToggling, final String elementClickedForTitleBar, WindowState windowState) {

        final AbstractBaseWindowDisplay view = getViewIfSuitableToOpenWindow(searchVal, windowState);
        if (view != null && view.addLaunchButton()) {

            final BaseWindowPresenter<? extends WidgetDisplay> winPresenter = createPresenter(view, multiWinId);
            /* extra here will have many uses to spare the bus) */
            view.setPresenter(winPresenter);

            containingPanel.add(view.asWidget());

            /*
             * (Response object will remain null mostly - unless in a toggle
             * scenario)
             */
            Response response = null;

            /* will be most of time */
            if (presetInfo != null) {
                final boolean isConvertingToChart = winPresenter.isChart();

                // may need to nullify the response if it is not suitable chart
                // data (i.e. with a view to fetching it again)
                final String responseText = (presetInfo.responseObj == null) ? null : presetInfo.responseObj.getText();
                if (!isConvertingToChart || JSONUtils.isChartData(responseText)) {
                    // really want this converting from toggle button
                    response = presetInfo.responseObj;
                }
                if(item.getCurrentToolBarType().equals(TOOLBAR_DVTP_GRID))
                {
                    response=null;
                }
                handleEnablingForReLaunch();
                timeSelectionData = presetInfo.timeSelectionData;
            }

            /*
             * NB: layout needs to be called before the initWindow method
             * otherwise some Grelim type rendering issues will occur !!!
             */
            containingPanel.layout();

            /*
             * item id serves as a query id. Pass in either the current or
             * cached (for toggling grid-graph off previous search in a
             * multi-instance scenario) value in search field in case needs to
             * populate straight away
             */

            if(isLaunchingFromHyperLink){
                winPresenter.initDrillWindow(item, searchVal, response, timeSelectionData, fixedQueryId, isToggling,
                        elementClickedForTitleBar);
            }else{
                winPresenter.initWindow(item, searchVal, response, timeSelectionData, fixedQueryId, isToggling,
                        elementClickedForTitleBar);
            }

            if (isLaunchingFromHyperLink && searchVal != null) {
                // the launch section of meta data
                // - title params not really used - so copying what would happen
                // for drilldown
                view.upDateDrillWindowTitle(searchVal.searchFieldVal);
            }

            if (presetInfo != null) {
                /*
                 * moved after layout to prevent wandering window issues -
                 * window height getting larger and larger on each toggle inside
                 * tiled windows unless do down here
                 */
                setupViewFromPresetData(view, item.isDisablingTime());

            }

            if (presetInfo == null) {
                /*
                 * set the position of the new window to be down and to the
                 * right of the last window
                 */
                setWindowInitialPosition(view);
            }
            view.fitIntoContainer();
            view.putWindowToFront();
        }
    }

    /**
     * Position the window in the ContentPanel down and to the right of the last
     * opened window, when you reach the end of the ContentPanel move the window
     * to the right until you meet the left extreme of the ContentPanel then
     * open windows on top of one another
     *
     * @param view
     *            the window to be positioned
     *
     */
    protected void setWindowInitialPosition(final AbstractBaseWindowDisplay view) {

        final EniqWindow eniqWindow = view.getWidget();
        final Point lastPosition = workspaceController.getLastOpenedWindowPosition();
        final Point newPosition = eniqWindow.setWindowPosition(lastPosition);
        workspaceController.setLastOpenedWindowPosition(newPosition);
    }

    /*
     * Extra when add on multiple mode, e.g. adding capability for several
     * instances of "search field user windows" when user selected Multiple
     * option
     * 
     * Returns same view that can be used in single mode, but carries out checks
     * and sets ids etc when in multiple mode
     * 
     * Base window toolbars and time updates are off the view rather than
     * presenter
     * 
     * Returns null if not ready to create view in multiple mode or bringing an
     * existing window to front
     * 
     * @param searchVal current search field value
     * 
     * @return a regular view if not multi mode, or null
     */
    private AbstractBaseWindowDisplay getViewIfSuitableToOpenWindow(final SearchFieldDataType searchVal,
            WindowState windowState) {

        if (showNoWindowAndWarn(searchVal)) {
            return null; // warned and exiting
        }

        /*
         * the code for adding InstanceWindowTypeKey needed for multiple mode is
         * in the view creation - i.e. this needs to be done first
         */
        final AbstractBaseWindowDisplay view = createView(multiWinId, windowState);

        return view;
    }

    /**
     * TODO a little bit ugly - this was added for grouping grid in cause code
     * 
     * THIS IS NOW also used to move from regular grid (columns) to one with
     * modified (licenced) headers customimised show/hide columns component
     * @param isDrilling - This method is called whether for parent or child window, so flag to indicate if we are drilling or not.
     *
     * @see #launchWindow(com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType, boolean, String, com.ericsson.eniq.events.ui.client.common.comp.WindowState)
     */
    public void launchDrillDownWindow(final String winTitle, final GridInfoDataType gridInfoDataType,
            final Response response, final JSONValue data, final List<Filter> filters, final Menu breadCrumbMenu,
            final SearchFieldDataType searchData, final String widgetURLParameters, final String wsURL,
            final TimeInfoDataType timeInfo, final boolean isToggling, final WindowState windowState, boolean isDrilling) {

        /*
         * if searchData is available from breadCrumbMenuItem, use it
         * otherwise, get searchVal from Preset data or Menu
         */

        final SearchFieldDataType searchVal;
        if (searchData != null) {
            searchVal = searchData;
        } else {
            /* presetInfo will be null most of time */
            searchVal = (presetInfo == null) ? workspaceController.getSearchComponentValue(windowId)
                    : presetInfo.searchFieldValue;
        }
        final AbstractBaseWindowDisplay view = createView(multiWinId, windowState);
        /**
         * Set the window category (Used for docking on taskbar). If we are going to a child (drill) window, use the grid id. if we are going to a primary or parent
         * window, lets use the meta data id. In this case we should be in a drill down state. But of course we come in here when returning to the parent window also.
         * so we need to check in windowState.  
         */
        if (isDrilling) {
            view.setWindowCategoryId(gridInfoDataType.gridId);
        }
        final BaseWindowPresenter<? extends WidgetDisplay> winPresenter = createPresenter(view, multiWinId);
        view.setPresenter(winPresenter);
        view.addLaunchButton();

        containingPanel.add(view.asWidget());

        /*
         * item id serves as a query id. Pass in either the current or cached
         * (for toggling grid-graph off previous search in a multi-instance
         * scenario) value in search field in case needs to populate straight
         * away
         */
        winPresenter.initWinFromGridViewChangeEvent(item, searchVal, gridInfoDataType, response, data, filters,
                fixedQueryId, widgetURLParameters, wsURL, timeInfo, winTitle, isToggling);

        containingPanel.layout();
        if (presetInfo != null) { /* will be most of time */
            setupViewFromPresetData(view, false);
        }

        final Button btnBreadCrumb = getButton(view.getWindowToolbar(), BTN_NAV);
        if (btnBreadCrumb != null) {
            btnBreadCrumb.getMenu().removeAll();
            final int counter = breadCrumbMenu.getItemCount();

            for (int i = 0; i < counter; i++) {
                /*
                 * need to update the listener on the new breadcrumb with
                 * pointer to the new window
                 */
                final BreadCrumbMenuItem updatedMenuItem = winPresenter
                        .reApplyBreadCrumbMenuItemListener((BreadCrumbMenuItem) breadCrumbMenu.getItem(0));
                btnBreadCrumb.getMenu().add(updatedMenuItem);
            }

            checkEnableForNavBtns(view, btnBreadCrumb.getMenu(), getComponent(btnBreadCrumb.getMenu()));

            final JSONValue jsonVal = (response != null && response.getText().length() > 0) ? JSONUtils.parse(response
                    .getText()) : data;

            if (isSuccessfulResponse(jsonVal)) {
                view.putWindowToFront();
            }
        }
    }

    private boolean isSuccessfulResponse(final JSONValue jsonVal) {
        if (jsonVal != null) {
            final JsonObjectWrapper metaData = new JsonObjectWrapper(jsonVal.isObject());
            return Boolean.parseBoolean(metaData.getString(CommonConstants.SUCCESS));
        }
        return false;
    }

    /*
     * Show warning in multiple mode if user launches window without populating
     * search field (Too much to handle non node windows in multiple mode, so
     * not allowing user to do it - force search field population)
     * 
     * Also added an "input" search field type - can not launch empty grid in
     * single mode with that (well for multi result set grid ids)
     */
    private boolean showNoWindowAndWarn(final SearchFieldDataType searchData) {

        if (item.isSearchFieldUser()) {
            // if say "Input" in search field (and not launched from hyperlink)

            // (Unfortunately "search data" getType includes "INPUT" but val ok
            final boolean isSearchDataOk = !(searchData == null || searchData.isEmpty());

            if (isSearchDataOk) {
                return false;
            }

            final MessageDialog dialog = createMessageDialogNextToSearchField();
            dialog.setSize(300 + "px", 120 + "px");
            dialog.show(item.getText() + " " + MISSING_INPUT_DATA, NEED_SEARCH_FIELD_MESSAGE);
            return true;
        }

        return false;
    }

    /*
     * want message dialog regarding search field input, they want it positioned
     * near search field rather than default (center)
     */
    private MessageDialog createMessageDialogNextToSearchField() {
        final MessageDialog dialog = new MessageDialog();

        int left = 0;
        int top = 0;
        /** Casting to Menu Task Bar here as this will never be used for our Workspace controller. **/
        final Component searchComp = workspaceController.getSearchComponent();

        if (searchComp != null) { // it will be in the ranking tab

            left = searchComp.getAbsoluteLeft();
            top = ((MenuTaskBar) workspaceController).getAbsoluteTop()
                    + ((MenuTaskBar) workspaceController).getHeight();
            int width = searchComp.getOffsetWidth();
            width = (width < MESSAGE_DIALOG_MIN_WIDTH) ? 255 : width;
            dialog.setWidth(width + "px");
            dialog.setPopupPosition(left, top);
        }
        return dialog;
    }

    /**
     * Create unique id for window based on tab id and random number
     * @return
     */
    protected MultipleInstanceWinId createMultipleInstanceWinId() {
        return new MultipleInstanceWinId(workspaceController.getTabOwnerId(), WorkspaceUtils.generateId());
    }

    private void checkEnableForNavBtns(final AbstractBaseWindowDisplay view, final Menu menu, final Component item) {
        final BreadCrumbMenuItem breadCrumbMenuItem = (BreadCrumbMenuItem) item;
        final Component btnBack = view.getWindowToolbar().getItemByItemId(BTN_BACK);
        final Component btnForward = view.getWindowToolbar().getItemByItemId(BTN_FORWARD);

        if (btnBack != null && btnForward != null) {
            if (breadCrumbMenuItem != null) {
                final int index = breadCrumbMenuItem.getIndex();
                btnBack.setEnabled(index != 0);
                if (menu != null) {
                    final int i = menu.getItemCount() - 1;
                    btnForward.setEnabled(index != i);
                }
            } else {
                btnBack.setEnabled(false);
                btnForward.setEnabled(false);
            }
        }
    }

    Component getComponent(final Menu menu) {
        Component item = null;
        for (int i = 0; i < menu.getItemCount(); i++) {
            if (((BreadCrumbMenuItem) menu.getItem(i)).isGridDisplayed()) {
                item = menu.getItem(i);
                break;
            }
        }
        return item;
    }

    /* extracted for junit */
    Button getButton(final BaseToolBar winToolBar, final String btnID) {
        return winToolBar.getButtonByItemId(btnID);
    }

    @Override
    public void componentSelected(final MenuEvent ce) {
        launchWindow(false);
    }

    /*
     * Use previous window information (size, position, last refresh time stamp)
     * to create new window, i.e. for toggle scenario when toggling from grid to
     * graph and vice versa
     * 
     * The toolbar will have to toggle also so available in winPresenter
     * #initWindow (which could mean back to original depending on number of
     * toggle clicks)
     */
    private void setupViewFromPresetData(final AbstractBaseWindowDisplay view, final boolean disableTime) {
        final EniqWindow window = view.getWidget();

        if (presetInfo.winProps.isMaximized) { // it is needed to pass the previous window restore size and etc.
            window.maximize(presetInfo.winProps);
        } else {
            window.setPagePosition(presetInfo.winProps.absoluteLeft, presetInfo.winProps.absoluteTop);
            window.setSize(presetInfo.winProps.offsetWidth, presetInfo.winProps.offsetHeight);
        }

        view.updateLastRefreshedTimeStamp(presetInfo.lastRefreshedTimeStamp);
        if (!disableTime) {
            view.updateTime(presetInfo.timeSelectionData);
        }
    }
}