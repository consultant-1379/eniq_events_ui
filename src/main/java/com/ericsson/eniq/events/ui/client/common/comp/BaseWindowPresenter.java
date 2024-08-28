/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.ui.client.buttonenabling.ToolBarButtonManager;
import com.ericsson.eniq.events.ui.client.charts.window.ChartWindowPresenter;
import com.ericsson.eniq.events.ui.client.common.GridRefreshTimerObservable;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.ReadLoginSessionProperties;
import com.ericsson.eniq.events.ui.client.common.listeners.ToolBarMenuItemListener;
import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.ericsson.eniq.events.ui.client.datatype.*;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.ericsson.eniq.events.ui.client.events.*;
import com.ericsson.eniq.events.ui.client.events.handlers.ServerFailedResponseHandler;
import com.ericsson.eniq.events.ui.client.events.handlers.ServerRequestHandler;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import java.util.List;

import static com.ericsson.eniq.events.common.client.json.MetaDataParserUtils.GRID_GROUPING_VIEW;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.comp.TitleUtils.removeDuplicateDrillWordsFromTitle;

/**
 * Base window presenter to support common functionality for window business logic. Windows can be grids or charts.
 * <p/>
 * Specifically for windows launched from menu item which might or might not need to read search field. When search
 * field is empty and need to read search field will launch empty.
 *
 * @author eeicmsy
 * @see com.ericsson.eniq.events.ui.client.common.widget.EventGridPresenter
 * @see com.ericsson.eniq.events.ui.client.charts.window.ChartWindowPresenter
 * @since Feb 2009
 */
public abstract class BaseWindowPresenter<D extends IBaseWindowView> extends BasePresenter<D> implements IBaseWindowPresenter {

    /* Drilldown variables: TODO: these fields are not supported correctly - presenters and views are recreated
    without passing these parameters between them */
    protected int drillDepth;

    protected String drillDownWidgetID;

    private final IMetaReader metaReader = MainEntryPoint.getInjector().getMetaReader();

    /* Query/Window variables */ SearchFieldDataType searchData; // searchField data (retained after each service's action

    private TimeInfoDataType timeData = TimeInfoDataType.DEFAULT;

    private TimeInfoDataType cachedTimeData = TimeInfoDataType.DEFAULT;

    private Response responseObj; // Set by sub class - can be set to null to force a new call

    private String fixedQueryId; // Window instance id

    protected String currentWidgetID; // Current fixedQueryID - used to determine if the window needs to be

    // reinitialised

    private InitializeToolbarHandler<? extends IExtendedWidgetDisplay> initToolBarHandler;

    /*
     * Meta data - stores information about the window (if this proves difficult read from MetaReader (with correct
     * winID)
     */
    private MetaMenuItem origWinMeta; // For resetting window params that are modified in a drilldown

    /* Window specifics from meta data (access for junit) */
    protected MetaMenuItem metaMenuItem;

    /* tab owner id which will remain with window for its life */
    protected String tabOwnerId = null;

    /*
     * isLaunchedInToggleSenario ::a window created following a killing of a previous window to say replace a chart
     * (presenter) with a grid (presenter)
     */
    private boolean isLaunchedInToggleSenario, initWindowComplete, isDrilledDownScreen = false;

    /* split out server communication for window to this class */
    private final BaseWinServerComms<D> serverComm;

    /* Handlers */
    private final BaseWinGraphToGridToggleHandler<D> graphToGridToggleHandler;

    private final BaseWinChangeGridViewHandler<D> changeGridViewHandler;

    private RefreshWindowEventHandler refreshWindowHandler;

    private final BaseWinSearchFieldValueResetHandler<D> searchFieldResetHandler;

    private TimeParameterValueChangeEventHandler timeParameterChangeHandler;

    private final BaseWinWidgetSpecificParamsChangeHandler<D> widgetSpecificParamsChangeHandler;

    // variable as may need to replace with our own one (for wizard handling)
    private HandlerRegistration timeHandlerRegistration = null;

    private HandlerRegistration refreshHandlerRegistration = null;

    private boolean isOrigRefreshAndTimeRegDone;

    private boolean isPathSearchFieldUser = false;

    private ServerRequestHandler serverRequestHandler;

    private final MultipleInstanceWinId multiWinId;

    private boolean isDrillDown = false;

    /**
     * Base presenter for all floating windows
     *
     * @param display  MVP view class
     * @param eventBus MVP event bus
     */
    public BaseWindowPresenter(final D display, final MultipleInstanceWinId multiWinId, final EventBus eventBus) {
        super(display, eventBus);
        this.multiWinId = multiWinId;
        graphToGridToggleHandler = new BaseWinGraphToGridToggleHandler<D>(this, eventBus, display);
        changeGridViewHandler = new BaseWinChangeGridViewHandler<D>(this, eventBus, display);
        searchFieldResetHandler = new BaseWinSearchFieldValueResetHandler<D>(this);

        widgetSpecificParamsChangeHandler = new BaseWinWidgetSpecificParamsChangeHandler<D>(this);
        /* handles all server communication for window with this */
        serverComm = new BaseWinServerComms<D>(this, eventBus, display, searchFieldResetHandler);

        addEventBusHandlers();
        ((IExtendedWidgetDisplay) display).updateTimeFromPresenter(timeData);
        tabOwnerId = getTabOwnerId(); // NOPMD by EEICMSY on 08/12/10 11:08
    }

    /**
     * The server just wants to know if it is a grid or a chart for outbound parameter
     *
     * @return OUT_BOUND_CHART_DISPLAY_PARAM or OUT_BOUND_GRID_DISPLAY_PARAM
     */
    protected abstract String getOutBoundDisplayTypeParameter();

    /* *********************** */
    /* Initialisation methods */
    /* *********************** */

    public void initDrillWindow (final MetaMenuItem meta, final SearchFieldDataType searchFieldData, final Response response,
                                 final TimeInfoDataType presetTimeData, final String fixedQueryID, final boolean isToggling,
                                 final String elementClickedForTitleBar){
        this.isDrillDown = true;
        initWindow(meta, searchFieldData, response, presetTimeData, fixedQueryID, isToggling, elementClickedForTitleBar);
    }

    /**
     * Method called at menu item window create time Window (of type Grid or ranking etc) has been created and is up on
     * screen at this point, i.e. it can be launched empty.
     * <p/>
     * The objective now is to determine if need to populate this window straight away (i.e. if enough data is available
     * to make a call}).
     *
     * @param meta                      MenuItem data used to launch window (meta data), including id for query, etc. Id of window and Id sent
     *                                  to event bus (tracks the call back to the correct window)
     * @param searchFieldData           - current data present in search field for tab at time of window launch (less we need to make a server
     *                                  call for this window straight away)
     * @param response                  - will not attempt to make a server call to fetch data when this is set (i.e. its null in most cases,
     *                                  except the things like when toggling from a graph to a grid and already have data - i.e. this is
     *                                  legacy response data from a previous window that existed (not this one))
     * @param presetTimeData            - Can launch a window with time which is not the default in scenario for example when are drilling
     *                                  from a chart and creating a new grid (a toggle with a new server call - null response being passed
     *                                  here), but want to preserve the chart windows current time settings across to new call Pass Null if
     *                                  not displaying time control.
     * @param fixedQueryID              - If null is passed will use the queryId in BaseWindowPresenter We have the queryId windows (same as
     *                                  winId) to identify grid or chart, but passing it in as a parameter here too because of multiple window
     *                                  instances scenario (a multiple instance chart being toggled to a table where the queryId has become a
     *                                  composite of queryId and search field data), i.e. to ensure fixed one is used when reading metadata
     *                                  for new grid
     * @param isToggling                true to maintain state of toolbars, etc. (we are creating a new window - to convert say from chart to
     *                                  grid - but want to maintain states from old window)
     * @param elementClickedForTitleBar used to carry chart clicked info if exists into grid title (when go from chart element  to grid)
     */

    public void initWindow(final MetaMenuItem meta, final SearchFieldDataType searchFieldData, final Response response,
            final TimeInfoDataType presetTimeData, final String fixedQueryID, final boolean isToggling,
            final String elementClickedForTitleBar) {

        this.drillDepth = 0;
        getView().getWindowState().resetDrillDepth();
        this.isLaunchedInToggleSenario = isToggling;

        if (isToggling) {
            /*
             * ensure chart drill does not over-write cached time for cases when time component has unique defaults per
             * view window
             */
            setTempTimeData(presetTimeData);
        } else {
            setTimeData(presetTimeData);
        }

        setWindowTimeDate(presetTimeData);

        /*
         * creating this new MetaMenuItem here would breaks code for windows in toggle state, i.e to toggle toolbars
         * when drilling on charts, or toggling charts to grids and resetting URLs between view menu changes (so don't do
         * it when toggling)
         * 
         * Reading entire fresh MetaMenuItem on purpose here for new windows, (because changing from chart to grid is
         * corrupting the "data" inside MetaMenuItem we have (do not want new windows launched for menu item affected by
         * previous launch of window).
         */
        origWinMeta = getFreshMetaMenuItem(meta); // fresh uncorrupted from MetaReader cache
        metaMenuItem = (isToggling) ? meta : origWinMeta;

        this.isPathSearchFieldUser = SearchFieldUser.PATH == metaMenuItem.getSearchFieldUser();

        /* not setting search data when not using so that say when launch from 
           a ranking grid we do not pull in taskbar search data (e..g group) into launched window) */
        if (this.isSearchFieldDataRequired()) {
            resetSearchData(searchFieldData);
        }

        /* register window for automated refresh */
        if (metaMenuItem.getWindowType() == MetaMenuItemDataType.Type.RANKING) {
            registerWindowAsRankingWindow();
        }
        /* now that query id known (leave here even if not currently making call - e.g. may need for refresh etc) */

        registerHandler(getEventBus().addHandler(FailedEvent.TYPE, getServerFailedResponseHandler()));
        registerHandler(getEventBus().addHandler(ServerRequestEvent.TYPE, getServerRequestHandler()));

        setFixedIDConsideringMultiResultSet(fixedQueryID);

        initializeWidgit(this.getFixedQueryId());
        initializeToolbar(presetTimeData != null);

        setupTimeAndRefreshHandlingIfNeeded();

        // Only proceed as normal is this is Window does not contain a wizard
        if (metaMenuItem.getWizardId().isEmpty()) {
            if (response == null) {
                getServerComm().potentiallyMakeCallOnWindowLaunch(searchFieldData);
            } else {
                /*
                 * you have data already from when you were a grid or a chart (don't want eventbus here)
                 */
                handleSuccessResponse(response);
            }
        }
        metaMenuItem.setLaunchedFromCellHyperlink(false); // Resetting the state of this flag
        initWindowComplete = true; // attempt for chart drilldown to get to a grid - to avoid
        // BaseWinSearchFieldValueResetHandler converting back to chart

        // to carry chart clicked info if exists into grid title
        getView().appendTitle(elementClickedForTitleBar);

        if (metaMenuItem.isDisablingTime()) {
            final BaseToolBar baseToolbar = ((IExtendedWidgetDisplay) getView()).getWindowToolbar();
            baseToolbar.disableTimeRangeComp();
        }
    }

    public void initWinFromGridViewChangeEvent(final MetaMenuItem meta, final SearchFieldDataType searchfieldData,
            final GridInfoDataType gridInfoDataType, final Response response, final JSONValue data,
            final List<Filter> filters, final String fixedQueryID, final String widgetURLParameters, final String wsURL,
            final TimeInfoDataType timeInfo, final String sTitle, final boolean isTogglingFromGraph) {

        if (!isTogglingFromGraph) {
            this.drillDepth = 1;
        }

        resetSearchData(searchfieldData);

        this.isLaunchedInToggleSenario = isTogglingFromGraph;
        origWinMeta = getFreshMetaMenuItem(meta); // fresh uncorrupted from MetaReader cache
        // toggle check for same reason as #initWindow
        metaMenuItem = (isTogglingFromGraph) ? meta : origWinMeta;
        this.isPathSearchFieldUser = SearchFieldUser.PATH == metaMenuItem.getSearchFieldUser();

        // Need to use the updated wsURL and timeInfo
        setWsURL(wsURL);
        setTimeData(timeInfo);
        setWindowTimeDate(timeInfo);

        /*
         * store url parameters from original window that will be needed when building up URL again e.g. Time Change
         * these need to be held against the widget to allow the Breadcrumb to access
         */
        ((IExtendedWidgetDisplay) getView()).updateWidgetSpecificURLParams(widgetURLParameters);

        /* now that query id known (leave here even if not currently making call - e.g. may need for refresh etc) */
        registerHandler(getEventBus().addHandler(ServerRequestEvent.TYPE, getServerRequestHandler()));
        registerHandler(getEventBus().addHandler(FailedEvent.TYPE, new ServerFailedResponseHandler(multiWinId, getView(), metaMenuItem)));

        setFixedIDConsideringMultiResultSet(fixedQueryID);
        initializeWidgitWithGridInfo(gridInfoDataType, false, sTitle); /* calls to sub class */

        initializeToolbar();
        // (prob not needed in this method but safter to call)
        setupTimeAndRefreshHandlingIfNeeded();

        if (data == null) { /* possibly already have data from from grid or a chart (don't want eventbus here) */
            handleSuccessResponse(response);
        } else {
            handleSuccessResponseWithJSONValue(response, data, filters);
        }

        /*
         * override the title with the provided value, original was correct before window killed for changing from a
         * Group to Grid or vice versa
         */
        getView().updateTitle(sTitle);

        metaMenuItem.setLaunchedFromCellHyperlink(false); // Resetting the state of this flag
    }

    void registerWindowAsRankingWindow() {
        GridRefreshTimerObservable.getInstance(getEventBus()).registorGrid(multiWinId);
    }

    void removeWindowAsRankingWindow() {
        GridRefreshTimerObservable.getInstance(getEventBus()).removeGrid(multiWinId);
    }

    /**
     * Method to set or reset the enabling status of buttons on the window toolbar (those buttons handled in
     * ToolBarButtonManager)
     * <p/>
     * Call any time need to reset enabling status of any toolbars on the window Always call this on server response
     * (good or bad) to set relevant toolbar button enable status.
     *
     * @param rowCount count on grid or chart
     */
    public void handleButtonEnabling(final int rowCount) {
        /* fetch current conditions (affects all buttons in toolbar ) at time of calling */
        final ButtonEnableParametersDataType params = getButtonEnableParameters(rowCount);
        final BaseToolBar baseToolbar = ((IExtendedWidgetDisplay) getView()).getWindowToolbar();
        ToolBarButtonManager.handleToolbarButtonEnabling((IExtendedWidgetDisplay) getView(), baseToolbar, params);
    }

    /**
     * Utility for {@link #handleButtonEnabling(int)} call to fetch current button applicable parameters for button
     * enabling
     *
     * @param rowCount zero for a chart, else grid row count
     *
     * @return state of ButtonEnableParametersDataType at time of call
     */
    protected abstract ButtonEnableParametersDataType getButtonEnableParameters(final int rowCount);

    /**
     * Initialise the toolbar on grid or chart
     *
     * @param displayingTime true if displaying the time component on the window toolbar
     */
    public void initializeToolbar(final boolean displayingTime) {
        getInitToolBarHandler().initializeToolbar(displayingTime);
        handleButtonEnabling(0); // initialise state regardless of meta data enabling
    }

    public void initializeToolbar() {
        initializeToolbar(getTimeData() != null);
    }

    protected GridInfoDataType getGridInfoFromMetaReader(final String value) {
        return metaReader.getGridInfo(value);
    }

    @Override
    public void sendCancelRequestCall() {
        getServerComm().sendCancelRequestCall();
    }

    /*
     * take some reused code for setting fixed quiet id hasMultiResultSet : has event the potential to return different
     * resultsets based on the query params, i.e. need to use a different grids to display the potential responses.
     */
    private void setFixedIDConsideringMultiResultSet(final String fixedQueryID) {
        this.setFixedQueryId((fixedQueryID == null) ? metaMenuItem.getId() : fixedQueryID);

        if (metaMenuItem.hasMultiResult()) {
            // account for search param not provided so use a default of empty
            buildFixedQueryId(searchData);
        }
    }

    void buildFixedQueryId(final SearchFieldDataType searchData) {
        String id = EMPTY_STRING;

        if (searchData == null) {
            id = metaMenuItem.getId();
        } else if (searchData.isGroupMode() && !metaMenuItem.isLaunchedFromCellHyperlink()) {
            id = searchData.getType() == null ? metaMenuItem.getId() + UNDER_SCORE_GROUP : metaMenuItem.getId() + UNDERSCORE + searchData.getType() + UNDER_SCORE_GROUP;
        } else {
            id = metaMenuItem.getId() + (((searchData.getType() == null) || searchData.getType().equalsIgnoreCase(EMPTY_STRING)) ? EMPTY_STRING : UNDERSCORE + searchData.getType());
        }
        this.setFixedQueryId(id);
    }

    /*
     * Regardless of state (breadcrumb) of current grid - clear the lot and start from 
     * the menu item version (grid meta menu item) - e.g. clear for search field change back
     * to summary screen
     */
    void handleSearchFieldUpdateWithGridClear(final SearchFieldDataType data) {

        buildFixedQueryId(data);

        if (currentWidgetID == null || isChart()) {
            // wizard overlay
            return;
        }

        // check if a different widget or if original widget has been drilled away from
        final String calculatedFixedQueryId = getFixedQueryId(); // e.g. NETWORK_EVENT_ANALYSIS_APN_GROUP

        // reset these values as could have been overridden by drilldown
        metaMenuItem = new MetaMenuItem(origWinMeta);

        setSearchFieldUser(origWinMeta.getSearchFieldUser());
        setWsURL(origWinMeta.getWsURL());

        metaMenuItem.setDisplay(origWinMeta.getDisplay());
        metaMenuItem.setQueryType(origWinMeta.getQueryType());
        metaMenuItem.setQueryKey(origWinMeta.getQueryKey());

        this.drillDepth = 0;
        getView().getWindowState().resetDrillDepth();

        isDrilledDownScreen = false;

        final GridInfoDataType gridMetaData = getGridInfoFromMetaReader(calculatedFixedQueryId);
        // call reinitialise on grid columns and title - slight deviation in method if this is a grouping grid
        initializeWidgitWithGridInfo(gridMetaData, gridMetaData.gridType.equals(GRID_GROUPING_VIEW), gridMetaData.gridTitle);
        initializeToolbar(getTimeData() != null);

    }

    /* ****************** */
    /* Breadcrumb methods */
    /* ****************** */

    /**
     * initialise the new bread crumb menu item with vars that are specific to the new window that has been created this
     * is used when a New window needs to be launched as a consequence of moving from a grouping grid to a normal grid
     * or vice versa
     */
    public BreadCrumbMenuItem reApplyBreadCrumbMenuItemListener(final BreadCrumbMenuItem item) {
        item.removeAllListeners();
        item.addSelectionListener(new ToolBarMenuItemListener(getEventBus(), (IExtendedWidgetDisplay) getView(), EventType.NAVIGATION));
        item.setId(getFixedQueryId() + UNDERSCORE + String.valueOf(drillDepth));
        return item;
    }

    /**
     * Utlity to call when know you do not want to read widget parameters from breadcrumb menu when making server call
     * (over-ride when a grid)
     * <p/>
     * (Needed when switch view menu on grid (terminal analysis) after drilling down on grid that supported breadcrumb)
     */
    public void clearCurrentBreadCrumbMenuItem() { // override when grid
    }

    /* ******************* */
    /* Server call methods */
    /* ******************* */

    /**
     * build up the parameters to pass on the url (method extracted for unit test)
     *
     * @return calculated widget parameters
     */
    protected String getInternalRequestData() {
        return getServerComm().getInternalRequestData();
    }

    /**
     * Root for all requests for menu items windows to server. Call to the given Web Service for this window with
     * parameters (We will follow conversion with the parameters
     * http://www.eric.com/subscriberservoce?id=IMSI_EVENT_QUERY&1212121212121 Result handled by
     * SucessResponseEventHandler (the chart presenter and grid presenter)
     *
     * @param requestData fully built up parameters to pass with URI (can be build up or previously stored widget URLParameter).
     *                    e.g. ?time=30&display=grid&type=IMSI&tzOffset=+0000&maxRows=50
     */
    protected void makeServerRequestForData(final String requestData) {
        getServerComm().makeServerRequestForData(requestData);
    }

    /**
     * Get search parameter in URL format if applicable
     *
     * @return e.g. &imsi=val
     */
    @Override
    public String getSearchURLParameters() {
        return getServerComm().getSearchURLParameters();
    }

    protected void makeServerCallWithURLParams() {
        getServerComm().makeServerCallWithURLParams();
    }

    /**
     * Gets the maxRows URL parameter, including the regular URL parameter ("&maxRows=2000")
     *
     * @return The URL parameter, a String
     */
    public String getMaxRowsURLParameter() {
        String maxRowsParam = metaMenuItem.getMaxRowsParam();
        maxRowsParam = ((maxRowsParam == null) || (maxRowsParam.length() == 0)) ? DEFAULT_MAX_ROWS_VALUE : maxRowsParam;
        return CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR + MAX_ROWS_URL_PARAM + ReadLoginSessionProperties.getMaxRowsValue(maxRowsParam);
    }

    public String getDataTieredDelayURLParameter() {
        return (metaMenuItem.getDataTieredDelayParam()) ? CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR +
                DATA_TIERED_DELAY_PARAM + "TRUE" : EMPTY_STRING;
    }

    @Override
    public PresetResponseDisplayDataType getPresetResponseDisplayData() {
        Widget win = getView().asWidget();
        WindowPropertiesDataType winProperties = new WindowPropertiesDataType(win);

        final String lastRefreshedStamp = ((IExtendedWidgetDisplay) getView()).getLastRefreshTimeStamp();
        final TimeInfoDataType timeSelectionData = ((IExtendedWidgetDisplay) getView()).getTimeData();

        return new PresetResponseDisplayDataType(getResponseObj(), searchData, winProperties, lastRefreshedStamp, timeSelectionData);
    }

    /* ******************** */
    /* Window title methods */
    /* ******************** */

    /**
     * When know you want the search data (from whatever source - e.g. hyper link) on the window title
     *
     * @param data search field data type
     */
    protected void upDateWindowTitleWithSearchData(final SearchFieldDataType data) {
        getView().updateSearchFieldDataType(data);
    }

    /**
     * Update the title on the window with what ever data is in search field if search field necessary for window in
     * format required by UI specification
     */
    protected void upDateWindowTitleWithSearchDataIfRequired() {

        if (searchData != null && !searchData.isEmpty()) {

            final boolean isSearchFieldUser= false;

            if (isSearchFieldUser) {
                // which includes search data gathered from grid row (drillDownWindows - isTitleParam)
                upDateWindowTitleWithSearchData(searchData);
            }
        }
    }

    /**
     * Update the title on the window with what ever data is in drill down parameters metadata in the format required by
     * UI specification
     * Method is updated for TR HR83479 Issue 1 : Window Title should not contain duplicate node name.
     * like  BSC1 - BSC1 - KPI Analysis By CauseCode  should be  BSC1 - KPI Analysis By CauseCode
     */
    protected void upDateWindowTitleWithDrillDownDataDataIfRequired(final String drillParameters) {
        final StringBuilder buffer = new StringBuilder(drillParameters);
        String title = removeDuplicateDrillWordsFromTitle(drillParameters,getView().getBaseWindowTitle());
        buffer.append(title);
        getView().updateTitle(buffer.toString());
    }


    /* ************* */
    /* Handlers */
    /* ************* */
    @Override
    public final void registerHandler(final HandlerRegistration handlerRegistration) {
        // exposing protect method
        super.registerHandler(handlerRegistration);
    }

    /**
     * Register common event bus handlers for window (base class maintains a list of registered HandlerRegistration each
     * of which can unregister when call #unbind (see handleShutdown)). NOT registoring when #unbind would fail - in
     * which case we handle these ourselves (Subclasses of this presenter should also register handlers using
     * #registerHandler to ensure clean window closure).
     */
    private final void addEventBusHandlers() {

        // note time and refresh not done here (suits wizard handling) */

        registerHandler(getEventBus().addHandler(SearchFieldValueResetEvent.TYPE, searchFieldResetHandler));
        registerHandler(getEventBus().addHandler(GraphToGridEvent.TYPE, graphToGridToggleHandler));
        registerHandler(getEventBus().addHandler(ChangeGridViewEvent.TYPE, changeGridViewHandler));
        registerHandler(getEventBus().addHandler(WidgetSpecificParamsChangeEvent.TYPE, widgetSpecificParamsChangeHandler));
    }

    /**
     * Utility to replace the TimeParameterValueChangeEvent.TYPE and RefreshWindowEvent.TYPE with own handling In GXT
     * land (see even tbus methods) it all ends up in a map with the TYPE as the key in a Map so can replace direct
     */
    public void replaceTimeAndRefreshHandlers(final TimeParameterValueChangeEventHandler anotherTimeHandler,
            final RefreshWindowEventHandler anotherRefreshHandler) {

        if ((anotherTimeHandler != null) && (anotherRefreshHandler != null)) {

            timeParameterChangeHandler = anotherTimeHandler;
            timeHandlerRegistration = (getEventBus().addHandler(TimeParameterValueChangeEvent.TYPE, anotherTimeHandler));
            registerHandler(timeHandlerRegistration);

            refreshWindowHandler = anotherRefreshHandler;
            refreshHandlerRegistration = (getEventBus().addHandler(RefreshWindowEvent.TYPE, anotherRefreshHandler));
            registerHandler(refreshHandlerRegistration);

        }
    }

    /* do after init toolbar */
    private void setupTimeAndRefreshHandlingIfNeeded() {

        if (metaMenuItem == null) { // have to call post init (toolbar must be present)
            return;
        }

        /*
         * its ok for fixed wizard to use default refresh and time action it does not have to fetch new checkboxes
         * Important though dynamic wizard does not use default time and refresh
         */
        if (getInitToolBarHandler().containsDynamicWizard()) {
            return;
        }
        if (isOrigRefreshAndTimeRegDone) {
            return;
        }

        replaceTimeAndRefreshHandlers(createDefaultTimeParameterValueHandler(), createDefaultRefreshWindowHandler());

        isOrigRefreshAndTimeRegDone = true;

    }

    BaseWinTimeParameterValueHandler<?> createDefaultTimeParameterValueHandler() {
        return new BaseWinTimeParameterValueHandler<D>(this);
    }

    BaseWinRefreshWindowHandler<?> createDefaultRefreshWindowHandler() {
        return new BaseWinRefreshWindowHandler<D>(this);
    }

    /**
     * Return a reference to the InitializeToolbarHandler set up by this class should the subclasses need to call its
     * methods
     *
     * @return init
     */
    protected InitializeToolbarHandler<? extends IExtendedWidgetDisplay> getInitToolBarHandler() {
        if (initToolBarHandler == null) {
            initToolBarHandler = new InitializeToolbarHandler<IExtendedWidgetDisplay>((IExtendedWidgetDisplay) getView(), getEventBus(), this);
        }
        return initToolBarHandler;
    }

    /**
     * Direct call to toggle window from graph to grid and vice versa
     */
    public void handleGraphToGridToggle(final String toolbarType, final String elementClickedForTitleBar) {
        graphToGridToggleHandler.handleGraphToGridToggle(multiWinId, false, toolbarType, elementClickedForTitleBar);
    }

    public void handleGraphToGridToggleReset(final SearchFieldDataType data) {
        graphToGridToggleHandler.handleGraphToGridToggleReset(multiWinId, data);
    }

    /**
     * Direct call to refresh window with its own URL parameters (not using event bus)
     */
    public void handleWindowRefresh() {
        refreshWindowHandler.handleWindowRefresh();
    }

    /**
     * Guard to check when event bus broadcasts, i.e. to check if this window is the one that need to react to the
     * response NOTICE NOT CACHED : The search field data for a window can change over its life
     *
     * @param multiWinId window id with tab id and multiple window instance support
     *
     * @return true if this is the window that matches multiwinID
     */
    public boolean isThisWindowGuardCheck(final MultipleInstanceWinId multiWinId) {

        if (!getTabOwnerId().equals(multiWinId.getTabId())) {
            return false;
        }
        final String compareId = multiWinId.getWinId();

        /**
         * ecarsea. We are generating window ids now with a random number generator rather than based on meta data and search data etc in the new workspace
         * so added in a check for this id.
         */
        final boolean isQueryIdOK = compareId.equals(((IExtendedWidgetDisplay) getView()).getParentWindow().getBaseWindowID());
        if (!isQueryIdOK) {
            return false;
        }

        if (!this.metaMenuItem.getWizardId().isEmpty()) { // Don't want wizard overlay responses handled by base window
            return false;
        }
        return true;
    }

    /*
     * Direct call to shutdown window STILL NEED TO CALL HIDE ON VIEW
     */
    @Override
    public void handleShutDown() {

        if (getWindowType() == MetaMenuItemDataType.Type.RANKING) {
            removeWindowAsRankingWindow();
        }
        cleanUpOnClose();
        /*
         * #unbind clears List<HandlerRegistration> in event bus once handlers added via #registerHandler
         */
        unbind();
    }

    /**
     * Direct call for search field update
     *
     * @param tabId   id of the Tab to which the window belongs
     * @param queryId id of the Window to be updated
     * @param url     The default window URL (which may be for example lost when drill into KPI ratio)
     */
    public void handleSearchFieldParamUpdate(final String tabId, final String queryId, final SearchFieldDataType data,
            final String url) {
        searchFieldResetHandler.handleSearchFieldParamUpdate(tabId, queryId, data, url);
    }

    /**
     * direct call to time update
     *
     * @param multiWinID id of the Window to be updated, containing multiple instance window information when needed
     * @param time       new time info to update window with
     */
    public void handleTimeParamUpdate(final MultipleInstanceWinId multiWinID, final TimeInfoDataType time) {

        getTimeParameterValueChangeEventHandler().handleTimeParamUpdate(multiWinID, time);
    }

    TimeParameterValueChangeEventHandler getTimeParameterValueChangeEventHandler() {
        return timeParameterChangeHandler;
    }

    @Override
    public void handleGroupingGrid(final String winTitle, final GridInfoDataType gridInfoDataType,
            final Response response, final JSONValue data, final List<Filter> filters, final Menu breadCrumbMenu,
            final SearchFieldDataType searchData, final String wsURL, final TimeInfoDataType timeInfo,
            final boolean isTogglingFromGraph, boolean isDrilling) {

        changeGridViewHandler.handleChangeGridView(multiWinId, winTitle, gridInfoDataType, response, data, filters, breadCrumbMenu, searchData, wsURL, timeInfo, isTogglingFromGraph, isDrilling);
    }

    /*
     * Attempt to produce MetaMenuItem which is unaffected by previous opening of the window. Can go right back to
     * cached UIMetaData data for Menu TaskBar MenuItems otherwise using a new constructor (unless toggling - which is
     * not as safe)
     */
    private MetaMenuItem getFreshMetaMenuItem(final MetaMenuItem meta) {
        MetaMenuItem returnVal = getMenuTaskBarMenuItemByID(meta.getID());
        if (returnVal == null) {
            // MetaReader Method still returns null when not in tab, e.g. KPI button
            // (Bear in mind new MetaMenuItem call resets toolbar on it!)
            returnVal = (isLaunchedInToggleSenario) ? meta : new MetaMenuItem(meta);
        }
        return returnVal;
    }

    /*
     * Cached local reference to ServerRequestHandler (so can ensure load
     * mask works when change search data in mutliple mode)
     * @return local reference to ServerRequestHandler or null
     */
    private ServerRequestHandler getServerRequestHandler() {
        if (serverRequestHandler == null && metaMenuItem != null) {
            serverRequestHandler = new ServerRequestHandler(multiWinId, getView());
        }
        return serverRequestHandler;
    }

    /* junit */
    MetaMenuItem getMenuTaskBarMenuItemByID(final String id) {
        return metaReader.getMetaMenuItemFromID(id);
    }

    /* ******************* */
    /* Getters and setters */
    /* ******************* */
    @Override
    public String getTabOwnerId() {
        /* note called from constructor - can use tabOwnerId direct from there on */
        if (tabOwnerId == null) {
            tabOwnerId = ((IExtendedWidgetDisplay) getView()).getWorkspaceController().getTabOwnerId();
        }
        return tabOwnerId;
    }

    // for junit over-ride
    public BaseWinServerComms<D> getServerComm() {
        return serverComm;
    }

    protected boolean isDrilledDownScreen() {
        return isDrilledDownScreen;
    }

    protected void setDrilledDownScreen(final boolean isDrilledDownScreen) {
        this.isDrilledDownScreen = isDrilledDownScreen;
    }

    /**
     * 'On the fly' setting URL parameters via presenter and updating bread crumb for (e.g. recurring errors window
     * updates WidgetURLParameter on the fly following row selection change)
     *
     * @param widgetSpecificParams extra URL parameters for outbound call, e.g. &something=whatever May not be the full widget params.
     *                             Empty string clears presenter version (when supported)
     */
    public void setWidgetSpecificParams(final String widgetSpecificParams) {
        // the view version of meta menu item is one holding final state being used
        if (metaMenuItem.setWidgetSpecificParams(widgetSpecificParams)) {
            ((IExtendedWidgetDisplay) getView()).updateWidgetSpecificURLParams(widgetSpecificParams);

        }
    }

    protected String getWidgetSpecificURLParams() {
        return metaMenuItem.getWidgetSpecificParams();
    }

    protected MetaMenuItem getMetaMenuItem() {
        return metaMenuItem;
    }

    /**
     * Utility to know if window is interested in current search field type
     * based on excludedSearchParam added to meta data (meta menu item
     *
     * @param type type selection in search field - APN, BSC, etc as per searchField.json id
     *
     * @return true if this window should not react to changes of this type
     */
    public boolean isExcludedSearchType(final String type) {
        return type != null && metaMenuItem.getExcludedSearchTypes().contains(type);
    }

    /**
     * Utlity to check if window needs search data
     * From MetaMenuItem  - note  (which is terrible)-  some one changed search field user
     * in MetaMenuItem at run time when drill down to a new node. So this can go true and false on same
     * window
     * <p/>
     * Can not use InstanceWindowType.isInstanceWindowType(((IBaseWindowView) display).getBaseWindowID() here -
     * i.e. a true maintained for all drilldowns states - if used that condition that returned true here would
     * get Two type=TAC&tac=35179401 entries TODO rework when not in an EC build - with a view to avoiding metaMenuItem work
     * and centralising)
     *
     * @return true if windows needs search component data to populate
     */
    public boolean isSearchFieldDataRequired() {
        return metaMenuItem.isSearchFieldUser();
    }

    public SearchFieldUser getSearchFieldUser() {
        return metaMenuItem.getSearchFieldUser();
    }

    /**
     * flag defined in meta to determine if the query associated with this menu has the potential to return different
     * result sets (_APN, _SGSN )
     *
     * @return true if reusing same pice of meta data for different node types
     */
    public boolean hasMultiResultSet() {
        return metaMenuItem.hasMultiResult();
    }

    @Override
    public String getFixedQueryId() {
        return fixedQueryId;
    }

    /**
     * Update web service URL for the window
     *
     * @param url new web service url
     */
    @Override
    public void setWsURL(final String url) {
        metaMenuItem.setWsURL(url);
        ((IExtendedWidgetDisplay) getView()).getViewSettings().setWsURL(url);
    }

    /**
     * Update max rows param for the window
     *
     * @param maxRowsParam the key for the JNDI value representing the max number of rows for this item
     */
    public void setMaxRowsParam(final String maxRowsParam) {
        metaMenuItem.setMaxRowsParam(maxRowsParam);
    }

    @Override
    public String getQueryId() {
        return metaMenuItem.getID();
    }

    /**
     * Utility setting query id on window (for test)
     *
     * @param winId windId (queryId) defined in meta data (same as id will use for windows, buttons and menu items)
     */
    public void setQueryId(final String winId) {
        this.metaMenuItem.setId(winId);
    }

    /**
     * Utility to return display type . String defined in metaData, used to determine the type of display for this
     * window e.g. grid, barchart, linechart, etc FROM current meta data
     *
     * @return display type for window, e.g. GRID_DISPLAY, LINE_CHART_DISPLAY
     */
    public String getDisplayType() {
        return metaMenuItem.getDisplay();
    }

    /**
     * Utility to rset display type.
     *
     * @param displayType - String defined in metaData, used to determine the type of display for this window e.g. grid,
     *                    barchart, linechart, etc
     */
    public void setDisplayType(final String displayType) {
        metaMenuItem.setDisplay(displayType);
    }

    /**
     * Utility returning window type of this window
     *
     * @return window type, e.g. RANKING, GRID, CHART
     */
    public MetaMenuItemDataType.Type getWindowType() {
        return metaMenuItem.getWindowType();
    }

    /**
     * Utility returning window type of this window
     *
     * @return window type, e.g. RANKING, GRID, CHART
     */
    public void setWindowType(final MetaMenuItemDataType.Type type) {
        metaMenuItem.setWindowType(type);
    }

    /**
     * Returns the URL for the call. However is   "needSearchParam":"PATH",
     * is set change the return to append node type onto address
     * URL/BSC or baseURL/BSC_GROUP
     * <p/>
     * (address passed depends on the search type when you pass
     * "PATH" instead of "TRUE" into needSearchParam meta data
     *
     * @return web service URL for server calls in this window
     */
    public String getWsURL() {
        final String baseWsURL = metaMenuItem.getWsURL();

        if (isPathSearchFieldUser) {
            final String groupAppendage = (searchData.isGroupMode()) ? UNDER_SCORE_GROUP : EMPTY_STRING;
            return baseWsURL + "/" + searchData.getType() + groupAppendage;
        }
        return baseWsURL;
    }

    public String getWindowStyle() {
        return metaMenuItem.getStyle();
    }

    /**
     * Set time on a menu but don't affect cached value, e.g. business intel might want one view to launch with 1 days
     * work of data by default (which must follow though for drilldown on chart (hence toggle true check in
     * #intiWindow), but then must revert to last saved value when go to next menu option)
     *
     * @param inTimeData null to revert time to last saved data fir window (i.e. time other than one hard-coded in (e.g. see
     *                   DEFAULT_SUB_BI_BUSY_DAY_TIME_DATA)
     */
    public void setTempTimeData(final TimeInfoDataType inTimeData) {
        /* not updating cache one - which we will want to keep to revert to */
        final TimeInfoDataType tempTimeData = ((inTimeData == null) ? cachedTimeData : inTimeData);
        this.timeData = tempTimeData;
        ((IExtendedWidgetDisplay) getView()).updateTimeFromPresenter(tempTimeData);
    }

    /**
     * Updates time on presenter and view for window (sets cache of last saved time) Updates bread crumb so can reuse
     * directly later
     *
     * @param timeData the timeData to set
     */
    @Override
    public void setTimeData(final TimeInfoDataType timeData) {
        cachedTimeData = timeData;
        this.timeData = timeData;
        ((IExtendedWidgetDisplay) getView()).updateTimeFromPresenter(timeData);

        final BreadCrumbMenuItem breadCrumb = this.getCurrentBreadCrumbMenuItem();
        if (breadCrumb != null) {
            breadCrumb.setTimeData(timeData);
        }
    }

    public TimeInfoDataType getTimeData() {
        return timeData;
    }

    public boolean getIsDrillDown(){
        return this.isDrillDown;
    }

    public void setIsDrillDown(boolean isDrillDown){
        this.isDrillDown = isDrillDown;
    }


    /**
     * Ideally this would not change from origional *
     *
     * @param isSearchFieldUser true (or "path") to set if this window is interested in search field (MenuTaskBar cached this info initialy)
     */
    protected void setSearchFieldUser(final SearchFieldUser isSearchFieldUser) {
        metaMenuItem.setSearchFieldUser(isSearchFieldUser);
    }

    /**
     * Window can either be a chart or a grid.
     * <p/>
     * Utility to check THE CURRENT STATE of window, i.e. if window currently displaying as a grid when began life (from
     * fresh MetaReader metaData) as a chart.
     * <p/>
     * (To date no grid begins life as a grid and gets toggled to a chart. So This method not catering for this case.
     * All charts can be toggled to grids - from graph toggle or drilldown).
     *
     * @return true if window created as a chart is now a grid. false if window began as a chart and is now a grid or if
     *         window was always a grid.
     */
    protected boolean isWindowInToggledState() {

        if (this.isLaunchedInToggleSenario && initWindowComplete) {

            final boolean wasInitallyAChart = origWinMeta.getWindowType() == MetaMenuItemDataType.Type.CHART;
            // only ever start as a chart and toggle to grid not vice versa so not coding it
            if (wasInitallyAChart) {
                return (!isChart());
            }
        }
        return false;
    }

    /**
     * Utility (not using metaMenuItem) to see if presenting a chart currenlty
     *
     * @return true if presenting a chart (false if grid (or ranking))
     */
    public boolean isChart() {
        // hack existing to spare us asking if subclass is chart presenter or grid presenter
        return OUT_BOUND_CHART_DISPLAY_PARAM.equals(getOutBoundDisplayTypeParameter());
    }

    /**
     * Set search data on presenter. See sub class (EventGridPresenter) If the place button is enabled and presses with
     * exising data this method should return false
     *
     * @param data - search field data to update for this window - which may not necessarily correspond to the current
     *             value in the menu task bar search component.
     *
     * @return true if actually changing the data (if the data is different than current)
     */
    @Override
    public boolean resetSearchData(final SearchFieldDataType data) {

        // change toolbar not just for multiResults sets, e.g. subscriber overview wants to swap PTMSI and IMSI toolbar
        // complet'y messes up breadcrumb if call to reset on a grid
        final boolean resetToolBar = this instanceof ChartWindowPresenter;
        return resetSearchData(data, resetToolBar);
    }

    public boolean resetSearchData(final SearchFieldDataType data, final boolean resetToolBar) {

        if ((searchData != null) && searchData.equals(data)) {
            return false;
        }
        searchData = data;

        if (searchData != null) {
            this.searchData.setPathMode(isPathSearchFieldUser);
        }

        getView().resetSearchData(searchData);
        /**
         *  TR HR83479 Issue 2 & DEFTFTMIT-592
         *  only at the time of opening new window updateSearchFieldDataType(searchData);
         *  needs to call. Not at the time of back button click
         */
        if(drillDepth == 0) {
            getView().updateSearchFieldDataType(searchData);
        }

        final BreadCrumbMenuItem breadCrumb = getCurrentBreadCrumbMenuItem();
        if (breadCrumb != null) {
            breadCrumb.resetSearchData(searchData);
        }

        /* keep loading mask when drill say to TAC in multiple mode */
        if (serverRequestHandler != null) { // null because meta menu itme was null when called
            serverRequestHandler.resetSearchData(data);
        }

        if (resetToolBar) { // don't reset if drill from say imsi to tac or break navigation
            initializeToolbar();
        }
        return true;
    }

    protected void resetUpperToggleToolBar() {
        if (metaMenuItem != null) {
            metaMenuItem.reset();
        }
    }

    /**
     * Getter utlity for search data
     *
     * @return search data for window
     */
    @Override
    public SearchFieldDataType getSearchData() {
        return searchData;
    }

    public void setFixedQueryId(final String fixedQueryId) {
        this.fixedQueryId = fixedQueryId;
    }

    public void setResponseObj(final Response responseObj) {
        this.responseObj = responseObj;
    }

    public Response getResponseObj() {
        return responseObj;
    }

    protected String getQueryType() {
        return metaMenuItem.getQueryType();
    }

    protected String getViewType() {
        return metaMenuItem.getQueryKey();
    }

    /**
     * @param currentToolBar
     */
    public void setCurrentToolBarType(final String currentToolBar) {
        metaMenuItem.setCurrentToolBarType(currentToolBar);

    }

    /**
     * @return
     */
    @Override
    public MultipleInstanceWinId getMultipleInstanceWinId() {
        return multiWinId;
    }
}