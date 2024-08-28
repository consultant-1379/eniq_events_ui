/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for SearchFieldTypeChangeEvent   (type change ONLY)
 * @author eeicmsy
 * @since May 2010
 *
 */
public interface SearchFieldTypeChangeEventHandler extends EventHandler {

    /**
     * Event type for search field type change (only)
     * This is not for windows. Group search component containing a type 
     * will be interested
     * 
     * @param tabId         - unique id of tab where search component is contained 
     * @param typeSelected  - defined type ID in metadata on a paired search field, e.g.
     *                        APN, SGSN, CELL, etc., which will also be recognisable in 
     *                        meta data for group search component
     * 
     * @param isGroup      - boolean supporting displaying only the search field or the 
     *                       group component at one time on UI. If isGroup is set 
     *                       display the group component (and hide search component minus 
     *                       the type combo-menu item)
     * @param typeText     - text for selected type
     */
    void handleTypeChanged(final String tabId, final String typeSelected, boolean isGroup, String typeText);
}
