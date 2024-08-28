/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.component;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

/**
 * @author eblkenn
 * @since 2011
 *
 */

public class EIconButton extends PushButton {

    /** 
     * Right button used by login arrow faces right
     */
    public enum ButtonType {
        LOGIN_SUBMIT("-login_submit"), WINDOW_MANAGE("-window_manage"), SLIDE_ARROW_LEFT("-slide_arrow_left"), SLIDE_ARROW_RIGHT(
                "-slide_arrow_right");

        private final String iconStyleName;

        ButtonType(final String iconStyleName) {
            this.iconStyleName = iconStyleName;
        }

        private String getIconStyleName() {
            return this.iconStyleName;
        }
    }

    /**
    * Create an EIconButton this may be used in the future.
    */
    public EIconButton() {
        super();
        setPrimaryStyle("");
    }

    /**
     * Create an EIconButton with an image this can not be used at the moment because of the need to change theme
     * at run time the image used needs to be loaded via css. This may be needed in the future.
     * 
     * @param upImage image to display on the button when it is in a normal state
     */
    public EIconButton(final Image upImage) {
        super(upImage);
        setPrimaryStyle("");
    }

    /**
     * Create an EIconButton with a button type these types are defined in the enum ButtonType, set the 
     * primary and icon dependent style.
     * @param buttonStyleName the style of icon button to create
     */
    public EIconButton(final ButtonType buttonStyleName) {
        super();
        setPrimaryStyle(buttonStyleName.getIconStyleName());
    }

    /*
     * Set Primary Style for EIconButton based on the applications theme.
     * @param buttonStyleName the style of icon button to create
     */
    private void setPrimaryStyle(final String buttonStyleName) {
        setStyleName("EIconButton" + buttonStyleName);
    }
}
