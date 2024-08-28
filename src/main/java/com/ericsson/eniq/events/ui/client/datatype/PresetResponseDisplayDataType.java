/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import com.google.gwt.http.client.Response;

/**
 * Data type storing a windows "Response data (from success server call)"  
 * and current position settings at time of request to retrieve data.
 * 
 * Datatype intends for use in a toggle (graph to grid scenario) when 
 * we already have the server response information and we are now replacing an existing
 * window with its toggle equivalent (we want to fool the user into thinking its the same window, 
 * by remembering the previous window size and position and putting a new window in it place)  
 * 
 * @author eeicmsy
 * @since April 2010
 *
 */
public final class PresetResponseDisplayDataType {

    public Response responseObj;

    public final WindowPropertiesDataType winProps;

    public final SearchFieldDataType searchFieldValue;

    public final String lastRefreshedTimeStamp;

    public TimeInfoDataType timeSelectionData;

    /**
     * Presenters (charts and grids) should be able to return their Response object and 
     * current layout positions and size at time of calling 
     * 
     * Response object part is there to support toggling date from grid to a chart and vice versa
     * 
     * @param responseObj       - from successful server call for data
     * @param searchFieldValue  - The search field value that was in place when 
     *                            set up this data type (so that new window created from 
     *                            a toggle scenario will be initialising with orginal search field value 
     *                            and not a current one.
     *                            
     * @param winProperties   - window size and position information    
     * @param lastRefreshedTimeStamp - Time this data was fetched from server
     * @param timeSelectionData      - time selected in time dialog for window 
     */
    public PresetResponseDisplayDataType(final Response responseObj, final SearchFieldDataType searchFieldValue,
            final WindowPropertiesDataType winProps, final String lastRefreshedTimeStamp,
            final TimeInfoDataType timeSelectionData) {

        this.responseObj = responseObj;
        this.searchFieldValue = searchFieldValue;

        this.winProps = winProps;
        this.lastRefreshedTimeStamp = lastRefreshedTimeStamp;
        this.timeSelectionData = timeSelectionData;
    }

    public void nullifyResponse() {
        responseObj = null;
    }

    public void resetTimeSelectionData(final TimeInfoDataType parentTime) {
        timeSelectionData = parentTime;

    }

}
