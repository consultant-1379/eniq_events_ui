/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet.chartconfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ericsson.eniq.events.common.client.datatype.ChartDataType;
import com.ericsson.eniq.events.common.client.datatype.ChartItemDataType;
import com.ericsson.eniq.events.highcharts.client.HighChartsJS;
import com.ericsson.eniq.events.highcharts.client.RawStringType;
import com.ericsson.eniq.events.highcharts.client.ChartEnums.eSeriesType;
import com.ericsson.eniq.events.highcharts.client.ChartEnums.eZoomType;
import com.ericsson.eniq.events.highcharts.client.config.AbstractAxesChartConfiguration;
import com.ericsson.eniq.events.highcharts.client.options.BaseOptions;
import com.ericsson.eniq.events.highcharts.client.options.CSSStyleOptions;
import com.ericsson.eniq.events.highcharts.client.options.ChartOptions;
import com.ericsson.eniq.events.highcharts.client.options.ChartTitleOptions;
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
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

/**
 * Chart Template for a Homer / Roamer Chart. This is a specialized chart for displaying top homers/roamers
 *
 * @author emauoco
 * @since 2011
 */
public class HomerRoamerChartConfiguration extends AbstractAxesChartConfiguration {

    // Index of Previous bar.
    private static final int PREVIOUS_INDEX = 6;

    /*
     * Overriding init to remove Previous bar. Better to use JavaScriptObjects here etc.
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractChartConfiguration#init(com.ericsson.eniq.events.highcharts.client.HighChartsJS, com.ericsson.eniq.events.ui.client.datatype.ChartDataType, com.google.gwt.json.client.JSONValue)
     */
    @Override
    public void init(final HighChartsJS highChartsJS, final ChartDataType chartMetaData,
            final Map<String, Object[]> data) {

        /** Remove unwanted series **/
        data.remove(String.valueOf(PREVIOUS_INDEX));
        super.init(highChartsJS, chartMetaData, data);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractAxesChartConfiguration#buildChart()
     */
    @Override
    public void buildChart() {
        super.buildChart();

        setPlotOptions();
    }

    @Override
    protected ExportingOptions getExportingOptions() {
        final ExportingOptions exportingOptions = new ExportingOptions();
        exportingOptions.setEnabled(false);
        return exportingOptions;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractChartConfiguration#getChartOptions(java.lang.String)
     */
    @Override
    protected ChartOptions getChartOptions(final String containerDiv) {
        final ChartOptions chartOptions = super.getChartOptions(containerDiv);
        chartOptions.setSeriesType(eSeriesType.bar);
        chartOptions.setZoomType(eZoomType.none);
        chartOptions.setProperty("spacingRight", 40);
        chartOptions.setProperty("spacingLeft", 20);
        chartOptions.setProperty("spacingBottom", 20);
        chartOptions.setProperty("spacingTop", 20);
        chartOptions.setProperty("reflow", false);
        /** Set the height and width here related to the height/width of the actual container element, which is the parent of the high charts 
         * frame **/
        final Element chartElement = DOM.getElementById(containerDiv);
        if (chartElement != null) {
            chartOptions.setHeight(chartElement.getParentElement().getClientHeight());
        }
        return chartOptions;
    }

    @Override
    protected YAxisOptions getYAxisOptions() {
        final YAxisOptions yAxisOptions = super.getYAxisOptions();
        final AxisLabelOptions axisLabelOptions = new AxisLabelOptions();
        axisLabelOptions.setEnabled(false);
        yAxisOptions.setLabelOptions(axisLabelOptions);
        return yAxisOptions;
    }

    @Override
    protected XAxisOptions getXAxisOptions() {
        final XAxisOptions xAxisOptions = super.getXAxisOptions();
        final AxisLabelOptions axisLabelOptions = new AxisLabelOptions();
        axisLabelOptions.setRotation(0);
        xAxisOptions.setLabelOptions(axisLabelOptions);
        return xAxisOptions;
    }

    @Override
    protected void setTooltipOptions() {
        final ToolTipOptions toolTipOptions = new ToolTipOptions();
        toolTipOptions.setSnap(10);
        toolTipOptions.setFormatter(new RawStringType("function() {"
                + "return $wnd.toolTipFormatterRoamerPortalChart(this);}"));
        setTooltipOptions(toolTipOptions);
    }

    @Override
    protected void setLegendOptions() {
        final LegendOptions legendOptions = new LegendOptions();
        legendOptions.setEnabled(false);
        setLegendOptions(legendOptions);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractChartConfiguration#getSeriesList()
     */
    @Override
    protected List<BaseOptions> getSeriesList() {
        return createSeriesList(Arrays.asList(getChartMetaData().itemInfo));
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractChartConfiguration#showChartElements(java.util.Set)
     */
    @Override
    public boolean showChartElements(final Set<String> chartElementIds) {
        // No selection for chart elements in this chart
        return false;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractAxesChartConfiguration#getSeriesType()
     */
    @Override
    protected eSeriesType getSeriesType() {
        return eSeriesType.bar;
    }

    protected void setPlotOptions() {
        final PlotOptions plotOptions = new PlotOptions();
        final BarChartPlotOptions barChartPlotOptions = new BarChartPlotOptions();
        final DataLabelOptions dataLabelOptions = new DataLabelOptions();
        dataLabelOptions.setEnabled(true);
        final CSSStyleOptions style = new CSSStyleOptions();
        style.setFontSize("10px");
        style.setFontWeight("normal");
        dataLabelOptions.setColor("#333333");
        dataLabelOptions.setStyle(style);
        barChartPlotOptions.setDataLabelOptions(dataLabelOptions);
        plotOptions.setBarChartOptions(barChartPlotOptions);
        this.setPlotOptions(plotOptions);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.AbstractChartConfiguration#getChartTitleOptions()
     */
    @Override
    protected ChartTitleOptions getChartTitleOptions() {
        final ChartTitleOptions chartTitleOptions = super.getChartTitleOptions();
        chartTitleOptions.setText(null);
        return chartTitleOptions;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.HCBarWithLineChartConfiguration#createSeries(com.ericsson.eniq.events.ui.client.datatype.ChartItemDataType)
     */
    @Override
    protected SeriesOptions getSeriesOptions(final ChartItemDataType seriesItem) {
        final SeriesOptions seriesOptions = super.getSeriesOptions(seriesItem);
        seriesOptions.setBorderColor("#4c4c4c");
        seriesOptions.setBorderWidth(1);
        seriesOptions.setShadow(false);
        return seriesOptions;
    }

}
