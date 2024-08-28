/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import java.util.Arrays;

import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;

/**
 * Holds information relating to
 * the configuration and display 
 * settings for drill down window instance
 * 
 * @author eendmcm
 * @since Mar2010
 */
public class DrillDownInfoDataType implements IHyperLinkDataType {

    /**
     * drill down unique identifier
     */
    public String id = EMPTY_STRING;

    /**
     * name used for display purposes on the drill
     * down window
     */
    public String name = EMPTY_STRING;

    /**
     * url of the service that will return the
     * data for the given drilldown 
     */
    public String url = EMPTY_STRING;

    /**
     * flag to determine if the drilldown window
     * is enabled
     */
    public boolean isEnabled = false;

    /**
     * string identifier of the toolBar type that 
     * will be displayed with the given drilldown
     */
    public String toolBarType = EMPTY_STRING;

    /**
     * the display type of the drilldown i.e. grid, barchart etc
     */
    public String displayType = EMPTY_STRING;

    /**
     * the style class associated with the drilldown
     */
    public String style = EMPTY_STRING;

    /**
     * the query parameter info to pass to the above url when
     * data is requested from a service
     */
    public DrillDownParameterInfoDataType[] queryParameters = null;

    /**
     * details the column to use for the
     * search value (has to be an integer)
     */
    public String searchValFromCol = EMPTY_STRING;

    /**
     * details the type e.g. BSC / APN etc
     * this is needed when the SearchDataType object 
     * is dynamically built
     */
    public String type = EMPTY_STRING;

    /**
     * (optional data)
     * the identifier of the grid layout to use if the grid definition
     * that is associated with the id is not been used.
     */
    public String gridDisplayID = EMPTY_STRING;

    /**
     * (optional data)
     * defines if the drilldown needs to carry the parameter
     * info that was associated with the initial window
     * and provided by the end user 
     */
    public SearchFieldUser needSearchParameter = SearchFieldUser.FALSE;

    /**
     * defines the max number of rows to return 
     */
    public String maxRowsParam = EMPTY_STRING;

    /**
     * to disable drill down time combo box in lte cfa/hfa
     * */
    public boolean isDisablingTime=false;

    /**
     * construct a dataType with info pertaining to the drilldown
     * @param id - uniqueIdentifier of the drill down
     */
    public DrillDownInfoDataType(final String id) {
        this.id = id;
    }

    //////////////////////////////////////////////
    ///////////    Implement IHyperLinkDataType
    //////////////////////////////////////////////

    @Override
    public DrillDownParameterInfoDataType[] getParams() {
        return queryParameters;
    }

    @Override
    public String getSearchValColumn() {
        return searchValFromCol;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean isDisablingTime() {
        return isDisablingTime;
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder(256);
        sb.append("DrillDownInfoDataType");
        sb.append("{id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", isEnabled=").append(isEnabled);
        sb.append(", isDisablingTime=").append(isDisablingTime);
        sb.append(", toolBarType='").append(toolBarType).append('\'');
        sb.append(", displayType='").append(displayType).append('\'');
        sb.append(", style='").append(style).append('\'');
        sb.append(", queryParameters=")
                .append(queryParameters == null ? "null" : Arrays.asList(queryParameters).toString());
        sb.append(", searchValFromCol='").append(searchValFromCol).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", gridDisplayID='").append(gridDisplayID).append('\'');
        sb.append(", needSearchParameter=").append(needSearchParameter);
        sb.append(", maxRowsParam='").append(maxRowsParam).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
