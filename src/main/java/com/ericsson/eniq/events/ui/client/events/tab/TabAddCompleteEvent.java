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
public class TabAddCompleteEvent extends GwtEvent<TabAddCompleteEventHandler> {

    public final static Type<TabAddCompleteEventHandler> TYPE = new Type<TabAddCompleteEventHandler>();

    private final String tabId;

    public TabAddCompleteEvent(final String tabId) {
        this.tabId = tabId;
    }

    @Override
    public Type<TabAddCompleteEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final TabAddCompleteEventHandler handler) {
        handler.onTabAddComplete(this);
    }

    /**
     * @return the tabId
     */
    public String getTabId() {
        return tabId;
    }
}
