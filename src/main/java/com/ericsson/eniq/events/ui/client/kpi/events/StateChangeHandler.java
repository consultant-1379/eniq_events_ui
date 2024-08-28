/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.kpi.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author eaajssa
 * @since 2012
 *
 */
public interface StateChangeHandler extends EventHandler{

    /**
     * @param iconType
     */
    void onStateChange(String iconType);
}
