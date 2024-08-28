/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.main;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ericsson.eniq.events.ui.client.common.UIComponentFactory;
import com.ericsson.eniq.events.ui.client.datatype.TabInfoDataType;
import com.ericsson.eniq.events.ui.client.workspace.IWorkspaceContainer;
import com.ericsson.eniq.events.ui.client.workspace.tab.PlusTabItem;
import com.ericsson.eniq.events.ui.client.workspace.tab.WorkspaceOptionsMenu;
import com.ericsson.eniq.events.ui.client.workspace.tab.WorkspaceTabsResourceBundle;
import com.ericsson.eniq.events.ui.client.workspace.tab.WorkspaceUserTabItem;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author ealeerm
 * @since 06/2012
 */
class TabbedWorkspaceContainer implements IWorkspaceContainer {

    final IEniqEventsModuleRegistry moduleRegistry;

    final TabPanel tabContainer;

    final UIComponentFactory componentFactory;

    final EventBus eventBus;

    private static final WorkspaceTabsResourceBundle resources;

    static {
        resources = GWT.create(WorkspaceTabsResourceBundle.class);
        resources.css().ensureInjected();
    }

    /* cache presenters for meta data change - excludes dashboard tab(s) */
    final Map<String, GenericTabPresenter> currentTabPresenters = new HashMap<String, GenericTabPresenter>();

    /* map so only display tab icon when node is selected */
    final Map<String, String> tabStyleMap = new HashMap<String, String>();

    private TabItem plusTabItem = null;

    public TabbedWorkspaceContainer(IEniqEventsModuleRegistry moduleRegistry, TabPanel tabContainer,
            UIComponentFactory componentFactory, EventBus eventBus) {
        this.moduleRegistry = moduleRegistry;
        this.tabContainer = tabContainer;
        this.componentFactory = componentFactory;
        this.eventBus = eventBus;
    }

    public Collection<GenericTabPresenter> getCurrentTabPresenters() {
        return currentTabPresenters.values();
    }

    @Override
    public TabPanel getTabContainer() {
        return tabContainer;
    }

    @Override
    public TabItem addWorkspaceTab(TabInfoDataType tabInfo) {
        if (tabInfo == null || tabInfo.isPlusTab() && plusTabItem != null) {
            return null;
        }

        TabItem tabItem = createTab(tabInfo);
        TabPanel container = getTabContainer();
        if (tabInfo.isPlusTab()) {
            if (container.add(tabItem)) { // adding plus tab
                plusTabItem = tabItem;
            }
        } else { // regular tab
            if (plusTabItem != null) {
                // Plus tab has to be removed and added again because of index consistency in the internal AccessStack of TabPanel
                container.remove(plusTabItem);
                container.add(tabItem);
                container.add(plusTabItem);
            } else {
                container.add(tabItem);
            }
        }
        return tabItem;
    }

    TabItem createTab(TabInfoDataType tabInfo) {
        TabItem tabItem = createTabItem(tabInfo);
        tabItem.setEnabled(false);
        tabItem.setId(tabInfo.getId());
        tabItem.setEnabled(tabInfo.isRoleEnabled());
        tabItem.setHideMode(Style.HideMode.OFFSETS); // Changed to ensure windows update correctly when item not in focus (HM92271)

        if (!tabInfo.isUserTab() && !tabInfo.isPlusTab()) {
            tabStyleMap.put(tabInfo.getId(), tabInfo.getStyle());
        }
        tabItem.setStyleName(tabInfo.getTabItemCenterStyle());

        return tabItem;
    }

    /* Method extracted for junit test*/
    TabItem createTabItem(final TabInfoDataType tabInfo) {
        TabItem item;
        if (tabInfo.isPlusTab()) {
            item = new PlusTabItem(tabInfo.getName(), resources.css().plusTab(),
                    resources.css().plusTabIcon(), resources.css().plusTabDisabled());
        } else {
            if (tabInfo.isUserTab()) {
                final WorkspaceOptionsMenu optionsMenu = new WorkspaceOptionsMenuBuilder(tabInfo, eventBus).invoke();
                item = new WorkspaceUserTabItem(tabInfo, optionsMenu, eventBus, resources);
            } else {
                item = new TabItem(tabInfo.getName());
            }
        }
        return item;
    }

    /*
    * Method extracted for junit test
    */
    SelectionListener<TabPanelEvent> createTabSelectionListener(TabItem tabItem) {
        return new SetupTabSectionListener(tabItem, moduleRegistry, componentFactory, eventBus, currentTabPresenters,
                tabStyleMap);
    }
}
