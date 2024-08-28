/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author eromsza
 * @since 11/2012
 */
public class GroupPopupChangeEvent extends GwtEvent<GroupPopupChangeEventHandler> {

    public static final Type<GroupPopupChangeEventHandler> TYPE = new Type<GroupPopupChangeEventHandler>();

    private final String workspaceId;

    private final Boolean isToggled;

    public GroupPopupChangeEvent(String workspaceId) {
        this.workspaceId = workspaceId;
        this.isToggled = null;
    }

    public GroupPopupChangeEvent(String workspaceId, Boolean isToggled) {
        this.workspaceId = workspaceId;
        this.isToggled = isToggled;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public Type<GroupPopupChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(GroupPopupChangeEventHandler handler) {
        handler.onGroupPopupChange(workspaceId, isToggled);
    }
}
