/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.events;

import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class WorkspaceLaunchConfigCompleteEvent extends GwtEvent<WorkspaceLaunchConfigCompleteEventHandler> {

    public final static Type<WorkspaceLaunchConfigCompleteEventHandler> TYPE = new Type<WorkspaceLaunchConfigCompleteEventHandler>();

    private final WorkspaceState workspaceState;

    private final String workspaceId;

    /**
     * @param workspaces
     */
    public WorkspaceLaunchConfigCompleteEvent(String workspaceId, WorkspaceState workspaceState) {
        this.workspaceId = workspaceId;
        this.workspaceState = workspaceState;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<WorkspaceLaunchConfigCompleteEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * @return the workspaceState
     */
    public WorkspaceState getWorkspaceState() {
        return workspaceState;
    }

    /**
     * @return
     */
    public String getWorkspaceId() {
        return workspaceId;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(WorkspaceLaunchConfigCompleteEventHandler handler) {
        handler.onWorkspaceLaunchConfigComplete(this);
    }

}
