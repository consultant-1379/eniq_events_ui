/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard;

import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Template for the content of a portlet.
 * 
 * @author edmibuz
 *
 */
public interface PortletTemplate extends IsWidget {

    /**
     * Static initialisation of the template. Data is not there yet only configuration.
     * 
     * @param descriptor of the portlet
     */
    void init(PortletDataType descriptor);

    /**
     * Dynamic part of the initialisation. JSON data is received from the server and passed.
     * The extra search and time information may need to be known for subsequent drill-downs (luanches)
     * 
     * @param data               - data for success response from server used to populate porlet
     * @param windowSearchData   - current search data for window (that made server call to fetch data with)
     * @param windowTimeData     - current time data for window (that made server call to fetch data with)
     */
    void update(JSONValue data, final SearchFieldDataType windowSearchData, final TimeInfoDataType windowTimeData);

    /**
     * Get current search data associated with window (support drilldown)
     * @return   current search data for window (that made server call to fetch data with)
     */
    SearchFieldDataType getSearchFieldData();

    /**
     * Get current time data associated with window (support drilldown)
     * @return   current time data for window (that made server call to fetch data with)
     */
    TimeInfoDataType getTimeFieldData();

}
