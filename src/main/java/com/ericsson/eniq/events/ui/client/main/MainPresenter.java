/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.main;

import com.ericsson.eniq.events.common.client.PerformanceUtil;
import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.ui.client.common.UIComponentFactory;
import com.ericsson.eniq.events.ui.client.datatype.LicenseInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.TabInfoDataType;
import com.ericsson.eniq.events.ui.client.events.MetaDataReadyEvent;
import com.ericsson.eniq.events.ui.client.events.MetaDataReadyEventHandler;
import com.ericsson.eniq.events.ui.client.events.SetupLicensesEvent;
import com.ericsson.eniq.events.ui.client.events.WindowToolbarEvent;
import com.ericsson.eniq.events.ui.client.events.handlers.WindowToolBarItemEvent;
import com.ericsson.eniq.events.ui.client.events.tab.*;
import com.ericsson.eniq.events.ui.client.gin.EniqEventsUIGinjector;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.ui.client.northpanel.NorthPanelPresenter;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceManager;
import com.ericsson.eniq.events.ui.client.workspace.events.GroupPopupChangeEvent;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ericsson.eniq.events.ui.client.common.Constants.NO_LICENSE_FOUND_TO_DISPLAY_ROLE_BASED;
import static com.ericsson.eniq.events.ui.client.common.Constants.NO_LICENSE_MESSAGE_TITLE;

/**
 * Main presenter class.
 * <p/>
 * Construct all tab content via meta data. Use injection - see gin,
 * and Model View Presenter pattern.
 * The job here is to initially load tab content (and menu items)
 * via meta data after select the tab.
 * <p/>
 * JsonObjectWrapper is fetched from server call at start up, i.e.
 * so need to see that metadata has returned before can build up the
 * display so we use the event bus to notify us when ready
 *
 * @author eeicmsy
 * @see MetaDataReadyEventHandler
 * @since Feb 2010
 */
public class MainPresenter extends BasePresenter<IMainView> implements MetaDataReadyEventHandler {

    /* Map of all static and dynamic tab items populated on start up */
    private final Map<String, TabInfoDataType> tabMetaDataMap = new HashMap<String, TabInfoDataType>();

    final TabbedWorkspaceContainer tabbedWsContainer;

    private final EniqEventsUIGinjector injector = MainEntryPoint.getInjector();

    private final IEniqEventsModuleRegistry moduleRegistry;

    private final NorthPanelPresenter northPanelPresenter;

    /**
     * Constructor initiating MVP objects via GIN  (inject annotation).
     * <p/>
     * Forces {@link com.ericsson.eniq.events.ui.client.common.MetaReader} to make its
     * initial server call such that it writes to event bus when meta data is
     * ready. Initialises the event Bus Handlers.
     *
     * @param mainDisplay Main view abstraction in MVP pattern
     * @param eventBus    Event bus (singleton) required by base class
     */
    @Inject
    public MainPresenter(final IMainView mainDisplay, final EventBus eventBus,
            final UIComponentFactory componentFactory, final IEniqEventsModuleRegistry moduleRegistry,
            final NorthPanelPresenter northPanelPresenter) {

        super(mainDisplay, eventBus);
        this.moduleRegistry = moduleRegistry;
        this.tabbedWsContainer = new TabbedWorkspaceContainer(moduleRegistry, mainDisplay.getContainerTab(),
                componentFactory, eventBus);
        this.northPanelPresenter = northPanelPresenter;

        initEventBusHandlers();
        loadMetaDataFromServer();
    }

    @Override
    public final void handleMetaDataReadyEvent() {
        final List<LicenseInfoDataType> licenses = injector.getMetaReader().getLicenses();
        final SetupLicensesEvent licensesEvent = new SetupLicensesEvent(licenses);
        getEventBus().fireEvent(licensesEvent);
        if (TabViewRegistry.get().isEmpty()) {
            bind(); // so calls #onBind when meta data ready
        } else {
            /* e.g. packet switch to circuit switched functionality - changing all menu items *///TODO do we need this now
            PerformanceUtil.getSharedInstance().clear("TabItem");
            for (final GenericTabPresenter tabPresenter : tabbedWsContainer.getCurrentTabPresenters()) {
                tabPresenter.removeAllListeners();
                tabPresenter.onMetaDataUpDate();
                tabPresenter.addGenericListenersToMenuItems(); // with new meta data
            }
            PerformanceUtil.getSharedInstance().logTimeTaken("Time taken in populating tab items : ", "TabItem");
        }
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                MainEntryPoint.getInjector().getKpiPanelPresenter();
            }

            @Override
            public void onFailure(final Throwable reason) {
                Window.alert("code splitting failed..");
            }
        });
        PerformanceUtil.getSharedInstance().logCurrentTime("handleMetaDataReadyEvent  finish : ");
    }

    @Override
    protected void onBind() {
        PerformanceUtil.getSharedInstance().clear("MainPresenterOnBind");
        getView().getContainerTab().addListener(Events.Select, new Listener<TabPanelEvent>() {

            @Override
            public void handleEvent(final TabPanelEvent tabPanelEvent) {
                final TabItem item = tabPanelEvent.getItem();

                getEventBus().fireEvent(new GroupPopupChangeEvent(item.getId()));
                getEventBus().fireEvent(new TabChangeEvent(item.getId()));
            }
        });

        /* Unhide the Options menu */
        northPanelPresenter.getView().setVisible(true);

        final List<TabInfoDataType> tabsMetaData = getTabDataMetaInfoFromMetaReader();
        for (final TabInfoDataType tabInfo : tabsMetaData) {
            tabMetaDataMap.put(tabInfo.getId(), tabInfo);
            /** Dont create a tab for a module if it is not in the registry, even though it may be configured in meta
             * data. Allow to exclude tabs in dev mode etc.
             */
            if (tabInfo.isModule() && !moduleRegistry.containsModule(tabInfo.getId())) {
                continue;
            }
            final TabItem tabItem = tabbedWsContainer.addWorkspaceTab(tabInfo);
            /* the listeners job is to load the panel data which will delay until tab is selected */
            tabItem.addListener(Events.Select, tabbedWsContainer.createTabSelectionListener(tabItem));
        }
        setDefaultTabOnLogin(getView().getContainerTab(), tabsMetaData);
        removeDisabledTabs(getView().getContainerTab(), tabsMetaData);

        final WorkspaceManager workspaceManager = injector.getWorkspaceManager();
        workspaceManager.init(tabbedWsContainer);

        //remove the loading mask from login once everything loaded
        getView().stopProcessing();
        PerformanceUtil.getSharedInstance().logTimeTaken("Time taken in creating workspace : ", "MainPresenterOnBind");
    }

    private void setDefaultTabOnLogin(final TabPanel tabPanel, final List<TabInfoDataType> tabsMetaData) {
        /*
        * To make sure that the default tab on login is role enabled we walk through tabs till the first isRoleEnabled
        */
        boolean isFound = false;
        for (final TabInfoDataType tabInfo : tabsMetaData) {
            final TabItem currentTab = tabPanel.findItem(tabInfo.getId(), false);
            final boolean isRoleEnabled = tabInfo.isRoleEnabled();
            if (isRoleEnabled) {
                tabPanel.setSelection(currentTab);
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            MessageDialog.get().show(NO_LICENSE_MESSAGE_TITLE, NO_LICENSE_FOUND_TO_DISPLAY_ROLE_BASED,
                    MessageDialog.DialogType.WARNING);
        }
    }

    private void removeDisabledTabs(final TabPanel tabPanel, final List<TabInfoDataType> tabsMetaData) {
        for (final TabInfoDataType tabInfo : tabsMetaData) {
            final TabItem currentTab = tabPanel.findItem(tabInfo.getId(), false);
            final boolean isRoleEnabled = tabInfo.isRoleEnabled();
            if(!isRoleEnabled ){
                tabPanel.remove(currentTab);
            }
        }
    }
    /*
    * (exposed for junit overide)
    * @return true if any open windows exists
    */
    boolean openSearchFieldWindowsExist() {
        for (final GenericTabPresenter tabPresenter : tabbedWsContainer.getCurrentTabPresenters()) {
            if (tabPresenter.containsOpenSearchFieldUserWindows()) {
                return true;
            }
        }
        return false;
    }

    /*
    * initialises all the handlers that the event Bus
    * must be aware of - This should only be called once
    * on application startup
    */
    private void initEventBusHandlers() {
        final EventBus eventBus = getEventBus();

        registerHandler(eventBus.addHandler(WindowToolbarEvent.TYPE, new WindowToolBarItemEvent()));
        registerHandler(eventBus.addHandler(MetaDataReadyEvent.TYPE, this));
        registerHandler(eventBus.addHandler(TabAddEvent.TYPE, new TabAddEventHandler() {

            @Override
            public void onTabAdd(final TabAddEvent tabAddEvent) {
                TabItem tabItem;
                /** If its a dynamic tab item it wont have been added to the tab panel yet so add it **/
                final String tabId = tabAddEvent.getTabId();

                if ((tabItem = tabbedWsContainer.getTabContainer().getItemByItemId(tabId)) == null) {
                    final TabInfoDataType tabInfoDataType = tabMetaDataMap.get(tabId);
                    System.out.println(tabInfoDataType);
                    tabbedWsContainer.addWorkspaceTab(tabInfoDataType);
                }

                if (tabItem != null) {
                    tabItem.getTabPanel().setSelection(tabItem);
                }
            }

        }));
        registerHandler(eventBus.addHandler(TabRemoveEvent.TYPE, new TabRemoveEventHandler() {

            @Override
            public void onTabRemove(final TabRemoveEvent tabRemoveEvent) {
                /** Remove tab from panel and registry **/
                final String tabId = tabRemoveEvent.getTabId();
                if (TabViewRegistry.get().containsTabView(tabId)) {
                    final IGenericTabView tabView = TabViewRegistry.get().getTabView(tabId);
                    final TabItem tabItem = tabView.getTabItem();
                    tabbedWsContainer.getTabContainer().remove(tabItem);
                    TabViewRegistry.get().removeTabView(tabId);
                }
            }
        }));

        registerHandler(eventBus.addHandler(TabSelectEvent.TYPE, new TabSelectEventHandler() {
            @Override
            public void onSelect(final String itemId) {
                final TabPanel containerTab = tabbedWsContainer.getTabContainer();
                final TabItem tabItem = containerTab.getItemByItemId(itemId);
                tabItem.getTabPanel().setSelection(tabItem);
            }
        }));

    }

    /*
    * Method extracted for junit test
    */
    List<TabInfoDataType> getTabDataMetaInfoFromMetaReader() {
        return injector.getMetaReader().getTabDataMetaInfo();
    }

    /*
    *  Method extracted for junit test
    *  Method results in a server call
    *  (when finished thread you get to #handleMetaDataReadyEvent)
    */
    void loadMetaDataFromServer() {
        injector.getMetaReader().loadMetaData();
        injector.getUserPreferencesReader().loadUserPreferences();
    }

}
