/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.Map;

import com.ericsson.eniq.events.ui.client.common.URLParamUtils;

/**
 * Data type in use when have to collect toolbar menu item (in a window) data, 
 * where the toolbar action listener will cause a new server call to be initiated
 * (e.g. a toggle from a roaming by operator to a roaming by country scenario)
 * 
 * Will use this also when click down on a chart element to replace the window 
 * with a grid
 * 
 * @author eeicmsy
 * @since June 2010
 *
 */
public class ToolBarURLChangeDataType {

    /*
     * extras for toolbar menu items which can make new server calls inside there window
     * (e.g. switching from roaming by operator to country) 
     * This is assuming the "needSearchParam" and enabled params will be controlled by base window
     * the toolbar is launching windows inside) 
     */
    public String url, windowType, displayType, toolbarType, drillDownWindowType;

    /*
     * normally null except for case where view menu change is also ajusting
     * current time default that the window will be launched with 
     */
    private TimeInfoDataType tempTimeDefaultValue = null;

    /* ensure no duplicate params added  */
    private final URLParamUtils paramUtils;

    /**
     * key for extra parameter to add to URL for drilldown 
     * e.g groupname
     */
    public String urlDrillDownParamKey;

    /**
     * Construct when build up for chart drilldowns 
     * (not using a new url - appending to existing)
     */

    /**
     * flag that determines if this menu item has the potential to
     * display a different result set based on the provided parameter
     * i.e. the columns of the result set can vary and therefore the metadata
     * for the grid that is displayed needs to differ
     */
    public boolean hasMultiResult = false;

    public String maxRowsParam;

    public ToolBarURLChangeDataType() {
        this(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING);
    }

    /**
     * Data type information for toolbar menu items which make server calls
     *  
     * @param url               - web service from meta data (string from meta data)
     * @param displayType       - grid, bar, line  (string from meta data)
     * @param windowType        - CHART, or GRID  (string from meta data)
     * @param toolbarType       - the new toolbar to place on the new window we are creating 
     *                            if a new toolbar is being set
     * @param maxRowsParam      - JNDI key representing the max number of rows to be returned for the item in question
     */
    public ToolBarURLChangeDataType(final String url, final String displayType, final String windowType,
            final String toolbarType, final String maxRowsParam) {
        this.url = url;
        this.displayType = displayType;
        this.windowType = windowType;
        this.toolbarType = toolbarType;
        this.maxRowsParam = maxRowsParam;

        paramUtils = new URLParamUtils();

    }

    /**
     * Set temporary default time for window to launch with
     * @param tempTimeDefaultValue  time or null to reset to cached data from previous windows.
     */
    public void setTempTimeInfoDataType(final TimeInfoDataType tempTimeDefaultValue) {
        this.tempTimeDefaultValue = tempTimeDefaultValue;
    }

    /**
     * Return new time to launch window with (e.g. busy day chart needs default to be a week which 
     * will be different than the default (or last saved) for the other items being launched within the window)
     * @return  null or a default temporary time to launch the window with
     */
    public TimeInfoDataType getTempTimeInfoDataType() {
        return tempTimeDefaultValue;
    }

    /**
     * Add or over-write URL parameters to existing URL (after current initial params, exluding ? param)
     * (in existing set up of time is only one already there in 
     * method to reuse parameters in BaseWindowPresenter so
     * need to add display, e.g. 
     * 
     * addOutBoundParameter("display=", "grid");
     * addOutBoundParameter("groupname=", chartElementClicked);
     * 
     * NOTE ASSUMES KEY HAS "="
     *   
     * @param keyParam  key contain =, e.g. "&display="
     * @param value     value for key
     */
    public void addOutBoundParameter(final String keyParam, final String value) {
        paramUtils.addOutBoundRegularParameter(keyParam, value);
    }

    /**
     * Keep all paramters starting from & delimiter (ignore ? param)
     * Hack - keeping search component value
     * 
     * @param params  stringified parameters, e.g. &imsi=12345&display=chart  
     */
    public void keepExistingRegularParams(final String params) {
        paramUtils.keepExistingRegularParams(params);
    }

    public void replaceTimeParams(final TimeInfoDataType timeData) {
        paramUtils.replaceTimeParams(timeData);
    }

    /**
     * Keep all paramters starting from & delimiter (ignore ? param)
     * Hack - keeping search component value
     * @see URLParamUtils
     */
    public void replaceSearchDataParams(final String widgetSpecificUrlParams, final SearchFieldDataType data) {
        paramUtils.replaceSearchDataParams(widgetSpecificUrlParams, data);

    }

    /**
     * 'Get extra drilldown parameters 
     * @return parameters 'starting and separated by &
     */
    public String getWidgetSpecificParams() {
        return paramUtils.getWidgetSpecificParams();
    }

    /**
     * method to support some testing
     */
    Map<String, String> getParametersMap() {
        return paramUtils.getParametersMap();
    }

    public void replaceTimeDrillParams(TimeInfoDataType windowTimeDate) {
        paramUtils.replaceTimeDrillParams(windowTimeDate);
    }
}
