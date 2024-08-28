/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for toggle from a Graph Display to a Grid display
 * 
 * @author eendmcm
 * @author eeicmsy 
 * @since Apr 2010
 */
public interface GraphToGridEventHandler extends EventHandler {

    /**
     * Handle "toggle" button press to toggle presentation from
     * a chart to a grid or vice versa.
     * 
     * @param multiWinId                - id of window been updated (same as query id), 
     *                                    containing multi-instance window information 
     * @param shouldResetMeta           -  RESET (undo all toggling etc),  e.g. for search field update.
     * @param toolbarType               - toolbar type to put on grid
     * @param elementClickedForTitleBar - element to carry through for title bar (clicks on an element on the chart, want to 
     *                                    put the same on title bar of resultant grid)
     */
    void handleGraphToGridToggle(final MultipleInstanceWinId multiWinId, boolean shouldResetMeta, String toolbarType,
            final String elementClickedForTitleBar);

    /**
     * RESET (undo all toggling etc), e.g. for search field update.
     *  
     * Reset window to initial state (fresh metaMenu item),
     * - no matter what state the window has got into,
     * launch a complete new one in its place (position) using default window
     * metaMenuItem.
     * 
     * (Opposite of toggle - revert to window in same positition with fresh metamenu Item)
     * 
     * @param multiWinID - id of window been updated (same as query id), 
     *                     containing multi-instance window information 
     * @param data       - search field data
     */
    void handleGraphToGridToggleReset(final MultipleInstanceWinId multiWinId, final SearchFieldDataType data);

    /**
     * Handle "toggle" button press to toggle presentation from
     * a chart to a grid or vice versa.
     * @param multiWinID - id of window been updated (same as query id), 
     *                     containing multi-instance window information 
     *                     
     */
}
