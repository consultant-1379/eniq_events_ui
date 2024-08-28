/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.charts.highcharts.config;

import com.ericsson.eniq.events.common.client.datatype.ChartDataType;
import com.ericsson.eniq.events.common.client.datatype.ChartItemDataType;
import com.ericsson.eniq.events.highcharts.client.ChartEnums;
import com.ericsson.eniq.events.highcharts.client.HighChartsJS;
import com.ericsson.eniq.events.highcharts.client.RawStringType;
import com.ericsson.eniq.events.highcharts.client.config.HCBarChartConfiguration;
import com.ericsson.eniq.events.highcharts.client.options.AbstractAxisOptions;
import com.ericsson.eniq.events.highcharts.client.options.ChartOptions;
import com.ericsson.eniq.events.highcharts.client.options.ChartTitleOptions;
import com.ericsson.eniq.events.highcharts.client.options.ExportingOptions;
import com.ericsson.eniq.events.highcharts.client.options.LegendOptions;
import com.ericsson.eniq.events.highcharts.client.options.SeriesOptions;
import com.ericsson.eniq.events.highcharts.client.options.ToolTipOptions;
import com.ericsson.eniq.events.highcharts.client.options.XAxisOptions;
import com.ericsson.eniq.events.highcharts.client.options.YAxisOptions;
import com.ericsson.eniq.events.highcharts.client.options.axis.AxisLabelOptions;
import com.ericsson.eniq.events.highcharts.client.options.series.PointOptions;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.CoreNetworkKPIsPortlet;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.LteCoreNetworkKPIsPortlet;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

import java.util.Map;

/**
 * @author eshusai
 * @since 2012
 */
public class LteCoreNetKpiPortletChartConfiguration extends HCBarChartConfiguration {

   private ChartDataType graphConfigData;

   @Override
   protected ChartEnums.eSeriesType getSeriesType() {
      return ChartEnums.eSeriesType.bar;
   }

   /* (non-Javadoc)
   * @see com.ericsson.eniq.events.ui.client.charts.highcharts.config.HCBarChartConfiguration#getChartOptions(java.lang.String)
   */
   @Override
   protected ChartOptions getChartOptions(final String containerDiv) {
      final ChartOptions chartOptions = super.getChartOptions(containerDiv);
      chartOptions.setProperty("marginLeft", 145);
      chartOptions.setProperty("spacingTop", 0);
      chartOptions.setProperty("spacingBottom", 0);
      chartOptions.setProperty("marginRight", 15);
      chartOptions.setProperty("borderWidth", 0);
      chartOptions.setProperty("reflow", false);
      if (graphConfigData.id.equals(LteCoreNetworkKPIsPortlet.SUPPORTED_SUBSCRIBERS_EVENT)
              || graphConfigData.id.equals(LteCoreNetworkKPIsPortlet.ATTACH_FAILURE_EVENT)) {
         chartOptions.setProperty("marginTop", 28);
         chartOptions.setProperty("marginBottom", 0);
      } else if (graphConfigData.id.equals(LteCoreNetworkKPIsPortlet.PAGING_FAILURE_EVENT)
              || graphConfigData.id.equals(LteCoreNetworkKPIsPortlet.ATTACHED_SUBSCRIBERS_EVENT)) {
         chartOptions.setProperty("marginTop", 0);
         chartOptions.setProperty("marginBottom", 31);
      } else {
         chartOptions.setProperty("marginTop", 0);
         chartOptions.setProperty("marginBottom", 0);
      }
      /** Set the height and width here related to the height/width of the actual container element, which is the parent of the high charts
       * frame **/
      final Element chartElement = DOM.getElementById(containerDiv);
      if (chartElement != null) {
         chartOptions.setHeight(chartElement.getParentElement().getClientHeight());
      }
      chartOptions.setZoomType(ChartEnums.eZoomType.none);
      return chartOptions;
   }

   /* (non-Javadoc)
   * @see com.ericsson.eniq.events.ui.client.charts.highcharts.config.AbstractChartConfiguration#getChartTitleOptions()
   */
   @Override
   protected ChartTitleOptions getChartTitleOptions() {
      final ChartTitleOptions chartTitleOptions = super.getChartTitleOptions();
      if (getChartMetaData().chartTitle.isEmpty()) {
         chartTitleOptions.setText(null);
      }
      chartTitleOptions.setY(10);
      return chartTitleOptions;
   }

   @Override
   public void init(final HighChartsJS highChartsJS, final ChartDataType chartMetaData,
                    final Map<String, Object[]> data) {
      this.graphConfigData = chartMetaData;
      super.init(highChartsJS, chartMetaData, data);
   }

   @Override
   protected void setLegendOptions() {
      final LegendOptions legendOptions = new LegendOptions();
      legendOptions.setEnabled(false);
      setLegendOptions(legendOptions);
   }

   @Override
   protected XAxisOptions getXAxisOptions() {
      final XAxisOptions xAxisOptions = super.getXAxisOptions();
      final AxisLabelOptions axisLabelOptions = new AxisLabelOptions();
      axisLabelOptions.setRotation(0);
      xAxisOptions.setLabelOptions(axisLabelOptions);
      xAxisOptions.setTickLength(0);
      xAxisOptions.setGridLineWidth(0);
      xAxisOptions.setLineWidth(1);
      xAxisOptions.setTickMarkPlacement(AbstractAxisOptions.eTickMarkPlacement.ON);
      return xAxisOptions;

   }

   /* (non-Javadoc)
   * @see com.ericsson.eniq.events.ui.client.charts.highcharts.config.AbstractChartConfiguration#getCategories()
   */
   @Override
   protected Object[] getCategories() {
      /** No XAxis in data, only one series item and one row, so return the series name **/
      return new String[]{getChartMetaData().itemInfo[0].name};
   }

   @Override
   protected void setTooltipOptions() {
      final ToolTipOptions toolTipOptions = new ToolTipOptions();
      if (graphConfigData.id.equals(LteCoreNetworkKPIsPortlet.SUPPORTED_SUBSCRIBERS_EVENT)
              || graphConfigData.id.equals(LteCoreNetworkKPIsPortlet.ATTACHED_SUBSCRIBERS_EVENT)) {
         toolTipOptions.setFormatter(new RawStringType("function() {return this.y ;}"));
      } else {
         toolTipOptions.setFormatter(new RawStringType("function() {return this.y +'%';}"));
      }
      setTooltipOptions(toolTipOptions);
   }

   /* (non-Javadoc)
   * @see com.ericsson.eniq.events.ui.client.charts.highcharts.config.AbstractChartConfiguration#getRowCount()
   */
   @Override
   public int getRowCount() {
      /** Only one series in each of these charts, no axis so need to get the row count from the number of rows in the single series. **/
      return getDataSeriesMap().get(getChartMetaData().itemInfo[0].id) == null ? 0 : getDataSeriesMap().get(
              getChartMetaData().itemInfo[0].id).length;
   }

   @Override
   protected YAxisOptions getYAxisOptions() {
      final YAxisOptions yAxisOptions = super.getYAxisOptions();
      final Double min = Double.parseDouble(getChartMetaData().parameters
              .getParameter(LteCoreNetworkKPIsPortlet.YAXIS_MIN));
      final Double max = Double.parseDouble(getChartMetaData().parameters
              .getParameter(LteCoreNetworkKPIsPortlet.YAXIS_MAX));
      final AxisLabelOptions labelOptions = new AxisLabelOptions();

      if (graphConfigData.id.equals(LteCoreNetworkKPIsPortlet.SUPPORTED_SUBSCRIBERS_EVENT)
              || graphConfigData.id.equals(LteCoreNetworkKPIsPortlet.ATTACHED_SUBSCRIBERS_EVENT)) {
         yAxisOptions.setMin(0);
         yAxisOptions.setMax(max);
         yAxisOptions.setTickPixelInterval(50);
         yAxisOptions.setGridLineWidth(1);
         yAxisOptions.setAxisGridLineColor("#c0c0c0");
      } else {
         /** Min Value in Chart is to the nearest multiple of 20 below the chart value to a max of 80 as
          * Chart Max will not be larger than 100
          */
         yAxisOptions.setMin(min == null ? 0 : (Math.min(80, min)));
         yAxisOptions.setMax(100);
         yAxisOptions.setTickPixelInterval(50);
         yAxisOptions.setGridLineWidth(1);
         yAxisOptions.setAxisGridLineColor("#c0c0c0");
         labelOptions.setY(20);
      }
      if (Boolean.parseBoolean(getChartMetaData().parameters
              .getParameter(CoreNetworkKPIsPortlet.YAXIS_LABELS_DISABLED))) {
         labelOptions.setEnabled(false);
         yAxisOptions.setLineWidth(0);
         yAxisOptions.setTickWidth(0);
      }
      yAxisOptions.setLabelOptions(getLabelOptions(labelOptions));
      return yAxisOptions;
   }

   /**
    * @param options
    *
    * @return
    */
   private AxisLabelOptions getLabelOptions(final AxisLabelOptions options) {
      if (graphConfigData.id.equals(LteCoreNetworkKPIsPortlet.SUPPORTED_SUBSCRIBERS_EVENT)
              || graphConfigData.id.equals(LteCoreNetworkKPIsPortlet.ATTACHED_SUBSCRIBERS_EVENT)) {
         options.setFormatter(new RawStringType("function() {return this.value ;}"));
      } else {
         options.setFormatter(new RawStringType("function() {return this.value +'%';}"));
      }
      return options;
   }

   /* (non-Javadoc)
   * @see com.ericsson.eniq.events.ui.client.charts.highcharts.config.AbstractAxesChartConfiguration#getSeriesOptions(com.ericsson.eniq.events.ui.client.datatype.ChartItemDataType)
   */
   @Override
   protected SeriesOptions getSeriesOptions(final ChartItemDataType seriesItem) {
      final SeriesOptions seriesOptions = super.getSeriesOptions(seriesItem);
      seriesOptions.setBorderWidth(1);
      seriesOptions.setBorderColor("#4c4c4c");
      seriesOptions.setShadow(false);
      seriesOptions.setPointWidth(12);
      return seriesOptions;
   }

   @Override
   protected void setDrillablePoint(final ChartItemDataType chartItemDataType, final SeriesOptions seriesOptions,
                                    final int rowIndex, final PointOptions pointOptions) {
      if (Boolean.parseBoolean(getChartMetaData().parameters.getParameter(CoreNetworkKPIsPortlet.DRILLABLE_CHART))) {
         super.setDrillablePoint(chartItemDataType, seriesOptions, rowIndex, pointOptions);
      }
   }

   /*
   * (non-Javadoc)
   *
   * @see com.ericsson.eniq.events.ui.client.charts.highcharts.config.
   * AbstractAxesChartConfiguration#createPointOptions(java.lang.String,
   * java.lang.Object, int)
   */
   @Override
   protected PointOptions getPointOptions(final String category, final Object value, final int rowIndex) {
      final PointOptions pointOptions = new PointOptions();
      pointOptions.setName(category);
      if (value != null) {
         final double valueDbl = Double.valueOf(value.toString());
         if (graphConfigData.id.equals(LteCoreNetworkKPIsPortlet.SUPPORTED_SUBSCRIBERS_EVENT)
                 || graphConfigData.id.equals(LteCoreNetworkKPIsPortlet.ATTACHED_SUBSCRIBERS_EVENT)) {
            pointOptions.setY(valueDbl);
         } else {
            pointOptions.setY(Math.min(100, valueDbl));
         }
         if (!(getChartMetaData().getThresholds().isEmpty())) {
            if (valueDbl < getChartMetaData().getThresholds().get(0).getLowest()) {
               pointOptions.setColor("#B53A3D");
            }
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
   * @see com.ericsson.eniq.events.ui.client.charts.highcharts.config.AbstractChartConfiguration#getExportingOptions()
   */
   @Override
   protected ExportingOptions getExportingOptions() {
      final ExportingOptions exportingOptions = new ExportingOptions();
      exportingOptions.setEnabled(false);
      return exportingOptions;
   }

}
