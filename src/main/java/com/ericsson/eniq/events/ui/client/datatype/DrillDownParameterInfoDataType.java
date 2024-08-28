/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;

/**
 * datatype that holds the information pertaining
 * to the values that needs to be passed on 
 * a hyperlink click
 * 
 * @author eendmcm
 * @since Mar 2010
 */
public class DrillDownParameterInfoDataType {

    /**
     * String Name of Parameter that will be passed on the url
     */
    public String parameterName = EMPTY_STRING;

    /**
     * String identifier of column that holds the value that will be passed on the url
     */
    public String parameterValue = EMPTY_STRING;

    /**
     * flag to determine if a value needs to be retrieved
     * from the grid cell for this parameter
     */
    public boolean isFixedType = false;

    /**
     * flag to determine if a value needs to be accounted
     * for on the title of the drill down window
     */
    public boolean isTitleParam = false;

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder(128);
        sb.append("DrillDownParameterInfoDataType");
        sb.append("{parameterName='").append(parameterName).append('\'');
        sb.append(", parameterValue='").append(parameterValue).append('\'');
        sb.append(", isFixedType=").append(isFixedType);
        sb.append(", isTitleParam=").append(isTitleParam);
        sb.append('}');
        return sb.toString();
    }
}
