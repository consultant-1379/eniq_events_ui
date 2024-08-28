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
public class WorkspaceDataSaveRequestEvent extends GwtEvent<WorkspaceDataSaveRequestEventHandler> {

    public final static Type<WorkspaceDataSaveRequestEventHandler> TYPE = new Type<WorkspaceDataSaveRequestEventHandler>();

    private final List<WorkspaceState> workspaceStates;

    private final boolean renamingWorkspaces;

    private final List<String> startupItems;

    /**
     * @param workspaceStates
     * @param favourites
     */
    public WorkspaceDataSaveRequestEvent(List<WorkspaceState> workspaceStates, List<String> favourites) {
        this(workspaceStates, favourites, false);
    }

    /**
     * @param workspaceStates
     * @param startupItems
     * @param renamingWorkspaces
     */
    public WorkspaceDataSaveRequestEvent(List<WorkspaceState> workspaceStates, List<String> startupItems,
            boolean renamingWorkspaces) {
        this.workspaceStates = workspaceStates;
        this.startupItems = startupItems;
        this.renamingWorkspaces = renamingWorkspaces;
    }

    /**
     * @return the workspaceStates
     */
    public List<WorkspaceState> getWorkspaceStates() {
        return workspaceStates;
    }

    /**
     * @return renamingWorkspaces
     */
    public boolean isRenamingWorkspaces() {
        return renamingWorkspaces;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<WorkspaceDataSaveRequestEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(WorkspaceDataSaveRequestEventHandler handler) {
        handler.onWorkspaceSaveRequest(this);
    }

    /**
     * @return
     */
    public List<String> getStartupItems() {
        return startupItems;
    }
}
