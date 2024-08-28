/*
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events.component;

import com.ericsson.eniq.events.widgets.client.component.ComponentMessageType;

public class ComponentMessageEvent extends BaseComponentEvent<ComponentMessageEventHandler> {

    public final static Type<ComponentMessageEventHandler> TYPE = new Type<ComponentMessageEventHandler>();

    private final ComponentMessageType type;

    private final String message;

    public ComponentMessageEvent(final String portletId, final ComponentMessageType type, final String message) {
        super(portletId);
        this.type = type;
        this.message = message;
    }

    @Override
    public Type<ComponentMessageEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final ComponentMessageEventHandler handler) {
        handler.onMessage(this);
    }

    public ComponentMessageType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}