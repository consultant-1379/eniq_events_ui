/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.events;

import com.ericsson.eniq.events.ui.client.workspace.launch.WindowLaunchParams;
import com.google.gwt.event.shared.EventHandler;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface LaunchWindowEventHandler extends EventHandler {

    /**
     * @param launchParams
     */
    void onWindowLaunch(WindowLaunchParams launchParams);

}
