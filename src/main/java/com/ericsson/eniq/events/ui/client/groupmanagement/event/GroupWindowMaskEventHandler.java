/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface GroupWindowMaskEventHandler extends EventHandler {
    void onGroupWindowMaskEvent(GroupWindowMaskEvent groupWindowMaskEvent);
}
