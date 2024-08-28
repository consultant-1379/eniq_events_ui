/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import java.util.List;

import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;

/**
 * Event fired to event bus on drill down or navigation when update 
 * to the grid with a new set of data requires the grid to change its view
 * This indicates that the window needs convert the display from
 * a grid view to a grouping view or vice versa
 * @author esuslyn
 *
 */
public class ChangeGridViewEvent extends GwtEvent<ChangeGridViewEventHandler> {

    public static final Type<ChangeGridViewEventHandler> TYPE = new Type<ChangeGridViewEventHandler>();

    /*
     * id of window to update 
     * (same as query id that will be sent to URL) 
     */
    private final MultipleInstanceWinId multiWinId;

    private final String winTitle;

    private final GridInfoDataType gridInfoDataType;

    private final JSONValue data;

    private final Response response;

    private final Menu breadCrumbMenu;

    private final String wsURL;

    private final TimeInfoDataType timeData;

    private final boolean isTogglingFromGraph;

    private final List<Filter> filters;

    private final boolean isDrilling;

    public ChangeGridViewEvent(final MultipleInstanceWinId multiWinId, final String winTitle,
            final GridInfoDataType gridInfoDataType, final Response response, final JSONValue data,
            final List<Filter> filters, final Menu breadCrumbMenu, final String wsURL, final TimeInfoDataType timeInfo,
            final boolean isTogglingFromGraph, boolean isDrilling) {
        this.multiWinId = multiWinId;
        this.winTitle = winTitle;

        this.gridInfoDataType = gridInfoDataType;
        this.response = response;
        this.data = data;
        this.filters = filters;

        this.breadCrumbMenu = breadCrumbMenu;
        this.wsURL = wsURL;
        this.timeData = timeInfo;
        this.isTogglingFromGraph = isTogglingFromGraph;
        this.isDrilling = isDrilling;
    }

    @Override
    protected void dispatch(final ChangeGridViewEventHandler handler) {
        handler.handleChangeGridView(multiWinId, winTitle, gridInfoDataType, response, data, filters, breadCrumbMenu,
                null, wsURL, timeData, isTogglingFromGraph, isDrilling);

    }

    @Override
    public Type<ChangeGridViewEventHandler> getAssociatedType() {
        return TYPE;
    }

}
