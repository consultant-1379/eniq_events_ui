/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 ******************************************************************************* */
package com.ericsson.eniq.events.ui.client.common.widget;

import static com.ericsson.eniq.events.common.client.json.MetaDataParserUtils.*;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;
import java.util.ArrayList;
import java.util.List;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.common.client.url.UrlUtils;
import com.ericsson.eniq.events.ui.client.common.*;
import com.ericsson.eniq.events.ui.client.common.comp.*;
import com.ericsson.eniq.events.ui.client.datatype.*;
import com.ericsson.eniq.events.ui.client.datatype.LaunchWinDataType.DrillTargetType;
import com.ericsson.eniq.events.ui.client.events.*;
import com.ericsson.eniq.events.ui.client.events.handlers.ChartGridChangeEventHandler;
import com.ericsson.eniq.events.ui.client.events.handlers.ServerFailedResponseHandler;
import com.ericsson.eniq.events.ui.client.grid.*;
import com.ericsson.eniq.events.ui.client.grid.listeners.*;
import com.ericsson.eniq.events.ui.client.main.*;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.ui.client.wcdmauertt.*;
import com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessageType;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presents a grid with results of the given action Main Pressenter class for grids.
 */
public class EventGridPresenter extends BaseWindowPresenter<IEventGridView> implements HyperLinkCellClickEventHandler,
        HyperLinkCellWinLauncherEventHandler, ButtonEnablingEventHandler {

    private static final String UERTT_TAG = "WCDMA-UERTT";
    private final IMetaReader metaReader = MainEntryPoint.getInjector().getMetaReader();
    private final ServerSuccessResponseHandler gridDataHandler = new ServerSuccessResponseHandler();
    private JSONGrid eventGrid;
    private boolean isWidgetAddedAlready;
    private HyperLinkCellClickListener cellLinkListener;
    private RowSelectListener rowSelectListener = null;
    SearchFieldDataType cachedLastRecievedSearchData = null;
    /*
     * For storage of current parameters passed on hyperlink click (one off into Breadcrumb) - as need them to check button enabing status, (but the
     * getWidgetSpecificParams from BaseWindowPresenter or Breadcrumb will have been lost after successful server call)
     */
    private DrillDownInfoDataType drillInfo;
    private boolean isDrillDownRequestOngoing;
    /* cheap fix for handle success over-writing built up title bar */
    private boolean isDrillDownTitleParamsSet;

    private TimeInfoDataType windowTimeDate;

    /**
     * @param display
     *            view class in MVP pattern
     * @param eventBus
     *            eventBus singleton required for presenters
     */
    public EventGridPresenter(final IEventGridView display, final MultipleInstanceWinId multiWinId, final EventBus eventBus) {
        super(display, multiWinId, eventBus);
        /* needed for every every grid (register so will be removed on grid closure) */
        registerHandler(eventBus.addHandler(SucessResponseEvent.TYPE, gridDataHandler));
        registerHandler(eventBus.addHandler(HyperLinkCellClickEvent.TYPE, this));
        registerHandler(eventBus.addHandler(HyperLinkCellWinLauncherEvent.TYPE, this));
        /*
         * for when view menu item switches from chart to a grid (and we want to get back out again)
         */
        registerHandler(eventBus.addHandler(ChangeChartGridEvent.TYPE, new ChartGridChangeEventHandler(eventBus, this)));
        registerHandler(eventBus.addHandler(ButtonEnablingEvent.TYPE, this));
        bind();
    }

    @Override
    public void initializeWidgit(final String fixedId) {
        /* get the meta data that defines grid layout */
        final GridInfoDataType gridMetaData = getGridInfoFromMetaReader(getFixedQueryId());
        if (metaMenuItem.getWizardId().isEmpty() && gridMetaData != null) {
            // Only display the widget if this widget has no overlay to capture
            // further information
            initializeWidgitWithGridInfo(gridMetaData, false, gridMetaData.gridTitle);
        }
    }

    @Override
    public void initializeWidgitWithGridInfo(final GridInfoDataType gridMetaData, final boolean resetColumns, final String title) {
        /* store the name of grid config id that is been initialised */
        currentWidgetID = getFixedQueryId();
        if ((gridMetaData.gridStateId == null) || gridMetaData.gridStateId.isEmpty()) {
            gridMetaData.gridStateId = currentWidgetID;
        }
        eventGrid = getDisplayedGrid();
        eventGrid.setupStateful(gridMetaData.gridTitle, gridMetaData.categoryId);
        // important this id is unique
        if (resetColumns) {
            eventGrid.resetColumns(gridMetaData);
        } else {
            eventGrid.setColumns(gridMetaData);
        }
        /**
         * Set the window category (Used for docking on taskbar). If we are going to a child (drill) window, use the grid id. if we are going to a
         * primary or parent
         * 
         * window, lets use the meta data id. Because for some reason (I havent time to investigate why) when changing grid type we kill the window
         * and relaunch it thereby losing our context i.e. are we in a drilled window or parent, I need to set the category in
         * AbstractWindowLauncher.launchDrillDownWindow also, and if it
         * 
         * has been set there or in the Navigation Helper, dont set it here, where we may not know the drill context because this object is recreated
         * when changing grid type.
         */
        if (getView().getWindowCategoryId().isEmpty()) {
            getView().setWindowCategoryId(this.drillDepth == 0 ? metaMenuItem.getID() : gridMetaData.gridId);
        }
        /* override the window title to the title of the current widget */
        getView().setWidgetTitle(title != null && !title.isEmpty() ? title : gridMetaData.gridTitle);
        /* ensure only one instance of each listener is allocated to the grid */
        if (rowSelectListener == null) {
            rowSelectListener = new RowSelectListener(eventGrid, getView());
            eventGrid.addListener(Events.RowClick, rowSelectListener);
        }
        eventGrid.replaceRefreshBtnListener(new RefreshGridFromServerListener(getEventBus(), getMultipleInstanceWinId()));
        /*
         * Initialise and add a new cell link listener for this grid instance this is needed as the fixedQueryID can change e.g. user changes from
         * search on APN to BSC
         */
        eventGrid.removeListener(Events.CellClick, cellLinkListener);
        cellLinkListener = new HyperLinkCellClickListener(getMultipleInstanceWinId(), getEventBus(), metaReader);
        eventGrid.addListener(Events.CellClick, cellLinkListener);

        displayingFullFooter();
        if (getView().getViewSettings().getWindowType().equals(MetaMenuItemDataType.Type.RANKING)) {
            eventGrid.setRowsPerPage(20);
        }
    }

    private void displayingFullFooter() {
        if (eventGrid.getBottomToolbar() instanceof GridPagingToolBar) {
            final GridPagingToolBar footToolBar = (GridPagingToolBar) eventGrid.getBottomToolbar();
            footToolBar.displayFullToolbar();
        }
    }

    @Override
    public void cleanUpOnClose() {
        if (eventGrid != null) {
            eventGrid.cleanUpOnClose();
        }
    }

    @Override
    protected String getOutBoundDisplayTypeParameter() {
        return OUT_BOUND_GRID_DISPLAY_PARAM;
    }

    /*
     * gets a handle to the current grid instance in the display and reconfigs the grid with the info pertaining to the requested drilldown
     */
    private String initializeDrillDownWidget(GridInfoDataType gridMetaData, final String drillWinID) {
        drillDownWidgetID = drillWinID;
        /* get the meta data that defines grid layout */
        if (gridMetaData == null) {
            gridMetaData = getGridInfoFromMetaReader(drillWinID);
        }
        gridMetaData.gridStateId = getFixedQueryId();
        initializeWidgitWithGridInfo(gridMetaData, false, gridMetaData.gridTitle);
        /* return the title of the grid for use by the breadcrumb */
        return gridMetaData.gridTitle;
    }

    /*
     * Method extracted for junit test
     */
    JSONGrid getDisplayedGrid() {
        return (JSONGrid) getView().getGridControl();
    }

    // from Navigation update
    @Override
    public void handleButtonEnabling(final String winId, final int rowCount) {
        if (!getFixedQueryId().equals(winId)) {
            return;
        }
        this.handleButtonEnabling(rowCount);
    }

    /*
     * Method used when drill on a chart in dashboard tab and using this when launching new window - followed by automated drilldown in network tab
     * (widget params are not being read from grid row - rather they are known in advance from chart drill(launch)
     * 
     * @param drillDownWindowTypeId Used a comma seperate string for gridDisplayID and this is the second one
     * 
     * @param widgetSpecificParameters - known url parameters, e.g. &eventId=4
     */
    private void handleCellLinkClickWithParams(final String drillDownWindowTypeId, final String widgetSpecificParameters) {
        this.setIsDrillDown(true);
        drillInfo = getDrillDownWindowInfoFromMetaData(drillDownWindowTypeId);
        eventGrid = getDisplayedGrid();
        // positioned here before reset so saving old search data on current window
        handleBreadCrumbOnCellLinkClick();
        this.drillDepth++;
        getView().getWindowState().incrementDrillDepth();
        setWsURL(drillInfo.url);
        final String widgetTitle = initializeDrillDownWidget(null, (EMPTY_STRING.equals(drillInfo.gridDisplayID) ? drillInfo.id
                : drillInfo.gridDisplayID));
        /* update the navigation breadcrumb */
        getInitToolBarHandler().updateToolBarNavigation(widgetTitle, drillInfo);
        /* specific case of drilldown created from dashboard click and assuming search field user when parent is */
        final SearchFieldDataType sData = this.getSearchData();
        if (isSearchFieldDataRequired()) {
            if (sData != null && !sData.isEmpty()) {
                upDateWindowTitleWithDrillDownDataDataIfRequired(sData.searchFieldVal + DASH);
            }
        }
        /* request for data from the server */
        isDrillDownRequestOngoing = true;
        getServerDataForDrillDown(widgetSpecificParameters);
    }

    /**
     * handle the end user invoked click of a hyper link within a cell i.e. a drill down call to the server
     */
    @Override
    public void handleCellLinkClick(final MultipleInstanceWinId multiWinId, final String value, final String drillDownWindowTypeId, final int rowIndex) {
        if (!isThisWindowGuardCheck(multiWinId)) {
            return;
        }
        this.setIsDrillDown(true);
        drillInfo = getDrillDownWindowInfoFromMetaData(drillDownWindowTypeId);
        eventGrid = getDisplayedGrid();
        /* determine the parameters that will be used and the window title bar */
        final DrillURLParamsAndTitleHolder titleAndParams = getDrillURLParamsAndWindowTitle(drillInfo, rowIndex);
        /* If any url param is missing - do not perform drill */
        if (titleAndParams.isMissingParams()) {
            final MessageDialog errorDialog = new MessageDialog();
            errorDialog.show("Error", "Not enough parameters to perform requested action.", MessageDialog.DialogType.ERROR);
            errorDialog.center();
            return;
        }
        // positioned here before reset so saving old search data on current window
        handleBreadCrumbOnCellLinkClick();
        final SearchFieldDataType searchData = buildSearchDataFromHperLinkDetails(rowIndex, drillInfo, getGroupModeFromSearchData());
        if ((searchData != null) && !searchData.isEmpty()) {
            resetSearchData(searchData, false); // false meaning drilling - not
            // resetting toolbar (resetting base window presenter data)
        }
        /* increment the drill down depth */
        this.drillDepth++;
        getView().getWindowState().incrementDrillDepth();
        setSearchFieldUser(drillInfo.needSearchParameter);
        setWsURL(drillInfo.url);
        final String widgetTitle = initializeDrillDownWidget(null, (EMPTY_STRING.equals(drillInfo.gridDisplayID) ? drillInfo.id
                : drillInfo.gridDisplayID));
        final String winTitle = titleAndParams.getParamsTitle();
        final StringBuilder paramBuffer = titleAndParams.getParamsBuffer();
        /* determine if a drilldown param needs to be displayed on the Window Title */
        if (winTitle.length() > 0) {
            // reset the need parameter flag on the base window
            upDateWindowTitleWithDrillDownDataDataIfRequired(winTitle);
        }
        if (drillInfo.isDisablingTime()) {
            getView().getWindowToolbar().disableTimeRangeComp();
        }
        /* update the navigation breadcrumb */
        getInitToolBarHandler().updateToolBarNavigation(widgetTitle, drillInfo);
        /* request for data from the server */
        isDrillDownRequestOngoing = true;
        getServerDataForDrillDown(paramBuffer.toString());
    }

    /* hyperlink click resulting in new window */
    @Override
    public void handleCellLauncherClick(final MultipleInstanceWinId multiWinId, final String value, final String launchID, final int rowIndex) {
        if (!isThisWindowGuardCheck(multiWinId)) {
            return;
        }
        final LaunchWinDataType launchDetails = getLaunchDetailsFromMetaData(launchID);
        final SearchFieldDataType oSearchParms = buildSearchDataFromHperLinkDetails(rowIndex, launchDetails, getGroupModeFromSearchData());
        final SearchFieldDataType searchFieldDataType = getSearchData();
        if (searchFieldDataType != null) {
            oSearchParms.setTitlePostfix(searchFieldDataType.getTitlePostfix());
        }
        multiWinId.setSearchInfo(oSearchParms);
        final IWorkspaceController workspaceController = getView().getWorkspaceController();
        final ContentPanel constrainArea = getView().getParentWindow().getConstraintArea();
        final MetaMenuItem item = getMetaMenuItemFromLaunchDetails(launchDetails.menuItem);
        item.setLaunchedFromCellHyperlink(true);
        item.setWizard(EMPTY_STRING);
        AbstractWindowLauncher launcher = null;
        if (launchDetails.drillTargetType == DrillTargetType.GRID) {
            launcher = new GridLauncher(item, getEventBus(), constrainArea, workspaceController);
        } else {
            launcher = new ChartLauncher(item, getEventBus(), constrainArea, workspaceController);
        }
        launcher.setFixedQueryId(launchDetails.menuItem);
        /*
         * pass the current windows time and created search data to new window being created
         */
        launchWindowFromHyperLink(launcher, oSearchParms, new TimeInfoDataType(this.windowTimeDate));
    }

    /*
     * determine the parameters that will be used in outbound call and the window title bar when drill on hyper link
     */
    private DrillURLParamsAndTitleHolder getDrillURLParamsAndWindowTitle(final DrillDownInfoDataType drillInfo, final int rowIndex) {
        boolean missingDrillParams = false;
        /* determine the parameters that will be used on the window title bar */
        final StringBuilder paramsTitle = new StringBuilder();
        /* build the query string for the url */
        final StringBuilder paramBuffer = new StringBuilder();
        isDrillDownTitleParamsSet = false;
        for (final DrillDownParameterInfoDataType queryParameter : drillInfo.queryParameters) {
            final String parameterColID = queryParameter.parameterValue;
            String paramVal = queryParameter.parameterValue;
            if (!queryParameter.isFixedType) {
                /* Grouping Grids have different mechanism to get the cell value */
                if (eventGrid.getGridType().equals(GRID_GROUPING_VIEW)) {
                    paramVal = getView().getGroupingGridCellValue(rowIndex, parameterColID);
                } else {
                    paramVal = getView().getGridCellValue(rowIndex, parameterColID);
                }
            }
            final String parameterName = queryParameter.parameterName;
            // determine if the parameter needs to be added to the query url & ensure no null values
            if ((parameterName != null) && !parameterName.isEmpty()) {
                //only highlight missing params if the value for the param is missing
                if (isValidParamValue(paramVal)) {
                    paramBuffer.append(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR);
                    paramBuffer.append(parameterName);
                    paramBuffer.append(EQUAL_STRING);
                    paramBuffer.append(UrlUtils.checkForAndRemoveAmpersand(paramVal));
                } else {
                    missingDrillParams = true;
                }
            }
            /* determine if this parameter needs to be displayed on the Window Title */
            if (queryParameter.isTitleParam) {
                paramsTitle.append(paramVal);
                paramsTitle.append(DASH);
                isDrillDownTitleParamsSet = true;
            }
        }
        final DrillURLParamsAndTitleHolder drillParams = new DrillURLParamsAndTitleHolder(paramsTitle, paramBuffer);
        drillParams.setIsMissingParams(missingDrillParams);
        return drillParams;
    }

    /* Check to see if there is any empty value in the param value (null) - however null is /0 character and must be checked in this way */
    private boolean isValidParamValue(final String paramValue) {
        /* must consider this case */
        if (paramValue.trim().length() == 1 && Character.codePointAt(paramValue, 0) == 160) {
            return false;
        }
        /* All other string cases */
        if (paramValue == "" || paramValue == null || paramValue.isEmpty()) {
            return false;
        }
        return true;
    }

    /* get the type of view for the window as either summary or raw events view */
    private IHyperLinkDataType getWidgetSpecificInfoUsingBreadCrumb() {
        final Menu breadCrumbMenu = getNavigationMenu();
        final BreadCrumbMenuItem item = (breadCrumbMenu != null) ? (BreadCrumbMenuItem) getComponent(breadCrumbMenu) : null;
        if (item != null) {
            return item.getWidgetSpecificInfo();
        }
        return null;
    }

    /*
     * Gets the BreadCrumbMenuItem that represents the current grid displayed and saves the grid data and configurations to the BreadCrumbMenuItem
     */
    private void handleBreadCrumbOnCellLinkClick() {
        final Menu breadCrumbMenu = getNavigationMenu();
        final BreadCrumbMenuItem item = (breadCrumbMenu != null) ? (BreadCrumbMenuItem) getComponent(breadCrumbMenu) : null;
        if (item != null) {
            final BreadCrumbMenuItem currentBreadCrumb = item;
            final String sTitle = getView().getBaseWindowTitle();
            // Update the window URL info if in case View menu items are selected
            // after the window launched for the
            // first time, mainly for Terminal Analysis view menu items. Fix for TR
            // HN28493
            currentBreadCrumb.setURL(this.getWsURL());
            final List<Filter> filters = eventGrid.getFilters().getFilterData();
            currentBreadCrumb.saveGridConfigurations(eventGrid.getColumns(), eventGrid.getData(), filters, sTitle, new TimeInfoDataType(currentBreadCrumb.getTimeData()/*this.windowTimeDate*/), getView().getLastRefreshTimeStamp(), getSearchData(), eventGrid.getStateId());
            eventGrid.saveState();
            breadCrumbMenu.insert(currentBreadCrumb, currentBreadCrumb.getIndex());
            breadCrumbMenu.insert(currentBreadCrumb, currentBreadCrumb.getIndex());
            // store the identity of hidden column
            currentBreadCrumb.saveHiddenColumns(eventGrid.getColumnModel().getColumns());
            /*
             * Remove all subsequent MenuItems from Breadcrumb as the end user has clicked a hyperlink
             */
            for (int x = currentBreadCrumb.getIndex() + 1; x < breadCrumbMenu.getItems().size(); x++) {
                breadCrumbMenu.remove(breadCrumbMenu.getItem(x));
                // account for item removed
                x--;
            }
            // the Forward Button must be disabled
            getView().setToolbarButtonEnabled(BTN_FORWARD, false);
        }
    }

    private Menu getNavigationMenu() {
        final BaseToolBar winToolBar = getView().getWindowToolbar();
        final Button btnBreadCrumb = getNavButton(winToolBar);
        return (btnBreadCrumb != null) ? btnBreadCrumb.getMenu() : null;
    }

    /* extracted for junit */
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
    Button getNavButton(final BaseToolBar winToolBar) {
        return winToolBar.getButtonByItemId(BTN_NAV);
    }

    /* extracted for junit */
    Button getButtonById(final BaseToolBar winToolBar, final String buttonId) {
        return winToolBar.getButtonByItemId(buttonId);
    }

    /*
     * extracted for JUNIT
     */
    void getServerDataForDrillDown(final String drillDownParams) {
        setDrilledDownScreen(true);
        final String queryParameters = getInternalRequestData() + drillDownParams;
        makeServerRequestForData(queryParameters);
    }

    /*
     * gets the information that details the drill down screen requested by the end user (split like this to enable JUNIT)
     */
    DrillDownInfoDataType getDrillDownWindowInfoFromMetaData(final String drillWinTypeID) {
        return MainEntryPoint.getInjector().getMetaReader().getDrillDownWindowType(drillWinTypeID);
    }

    JSONValue parseText(final String s) {
        return JSONUtils.parse(s);
    }

    @Override
    public int handleSuccessResponse(final Response response) {
        isDrillDownRequestOngoing = false; // in case parse fails
        EventGridPresenter.this.setResponseObj(response);
        JSONValue responseValue;
        final AbstractBaseWindowDisplay display = (AbstractBaseWindowDisplay) getView();
        display.hideErrorMessage();
        try {
            responseValue = parseText(response.getText());
        } catch (final JSONException e) {
            if (response.getText().contains(CommonConstants.LOGIN)) {
                display.showErrorMessage(ComponentMessageType.ERROR, A_PROBLEM_OCCURRED, LOGIN_AGAIN);  //we got a response but was html page!
            } else {
                display.showErrorMessage(ComponentMessageType.ERROR, A_PROBLEM_OCCURRED, PARSE_ERROR);  //we got a response but not json!
            }
            return 0;
        }
        return handleSuccessResponseWithJSONValue(response, responseValue, null);
    }


    /**
     * Get the dateTime local to the current grid. This time is equal to the time displayed in the
     * bottom right corner of the window.
     * @return local timeDate (timeDate returned from services after offset.)
     */
    public TimeInfoDataType getWindowTimeDate() {
        return windowTimeDate;
    }

    public void setWindowTimeDate(TimeInfoDataType timeInfoDataType){
        this.windowTimeDate = new TimeInfoDataType(timeInfoDataType);
    }


    @Override
    public int handleSuccessResponseWithJSONValue(final Response response, final JSONValue responseValue, final List<Filter> filter) {
        if (isUerttResponse(responseValue)) {
            EventGridViewUertt eventGridViewUertt = new EventGridViewUertt(getView(),eventGrid);
            EventGridPresenterUertt eventGridPresenterUertt = new EventGridPresenterUertt(eventGridViewUertt,getEventBus());
            eventGridPresenterUertt.handleUerttResponse(responseValue);

        }
        isDrillDownRequestOngoing = false;
        if ((responseValue != null) && JSONUtils.checkData(responseValue, getEventBus(), getMultipleInstanceWinId())) {
            delayUpdateOfWindowTitleUntilDataIsPresent();
            final int rowCount = initialiseAndBindGridData(responseValue);
            furtherDrilldownToHappenFromThisGrid();

            this.windowTimeDate = new TimeInfoDataType(getTimeData());

            return rowCount;
        }
        handleButtonEnabling(0);
        return 0;
    }


    private boolean isUerttResponse(final JSONValue responseValue) {
        if (!responseValue.toString().isEmpty()) {
            return checkForUertt(responseValue);
        }
        return false;
    }

    private boolean checkForUertt(final JSONValue responseValue) {
        if (responseValue.isObject().containsKey("Feature")) {
            final String feature = responseValue.isObject().get("Feature").toString();
            if (removeDoubleQuotes(feature).toString().equals(UERTT_TAG)) {
                return true;
            }
        }
        return false;
    }

    private String removeDoubleQuotes(final String string) {
        return string.substring(1, string.length() - 1);
    }

    private void delayUpdateOfWindowTitleUntilDataIsPresent() {
        if (!isDrillDownTitleParamsSet && !getView().getWindowState().isDrillDown()) {
            // isDrillDownTitleParamsSet is false, drillDownWidgetID = null, and drillDepth is 0 when
            // the window is drilled down: e.g. Subscriber [tab] > select IMSI > enter IMSI e.g. 310410000004327 >
            // Launch [menu] > Event Analysis -> Core -> drill down to a SGSN-MME value (e.g. 'MME1')
            upDateWindowTitleWithSearchDataIfRequired();
        }
    }

    private int initialiseAndBindGridData(final JSONValue responseValue) {
        eventGrid.setData(responseValue);
        eventGrid.setWindowType(metaMenuItem.getWindowType());
        eventGrid.bind();
        if (!isWidgetAddedAlready && !isUerttResponse(responseValue)) { // toggle scenario
            /* add the grid to the display widget (grid will repaint itself) */
            getView().addWidget(eventGrid.asWidget());
            isWidgetAddedAlready = true;
        }
        final int rowCount = eventGrid.getGridRowCount();
        handleButtonEnabling(rowCount);
        return rowCount;
    }

    private void furtherDrilldownToHappenFromThisGrid() {
        final String forLaterWidgetSpecificParams = getMetaMenuItem().getForLaterWidgetSpecificParams();
        if (!forLaterWidgetSpecificParams.isEmpty()) {
            this.handleCellLinkClickWithParams(getMetaMenuItem().getForLaterInfoWithURL(), forLaterWidgetSpecificParams);
            // clear (avoid recursion when back in success from second call)
            getMetaMenuItem().setForLaterWidgetSpecificParams(EMPTY_STRING, EMPTY_STRING);
        }
    }

    @Override
    public void cleanUpWindowForCancelRequest() {
        EventGridPresenter.this.setWidgetSpecificParams(EMPTY_STRING);
        handleCellLinkClickCanceled();
    }

    @Override
    public void cleanUpBreadCrumbMenu() {
        final Button btnBreadCrumb = getNavButton(getView().getWindowToolbar());
        if (btnBreadCrumb != null) {
            final Menu breadCrumbMenu = btnBreadCrumb.getMenu();
            for (int i = 0; i < breadCrumbMenu.getItemCount(); i++) {
                final Component menuItem = breadCrumbMenu.getItem(i);
                if (menuItem instanceof BreadCrumbMenuItem) {
                    final BreadCrumbMenuItem item = (BreadCrumbMenuItem) menuItem;
                    if (item.isGridDisplayed()) {
                        if (item.getIndex() != (breadCrumbMenu.getItemCount() - 1)) {
                            for (int k = i + 1; k < breadCrumbMenu.getItemCount(); k++) {
                                getNavButton(getView().getWindowToolbar()).getMenu().remove(breadCrumbMenu.getItem(k));
                            }
                            getView().setToolbarButtonEnabled(BTN_FORWARD, false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void clearCurrentBreadCrumbMenuItem() {
        final BreadCrumbMenuItem breadCrumb = getCurrentBreadCrumbMenuItem();
        if (breadCrumb != null) {
            breadCrumb.setWidgetURLParameters(EMPTY_STRING, this.getSearchData());
            /*
             * clear the paramsMap too to reload it with the window being launched now. This is necessary in case Terminal View menu option is clicked
             * 
             * instead of Back button after a drilldown
             */
            breadCrumb.getParametersMap().clear();
        }
    }

    @Override
    public BreadCrumbMenuItem getCurrentBreadCrumbMenuItem() {
        final Button btnBreadCrumb = getNavButton(getView().getWindowToolbar());
        // removing "hack" for btnBreadCrumb != null && !btnBreadCrumb.isVisible())
        // (will still use breadcrumb finctionlaity for that case)
        BreadCrumbMenuItem item = null;
        final Menu menu = (btnBreadCrumb != null) ? btnBreadCrumb.getMenu() : null;
        if (menu != null) {
            for (int i = 0; i < menu.getItemCount(); i++) {
                if (((BreadCrumbMenuItem) menu.getItem(i)).isGridDisplayed()) {
                    item = (BreadCrumbMenuItem) menu.getItem(i);
                    break;
                }
            }
        }
        return item; // can be null
    }

    @Override
    public ButtonEnableParametersDataType getButtonEnableParameters(final int rowCount) {
        final ButtonEnableParametersDataType params = new ButtonEnableParametersDataType();
        params.rowCount = rowCount;
        params.searchData = getSearchData(); // not the menu task bar one - one
        // owned by window
        params.hasSearchFieldChanged = hasSearchFieldChanged();
        params.isRowSelected = (eventGrid == null) ? false : (!eventGrid.getSelectionModel().getSelection().isEmpty());
        params.widgetSpecificInfo = getWidgetSpecificInfoUsingBreadCrumb();
        params.columnsMetaData = getView().getColumns();
        return params;
    }

    /*
     * not the menu task bar one - this is taken from search data currently owned by the window We can trust the group mode status of current window
     * search data will not have changed when building new search data for drilling, etc (menu taskbar could)
     * 
     * @return group mode from current window search data, true if group mode
     */
    private boolean getGroupModeFromSearchData() {
        final SearchFieldDataType searchData = getSearchData();
        if ((searchData == null) || searchData.isEmpty()) {
            return false;
        }
        return searchData.isGroupMode();
    }

    /*
     * hack because you only want the KPI buttonns to be enabled when receive data for a new node, (but can cause a server update without changing the
     * node by
     * 
     * changing time param)
     */
    private boolean hasSearchFieldChanged() {
        if ((cachedLastRecievedSearchData == null) || (EventGridPresenter.this.getSearchData() == null)) {
            return true;
        }
        return (!EventGridPresenter.this.getSearchData().equals(cachedLastRecievedSearchData));
    }

    /*
     * Handle when have pressed cancel on a call that was drilling down to a new grid. The new grid and navigation change has been created prior to
     * sending
     * 
     * #handleCellLinkClick call. So now leaving that code but removing the trial after cancel button detected (only for case of when drilling down -
     * not for changing time etc)
     */
    private void handleCellLinkClickCanceled() {
        if (isDrillDownRequestOngoing) {
            isDrillDownRequestOngoing = false;
            getDrillDownNavigator().handleBack();
            this.drillDepth--;
            getView().getWindowState().decrementDrillDepth();
            cleanUpBreadCrumbMenu();
        }
    }

    /*
     * @return a new instance of DrillDownNavPresenter
     */
    private NavigationHelper getDrillDownNavigator() {
        return new NavigationHelper(getView(), getEventBus());
    }

    /**
     * Handle success response when event bus fired a SucessResponseEvent Indicating server has returned with some success result
     */
    private final class ServerSuccessResponseHandler implements SucessResponseEventHandler {
        @Override
        public void handleResponse(final MultipleInstanceWinId multiWinId, final String requestData, final Response response) {
            if (!isThisWindowGuardCheck(multiWinId)) {
                return;
            }
            if (!saveJSONResponseTimeRangeValues(response)) {
                getView().stopProcessing();
                return;
            }
            /* oh dear - don't carry these to next call */
            setWidgetSpecificParams(EMPTY_STRING);
            getView().stopProcessing(); // unmask
            final boolean isGroupToGridChange = ((eventGrid.getView() instanceof GroupingView) && GRID_VIEW.equals(eventGrid.getColumns().gridType));
            final boolean isGridToGroupChange = (!(eventGrid.getView() instanceof GroupingView) && GRID_GROUPING_VIEW
                    .equals(eventGrid.getColumns().gridType));
            final boolean isViewToColumnViewChange = GRID_COLUMN_VIEW.equals(eventGrid.getColumns().gridType);
            final boolean isColumnViewToViewChange = GRID_VIEW.equals(eventGrid.getColumns().gridType)
                    && (eventGrid.getView() instanceof GridColumnView);
            /*
             * Determine if changing from a Grouping Grid to Normal Grid or Vice Versa. If so need to fire changeGridViewEvent
             */
            if (isGroupToGridChange || isGridToGroupChange) {
                final String windowTitle = getView().getBaseWindowTitle();
                final Button btnBreadCrumb = getNavButton(getView().getWindowToolbar());
                // Works on assumption that all grid have filter columns
                final List<Filter> filters = eventGrid.getFilters().getFilterData();
                getEventBus().fireEvent(
                        new ChangeGridViewEvent(getMultipleInstanceWinId(), windowTitle, eventGrid.getColumns(), response, null, filters,
                                btnBreadCrumb.getMenu(), EventGridPresenter.this.getWsURL(), EventGridPresenter.this.getTimeData(), false,
                                drillDepth > 0));
            } else if (isViewToColumnViewChange || isColumnViewToViewChange) {
                eventGrid.upDateLastRefreshedLabel(response);
                final String windowTitle = getView().getBaseWindowTitle();
                final Button btnBreadCrumb = getNavButton(getView().getWindowToolbar());
                List<Filter> filters = null;
                if (eventGrid.getFilters() != null) {
                    filters = eventGrid.getFilters().getFilterData();
                }
                final Menu breadCrumbMenu = (btnBreadCrumb == null) ? null : btnBreadCrumb.getMenu();
                // adding toggled state handling here for the same reason that the main
                // #initWindow in
                // BaseWindowPresenter
                // had it (because MetaMenuItem is preserving state for the upper
                // toolbar (to give a temp toolbar when
                // drilldown on a chart
                final boolean isTogglingFromGraph = EventGridPresenter.this.isWindowInToggledState();
                getEventBus().fireEvent(
                        new ChangeGridViewEvent(getMultipleInstanceWinId(), windowTitle, eventGrid.getColumns(), response, null, filters,
                                breadCrumbMenu, EventGridPresenter.this.getWsURL(), EventGridPresenter.this.getTimeData(), isTogglingFromGraph,
                                drillDepth > 0));
            } else {
                eventGrid.upDateLastRefreshedLabel(response); // only at server call (not
                // toggle- or breadcrumb change)
                final int rowCount = EventGridPresenter.this.handleSuccessResponse(response);
                // would (potentially) only want this for real server call (not when
                // reusing a response object for a
                // toggle)
                // don't leave KPI disabled for same search field data when user updates
                // time (to have data) for example
                cachedLastRecievedSearchData = (rowCount > 0) ? EventGridPresenter.this.getSearchData() : null;
                handleButtonEnabling(rowCount);
            }
        }

        /*
         * Retrieves the time range params in the json data reponse sent back from the server and stores them in the breadcrumb, presenter and display
         * to
         * 
         * keep track of the time range of the data rendered to the grid
         */
        private boolean saveJSONResponseTimeRangeValues(final Response response) {
            if (response.getText().length() > 0) {
                JsonObjectWrapper data;
                try {
                    data = new JsonObjectWrapper(JSONUtils.parse(response.getText()).isObject());
                } catch (final JSONException e) {

                    if (response.getText().contains(/*LOGIN*/ "Login")) {
                        ((AbstractBaseWindowDisplay) getView()).showErrorMessage(ComponentMessageType.ERROR, A_PROBLEM_OCCURRED, LOGIN_AGAIN);  //we got a response but was html page!
                    } else {
                        ((AbstractBaseWindowDisplay) getView()).showErrorMessage(ComponentMessageType.ERROR, A_PROBLEM_OCCURRED, PARSE_ERROR);  //we got a response but not json!
                    }

                    return false;
                }
                if(Boolean.parseBoolean(data.getString(CommonConstants.SUCCESS))){
                    final String dataTimeFrom = data.getString(DATA_TIME_FROM_PARMA_JSON_RESPONSE);
                    final String dataTimeTo = data.getString(DATA_TIME_TO_PARMA_JSON_RESPONSE);
                    final String timeZone = data.getString(DATA_TIMEZONE_PARMA_JSON_RESPONSE);
                    // Update the time information with the data returned from services
                    final TimeInfoDataType timeData = getTimeData();
                    if (timeData != null) { // e.g. cause code table
                        timeData.dataTimeFrom = dataTimeFrom;
                        timeData.dataTimeTo = dataTimeTo;
                        timeData.timeZone = timeZone;
                        getView().updateTime(timeData);
                    }
                    final Menu breadCrumbMenu = getNavigationMenu();
                    final BreadCrumbMenuItem currentBreadCrumbMenuItem = (breadCrumbMenu != null) ? (BreadCrumbMenuItem) getComponent(breadCrumbMenu)
                            : null;
                    if (currentBreadCrumbMenuItem != null) {
                        currentBreadCrumbMenuItem.dataTimeFrom = data.getString(DATA_TIME_FROM_PARMA_JSON_RESPONSE);
                        currentBreadCrumbMenuItem.dataTimeTo = data.getString(DATA_TIME_TO_PARMA_JSON_RESPONSE);
                        currentBreadCrumbMenuItem.timeZone = data.getString(DATA_TIMEZONE_PARMA_JSON_RESPONSE);
                    }
                }
            }
            return true;
        }
    }
    /*
     * build up search data for hyperlink presss launching new window
     * 
     * Fixed type, e.g. "type=BSC" - BSC is fixed from metadata, as opposed to content in the cell row
     * 
     * 
     * 
     * @param isGroupMode for search data (note it is NEVER really appropriate to use menutask bar information, display.getMenuTaskBar().isGroupMode()
     * when
     * 
     * checking a window in case user not pressed play button yet (if current type field nothing to do with this window)
     */
    @SuppressWarnings("null")
    private SearchFieldDataType buildSearchDataFromHperLinkDetails(final int rowIndex, final IHyperLinkDataType launchDetails,
                                                                   final boolean isGroupMode) {
        final DrillDownParameterInfoDataType[] drillParams = launchDetails.getParams();
        // TODO this is more than search data (e.g. could be adding Key=SUM into
        // search data URL param (which is
        // needed in the URL parameters alright - but in some cases only (and
        // nothing to so with search field
        // component))
        final String[] searchParms = new String[(drillParams == null ? 0 : drillParams.length)];
        for (int x = 0; x < searchParms.length; x++) {
            final StringBuilder valBuffer = new StringBuilder(); // NOPMD by eendmcm
            // NOPMD by eendmcm
            valBuffer.append(drillParams[x].parameterName);
            valBuffer.append(EQUAL_STRING);
            if (drillParams[x].isFixedType) {
                valBuffer.append(drillParams[x].parameterValue);
            } else {
                final String columnId = drillParams[x].parameterValue;
                valBuffer.append(getView().getGridCellValue(rowIndex, columnId));
            }
            searchParms[x] = valBuffer.toString();
        }
        // Get the Value for Title Bar and search data
        try {
            // Get the Value(s) for Title Bar
            final List<String> columnIdTitleList = new ArrayList<String>();
            for (final String index : launchDetails.getSearchValColumn().split(",")) {
                if (!index.isEmpty()) {
                    columnIdTitleList.add(index.trim());
                }
            }
            final StringBuilder title = new StringBuilder();
            for (final String columnId : columnIdTitleList) {
                title.append(title.length() == 0 ? "" : ", ");
                title.append(getView().getGridCellValue(rowIndex, columnId));
            }
            /* build up the search parameters datatype */
            return new SearchFieldDataType(title.toString().trim(), searchParms, launchDetails.getType(), null, isGroupMode, EMPTY_STRING, null,
                    false);
        } catch (final NumberFormatException e) {
            return new SearchFieldDataType(null, null, null, null, isGroupMode, EMPTY_STRING, null, false);
        }
    }

    @Override
    public ServerFailedResponseHandler getServerFailedResponseHandler() {
        return new ServerFailedResponseHandler(getMultipleInstanceWinId(), getView(), EventGridPresenter.this.getMetaMenuItem()) {
            @Override
            protected void completeRendering() {
                isDrillDownRequestOngoing = false;
                EventGridPresenter.this.handleButtonEnabling(0);
                eventGrid.setData(JSONUtils.parse("{}"));
                eventGrid.bind();
                getView().addWidget(eventGrid.asWidget());
            }
        };
    }

    /* holder class in effort to reduce cyclometric complexity */
    private final class DrillURLParamsAndTitleHolder {
        private boolean isMissingParams = false;
        private final String paramsTitle;
        private final StringBuilder paramsBuffer;

        /**
         * Private holder class for drilldown information
         * 
         * 
         * 
         * @param paramsTitle
         *            hold title header to go on drilled down window
         * 
         * @param paramsBuffer
         *            hold sequence of extra drilldown paramters to add to url
         */
        public DrillURLParamsAndTitleHolder(final StringBuilder paramsTitle, final StringBuilder paramsBuffer) {
            this.paramsTitle = paramsTitle.toString();
            this.paramsBuffer = paramsBuffer;
        }

        public String getParamsTitle() {
            return paramsTitle;
        }

        public StringBuilder getParamsBuffer() {
            return paramsBuffer;
        }

        private boolean isMissingParams() {
            return this.isMissingParams;
        }

        private void setIsMissingParams(final boolean isMissingParams) {
            this.isMissingParams = isMissingParams;
        }
    }

    /* extract for junit */
    LaunchWinDataType getLaunchDetailsFromMetaData(final String launchID) {
        return metaReader.getLaunchWinFromHyperLink(launchID);
    }

    /* extract for junit */
    MetaMenuItem getMetaMenuItemFromLaunchDetails(final String launchDetailsMenuItem) {
        return MainEntryPoint.getInjector().getMetaReader().getMetaMenuItemFromID(launchDetailsMenuItem);
    }

    /* extract for junit */
    void launchWindowFromHyperLink(final AbstractWindowLauncher launcher, final SearchFieldDataType searchVal, final TimeInfoDataType initialTime) {
        launcher.launchWindowWithPresetSearchData(searchVal, initialTime, false);
    }
}