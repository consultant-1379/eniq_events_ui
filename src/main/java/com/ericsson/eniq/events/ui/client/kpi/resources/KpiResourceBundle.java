package com.ericsson.eniq.events.ui.client.kpi.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/*
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

/**
 * Style for KPI Panel.
 * 
 * @author eaajssa
 * @since Jan 2012
 */
public interface KpiResourceBundle extends ClientBundle {
    @Source("css/NotificationKpi.css")
    Style style();

    @Source("images/large_tab_top.png")
    ImageResource topImage();

    @Source("images/large_tab_bottom.png")
    ImageResource bottomImage();

    @Source("images/cog.png")
    ImageResource configImage();

    interface Style extends CssResource {
    }
}
