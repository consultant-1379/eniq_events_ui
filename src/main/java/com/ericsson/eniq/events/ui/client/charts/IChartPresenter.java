/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.charts;

import java.util.Map;
import java.util.Set;

import com.ericsson.eniq.events.common.client.datatype.ChartDataType;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * Interface to a Chart implementation for JSON Data
 * @author ecarsea
 * @since 2011
 *
 */
public interface IChartPresenter {

    /**
     * Hide or Show the Chart
     * @param b
     */
    void setVisible(boolean b);

    /**
     * Add a listener for Drill down on Chart elements
     * @param drillDownListener
     */
    void addChartDrillDownListener(IChartElementDrillDownListener drillDownListener);

    /**
     * Method to show or hide elements of a chart
     * @param chartElementIds - Chart Series Ids to be shown
     */
    boolean showChartElements(final Set<ChartElementDetails> chartElementIds);

    /**
     * Toggle the display of the chart legend
     */
    boolean hideShowChartLegend();

    /**
     * Set the Chart Meta Data
     * @param meta
     */
    void setConfigData(ChartDataType meta);

    /**
     * Get the Chart Widget
     * @return
     */
    Widget asWidget();

    /**
     * Get number of rows in chart
     * @return
     */
    int getChartRowCount();

    /**
     * Utility to update chart title (as apposed to window title)
     * Expected to take existing window title and append the passed parameter in as a prefix
     * 
     * @param extraTitlePrefix e.g. pass "Cause Code 16"  to become  "Cause Code 16 Sub Cause Code Analysis"
     */
    void addSubTitle(final String extraTitlePrefix);

    /**
     * Update the chart Data - The chart widget must be rendered before this is called.
     * @param chartData
     */
    void updateData(JSONValue chartData);

    /**
     * @param displayType - type of chart to show
     */
    void init(String displayType);

    /**
     * Update data with already processed data. Use this method in the case where manipulation of the JSON response from the service needs
     * to be performed before passing the data to the chart configuration.
     * @param seriesMap - map containing all chart series
     */
    void updateData(Map<String, Object[]> seriesMap);
}
