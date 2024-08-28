/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.component.base;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;

/**
 * This class is work in progress
 * 
 * @author michaeladams
 * @since 2011
 *
 * TODO Add UI Binding when the exact approach is agreed
 * TODO Add unit tests when the exact approach is agreed and if required
 */
public abstract class EFocusedWidget extends Composite implements Focusable { // NOPMD MA not renaming abstract class
    private int tabIndex = 0;

    private boolean doesHaveFocus = false; // NOPMD MA not transient and does not need accessor methods

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Focusable#getTabIndex()
     */
    @Override
    public int getTabIndex() {
        return tabIndex;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Focusable#setTabIndex(int)
     */
    @Override
    public void setTabIndex(final int index) {
        tabIndex = index;

    }

    /**
     * @param hasFocus the hasFocus to set
     */
    public void setHasFocus(final boolean hasFocus) {
        this.doesHaveFocus = hasFocus;
    }

    /**
     * @return the hasFocus
     */
    public boolean hasFocus() {
        return doesHaveFocus;
    }
}
