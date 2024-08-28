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
 * @author ecarsea
 * @since 2011
 *
 */
public interface ETabPanelResources extends ClientBundle {

    @Source({ "etabpanel/etabpanel.css", "etabpanel/etabpanel-light.css" })
    @NotStrict
    CssResource css();

    @Source("etabpanel/themes/light/ETabBar/tab.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource tabLight();

    @Source("etabpanel/themes/light/ETabBar/tabbar_bg.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource tabBarBgLight();

    @Source("etabpanel/themes/light/ETabPanel/tabpanel-bg.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource tabPanelBgLight();
}
