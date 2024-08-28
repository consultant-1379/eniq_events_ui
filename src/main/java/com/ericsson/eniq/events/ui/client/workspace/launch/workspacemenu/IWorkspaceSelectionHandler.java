/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu;

import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface IWorkspaceSelectionHandler {
    void onDoubleClick(WorkspaceState workspaceState);

    void onSelectionChange(int selectedWorkspaces);

    void onStartupItemUpdate(WorkspaceState state);
}
