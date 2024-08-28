/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.preferences.UserPreferencesHelper;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.JsonAutoBeanDataFactory;
import com.ericsson.eniq.events.ui.client.datatype.TabInfoDataType;
import com.ericsson.eniq.events.ui.client.events.UserLogoutEvent;
import com.ericsson.eniq.events.ui.client.events.UserLogoutEventHandler;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.DefinedWorkspaceType;
import com.ericsson.eniq.events.ui.client.workspace.config.WorkspaceConfigService;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IWindow;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IWorkspaceConfiguration;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IWorkspaceConfigurationWrapper;
import com.ericsson.eniq.events.ui.client.workspace.datatype.AllWorkspacesState;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WindowState;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;
import com.ericsson.eniq.events.ui.client.workspace.events.*;
import com.ericsson.eniq.events.ui.client.workspace.launch.WorkspaceLaunchMenuResourceBundle;
import com.ericsson.eniq.events.ui.client.workspace.tab.ITabSelector;
import com.ericsson.eniq.events.ui.client.workspace.tab.SafariTabSelector;
import com.ericsson.eniq.events.ui.client.workspace.tab.WorkspaceUserTabItem;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog.DialogType;
import com.ericsson.eniq.events.widgets.client.dialog.PromptDialog;
import com.ericsson.eniq.events.widgets.client.dialog.events.PromptCancelEventHandler;
import com.ericsson.eniq.events.widgets.client.dialog.events.PromptOkEvent;
import com.ericsson.eniq.events.widgets.client.dialog.events.PromptOkEventHandler;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.event.shared.EventBus;

import java.util.*;

import static com.ericsson.eniq.events.ui.client.workspace.TabIdUtils.generateTabId;
import static com.ericsson.eniq.events.ui.client.workspace.TabIdUtils.generateTabIdPrefix;
import static com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.MAX_NUMBER_OF_USER_TABS;

/**
 * Class to handle creating, closing and saving of workspaces
 *
 * @author ecarsea
 * @author ealeerm - Alexey Ermykin
 * @since 2012
 */
public class WorkspaceManager {

    private static final String WORKSPACE_MANAGEMENT_ID = "workspaceManagement";

    private static final String PLUS_TAB_ID = "PLUS_TAB";

    private final Provider<WorkspacePresenter> workspacePresenterProvider;

    /**
     * Active and open workspaces only *
     */
    private final LinkedHashMap<String, WorkspaceTabComponent> activeWorkspaces = new LinkedHashMap<String, WorkspaceTabComponent>();

    /**
     * All workspaces currently in the DB. Will not contain new unsaved workspaces *
     */
    private final Map<String, WorkspaceState> workspaceStateMap = new HashMap<String, WorkspaceState>();

    private final EventBus eventBus;

    private final UserPreferencesHelper userPreferencesHelper;

    private final JsonAutoBeanDataFactory factory;

    private final Provider<WorkspaceSaveAsPresenter> workspaceSaveAsPresenterProvider;

    private final List<String> startupItems = new ArrayList<String>();

    private final WorkspaceConfigService configService;

    private IWorkspaceContainer workspaceContainer;

    private TabItem plusTabItem;

    private ITabSelector tabSelector;

    private WorkspaceMessages messages;

    private ClosingHandlerImpl closingHandler = new ClosingHandlerImpl();

    private WorkspacePresenter activeWorkspacePresenter;

    private IWorkspaceConfiguration workspaceConfiguration;
    private static List<String> supportedAccessGroups;

    @Inject
    public WorkspaceManager(UserPreferencesHelper userPreferencesHelper,
            Provider<WorkspacePresenter> workspacePresenterProvider, EventBus eventBus,
            JsonAutoBeanDataFactory factory, Provider<WorkspaceSaveAsPresenter> workspaceSaveAsPresenterProvider,
            WorkspaceConfigService configService, WorkspaceMessages messages,
            IMetaReader metaReader,
            WorkspaceLaunchMenuResourceBundle resources,
            WorkspaceConfigAutoBeanFactory autoBeanFactory) {
        this.workspacePresenterProvider = workspacePresenterProvider;
        this.userPreferencesHelper = userPreferencesHelper;
        this.eventBus = eventBus;
        this.factory = factory;
        this.configService = configService;
        this.workspaceSaveAsPresenterProvider = workspaceSaveAsPresenterProvider;
        this.messages = messages;

        String configData = resources.workspaceLaunchConfig().getText();
        IWorkspaceConfigurationWrapper workspaceConfigurationWrapper = AutoBeanCodex.decode(autoBeanFactory, IWorkspaceConfigurationWrapper.class, configData).as();
        workspaceConfiguration = workspaceConfigurationWrapper.getWorkspaceConfiguration();
        supportedAccessGroups = metaReader.getSupportedAccessGroups();

        addHandlers();
    }


    public LinkedHashMap<String, WorkspaceTabComponent> getActiveWorkspaces() {
        return this.activeWorkspaces;
    }

    private void addHandlers() {

        eventBus.addHandler(UserLogoutEvent.TYPE, new UserLogoutEventHandler() {
            @Override
            public void onUserLogoutEvent() {
                if(dirtyUserTabsNumber() > 0) {
                    showConfirmationDialog(messages.logoutTitle(), messages.logout(dirtyUserTabsNumber()),
                            new PromptOkEventHandler() {
                                @Override
                                public void onPromptOk(final PromptOkEvent event) {
                                    closingHandler.setIsLoggingOut(true);
                                    logOut();
                                }
                            });
                } else {
                    logOut();
                } // Nothing will happen if the user hits cancel on the dialog
            }
        });

        eventBus.addHandler(WorkspaceLaunchConfigEvent.TYPE, new WorkspaceLaunchConfigEventHandler() {

            @Override
            public void onWorkspaceLaunchConfig(WorkspaceLaunchConfigEvent event) {
                int currentWorkspaces = activeWorkspaces.size();
                if ((currentWorkspaces + event.getWorkspaceStates().size()) > MAX_NUMBER_OF_USER_TABS) {
                    showMaxWorkspacesError();
                    return;
                }
                boolean load = true; //TODO loading first
                int activeWorkspacesCount = 0;
                StringBuilder workspaceNamesBuilder = new StringBuilder();
                for (WorkspaceState workspaceState : event.getWorkspaceStates()) {
                    if (activeWorkspaces.containsKey(workspaceState.getId())) {
                        activeWorkspacesCount++;
                        workspaceNamesBuilder.append(workspaceState.getName()).append(",");
                    } else {
                        restoreWorkspace(workspaceState, load);
                        workspaceStateMap.put(workspaceState.getId(), workspaceState);
                        load = false;
                    }
                }
                if (activeWorkspacesCount > 0) {
                    new MessageDialog().show(messages.activeWorkspacesTitle(activeWorkspacesCount), messages.activeWorkspacesMessage(activeWorkspacesCount, workspaceNamesBuilder.toString()), DialogType.INFO);
                }
            }
        });

        eventBus.addHandler(NewWorkspaceEvent.TYPE, new NewWorkspaceEventHandler() {
            @Override
            public void onNewWorkspace() {
                createNewWorkspace();
            }
        });

        eventBus.addHandler(CloseWorkspaceEvent.TYPE, new CloseWorkspaceEventHandler() {

            @Override
            public void closeWorkspace(final TabInfoDataType tabInfo) {
                if (TabInfoDataType.ALL_USER_TABS == tabInfo) {
                    int dirtyTabsNumber = dirtyUserTabsNumber();
                    boolean isConfirmRequired = dirtyTabsNumber > 0;
                    if (isConfirmRequired) {
                        if (activeWorkspaces.size() == 1) {
                            WorkspaceTabComponent tabComponent = activeWorkspaces.values().iterator().next();
                            if (tabComponent != null) {
                                closeOneWorkspace(tabComponent.getPresenter().getWorkspaceInfo());
                            }
                        } else {
                            showConfirmationDialog(messages.closeAllWorkspacesTitle(),
                                    messages.closeAllWorkspacesMessage(dirtyTabsNumber),
                                    new PromptOkEventHandler() {
                                @Override
                                public void onPromptOk(PromptOkEvent event) {
                                    closeAllWorkspaces();
                                }
                            });
                        }
                    } else {
                        closeAllWorkspaces();
                    }
                } else {
                    closeOneWorkspace(tabInfo);
                }
            }

            private void closeOneWorkspace(TabInfoDataType tabInfo) {
                if (tabInfo == null) {
                    return;
                }

                final String workspaceId = tabInfo.getId();
                WorkspaceTabComponent tabComponent = activeWorkspaces.get(workspaceId);
                if (tabComponent == null || !tabInfo.isUserTab()) {
                    return;
                }
                TabItem tabItem = tabComponent.getTabItem();
                if (!(tabItem instanceof WorkspaceUserTabItem)) {
                    return;
                }
                boolean isConfirmRequired = ((WorkspaceUserTabItem) tabItem).isDirty();
                if (isConfirmRequired) {
                    showConfirmationDialog(messages.closeWorkspaceTitle(), messages.closeWorkspaceMessage(),
                            new PromptOkEventHandler() {
                        @Override
                        public void onPromptOk(final PromptOkEvent event) {
                            WorkspaceManager.this.closeWorkspace(workspaceId);
                        }
                    });
                } else {
                    WorkspaceManager.this.closeWorkspace(workspaceId);
                }
            }
        });

        eventBus.addHandler(SaveWorkspaceEvent.TYPE, new SaveWorkspaceEventHandler() {

            @Override
            public void saveWorkspace(SaveWorkspaceEvent event) {
                if (activeWorkspaces.containsKey(event.getTabInfo().getId())) {
                    WorkspaceManager.this.saveWorkspace(event.getTabInfo());
                }
            }
        });

        eventBus.addHandler(SaveWorkspaceAsEvent.TYPE, new SaveWorkspaceAsEventHandler() {

            @Override
            public void saveWorkspaceAs(SaveWorkspaceAsEvent event) {
                TabInfoDataType tabInfo = event.getTabInfo();
                if (activeWorkspaces.containsKey(tabInfo.getId())) {
                    if (event.isNewWorkspaceNameDefined()) {
                        tabInfo.setName(event.getNewWorkspaceName());
                        /** Change text of tab to new workspace name **/
                        TabItem tabItem = activeWorkspaces.get(tabInfo.getId()).getTabItem();
                        tabItem.setText(tabInfo.getName());
                        /** Save workspace state **/
                        saveWorkspace(tabInfo);
                    } else {
                        launchSaveAsDialog(tabInfo);
                    }
                }
            }
        });

        eventBus.addHandler(WorkspaceDataSaveRequestEvent.TYPE, new WorkspaceDataSaveRequestEventHandler() {

            @Override
            public void onWorkspaceSaveRequest(WorkspaceDataSaveRequestEvent event) {
                saveWorkspaceData(event.getWorkspaceStates(), event.getStartupItems(), event.isRenamingWorkspaces());
            }
        });

        Window.addWindowClosingHandler(closingHandler);
    }

    /** @deprecated hack for KPI, do not reuse this  purlease
     */
    @Deprecated
    public WorkspacePresenter getActiveWorkspacePresenter() {
        return activeWorkspacePresenter;
    }

    private class ClosingHandlerImpl implements Window.ClosingHandler {

        private boolean isLoggingOut = false;

        public void setIsLoggingOut(boolean isLoggingOut) {
            this.isLoggingOut = isLoggingOut;
        }

        @Override
        public void onWindowClosing(final Window.ClosingEvent event) {
            int dirtyTabsNumber = dirtyUserTabsNumber();
            // Do not set a browser message for log out if there are no tabs dirty
            // or if the user has chosen the log out menu option
            if(dirtyTabsNumber != 0 && !isLoggingOut) {
                event.setMessage(messages.browserLogout(dirtyTabsNumber));
            }
        }
    }

    /**
     * Launch Save As Dialog
     *
     * @param tabInfo tab info
     */
    private void launchSaveAsDialog(TabInfoDataType tabInfo) {
        WorkspaceSaveAsPresenter workspaceSaveAsPresenter = workspaceSaveAsPresenterProvider.get();
        workspaceSaveAsPresenter.launch(workspaceStateMap, tabInfo);
    }

    public void init(IWorkspaceContainer workspaceContainer) {
        this.workspaceContainer = workspaceContainer;
        // Assign a default tab selection strategy
        this.tabSelector = SafariTabSelector.DEFAULT_SAFARI_TAB_SELECTOR;
        
        //only initWorkspaces if the user has the required roles...
        if (isGrantedWorkspaceAccess()){
            initWorkspaces();
        }
    }

    /**
     * Create tabs based on previously saved user preferences, or if no previously saved workspaces, 
     * create a default one ('New workspace 1'). 
     */
    private void initWorkspaces() {
        AllWorkspacesState workspacesState;
        List<WorkspaceState> workspaceStateList = new ArrayList<WorkspaceState>();

        if ((workspacesState = userPreferencesHelper.getStateById(WORKSPACE_MANAGEMENT_ID, AllWorkspacesState.class)) != null) {
            workspaceStateList.addAll(workspacesState.getWorkspaces());
            startupItems.addAll(WorkspaceUtils.getCollectionWithNullCheck(workspacesState.getStartUpItems()));
        }
        /** Need to check both saved workspace list and the predefined workspace list for "launch on startup" workspaces. **/
        workspaceStateList.addAll(configService.getPredefinedWorkspaces());

        /** TODO loading first at the moment, save last selected tab in future and load that **/
        boolean atLeastOneWorkspaceRestored = false;

        if (!workspaceStateList.isEmpty()) {
            boolean selected = true;
            for (final WorkspaceState workspaceState : workspaceStateList) {
                if (WorkspaceUtils.isStartupItem(startupItems, workspaceState)) {
                    restoreWorkspace(workspaceState, selected);
                    selected = false;
                    atLeastOneWorkspaceRestored = true;
                }
                workspaceStateMap.put(workspaceState.getId(), workspaceState);
            }
        }

        // If atLeastOneWorkspaceRestored = false, we have no workspaces to restore at startup, create 'New workspace 1'
        if (!atLeastOneWorkspaceRestored) {
            createNewWorkspace();
        }
        createPlusTab();
        adjustPlusTabState();
    }


    private void restoreWorkspace(WorkspaceState workspaceState, boolean load) {
        final WorkspacePresenter presenter = workspacePresenterProvider.get();
        final WorkspaceState grantedWorkspaceState = removeWindowsFromWorkspace(workspaceState);
        /** Launch the isSelected workspace **/
        final TabItem tabItem = createWorkspaceTab(presenter, workspaceState.getName(), workspaceState.getId(), false);

        /* Only relaunch workspace when selected and do it once **/
        tabItem.addListener(Events.Select, new SelectionListener<TabPanelEvent>() {
            @Override
            public void componentSelected(TabPanelEvent tpe) {

                tabItem.removeListener(Events.Select, this);
                /* Defer to next event loop to ensure tab is rendered with height etc, as positioning of launch dialog will require this **/
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                    @Override
                    public void execute() {
                        presenter.restoreWorkspace(grantedWorkspaceState);
                    }
                });

            }
        });

        if (load) {
            tabSelector.selectTabOnCreate(workspaceContainer.getTabContainer(), workspaceState.getId());
        }
    }

    /**
     * Remove Workspace states that are not granted access. i.e. if a user created the workspace as Terminal
     * Specialist, but they were later changed to Network Monitoring. The user should now only be able to open
     * windows that are part of the Network Monitoring role
     * @param workspaceState
     * @return
     */
    private WorkspaceState removeWindowsFromWorkspace(final WorkspaceState workspaceState){
        List<WindowState> grantedWindowStates = new ArrayList<WindowState>(0);

        for(WindowState windowState: workspaceState.getWindows()){
            if(isGrantedAccess(windowState.getWindowId())){
                grantedWindowStates.add(windowState);
            }
        }
        workspaceState.setWindows(grantedWindowStates);
        return workspaceState;
    }


    /**
     * Checks if a user is granted access to window with windowId.
     * @param windowId
     * @return
     */
    private boolean isGrantedAccess(final String windowId) {
        boolean result = false;
        List<IWindow> allWindows = workspaceConfiguration.getWindows().getWindow();

        IWindow window = null;
        for (IWindow win : allWindows) {
            if (win.getId().equalsIgnoreCase(windowId)) {
                window = win;
                break;
            }
        }

        if (window == null) {
            result = false;
        } else {
            List<String> windowAccessGroups = window.getSupportedAccessGroups().getAccessGroup();

            for (String accessGroup : windowAccessGroups) {
                if (supportedAccessGroups.contains(accessGroup)) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * This is determines if the user has access to the Workspace.
     * @return true/false
     */
    private boolean isGrantedWorkspaceAccess(){
        boolean result = false;
        List<String> requiredGroups = new ArrayList<String>();
        requiredGroups.add("NETWORK_TAB");
        requiredGroups.add("RANKINGS_TAB");
        requiredGroups.add("TERMINAL_TAB");
        requiredGroups.add("SUBSCRIBER_TAB");

        for (String accessGroup : requiredGroups) {
            if (supportedAccessGroups.contains(accessGroup)) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    private void createNewWorkspace() {
        if (activeWorkspaces.size() < MAX_NUMBER_OF_USER_TABS) {
            String workspaceName = createDefaultWorkspaceName();
            createWorkspaceTab(workspacePresenterProvider.get(), workspaceName, createTabId(workspaceName), true);
        } else {
            showMaxWorkspacesError();
        }
        adjustPlusTabState();
    }

    private native void logOut() /*-{
        $wnd.onLogout();
    }-*/;

    private int dirtyUserTabsNumber() {
        int dirtyTabsNumber = 0;
        for (WorkspaceTabComponent tabComponent : activeWorkspaces.values()) {
            if (tabComponent == null) {
                continue;
            }
            TabItem tabItem = tabComponent.getTabItem();
            if (!(tabItem instanceof WorkspaceUserTabItem)) {
                continue;
            }
            if (((WorkspaceUserTabItem) tabItem).isDirty()) {
                dirtyTabsNumber++;
            }
        }
        return dirtyTabsNumber;
    }

    private void closeAllWorkspaces() {
        Set<String> tabsIdSet = activeWorkspaces.keySet();
        if (tabsIdSet.size() == 0) {
            return;
        }
        String[] tabsIds = tabsIdSet.toArray(new String[tabsIdSet.size()]);
        for (String workspaceId : tabsIds) {
            this.closeWorkspace(workspaceId);
        }
    }

    private void closeWorkspace(String workspaceId) {
        if (!activeWorkspaces.containsKey(workspaceId)) {
            return;
        }

        TabPanel tabPanel = workspaceContainer.getTabContainer();
        tabSelector.selectTabOnRemove(tabPanel, workspaceId);

        WorkspaceTabComponent workspaceTabComponent = activeWorkspaces.remove(workspaceId);
        workspaceTabComponent.getPresenter().unbind();
        tabPanel.remove(workspaceTabComponent.getTabItem());
        adjustPlusTabState();
    }

    /**
     * Try and prevent repeating default names - will occur if user saves workspaces without giving them a user defined name
     *
     * @return workspace name
     */
    private String createDefaultWorkspaceName() {
        String workspaceName;
        int workspaceCount = 0;
        /** Get names of both active and inactive (saved but not open) workspaces **/
        List<String> workspaceNames = new ArrayList<String>();
        for (WorkspaceState state : workspaceStateMap.values()) {
            workspaceNames.add(state.getName());
        }
        for (WorkspaceTabComponent activeWorkspace : activeWorkspaces.values()) {
            workspaceNames.add(activeWorkspace.getPresenter().getWorkspaceInfo().getName());
        }
        while (workspaceNames.contains(workspaceName = messages.newWorkspace(++workspaceCount))) {
        }
        return workspaceName;
    }

    void showMaxWorkspacesError() {
        new MessageDialog().show(messages.maxWorkspacesTitle(), messages.maxWorkspacesMessage(MAX_NUMBER_OF_USER_TABS),
                DialogType.ERROR);
    }

    private TabItem createWorkspaceTab(final WorkspacePresenter presenter, final String workspaceName,
            final String workspaceId, boolean selected) {
        String time = DateTimeFormat.getFormat(CommonConstants.DATE_MINUTE_FORMAT).format(new Date());
        String tip = "Loaded as \"" + workspaceName + "\" at " + time;
        TabInfoDataType dataType = new TabInfoDataType(workspaceId, workspaceName, tip, "", "centerPanelBackground",
                true, true);
        dataType.setUserTab(true);

        TabItem tabItem = workspaceContainer.addWorkspaceTab(dataType);

        tabItem.add(presenter.getView());
        activeWorkspaces.put(workspaceId, new WorkspaceTabComponent(presenter, tabItem));
        tabItem.addListener(Events.Select, new SelectionListener<TabPanelEvent>() {
            @Override
            public void componentSelected(TabPanelEvent tpe) {
                activeWorkspacePresenter = activeWorkspaces.get(workspaceId).getPresenter();
            }
        });
        presenter.init(dataType);

        if (selected) {
            tabSelector.selectTabOnCreate(workspaceContainer.getTabContainer(), workspaceId);
        }

        return tabItem;
    }

   private String createTabId(String workspaceName) {
        String tabIdPrefix = generateTabIdPrefix(workspaceName);

        String tabId;
        int index = 0;
        // Create unique Tab Ids so check DB and active workspaces to prevent duplicates.
        // TODO can we just generate a unique id without having to do this as per highcharts
        Set<String> workspaceIds = new HashSet<String>(workspaceStateMap.keySet());
        workspaceIds.addAll(activeWorkspaces.keySet());
        while (workspaceIds.contains(tabId = generateTabId(tabIdPrefix, ++index)) || PLUS_TAB_ID.equals(tabId)) { //
        // id should not be the same as for plus tab
        }

        return tabId;
    }

    private void createPlusTab() {
        String tip = "Click here to add a user workspace tab.<br>" + "You can have up to " + MAX_NUMBER_OF_USER_TABS
                + " user tabs.";
        TabInfoDataType dataType = new TabInfoDataType(PLUS_TAB_ID, null, tip, "", "centerPanelBackground", true, true);
        dataType.setPlusTab(true);

        plusTabItem = workspaceContainer.addWorkspaceTab(dataType);
        plusTabItem.addListener(Events.Select, new SelectionListener<TabPanelEvent>() {
            @Override
            public void componentSelected(TabPanelEvent tpe) {
                eventBus.fireEvent(new NewWorkspaceEvent());
            }
        });
    }

    private void adjustPlusTabState() {
        if (plusTabItem != null) {
            boolean isAddAllowed = activeWorkspaces.size() < MAX_NUMBER_OF_USER_TABS;
            if (isAddAllowed != plusTabItem.isEnabled()) {
                plusTabItem.setEnabled(isAddAllowed);
            }
        }
    }

    /**
     * @param workspacesToSave   - collection of workspaces to save
     * @param renamingWorkspaces - flag to indicate if this save of workspace data involves renaming or workspaces  in order to update the names of currently
     *                           active workspaces.
     */
    private void saveWorkspaceData(List<WorkspaceState> workspacesToSave, List<String> startupItems, boolean renamingWorkspaces) {
        /** Need to save all workspaces as its just one blob of JSON **/
        AllWorkspacesState allWorkspacesData = factory.workspacesState().as();
        allWorkspacesData.setWorkspaces(workspacesToSave);
        allWorkspacesData.setStartupItems(startupItems);
        userPreferencesHelper.setState(WORKSPACE_MANAGEMENT_ID, AllWorkspacesState.class, allWorkspacesData);

        /** Refresh any components depending on latest workspace state **/
        this.workspaceStateMap.clear();
        this.startupItems.clear();
        this.startupItems.addAll(WorkspaceUtils.getCollectionWithNullCheck(startupItems));
        Collection<WorkspaceState> workspaceList = userPreferencesHelper.getStateById(WORKSPACE_MANAGEMENT_ID,
                AllWorkspacesState.class).getWorkspaces();
        for (WorkspaceState state : workspaceList) {
            this.workspaceStateMap.put(state.getId(), state);
            if (renamingWorkspaces) {
                if (activeWorkspaces.containsKey(state.getId())) {
                    TabItem tabItem = activeWorkspaces.get(state.getId()).getTabItem();
                    tabItem.setText(state.getName());
                }
            }
        }
        eventBus.fireEvent(new WorkspaceDataSaveCompleteEvent());
    }

    private void saveWorkspace(TabInfoDataType tabData) {
        WorkspacePresenter presenter = activeWorkspaces.get(tabData.getId()).getPresenter();
        WorkspaceState workspaceState = presenter.getWorkspaceData();
        /**
         * No matter whether we save a predefined or a user defined workspace, once it is saved by the user it is now
         * user defined.
         */


        Collection<WorkspaceState> states =  this.configService.getPredefinedWorkspaces();
        for(WorkspaceState predefinedWorkspaceState : states)  {
            if(workspaceState.getId().equals(predefinedWorkspaceState.getId())) {
                    workspaceState.setId(WorkspaceUtils.generateId());
            }
        }

        workspaceState.setWorkspaceType(DefinedWorkspaceType.USER_DEFINED.toString());
        workspaceState.setName(tabData.getName());
        /** If we already have state for this workspace remove it and add the new state object **/
        workspaceStateMap.put(tabData.getId(), workspaceState);

        ArrayList<WorkspaceState> saveList = new ArrayList<WorkspaceState>();
        if(!workspaceStateMap.isEmpty()){
            for(WorkspaceState state : workspaceStateMap.values()){
                if(DefinedWorkspaceType.USER_DEFINED == DefinedWorkspaceType.fromString(state.getWorkspaceType())) {
                    saveList.add(state);
                }
            }
        }
        saveWorkspaceData(saveList, startupItems, false);
        int windowCount = workspaceState.getWindows() != null ? workspaceState.getWindows().size() : -1;
        eventBus.fireEvent(new WorkspaceStatusChangeEvent(tabData.getId(), windowCount, false));
    }


    private void showConfirmationDialog(String title, String message,
            PromptOkEventHandler promptOkEventHandler) {
        showConfirmationDialog(title, message, promptOkEventHandler, null);
    }

    private void showConfirmationDialog(String title, String message,
            PromptOkEventHandler okEventHandler,
            PromptCancelEventHandler promptCancelEventHandler) {

        final PromptDialog confirmDialog = new PromptDialog();
        confirmDialog.setGlassEnabled(true);
        confirmDialog.addOkEventHandler(okEventHandler);
        if(promptCancelEventHandler != null) {
            confirmDialog.addCancelEventHandler(promptCancelEventHandler);
        }
        confirmDialog.show(title, message, DialogType.WARNING);
    }


}
