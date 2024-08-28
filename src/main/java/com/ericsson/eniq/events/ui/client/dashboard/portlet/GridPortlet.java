/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import com.ericsson.eniq.events.ui.client.dashboard.PortletTemplate;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class GridPortlet implements PortletTemplate {

    private SearchFieldDataType windowSearchData;

    private TimeInfoDataType windowTimeData;

    @Override
    public Widget asWidget() {
        return new Label("Here be Grid!");
    }

    @Override
    public void init(final PortletDataType descriptor) {
    }

    @Override
    public void update(final JSONValue data, final SearchFieldDataType searchData, final TimeInfoDataType timeData) {
        // TODO data
        this.windowSearchData = searchData;
        this.windowTimeData = timeData;
    }

    @Override
    public SearchFieldDataType getSearchFieldData() {
        return windowSearchData;
    }

    @Override
    public TimeInfoDataType getTimeFieldData() {
        return windowTimeData;
    }

}
