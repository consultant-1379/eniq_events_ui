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
public class GroupWindowUnMaskEvent extends GwtEvent<GroupWindowUnMaskEventHandler> {

    public static final Type<GroupWindowUnMaskEventHandler> TYPE = new Type<GroupWindowUnMaskEventHandler>();

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<GroupWindowUnMaskEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(final GroupWindowUnMaskEventHandler handler) {
        handler.onGroupWindowUnMaskEvent();

    }

}
