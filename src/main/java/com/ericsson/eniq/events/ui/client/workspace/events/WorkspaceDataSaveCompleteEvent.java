/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class WorkspaceDataSaveCompleteEvent extends GwtEvent<WorkspaceDataSaveCompleteEventHandler> {

    public final static Type<WorkspaceDataSaveCompleteEventHandler> TYPE = new Type<WorkspaceDataSaveCompleteEventHandler>();

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<WorkspaceDataSaveCompleteEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(WorkspaceDataSaveCompleteEventHandler handler) {
        handler.onWorkspaceSaveComplete(this);
    }
}
