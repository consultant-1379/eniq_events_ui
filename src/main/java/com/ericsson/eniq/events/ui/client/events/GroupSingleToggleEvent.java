/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event fired when want to only show one search field component
 * at a time (i.e. toggle the search field group 
 * component and search field single node selection component displayed
 * on the UI)
 *  
 * @author eeicmsy
 * @since July 2010
 */
public class GroupSingleToggleEvent extends GwtEvent<GroupSingleToggleEventHandler> {
    public static final Type<GroupSingleToggleEventHandler> TYPE = new Type<GroupSingleToggleEventHandler>();

    private final boolean isGroup;

    private final String tabId;

    /**
     * Event fired when want to toggle search field display from 
     * group component to particular single node search component on the tab
     * 
     * @param tabId     unique id of tab where search component is contained 
     * @param isGroup   true to set group search component visible (toggling with 
     *                  single search component)
     */
    public GroupSingleToggleEvent(final String tabId, final boolean isGroup) {
        this.isGroup = isGroup;
        this.tabId = tabId;

    }

    @Override
    protected void dispatch(final GroupSingleToggleEventHandler handler) {
        handler.toggleGroupSingleDisplay(tabId, isGroup);
    }

    @Override
    public Type<GroupSingleToggleEventHandler> getAssociatedType() {
        return TYPE;
    }

}
