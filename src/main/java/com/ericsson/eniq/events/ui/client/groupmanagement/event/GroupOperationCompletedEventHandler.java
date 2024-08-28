/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public interface GroupOperationCompletedEventHandler extends EventHandler {
    void onGroupOperationCompleted(GroupOperationCompletedEvent event);
}
