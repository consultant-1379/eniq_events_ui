/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events.tab;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public class TabAddEvent extends GwtEvent<TabAddEventHandler> {

    public final static Type<TabAddEventHandler> TYPE = new Type<TabAddEventHandler>();

    private final String tabId;

    public TabAddEvent(final String tabId) {
        this.tabId = tabId;
    }

    @Override
    public Type<TabAddEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final TabAddEventHandler handler) {
        handler.onTabAdd(this);
    }

    /**
     * @return the tabId
     */
    public String getTabId() {
        return tabId;
    }

}
