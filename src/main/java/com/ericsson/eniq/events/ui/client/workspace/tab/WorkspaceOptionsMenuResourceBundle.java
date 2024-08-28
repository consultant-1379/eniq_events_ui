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
public interface WorkspaceOptionsMenuResourceBundle extends ClientBundle {

    @Source("WorkspaceOptionsMenu.css")
    WorkspaceOptionsMenuStyle css();

    @Source("images/menu_arrow_active_tab.png")
    ImageResource arrowActive();

    @Source("images/menu_arrow_active_hover_tab.png")
    ImageResource arrowActiveHover();

    @Source("images/menu_arrow_inactive_tab.png")
    ImageResource arrowInactive();

    @Source("images/menu_arrow_inactive_tab_hover.png")
    ImageResource arrowInactiveHover();

    interface WorkspaceOptionsMenuStyle extends CssResource {

        String open();

        String optionsMenu();
    }
}
