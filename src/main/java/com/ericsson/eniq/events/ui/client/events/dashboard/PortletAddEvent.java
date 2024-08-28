/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events.dashboard;

import com.ericsson.eniq.events.ui.client.dashboard.portlet.PlaceHolder;
import com.ericsson.eniq.events.ui.client.events.component.BaseComponentEvent;

public class PortletAddEvent extends BaseComponentEvent<PortletAddEventHandler> {

    public final static Type<PortletAddEventHandler> TYPE = new Type<PortletAddEventHandler>();

    private final PlaceHolder placeHolder;

    public PortletAddEvent(final String portletId, final PlaceHolder placeHolder) {
    	super(portletId);
        this.placeHolder = placeHolder;
    }

    @Override
    public Type<PortletAddEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final PortletAddEventHandler handler) {
        handler.onAdd(this);
    }

    public PlaceHolder getPlaceHolder() {
        return placeHolder;
    }
}