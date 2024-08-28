/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
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

import javax.inject.Inject;

/**
 * Generic Chart Portlet.
 *
 * @author edmibuz
 */
public class ChartPortlet implements PortletTemplate {

    private final IMetaReader metaReader;

    private final IChartPresenter chartPresenter;

    private SearchFieldDataType windowSearchData;

    private TimeInfoDataType windowTimeData;

    @Inject
    public ChartPortlet(final IMetaReader metaReader, final IChartPresenter chartPresenter) {
        this.metaReader = metaReader;
        this.chartPresenter = chartPresenter;
    }

    @Override
    public Widget asWidget() {
        final Widget widget = chartPresenter.asWidget();
        return widget;
    }

    @Override
    public void init(final PortletDataType descriptor) {
        final String displayType = descriptor.getDisplayType();
        chartPresenter.init(displayType);
        final String portletId = descriptor.getPortletId();
        final ChartDataType chartConfigInfo = metaReader.getChartConfigInfo(portletId);
        chartPresenter.setConfigData(chartConfigInfo);
    }

    @Override
    public void update(final JSONValue data, final SearchFieldDataType searchData, final TimeInfoDataType timeData) {

        this.windowSearchData = searchData;
        this.windowTimeData = timeData;

        chartPresenter.updateData(data);
    }

    @Override
    public SearchFieldDataType getSearchFieldData() {
        return windowSearchData;
    }

    @Override
    public TimeInfoDataType getTimeFieldData() {
        return windowTimeData;
    }

}
