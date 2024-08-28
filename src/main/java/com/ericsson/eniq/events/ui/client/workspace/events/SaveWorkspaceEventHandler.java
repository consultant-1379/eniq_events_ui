/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface SaveWorkspaceEventHandler extends EventHandler {

    /**
     * @param saveWorkspaceEvent
     */
    void saveWorkspace(SaveWorkspaceEvent saveWorkspaceEvent);

}
