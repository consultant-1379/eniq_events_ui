/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.tab;

import com.extjs.gxt.ui.client.widget.TabPanel;

/**
 * @author eromsza
 * @since 11/2012
 */
public interface ITabSelector {

    /**
     * Select the workspace tab after a workspace tab workspaceId is created and added to tabPanel.
     *
     * @param tabPanel A tab container on which to apply the selection
     * @param workspaceId A tab to remove
     */
    public void selectTabOnCreate(TabPanel tabPanel, String workspaceId);

    /**
     * Select the workspace tab before a workspace tab workspaceId is removed from tabPanel.
     *
     * @param tabPanel A tab container on which to apply the selection
     * @param workspaceId A tab to remove
     */
    public void selectTabOnRemove(TabPanel tabPanel, String workspaceId);
}
