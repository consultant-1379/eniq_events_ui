/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;

/**
 * Window launch buttons placed on taskbar. There is a one to one mapping of
 * launch buttons to number of open and mimised windows owned by a tab
 * 
 * @author eeicmsy
 * @since Feb 2010
 * 
 */
public class MenuTaskBarButton extends Button {

    private static final Logger LOGGER = Logger.getLogger(MenuTaskBarButton.class.getName());

    /* access exposed for junit */
    final SelectionListener<ButtonEvent> launchButtonListener = new LaunchButtonListener();

    private final IBaseWindowView winRef;

    /**
     * Button to appear on the main taskbar
     * 
     * @param title
     *          Text to appear on the button ( usually same as "name" of menu item
     *          that created it, unless used a MIN_MENU_NAME_ON_TASKBAR )
     * @param winRef
     *          Associated floating window for this launch button
     */
    public MenuTaskBarButton(final String title, final IBaseWindowView winRef) {
        super(title);
        this.winRef = winRef;

        setId(SELENIUM_TAG + "MenuTaskBarButton_" + getWindowID()); // NOPMD
        setStyleName("menuTaskBarButton");

        addListener(Events.Select, launchButtonListener);
    }

    /**
     * Get id of window launch button is launching
     * 
     * @return same ID being used for window, its query (queryId), its menu item,
     *         button on taskbar
     */
    public String getWindowID() {
        return winRef.getBaseWindowID();
    }

    /**
     * Buttons on task bar must focus window when pressed (keeping out of "view"
     * winRef class)
     */
    private final class LaunchButtonListener extends SelectionListener<ButtonEvent> {

        @Override
        public void componentSelected(final ButtonEvent ce) {
            try {
                winRef.bringToFront();
            } catch (final Exception e) {
                LOGGER.log(Level.WARNING, " MinimisedButtonListener: Failed to Maximise Win from Taskbar", e);
            }
        }
    }
}
