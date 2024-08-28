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
public class GroupSaveAsEvent extends GwtEvent<GroupSaveAsEventHandler> {

    public static final Type<GroupSaveAsEventHandler> TYPE = new Type<GroupSaveAsEventHandler>();

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<GroupSaveAsEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(final GroupSaveAsEventHandler handler) {
        handler.onGroupSaveAsEvent();

    }

}
