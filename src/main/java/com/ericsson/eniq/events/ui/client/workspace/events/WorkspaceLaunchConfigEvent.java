/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.events;

import java.util.List;

import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class WorkspaceLaunchConfigEvent extends GwtEvent<WorkspaceLaunchConfigEventHandler> {

    public final static Type<WorkspaceLaunchConfigEventHandler> TYPE = new Type<WorkspaceLaunchConfigEventHandler>();

    private final List<WorkspaceState> workspaceStates;

    /**
     * @param workspaceStates
     */
    public WorkspaceLaunchConfigEvent(List<WorkspaceState> workspaceStates) {
        this.workspaceStates = workspaceStates;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<WorkspaceLaunchConfigEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * @return the workspaces
     */
    public List<WorkspaceState> getWorkspaceStates() {
        return workspaceStates;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(WorkspaceLaunchConfigEventHandler handler) {
        handler.onWorkspaceLaunchConfig(this);
    }

}
