/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.ToolBarURLChangeDataType;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.ericsson.eniq.events.ui.client.events.handlers.ChartGridChangeEventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * Event to change to a new chart from view menu on window
 * (e.g. if eventId indicates to change, could be 
 * changing from "roam by operator" to "roam by country" etc)
 * 
 * 
 * @author eeicmsy
 * @since May 2010
 *
 */
public class ChangeChartGridEvent extends GwtEvent<ChangeChartGridEventHandler> {

    public final static Type<ChangeChartGridEventHandler> TYPE = new Type<ChangeChartGridEventHandler>();

    /*
     * id of window to update 
     * (same as query id that will be sent to URL) 
     */
    private final MultipleInstanceWinId multiWinId;

    private final EventType eventID;

    private final ToolBarURLChangeDataType urlInfo;

    private final String chartClickDrillInfo;

    /**
     * Event to fire to change a chart (e.g. from roaming by
     * country to roaming by operator) from view menu selection on a window 
     *  
     *
     * @param multiWinId - window initiating the request
     * @param eventID    - new chart type to change to,
     *                    e.g. ROAMING_BY_OPERATOR, ROAMING_BY_COUNTRY
     * @param urlInfo    - URL and window information required to make a new call
     * @param chartClickDrillInfo - String for chart element clicked (may be useful for updating title bar when drill)
     */
    public ChangeChartGridEvent(final MultipleInstanceWinId multiWinId, final EventType eventID,
            final ToolBarURLChangeDataType urlInfo, final String chartClickDrillInfo) {
        this.multiWinId = multiWinId;
        this.eventID = eventID;
        this.urlInfo = urlInfo;
        this.chartClickDrillInfo = chartClickDrillInfo;

    }

    @Override
    protected void dispatch(final ChangeChartGridEventHandler handler) {
        handler.handleChangeGridChart(multiWinId, eventID, urlInfo, chartClickDrillInfo, ((ChartGridChangeEventHandler) handler).getSearchFieldVal());
    }

    @Override
    public Type<ChangeChartGridEventHandler> getAssociatedType() {
        return TYPE;
    }

}
