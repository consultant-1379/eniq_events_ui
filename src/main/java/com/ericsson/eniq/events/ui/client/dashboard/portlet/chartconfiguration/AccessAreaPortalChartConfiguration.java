/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet.chartconfiguration;

import com.ericsson.eniq.events.common.client.datatype.ChartItemDataType;
import com.ericsson.eniq.events.highcharts.client.ChartEnums.eAlign;
import com.ericsson.eniq.events.highcharts.client.ChartEnums.eLayout;
import com.ericsson.eniq.events.highcharts.client.ChartEnums.eVerticalAlign;
import com.ericsson.eniq.events.highcharts.client.ChartEnums.eZoomType;
import com.ericsson.eniq.events.highcharts.client.RawStringType;
import com.ericsson.eniq.events.highcharts.client.config.HCBarWithLineChartConfiguration;
import com.ericsson.eniq.events.highcharts.client.options.BaseOptions;
import com.ericsson.eniq.events.highcharts.client.options.CSSStyleOptions;
import com.ericsson.eniq.events.highcharts.client.options.ChartOptions;
import com.ericsson.eniq.events.highcharts.client.options.ChartTitleOptions;
import com.ericsson.eniq.events.highcharts.client.options.ExportingOptions;
import com.ericsson.eniq.events.highcharts.client.options.LegendItemStyleOptions;
import com.ericsson.eniq.events.highcharts.client.options.LegendOptions;
import com.ericsson.eniq.events.highcharts.client.options.SeriesOptions;
import com.ericsson.eniq.events.highcharts.client.options.ToolTipOptions;
import com.ericsson.eniq.events.highcharts.client.options.YAxisOptions;
import com.ericsson.eniq.events.highcharts.client.options.axis.AxisLabelOptions;
import com.ericsson.eniq.events.highcharts.client.options.axis.AxisTitleOptions;
import com.ericsson.eniq.events.highcharts.client.options.series.MarkerOptions;
import com.ericsson.eniq.events.highcharts.client.options.series.PointEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.ericsson.eniq.events.highcharts.client.ChartConstants.MAX_LABEL_ITEM_LENGTH;

/**
 * @author echimma
 * @since 2012
 */
public class AccessAreaPortalChartConfiguration extends HCBarWithLineChartConfiguration {

   /* (non-Javadoc)
   * @see com.ericsson.eniq.events.highcharts.client.config.HCBarChartConfiguration#buildChart()
   */
   @Override
   public void buildChart() {

      super.buildChart();
      setChartLoadFunction("$wnd.onAxesChartLoad(this, '" + getChartName() + "'," + MAX_LABEL_ITEM_LENGTH
              + ");$wnd.setupXAxisLabelTooltips(this);");
   }

   @Override
   protected ExportingOptions getExportingOptions() {
      final ExportingOptions exportingOptions = new ExportingOptions();
      exportingOptions.setEnabled(false);
      return exportingOptions;
   }

   /* (non-Javadoc)
   * @see com.ericsson.eniq.events.highcharts.client.config.HCBarWithLineChartConfiguration#createSeries(com.ericsson.eniq.events.ui.client.datatype.ChartItemDataType)
   */
   @Override
   protected SeriesOptions getSeriesOptions(final ChartItemDataType seriesItem) {
      final SeriesOptions seriesOptions = super.getSeriesOptions(seriesItem);
      seriesOptions.setBorderColor("#4c4c4c");
      seriesOptions.setBorderWidth(1);
      seriesOptions.setPointWidth(18);
      seriesOptions.setShadow(false);
      final PointEvents pointEvents = new PointEvents();
      pointEvents.setLegendItemClickEvent(new RawStringType("function(event) {return false;}"));
      seriesOptions.setEvents(pointEvents);
      return seriesOptions;
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

   @Override
   protected void setLegendOptions() {
      final LegendOptions legendOptions = new LegendOptions();
      legendOptions.setAlign(eAlign.center);
      legendOptions.setVerticalAlign(eVerticalAlign.bottom);
      legendOptions.setLayout(eLayout.horizontal);
      getLegendStyleOptions(legendOptions);
      setLegendOptions(legendOptions);
   }

   private void getLegendStyleOptions(final LegendOptions legendOptions) {
      final LegendItemStyleOptions style = new LegendItemStyleOptions();
      style.setFontSize("10px");
      style.setColor("#333333");
      legendOptions.setItemStyle(style);
   }

   /* (non-Javadoc)
   * @see com.ericsson.eniq.events.highcharts.client.config.HCBarChartConfiguration#getChartOptions(java.lang.String, java.lang.String)
   */
   @Override
   protected ChartOptions getChartOptions(final String containerDiv) {
      final ChartOptions chartOptions = super.getChartOptions(containerDiv);
      chartOptions.setZoomType(eZoomType.none);
      chartOptions.setProperty("spacingRight", 20);
      chartOptions.setProperty("spacingLeft", 20);
      chartOptions.setProperty("spacingBottom", 60);
      chartOptions.setProperty("spacingTop", 40);
      chartOptions.setProperty("marginRight", 40);
      chartOptions.setProperty("reflow", false);
      /** Set the height and width here related to the height/width of the actual container element, which is the parent of the high charts
       * frame **/
      final Element chartElement = DOM.getElementById(containerDiv);
      if (chartElement != null) {
         chartOptions.setHeight(chartElement.getParentElement().getClientHeight());
      }
      return chartOptions;
   }

   /* (non-Javadoc)
   * @see com.ericsson.eniq.events.highcharts.client.config.AbstractAxesChartConfiguration#getAxisLabelOptions()
   */
   @Override
   protected AxisLabelOptions getXAxisLabelOptions() {
      return getTerminalXAxisLabelOptions();
   }

   private CSSStyleOptions getTextStyleOptions() {
      final CSSStyleOptions style = new CSSStyleOptions();
      style.setFontSize("10px");
      style.setColor("#333333");
      style.setFontWeight("normal");
      return style;
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
   * @see com.ericsson.eniq.events.highcharts.client.config.AbstractChartConfiguration#getSeriesList()
   */
   @Override
   protected List<BaseOptions> getSeriesList() {
      return createSeriesList(Arrays.asList(getChartMetaData().itemInfo));
   }

   @Override
   protected void setTooltipOptions() {
      final ToolTipOptions toolTipOptions = new ToolTipOptions();
      toolTipOptions.setSnap(10);
      toolTipOptions.setFormatter(new RawStringType("function() {"
              + "return this.series.name+'<br/>'+this.point.name + ' : ' + this.point.y;}"));
      setTooltipOptions(toolTipOptions);
   }

   /* (non-Javadoc)
   * @see com.ericsson.eniq.events.highcharts.client.config.HCBarWithLineChartConfiguration#getMarkerOptions()
   */
   @Override
   protected MarkerOptions getMarkerOptions() {
      final MarkerOptions markerOptions = new MarkerOptions();
      markerOptions.setRadius(7);
      return markerOptions;
   }

   /**
    * Get the YAxis Options. Override to Specialize
    *
    * @param yAxisOptions
    *
    * @return
    */
   @Override
   protected YAxisOptions getYAxisOptions() {
      final YAxisOptions yAxisOptions = new YAxisOptions();
      yAxisOptions.setLabelOptions(getTerminalYAxisLabelOptions());
      final AxisTitleOptions yAxisTitle = getYAxisTitleOptions();
      yAxisOptions.setTitleOptions(yAxisTitle);
      yAxisOptions.setLineColor("#b2b2b2");
      return yAxisOptions;
   }

   /** @return  */
   private AxisLabelOptions getTerminalYAxisLabelOptions() {
      final AxisLabelOptions options = new AxisLabelOptions();
      options.setStyleOptions(getTextStyleOptions());
      return options;
   }

   /** @return  */
   private AxisLabelOptions getTerminalXAxisLabelOptions() {
      final AxisLabelOptions options = new AxisLabelOptions();
      options.setStyleOptions(getTextStyleOptions());
      //Here need to set the rotation to be sideling. Because the Access Area is long.
      options.setRotation(-45);
      //Here need to set the xAxis label position.
      options.setY(20);
      options.setAlign(eAlign.right);
      options.setFormatter(new RawStringType("function() {return $wnd.truncateAxisLabels(this.value, 5);}"));
      return options;
   }

   /**
    * Get the YAxis Label Options. Override to Specialize
    *
    * @return
    */
   @Override
   protected AxisTitleOptions getYAxisTitleOptions() {
      final AxisTitleOptions titleOptions = new AxisTitleOptions();
      titleOptions.setText(getChartMetaData().ylabel);
      titleOptions.setProperty("margin", 20);
      titleOptions.setStyleOptions(getTextStyleOptions());
      return titleOptions;
   }

   /* (non-Javadoc)
   * @see com.ericsson.eniq.events.highcharts.client.config.AbstractAxesChartConfiguration#getRightAxisTitleOptions(com.ericsson.eniq.events.ui.client.datatype.ChartItemDataType)
   */
   @Override
   protected AxisTitleOptions getRightAxisTitleOptions(final ChartItemDataType seriesItem) {
      final AxisTitleOptions titleOptions = super.getRightAxisTitleOptions(seriesItem);
      titleOptions.setProperty("margin", 20);
      titleOptions.setStyleOptions(getTextStyleOptions());
      return titleOptions;
   }
}
