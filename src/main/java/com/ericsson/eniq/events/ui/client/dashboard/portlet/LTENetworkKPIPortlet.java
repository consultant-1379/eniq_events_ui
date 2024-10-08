/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import com.ericsson.eniq.events.common.client.datatype.ChartDataType;
import com.ericsson.eniq.events.common.client.datatype.ParametersDataType;
import com.ericsson.eniq.events.common.client.datatype.ThresholdDataType;
import com.ericsson.eniq.events.common.client.json.JsonDataParserUtils;
import com.ericsson.eniq.events.ui.client.charts.IChartPresenter;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.dashboard.PortletTemplate;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Core Network KPIs Chart Portlet.
 *
 * @author ekurshi
 * @author ecarsea
 */
public class LTENetworkKPIPortlet extends AbstractSearchTimeUserComposite implements PortletTemplate {

    public static final String YAXIS_MIN = "yMinValue";

    public static final String YAXIS_LABELS_DISABLED = "yaxisLabelsDisabled";

    public static final String INITIAL_ERAB_TOP_CHART_ID = "initialERABTopChartId";

    public static final String ADDED_ERAB_BOTTOM_CHART_ID = "addedERABBottomChartId";

    public static final String INITIAL_ERAB_SUCCESS_RATE = "INITIAL_ERAB_SUCCESS_RATE";

    public static final String ADDED_ERAB_SUCCESS_RATE = "ADDED_ERAB_SUCCESS_RATE";

    private static LTENetworkKPIUiBinder uiBinder = GWT.create(LTENetworkKPIUiBinder.class);

    public static final String DRILLABLE_CHART = "drillableChart";

    private static final String DRILLABLE_NODE_TYPES = "drillableNodeTypes";

    private AbstractPortletToTabDrillHandler drillDownHandler;

    @UiField
    SimplePanel initialERABTopContainer;

    @UiField
    SimplePanel addedERABBottomContainer;

    private IChartPresenter initialERABTopChart;

    private IChartPresenter addedERABBottomChart;

    private final IMetaReader metaReader;

    private final Provider<IChartPresenter> chartProvider;

    private PortletDataType descriptor;

    private String initialERABTopId;

    private String addedERABBottomId;

    private final EventBus eventBus;

    interface LTENetworkKPIUiBinder extends UiBinder<Widget, LTENetworkKPIPortlet> {
    }

    @Inject
    public LTENetworkKPIPortlet(final IMetaReader metaReader, final EventBus eventBus,
            final Provider<IChartPresenter> chartProvider) {
        this.metaReader = metaReader;
        this.eventBus = eventBus;
        this.chartProvider = chartProvider;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void init(final PortletDataType descriptor) {
        this.descriptor = descriptor;
        final ParametersDataType parameters = descriptor.getParameters();
        initialERABTopId = parameters.getParameter(INITIAL_ERAB_TOP_CHART_ID);
        addedERABBottomId = parameters.getParameter(ADDED_ERAB_BOTTOM_CHART_ID);

        drillDownHandler = new CoreNetworkKpiPortletToTabDrillDownHandler(metaReader, eventBus, descriptor);
        initialERABTopChart = setUpChart(initialERABTopContainer);
        addedERABBottomChart = setUpChart(addedERABBottomContainer);

    }

    @Override
    public void update(final JSONValue data, final SearchFieldDataType windowSearchData,
            final TimeInfoDataType windowTimeData) {
        // Update the drill handler with new search/time data 
        drillDownHandler.setSearchAndTimeData(windowSearchData, windowTimeData);
        final Map<String, Object[]> seriesMap = JsonDataParserUtils.convertJsonRowsToColumns(data);

        final double min = getMinValueForMultipleSeries(seriesMap, "topSeriesIds");
        final boolean drillableChart = isDrillableChart();
        updateChart(initialERABTopId, INITIAL_ERAB_SUCCESS_RATE, true, min, initialERABTopChart, seriesMap,
                drillableChart);
        updateChart(addedERABBottomId, ADDED_ERAB_SUCCESS_RATE, false, min, addedERABBottomChart, seriesMap,
                drillableChart);

    }

    protected double getMinValueForMultipleSeries(final Map<String, Object[]> seriesMap, final String seriesParameter) {
        double min = Double.MAX_VALUE;
        try {
            final String[] series = descriptor.getParameters().getParameter(seriesParameter).split(",");

            for (final String seriesId : series) {
                /** Only one value in the series **/
                final double value = Double.parseDouble(seriesMap.get(seriesId)[0].toString());
                min = Math.min(min, value);
            }
            /** Set min to either 0 or the closest multiple of 20 below the value **/
            min = Math.max(0, (int) (min - (min % 20)));
        } catch (final Exception e) {
            /** Any parse or other exceptions just return 0 for min **/
            min = 0;
        }
        return min;
    }

    /**
     * Set up the chart widget and add it to its container
     * @param container
     * @return
     */
    private IChartPresenter setUpChart(final SimplePanel container) {

        final IChartPresenter chartPresenter = chartProvider.get();
        final String displayType = descriptor.getDisplayType();
        chartPresenter.init(displayType);
        // Disabled drill down. No support from services
        // chartPresenter.addChartDrillDownListener(drillDownHandler);
        container.setWidget(chartPresenter.asWidget());
        return chartPresenter;
    }

    /**
     * Update the chart - need to recheck thresholds, metaData etc.
     * @param chartId
     * @param thresholdId
     * @param xAxisEnabled
     * @param minValue
     * @param chartPresenter
     * @param seriesMap
     * @param drillable
     */
    private void updateChart(final String chartId, final String thresholdId, final boolean xAxisEnabled,
            final double minValue, final IChartPresenter chartPresenter, final Map<String, Object[]> seriesMap,
            final boolean drillable) {
        final List<ThresholdDataType> thresholdList = descriptor.getThresholds();
        final ThresholdDataType threshold = getThresholdById(thresholdList, thresholdId);
        final ChartDataType chartConfigInfo = metaReader.getChartConfigInfo(chartId);
        if (threshold != null) {
            chartConfigInfo.addThreshold(threshold);
        }

        /** Some dynamic parameters to pass into the chart. Best to just use the Chart Meta Data. Its what it is for and it should be static and dynamic **/
        chartConfigInfo.parameters = new ParametersDataType(new HashMap<String, String>() {
            {
                put(YAXIS_LABELS_DISABLED, Boolean.toString(xAxisEnabled));
                put(YAXIS_MIN, Double.toString(minValue));
                put(DRILLABLE_CHART, Boolean.toString(drillable));
            }
        });

        chartPresenter.setConfigData(chartConfigInfo);
        /** Pass all series into each chart and let them pick out the series they need based on the metadata. **/
        chartPresenter.updateData(seriesMap);
    }

    private boolean isDrillableChart() {
        final String[] drillableNodeTypes = descriptor.getParameters().getParameter(DRILLABLE_NODE_TYPES).split(",");
        if (drillDownHandler.getSearchData() != null) {
            for (final String type : drillableNodeTypes) {
                if (type.equals(drillDownHandler.getSearchData().getType())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}
