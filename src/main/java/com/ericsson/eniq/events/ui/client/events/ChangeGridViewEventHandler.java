/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import java.util.List;

import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;

/**
 * Handler for changing the grid view
 * 
 * @author esuslyn
 *
 */
public interface ChangeGridViewEventHandler extends EventHandler {

    /**
     * Handle drill down or navigation actions that require the grid to change its view
     * from a grid view to a grouping view or vice versa.
     * 
     * THIS IS NOW also used to move from regular grid (columns) to one with modified (licenced) headers 
     * custimised show/hide columns component
     * 
     */
    void handleChangeGridView(final MultipleInstanceWinId multiWinId, final String winTitle,
            final GridInfoDataType gridInfoDataType, final Response response, final JSONValue data,
            final List<Filter> filters, final Menu breadCrumbMenu, final SearchFieldDataType searchData,
            final String wsURL, final TimeInfoDataType timeInfo, final boolean isTogglingFromGraph, boolean isDrilling);

}
