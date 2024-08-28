/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.component.interfaces;

/**
 * Implementations of this interface will be able minimize or fully
 * expand themselves, possibly using animation
 * 
 * @author peter.mucsi
 * @since 2011
 */
public interface ICollapsible {

    /**
     * Causes the widget to expand to show its full dimensions
     */
    void expand();

    /**
     * Causes the widget to minimize itself 
     */
    void collapse();
}
