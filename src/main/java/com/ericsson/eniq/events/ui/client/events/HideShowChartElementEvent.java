/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import java.util.Set;

import com.ericsson.eniq.events.ui.client.charts.ChartElementDetails;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event fired when user wishes to hide or show (toggle) 
 * element displayed on a chart (e.g. hide a line on a chart)
 * 
 * @see com.ericsson.eniq.events.ui.client.events.handlers.ChartGridChangeEventHandler
 * 
 * @author eeicmsy
 * @since April 2010
 *
 */
public class HideShowChartElementEvent extends GwtEvent<HideShowChartElementEventHandler> {

    public final static Type<HideShowChartElementEventHandler> TYPE = new Type<HideShowChartElementEventHandler>();

    /*
     * id of window been updated containing
     * multi-instance window information 
     */
    private final MultipleInstanceWinId multiWinID;

    /*
     * Always handling multiple checkboxes (so one bind not several)
     */
    private final Set<ChartElementDetails> applicableMenuItems;

    /**
     * Constructor when not passing a line on chart to hide.
     * (slight hack for code reuse). When this constructor is used will 
     * use to hide or show the chart legend.
     * 
     * @param multiWinID  id of window been updated  - containing multi-instance window information 
     *
     */
    public HideShowChartElementEvent(final MultipleInstanceWinId multiWinId) {
        this(multiWinId, null);
    }

    /**
     * Event added to event bus when have a bunch of 
     * (chart element show-hide) checkboxes ticked (so only do one bind). 
     * 
     * @param multiWinID  id of window been updated  - containing multi-instance window information 
     * @param chartElementDetails     - check boxes selected (show line)
     */
    public HideShowChartElementEvent(final MultipleInstanceWinId multiWinId,
            final Set<ChartElementDetails> chartElementDetails) {

        this.multiWinID = multiWinId;
        this.applicableMenuItems = chartElementDetails;

    }

    @Override
    protected void dispatch(final HideShowChartElementEventHandler handler) {

        if (applicableMenuItems == null) {
            handler.handleHideShowChartLegend(multiWinID);
        } else {
            handler.handleShowChartElementsEvent(multiWinID, applicableMenuItems);
        }
    }

    @Override
    public Type<HideShowChartElementEventHandler> getAssociatedType() {
        return TYPE;
    }

}
