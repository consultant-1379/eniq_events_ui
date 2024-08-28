package com.ericsson.eniq.events.ui.client.workspace.launchwindowconfig.datatype;

import com.ericsson.eniq.events.widgets.client.dropdown.IDropDownItem;

public class LaunchTypeMenuItem implements IDropDownItem {

    private ConfigLaunchType launchMenuItem;

    public LaunchTypeMenuItem(ConfigLaunchType LaunchMenuItem) {
        launchMenuItem = LaunchMenuItem;
    }

    @Override
    public String toString() {
        return this.launchMenuItem.getDisplayName();
    }

    public String getLaunchType() {
        return this.launchMenuItem.toString();
    }

    @Override
    public boolean isSeparator() {
        return false;
    }
}
