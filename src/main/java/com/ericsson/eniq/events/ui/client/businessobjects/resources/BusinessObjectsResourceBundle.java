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
public interface BusinessObjectsResourceBundle extends ClientBundle {
    @Source("business_objects_logo.png")
    ImageResource businessObjectsImage();

    @Source("business_objects_icon.png")
    @ImageOptions(repeatStyle = RepeatStyle.None)
    ImageResource windowIcon();

    @Source("BusinessObjects.css")
    BusinessObjectsStyle style();

    interface BusinessObjectsStyle extends CssResource {

        String boPortletImage();

        String windowIconImage();

        String windowContainer();

        String filler();

        @ClassName("x-panel-bwrap")
        String xPanelBwrap();

        @ClassName("x-panel-body")
        String xPanelBody();
    }
}
