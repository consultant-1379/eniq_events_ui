/*
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.northpanel.infoicon;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface InfoIconResources extends ClientBundle {

    @Source("i.png")
    ImageResource inActive();

    @Source("i_active.png")
    ImageResource active();

    interface InfoIconCss extends CssResource {

        String infoIcon();
        String glow();
    }

    @Source("InfoIcon.css")
    InfoIconCss css();
}
