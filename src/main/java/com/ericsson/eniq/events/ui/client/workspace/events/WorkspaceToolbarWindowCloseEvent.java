/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.events;

import com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowView;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class WorkspaceToolbarWindowCloseEvent extends GwtEvent<WorkspaceToolbarWindowCloseEventHandler> {

    public final static Type<WorkspaceToolbarWindowCloseEventHandler> TYPE = new Type<WorkspaceToolbarWindowCloseEventHandler>();

    private final String workspaceId;

    private final IBaseWindowView baseWindow;

    /**
     * @param workspaceId
     * @param window
     */
    public WorkspaceToolbarWindowCloseEvent(String workspaceId, IBaseWindowView window) {
        this.workspaceId = workspaceId;
        this.baseWindow = window;
    }

    /**
     * @return the baseWindow
     */
    public IBaseWindowView getBaseWindow() {
        return baseWindow;
    }

    /**
     * @return
     */
    public String getWorkspaceId() {
        return workspaceId;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<WorkspaceToolbarWindowCloseEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(WorkspaceToolbarWindowCloseEventHandler handler) {
        handler.onWindowToolbarCloseEvent(this);
    }
}
