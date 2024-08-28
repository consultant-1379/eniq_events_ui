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
package com.ericsson.eniq.events.ui.client.workspace;

import com.ericsson.eniq.events.common.client.time.TimePeriod;
import com.ericsson.eniq.events.ui.client.charts.PieChartHelper;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.MetaReader;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.common.widget.AbstractBaseWindowDisplay;
import com.ericsson.eniq.events.ui.client.common.widget.EniqWindow;
import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.JsonAutoBeanDataFactory;
import com.ericsson.eniq.events.ui.client.datatype.*;
import com.ericsson.eniq.events.ui.client.events.GraphDrillDownLaunchEvent;
import com.ericsson.eniq.events.ui.client.events.window.*;
import com.ericsson.eniq.events.ui.client.main.ChartLauncher;
import com.ericsson.eniq.events.ui.client.main.GridLauncher;
import com.ericsson.eniq.events.ui.client.search.ISubmitSearchHandler;
import com.ericsson.eniq.events.ui.client.workspace.WindowPositionHelper.WindowPositionInfo;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.WindowPositioning;
import com.ericsson.eniq.events.ui.client.workspace.config.WorkspaceConfigService;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IDimension;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IWindow;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WindowState;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;
import com.ericsson.eniq.events.ui.client.workspace.events.*;
import com.ericsson.eniq.events.ui.client.workspace.launch.WindowLaunchParams;
import com.ericsson.eniq.events.ui.client.workspace.launchwindowconfig.ConfigurationDialogPresenter;
import com.ericsson.eniq.events.ui.client.workspace.launchwindowconfig.ConfigurationDialogView;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog.DialogType;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import java.util.*;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

/**
 * Controller of the windows of an individual workspace/tab.
 */
public class WorkspaceWindowController implements IWorkspaceController, WindowTitleUpdateEventHandler {

    private static final DateTimeFormat labelDateFormat = DateTimeFormat.getFormat("HH:mm, yyyy-MM-dd");

    private Point lastOpenedWindowPosition = new Point(0, 0);

    private final EventBus eventBus;

    private IWindowContainer windowContainer;

    private final MetaReader metaReader;

    private String workspaceId;

    /* non-standard launch windows, from config window but have no variable input params */
    private static List<String> doNotUseUrlTypeList = new ArrayList<String>() {
        {
            add("RAN_CFA_CONFIG");
            add("NETWORK_CELL_RANKING_RAN_WCDMA_CFA_CONFIG");
        }
    };

    /**
     * Windows that have been launched from the Workspace Launch Menu. Can only save these window types.
     */
    private final Map<String, WindowParameters> windowLaunchMap = new HashMap<String, WindowParameters>();

    /**
     * All windows i.e. drills etc. Cant save drilled windows
     */
    private final Map<String, BaseWindow> windowsMap = new HashMap<String, BaseWindow>();

    private WindowPositionHelper windowPositionHelper;

    List<HandlerRegistration> handlers = new ArrayList<HandlerRegistration>();

    private final Set<String> savedWindowSet = new HashSet<String>();

    private final Set<String> unSavedWindowSet = new HashSet<String>();

    private final WorkspaceConfigService configService;

    private ConfigurationDialogView configurationDialogView;

    private ConfigurationDialogPresenter configurationDialogPresenter;

    private boolean pinned;

    /**
     * @param eventBus
     * @param metaReader
     * @param configService
     */
    @Inject
    public WorkspaceWindowController(EventBus eventBus, MetaReader metaReader, WorkspaceConfigService configService,
                                     ConfigurationDialogPresenter configurationDialogPresenter) {
        this.eventBus = eventBus;
        this.metaReader = metaReader;
        this.configService = configService;
    }

    protected void bind(EventBus eventBus) {
        handlers.add(eventBus.addHandler(WindowOpenedEvent.TYPE, this));
        handlers.add(eventBus.addHandler(WindowClosedEvent.TYPE, this));
        handlers.add(eventBus.addHandler(WindowTitleUpdateEvent.TYPE, this));
        handlers.add(eventBus.addHandler(GraphDrillDownLaunchEvent.TYPE, new PieChartHelper(this, eventBus)));
        handlers.add(eventBus.addHandler(WorkspaceDataSaveCompleteEvent.TYPE, new WorkspaceDataSaveCompleteEventHandler() {

            @Override
            public void onWorkspaceSaveComplete(WorkspaceDataSaveCompleteEvent event) {
                clearWindowPersistenceInfo();
                for (String id : windowLaunchMap.keySet()) {
                    savedWindowSet.add(id);
                }
            }
        }));
    }

    public void unbind() {
        for (HandlerRegistration handler : handlers) {
            handler.removeHandler();
        }
        handlers.clear();
    }

    public void init(String workspaceId, IWindowContainer windowContainer) {
        this.workspaceId = workspaceId;
        this.windowContainer = windowContainer;
        windowPositionHelper = new WindowPositionHelper(windowContainer, windowsMap);
        bind(eventBus);
    }

    /**
     * @param params
     * @param pinned
     */
    public void launchWindow(WindowLaunchParams params, final boolean pinned) {
        TimeInfoDataType timeInfoDataType = WorkspaceUtils.getTimeInfo(params.getTimePeriod(), params.getFrom(), params.getTo());
        final boolean tilingRequired = params.getWindows().size() > 1;
        WindowPositionInfo[] windowPositions = null;
        sortList(params.getWindows());
        for (int i = 0; i < params.getWindows().size(); i++) {
            IWindow window = params.getWindows().get(i);
            String windowMetaId = window.getId();
            MetaMenuItem item = metaReader.getMetaMenuItemFromID(windowMetaId);

            if (tilingRequired) {
                if (item.getWindowType().equals(MetaMenuItemDataType.Type.CONFIG)) {
                    windowPositions = windowPositionHelper.getConfigWindowPositionInfoForTiling(params.getWindows().size(), pinned);
                } else {
                    windowPositions = windowPositionHelper.getWindowPositionInfoForTiling(params.getWindows().size(), pinned);
                }
            } else {
                if (item.getWindowType().equals(MetaMenuItemDataType.Type.CONFIG)) {
                    windowPositions = windowPositionHelper.getConfigWindowPositionInfoForTiling(params.getWindows().size(), pinned);
                } else {
                    windowPositions = windowPositionHelper.getWindowPositionInfoForTiling(params.getWindows().size(), pinned);
                }
            }

        }
        sortList(params.getWindows());

        for (int i = 0; i < params.getWindows().size(); i++) {
            IWindow window = params.getWindows().get(i);
            String windowMetaId = window.getId();
            MetaMenuItem item = metaReader.getMetaMenuItemFromID(windowMetaId);

            SearchFieldDataType searchInfo = getSearchDataType(getSearchFieldValue(params.getPrimarySelection(), params.getSecondarySelection()),
                    getNodeName(params.getPrimarySelection(), params.getSecondarySelection()), params.getDimension(), item,
                    params.getExtraURLParams());
            WindowParameters windowParameters = new WindowParameters(params.getDimension(), params.getTimePeriod(), params.getFrom(), params.getTo(),
                    params.getPrimarySelection(), params.getPairedSelectionUrl(), params.getSecondarySelection(), window, searchInfo,
                    getCategory(windowMetaId), getTechnologyName(params));

            /** If we require tiling then we do not allow the window to set its own width and height **/
            launchWindow(params, item, timeInfoDataType, !tilingRequired, windowPositions[i], windowParameters, false);

            eventBus.fireEvent(new LaunchWindowCompletedEvent(params.getSource()));
        }
    }

    /* We want any CONFIG windows to be first window launched */
    private void sortList(List<IWindow> windows) {
        for (int i = 0; i < windows.size(); i++) {
            String windowMetaId = windows.get(i).getId();
            MetaMenuItem item = metaReader.getMetaMenuItemFromID(windowMetaId);
            if (item.getWindowType().equals(MetaMenuItemDataType.Type.CONFIG)) {
                //remove from list and add to the start of the list
                windows.add(0, windows.get(i));
                windows.remove(i + 1);
            }
        }
    }

    private String getSearchFieldValue(String primarySelection, String secondarySelection) {
        return secondarySelection.isEmpty() ? primarySelection : primarySelection + DASH + secondarySelection;
    }

    /**
     * @param params
     * @return
     */
    private String getTechnologyName(WindowLaunchParams params) {
        return params.getTechnologyType() == null ? "" : params.getTechnologyType().toString();//.name();
    }

    protected void launchWindow(WindowLaunchParams params, MetaMenuItem item, TimeInfoDataType timeInfoDataType, final boolean useDefaultDimensions,
                                WindowPositionInfo windowPosition, WindowParameters windowParameters, boolean restoring) {
        /** Generate unique id for each window. No more multiple instance stuff etc **/
        String windowId = WorkspaceUtils.generateId();
        /** If we are launching windows from a restore, we dont want a dirty workspace indicator **/
        if (restoring) {
            savedWindowSet.add(windowId);
        }
        if (item == null) {
            new MessageDialog().show("Error", "Missing " + windowParameters.getWindow().getId(), DialogType.ERROR);
            return;
        }
        switch (item.getWindowType()) {
            case GRID:
            case RANKING:
                windowLaunchMap.put(windowId, windowParameters);
                launchGrid(timeInfoDataType, item, windowPosition, useDefaultDimensions, windowId);
                break;
            case CHART:
                windowLaunchMap.put(windowId, windowParameters);
                launchChart(timeInfoDataType, item, windowPosition, useDefaultDimensions, windowId);
                break;
            case CONFIG:
                configurationDialogView = new ConfigurationDialogView();
                configurationDialogPresenter = new ConfigurationDialogPresenter(configurationDialogView, eventBus, metaReader, configService);

                configurationDialogPresenter.launch(timeInfoDataType, item, windowParameters, windowPosition, this, windowContainer, windowId,restoring);
                break;
        }
    }

    protected void launchChart(TimeInfoDataType timeInfoDataType, MetaMenuItem item, final WindowPositionInfo windowPositionInfo,
                               final boolean useDefaultDimensions, final String windowId) {
        new ChartLauncher(item, getEventBus(), windowContainer.getWindowContainerPanel(), this, windowId) {
            @Override
            protected MultipleInstanceWinId createMultipleInstanceWinId() {
                return new MultipleInstanceWinId(workspaceId, windowId);
            }

            ;

            @Override
            protected void setWindowInitialPosition(final AbstractBaseWindowDisplay view) {
                setWindowPosition(windowPositionInfo, useDefaultDimensions, view);
            }
        }.launchWindow(timeInfoDataType, false);
    }

    protected void launchGrid(TimeInfoDataType timeInfoDataType, MetaMenuItem item, final WindowPositionInfo windowPositionInfo,
                              final boolean useDefaultDimensions, final String windowId) {
        new GridLauncher(item, getEventBus(), windowContainer.getWindowContainerPanel(), this, windowId) {
            @Override
            protected MultipleInstanceWinId createMultipleInstanceWinId() {
                return new MultipleInstanceWinId(workspaceId, windowId);
            }

            ;

            @Override
            protected void setWindowInitialPosition(final AbstractBaseWindowDisplay view) {
                setWindowPosition(windowPositionInfo, useDefaultDimensions, view);
            }
        }.launchWindow(timeInfoDataType, false);
    }

    public void launchFromConfig(final TimeInfoDataType timeInfoDataType, final MetaMenuItem item, final WindowPositionInfo windowPositionInfo,
                                 final boolean useDefaultDimensions, final String windowId) {
        new GridLauncher(item, getEventBus(), windowContainer.getWindowContainerPanel(), this, windowId) {
            @Override
            protected MultipleInstanceWinId createMultipleInstanceWinId() {
                return new MultipleInstanceWinId(workspaceId, windowId);
            }

            ;

            @Override
            protected void setWindowInitialPosition(final AbstractBaseWindowDisplay view) {
                setWindowPosition(windowPositionInfo, useDefaultDimensions, view);
            }
        }.launchWindow(timeInfoDataType, false);
    }

    protected void setWindowPosition(final WindowPositionInfo windowPositionInfo, final boolean useDefaultDimensions,
                                     final AbstractBaseWindowDisplay view) {
        final EniqWindow eniqWindow = view.getWidget();
        if (!useDefaultDimensions) {
            /** Not using default dimensions (width, height), setting them here i.e. for tiling or restoring window **/
            eniqWindow.setPositionAndSize(windowPositionInfo.getX(), windowPositionInfo.getY(), windowPositionInfo.getWidth(),
                    windowPositionInfo.getHeight());

        } else {
            final Point newPosition = eniqWindow.setWindowPosition(new Point(windowPositionInfo.getX(), windowPositionInfo.getY()));
            setLastOpenedWindowPosition(newPosition);
        }
    }

    /*
     * @param nodeName
     * 
     * @param dimension
     * 
     * @param item
     * 
     * @return
     */
    private SearchFieldDataType getSearchDataType(String searchFieldVal, String nodeName, IDimension dimension, MetaMenuItem item,
                                                  String extraURLParams) {
        if (!item.isSearchFieldUser()) {
            return null;
        }
        /* e.g {"type=APN", "node=MyNode"}) */
        String[] urlParms;
        if (nodeName.trim().isEmpty()) {
            urlParms = createUrlParams(dimension, item, extraURLParams);
        } else {
            urlParms = new String[] { ("type=" + getNodeType(dimension)),
                    (isGroupType(dimension) ? "groupname" : dimension.getUrlParam()) + "=" + nodeName, extraURLParams };
        }
        String titlePostfix = translateCategory(extraURLParams);
        return new SearchFieldDataType(searchFieldVal, urlParms, getNodeType(dimension), dimension.getName(), titlePostfix, isGroupType(dimension),
                "", null, item.getSearchFieldUser().equals(SearchFieldUser.PATH));
    }

    private String[] createUrlParams(IDimension dimension, MetaMenuItem item, String extraURLParams) {
        String[] urlParms;

        /*
         * Some Urls specifically do not require the type param appended - due to the blob of code too difficult to remove without causing problems.
         * List at top, just ranking config windows for now
         */
        if (doNotUseUrlTypeList.contains(item.getID()))
            urlParms = new String[] { extraURLParams };
        else
            urlParms = new String[] { ("type=" + getNodeType(dimension)), extraURLParams };
        return urlParms;
    }

    //translate the url (eventID=438&categoryId=3&drillCat=438_3) by extracting the categoryId,
    //using the Id to determine the title (titlePostfix) of the window.
    public String translateCategory(String url) {
        String category = EMPTY_STRING;
        if (url.contains(CATEGORY_ID)) {
            int index = url.indexOf(CATEGORY_ID) + CATEGORY_ID.length();
            int value = Integer.parseInt(url.substring(index, index + 1));

            switch (value) {
                case TOTAL_RAB_FAILURES:
                    category = TITLE_TOTAL_RAB_FAILURES;
                    break;
                case CIRCUIT_SWITCHED_RAB_FAILURES:
                    category = TITLE_CIRCUIT_SWITCHED_RAB_FAILURES;
                    break;
                case PACKET_SWITCED_RAB_RAILURES:
                    category = TITLE_PACKET_SWITCED_RAB_RAILURES;
                    break;
                case MULTI_RAB_FAILURES:
                    category = TITLE_MULTI_RAB_FAILURES;
                    break;
                default:
                    break;
            }
        }
        return category;
    }

    public String getNodeType(IDimension dimension) {
        if (isGroupType(dimension))
            return dimension.getGroupType();
        else
            return dimension.getId();
    }

    protected boolean isGroupType(IDimension dimension) {
        return WorkspaceUtils.isNonEmptyString(dimension.getGroupType());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController#getSearchComponentValue(java.lang.String)
     */
    @Override
    public SearchFieldDataType getSearchComponentValue(String id) {
        if (windowLaunchMap.containsKey(id)) {
            return windowLaunchMap.get(id).getSearchData();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.eniq.events.ui.client.workspace.IWindowController#getTabOwnerId()
     */
    @Override
    public String getTabOwnerId() {
        return workspaceId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.eniq.events.ui.client.workspace.IWindowController#getWindow(java.lang.String)
     */
    @Override
    public BaseWindow getWindow(String winId) {
        return windowsMap.get(winId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.eniq.events.ui.client.workspace.IWindowController#getCenterPanel()
     */
    @Override
    public ContentPanel getCenterPanel() {
        return windowContainer.getWindowContainerPanel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.eniq.events.ui.client.workspace.IWindowController#addSubmitSearchHandler(com.ericsson.eniq.events.ui.client.search.
     * ISubmitSearchHandler)
     */
    @Override
    public void addSubmitSearchHandler(ISubmitSearchHandler handler) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.eniq.events.ui.client.workspace.IWindowController#removeSubmitSearchHandler(com.ericsson.eniq.events.ui.client.search.
     * ISubmitSearchHandler)
     */
    @Override
    public void removeSubmitSearchHandler(ISubmitSearchHandler handler) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.eniq.events.ui.client.workspace.IWindowController#getLastOpenedWindowPosition()
     */
    @Override
    public Point getLastOpenedWindowPosition() {
        return lastOpenedWindowPosition;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.eniq.events.ui.client.workspace.IWindowController#setLastOpenedWindowPosition(com.extjs.gxt.ui.client.util.Point)
     */
    @Override
    public void setLastOpenedWindowPosition(Point newPosition) {
        lastOpenedWindowPosition = newPosition;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.eniq.events.ui.client.workspace.IWindowController#justSaysInputInSearchFieldNoSelection()
     */
    @Override
    public boolean justSaysInputInSearchFieldNoSelection() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController#getEventBus()
     */
    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.eniq.events.ui.client.events.window.WindowOpenedEventHandler#onWindowOpened(com.ericsson.eniq.events.ui.client.events.window.
     * WindowOpenedEvent)
     */
    @Override
    public void onWindowOpened(WindowOpenedEvent event) {
        final WindowModel model = event.getModel();
        // Only accept events related to this workspace
        if (!workspaceId.equals(model.getTabId())) {
            return;
        }
        final BaseWindow window = event.getWindow();
        String windowId = window.getBaseWindowID();
        /**
         * Is it a window launched from the launch map, this will be null if not.
         */
        WindowParameters windowParameters = windowLaunchMap.get(windowId);

        if (windowParameters == null) {
            windowsMap.put(windowId, window);
        }

        String categoryId = getWindowCategoryId(window);

        if (windowParameters != null) {

            windowsMap.put(windowId, window);
            /**
             * if restoring then this is not an unsaved window otherwise it is.
             */
            if (!savedWindowSet.contains(windowId)) {
                unSavedWindowSet.add(windowId);
            }
        }
        getEventBus().fireEvent(
                new WorkspaceToolbarWindowOpenEvent(workspaceId, getCategory(categoryId), getTitle(((IExtendedWidgetDisplay) event.getWindow()),
                        windowParameters), model.getIcon(), window));
        getEventBus().fireEvent(new WorkspaceStatusChangeEvent(workspaceId, windowsMap.size(), unSavedWindowSet.size() > 0));
    }

    /**
     * @param window
     * @return
     */
    private String getWindowCategoryId(final BaseWindow window) {
        return window.getWindowCategoryId().isEmpty() ? ((IExtendedWidgetDisplay) window).getViewSettings().getID() : window.getWindowCategoryId();
    }

    /**
     * @param window
     * @param windowParameters
     * @return
     */
    private String getTitle(IExtendedWidgetDisplay window, WindowParameters windowParameters) {
        if (windowParameters == null) {
            /** Drill windows **/
            return window.getParentWindow().getBaseWindowTitle() + " : " + getDrillWindowTimeData(window);
        }
        boolean isExtraURLParam = ExtraURLDataType.isExtra(windowParameters.getExtraURLType());
        StringBuilder sbTitle = new StringBuilder();
        sbTitle.append(getTitleComponent(windowParameters.getTechnology(), false));
        sbTitle.append(getTitleComponent(windowParameters.getDimension().getName(), false));
        sbTitle.append(getTitleComponent(windowParameters.getPrimarySelection(), false));
        sbTitle.append(getTitleComponent(windowParameters.getSecondarySelection(), false));
        sbTitle.append(getTitleComponent(window.getParentWindow().getTitleBase(), isExtraURLParam));

        if (isExtraURLParam) {
            ArrayList<String> titles = ExtraURLDataType.extractExtraURLParams(
                    ExtraURLDataType.ExtraURLType.fromString(windowParameters.getExtraURLType()), windowParameters.getExtraURLParams());
            for (String extraTitle : titles) {
                if (!sbTitle.toString().endsWith("> ")) {
                    sbTitle.append(" > ");
                }
                sbTitle.append(extraTitle);
            }
        }

        //make sure there is no trailing chevron ( > ) in the title before the time is appended.
        if (sbTitle.lastIndexOf(" > ") == sbTitle.length() - 3) {
            sbTitle.replace(sbTitle.length() - 3, sbTitle.length(), "");
        }

        sbTitle.append(" : ");
        sbTitle.append(getTitleComponent(
                getFormattedDateRangeLabel(windowParameters.getTimePeriod(), windowParameters.getFrom(), windowParameters.getTo()), true));
        return sbTitle.toString();
    }

    /**
     * @param window
     * @return
     */
    private String getDrillWindowTimeData(IExtendedWidgetDisplay window) {
        /** Time range is sometimes empty at this point for some reason, presumably reset somewhere in Eniq Events Core **/
        if (window.getTimeData().timeRange.isEmpty() && window.getTimeData().timeFrom == null) {
            return "";
        }
        return window.getTimeData().toString();
    }

    private String getTitleComponent(String component, boolean isLast) {
        return (component.isEmpty() ? "" : component + (isLast ? "" : " > "));//TITLE_SEPARATOR));
    }

    String getFormattedDateRangeLabel(TimePeriod timePeriod, Date from, Date to) {
        if (timePeriod.equals(TimePeriod.CUSTOM)) {
            return labelDateFormat.format(from) + " - " + labelDateFormat.format(to);
        }
        return timePeriod.toFullText();
    }

    /**
     * @param categoryId
     * @return
     */
    private String getCategory(String categoryId) {
        String category = configService.getWindowCategory(categoryId);
        return category == null ? categoryId : category;
    }

    public void closeAllWindows() {
        /** Avoid concurrent modification exception by iterating over a copy of the windowsMap values. **/
        List<BaseWindow> windowsList = new ArrayList<BaseWindow>();
        for (BaseWindow w : windowsMap.values()) {
            windowsList.add(w);
        }
        for (BaseWindow window : windowsList) {
            window.hide();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.eniq.events.ui.client.events.window.WindowClosedEventHandler#onWindowClosed(com.ericsson.eniq.events.ui.client.events.window.
     * WindowClosedEvent)
     */
    @Override
    public void onWindowClosed(WindowClosedEvent event) {
        WindowModel model = event.getModel();
        if (workspaceId.equals(model.getTabId())) {
            String windowId = event.getWindow().getBaseWindowID();
            windowsMap.remove(windowId);
            windowLaunchMap.remove(windowId);
            unSavedWindowSet.remove(windowId);
            getEventBus().fireEvent(new WorkspaceStatusChangeEvent(workspaceId, windowsMap.size(), isWorkspaceUnsavedOnWindowClose(windowId)));
            getEventBus().fireEvent(new WorkspaceToolbarWindowCloseEvent(workspaceId, event.getWindow()));
        }
    }

    /**
     * Workspace is unsaved if there are unsaved windows still in it OR we have closed a saved window
     * 
     * @param windowId
     * @return
     */
    boolean isWorkspaceUnsavedOnWindowClose(String windowId) {
        return unSavedWindowSet.size() > 0 || savedWindowSet.contains(windowId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController#getSearchComponent()
     */
    @Override
    public Component getSearchComponent() {
        return null;
    }

    public void cascade(boolean pinned) {
        windowPositionHelper.cascade(pinned);
    }

    public void tile(boolean pinned) {
        windowPositionHelper.tile(pinned);
    }

    public WorkspaceState getWorkspaceState(JsonAutoBeanDataFactory factory) {

        WorkspaceState workspaceState = factory.workspaceState().as();
        workspaceState.setId(workspaceId);
        List<WindowState> windowList = new ArrayList<WindowState>();
        for (String windowId : windowLaunchMap.keySet()) {
            WindowState windowState = factory.windowState().as();
            WindowParameters params = windowLaunchMap.get(windowId);
            windowState.setDimensionId(params.getDimension().getId());
            windowState.setTechnology(params.getTechnology());
            windowState.setFrom(params.getFrom());
            windowState.setTo(params.getTo());
            windowState.setTimePeriod(params.getTimePeriod().toString());
            windowState.setPrimarySelection(params.getPrimarySelection());
            windowState.setPairedSelectionUrl(params.getPairedSelectionUrl());
            windowState.setSecondarySelection(params.getSecondarySelection());
            windowState.setExtraURLParams(params.getExtraURLParams());
            windowState.setExtraURLType(params.getExtraURLType());
            windowState.setWindowId(params.getWindow().getId());
            BaseWindow window = windowsMap.get(windowId);
            if (window != null) {
                windowState
                        .setWidthRatio(getRatio((double) window.getWidget().getWidth() / windowContainer.getWindowContainerPanel().getInnerWidth()));
                windowState.setHeightRatio(getRatio((double) window.getWidget().getHeight()
                        / windowContainer.getWindowContainerPanel().getInnerHeight()));
                windowState.setTopRatio(getRatio((double) window.getWidget().el().getTop()
                        / windowContainer.getWindowContainerPanel().getInnerHeight()));
                windowState.setLeftRatio(getRatio((double) window.getWidget().getAbsoluteLeft()
                        / windowContainer.getWindowContainerPanel().getInnerWidth()));
            }
            windowList.add(windowState);
        }

        workspaceState.setWindows(windowList);
        return workspaceState;
    }

    /**
     * @param ratio
     * @return
     */
    private double getRatio(double ratio) {
        return Double.valueOf(NumberFormat.getFormat("#.###").format(ratio));
    }

    private void clearWindowPersistenceInfo() {
        savedWindowSet.clear();
        unSavedWindowSet.clear();
    }

    /**
     * Restore a workspace to it's previously saved state. This is called on relaunch of saved workspaces.
     * 
     * @param workspaceState
     */
    public void restoreWorkspace(WorkspaceState workspaceState) {
        clearWindowPersistenceInfo();
        WindowPositioning positioning = WindowPositioning.fromString(workspaceState.getWindowPositioning());

        List<WindowState> windowList = new ArrayList<WindowState>();
        //only open enabled windows.
        for (WindowState ws : workspaceState.getWindows()) {
            if (ws.isEnabled()) {
                windowList.add(ws);
            }
        }

        WindowPositionInfo[] windowPositionInfoArray = null;
        if (positioning == WindowPositioning.CASCADE) {
            windowPositionInfoArray = windowPositionHelper.getWindowPositionInfoForCascade(windowList.size(), false);
        } else if (positioning == WindowPositioning.TILE) {
            windowPositionInfoArray = windowPositionHelper.getWindowPositionInfoForTiling(windowList.size(), false);
        }
        int i = 0;
        for (WindowState windowData : windowList) {
            IDimension dimension = configService.getDimension(windowData.getDimensionId());
            TimePeriod timePeriod = TimePeriod.fromString(windowData.getTimePeriod());
            Date from = windowData.getFrom();
            Date to = windowData.getTo();
            String primarySelection = WorkspaceUtils.getString(windowData.getPrimarySelection());
            String secondarySelection = WorkspaceUtils.getString(windowData.getSecondarySelection());
            IWindow window = configService.getWindow(windowData.getWindowId());
            String windowMetaId = window.getId();
            MetaMenuItem item = metaReader.getMetaMenuItemFromID(windowMetaId);
            SearchFieldDataType searchInfo = getSearchDataType(getSearchFieldValue(primarySelection, secondarySelection),
                    getNodeName(primarySelection, secondarySelection), dimension, item, windowData.getExtraURLParams());

            WindowParameters parameters = new WindowParameters(dimension, timePeriod, from, to, primarySelection, windowData.getPairedSelectionUrl(),
                    secondarySelection, window, searchInfo, getCategory(windowMetaId), WorkspaceUtils.getString(windowData.getTechnology()),
                    windowData.getExtraURLParams(), windowData.getExtraURLType());

            TimeInfoDataType timeInfoDataType = WorkspaceUtils.getTimeInfo(timePeriod, from, to);
            if (positioning == WindowPositioning.FREEFORM) {
                int width = (int) (windowContainer.getWindowContainerPanel().getInnerWidth() * windowData.getWidthRatio());
                int height = (int) (windowContainer.getWindowContainerPanel().getInnerHeight() * windowData.getHeightRatio());
                int top = (int) (windowContainer.getWindowContainerPanel().getInnerHeight() * windowData.getTopRatio());
                int left = (int) (windowContainer.getWindowContainerPanel().getInnerWidth() * windowData.getLeftRatio());
                WindowPositionInfo windowPosition = new WindowPositionInfo(width, height, left, top);
                //TODO: NULL here for LaunchParams.
                launchWindow(null, item, timeInfoDataType, false, windowPosition, parameters, true);
            } else {
                WindowPositionInfo windowPositionInfo = windowPositionInfoArray != null ? windowPositionInfoArray[i++] : null;
                //TODO: NULL here for LaunchParams.
                launchWindow(null, item, timeInfoDataType, false, windowPositionInfo, parameters, true);
            }
        }
        tile(this.isPinned());
    }

    /**
     * @param primarySelection
     * @param secondarySelection
     * @return
     */
    private String getNodeName(String primarySelection, String secondarySelection) {
        /** If its a terminal we need to get secondary selection for TAC **/
        return secondarySelection.isEmpty() ? primarySelection : secondarySelection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ericsson.eniq.events.ui.client.events.window.WindowTitleUpdateEventHandler#onWindowTitleUpdated(com.ericsson.eniq.events.ui.client.events
     * .window.WindowTitleUpdateEvent)
     */
    @Override
    public void onWindowTitleUpdated(WindowTitleUpdateEvent event) {
        final WindowModel model = event.getModel();
        // Only accept events related to this workspace
        if (!workspaceId.equals(model.getTabId())) {
            return;
        }
        final BaseWindow window = event.getWindow();
        String categoryId = getWindowCategoryId(window);

        /**
         * Is it a window launched from the launch map, this will be null if not.
         */
        WindowParameters windowParameters = windowLaunchMap.get(window.getBaseWindowID());
        getEventBus().fireEvent(
                new WorkspaceToolbarWindowTitleUpdateEvent(workspaceId, getCategory(categoryId), getTitle(
                        ((IExtendedWidgetDisplay) event.getWindow()), windowParameters), model.getIcon(), window));
    }

    public void addWindowFromConfig(String windowId, WindowParameters windowParameters) {
        windowLaunchMap.put(windowId, windowParameters);
    }

    public void removeConfigWindowFromMap(String configWindowId) {
        windowLaunchMap.remove(configWindowId);
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setIsPinned(boolean isPinned) {
        this.pinned = isPinned;
    }
}
