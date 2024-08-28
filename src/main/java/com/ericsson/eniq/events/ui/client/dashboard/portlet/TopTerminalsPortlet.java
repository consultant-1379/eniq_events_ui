/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import com.ericsson.eniq.events.common.client.datatype.ChartDataType;
import com.ericsson.eniq.events.ui.client.charts.IChartPresenter;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.dashboard.PortletTemplate;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class TopTerminalsPortlet extends AbstractSearchTimeUserComposite implements PortletTemplate {

    private final IMetaReader metaReader;

    private TerminalPortletToWorkspaceDrillHandler drillDownHandler;

    private final EventBus eventBus;

    private final IChartPresenter chartPresenter;


    @Inject
    public TopTerminalsPortlet(final IMetaReader metaReader, final IChartPresenter chartPresenter, final EventBus eventBus) {
        this.eventBus = eventBus;
        this.metaReader = metaReader;
        this.chartPresenter = chartPresenter;
    }

    @Override
    public Widget asWidget() {
        return chartPresenter.asWidget();
    }

    @Override
    public void init(final PortletDataType descriptor) {
        final String displayType = descriptor.getDisplayType();
        chartPresenter.init(displayType);
        final String portletId = descriptor.getPortletId();
        final ChartDataType chartConfigInfo = metaReader.getChartConfigInfo(portletId);
        chartPresenter.setConfigData(chartConfigInfo);
        drillDownHandler = new TerminalPortletToWorkspaceDrillHandler(metaReader, eventBus, descriptor);
        drillDownHandler.setSearchAndTimeData(getSearchFieldData(), getTimeFieldData());
        chartPresenter.addChartDrillDownListener(drillDownHandler);
    }

    /**
     * Dynamic part of the initialisation. JSON data is received from the server and passed.
     * The extra search and time information may need to be known for subsequent drill-downs (luanches)
     *
     * @param data             - data for success response from server used to populate porlet
     * @param windowSearchData - current search data for window (that made server call to fetch data with)
     * @param windowTimeData   - current time data for window (that made server call to fetch data with)
     */
    @Override
    public void update(final JSONValue data, final SearchFieldDataType windowSearchData, final TimeInfoDataType windowTimeData) {
        setSearchAndTimeData(windowSearchData, windowTimeData);
        drillDownHandler.setSearchAndTimeData(getSearchFieldData(), getTimeFieldData());
        chartPresenter.updateData(data);
    }


}
