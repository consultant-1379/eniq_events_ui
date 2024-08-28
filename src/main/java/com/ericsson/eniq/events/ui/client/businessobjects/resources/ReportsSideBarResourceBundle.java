/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.businessobjects.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public interface ReportsSideBarResourceBundle extends ClientBundle {

    @Source("business_objects_icon.png")
    ImageResource boIcon();

    @Source("tab_shadowless_open.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource boHandleOpen();

    @Source("tab_shadowless_close.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource boHandleClose();

    @Source("ReportsSideBarView.css")
    ReportsSideBarStyle style();

    interface ReportsSideBarStyle extends CssResource {
        String container();

        String inner();

        String header();

        String resizer();

        String footer();

        String handle();

        String boIcon();

        String reportTree();
    }
}
