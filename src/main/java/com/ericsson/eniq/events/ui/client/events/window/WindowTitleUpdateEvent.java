/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events.window;

import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event fired when window title is updated. Use to keep docking task bar in sync with actual window title
 * @author ecarsea
 * @since 2012
 *
 */
public class WindowTitleUpdateEvent extends GwtEvent<WindowTitleUpdateEventHandler> {

    public static final Type<WindowTitleUpdateEventHandler> TYPE = new Type<WindowTitleUpdateEventHandler>();

    private final WindowModel model;

    private final BaseWindow window;

    public WindowTitleUpdateEvent(final WindowModel model, final BaseWindow window) {
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
    public Type<WindowTitleUpdateEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final WindowTitleUpdateEventHandler handler) {
        handler.onWindowTitleUpdated(this);
    }
}