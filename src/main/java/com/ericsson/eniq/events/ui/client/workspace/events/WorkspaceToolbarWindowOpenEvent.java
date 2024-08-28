/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.events;

import com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowView;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class WorkspaceToolbarWindowOpenEvent extends GwtEvent<WorkspaceToolbarWindowOpenEventHandler> {

    public final static Type<WorkspaceToolbarWindowOpenEventHandler> TYPE = new Type<WorkspaceToolbarWindowOpenEventHandler>();

    private final String workspaceId, category, title, icon;

    private final IBaseWindowView baseWindow;

    /**
     * @param workspaceId
     * @param category
     * @param title
     * @param icon
     * @param window
     */
    public WorkspaceToolbarWindowOpenEvent(String workspaceId, String category, String title, String icon,
            IBaseWindowView window) {
        this.workspaceId = workspaceId;
        this.category = category;
        this.title = title;
        this.icon = icon;
        this.baseWindow = window;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @return the baseWindow
     */
    public IBaseWindowView getBaseWindow() {
        return baseWindow;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<WorkspaceToolbarWindowOpenEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(WorkspaceToolbarWindowOpenEventHandler handler) {
        handler.onWindowToolbarOpenEvent(this);
    }

    /**
     * @return
     */
    public String getWorkspaceId() {
        return workspaceId;
    }
}
