/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.events.handlers;

import java.util.Set;

import com.ericsson.eniq.events.ui.client.charts.ChartElementDetails;
import com.ericsson.eniq.events.ui.client.charts.IChartPresenter;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.events.HideShowChartElementEventHandler;

/**
 * Handle showing and hiding elements on chart
 * All chart types should be capable of hiding certain element
 * (requirement is only for line chart currently but this may change)
 *  
 * @author eeicmsy
 * @since March 2011
 */

public class ChartHideShowElementHandler implements HideShowChartElementEventHandler {

    private final MultipleInstanceWinId multiWinID;

    private final IChartPresenter currentChart;

    /**
     * Constructor taking window and chart reference
     * @param multiWinId      - id where window located (to avoid updating same window in wrong tab)
     *                          (accounting for multi-instances from menu item luanch
     *                          
     * @param currentChart   - current chart (bare in mind this can change)
     */
    public ChartHideShowElementHandler(final MultipleInstanceWinId multiWinId, final IChartPresenter currentChart) {

        this.multiWinID = multiWinId;
        this.currentChart = currentChart;

    }

    @Override
    public void handleShowChartElementsEvent(final MultipleInstanceWinId multiWinId,
            final Set<ChartElementDetails> chartElementDetails) {

        if (isBlockedByGuard(multiWinId)) {
            return;
        }
        currentChart.showChartElements(chartElementDetails); // could use conditional return and bind then but al same
    }

    @Override
    public void handleHideShowChartLegend(final MultipleInstanceWinId multiWinId) {

        if (isBlockedByGuard(multiWinId)) {
            return;
        }
        currentChart.hideShowChartLegend();

    }

    /*
     * Check if window is correct window to receive event
     * @param tabId          - id where window located (to avoid updating same window in wrong tab)
     * @param winId          - unique id for window 
     * @return               - false if this event has reached the correct window (not blocked by guard)
     */
    private boolean isBlockedByGuard(final MultipleInstanceWinId multiWinId) {
        return (!multiWinID.isThisWindowGuardCheck(multiWinId));

    }

}
