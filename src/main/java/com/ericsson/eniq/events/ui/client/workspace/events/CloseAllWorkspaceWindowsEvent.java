/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.events;

import com.ericsson.eniq.events.ui.client.datatype.TabInfoDataType;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author ealeerm - Alexey Ermykin
 * @since 06/2012
 */
public class CloseAllWorkspaceWindowsEvent extends GwtEvent<CloseAllWorkspaceWindowsEventHandler> {

    public final static Type<CloseAllWorkspaceWindowsEventHandler> TYPE = new Type<CloseAllWorkspaceWindowsEventHandler>();

    private final TabInfoDataType tabInfo;

    public CloseAllWorkspaceWindowsEvent(TabInfoDataType tabInfo) {
        this.tabInfo = tabInfo;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public Type<CloseAllWorkspaceWindowsEventHandler> getAssociatedType() {
        return TYPE;
    }

    public TabInfoDataType getTabInfo() {
        return tabInfo;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(CloseAllWorkspaceWindowsEventHandler handler) {
        handler.closeAllWorkspaceWindows(this);
    }
}
