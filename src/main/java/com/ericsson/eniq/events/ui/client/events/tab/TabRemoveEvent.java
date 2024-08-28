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
public class TabRemoveEvent extends GwtEvent<TabRemoveEventHandler> {

    public final static Type<TabRemoveEventHandler> TYPE = new Type<TabRemoveEventHandler>();

    private final String tabId;

    public TabRemoveEvent(final String tabId) {
        this.tabId = tabId;
    }

    @Override
    public Type<TabRemoveEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final TabRemoveEventHandler handler) {
        handler.onTabRemove(this);
    }

    /**
     * @return the tabId
     */
    public String getTabId() {
        return tabId;
    }
}
