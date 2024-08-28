/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event used for example, 
 * to say multiple window check box is checked 
 * or unchecked - when one check box updates all 
 * other check boxes must update
 * 
 * Also used to cancel (toggle back) previous selection.
 * 
 * @author eeicmsy
 * @since June 2011
 *
 */
public class ToggleEvent extends GwtEvent<ToggleEventHandler> {

    public static final Type<ToggleEventHandler> TYPE = new Type<ToggleEventHandler>();

    private final String tabId;

    private final boolean isThisTabOnly;

    /**
     * Toggle event from a tab
     * 
     * Handle a toggle event, either 
     * <li>across all tabs except the tab specified</li>
     * <li>only on the tab specified</li>
     * 
     * @param isThisTabOnly  - if true only toggle on the tab specified in 
     *                       second parameter. If false toggle on every tab EXCEPT
     *                       tab specified in second parameter
     *                      
     * @param tabId   - current tab (unique id) where event is fired from
     */
    public ToggleEvent(final boolean isThisTabOnly, final String tabId) {
        this.tabId = tabId;
        this.isThisTabOnly = isThisTabOnly;
    }

    @Override
    protected void dispatch(final ToggleEventHandler handler) {

        handler.handleToggleEvent(isThisTabOnly, tabId);
    }

    @Override
    public Type<ToggleEventHandler> getAssociatedType() {

        return TYPE;
    }

}
