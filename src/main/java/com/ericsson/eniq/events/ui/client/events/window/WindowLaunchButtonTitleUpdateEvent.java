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
 * Fired when a launched window title is updated by drilldown, search field update or navigation,
 * and the same should reflect in its menuTaskBar launch button.
 *
 * @author xkancha
 */
public final class WindowLaunchButtonTitleUpdateEvent extends GwtEvent<WindowLaunchButtonTitleUpdateEventHandler> {

    public static final Type<WindowLaunchButtonTitleUpdateEventHandler> TYPE = new Type<WindowLaunchButtonTitleUpdateEventHandler>();

    private final String tabId;

    private final BaseWindow window;
    
    private final String title;

    public WindowLaunchButtonTitleUpdateEvent(final String tabId, final BaseWindow window, final String title) {
        this.tabId = tabId;
        this.window = window;
        this.title = title;
    }

    public String getTabId() {
        return tabId;
    }
    
    public BaseWindow getWindow() {
        return window;
    }

    public String getTitle() {
        return title;
    }


    @Override
    public Type<WindowLaunchButtonTitleUpdateEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final WindowLaunchButtonTitleUpdateEventHandler handler) {
        handler.onWindowTitleUpdate(this);
    }

}
