package com.ericsson.eniq.events.ui.client.common;

import com.ericsson.eniq.events.ui.client.common.comp.MenuTaskBar;
import com.ericsson.eniq.events.ui.client.main.GenericTabView;
import com.ericsson.eniq.events.ui.client.search.GroupSingleToggleComponent;
import com.ericsson.eniq.events.ui.client.search.ISearchComponent;
import com.extjs.gxt.ui.client.widget.TabItem;

/**
 * Generic widget factory, which encapsulates all creation logic of common UI components.
 *
 * Responsible for validation of input parameters, population of default attributes and wiring dependencies.
 *
 * @author edmibuz
 */
public interface UIComponentFactory {

    MenuTaskBar createMenuTaskBar(final GenericTabView parentView,
                                  final String tabId,
                                  final ISearchComponent searchComp,
                                  final ISearchComponent groupSelectComp,
                                  final GroupSingleToggleComponent groupSingleToggler,
                                  final String defaultMenu);

    GenericTabView createGenericTabView(final String tabId, final TabItem tabItem);

}
