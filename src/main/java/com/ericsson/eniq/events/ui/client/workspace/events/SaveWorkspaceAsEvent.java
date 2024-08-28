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
public class SaveWorkspaceAsEvent extends GwtEvent<SaveWorkspaceAsEventHandler> {

    public final static Type<SaveWorkspaceAsEventHandler> TYPE = new Type<SaveWorkspaceAsEventHandler>();

    private final TabInfoDataType tabInfo;

    private String newWorkspaceName;

    public SaveWorkspaceAsEvent(TabInfoDataType tabInfo) {
        this.tabInfo = tabInfo;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public Type<SaveWorkspaceAsEventHandler> getAssociatedType() {
        return TYPE;
    }

    public TabInfoDataType getTabInfo() {
        return tabInfo;
    }

    public String getNewWorkspaceName() {
        return newWorkspaceName;
    }

    public boolean isNewWorkspaceNameDefined() {
        return newWorkspaceName != null && !newWorkspaceName.trim().isEmpty();
    }

    public void setNewWorkspaceName(String newWorkspaceName) {
        this.newWorkspaceName = newWorkspaceName;
    }
    /* (non-Javadoc)
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
    @Override
    protected void dispatch(SaveWorkspaceAsEventHandler handler) {
        handler.saveWorkspaceAs(this);
    }
}
