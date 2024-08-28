/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public class GroupWindowMaskEvent extends GwtEvent<GroupWindowMaskEventHandler> {

    public static final Type<GroupWindowMaskEventHandler> TYPE = new Type<GroupWindowMaskEventHandler>();

    private final String maskMessage;

    /**
     * @param maskMessage
     */
    public GroupWindowMaskEvent(final String maskMessage) {
        this.maskMessage = maskMessage;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<GroupWindowMaskEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * @return the maskMessage
     */
    public String getMaskMessage() {
        return maskMessage;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(final GroupWindowMaskEventHandler handler) {
        handler.onGroupWindowMaskEvent(this);

    }

}
