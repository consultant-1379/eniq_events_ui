/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events.dashboard;

import com.google.gwt.event.shared.EventHandler;

public interface PortletMoveEventHandler extends EventHandler {

    void onMove(PortletMoveEvent portletMoveEvent);
}