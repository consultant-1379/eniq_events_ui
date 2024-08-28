/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.events;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.google.gwt.event.shared.EventHandler;

/**
 * Handle checking with user if can close all windows
 * prior to firing click event for MultiWinSelectComponent
 * 
 * @author eeicmsy
 * @since June 2011
 *
 */
public interface CheckAndCloseAllWindowsEventHandler extends EventHandler {

    /**
     * Check with user if can close all windows
     * prior to firing click event for MultiWinSelectComponent
     * 
     * @param ce           - component event, 
     *                       @see #com.extjs.gxt.ui.client.widget.form.CheckBox.onClick
     * @param tabId        - tabId owner of checkbox (whose manual setting we may need to undo) 
     */
    void handleCheckForMultiWinSelectComp(final ComponentEvent ce, final String tabId);

}
