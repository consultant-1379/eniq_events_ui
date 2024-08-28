/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for click of grid cell hyper link. 
 * 
 * @author eendmcm
 * @since Mar 2010
 */
public interface HyperLinkCellClickEventHandler extends EventHandler {

    /**
     * Handles the end user clicking on a hyperlink with a grid cell
     * 
     * @param multiWinId    unique window id with multi instance support (search field data)
     * @param value     - value contained in the grid cell
     * @param url       - the url that is associated with the hyperlink
     * @param rowIndex - the index within the current page of the row that contains the cell
     */
    void handleCellLinkClick(final MultipleInstanceWinId multiWinId, final String value, final String url,
            final int rowIndex);
}
