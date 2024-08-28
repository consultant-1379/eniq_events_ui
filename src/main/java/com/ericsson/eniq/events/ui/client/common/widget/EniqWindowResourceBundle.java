package com.ericsson.eniq.events.ui.client.common.widget;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * -----------------------------------------------------------------------
 * Copyright (C) 2013 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
public interface EniqWindowResourceBundle extends ClientBundle{
    
    @ClientBundle.Source("EniqWindow.css")
    EniqWindowStyle style();
    
    public interface EniqWindowStyle extends CssResource{
        String noticeMe();
        String noticeMeAnimation();
    }
}
