/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace;

import com.extjs.gxt.ui.client.widget.TabItem;

/**
* @author ecarsea
* @since 06/2012
*/
public class WorkspaceTabComponent {

    private final WorkspacePresenter presenter;

    private final TabItem tabItem;

    /**
     * @param presenter
     * @param tabItem
     */
    public WorkspaceTabComponent(WorkspacePresenter presenter, TabItem tabItem) {
        this.presenter = presenter;
        this.tabItem = tabItem;
    }

    /**
     * @return the presenter
     */
    public WorkspacePresenter getPresenter() {
        return presenter;
    }

    /**
     * @return the tabItem
     */
    public TabItem getTabItem() {
        return tabItem;
    }
}
