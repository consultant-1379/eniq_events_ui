/*
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events.dashboard;

import com.google.gwt.event.shared.GwtEvent;

public class PortletResizeEvent extends GwtEvent<PortletResizeEventHandler> {

    public final static Type<PortletResizeEventHandler> TYPE = new Type<PortletResizeEventHandler>();

    @Override
    public Type<PortletResizeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final PortletResizeEventHandler handler) {
        handler.onResize(this);
    }
}