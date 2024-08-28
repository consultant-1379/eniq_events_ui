/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handle event fired from GroupSingleToggleComponent
 * whereby receiver must either set the single search component
 * visible or the group search component visible
 * 
 * @author eeicmsy
 * @since July 2010
 * 
 */
public interface GroupSingleToggleEventHandler extends EventHandler {

    /**
     * Toggle search field display to show either single node selection component 
     * or a group selection component 
     * (note not used on network tab as taking over type combobox for that)
     * 
     *@param tabId             -  unique id of tab where search component is contained 
     * 
     * @param setGroupVisible  -  true to set the group component visible (and
     *                            search component invisible) and vice versa for false.
     
     */
    void toggleGroupSingleDisplay(final String tabId, final boolean setGroupVisible);

}
