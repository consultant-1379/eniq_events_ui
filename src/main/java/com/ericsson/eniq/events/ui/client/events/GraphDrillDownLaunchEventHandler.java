/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for drilling down on an element
 * in a Graph to invoke a call for a new
 * window with another widget
 *
 * @author eendmcm
 * @since 2011
 */
public interface GraphDrillDownLaunchEventHandler extends EventHandler {

    /**
     * Handle notification that end user clicks/selects and wishes to view
     * a new display instance in a new window
     *
     * @param tabOwnerId      - don't launch in multple tabs
     * @param info            - DataType retainining the information for the chart that needs to be displayed
     * @param chosenChart     - String for chart element clicked (may be useful for updating title bar when drill)
     * @param searchData      - the original search criteria provided on the window
     * @param winStyle        - the style used on the parent window
     * @param parentTime      - the time dataType from the parent window
     * @param queryParameters - query parameters specific to the chart element that has been clicked
     * @param searchFieldUser - the type of searchFieldUser
     */
    void handleLaunchFromGraphDrillDown(GraphDrillDownLaunchEvent graphDrillDownLaunchEvent);
}
