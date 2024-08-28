/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import java.util.Set;

import com.ericsson.eniq.events.ui.client.charts.ChartElementDetails;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.google.gwt.event.shared.EventHandler;

/**
 * Handle local client calls (all client no server) to hide or show a line on a chart.
 * Also can use this to hide or show entire legend
 * 
 * @author eeicmsy
 * @since April 2010
 * 
 *
 */
public interface HideShowChartElementEventHandler extends EventHandler {

    /**
     * Handle instruction to hide (if showing) or show (if hidden) 
     * a chart element (e.g. line) on the 
     * particular open chart window that 
     * initiated the call
     * 
     * @param multiWinID - id of window been updated  - containing multi-instance window information 
     * @param  chartElementDetails  - ticked checkbox elements (requiring these chart elements to be shown) 
     */
    void handleShowChartElementsEvent(final MultipleInstanceWinId multiWinId,
            final Set<ChartElementDetails> chartElementDetails);

    /**
     * Handle instruction to hide (if showing) or show (if hidden) 
     * the chart legend  on the 
     * particular open chart window that 
     * initiated the call
     * 
     * @param multiWinID - id of window been updated  - containing multi-instance window information    
     */
    void handleHideShowChartLegend(final MultipleInstanceWinId multiWinId);

}
