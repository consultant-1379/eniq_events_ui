/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet.chartconfiguration;

import com.ericsson.eniq.events.common.client.datatype.ChartItemDataType;
import com.ericsson.eniq.events.highcharts.client.RawStringType;
import com.ericsson.eniq.events.highcharts.client.ChartEnums.eAlign;
import com.ericsson.eniq.events.highcharts.client.ChartEnums.eSeriesType;
import com.ericsson.eniq.events.highcharts.client.ChartEnums.eZoomType;
import com.ericsson.eniq.events.highcharts.client.config.AbstractAxesChartConfiguration;
import com.ericsson.eniq.events.highcharts.client.options.ChartOptions;
import com.ericsson.eniq.events.highcharts.client.options.ExportingOptions;
import com.ericsson.eniq.events.highcharts.client.options.LegendOptions;
import com.ericsson.eniq.events.highcharts.client.options.PlotOptions;
import com.ericsson.eniq.events.highcharts.client.options.SeriesOptions;
import com.ericsson.eniq.events.highcharts.client.options.ToolTipOptions;
import com.ericsson.eniq.events.highcharts.client.options.XAxisOptions;
import com.ericsson.eniq.events.highcharts.client.options.YAxisOptions;
import com.ericsson.eniq.events.highcharts.client.options.axis.AxisLabelOptions;
import com.ericsson.eniq.events.highcharts.client.options.plot.BarChartPlotOptions;
import com.ericsson.eniq.events.highcharts.client.options.plot.DataLabelOptions;
import com.ericsson.eniq.events.highcharts.client.options.plot.SeriesPlotOptions;
import com.ericsson.eniq.events.highcharts.client.options.series.PointOptions;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

/**
 * Chart Template for a Network KPI Chart. This is a specialized chart for displaying Network KPI's
 * 
 * @author ecarsea
 * @since 2011
 * 
 */
public class NetworkKpiChartConfiguration extends AbstractAxesChartConfiguration {

    /* (non-Javadoc)
         * @see com.ericsson.eniq.events.highcharts.client.config.AbstractAxesChartConfiguration#buildChart()
         */
    @Override
    public void buildChart() {
        super.buildChart();
        setPlotOptions();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractChartConfiguration#getExportingOptions()
     */
    @Override
    protected ExportingOptions getExportingOptions() {
        final ExportingOptions exportingOptions = new ExportingOptions();
        exportingOptions.setEnabled(false);
        return exportingOptions;
    }

    @Override
    protected XAxisOptions getXAxisOptions() {
        final XAxisOptions xAxisOptions = super.getXAxisOptions();
        xAxisOptions.setTickLength(0);
        return xAxisOptions;
    }

    private void setPlotOptions() {
        final PlotOptions options = new PlotOptions();
        final SeriesPlotOptions seriesPlotOptions = new SeriesPlotOptions();
        options.setSeriesChartOptions(getSeriesPlotOptions(seriesPlotOptions));

        final BarChartPlotOptions barChartPlotOptions = new BarChartPlotOptions();
        options.setBarChartOptions(getBarChartPlotOptions(barChartPlotOptions));
        setPlotOptions(options);
    }

    /**
     * @param barChartPlotOptions
     * @return
     */
    private BarChartPlotOptions getBarChartPlotOptions(final BarChartPlotOptions barChartPlotOptions) {
        barChartPlotOptions.setBorderColors("#000000");
        barChartPlotOptions.setBorderWidth(0.6);
        final DataLabelOptions labelOptions = new DataLabelOptions();
        barChartPlotOptions.setDataLabelOptions(getDataLabelBarOptions(labelOptions));
        return barChartPlotOptions;
    }

    /**
     * @param labelOptions
     * @return
     */
    private DataLabelOptions getDataLabelBarOptions(final DataLabelOptions labelOptions) {
        labelOptions.setEnabled(false);
        return labelOptions;
    }

    /**
     * @param seriesPlotOptions
     * @return
     */
    private SeriesPlotOptions getSeriesPlotOptions(final SeriesPlotOptions seriesPlotOptions) {
        seriesPlotOptions.setPointPadding(0.2);
        return seriesPlotOptions;
    }

    @Override
    protected void setTooltipOptions() {
        final ToolTipOptions toolTipOptions = new ToolTipOptions();
        toolTipOptions.setFormatter(new RawStringType("function() {return this.y +'%';}"));
        setTooltipOptions(toolTipOptions);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractAxesChartConfiguration#getChartOptions(java.lang.String)
     */
    @Override
    protected ChartOptions getChartOptions(final String containerDiv) {
        final ChartOptions chartOptions = super.getChartOptions(containerDiv);
        chartOptions.setSeriesType(eSeriesType.bar);
        chartOptions.setProperty("marginRight", 15);
        chartOptions.setProperty("marginLeft", 70);
        chartOptions.setProperty("spacingTop", 0);
        chartOptions.setProperty("spacingBottom", 0);
        chartOptions.setProperty("marginBottom", 30);
        chartOptions.setProperty("marginTop", 30);
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
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractAxesChartConfiguration#getYAxisOptions()
     */
    @Override
    protected YAxisOptions getYAxisOptions() {
        final YAxisOptions yAxisOptions = super.getYAxisOptions();
        yAxisOptions.setMin(0);
        yAxisOptions.setMax(100);
        yAxisOptions.setTickPixelInterval(50);
        yAxisOptions.setAxisGridLineColor("#c0c0c0");
        final AxisLabelOptions options = new AxisLabelOptions();
        yAxisOptions.setLabelOptions(getLabelOptions(options));
        return yAxisOptions;
    }

    /**
     * @param options
     * @return
     */
    private AxisLabelOptions getLabelOptions(final AxisLabelOptions options) {
        options.setFormatter(new RawStringType("function() {return this.value +'%';}"));
        return options;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.eniq.events.highcharts.client.config.
     * AbstractAxesChartConfiguration#createPointOptions(java.lang.String,
     * java.lang.Object, int)
     */
    @Override
    protected PointOptions getPointOptions(final String category, final Object value, final int rowIndex) {
        final PointOptions pointOptions = new PointOptions();
        pointOptions.setName(category);
        if (value != null) {
            final double valueDbl = Double.valueOf(value.toString());
            pointOptions.setY(Math.min(100, valueDbl)); // Max of 100 - its a percentage map
            if (valueDbl < getChartMetaData().getThresholds().get(0).getLowest()) {
                pointOptions.setColor("#B53A3D");
            } else {
                pointOptions.setColor("#88A44D");
            }
            if (getRowCount() > 0) {
                setToolTipData(pointOptions, rowIndex);
            }
        } else {
            pointOptions.setY(value);
        }
        return pointOptions;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractChartConfiguration#getSeriesDataPoints(com.ericsson.eniq.events.ui.client.datatype.ChartItemDataType, com.ericsson.eniq.events.highcharts.client.options.SeriesOptions)
     */
    @Override
    protected PointOptions[] getSeriesDataPoints(final ChartItemDataType chartItemDataType,
            final SeriesOptions seriesOptions) {
        seriesOptions.setPointPadding(0.2);
        seriesOptions.setBorderColor("#4c4c4c");
        seriesOptions.setBorderWidth(1);
        seriesOptions.setShadow(false);
        return super.getSeriesDataPoints(chartItemDataType, seriesOptions);
    }

    @Override
    protected void setLegendOptions() {
        final LegendOptions legendOptions = new LegendOptions();
        legendOptions.setEnabled(false);
        setLegendOptions(legendOptions);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.eniq.events.highcharts.client.config.
     * AbstractAxesChartConfiguration#getSeriesType()
     */
    @Override
    protected eSeriesType getSeriesType() {
        return eSeriesType.bar;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractChartConfiguration#getCategories()
     */
    @Override
    protected Object[] getCategories() {
        return new String[] { "Success Rate" };
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractChartConfiguration#getRowCount()
     */
    @Override
    public int getRowCount() {
        return 1;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractAxesChartConfiguration#getXAxisLabelOptions()
     */
    @Override
    protected AxisLabelOptions getXAxisLabelOptions() {
        final AxisLabelOptions labelOptions = super.getXAxisLabelOptions();
        labelOptions.setRotation(0);
        labelOptions.setAlign(eAlign.right);
        return labelOptions;
    }
}
