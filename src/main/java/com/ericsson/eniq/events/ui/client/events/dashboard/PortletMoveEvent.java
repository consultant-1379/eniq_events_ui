/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events.dashboard;


import com.ericsson.eniq.events.ui.client.events.component.BaseComponentEvent;

public class PortletMoveEvent extends BaseComponentEvent<PortletMoveEventHandler> {

    public final static Type<PortletMoveEventHandler> TYPE = new Type<PortletMoveEventHandler>();

    public PortletMoveEvent(final String portletId) {
        super(portletId);
    }

    @Override
    public Type<PortletMoveEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final PortletMoveEventHandler handler) {
        handler.onMove(this);
    }

}