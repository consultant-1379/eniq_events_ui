/*
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events.component;

import com.google.gwt.event.shared.EventHandler;

public interface ComponentMessageEventHandler extends EventHandler {

    void onMessage(ComponentMessageEvent messageEvent);
}
