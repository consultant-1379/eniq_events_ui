/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu;

import java.util.List;

import com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu.WindowFilter.WindowItem;
import com.google.gwt.view.client.ListDataProvider;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface IWindowFilterView {

    void clearFilter();

    void addItem(String name, ListDataProvider<WindowItem> dataProvider);

    List<WindowItem> getSelectedItems();

    void addAllCategoryPanels();

    void removeAllCategoryPanels();
}
