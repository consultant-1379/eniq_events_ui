/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu;

import java.util.Collection;

import com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu.WorkspaceFilter.WorkspaceStateItem;
import com.google.gwt.view.client.ListDataProvider;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface IWorkspaceFilterView {

    void clearFilter();

    Collection<WorkspaceStateItem> getSelectedItems();

    void addWorkspacesItems(ListDataProvider<WorkspaceStateItem> dataProvider, String header);

    void addStartupItems(ListDataProvider<WorkspaceStateItem> dataProvider);

}
