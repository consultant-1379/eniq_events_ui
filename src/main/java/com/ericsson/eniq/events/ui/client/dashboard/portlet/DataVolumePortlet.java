/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Provider;

import com.ericsson.eniq.events.common.client.CommonConstants;
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
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.highcharts.client.ChartConstants.CHART_ELEMENT_DRILLDOWN_KEY;
import static com.ericsson.eniq.events.highcharts.client.ChartConstants.CHART_ELEMENT_SELECTED_KEY;

/**
 * @author evyagrz
 * @author edmibuz
 * @author ecarsea
 * @since 10 2011
 */
public class DataVolumePortlet extends AbstractSearchTimeUserComposite implements PortletTemplate {

    private static final String PDP_DRILLDOWN_KEY = "PDP";

    private static final String TOP_CHART_ID = "topChartId";

    private static final String BOTTOM_CHART_ID = "bottomChartId";

    private static final String PDP_SESSIONS_SERIES_ID = "pdpSessionsSeriesId";

    private static final String PORTLET_CHART_ID_PREFIX_PARAM = "portletChartIdPrefix";

    private static DataVolumeUiBinder uiBinder = GWT.create(DataVolumeUiBinder.class);

    @UiField
    SimplePanel downlinkChartContainer;

    @UiField
    SimplePanel uplinkChartContainer;

    private IChartPresenter uplinkChart;

    private IChartPresenter downlinkChart;

    private final IMetaReader metaReader;

    private final Provider<IChartPresenter> chartProvider;

    private String pdpSessionsSeriesId;

    @UiField
    PortletPercentPanel percentPanel;

    @UiField
    Label pdpSessionsLabel;

    private final EventBus eventBus;

    private AbstractPortletToTabDrillHandler drillDownHandler;

    private PortletDataType descriptor;

    private String chartIdPrefix;

    private boolean pdpData;

    interface DataVolumeUiBinder extends UiBinder<Widget, DataVolumePortlet> {
    }

    @UiHandler("percentPanel")
    public void onPdpContainerClicked(@SuppressWarnings("unused") final ClickEvent event) {
        /** Do Nothing if no PDP Data **/
        if (pdpData) {
            final TimeInfoDataType time = drillDownHandler.getTimeData();
            /** Set the selected day to the time component selection minus one day, as Data Volume Drill Handler sets the time range to
             * the selected day +1. The dateTo field of the time component is the end of the current day, so we will get a time range
             * of the start of the current day to the end of the current day.
             */
            final Date selectedDate = new Date(time.dateTo.getTime() - CommonConstants.DAY_IN_MILLISEC);
            /** Cheating a little here. Pretending its a chart drill down and setting the day to the selected day in time component. **/
            final Map<String, String> drillDownDataMap = new HashMap<String, String>() {
                {
                    put(CHART_ELEMENT_SELECTED_KEY, Long.toString(selectedDate.getTime()));
                    put(CHART_ELEMENT_DRILLDOWN_KEY, PDP_DRILLDOWN_KEY); // This key will identify the MetaMenuItem for PDP Drilldown chart
                }
            };
            drillDownHandler.drillDown(drillDownDataMap);
        }
    }

    @Inject
    public DataVolumePortlet(final IMetaReader metaReader, final Provider<IChartPresenter> chartProvider,
            final EventBus eventBus) {
        this.metaReader = metaReader;
        this.chartProvider = chartProvider;
        this.eventBus = eventBus;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void init(final PortletDataType descriptor) {
        this.descriptor = descriptor;
        /** There are two charts in this portlet. This is the prefix for both those charts **/
        chartIdPrefix = descriptor.getParameters().getParameter(PORTLET_CHART_ID_PREFIX_PARAM);
        pdpSessionsSeriesId = descriptor.getParameters().getParameter(PDP_SESSIONS_SERIES_ID);
        drillDownHandler = new DataVolumePortletToTabDrillDownHandler(metaReader, eventBus, descriptor);

        uplinkChart = setUpChart(descriptor);
        downlinkChart = setUpChart(descriptor);

        uplinkChartContainer.setWidget(uplinkChart.asWidget());
        downlinkChartContainer.setWidget(downlinkChart.asWidget());
    }

    /**
     * Create chart presenter from Portlet Meta Data.
     * @param descriptor
     * @return
     */
    private IChartPresenter setUpChart(final PortletDataType descriptor) {
        final String displayType = descriptor.getDisplayType();
        final IChartPresenter chartPresenter = chartProvider.get();

        chartPresenter.init(displayType);
        chartPresenter.addChartDrillDownListener(drillDownHandler);
        return chartPresenter;
    }

    /**
     * @param chartPresenter
     * @param chartId - Indicator for uplink or downlink Chart, Appended to the Portlet Chart Prefix to retrieve the required chart meta data
     * @param thresholdId
     */
    private void configureChart(final IChartPresenter chartPresenter, final String chartId, final String thresholdId) {
        final List<ThresholdDataType> thresholdList = descriptor.getThresholds();
        /** Ap Uplink or Downlink to the Chart Prefix depending on which of the charts is being configured **/
        final ChartDataType chartConfigInfo = metaReader.getChartConfigInfo(chartIdPrefix + "_" + chartId);
        final ThresholdDataType threshold = getThresholdById(thresholdList, thresholdId);
        if (threshold != null) {
            chartConfigInfo.addThreshold(threshold);
        }
        chartPresenter.setConfigData(chartConfigInfo);
    }

    @Override
    public void update(final JSONValue data, final SearchFieldDataType searchData, final TimeInfoDataType timeData) {
        final String topChartId = descriptor.getParameters().getParameter(TOP_CHART_ID);
        final String bottomChartId = descriptor.getParameters().getParameter(BOTTOM_CHART_ID);
        // Update the drill handler with new search/time data 
        drillDownHandler.setSearchAndTimeData(searchData, timeData);

        configureChart(uplinkChart, topChartId, "UPLINK");
        configureChart(downlinkChart, bottomChartId, "DOWNLINK");
        final Map<String, Object[]> seriesMap = JsonDataParserUtils.convertJsonRowsToColumns(data);
        /**
         * Pass all series into each chart and let them pick out the series they need based on the metadata.
         */
        uplinkChart.updateData(seriesMap);
        downlinkChart.updateData(seriesMap);
        configurePdpElement(seriesMap);
    }

    /**
     * @param seriesMap
     */
    protected void configurePdpElement(final Map<String, Object[]> seriesMap) {
        final Object[] pdpSessionSeries = seriesMap.get(pdpSessionsSeriesId);
        int currentSessionTotal = 0;
        int previousSessionTotal = 0;
        if (pdpSessionSeries != null && pdpSessionSeries.length > 0) {
            pdpData = true;
            try {
                currentSessionTotal = Integer.parseInt(pdpSessionSeries[pdpSessionSeries.length - 1].toString());
                if (pdpSessionSeries.length > 1) {
                    previousSessionTotal = Integer.parseInt(pdpSessionSeries[pdpSessionSeries.length - 2].toString());
                }
            } catch (final NumberFormatException nfe) {
                // Ignore, just leave it as 0 in case we have no values
            }
        } else {
            pdpData = false;
        }

        pdpSessionsLabel.setText("PDP Sessions " + currentSessionTotal);

        percentPanel.setIndicators(previousSessionTotal, currentSessionTotal);
        percentPanel.removePaddingForIndicator();

        percentPanel.getElement().getStyle().setCursor(pdpData ? Cursor.POINTER : Cursor.DEFAULT);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}