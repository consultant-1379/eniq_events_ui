/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Implementing class handling request  to check its toolbar buttons enable status 
 * (e.g. on BreadCrumb Navigation change)
 * 
 * @author esuslyn
 * @author eeicmsy
 * @since October 2010
 * 
 * Interface for class to handle ButtonEnablingEvent dispatched using EventBus
 */
public interface ButtonEnablingEventHandler extends EventHandler {
    /**
     * Handle Enabling/Disabling buttons
     * @param winId - id of window
     * @param rowCount - number of rows in grid
     */
    void handleButtonEnabling(final String winId, final int rowCount);
}
