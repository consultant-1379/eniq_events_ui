/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.component;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

/**
 * @author egallou
 * @since 2011
 *
 */
public interface ETeardropPopupResources extends ClientBundle {

    @Source({ "eteardropPopup/eteardropPopup.css", "eteardropPopup/eteardropPopup-light.css" })
    @NotStrict
    CssResource css();

    @Source("eteardropPopup/themes/light/ETeardropPopup/t_drop.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource t_light();

    @Source("eteardropPopup/themes/light/ETeardropPopup/tl_drop.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource tl_light();

    @Source("eteardropPopup/themes/light/ETeardropPopup/tr_drop.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource tr_light();

    @Source("eteardropPopup/themes/light/ETeardropPopup/teardrop_info.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource t_drop_light();

    @Source("eteardropPopup/themes/light/ETeardropPopup/action_menu_l.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource l_light();

    @Source("eteardropPopup/themes/light/ETeardropPopup/action_menu_r.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource r_light();

    @Source("eteardropPopup/themes/light/ETeardropPopup/action_menu_bl.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource bl_light();

    @Source("eteardropPopup/themes/light/ETeardropPopup/action_menu_b.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource b_light();

    @Source("eteardropPopup/themes/light/ETeardropPopup/action_menu_br.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource br_light();
}
