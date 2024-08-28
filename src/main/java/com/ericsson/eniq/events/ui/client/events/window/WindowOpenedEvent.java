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
 * Fired when window is being displayed to user.
 *
 * @author edmibuz
 */
public final class WindowOpenedEvent extends GwtEvent<WindowOpenedEventHandler> {

    public static final Type<WindowOpenedEventHandler> TYPE = new Type<WindowOpenedEventHandler>();

    private final WindowModel model;

    private final BaseWindow window;

    public WindowOpenedEvent(final WindowModel model, final BaseWindow window) {
        this.model = model;
        this.window = window;
    }

    public WindowModel getModel() {
        return model;
    }

    public BaseWindow getWindow() {
        return window;
    }

    @Override
    public Type<WindowOpenedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final WindowOpenedEventHandler handler) {
        handler.onWindowOpened(this);
    }

}
