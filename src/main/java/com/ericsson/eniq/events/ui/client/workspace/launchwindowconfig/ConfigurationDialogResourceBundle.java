/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launchwindowconfig;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author egallou
 * @since 2013
 */
public interface ConfigurationDialogResourceBundle extends ClientBundle {

    @Source("css/ConfigurationDialog.css")
    ConfigurationDialogStyle style();

    @Source("images/Plus_Enabled.png")
    ImageResource plusButton();

    @Source("images/Minus_Enabled.png")
    ImageResource minusButton();

    @Source("images/Plus_Hover.png")
    ImageResource plusButtonHover();

    @Source("images/Minus_Hover.png")
    ImageResource minusButtonHover();

    @Source("images/Plus_Disabled.png")
    ImageResource plusButtonDisable();

    @Source("images/Minus_Disabled.png")
    ImageResource minusButtonDisable();

    /*footer status message*/
    @Source("images/info_icon.png")
    ImageResource infoIcon();

    @Source("images/warning_icon.png")
    ImageResource warningIcon();


    interface ConfigurationDialogStyle extends CssResource {
        String plusButton();

        String plusButtonHover();

        String plusButtonDisable();

        String minusButton();

        String minusButtonHover();

        String minusButtonDisable();

        String gridStyle();

        String warningButton();
    }
}
