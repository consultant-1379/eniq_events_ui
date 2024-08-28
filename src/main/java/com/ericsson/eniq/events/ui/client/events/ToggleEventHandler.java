/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Event Handler for Toggle Event (e.g.
 * When one multiple windows checkbox is checked
 * all must be checked)
 * 
 * @author eeicmsy
 * @since June 2011
 *
 */
public interface ToggleEventHandler extends EventHandler {

    /**
     * Handle a toggle event, either 
     * <li>across all tabs except the tab specified</li>
     * <li>only on the tab specified</li>
     *
     * @param thisTabOnly  - if true only toggle on the tab specified in 
     *                       second parameter. If false toggle on every tab EXCEPT
     *                       tab specified in second parameter
     * @param fromTabId  unique id of tab when event fired from
     */
    void handleToggleEvent(final boolean thisTabOnly, final String fromTabId);

}
