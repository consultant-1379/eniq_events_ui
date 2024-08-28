/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.events;

import com.google.web.bindery.event.shared.Event;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class WorkspaceStatusChangeEvent extends Event<WorkspaceStatusChangeEventHandler> {

    public final static Type<WorkspaceStatusChangeEventHandler> TYPE = new Type<WorkspaceStatusChangeEventHandler>();

    private final int currentOpenWindowCount;

    private final boolean isDirty;

    private final String workspaceId;

    public WorkspaceStatusChangeEvent(String workspaceId, int currentOpenWindowCount, boolean isDirty) {
        this.workspaceId = workspaceId;
        this.currentOpenWindowCount = currentOpenWindowCount;
        this.isDirty = isDirty;
    }

    /* (non-Javadoc)
     * @see com.google.web.bindery.event.shared.Event#getAssociatedType()
     */
    @Override
    public com.google.web.bindery.event.shared.Event.Type<WorkspaceStatusChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * @return the workspaceId
     */
    public String getWorkspaceId() {
        return workspaceId;
    }

    /**
     * @return the currentOpenWindowCount that can be <tt>-1</tt> if not defined
     */
    public int getCurrentOpenWindowCount() {
        return currentOpenWindowCount;
    }

    /**
     * @return the isDirty
     */
    public boolean isDirty() {
        return isDirty;
    }

    /* (non-Javadoc)
     * @see com.google.web.bindery.event.shared.Event#dispatch(java.lang.Object)
     */
    @Override
    protected void dispatch(WorkspaceStatusChangeEventHandler handler) {
        handler.onWindowStatusChange(this);

    }

}
