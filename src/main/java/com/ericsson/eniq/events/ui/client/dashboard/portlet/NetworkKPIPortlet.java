/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.eniq.events.common.client.datatype.ChartDataType;
import com.ericsson.eniq.events.common.client.datatype.ThresholdDataType;
import com.ericsson.eniq.events.common.client.json.JsonDataParserUtils;
import com.ericsson.eniq.events.ui.client.charts.IChartPresenter;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.dashboard.PortletTemplate;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.infopanels.PortletPercentPanel;
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
 * Network KPI Chart Portlet.
 *
 * @author ejanera
 * @authoer ecarsea
 */
public class NetworkKPIPortlet extends AbstractSearchTimeUserComposite implements PortletTemplate {

    private static final String REQUEST_SERIES_ID = "requestSeriesId";

    private static final String PERCENT_CHANGE_SERIES_ID = "percentChangeSeriesId";

    private static NetworkKPIUiBinder uiBinder = GWT.create(NetworkKPIUiBinder.class);

    private final IMetaReader metaReader;

    @UiField
    SimplePanel chartContainer;

    @UiField
    PortletPercentPanel percentPanel;

    private ChartDataType chartConfigInfo;

    private final IChartPresenter chartPresenter;

    private PortletDataType descriptor;

    private String requestSeriesId;

    private String percentChangeSeriesId;

    private AbstractPortletToTabDrillHandler drillDownHandler;

    private final EventBus eventBus;

    interface NetworkKPIUiBinder extends UiBinder<Widget, NetworkKPIPortlet> {
    }

    @Inject
    public NetworkKPIPortlet(final IMetaReader metaReader, final IChartPresenter chartPresenter, final EventBus eventBus) {
        this.chartPresenter = chartPresenter;
        this.metaReader = metaReader;
        this.eventBus = eventBus;

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void init(final PortletDataType descriptor) {
        this.descriptor = descriptor;
        final String displayType = descriptor.getDisplayType();
        requestSeriesId = descriptor.getParameters().getParameter(REQUEST_SERIES_ID);
        percentChangeSeriesId = descriptor.getParameters().getParameter(PERCENT_CHANGE_SERIES_ID);
        chartPresenter.init(displayType);
        chartContainer.add(chartPresenter.asWidget());
        drillDownHandler = new NetworkKpiPortletToTabDrillDownHandler(metaReader, eventBus, descriptor);
        chartPresenter.addChartDrillDownListener(drillDownHandler);
    }

    private void configureChart() {
        final List<ThresholdDataType> thresholdList = descriptor.getThresholds();

        chartConfigInfo = metaReader.getChartConfigInfo(descriptor.getPortletId());

        final ThresholdDataType successRateThreshold = getThresholdById(thresholdList, "SUCCESS_RATE");
        if (successRateThreshold != null) {
            chartConfigInfo.addThreshold(successRateThreshold);
        }
        chartPresenter.setConfigData(chartConfigInfo);
    }

    @Override
    public void update(final JSONValue data, final SearchFieldDataType searchData, final TimeInfoDataType timeData) {

        // Update the drill handler with new search/time data 
        drillDownHandler.setSearchAndTimeData(searchData, timeData);
        configureChart();
        final Map<String, Object[]> seriesMap = JsonDataParserUtils.convertJsonRowsToColumns(data);

        final Object[] requestsSeries = seriesMap.get(requestSeriesId);
        final String requests = requestsSeries == null ? "" : requestsSeries[0].toString();
        percentPanel.setDescription("Number of requests = " + requests);

        final Object[] percentSeries = seriesMap.get(percentChangeSeriesId);
        double value = Double.NaN;
        if (percentSeries != null && percentSeries.length > 0) {
            try {
                value = Double.valueOf(percentSeries[0].toString());
            } catch (final NumberFormatException nfe) {
                // do nothing
            }
        }
        percentPanel.setPercent(value);
        chartPresenter.updateData(seriesMap);
    }
}
