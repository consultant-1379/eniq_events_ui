/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.window;

import com.google.gwt.user.client.ui.Widget;

/**
 * Provides the behaviour handles to interact with a {@link IWindow}.
 * 
 * @author Pedro Tavares - epedtav
 * 
 */
public interface IWindow {

    /**
     * Shows the window at its current position.
     * 
     */
    void setVisible(boolean isVisible);

    /**
     * Sets the window's content using an existing Widget.
     * 
     * @param widget
     *            the content to show into the window
     */
    void setContent(Widget theContent);
}
