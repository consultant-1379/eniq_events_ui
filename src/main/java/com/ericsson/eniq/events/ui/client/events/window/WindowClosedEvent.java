/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.events.window;

import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.google.gwt.event.shared.GwtEvent;

/**
 *
 * Fired when window is being closed by user.
 *
 * @author edmibuz
 */
public final class WindowClosedEvent extends GwtEvent<WindowClosedEventHandler> {

    public static final Type<WindowClosedEventHandler> TYPE = new Type<WindowClosedEventHandler>();

    private final WindowModel model;

    private final BaseWindow window;

    public WindowClosedEvent(final WindowModel model, final BaseWindow window) {
        this.model = model;
        this.window = window;
    }

    public BaseWindow getWindow() {
        return window;
    }

    public WindowModel getModel() {
        return model;
    }

    @Override
    public Type<WindowClosedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final WindowClosedEventHandler handler) {
        handler.onWindowClosed(this);
    }

}
