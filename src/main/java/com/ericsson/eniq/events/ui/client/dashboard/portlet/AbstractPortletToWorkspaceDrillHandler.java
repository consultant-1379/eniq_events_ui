/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.datatype.ChartDrillDownInfoDataType;
import com.ericsson.eniq.events.ui.client.charts.IChartElementDrillDownListener;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceTabComponent;
import com.ericsson.eniq.events.ui.client.workspace.events.NewWorkspaceEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.NewWorkspaceEventHandler;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.highcharts.client.ChartConstants.CHART_ELEMENT_DRILLDOWN_KEY;
import static com.ericsson.eniq.events.highcharts.client.ChartConstants.CHART_ELEMENT_SELECTED_KEY;

/**
 * @author emauoco
 * @since 2011
 *
 */
    public abstract class AbstractPortletToWorkspaceDrillHandler implements IChartElementDrillDownListener {

    private final IMetaReader metaReader;

    private final EventBus eventBus;

    private boolean drillDownPending;

    private Map<String, String> drillDownDataMap;

    private SearchFieldDataType searchData;

    private TimeInfoDataType timeData;

    private final PortletDataType descriptor;

    /**
     * @param metaReader
     * @param eventBus
     */
    public AbstractPortletToWorkspaceDrillHandler(final IMetaReader metaReader, final EventBus eventBus,
            final PortletDataType descriptor) {
        this.metaReader = metaReader;
        this.eventBus = eventBus;
        this.descriptor = descriptor;
        bind();
    }

    protected void bind() {

        eventBus.addHandler(NewWorkspaceEvent.TYPE, new NewWorkspaceEventHandler() {
            @Override
            public void onNewWorkspace() {
                if(drillDownPending) {
                    LinkedHashMap<String, WorkspaceTabComponent> linkedHashMap = MainEntryPoint.getInjector()
                            .getWorkspaceManager().getActiveWorkspaces();

                    WorkspaceTabComponent workspaceTabComponent = null;
                    for(String key : linkedHashMap.keySet()) {
                        workspaceTabComponent = linkedHashMap.get(key);
                    }

                    performDrillDown(drillDownDataMap, workspaceTabComponent.getTabItem());
                    drillDownPending = false;
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
        // In come the workspaces hacks...


//        if(workspaceManager.getActiveWorkspaces().isEmpty()) {
        this.drillDownDataMap = drillDownMap;
        drillDownPending = true;
        eventBus.fireEvent(new NewWorkspaceEvent());
//        } else {
//            performDrillDown(null, null);
//        }
    }

    /**
     * @param drillDownDataMap
     * @param tabItem
     */
    @SuppressWarnings("hiding")
    public void performDrillDown(final Map<String, String> drillDownDataMap, TabItem tabItem) {
        drillDownChart(drillDownDataMap, tabItem);
    }

    /**
     * Implement to do the specialized per-Portlet drill down for this particular portlet.
     * @param drillDownDataMap
     * @param tabItem
     */
    @SuppressWarnings("hiding")
    protected abstract void drillDownChart(final Map<String, String> drillDownDataMap, TabItem tabItem);

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
