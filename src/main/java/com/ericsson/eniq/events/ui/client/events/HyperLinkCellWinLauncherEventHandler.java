/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for click of grid cell hyper link that launches a window.
 * This results in a brand new separate window being launched from the hyperlink
 * 
 * (e.g. clicking on  BSC hyperlink in ranking tab launching the Event Analysis Window)
 * 
 * @author eendmcm
 * @since June 2010
 * 
 */
public interface HyperLinkCellWinLauncherEventHandler extends EventHandler {

    /**
     * Handles the end user clicking on a hyperlink with a grid cell
     *  
     * This is specially CSS set up hyperlink when know have to launch a new window instead of
     * doing a drilldown. The new window being launched is usually a window which could also be launched 
     * from a different tab (e.g event analysis window from a node name clicked on a hyperlink), so it is 
     * important that the window is launched in the tab where the htperlink is pressed (hence tab id is passed also) 
     * 
     * @param multiWinID - id of window been updated  - containing multi-instance window information 
     * @param value      - value contained in the grid cell
     * @param launchID   - the id of the section in metadata that defines the window been launched 
     * @param rowIndex   - the index within the current page of the row that contains the cell
     */
    void handleCellLauncherClick(final MultipleInstanceWinId multiWinId, final String value, final String launchID,
            final int rowIndex);

}
