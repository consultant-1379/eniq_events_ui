/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for Mask of UI
 * 
 * @author eriwals
 * @since Oct 2010
 */
public interface MaskEventHandler extends EventHandler {

    /**
     * Handles the masking / demasking of the UI
     * @param isMasked True / False of whether the UI is masked or not.
     * @param tabOwner String storing the Tab to which this event is addressed
     */
    void handleMaskEvent(final boolean isMasked, String tabOwner);

}
