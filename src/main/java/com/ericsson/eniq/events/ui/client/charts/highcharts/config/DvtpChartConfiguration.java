/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.charts.highcharts.config;

import static com.ericsson.eniq.events.common.client.CommonConstants.*;
import static com.ericsson.eniq.events.highcharts.client.ChartConstants.*;

import java.util.*;

import com.ericsson.eniq.events.highcharts.client.RawStringType;
import com.ericsson.eniq.events.common.client.datatype.ChartDataType;
import com.ericsson.eniq.events.common.client.datatype.ChartItemDataType;
import com.ericsson.eniq.events.highcharts.client.ChartEnums.eSeriesType;
import com.ericsson.eniq.events.highcharts.client.HighChartsJS;
import com.ericsson.eniq.events.highcharts.client.options.BaseOptions;
import com.ericsson.eniq.events.highcharts.client.options.plot.ColumnChartPlotOptions;
import com.ericsson.eniq.events.highcharts.client.options.series.PointEvents;
import com.ericsson.eniq.events.ui.client.common.Constants;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.DOM;
import com.ericsson.eniq.events.highcharts.client.options.ChartOptions;
import com.ericsson.eniq.events.highcharts.client.options.LegendItemStyleOptions;
import com.ericsson.eniq.events.highcharts.client.options.LegendOptions;
import com.ericsson.eniq.events.highcharts.client.options.SeriesOptions;
import com.ericsson.eniq.events.highcharts.client.options.series.MarkerOptions;
import com.google.gwt.dom.client.Element;
import com.ericsson.eniq.events.highcharts.client.ChartEnums;
import com.ericsson.eniq.events.highcharts.client.config.AbstractAxesChartConfiguration;
import com.ericsson.eniq.events.highcharts.client.options.*;
import com.ericsson.eniq.events.highcharts.client.options.axis.AxisLabelOptions;
import com.ericsson.eniq.events.highcharts.client.options.axis.AxisTitleOptions;
import com.ericsson.eniq.events.highcharts.client.options.plot.SeriesPlotOptions;

public class DvtpChartConfiguration extends AbstractAxesChartConfiguration {

    public static final double FOURTY_FIVE_PERCENTAGE = 0.45;
    public static final double FIFTY_FIVE_PERCENTAGE = 0.55;
    public static final int OFFSET_PIXEL_FROM_TOP = 40;
    public static final int XAXIS_NEGATIVE_OFFSET_FROM_BOTTOM = -50;
    public static final int MAX_XAXIS_HEIGHT = 30;
    protected final List<ChartItemDataType> visibleSeries = new ArrayList<ChartItemDataType>();

    private final Map<String, ChartItemDataType> allSeries = new HashMap<String, ChartItemDataType>();

    private int windowHeight = 300;

    private int windowWidth = 600;

    double chartAreaHeight =0;

    private final static String HEIGHT_PARAM = "height";

    private final static String TOP_PARAM = "top";

    private final static String OFFSET_PARAM = "offset";

    private final static String BOTTOM_MARGIN_PARAM = "marginBottom";

    private final static String REFLOW_PARAM = "reflow";

    private final static String DATAVOL_AXIS_TITLE = "Data Volume";

    private final static String THROUGHPUT_AXIS_TITLE = "Throughput";

    private final static String SUBSCRIBER_AXIS_TITLE = "Subscribers";

    private final static String SESSION_AXIS_TITLE = "Session";

    private final static String THROUGHPUT_AXIS_ID = "1";

    private final static String SESSION_AXIS_ID = "3";

    private final static String DATAVOLUME_AXIS_ID = "0";

    private final static String SUBSCRIBER_AXIS_ID = "2";

    private DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(DATE_MINUTE_FORMAT);

    /**
     * Maintain own list of categories as we: Need to display categories for 1) No Data 2) All Data 3) No Data displayed, but available for selection
     */
    private Object[] categories;

    private HighChartsJS highChartsJS;

    private boolean isSubscriberAxisVisible;

    private int timeTickInterval;

    private long startTimeInMilliSeconds;

    private long endTimeInMilliSeconds;

    private int noOfTicks;

    private int timeZone;

    private boolean[] tickPresentFlag;

    private boolean isEntityChart = false;

    private int TIME_COLUMN_INDEX;

    private Map<Integer, Double> axisExtremes;

    @Override
    public void init(final HighChartsJS highChartsJS, final ChartDataType chartMetaData, Map<String, Object[]> data) {
        if (!chartMetaData.id.equalsIgnoreCase("DATAVOL_ANALYSIS_SUBSCRIBER")) {
            isSubscriberAxisVisible = true;
        }
        timeZone = parseTimeZoneOffset(chartMetaData.parameters.getParameter(Constants.DATA_TIMEZONE_PARMA_JSON_RESPONSE));
        axisExtremes = getMaxOfEachAxis(data);
        if (chartMetaData.xlabel != null && chartMetaData.xlabel.equalsIgnoreCase("Timestamp")) {
            //For Datavolume Roaming by country/Operator Xaxis is not time so dont create timeticks.
            isEntityChart = true;
            data = createTimeCategories(chartMetaData, data);
        }else{
            getChartParameters(chartMetaData);
        }

        super.init(highChartsJS, chartMetaData, data);
        setupSeriesMap();
        categories = super.getCategories();
        this.highChartsJS = highChartsJS;
    }

    private Map<Integer, Double> getMaxOfEachAxis(Map<String, Object[]> data) {
        if (data == null || data.size() == 0) {
            return null;
        }
        Map<Integer, Double> seriesMaxValues = new HashMap<Integer, Double>();
        for (int i = 1; i <= data.size(); i++) {
            if (i == TIME_COLUMN_INDEX + 1) {
                continue;
            }
            seriesMaxValues.put(i, getArrayMax(data.get(Integer.toString(i))));
        }

        Map<Integer, Double> maxForEachAxis = new HashMap<Integer, Double>(isSubscriberAxisVisible ? 4 : 3);
        maxForEachAxis.put(Integer.valueOf(DATAVOLUME_AXIS_ID), seriesMaxValues.get(2) + seriesMaxValues.get(3));
        maxForEachAxis.put(Integer.valueOf(THROUGHPUT_AXIS_ID), Math.max(seriesMaxValues.get(4), seriesMaxValues.get(5)));
        if (isSubscriberAxisVisible) {
            maxForEachAxis.put(Integer.valueOf(SUBSCRIBER_AXIS_ID), seriesMaxValues.get(6));
            maxForEachAxis.put(Integer.valueOf(SESSION_AXIS_ID), seriesMaxValues.get(7));
        } else {
            maxForEachAxis.put(Integer.valueOf(SESSION_AXIS_ID) - 1, seriesMaxValues.get(6));
        }

        return maxForEachAxis;
    }

    private Double getArrayMax(Object[] objects) {
        double max;
        if (objects == null || objects[0] == null) {
            return null;
        } else {
            max = Double.parseDouble(objects[0].toString());
        }
        for (int i = 1; i < objects.length; i++) {
            double nextValue = Double.parseDouble(objects[i].toString());
            if (nextValue > max) {
                max = nextValue;
            }
        }
        return max;
    }

    private Map<String, Object[]> createTimeCategories(ChartDataType chartMetaData, Map<String, Object[]> data) {
        getChartParameters(chartMetaData);
        tickPresentFlag = new boolean[noOfTicks];
        categories = computeTimeTicks(data == null ? null : data.get(chartMetaData.xAxisColID), timeTickInterval, startTimeInMilliSeconds,
                endTimeInMilliSeconds);
        data = addDataForMissingTimeTicks(data, categories);
        return data;
    }

    private void getChartParameters(ChartDataType chartMetaData) {
        startTimeInMilliSeconds = Long.parseLong(chartMetaData.parameters.getParameter(Constants.CHART_START_TIME_PARAM));
        endTimeInMilliSeconds = Long.parseLong(chartMetaData.parameters.getParameter(Constants.CHART_END_TIME_PARAM));
        if(isEntityChart){
        TIME_COLUMN_INDEX = Integer.parseInt(chartMetaData.xAxisColID);
        timeTickInterval = Integer.parseInt(chartMetaData.parameters.getParameter(Constants.CHART_TIME_TICK_INTERVAL_PARAM));

        noOfTicks = (int) (endTimeInMilliSeconds - startTimeInMilliSeconds) / (1000 * timeTickInterval * 60);
        }
    }

    private int parseTimeZoneOffset(String parameter) {
        int tzOffset = Integer.parseInt(parameter.substring(1, 3)) * 60 + Integer.parseInt(parameter.substring(3, 5));
        if (parameter.charAt(0) == '-') {
            return tzOffset;
        } else {
            return -tzOffset;
        }
    }

    private Object[] computeTimeTicks(Object[] categories, int timeTickInterval, long startTimeInMilliSeconds, long endTimeInMilliSeconds) {
        Object[] timeTicks = new Object[noOfTicks];
        DateTimeFormat chartTimeFormat;
        int timeDuration = (int) (endTimeInMilliSeconds - startTimeInMilliSeconds) / 60000;

        if (timeDuration <= Integer.parseInt(Constants.ONE_DAY_MS_TIME_PARAMETER)) {
            chartTimeFormat = DateTimeFormat.getFormat(Constants.TIME_IN_HH_MM_FORMAT);
        } else if (timeDuration < Integer.parseInt(Constants.ONE_WEEK_MS_TIME_PARAMETER)) {
            chartTimeFormat = DateTimeFormat.getFormat(DATE_MINUTE_FORMAT);
        } else {
            chartTimeFormat = DateTimeFormat.getFormat(DATE_ONLY_FORMAT);
        }
        TimeZone tz = TimeZone.createTimeZone(timeZone);

        final Date startTime = dateTimeFormat.parse(dateTimeFormat.format(new Date(startTimeInMilliSeconds), tz));
        final Date endTime = dateTimeFormat.parse(dateTimeFormat.format(new Date(endTimeInMilliSeconds), tz));
        Date nextExpectedTimeTick = startTime;
        for (int i = 0, j = 0; i < noOfTicks && nextExpectedTimeTick.before(endTime); i++) {
            timeTicks[i] = chartTimeFormat.format(nextExpectedTimeTick);
            if (categories != null && j < categories.length
                    && nextExpectedTimeTick.getTime() == (dateTimeFormat.parse(categories[j].toString())).getTime()) {
                tickPresentFlag[i] = true;
                j++;
            }
            nextExpectedTimeTick = new Date(nextExpectedTimeTick.getTime() + timeTickInterval * 60000);
        }
        return timeTicks;
    }

    private Map<String, Object[]> addDataForMissingTimeTicks(Map<String, Object[]> data, Object[] categories) {
        Map<String, Object[]> newData = new HashMap<String, Object[]>();
        if (data == null) {
            return null;
        }
        for (int i = 1; i <= data.size(); i++) {
            if (i == TIME_COLUMN_INDEX) {
                newData.put(Integer.toString(i), categories);
                continue;
            }

            Object[] seriesData = data.get(Integer.toString(i));
            Object[] newSeriesData = new Object[noOfTicks];
            for (int j = 0, k = 0; j < noOfTicks; j++) {
                if (tickPresentFlag[j]) {
                    newSeriesData[j] = seriesData[k];
                    k++;
                } else
                    newSeriesData[j] = null;
            }

            newData.put(Integer.toString(i), newSeriesData);
        }
        return newData;
    }

    /**
     * Set up the Map of all the series.
     */
    protected void setupSeriesMap() {
        for (final ChartItemDataType chartItemDataType : getChartMetaData().itemInfo) {
            allSeries.put(chartItemDataType.id, chartItemDataType);
            visibleSeries.add(chartItemDataType);
        }
    }

    @Override
    protected AxisTitleOptions getXAxisTitleOptions() {
        final AxisTitleOptions titleOptions = new AxisTitleOptions();
        titleOptions.setText(null);
        return titleOptions;
    }

    protected AxisTitleOptions getYAxisTitleOptions(final String title) {
        final AxisTitleOptions titleOptions = super.getYAxisTitleOptions();
        titleOptions.setText(title);
        return titleOptions;
    }

    @Override
    public void buildChart() {
        Element chartElement = DOM.getElementById(highChartsJS.getContainerDivId());
        if (chartElement != null) {
            windowHeight = chartElement.getClientHeight();
            windowWidth = chartElement.getClientWidth();
            chartAreaHeight = windowHeight - (OFFSET_PIXEL_FROM_TOP - XAXIS_NEGATIVE_OFFSET_FROM_BOTTOM + MAX_XAXIS_HEIGHT);
        }
        super.buildChart();
        addAxis(getThroughputYAxisOptions());
        if (isSubscriberAxisVisible) {
            addAxis(getSubscriberYAxisOptions());
        }
        addAxis(getSessionYAxisOptions());

        setPlotOptions();
    }

    protected void setTooltipOptions() {
        final ToolTipOptions toolTipOptions = new ToolTipOptions();
        String dataUnitSuffix = isSubscriberAxisVisible ? "MB" : "KB";
        StringBuilder formatter = new StringBuilder();
        formatter.append("function() {\n").append("var s = ' ';\n").append("if (this.point.tooltipData) {\n")
                .append("var total =parseFloat(this.point.tooltipData[0].value) + parseFloat(this.point.tooltipData[1].value);\n")
                .append("total=Math.round(total*100)/100;\n").append("s += 'Total Datavolume (" + dataUnitSuffix + ") : ' +  total + '<br/>';\n")
                .append("for (var i = 0; i < this.point.tooltipData.length; i++) {\n").append("s += this.point.tooltipData[i].name + ' : ';\n")
                .append("s += this.point.tooltipData[i].value + '<br/>';\n").append("}} \n").append("return s;\n").append("}");
        toolTipOptions.setFormatter(new RawStringType(formatter.toString()));
        setTooltipOptions(toolTipOptions);


    }

    protected void setPlotOptions() {
        final PlotOptions plotOptions = new PlotOptions();
        final SeriesPlotOptions seriesPlotOptions = new SeriesPlotOptions();
        final ColumnChartPlotOptions columnPlotOptions = new ColumnChartPlotOptions();
        columnPlotOptions.setStacking(ChartEnums.eStacking.normal);
        plotOptions.setColumnChartOptions(columnPlotOptions);
        seriesPlotOptions.setAnimationEnabled(true);
        plotOptions.setSeriesChartOptions(seriesPlotOptions);
        this.setPlotOptions(plotOptions);
    }

    @Override
    protected void setLegendOptions() {
        final LegendOptions legendOptions = new LegendOptions();
        final LegendItemStyleOptions styleOptions = new LegendItemStyleOptions();
        styleOptions.setFontFamily(DEFAULT_FONT_FAMILY);
        legendOptions.setVerticalAlign(ChartEnums.eVerticalAlign.bottom);
        legendOptions.setLayout(ChartEnums.eLayout.horizontal);
        legendOptions.setAlign(ChartEnums.eAlign.center);
        legendOptions.setItemStyle(styleOptions);
        legendOptions.setBorderColor("#c0c0c0");
        legendOptions.setBorderWidth(1);
        legendOptions.setY(10);
        setLegendOptions(legendOptions);
    }

    @Override
    protected List<BaseOptions> createSeriesList(final List<ChartItemDataType> seriesMetaDataList) {
        final List<BaseOptions> seriesList = new ArrayList<BaseOptions>();
        for (final ChartItemDataType seriesItem : seriesMetaDataList) {
            /** Dont create a series for the category X axis **/
            if (!seriesItem.id.equals(getChartMetaData().xAxisColID) && !seriesItem.isSystem) {
                seriesList.add(getSeriesOptions(seriesItem));
            }
        }
        return seriesList;
    }

    @Override
    protected ChartOptions getChartOptions(final String containerDiv) {
        final ChartOptions chartOptions = super.getChartOptions(containerDiv);
        chartOptions.setProperty(BOTTOM_MARGIN_PARAM, 30);
        chartOptions.setProperty(REFLOW_PARAM, true);
        return chartOptions;
    }

    @Override
    protected ChartTitleOptions getChartTitleOptions() {
        final ChartTitleOptions chartTitleOptions = super.getChartTitleOptions();
        String startTime = dateTimeFormat.format(new Date(startTimeInMilliSeconds), TimeZone.createTimeZone(timeZone));
        String endTime = dateTimeFormat.format(new Date(endTimeInMilliSeconds), TimeZone.createTimeZone(timeZone));
        String chartTitle = isEntityChart ? getChartMetaData().parameters.getParameter(Constants.CHART_TITLE_PARAM)
                                            : getChartMetaData().chartTitle;
        StringBuilder title= new StringBuilder(chartTitle).append("<br/>").append(startTime).append(" - ").append(endTime);
        chartTitleOptions.setText(title.toString());
        CSSStyleOptions style = new CSSStyleOptions();
        style.setFontSize("12");
        style.setProperty("width", windowWidth * 0.7);
        chartTitleOptions.setY(5);
        chartTitleOptions.setStyleOptions(style);
        return chartTitleOptions;
    }

    @Override
    protected List<BaseOptions> getSeriesList() {

        final List<BaseOptions> seriesList;
        if (!visibleSeries.isEmpty()) {
            seriesList = createSeriesList(visibleSeries);
        } else {
            /** Get a default series just in order to render an empty graph **/
            final SeriesOptions seriesOptions = new SeriesOptions();
            seriesOptions.setName(" ");
            seriesOptions.setData(getDefaultDataSeriesValues());
            seriesList = new ArrayList<BaseOptions>();
            seriesList.add(seriesOptions);
        }

        /** Only enable the legend if we are showing at least one series **/
        this.getLegendOptions().setEnabled(!visibleSeries.isEmpty());
        return seriesList;
    }

    @Override
    protected ExportingOptions getExportingOptions() {
        final ExportingOptions exportingOptions = super.getExportingOptions();
        exportingOptions.setEnabled(true);
        return exportingOptions;
    }

    @Override
    protected SeriesOptions getSeriesOptions(final ChartItemDataType seriesItem) {
        final SeriesOptions seriesOptions = super.getSeriesOptions(seriesItem);
        final PointEvents pointEvents = new PointEvents();
        pointEvents.setLegendItemClickEvent(new RawStringType("function(event) {return false;}"));
        seriesOptions.setEvents(pointEvents);
        seriesOptions.setMarkerOptions(new MarkerOptions());
        seriesOptions.setColor("#" + seriesItem.color);
        if (!isSubscriberAxisVisible && seriesItem.yAxisIndex.equalsIgnoreCase(SESSION_AXIS_ID)) {
            seriesOptions.setYaxis(Integer.parseInt(seriesItem.yAxisIndex) - 1);
        } else {
            seriesOptions.setYaxis(Integer.parseInt(seriesItem.yAxisIndex));
        }
        if (seriesItem.yAxisIndex.equals(THROUGHPUT_AXIS_ID) || seriesItem.yAxisIndex.equals(SESSION_AXIS_ID)) {
            seriesOptions.setType(eSeriesType.spline);
        } else {
            seriesOptions.setType(eSeriesType.column);
        }
        return seriesOptions;
    }

    @Override
    protected eSeriesType getSeriesType() {
        return eSeriesType.line;
    }

    @Override
    public boolean showChartElements(final Set<String> chartElementIds) {
        visibleSeries.clear();
        for (final String elementId : chartElementIds) {
            hideShowChartElement(elementId);
        }
        return true;
    }

    public boolean hideShowChartElement(final String elementId) {
        if (allSeries.containsKey(elementId)) {
            final ChartItemDataType chartItemDataType = allSeries.get(elementId);
            if (visibleSeries.contains(chartItemDataType)) {
                return visibleSeries.remove(chartItemDataType);
            }
            return visibleSeries.add(chartItemDataType);
        }
        return false;
    }

    @Override
    protected Object[] getCategories() {
        return categories;
    }

    @Override
    protected Object[] getDefaultCategories() {
        return new String[]{NO_DATA_LABEL};
    }

    @Override
    protected YAxisOptions getYAxisOptions() {
        final YAxisOptions yAxisOptions = super.getYAxisOptions();
        yAxisOptions.setAxisGridLineColor("#c0c0c0");
        yAxisOptions.setTitleOptions(getYAxisTitleOptions(DATAVOL_AXIS_TITLE));
        final AxisLabelOptions label = new AxisLabelOptions();
        String dataUnitSuffix = isSubscriberAxisVisible ? "MB" : "KB";
        label.setFormatter(new RawStringType("function () {\n" +
                    "return (Math.abs(this.value) ) + ' " + dataUnitSuffix + "'}"));
        yAxisOptions.setGridLineWidth(1);
        yAxisOptions.setLabelOptions(label);
        if (axisExtremes != null) {
            double max = roundOff(axisExtremes.get(Integer.valueOf(DATAVOLUME_AXIS_ID)));
            yAxisOptions.setMax(max);
            yAxisOptions.setTickInterval(max / 4);
        }

        yAxisOptions.setProperty(HEIGHT_PARAM, chartAreaHeight * FOURTY_FIVE_PERCENTAGE);
        yAxisOptions.setProperty(TOP_PARAM, OFFSET_PIXEL_FROM_TOP);
        return yAxisOptions;
    }

    private YAxisOptions getThroughputYAxisOptions() {
        final YAxisOptions options = super.getYAxisOptions();
        final AxisLabelOptions label = new AxisLabelOptions();
        options.setOpposite(true);
        options.setGridLineWidth(1);
        if (axisExtremes != null) {
            double max = roundOff(axisExtremes.get(Integer.valueOf(THROUGHPUT_AXIS_ID)));
            options.setMax(max);
            options.setTickInterval(max / 4);
        }
        options.setProperty(HEIGHT_PARAM, chartAreaHeight * FOURTY_FIVE_PERCENTAGE);
        options.setProperty(TOP_PARAM, OFFSET_PIXEL_FROM_TOP);
        options.setAllowDecimals(true);
        options.setTitleOptions(getYAxisTitleOptions(THROUGHPUT_AXIS_TITLE));
        String throughputUnitSuffix = isSubscriberAxisVisible ? "Mbps" : "Kbps";
        label.setFormatter(new RawStringType("function () {\n" +
                "return (Math.abs(this.value) ) + ' " + throughputUnitSuffix + "'}"));
        options.setLabelOptions(label);
        return options;
    }

    private YAxisOptions getSubscriberYAxisOptions() {
        final YAxisOptions options = super.getYAxisOptions();
        final AxisLabelOptions label = new AxisLabelOptions();
        label.setX(-55);
        options.setLabelOptions(label);
        options.setOpposite(true);
        options.setTitleOptions(getYAxisTitleOptions(SUBSCRIBER_AXIS_TITLE));
        options.setGridLineWidth(1);
        if (axisExtremes != null) {
            double max = roundOff(axisExtremes.get(Integer.valueOf(SUBSCRIBER_AXIS_ID)));
            options.setMax(max);
            options.setTickInterval(max / 4);
        }
        options.setProperty(HEIGHT_PARAM, chartAreaHeight * FOURTY_FIVE_PERCENTAGE);
        options.setProperty(TOP_PARAM, OFFSET_PIXEL_FROM_TOP + chartAreaHeight * FIFTY_FIVE_PERCENTAGE);
        options.setStartOnTick(true);
        return options;
    }

    private YAxisOptions getSessionYAxisOptions() {
        final YAxisOptions options = super.getYAxisOptions();
        final AxisLabelOptions label = new AxisLabelOptions();
        label.setX(55);
        options.setLabelOptions(label);
        options.setTitleOptions(getYAxisTitleOptions(SESSION_AXIS_TITLE));
        options.setGridLineWidth(1);
        if (axisExtremes != null) {
            double max = roundOff(axisExtremes.get(isSubscriberAxisVisible? Integer.valueOf(SESSION_AXIS_ID)
                        :Integer.valueOf(SESSION_AXIS_ID) - 1 ));
            options.setMax(max);
            options.setTickInterval(max / 4);
        }
        options.setProperty(HEIGHT_PARAM, chartAreaHeight * FOURTY_FIVE_PERCENTAGE);
        options.setProperty(TOP_PARAM, OFFSET_PIXEL_FROM_TOP + chartAreaHeight * FIFTY_FIVE_PERCENTAGE);
        return options;
    }

    @Override
    protected XAxisOptions getXAxisOptions() {
        final XAxisOptions xAxisOptions = super.getXAxisOptions();
        xAxisOptions.setProperty(OFFSET_PARAM, XAXIS_NEGATIVE_OFFSET_FROM_BOTTOM);
        final AxisLabelOptions label = new AxisLabelOptions();
        label.setRotation(0);
        label.setStaggerLines(2);
        xAxisOptions.setLabelOptions(label);
        return xAxisOptions;
    }

    private double roundOff(Double value) {
        int mantissa, exponent = 0;

        if( value == 0){
            return 0;
        }
        if (value > 1) {
            while (value > 10) {
                value /= 10;
                exponent++;
            }
        } else {
            while (value < 1) {
                value *= 10;
                exponent--;
            }
        }

        mantissa = new Double(value).intValue();
        return (mantissa + 1) * Math.pow(10, exponent);
    }
}
