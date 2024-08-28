/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events.window;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface WindowTitleUpdateEventHandler extends EventHandler {

    /**
     * @param event
     */
    void onWindowTitleUpdated(WindowTitleUpdateEvent event);

}
