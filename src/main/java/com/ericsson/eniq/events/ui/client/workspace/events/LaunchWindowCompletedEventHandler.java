/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface LaunchWindowCompletedEventHandler extends EventHandler {
    /**
     * @param source
     */
    void onWindowLaunchComplete(Widget source);
}
