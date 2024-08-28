/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.events;

import com.ericsson.eniq.events.ui.client.datatype.TabInfoDataType;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class SaveWorkspaceEvent extends GwtEvent<SaveWorkspaceEventHandler> {

    public final static Type<SaveWorkspaceEventHandler> TYPE = new Type<SaveWorkspaceEventHandler>();

    private final TabInfoDataType tabInfo;

    public SaveWorkspaceEvent(TabInfoDataType tabInfo) {
        this.tabInfo = tabInfo;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<SaveWorkspaceEventHandler> getAssociatedType() {
        return TYPE;
    }

    public TabInfoDataType getTabInfo() {
        return tabInfo;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(SaveWorkspaceEventHandler handler) {
        handler.saveWorkspace(this);
    }
}
