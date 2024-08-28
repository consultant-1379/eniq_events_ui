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
public class CloseWorkspaceEvent extends GwtEvent<CloseWorkspaceEventHandler> {

    public final static Type<CloseWorkspaceEventHandler> TYPE = new Type<CloseWorkspaceEventHandler>();

    private final TabInfoDataType tabInfo;

    public CloseWorkspaceEvent(TabInfoDataType tabInfo) {
        this.tabInfo = tabInfo;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<CloseWorkspaceEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(CloseWorkspaceEventHandler handler) {
        handler.closeWorkspace(tabInfo);
    }
}
