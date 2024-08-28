/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author ealeerm - Alexey Ermykin
 * @since 06/2012
 */
public interface CloseAllWorkspaceWindowsEventHandler extends EventHandler {

    /**
     * @param event event with data
     */
    void closeAllWorkspaceWindows(CloseAllWorkspaceWindowsEvent event);

}
