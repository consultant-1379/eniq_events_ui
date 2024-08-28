/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.util;

import com.google.gwt.user.client.DOM;

/**
 * DOM Utility class
 *  
 * @author Pedro Tavares - epedtav
 * @since 2011
 *
 */
public class DOMUtils {

    private DOMUtils() {
        // Don't instantiate
    }

    /**
     * Sets the "id" attribute of a given element 
     * 
     * @param element the element to set the attribute on
     * @param id the attribute value
     */
    public static void setElementId(final com.google.gwt.user.client.Element element, final String id) {
        DOM.setElementAttribute(element, "id", id);
    }
}
