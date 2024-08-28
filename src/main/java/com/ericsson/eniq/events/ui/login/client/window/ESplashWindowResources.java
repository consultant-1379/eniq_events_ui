/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.window;

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
public interface ESplashWindowResources extends ClientBundle {
    @Source({ "esplashwindow.css", "esplashwindow-light.css", "esplashwindow-dark.css" })
    @NotStrict
    CssResource css();

    @Source("themes/light/tl.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource topLeftLight();

    @Source("themes/light/tr.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource topRightLight();

    @Source("themes/light/bl.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource bottomLeftLight();

    @Source("themes/light/b.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource bottomLight();

    @Source("themes/light/t.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource topLight();

    @Source("themes/light/br.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource bottomRightLight();

    @Source("themes/light/l.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource leftLight();

    @Source("themes/light/r.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource rightLight();

    @Source("themes/light/ctl.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource collapsibleTopLeftLight();

    @Source("themes/light/ctr.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource collapsibleTopRightLight();

    @Source("themes/light/cbl.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource collapsibleBottomLeftLight();

    @Source("themes/light/cbr.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource collapsibleBottomRightLight();

    @Source("themes/light/shadow_overlay_9.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource shadowOverlayLight();

}
