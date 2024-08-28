/*
 * -----------------------------------------------------------------------
 *      Copyright (C) ${YEAR} LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.dashboard;



import static com.ericsson.eniq.events.ui.client.dashboard.DashboardView.*;
import static com.ericsson.eniq.events.ui.client.common.Constants.LICENSE_ERROR;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.ericsson.eniq.events.common.client.PerformanceUtil;
import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.ericsson.eniq.events.ui.client.common.service.DashboardManager;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.HasPortletId;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.MapPortletWindow;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.MapPresenter;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.PlaceHolder;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.PortletWindow;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.message.PortletMessages;
import com.ericsson.eniq.events.ui.client.dashboard.threshold.ThresholdsPresenter;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.DashBoardDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletType;
import com.ericsson.eniq.events.ui.client.events.FailedEvent;
import com.ericsson.eniq.events.ui.client.events.FailedEventHandler;
import com.ericsson.eniq.events.ui.client.events.MaskEvent;
import com.ericsson.eniq.events.ui.client.events.SearchFieldTypeChangeEvent;
import com.ericsson.eniq.events.ui.client.events.SearchFieldTypeChangeEventHandler;
import com.ericsson.eniq.events.ui.client.events.SearchFieldValueResetEvent;
import com.ericsson.eniq.events.ui.client.events.SearchFieldValueResetEventHandler;
import com.ericsson.eniq.events.ui.client.events.SucessResponseEvent;
import com.ericsson.eniq.events.ui.client.events.SucessResponseEventHandler;
import com.ericsson.eniq.events.ui.client.events.TimeParameterValueChangeEvent;
import com.ericsson.eniq.events.ui.client.events.TimeParameterValueChangeEventHandler;
import com.ericsson.eniq.events.ui.client.events.component.ComponentMessageEvent;
import com.ericsson.eniq.events.ui.client.events.component.ComponentMessageEventHandler;
import com.ericsson.eniq.events.ui.client.events.dashboard.MapMaskEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.MapMaskEventHandler;
import com.ericsson.eniq.events.ui.client.events.dashboard.MapMaximizeEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.MapMaximizeEventHandler;
import com.ericsson.eniq.events.ui.client.events.dashboard.MapResizeEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.MapRestoreEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.MapRestoreEventHandler;
import com.ericsson.eniq.events.ui.client.events.dashboard.MapUnMaskEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.MapUnMaskEventHandler;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletAddEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletAddEventHandler;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletMaskEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletMaskEventHandler;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletMoveEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletMoveEventHandler;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletRefreshEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletRemoveEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletRemoveEventHandler;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletResizeEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletResizeEventHandler;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletUnMaskEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletUnMaskEventHandler;
import com.ericsson.eniq.events.ui.client.main.IGenericTabView;
import com.ericsson.eniq.events.ui.client.main.TabViewRegistry;
import com.ericsson.eniq.events.ui.client.resources.EniqResourceBundle;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessagePanel;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessageType;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.RequestTimeoutException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Main class for handling DashboardView.
 * All communication with services will go through this presenter.
 *
 * @author evyagrz
 * @since Oct 2011
 */
public class DashboardPresenter extends BasePresenter<DashboardView> implements SearchFieldValueResetEventHandler,
        PortletAddEventHandler, PortletMoveEventHandler, PortletRemoveEventHandler, ComponentMessageEventHandler,
        PortletMaskEventHandler, PortletUnMaskEventHandler, PortletResizeEventHandler, MapMaskEventHandler,
        MapUnMaskEventHandler, MapMaximizeEventHandler, MapRestoreEventHandler, TimeParameterValueChangeEventHandler,
        SearchFieldTypeChangeEventHandler, SucessResponseEventHandler, FailedEventHandler {

    private final EventBus eventBus;

    private final EniqResourceBundle eniqResourceBundle;

    private final DashboardManager dashboardManager;

    private final PortletTemplateRegistry portletTemplateRegistry;

    private final ThresholdsPresenter thresholdsPresenter;

    private final Map<String, PortletTemplate> templateMap = new HashMap<String, PortletTemplate>();

    private String tabOwnerId;

    private String dashboardWinId;

    private IDashboardTaskbarHelper taskbarHelper;

    @Inject
    public DashboardPresenter(final EventBus eventBus, final EniqResourceBundle eniqResourceBundle,
            final DashboardView view, final DashboardManager dashboardManager,
            final PortletTemplateRegistry portletTemplateRegistry, final ThresholdsPresenter thresholdPresenter) {
        super(view, eventBus);
        PerformanceUtil.getSharedInstance().logCurrentTime("dashboard Presenter instantiating...");
        this.eventBus = eventBus;
        this.eniqResourceBundle = eniqResourceBundle;
        this.dashboardManager = dashboardManager;
        this.portletTemplateRegistry = portletTemplateRegistry;
        this.thresholdsPresenter = thresholdPresenter;
        bind();
    }

    private void addEventBusHandlers() {

        eventBus.addHandler(SearchFieldValueResetEvent.TYPE, this);
        eventBus.addHandler(TimeParameterValueChangeEvent.TYPE, this);
        /* type change to "Network" (summary) */
        eventBus.addHandler(SearchFieldTypeChangeEvent.TYPE, this);

        eventBus.addHandler(PortletAddEvent.TYPE, this);
        eventBus.addHandler(PortletMoveEvent.TYPE, this);
        eventBus.addHandler(PortletRemoveEvent.TYPE, this);
        eventBus.addHandler(ComponentMessageEvent.TYPE, this);

        // Mask/UnMask events for portlets
        eventBus.addHandler(PortletMaskEvent.TYPE, this);
        eventBus.addHandler(PortletUnMaskEvent.TYPE, this);

        eventBus.addHandler(SucessResponseEvent.TYPE, this);
        eventBus.addHandler(FailedEvent.TYPE, this);

        // Map specific events
        eventBus.addHandler(PortletResizeEvent.TYPE, this);
        eventBus.addHandler(MapMaskEvent.TYPE, this);
        eventBus.addHandler(MapUnMaskEvent.TYPE, this);
        eventBus.addHandler(MapMaximizeEvent.TYPE, this);
        eventBus.addHandler(MapRestoreEvent.TYPE, this);
    }

    /**
     * Load the view with data from JsonObjectWrapper for tabId.
     * Provide taskbar information to view for "read" case (reopening windows)
     *
     * @param taskbarHelper - Helper for taskbar contains tabId needs to initially populate
     *                      new portlets using current time data (and search data)
     * @param dashBoardData - data to launch with
     */
    public void loadDashboardData(final IDashboardTaskbarHelper taskbarHelper, final DashBoardDataType dashBoardData) {
        final DashboardView view = getView();
        final String tabOwnerId = dashBoardData.getTabOwnerId();
        final String winId = dashBoardData.getWinId();

        // Set taskbar (for reading), for window re-opening scenarios (when will have missed notifications)
        setTaskBarHelper(taskbarHelper);
        setEventHandlingIds(tabOwnerId, winId);
        dashboardManager.clearDashboardPortlets();
        dashboardManager.setSearchField(getCurrentSearchData());
        dashboardManager.restoreDashboardLayout(dashBoardData);
        view.update(dashBoardData);
        view.updateTitleBarText(getCurrentTimeData());

        // Create and add portlets
        final List<PortletDataType> portalInfos = dashBoardData.getPortals();
        createPortlets(portalInfos);
        getView().markEmptyColumn();
    }

    @Override
    protected void onBind() {
        // Subscribe for events here
        addEventBusHandlers();
    }

    private void createPortlets(final List<PortletDataType> portalInfos) {
        if (dashboardManager.isStateRestored()) {
            for (final PortletDataType porletData : portalInfos) {
                addPortlet(porletData);
            }
        } else {
            buildDefaultPortletLayout(portalInfos);
        }

        if (hasMapPortlet(portalInfos)) {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    alignMapPortlet();
                }
            });

        }
    }

    void buildDefaultPortletLayout(final List<PortletDataType> portalInfos) {
        int col = 0;
        int row = 0;
        for (final PortletDataType porletData : portalInfos) {
            final int rowIndex = porletData.getRowIndex();
            final int columnIndex = porletData.getColumnIndex();

            if (rowIndex == 0 && columnIndex == 0) {
                porletData.setRowIndex(row);
                porletData.setColumnIndex(col);
            }

            addPortlet(porletData);

            if (col >= (MAX_PORTAL_COLUMNS - 1)) {
                row++;
                col = 0;
            } else {
                col++;
            }
        }
    }

    /**
     * Effectively can b e used to check if map is a PlaceHolder
     * also (i.e. in place of checking mapPanel.getWidget() instanceof PlaceHolder)
     *
     * @return null if a PlaceHolder - else the Map window
     */
    public MapPortletWindow getMapWindow() {
        final SimplePanel mapPanel = getView().getMapPanel();
        final Widget mapWigit = mapPanel.getWidget();
        if (mapWigit instanceof MapPortletWindow) {
            return (MapPortletWindow) mapPanel.getWidget();
        }
        return null; // e.g. its a PlaceHolder
    }

    private void alignMapPortlet() {
        final DashboardView view = getView();
        final MapPortletWindow mapPortletWindow = getMapWindow();

        if (mapPortletWindow != null) {
            final AbsolutePanel dashboardContainer = view.getDashboardContainer();
            final FlowPanel portletPanel = view.getPortletPanel();

            final int absoluteTop = dashboardContainer.getAbsoluteTop();
            final int offsetHeight = portletPanel.getOffsetHeight();
            mapPortletWindow.calculateMapHeight(absoluteTop, offsetHeight);

            // Force Map itself to redraw as well
            eventBus.fireEvent(new MapResizeEvent());
        }
    }

    private boolean hasMapPortlet(final List<PortletDataType> portlets) {
        boolean hasMap = false;
        for (final PortletDataType portlet : portlets) {
            if (PortletType.MAP.equals(portlet.getType())) {
                hasMap = true;
            }
        }
        return hasMap;
    }

    /**
     * Add IDs to use for external event handling, i.e for guards
     * (for search field (time field) events)
     *
     * @param tabOwnerId     - tab owner id from meta data
     * @param dashboardWinId - dashboard id from meta data
     */
    public void setEventHandlingIds(final String tabOwnerId, final String dashboardWinId) {
        this.tabOwnerId = tabOwnerId;
        this.dashboardWinId = dashboardWinId;
    }

    /**
     * Set task-bar (for reading), for window re-opening scenarios (when will have missed notifications)
     *
     * @param taskbarHelper taskbarHelper utility for search component and time component (reading)
     */
    public void setTaskBarHelper(final IDashboardTaskbarHelper taskbarHelper) {
        this.taskbarHelper = taskbarHelper;
    }

    private void addPortlet(final PortletDataType portletData) {
        SearchFieldDataType searchData;
        // all search field user porlets will want this (includes SUMMARY - Network views)
        searchData = getCurrentSearchData();
        final TimeInfoDataType timeData = getCurrentTimeData();

        // Populate the body of PortletWindow based on Meta data
        final PortletType portletType = portletData.getType();
        final PortletTemplate portletTemplate = portletTemplateRegistry.createByName(portletType);
        if (portletTemplate instanceof MapPresenter) {
            ((MapPresenter) portletTemplate).setDashboardPresenter(this);
        }
        dashboardManager.registerPortlet(portletData);
        templateMap.put(portletData.getPortletId(), portletTemplate);

        getView().addPortlet(portletData, searchData, timeData, portletTemplate);

    }

    /*
    * Re-open porlet senario (will have missed notifications)
    * Will want to use same search data as other porlets if they exist
    */
    private SearchFieldDataType getCurrentSearchData() {
        // ask other porlets first
        SearchFieldDataType data;
        final Set<String> porletIds = dashboardManager.getPortletMap().keySet();
        for (final String porletId : porletIds) {
            final PortletWindow portletWindow = getPortletById(porletId);
            if (portletWindow != null) {
                data = portletWindow.getCurrentSearchData();
                if (data != null) {
                    return data; // break
                }
            }
        }
        // ONLY if no other portlets to ask (regardless of if play pressed)
        return taskbarHelper.getSearchComponentValue();
    }

    /**
     * Find PortletWindow using id
     *
     * @param portletId - id for porlet from meta data
     *
     * @return - window for this id
     */
    public PortletWindow getPortletById(final String portletId) {
        final PortletDataType portletInfo = dashboardManager.getPortletMap().get(portletId);

        // In case we have a placeholder, there is no portlet.

        //TODO can some one explain why would always return null for a MAP (GeoMap) portlet
        // (surely better to check for  PortletType.MAP.equals(portletType) elsewhere and not hack return here
        // TODO: Moreover in this case PortletWindow won't be reused for Map after search update + the old Map window
        // maybe not destroyed after creating new one => memory leak + bug prone approach.
        final PortletType portletType = portletInfo.getType();
        if (PortletType.PLACE_HOLDER.equals(portletType) || PortletType.MAP.equals(portletType)) {
            return null;
        }

        return getView().getPortletWindow(portletInfo);
    }

    /** Should open menu, but opening threshold window for now */
    public void openDashboardMenu() {
        // should open menu, but opening threshold window for now
        final Map<String, PortletDataType> portletMap = dashboardManager.getPortletMap();
        for (final PortletDataType portletData : portletMap.values()) {
            thresholdsPresenter.addThresholdsSection(portletData);
        }

        if (!thresholdsPresenter.isBound()) {
            thresholdsPresenter.bind();
        }

        thresholdsPresenter.showDialog();
    }

    /*
    * Re-open porlet senario (will have missed notifications)
    * @return latest known time data
    */
    private TimeInfoDataType getCurrentTimeData() {
        return taskbarHelper.getCurrentDashBoardTimeData();
    }

    private JSONValue checkAndParse(final MultipleInstanceWinId multiWinId, final Response response) {
        JSONValue responseValue = null;
        final String text = response.getText();
        try {
            // like server down - not a set response
            responseValue = JSONUtils.parse(text);
        } catch (final JSONException e) {
            // server should not have passed success, e.g. now trying to parse an error message

            final String portletId = multiWinId.getWinId();
            eventBus.fireEvent(new ComponentMessageEvent(portletId, ComponentMessageType.ERROR,
                    SERVER_CORRUPT_RESPONSE));
            eventBus.fireEvent(new PortletUnMaskEvent(portletId));
        }
        return responseValue;

    }

    /*
    * When Dash-board received notification of search data change, ask all
    * Search field user portlets to refresh themselves with the new search data
    * (the dash-board is only one registered for search field updates)
    */
    @Override
    public void handleSearchFieldParamUpdate(final String tabId, final String winId, final SearchFieldDataType data,
            final String url) {
        // guards
        if (!tabOwnerId.equals(tabId) || !dashboardWinId.equals(winId)) {
            return;
        }
        updateSearchUserPortlets(data);
    }

    private void updateSearchUserPortlets(final SearchFieldDataType data) {
        final boolean thereArePortletsToUpdate = false;

        final IGenericTabView genericview = TabViewRegistry.get().getTabView(DASHBOARD_TAB);

        final List<String> allLicences = genericview.getAllCurrentLicencesVoiceData();
        if (data.getType().equals(SUMMARY_LTE)) {

            final DashBoardDataType dashBoardData = genericview.getDashBoardDataLTE();

            getTaskBarHelper().launchDashBoard(dashBoardData, allLicences);
        } else {
            final DashBoardDataType dashBoardData = genericview.getDashBoardData();

            getTaskBarHelper().launchDashBoard(dashBoardData, allLicences);
        }
        // TODO to handle launch if
        //        for (final Map.Entry<String, PortletDataType> entry : dashboardManager.getPortletMap().entrySet()) {
        //            final PortletDataType info = entry.getValue();
        //
        //            if (info != null && info.isSearchFieldUser()) {
        //                final PortletWindow portletWindow = getPortletById(entry.getKey());
        //                if (portletWindow != null) {
        //                    thereArePortletsToUpdate = true;
        //                    portletWindow.refreshSearchData(data);
        //                }
        //            }
        //        }
        if (!thereArePortletsToUpdate) {
            // Have to use scheduler here due to many overlapping handlers, which disable the submit button
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    // Enable Submit Button back even if there where no portlets to update
                    eventBus.fireEvent(new MaskEvent(false, tabOwnerId));
                }
            });
        }
    }

    public IDashboardTaskbarHelper getTaskBarHelper() {
        return taskbarHelper;
    }

    /*
    * Called when portlet that was removed is added again.
    * Need to read in latest search data and time data to repopulate)
    */
    @Override
    public void onAdd(final PortletAddEvent portletAddEvent) {
        final String portletId = portletAddEvent.getComponentId();

        final PlaceHolder placeHolder = portletAddEvent.getPlaceHolder();
        placeHolder.removeFromParent();

        final PortletDataType portletInfo = dashboardManager.getPortletMap().get(portletId);
        final PortletType portletType = portletInfo.getType();
        addPortlet(portletInfo);

        // Resize Map so it fits on screen
        if (PortletType.MAP.equals(portletType)) {
            alignMapPortlet();
        }
    }

    /*
    * Portlet move event handling change column and row settings
    */
    @Override
    public void onMove(final PortletMoveEvent portletMoveEvent) {
        updatePortletInfos();
        dashboardManager.saveDashboardLayout();

        // Auto size Map if it's available
        alignMapPortlet();
    }

    private void updatePortletInfos() {
        int columnIndex = 0;
        for (final VerticalPanel holder : getView().getColumnList()) {
            int rowIndex = 0;
            for (final Widget widget : holder) {
                final HasPortletId hasPortletId = (HasPortletId) widget;
                final String portletId = hasPortletId.getPortletId();
                final PortletDataType portletInfo = dashboardManager.getPortletMap().get(portletId);

                portletInfo.setColumnIndex(columnIndex);
                portletInfo.setRowIndex(rowIndex);
                rowIndex++;
            }
            columnIndex++;
        }
    }

    /*
    * Portlet remove event handling. Replace portlet with place holder
    */
    @Override
    public void onRemove(final PortletRemoveEvent portletRemoveEvent) {
        final String portletId = portletRemoveEvent.getComponentId();
        final PortletWindow portlet = getPortletById(portletId);
        if (portlet != null) {
            portlet.removeFromParent();

            final PortletDataType portletInfo = dashboardManager.getPortletMap().get(portletId);
            final int columnIndex = portletInfo.getColumnIndex();
            final VerticalPanel portletHolder = getView().getColumnList().get(columnIndex);

            final PlaceHolder placeHolder = getView().createPlaceHolder(portletInfo);

            int rowIndex = portletInfo.getRowIndex();
            if (rowIndex > portletHolder.getWidgetCount()) {
                rowIndex = portletHolder.getWidgetCount();
            }
            portletHolder.insert(placeHolder, rowIndex);
        } else {
            // Handle Map remove
            final SimplePanel mapPanel = getView().getMapPanel();
            final int mapHeight = mapPanel.getOffsetHeight();
            mapPanel.getWidget().removeFromParent();

            final PlaceHolder placeHolder = getView().createPlaceHolder(portletId, mapHeight);
            mapPanel.setWidget(placeHolder);

        }
    }

    @Override
    public void onMessage(final ComponentMessageEvent messageEvent) {
        final String portletId = messageEvent.getComponentId();
        final PortletWindow portlet = getPortletById(portletId);
        final PortletDataType portletInfo = dashboardManager.getPortletMap().get(portletId);

        final ComponentMessagePanel panel = new ComponentMessagePanel();
        panel.populate(messageEvent.getType(), portletInfo.getPortletTitle(), messageEvent.getMessage());

        initButtons(portletId, panel, portletInfo);

        portlet.setMessage(panel);

        // If mask is shown, remove it.
        portlet.unmask();
    }

    private void initButtons(final String portletId, final ComponentMessagePanel panel,
            final PortletDataType portletInfo) {
        final PortletType portletType = portletInfo.getType();
        if (PortletType.BUSINESS_OBJECTS.equals(portletType)) {
            // BO portlet have Retry button rather than Replace
            final Button retry = new Button("Retry");
            retry.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(final ClickEvent event) {
                    eventBus.fireEvent(new PortletRefreshEvent(portletId));
                }
            });
            panel.addButton(retry);
        } else {
            panel.addButton(new Button("Replace"));
        }

        final Button button = new Button("Close Widget");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                eventBus.fireEvent(new PortletRemoveEvent(portletId));
            }
        });
        panel.addButton(button);
    }

    /** Port mask event. Show portlet mask when data is loading. */
    @Override
    public void onMask(final PortletMaskEvent portletMaskEvent) {
        final String portletId = portletMaskEvent.getComponentId();
        final PortletWindow portletWindow = getPortletById(portletId);
        if (null != portletWindow) {
            portletWindow.mask();
        }
    }

    @Override
    public void onUnMask(final PortletUnMaskEvent portletUnMaskEvent) {
        final String portletId = portletUnMaskEvent.getComponentId();
        final PortletWindow portletWindow = getPortletById(portletId);
        if (null != portletWindow) {
            portletWindow.unmask();
        }
    }

    /** Handle Resize for Map portlet to fit on available space */
    @Override
    public void onResize(final PortletResizeEvent portletAddEvent) {
        alignMapPortlet();
    }

    @Override
    public void onMask(final MapMaskEvent mapMaskEvent) {
        final MapPortletWindow mapWindow = getMapWindow();
        if (null != mapWindow) {
            mapWindow.mask();
        }
    }

    @Override
    public void onUnMask(final MapUnMaskEvent mapUnMaskEvent) {
        final MapPortletWindow mapWindow = getMapWindow();
        if (null != mapWindow) {
            mapWindow.unmask();
        }
    }

    @Override
    public void handleMaximize(final MapMaximizeEvent event) {
        final FlowPanel portletPanel = getView().getPortletPanel();
        portletPanel.setVisible(false);
        alignMapPortlet();
    }

    /** Handle restore of initial size for Map portlet */
    @Override
    public void restoreMap(final MapRestoreEvent event) {
        final FlowPanel portletPanel = getView().getPortletPanel();
        portletPanel.setVisible(true);
        alignMapPortlet();
    }

    /*
    * When Dash-board reveived notification of time change, as ALL open porlets to refresh
    * The porlets themselves will have to sort out if that are actually ready to refresh
    * (i.e if they need search data and they don't have it they are not ready)
    *
    */
    @Override
    public void handleTimeParamUpdate(final MultipleInstanceWinId multiWinId, final TimeInfoDataType timeData) {
        // guards
        if (!tabOwnerId.equals(multiWinId.getTabId()) || !dashboardWinId.equals(multiWinId.getWinId())) {
            return;
        }

        // (this is latest  time data being used (being attempted)
        getView().updateTitleBarText(timeData);

        final Set<String> porletIds = dashboardManager.getPortletMap().keySet();
        for (final String porletId : porletIds) {
            final PortletWindow portletWindow = getPortletById(porletId);
            if (portletWindow != null) {
                /* porlet windows will now make call and adjust for own dateFroms */
                portletWindow.refreshTimeData(timeData);
            }
        }
    }

    @Override
    public void handleTimeParamUpdate(final TimeInfoDataType time) {
        // not used for dashboard
    }

    /*
    * Paired search type changes - particular interested in "Network" selection (which must sent a call
    * with URL/SUMMARY to all search field user portlets
    */
    @Override
    public void handleTypeChanged(final String tabId, final String typeSelected, final boolean isGroup,
            final String typeText) {
        // guards
        if (!tabOwnerId.equals(tabId)) {
            return;
        }
        /*
        * i.e. Network selection in search type box, -  rest of search field changes are in value part
        * of paired search field (DashBoardSearchFieldValueResetImpl)
        */
        if (SearchFieldDataType.isSummaryType(typeSelected)) {
            updateSearchUserPortlets(taskbarHelper.getSearchComponentValue());
        }
    }

    /*
    * Success response from server call, update the correct porlet
    */
    @Override
    public void handleResponse(final MultipleInstanceWinId multiWinId, final String requestData, final Response response) {
        // guard
        if (!tabOwnerId.equals(multiWinId.getTabId())) {
            return;
        }

        final Set<String> ids = dashboardManager.getPortletMap().keySet();
        for (final String id : ids) {
            final PortletWindow portlet = getPortletById(id);

            if (portlet != null && portlet.isSamePorlet(multiWinId)) { // guard
                final JSONValue data = checkAndParse(multiWinId, response);

                // exception message written into response
                if (data != null && JSONUtils.checkData(data, eventBus, multiWinId)) {

                    final PortletDataType portletDataType = dashboardManager.getPortletMap().get(id);

                    // As BusinessObjects and other non charts & grid portlets should not show No Data message
                    final String displayType = portletDataType.getDisplayType();
                    if (JSONUtils.isDataEmpty(data) && displayType != null && !displayType.isEmpty()) {
                        eventBus.fireEvent(new ComponentMessageEvent(id, ComponentMessageType.INFO,
                                NO_DATA_MESSAGE_DASHBOARD));
                    } else { // there is data
                        final PortletTemplate portletTemplate = templateMap.get(id);

                        // portlet may need search data and time data from drilldowns
                        portletTemplate.update(data, portlet.getCurrentSearchData(), portlet.getCurrentTimeData());
                    }
                }
                // unmask if bad result when parse too
                eventBus.fireEvent(new PortletUnMaskEvent(id));
                break;
            }
        }
        /* To enable search button*/
        eventBus.fireEvent(new MaskEvent(false, tabOwnerId));
    }

    /*
    * Failed handling - only show one exception (for multiple porlet failures)
    */
    @Override
    public void handleFail(final MultipleInstanceWinId multiWinId, final String requestData, final Throwable exception) {
        if (!tabOwnerId.equals(multiWinId.getTabId())) {
            return; // guard against failure handling from other tabs (dashboard is only window in this tab)
        }

        String message;
        final String errorMessage = exception.getMessage();
        if (exception instanceof RequestTimeoutException) {
            message = TIMEOUT_EXCEPTION;
        } else if (errorMessage != null && LICENSE_ERROR.equals(errorMessage)) {
            message = LICENSE_ERROR;
        } else {
            message = SERVICES_ERROR;
        }

        final String portletId = multiWinId.getWinId();
        eventBus.fireEvent(new ComponentMessageEvent(portletId, ComponentMessageType.ERROR, message));
        eventBus.fireEvent(new PortletUnMaskEvent(portletId));

        /* To enable search button*/
        eventBus.fireEvent(new MaskEvent(false, tabOwnerId));
    }

}