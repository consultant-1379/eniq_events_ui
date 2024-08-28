/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events.dashboard;


import com.ericsson.eniq.events.ui.client.events.component.BaseComponentEvent;

public class PortletRemoveEvent extends BaseComponentEvent<PortletRemoveEventHandler> {

    public final static Type<PortletRemoveEventHandler> TYPE = new Type<PortletRemoveEventHandler>();

    public PortletRemoveEvent(final String portletId) {
        super(portletId);
    }

    @Override
    public Type<PortletRemoveEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final PortletRemoveEventHandler handler) {
        handler.onRemove(this);
    }
}