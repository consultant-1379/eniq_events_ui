/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

/**
 * Holds data that is used when a window launchs from a hyperlink
 * @author eendmcm
 * @since June 2010
 */
public class LaunchWinDataType implements IHyperLinkDataType {

    public enum DrillTargetType {
        CHART("chart"), GRID("grid");
        private String type;

        private DrillTargetType(String type) {
            this.type = type;
        }

        public static DrillTargetType fromString(String type) {
            for (DrillTargetType targetType : DrillTargetType.values()) {
                if (targetType.toString().equalsIgnoreCase(type)) {
                    return targetType;
                }
            }
            /** Default to GRID **/
            return GRID;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return type;
        }
    }

    /**
     * drill down unique identifier
     */
    public String id = EMPTY_STRING;

    /**
     * details the menu Item that contains
     * the details of the launch window
     */
    public String menuItem = EMPTY_STRING;

    /**
     * details the column to use for the
     * search value that will be passed to
     * the menu item
     */
    public String searchValFromCol = EMPTY_STRING;

    /**
     * details the type e.g. BSC / APN etc
     * this is needed when the SearchDataType object 
     * is dynamically built
     */
    public String type = EMPTY_STRING;

    /**
     * the parameter info to pass to the method that will 
     * launch the new window - reuse the same dataType used for drill downs
     */
    public DrillDownParameterInfoDataType[] params = null;

    /** Launch a chart or grid. default = grid **/
    public DrillTargetType drillTargetType = DrillTargetType.GRID;

    public boolean isDisablingTime=false;

    /**
     * construct a dataType with info pertaining to the LaunchWindow
     * @param id - uniqueIdentifier of the Launch Window
     */
    public LaunchWinDataType(final String id) {
        this.id = id;
    }

    //////////////////////////////////////////////
    ///////////    Implement IHyperLinkDataType
    //////////////////////////////////////////////

    @Override
    public DrillDownParameterInfoDataType[] getParams() {
        return params;
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

}
