/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch;

import com.ericsson.eniq.events.widgets.client.launch.resources.LaunchResourceBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface WorkspaceLaunchMenuResourceBundle extends LaunchResourceBundle {

    @Source("css/WorkspaceLaunchMenu.css")
    WorkspaceLaunchViewStyle workspaceLaunchStyle();

    @Source("config/workspaceLaunch.json")
    TextResource workspaceLaunchConfig();

    @Source("config/predefinedWorkspaces.json")
    TextResource predefinedWorkspaces();

    @Source("images/launch_icon.png")
    ImageResource launchButton();

    @Source("images/group_info_icon.png")
    ImageResource groupInfo();

    @Source("images/group_info_icon_ON.png")
    ImageResource groupInfoOn();

    @Source("images/info_icon.png")
    ImageResource infoIcon();

    @Source("images/warning_icon.png")
    ImageResource warningIcon();

    @Source("images/pin_normal.png")
    ImageResource pinNormal();

    @Source("images/pin_pinned.png")
    ImageResource pinPinned();

    @Source("images/add_to_favourites.png")
    ImageResource addToFavourites();

    @Source("images/remove_from_favourites.png")
    ImageResource removeFromFavourites();

    @Source("images/tab/active_tab_launch.png")
    ImageResource tabActive();

    @Source("images/tab/inactive_tab_launch.png")
    ImageResource tabInactive();

    interface WorkspaceLaunchViewStyle extends CssResource {
        String tabPanelBottom();

        String enabled();

        String glassPanel();

        String windowListItem();

        String showSelectionItem();

        String hideSelectionItem();

        String windowFilterPanelDisabled();

        String liveloadPopup();

        String tabPanel();

        String tabBar();

        String dimensionButton();

        String pin();

        String groupInfoToggle();

        String workspaceListItem();

        String addToFavourites();

        String removeFromFavourites();

        String workspaceFavourites();

        String workspaceItemHolder();

        String noFavouritesIcon();
    }
}
