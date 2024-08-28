package com.ericsson.eniq.events.ui.client.common;

import com.ericsson.eniq.events.ui.client.common.comp.MenuTaskBar;
import com.ericsson.eniq.events.ui.client.common.service.TabManager;
import com.ericsson.eniq.events.ui.client.common.service.WindowManager;
import com.ericsson.eniq.events.ui.client.main.GenericTabView;
import com.ericsson.eniq.events.ui.client.search.GroupSingleToggleComponent;
import com.ericsson.eniq.events.ui.client.search.ISearchComponent;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;

/**
 * @author edmibuz
 */
public class UIComponentFactoryImpl implements UIComponentFactory {

    private final TabManager tabManager;
    private final EventBus eventBus;

    @Inject
    public UIComponentFactoryImpl(final TabManager tabManager,
                                  final EventBus eventBus) {
        this.tabManager = tabManager;
        this.eventBus = eventBus;
    }
    
    @Override
    public MenuTaskBar createMenuTaskBar(final GenericTabView parentView, 
                                         final String tabId, 
                                         final ISearchComponent searchComp, 
                                         final ISearchComponent groupSelectComp, 
                                         final GroupSingleToggleComponent groupSingleToggler, 
                                         final String defaultMenu) {

        final WindowManager windowManager = tabManager.getWindowManager(tabId);

        final MenuTaskBar menuTaskBar = new MenuTaskBar(parentView, tabId, searchComp, groupSelectComp, groupSingleToggler, defaultMenu);
        menuTaskBar.setWindowManager(windowManager);
        menuTaskBar.setEventBus(eventBus);
        
        return menuTaskBar;
    }

    @Override
    public GenericTabView createGenericTabView(final String tabId, final TabItem tabItem) {
        final GenericTabView genericTabView = new GenericTabView();
        genericTabView.setEventBus(eventBus);
        genericTabView.init(tabId, tabItem);
        return genericTabView;
    }
}
