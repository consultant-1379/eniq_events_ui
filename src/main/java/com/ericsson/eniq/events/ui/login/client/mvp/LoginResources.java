/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.mvp;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public interface LoginResources extends ClientBundle {
    @Source({ "loginview.css", "loginview-light.css", "loginview-dark.css" })
    @NotStrict
    CssResource css();

    @Source("themes/light/caps_lock.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource capsLockLight();

    /*@Source("themes/light/globes.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource globesLight();*/

    @Source("themes/light/logo.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource logoLight();

    @Source("themes/light/OSS.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource eniqLight();

    @Source("themes/light/navigation_on.png")
    ImageResource navOnLight();

    @Source("themes/light/navigation_off.png")
    ImageResource navOffLight();

    @Source("themes/light/t.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource changePasswordPageSepLight();

    @Source("themes/light/middle_top_to_split.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource upperSplashPanelLight();

    @Source("themes/light/middle_bottom_to_split.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource lowerSplashPanelLight();

    @Source("themes/light/question_icon.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource passwordStrengthHelpLight();
}
