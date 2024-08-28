/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.tab;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author ealeerm - Alexey Ermykin
 * @since 06/2012
 */
public interface WorkspaceTabsResourceBundle extends ClientBundle {

    @Source("WorkspaceTabs.css")
    WorkspaceTabsStyle css();

    @Source("images/add_workspace_active.png")
    ImageResource addWorkspaceActive();

    @Source("images/add_workspace_inactive.png")
    ImageResource addWorkspaceInactive();

    @Source("images/add_workspace_active_hover.png")
    ImageResource addWorkspaceActiveHover();

    @Source("images/menu_arrow_active_hover_tab.png")
    ImageResource menuArrowActiveHover();

    @Source("images/menu_arrow_active_tab.png")
    ImageResource menuArrowActive();

    @Source("images/menu_arrow_inactive_tab_hover.png")
    ImageResource menuArrowInactiveHover();

    @Source("images/menu_arrow_inactive_tab.png")
    ImageResource menuArrowInactive();

    @Source("images/close_active_tab.png")
    ImageResource closeActiveTab();

    @Source("images/close_active_tab_hover.png")
    ImageResource closeActiveTabHover();

    @Source("images/unsaved_workspace_active.png")
    ImageResource unsavedWorkspaceActive();

    @Source("images/unsaved_workspace_inactive.png")
    ImageResource unsavedWorkspaceInactive();

    interface WorkspaceTabsStyle extends CssResource {
        String plusTab();

        String plusTabIcon();

        String plusTabDisabled();

        String closeTabButton();

        String dirtyWorkspaceIndicator();

        String hideDirtyWorkspace();
    }
}
