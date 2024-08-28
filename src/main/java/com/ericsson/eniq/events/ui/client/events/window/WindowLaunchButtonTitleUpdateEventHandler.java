/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.events.window;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author xkancha
 */
public interface WindowLaunchButtonTitleUpdateEventHandler extends EventHandler {

    void onWindowTitleUpdate(WindowLaunchButtonTitleUpdateEvent event);

}
