/*
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.dashboard.portlet.infopanels;


import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface PortletPercentResourceBundle extends ClientBundle {

    @Source("images/triangle_up.png")
    ImageResource triangleUp();

    @Source("images/triangle_down.png")
    ImageResource triangleDown();

    @Source("images/triangle_right.png")
    ImageResource triangleEquals();

}
