/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import java.util.List;

import com.ericsson.eniq.events.common.client.datatype.ThresholdDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.google.gwt.user.client.ui.Composite;

/**
 * Using class to have search and time data available for PortletTemplate
 * This class is only serving to reduce repeat code.
 * 
 * XXXPortlet extends AbstractSearchTimeUserComposite implements PortletTemplate 
 * 
 * implements part of PortletTemplate, i.e
 * 
 *  #getSearchFieldData();
 *  #TimeInfoDataType getTimeFieldData();
 * 
 * @see com.ericsson.eniq.events.ui.client.dashboard.PortletTemplate
 * @see com.ericsson.eniq.events.ui.client.dashboard.DashboardPresenter.SucessResponseEventImpl
 * 
 * @author eeicmsy
 * @since Nov 2011
 *
 */
public abstract class AbstractSearchTimeUserComposite extends Composite {

    private SearchFieldDataType searchData;

    private TimeInfoDataType timeData;

    /**
     * Utility to get current search data associated with window (support drilldown)
     * @see com.ericsson.eniq.events.ui.client.dashboard.PortletTemplate
     * @return   current search data for window (that made server call to fetch data with)
     */
    public SearchFieldDataType getSearchFieldData() {
        return searchData;
    }

    /**
     * Utility to get current time data associated with window (support drilldown)
     * @see com.ericsson.eniq.events.ui.client.dashboard.PortletTemplate
     * @return   current time data for window (that made server call to fetch data with)
     */
    public TimeInfoDataType getTimeFieldData() {
        return timeData;
    }

    /**
     * Composite data update should also update current search and time data in force
     * 
     * @param searchData the searchData to set
     * @param timeData the timeData to set
     */
    protected void setSearchAndTimeData(final SearchFieldDataType searchData, final TimeInfoDataType timeData) {
        this.searchData = searchData;
        this.timeData = timeData;
    }

    protected ThresholdDataType getThresholdById(final List<ThresholdDataType> thresholdList, final String thresholdId) {
        for (final ThresholdDataType threshold : thresholdList) {
            if (thresholdId.equals(threshold.getId())) {
                return threshold;
            }
        }
        return null;
    }
}
