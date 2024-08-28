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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class EmptyPortlet implements PortletTemplate {

    @Override
    public Widget asWidget() {
        return new SimplePanel();
    }

    @Override
    public void init(final PortletDataType descriptor) {
    }

    @Override
    public void update(final JSONValue data, final SearchFieldDataType searchData, final TimeInfoDataType timeData) {
    }

    @Override
    public SearchFieldDataType getSearchFieldData() {
        return null;
    }

    @Override
    public TimeInfoDataType getTimeFieldData() {
        return null;
    }

}
