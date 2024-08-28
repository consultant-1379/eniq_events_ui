/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;

/**
 * @author eeicmsy
 *
 */
public class JSONUtilsTest extends TestEniqEventsUI {

    @Test
    public void isChartDataExpectedTrue() {
        final String chartData = "{'success':'true','errorDescription':'','yaxis_min':'0.0','yaxis_max':'0.0','data':[]}";

        assertEquals("chart data expected ", true, JSONUtils.isChartData(chartData));
    }

    @Test
    public void isChartDataExpectedFalseNull() {
        final String chartData = null;
        assertEquals("chart data ", false, JSONUtils.isChartData(chartData));
    }

    @Test
    public void isChartDataExpectedFalseGrid() {
        final String gridData = "{'success':'true','errorDescription':'','data':[]}";

        assertEquals("chart data not expected ", false, JSONUtils.isChartData(gridData));
    }

}
