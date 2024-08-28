/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.google.gwt.event.shared.EventHandler;

/**
 * Handler interface for WidgetSpecificParamsChangeEvent
 * 
 * 
 * @author eeicmsy
 * @since Jan 2011
 *
 */
public interface WidgetSpecificParamsChangeEventHandler extends EventHandler {

    /**
     * Handle widgetSpecificParametersChange for a window
     * Handler of this action will  be expected to change the (MetaMenuItem) 
     * widgetSpecificParams and force a window refresh with these parameters
     *
     * @param tabId                - tab owner of the window
     * @param winId                - id of window
     * @param widgetSpecificParams - new widgetSpecificParams for the window
     * @param url                  - URI to go with these widgetParams (to avoid bread crumb, drilled down one being picked up)
     * @param searchData           - most recent reference to search data for window (to avoid race conditions on presenters search data)
     */
    void handleWidgetSpecificParamsChange(final String tabId, final String winId, final String widgetSpecificParams,
            final String url, final SearchFieldDataType searchData, boolean neverRelaunchWindow);

}
