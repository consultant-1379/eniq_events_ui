/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.eniq.events.common.client.datatype.ChartDataType;
import com.ericsson.eniq.events.common.client.json.JsonDataParserUtils;
import com.ericsson.eniq.events.ui.client.charts.IChartPresenter;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessageType;
import com.ericsson.eniq.events.ui.client.dashboard.PortletTemplate;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.infopanels.PortletPercentPanel;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.events.component.ComponentMessageEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class HomerRoamerPortlet extends AbstractSearchTimeUserComposite implements PortletTemplate {

    private static final String VALUE_COLUMN_ID = "3";

    private static final String LABEL_COLUMN_ID = "2";

    private String portletId;

    private final IMetaReader metaReader;

    private final EventBus eventBus;

    private final IChartPresenter chartPresenter;

    @UiField
    SimplePanel chartSimplePanel;

    @UiField
    PortletPercentPanel perccentPanel;

    private static HomerRoamerUiBinder uiBinder = GWT.create(HomerRoamerUiBinder.class);
    interface HomerRoamerUiBinder extends UiBinder<Widget, HomerRoamerPortlet> {
    }

    @Inject
    public HomerRoamerPortlet(final IMetaReader metaReader, final IChartPresenter chartPresenter,
            final EventBus eventBus) {
        this.metaReader = metaReader;
        this.chartPresenter = chartPresenter;
        this.eventBus = eventBus;

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void init(final PortletDataType descriptor) {
        final String displayType = descriptor.getDisplayType();
        chartPresenter.init(displayType);

        portletId = descriptor.getPortletId();

        final ChartDataType chartConfigInfo = metaReader.getChartConfigInfo(portletId);
        chartPresenter.setConfigData(chartConfigInfo);
        chartSimplePanel.add(chartPresenter.asWidget());
    }

    @Override
    public void update(final JSONValue data, final SearchFieldDataType windowSearchData,
            final TimeInfoDataType windowTimeData) {
        final Map<String, Object[]> seriesMap = JsonDataParserUtils.convertJsonRowsToColumns(data);

        processTotals(seriesMap);
        processSeries(seriesMap);
    }

    private void processTotals(final Map<String, Object[]> seriesMap) {
        final int currentTotal = extractValue(seriesMap, "Total");
        final int previousTotal = extractValue(seriesMap, "Previous");

        perccentPanel.setDescription("Total Roamers = " + currentTotal);
        perccentPanel.setIndicators(previousTotal, currentTotal);
    }

    private void processSeries(final Map<String, Object[]> seriesMap) {
        final Map<String, Object[]> processedSeriesMap = removeSeries(seriesMap, Arrays.asList("Previous", "Total"));
        if (processedSeriesMap != null) {
            chartPresenter.updateData(processedSeriesMap);
        } else {
            eventBus.fireEvent(new ComponentMessageEvent(portletId, ComponentMessageType.INFO,
                    NO_DATA_MESSAGE_DASHBOARD));
        }
    }

    private int extractValue(final Map<String, Object[]> series, final String name) {
        final Object[] names = series.get(LABEL_COLUMN_ID);
        final Object[] values = series.get(VALUE_COLUMN_ID);

        for (int i = 0; i < names.length; i++) {
            final Object n = names[i];

            // Find the according name index in names array
            if (n.equals(name)) {
                // By using the same names array index, get the value
                String value = (String) values[i];
                if (value == null || "".equals(value)) {
                    // Empty string in value by default means it is equal to 0
                    value = "0";
                }

                return Integer.parseInt(String.valueOf(value));
            }
        }

        return 0;
    }

    private Map<String, Object[]> removeSeries(final Map<String, Object[]> series,
            final List<String> removableSeriesNames) {
        final List<Integer> removableSeriesIndexes = new ArrayList<Integer>();

        final Object[] names = series.get(LABEL_COLUMN_ID);

        for (int i = 0; i < names.length; i++) {
            final String name = (String) names[i];
            if (removableSeriesNames.contains(name)) {
                // Get the list of removable series index
                removableSeriesIndexes.add(i);
            }
        }

        if (!removableSeriesIndexes.isEmpty()) {
            boolean hasData = false;

            final Map<String, Object[]> newSeries = new HashMap<String, Object[]>();

            for (final String id : series.keySet()) {
                // Get the lenght of new Object[] array for new series
                final int arrayLength = names.length - removableSeriesIndexes.size();

                if (arrayLength > 0) {
                    hasData = true;
                }

                final Object[] newSerie = new Object[arrayLength];
                final Object[] serie = series.get(id); // Old serie array

                int newIndex = 0;
                for (int i = 0; i < serie.length; i++) {
                    // Go through old serie array and place only needed data in newSerie array
                    if (!removableSeriesIndexes.contains(i)) {
                        newSerie[newIndex] = serie[i];
                        newIndex++;
                    }
                }

                newSeries.put(id, newSerie);
            }

            if (hasData) {
                return newSeries;
            }

            // Series are all empty, therefore there is no data, returning null
            return null;

        }

        // No series to delete, therefore returning the same series map
        return series;
    }
}
