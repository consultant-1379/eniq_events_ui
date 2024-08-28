/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.datatype;

import java.util.List;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface PredefinedWorkspaceState {
    List<WorkspaceState> getWorkspaces();

    void setWorkspaces(List<WorkspaceState> workspacesToSave);
}
