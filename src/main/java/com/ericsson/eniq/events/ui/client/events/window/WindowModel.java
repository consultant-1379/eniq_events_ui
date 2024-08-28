/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.events.window;

/**
 * Current state of the window.
 *
 * @author edmibuz
 */
public final class WindowModel {

    // Mandatory
    private final String tabId;

    // Single-instance fields
    private String title;

    private String icon;

    public WindowModel(final String tabId) {
        this.tabId = tabId;
    }

    public String getTabId() {
        return tabId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(final String icon) {
        this.icon = icon;
    }
}
