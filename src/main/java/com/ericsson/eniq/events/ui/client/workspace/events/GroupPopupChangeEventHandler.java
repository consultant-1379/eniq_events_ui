/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author eromsza
 * @since 11/2012
 */
public interface GroupPopupChangeEventHandler extends EventHandler {
    void onGroupPopupChange(String workspaceId, Boolean isToggled);
}
