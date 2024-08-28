/*
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.tab;

/**
 * Id type defined for items in workspace (tab) options menu
 * (to support individual menu items enabling - and general menu identification)
 *
 * @author ealeerm - Alexey Ermykin
 * @since 06/2012
 */
public enum WorkspaceOptionMenuItemType {

    SAVE_WORKSPACE("Save Workspace"),
    SAVE_WORKSPACE_AS("Save Workspace As..."),


    CLOSE_WINDOWS("Close Windows"),

    CLOSE_WORKSPACE("Close Workspace"),
    CLOSE_ALL_WORKSPACES("Close All Workspaces");

    private String name;

    /**
     * This sets up the options in the option menu.
     *
     * @param name Menu item name
     */
    WorkspaceOptionMenuItemType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
