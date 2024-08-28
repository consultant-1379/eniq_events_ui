package com.ericsson.eniq.events.ui.client.datatype;

/**
 * -----------------------------------------------------------------------
 * Copyright (C) 2013 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */


/*Result from services for trend during a time period, currently 3 UP, DOWN, LEVEL
* Each has a unique style and tooltip*/

public enum GridCellTrendType {

    TREND_UP("trendUp", "Failures during the period are up"),
    TREND_DOWN("trendDown", "Failures during the period are down"),
    TREND_NO_CHANGE("trendNoChange", "Failures during the period are level");

    private String type;
    private String tooltip;


    GridCellTrendType(String type, String tooltip) {
        this.type = type;
        this.tooltip = tooltip;
    }

    public static GridCellTrendType getTrendFromCell(final String cellValue) {
        GridCellTrendType trendType = TREND_NO_CHANGE; //default
        if (cellValue.equalsIgnoreCase("UP")) {
            trendType = TREND_UP;
        } else if (cellValue.equalsIgnoreCase("DOWN")) {
            trendType = TREND_DOWN;
        }
        return trendType;
    }

    public String getToolTip() {
        return tooltip;
    }

    @Override
    public String toString() {
        return type;
    }
}
