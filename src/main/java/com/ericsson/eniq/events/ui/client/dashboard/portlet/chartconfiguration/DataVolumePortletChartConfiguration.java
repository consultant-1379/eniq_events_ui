/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet.chartconfiguration;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.eniq.events.common.client.datatype.ChartItemDataType;
import com.ericsson.eniq.events.highcharts.client.ChartEnums.eZoomType;
import com.ericsson.eniq.events.highcharts.client.config.HCLineChartConfiguration;
import com.ericsson.eniq.events.highcharts.client.options.BaseOptions;
import com.ericsson.eniq.events.highcharts.client.options.ChartOptions;
import com.ericsson.eniq.events.highcharts.client.options.ChartTitleOptions;
import com.ericsson.eniq.events.highcharts.client.options.ExportingOptions;
import com.ericsson.eniq.events.highcharts.client.options.LegendOptions;
import com.ericsson.eniq.events.highcharts.client.options.SeriesOptions;
import com.ericsson.eniq.events.highcharts.client.options.YAxisOptions;
import com.ericsson.eniq.events.highcharts.client.options.axis.AxisTitleOptions;
import com.ericsson.eniq.events.highcharts.client.options.series.PointOptions;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

/**
 * 
 * @author ecarsea
 * @since 2011
 * 
 */
public class DataVolumePortletChartConfiguration extends HCLineChartConfiguration {

    private String seriesColor = "#9ec057";

    /* (non-Javadoc)
         * @see com.ericsson.eniq.events.highcharts.client.config.AbstractAxesChartConfiguration#buildChart()
         */
    @Override
    public void buildChart() {
        super.buildChart();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractAxesChartConfiguration#getXAxisTitleOptions()
     */
    @Override
    protected AxisTitleOptions getXAxisTitleOptions() {
        final AxisTitleOptions titleOptions = new AxisTitleOptions();
        titleOptions.setText(null);
        return titleOptions;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractAxesChartConfiguration#getXAxisTitleOptions()
     */
    @Override
    protected AxisTitleOptions getYAxisTitleOptions() {
        final AxisTitleOptions titleOptions = super.getYAxisTitleOptions();
        titleOptions.setMargin(20);
        return titleOptions;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.HCLineChartConfiguration#setLegendOptions()
     */
    @Override
    protected void setLegendOptions() {
        final LegendOptions legendOptions = new LegendOptions();
        legendOptions.setEnabled(false);
        super.setLegendOptions(legendOptions);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractChartConfiguration#getSeriesList()
     */
    @Override
    protected List<BaseOptions> getSeriesList() {

        final List<BaseOptions> seriesList;

        if (!visibleSeries.isEmpty()) {
            seriesList = createSeriesList(visibleSeries);
        } else {
            /** Get a default series just in order to render an empty graph **/
            final SeriesOptions defaultSeries = new SeriesOptions();
            defaultSeries.setName(" ");
            defaultSeries.setType(getSeriesType());
            defaultSeries.setData(getDefaultDataSeriesValues());
            seriesList = new ArrayList<BaseOptions>();
            seriesList.add(defaultSeries);
        }
        return seriesList;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractChartConfiguration#getExportingOptions()
     */
    @Override
    protected ExportingOptions getExportingOptions() {
        final ExportingOptions exportingOptions = super.getExportingOptions();
        exportingOptions.setEnabled(false);
        return exportingOptions;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.HCLineChartConfiguration#getChartOptions(java.lang.String)
     */
    @Override
    protected ChartOptions getChartOptions(final String containerDiv) {
        final ChartOptions chartOptions = super.getChartOptions(containerDiv);
        chartOptions.setProperty("spacingRight", 0);
        chartOptions.setProperty("spacingLeft", 0);
        chartOptions.setProperty("spacingTop", 0);
        chartOptions.setProperty("spacingBottom", 0);
        chartOptions.setProperty("marginTop", 30);
        chartOptions.setProperty("marginLeft", 75);
        chartOptions.setProperty("marginBottom", 30);
        chartOptions.setProperty("marginRight", 61);
        chartOptions.setProperty("reflow", false);
        /** Set the height and width here related to the height/width of the actual container element, which is the parent of the high charts 
         * frame **/
        final Element chartElement = DOM.getElementById(containerDiv);
        if (chartElement != null) {
            chartOptions.setHeight(chartElement.getParentElement().getClientHeight());
        }
        chartOptions.setZoomType(eZoomType.none);
        return chartOptions;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractChartConfiguration#getChartTitleOptions()
     */
    @Override
    protected ChartTitleOptions getChartTitleOptions() {
        final ChartTitleOptions chartTitleOptions = super.getChartTitleOptions();
        chartTitleOptions.setY(22);
        return chartTitleOptions;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractAxesChartConfiguration#getYAxisOptions()
     */
    @Override
    protected YAxisOptions getYAxisOptions() {
        final YAxisOptions yAxisOptions = super.getYAxisOptions();
        yAxisOptions.setAxisGridLineColor("#c0c0c0");
        return yAxisOptions;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.HCLineChartConfiguration#getSeriesOptions(com.ericsson.eniq.events.ui.client.datatype.ChartItemDataType)
     */
    @Override
    protected SeriesOptions getSeriesOptions(final ChartItemDataType seriesItem) {
        final SeriesOptions seriesOptions = super.getSeriesOptions(seriesItem);
        seriesOptions.setColor(seriesColor);
        seriesOptions.setBorderColor("#4c4c4c");
        seriesOptions.setBorderWidth(1);
        seriesOptions.setShadow(false);
        return seriesOptions;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractAxesChartConfiguration#getPointOptions(java.lang.String, java.lang.Object, int)
     */
    @Override
    protected PointOptions getPointOptions(final String category, final Object value, final int rowIndex) {
        if (value != null) {
            final double point = Double.valueOf(value.toString());
            /** Have we thresholds?? **/
            if (getChartMetaData().getThresholds().size() > 0) {
                if (point < getChartMetaData().getThresholds().get(0).getLowest()
                        || point > getChartMetaData().getThresholds().get(0).getHighest()) {
                    seriesColor = "#d11616";
                }
            }
        }
        return super.getPointOptions(category, value, rowIndex);
    }
}
