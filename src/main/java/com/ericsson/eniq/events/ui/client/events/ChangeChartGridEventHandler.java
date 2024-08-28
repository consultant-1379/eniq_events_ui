/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.ToolBarURLChangeDataType;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for ChangeChartEvent
 * 
 * @author eeicmsy
 * @since May 2010
 *
 */
public interface ChangeChartGridEventHandler extends EventHandler {

    /**
     * Handle notification to change a chart (e.g. from roaming by
     * country to roaming by operator) from view menu selection on a window 
     *  
     *
     * @param multiWinId - window initiating the request
     * @param eventID    - chart to change to,
     *                    e.g. ROAMING_BY_OPERATOR, ROAMING_BY_COUNTRY
     * @param urlInfo   - info for toolbar menu item is making its own new server calls
     *
     * @param chartClickDrillInfo - String for chart element clicked (may be useful for updating title bar when drill)
     * @param searchFieldVal   */
    void handleChangeGridChart(final MultipleInstanceWinId multiWinId, EventType eventID,
                               final ToolBarURLChangeDataType urlInfo, final String chartClickDrillInfo, String searchFieldVal);

}
