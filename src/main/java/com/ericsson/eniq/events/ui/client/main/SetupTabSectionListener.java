/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.main;

import com.ericsson.eniq.events.common.client.PerformanceUtil;
import com.ericsson.eniq.events.ui.client.common.UIComponentFactory;
import com.ericsson.eniq.events.ui.client.events.tab.TabAddCompleteEvent;
import com.ericsson.eniq.events.ui.client.gin.EniqEventsUIGinjector;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.google.gwt.core.client.Scheduler;
import com.google.web.bindery.event.shared.EventBus;

import java.util.Map;

import static com.ericsson.eniq.events.ui.client.common.CSSConstants.NO_LICENCE_STYLE;

/**
 * Main Tab selection listener which lays out initial panel (add widgits).
 * This the content of the action only is to occur on initial press
 * <p/>
 * Also use to distinguish icon selection using "selected icon" when tab selected
 */
class SetupTabSectionListener extends SelectionListener<TabPanelEvent> {

    private final TabItem tabItem;

    private final IEniqEventsModuleRegistry moduleRegistry;

    private final UIComponentFactory componentFactory;

    final EventBus eventBus;

    final Map<String, String> tabStyleMap;

    private final Map<String, GenericTabPresenter> currentTabPresenters;

    private final EniqEventsUIGinjector injector = MainEntryPoint.getInjector();

    public SetupTabSectionListener(final TabItem tabItem, final IEniqEventsModuleRegistry moduleRegistry,
            final UIComponentFactory componentFactory, final EventBus eventBus,
            final Map<String, GenericTabPresenter> currentTabPresenters, final Map<String, String> tabStyleMap) {
        this.tabItem = tabItem;
        this.moduleRegistry = moduleRegistry;
        this.componentFactory = componentFactory;
        this.eventBus = eventBus;
        this.currentTabPresenters = currentTabPresenters;
        this.tabStyleMap = tabStyleMap;
    }

    /*
    * Tab selection - perform adding widgets if not entered tab before
    */
    @Override
    public void componentSelected(final TabPanelEvent tabPanelEvent) {
        PerformanceUtil.getSharedInstance().clear("TabSelected");
        final TabItem item = tabPanelEvent.getItem();
        final TabItem.HeaderItem tabHeader = item.getHeader();
        final String tabId = item.getId();

        GenericTabPresenter genericPresenter = null;
        if (!TabViewRegistry.get().containsTabView(tabId)) {

            if (tabItem.isEnabled()) { // role enabled

                /* pass Ids in place of names so we can localise names.
                * Not using GIN in favour of meta data- create view and presenter here */
                final GenericTabView genericView = componentFactory.createGenericTabView(tabId, tabItem);
                genericPresenter = createGenericTabPresenter(genericView);

                tabItem.add(genericView.asWidget());

                TabViewRegistry.get().addTabView(tabId, genericView);

                /* only add meta data handlers */
                if (genericView.requiresMetaDataUpdate()) {
                    currentTabPresenters.put(tabId, genericPresenter);
                }

                //TODO Move Reports Tab to module at a later date
                if (tabItem.isEnabled() && genericPresenter != null && "BUSINESS_OBJECTS_TAB".equals(tabId)) {
                    injector.getBusinessObjectsPresenter().init("BUSINESS_OBJECTS_TAB");
                }
            } else {
                // not sure what is going on but when all 4 tabs have no
                // licence loosing the style on the network tab
                tabHeader.setStyleName(NO_LICENCE_STYLE);
            }
            tabItem.layout();

            // TODO put dashboard and session browser into the above tabcontent registry and launch from there
            // after tabItem layout - launch dashboard if licenced
            if (tabItem.isEnabled() && genericPresenter != null) {
                genericPresenter.checkAndLaunchDashBoard();
            }

            if (tabItem.isEnabled() && genericPresenter != null && moduleRegistry.containsModule(tabId)) {
                genericPresenter.checkAndLaunchCustomView(moduleRegistry.getModule(tabId).getModuleView());
            }

            /** Notify listeners that tab is created with views etc. Do it in next event loop due to ongoing registration of required handlers in
             * this loop **/
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    eventBus.fireEvent(new TabAddCompleteEvent(tabId));
                }
            });
        }

        resetSelectedTabIcon(tabId, tabHeader);
        PerformanceUtil.getSharedInstance().logTimeTaken("Time taken to switch " + tabId + " : ", "TabSelected");
    }

    /* only place an icon on selected tab */
    private void resetSelectedTabIcon(final String tabId, final TabItem.HeaderItem selectedTabHeader) {
        for (final IGenericTabView tabView : TabViewRegistry.get().getAllTabViews()) {
            tabView.getTabItem().getHeader().setIconStyle(null);
        }
        selectedTabHeader.setIconStyle(tabStyleMap.get(tabId));
    }

    /* junit */
    GenericTabPresenter createGenericTabPresenter(final GenericTabView genericView) {
        return new GenericTabPresenter(genericView, eventBus);
    }

}
