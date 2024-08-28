/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.datatype.ChartDrillDownInfoDataType;
import com.ericsson.eniq.events.ui.client.charts.IChartElementDrillDownListener;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.events.tab.TabAddCompleteEvent;
import com.ericsson.eniq.events.ui.client.events.tab.TabAddCompleteEventHandler;
import com.ericsson.eniq.events.ui.client.events.tab.TabAddEvent;
import com.ericsson.eniq.events.ui.client.main.IGenericTabView;
import com.ericsson.eniq.events.ui.client.main.TabViewRegistry;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.highcharts.client.ChartConstants.CHART_ELEMENT_DRILLDOWN_KEY;
import static com.ericsson.eniq.events.highcharts.client.ChartConstants.CHART_ELEMENT_SELECTED_KEY;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public abstract class AbstractPortletToTabDrillHandler implements IChartElementDrillDownListener {

    private final IMetaReader metaReader;

    private final EventBus eventBus;

    private boolean drillDownPending;

    private Map<String, String> drillDownDataMap;

    private final String drillDownTabId;

    private SearchFieldDataType searchData;

    private TimeInfoDataType timeData;

    private final PortletDataType descriptor;

    private static final String DRILL_DOWN_TAB_ID = "drillDownTabId";

    /**
     * @param metaReader
     * @param eventBus
     */
    public AbstractPortletToTabDrillHandler(final IMetaReader metaReader, final EventBus eventBus,
            final PortletDataType descriptor) {
        this.metaReader = metaReader;
        this.eventBus = eventBus;
        this.descriptor = descriptor;
        drillDownTabId = descriptor.getParameters().getParameter(DRILL_DOWN_TAB_ID);
        bind(); // NOPMD by eeicmsy on 25/11/11 20:41
    }

    protected void bind() {

        /** Not ideal to do the drill down asynchronously while waiting for the tab view to be added to the tab panel.
         * Need to maintain some state i.e. drillDownPending and DrillDownDataMap
         */
        eventBus.addHandler(TabAddCompleteEvent.TYPE, new TabAddCompleteEventHandler() {

            @Override
            public void onTabAddComplete(final TabAddCompleteEvent tabAddCompleteEvent) {
                /** Is it our Tab and are we waiting to do a drill down**/
                if (tabAddCompleteEvent.getTabId().equals(drillDownTabId) && drillDownPending) {
                    final IGenericTabView tabView = TabViewRegistry.get().getTabView(drillDownTabId);
                    drillDownPending = false;
                    if (tabView != null) {
                        performDrillDown(drillDownDataMap, tabView);
                    }
                }
            }
        });
    }

    /**
     * Retrieve the time range for the new drill down grid based on the date passed to +/- 24 hours
     * @param dateStr - current Date in milliseconds string
     * @param forward - Set the range to 1 day forward or back.
     * @return
     */
    @SuppressWarnings("deprecation")
    protected TimeInfoDataType getDrillDownDateTimeRange(final String dateStr, final boolean forward) {
        final long dateMillis = Long.parseLong(dateStr);

        final Date drillDateFrom = new Date(forward ? dateMillis : dateMillis - CommonConstants.DAY_IN_MILLISEC);
        final Date drillDateTo = new Date(forward ? dateMillis + CommonConstants.DAY_IN_MILLISEC : dateMillis);

        final TimeInfoDataType timeInfoDataType = new TimeInfoDataType();
        timeInfoDataType.dateTo = drillDateTo;
        timeInfoDataType.dateFrom = drillDateFrom;

        /** Ignore deprecation. It is used by GWTs DateTimeFormat anyway**/
        timeInfoDataType.timeTo = new Time(drillDateTo.getHours(), drillDateTo.getMinutes());
        timeInfoDataType.timeFrom = new Time(drillDateFrom.getHours(), drillDateFrom.getMinutes());
        return timeInfoDataType;
    }

    /**
     * @param searchData
     * @param timeData
     */
    public void setSearchAndTimeData(final SearchFieldDataType searchData, final TimeInfoDataType timeData) {
        this.searchData = searchData;
        this.timeData = timeData;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.charts.IChartElementDrillDownListener#drillDown(java.util.Map)
     */
    @Override
    public void drillDown(final Map<String, String> drillDownMap) {
        /** If our tab has already been created and had its view attached then we can go ahead and do the drilldown 
         * otherwise we need to fire and event to get the tab view created, and set a flag to indicate that we are awaiting a 
         * drill down once we receive back the event indicating that the tab view has been created 
         */
        if (TabViewRegistry.get().containsTabView(drillDownTabId)) {
            final IGenericTabView tabView = TabViewRegistry.get().getTabView(drillDownTabId);
            final TabItem tabItem = tabView.getTabItem();
            tabItem.getTabPanel().setSelection(tabItem);
            performDrillDown(drillDownMap, tabView);
        } else {
            drillDownPending = true;
            drillDownDataMap = drillDownMap;
            eventBus.fireEvent(new TabAddEvent(drillDownTabId));
        }

    }

    /**
     * @param drillDownDataMap
     * @param tabView
     */
    @SuppressWarnings("hiding")
    public void performDrillDown(final Map<String, String> drillDownDataMap, final IGenericTabView tabView) {
        drillDownChart(drillDownDataMap, tabView);
    }

    /**
     * Implement to do the specialized per-Portlet drill down for this particular portlet.
     * @param drillDownDataMap
     * @param tabView
     */
    @SuppressWarnings("hiding")
    protected abstract void drillDownChart(final Map<String, String> drillDownDataMap, IGenericTabView tabView);

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.charts.IChartElementDrillDownListener#setEventId(java.lang.String)
     */
    @Override
    public void setEventId(final String id) {
        // not implemented. override if required
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.charts.IChartElementDrillDownListener#performChartClickManually(java.lang.String, java.lang.String)
     */
    @Override
    public void performChartClickManually(final String chartElementClicked, final String drillDownWindowType) {
        final Map<String, String> map = new HashMap<String, String>() {
            {
                put(CHART_ELEMENT_SELECTED_KEY, chartElementClicked);
                put(CHART_ELEMENT_DRILLDOWN_KEY, drillDownWindowType);
            }
        };
        drillDown(map);
    }

    protected ChartDrillDownInfoDataType getChartDrillDownInfo(final String drillDownWindowType) {
        return metaReader.getChartDrillDownWindowType(drillDownWindowType);
    }

    protected EventBus getEventBus() {
        return eventBus;
    }

    protected IMetaReader getMetaReader() {
        return metaReader;
    }

    protected SearchFieldDataType getSearchData() {
        return searchData;
    }

    protected TimeInfoDataType getTimeData() {
        return timeData;
    }

    protected PortletDataType getDescriptor() {
        return descriptor;
    }
}
