/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for Refresh of Window (grid or chart)
 * 
 * @author eendmcm
 * @since Mar 2010
 */
public interface RefreshWindowEventHandler extends EventHandler {

    /**
     * Windows listen to the event should refresh when the event raised
     * 
     * @param multiWinID    Contains Id of window that holds the window (grid, chart) 
     *                      been Refreshed (and relevant multi mode information)
     */
    void handleWindowRefresh(final MultipleInstanceWinId multiWinID);

    /**
     * Direct call to the window when know it is the correct window 
     * and don't need to check guards
     */
    void handleWindowRefresh();

}
