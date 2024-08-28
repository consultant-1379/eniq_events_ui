/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
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
 * @author eshusai
 * @since 2012
 */
public class LteCoreNetworkKPIsPortlet extends AbstractSearchTimeUserComposite implements PortletTemplate {

    public static final String YAXIS_MIN = "yMinValue";

    public static final String YAXIS_LABELS_DISABLED = "yaxisLabelsDisabled";

    public static final String YAXIS_MAX = "yMaxValue";

    public static final String SUPPORTED_SUBS_KEY = "1";

    public static final String ATTACHED_SUBS_KEY = "2";

    public static final String SUPPORTED_SUBSCRIBERS_ID = "supportedSubscribersId";

    public static final String ATTACHED_SUBSCRIBERS_ID = "attachedSubscribersId";

    public static final String ATTACH_FAILURE_ID = "attachFailureId";

    public static final String SERVICE_REQUEST_FAILURE_ID = "serviceRequestFailureId";

    public static final String PAGING_FAILURE_ID = "pagingFailureId";

    public static final String SUPPORTED_SUBSCRIBERS_EVENT = "SUPPORTED_SUBSCRIBERS_EVENT";

    public static final String ATTACHED_SUBSCRIBERS_EVENT = "ATTACHED_SUBSCRIBERS_EVENT";

    public static final String ATTACH_FAILURE_EVENT = "ATTACH_FAILURE_EVENT";

    public static final String SERVICE_REQUEST_FAILURE_EVENT = "SERVICE_REQUEST_FAILURE_EVENT";

    public static final String PAGING_FAILURE_EVENT = "PAGING_FAILURE_EVENT";

    private static LteCoreNetworkKPIsUiBinder uiBinder = GWT.create(LteCoreNetworkKPIsUiBinder.class);

    public static final String DRILLABLE_CHART = "drillableChart";

    private static final String DRILLABLE_NODE_TYPES = "drillableNodeTypes";

    private AbstractPortletToTabDrillHandler drillDownHandler;

    @UiField
    SimplePanel supportedSubscribersContainer;

    @UiField
    SimplePanel attachedSubscribersContainer;

    @UiField
    SimplePanel attachFailureContainer;

    @UiField
    SimplePanel serviceRequestFailureContainer;

    @UiField
    SimplePanel pagingFailureContainer;

    private IChartPresenter supportedSubscribersChart;

    private IChartPresenter attachedSubscribersChart;

    private IChartPresenter attachFailureChart;

    private IChartPresenter serviceRequestFailureChart;

    private IChartPresenter pagingFailureChart;

    private final IMetaReader metaReader;

    private final Provider<IChartPresenter> chartProvider;

    private PortletDataType descriptor;

    private String supportedSubscribersId;

    private String attachedSubscribersId;

    private String attachFailureId;

    private String serviceRequestFailureId;

    private String pagingFailureId;

    private final EventBus eventBus;

    interface LteCoreNetworkKPIsUiBinder extends UiBinder<Widget, LteCoreNetworkKPIsPortlet> {
    }

    @Inject
    public LteCoreNetworkKPIsPortlet(final IMetaReader metaReader, final EventBus eventBus,
            final Provider<IChartPresenter> chartProvider) {
        this.metaReader = metaReader;
        this.eventBus = eventBus;
        this.chartProvider = chartProvider;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void init(@SuppressWarnings("hiding") final PortletDataType descriptor) {
        this.descriptor = descriptor;
        final ParametersDataType parameters = descriptor.getParameters();
        supportedSubscribersId = parameters.getParameter(SUPPORTED_SUBSCRIBERS_ID);
        attachedSubscribersId = parameters.getParameter(ATTACHED_SUBSCRIBERS_ID);
        attachFailureId = parameters.getParameter(ATTACH_FAILURE_ID);
        serviceRequestFailureId = parameters.getParameter(SERVICE_REQUEST_FAILURE_ID);
        pagingFailureId = parameters.getParameter(PAGING_FAILURE_ID);

        drillDownHandler = new CoreNetworkKpiPortletToTabDrillDownHandler(metaReader, eventBus, descriptor);
        supportedSubscribersChart = setUpChart(supportedSubscribersContainer);
        attachedSubscribersChart = setUpChart(attachedSubscribersContainer);
        attachFailureChart = setUpChart(attachFailureContainer);
        serviceRequestFailureChart = setUpChart(serviceRequestFailureContainer);
        pagingFailureChart = setUpChart(pagingFailureContainer);
    }

    @Override
    public void update(final JSONValue data, final SearchFieldDataType windowSearchData,
            final TimeInfoDataType windowTimeData) {
        // Update the drill handler with new search/time data
        drillDownHandler.setSearchAndTimeData(windowSearchData, windowTimeData);
        final Map<String, Object[]> seriesMap = JsonDataParserUtils.convertJsonRowsToColumns(data);

        double min = 0.0;
        final boolean drillableChart = isDrillableChart();
        min = getMinValueForMultipleSeries(seriesMap, "topSeriesIds");
        updateChart(supportedSubscribersId, SUPPORTED_SUBSCRIBERS_EVENT, true, min, supportedSubscribersChart,
                seriesMap, drillableChart);
        updateChart(attachedSubscribersId, ATTACHED_SUBSCRIBERS_EVENT, false, min, attachedSubscribersChart, seriesMap,
                drillableChart);
        min = getMinValueForMultipleSeries(seriesMap, "bottomSeriesIds");
        updateChart(attachFailureId, ATTACH_FAILURE_EVENT, true, min, attachFailureChart, seriesMap, drillableChart);
        updateChart(serviceRequestFailureId, SERVICE_REQUEST_FAILURE_EVENT, true, min, serviceRequestFailureChart,
                seriesMap, drillableChart);
        updateChart(pagingFailureId, PAGING_FAILURE_EVENT, false, min, pagingFailureChart, seriesMap, drillableChart);
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
     *
     * @param container
     *
     * @return
     */
    private IChartPresenter setUpChart(final SimplePanel container) {

        final IChartPresenter chartPresenter = chartProvider.get();
        final String displayType = descriptor.getDisplayType();
        chartPresenter.init(displayType);
        chartPresenter.addChartDrillDownListener(drillDownHandler);
        container.setWidget(chartPresenter.asWidget());
        return chartPresenter;
    }

    /**
     * Update the chart - need to recheck thresholds, metaData etc.
     *
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
        final ChartDataType chartConfigInfo = metaReader.getChartConfigInfo(chartId);

        if (thresholdId != null) {
            final List<ThresholdDataType> thresholdList = descriptor.getThresholds();
            final ThresholdDataType threshold = getThresholdById(thresholdList, thresholdId);
            if (threshold != null) {
                chartConfigInfo.addThreshold(threshold);
            }
        }

        /** Some dynamic parameters to pass into the chart. Best to just use the Chart Meta Data. Its what it is for and it should be static and dynamic **/
        chartConfigInfo.parameters = new ParametersDataType(new HashMap<String, String>() {
            {
                put(YAXIS_LABELS_DISABLED, Boolean.toString(xAxisEnabled));
                put(YAXIS_MIN, Double.toString(minValue));
                put(DRILLABLE_CHART, Boolean.toString(drillable));
                put(YAXIS_MAX, getMaxSubscribers(seriesMap).toString());
            }
        });

        chartPresenter.setConfigData(chartConfigInfo);
        /** Pass all series into each chart and let them pick out the series they need based on the metadata. **/
        chartPresenter.updateData(seriesMap);
    }

    private Double getMaxSubscribers(final Map<String, Object[]> seriesMap) {
        Double suppSubsMax = 0.0;
        Double attSubsMax = 0.0;
        Object[] objs = seriesMap.get(SUPPORTED_SUBS_KEY);
        for (final Object obj : objs) {
            suppSubsMax = Double.valueOf((String) obj);
        }
        objs = seriesMap.get(ATTACHED_SUBS_KEY);
        for (final Object obj : objs) {
            attSubsMax = Double.valueOf((String) obj);
        }

        if (attSubsMax > 50) {
            attSubsMax = (Math.ceil(attSubsMax / 50)) * 50;
        } else if (suppSubsMax > 50) {
            suppSubsMax = (Math.ceil(suppSubsMax / 50)) * 50;
        } else {
            return 50.0;
        }

        return Math.max(attSubsMax, suppSubsMax);
    }

    private boolean isDrillableChart() {
        final String drillableNodeType = descriptor.getParameters().getParameter(DRILLABLE_NODE_TYPES);
        if (drillableNodeType == null) {
            return false;
        }
        final String[] drillableNodeTypes = drillableNodeType.split(",");
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
