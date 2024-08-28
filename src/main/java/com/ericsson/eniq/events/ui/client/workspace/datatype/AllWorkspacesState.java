/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.datatype;

import java.util.List;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface AllWorkspacesState {
    List<WorkspaceState> getWorkspaces();

    void setWorkspaces(List<WorkspaceState> workspacesToSave);

    @PropertyName("startupItems")
    List<String> getStartUpItems();

    @PropertyName("startupItems")
    void setStartupItems(List<String> startupItems);
}
