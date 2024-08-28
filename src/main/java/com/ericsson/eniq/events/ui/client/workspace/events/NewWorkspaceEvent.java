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
public class NewWorkspaceEvent extends GwtEvent<NewWorkspaceEventHandler> {

    public final static Type<NewWorkspaceEventHandler> TYPE = new Type<NewWorkspaceEventHandler>();

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<NewWorkspaceEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(NewWorkspaceEventHandler handler) {
        handler.onNewWorkspace();
    }
}
