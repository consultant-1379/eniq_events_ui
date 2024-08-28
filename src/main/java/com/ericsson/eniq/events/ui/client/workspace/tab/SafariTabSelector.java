/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.tab;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;

/**
 * Safari browser tab selection strategy
 *
 * @author eromsza
 * @since 11/2012
 */
public enum SafariTabSelector implements ITabSelector {

    DEFAULT_SAFARI_TAB_SELECTOR;

    /**
     * Select the workspace tab after a workspace tab workspaceId is created and added to tabPanel according to Safari.
     *
     * @param tabPanel A tab container on which to apply the selection
     * @param workspaceId A tab to remove
     */
    @Override
    public void selectTabOnCreate(TabPanel tabPanel, String workspaceId) {
        // Select the tab with workspaceId from the tabPanel container
        tabPanel.setSelection(tabPanel.getItemByItemId(workspaceId));
    }

    /**
     * Select the workspace tab before a workspace tab workspaceId is removed from tabPanel according to Safari.
     *
     * @param tabPanel A tab container on which to apply the selection
     * @param workspaceId A tab to remove
     */
    @Override
    public void selectTabOnRemove(TabPanel tabPanel, String workspaceId) {
        TabItem tabItemToRemove = tabPanel.getItemByItemId(workspaceId);
        TabItem tabItemSelected = tabPanel.getSelectedItem();
        int toRemove = -1;
        int selected = -1;
        int itemCount = tabPanel.getItemCount();

        // Get an index of the selected item (excluding the plus button) due to a missing method for getting the index
        for (int index = 0; index < itemCount - 1; index++) {
            if (tabPanel.getItem(index).equals(tabItemSelected)) {
                selected = index;
                break;
            }
        }
        // Get an index of the item to remove (excluding the plus button) due to a missing method for getting the index
        for (int index = 0; index < itemCount - 1; index++) {
            if (tabPanel.getItem(index).equals(tabItemToRemove)) {
                toRemove = index;
                break;
            }
        }
        // if the selected tab is the most right tab
        if (selected == itemCount - 2) {
            // if the tab to be removed is the same as selected, select the previous tab
            if (toRemove == selected) {
                tabPanel.setSelection(tabPanel.getItem(selected - 1));
            // if the tab to be removed is more to the right that the selected, re-select the selected tab
            } else {
                tabPanel.setSelection(tabPanel.getItem(selected));
            }
        } else {
            // if the tab to be removed is the same as selected, select the next tab
            if (toRemove == selected) {
                tabPanel.setSelection(tabPanel.getItem(selected + 1));
            // if the tab to be removed is more to the right or to the left that the selected, re-select the selected tab
            } else {
                tabPanel.setSelection(tabPanel.getItem(selected));
            }
        }
    }
}
