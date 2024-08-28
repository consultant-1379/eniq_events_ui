/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ericsson.eniq.events.common.client.datatype.ChartDataType;
import com.ericsson.eniq.events.common.client.datatype.ChartDrillDownInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.ChartItemDataType;
import com.ericsson.eniq.events.ui.client.charts.IChartElementDrillDownListener;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.URLParamUtils;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.common.comp.WindowState;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.main.AbstractWindowLauncher;
import com.ericsson.eniq.events.ui.client.main.GridLauncher;
import com.ericsson.eniq.events.ui.client.main.IGenericTabView;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.highcharts.client.ChartConstants.CHART_ELEMENT_DRILLDOWN_KEY;
import static com.ericsson.eniq.events.highcharts.client.ChartConstants.CHART_META_ID;
import static com.ericsson.eniq.events.ui.client.common.Constants.COMMA;
import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;
import static com.ericsson.eniq.events.ui.client.common.Constants.EQUAL_STRING;
import static com.ericsson.eniq.events.ui.client.common.Constants.MULTI_IDENTIFER;
import static com.ericsson.eniq.events.ui.client.common.Constants.UNDERSCORE;
import static com.ericsson.eniq.events.ui.client.common.Constants.UNDER_SCORE_GROUP;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public class CoreNetworkKpiPortletToTabDrillDownHandler extends AbstractPortletToTabDrillHandler implements
        IChartElementDrillDownListener {

    /**
     * @param metaReader
     * @param eventBus
     * @param descriptor
     */
    public CoreNetworkKpiPortletToTabDrillDownHandler(final IMetaReader metaReader, final EventBus eventBus,
            final PortletDataType descriptor) {
        super(metaReader, eventBus, descriptor);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.dashboard.portlet.AbstractPortletToTabDrillHandler#setSearchAndTimeData(com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType, com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType)
     */
    @Override
    public void setSearchAndTimeData(final SearchFieldDataType searchData, final TimeInfoDataType timeData) {
        /** Taking a copy here, because the Portlet Search itself is by Path Mode whereas the drill down because it is
         * using old services is non-Path Mode so need different searchData objects
         */
        super.setSearchAndTimeData(searchData == null ? searchData : SearchFieldDataType.newInstance(searchData),
                timeData);
    }

    /**
     * @param drillDownDataMap
     */
    @Override
    protected void drillDownChart(final Map<String, String> drillDownDataMap, final IGenericTabView tabView) {
        final String drillDownWindowType = drillDownDataMap.get(CHART_ELEMENT_DRILLDOWN_KEY);
        final String chartMetaId = drillDownDataMap.get(CHART_META_ID);
        final TimeInfoDataType time = getTimeData();
        final Date selectedDate = time.dateTo;
        /** Time is selected date - 1 day **/
        final TimeInfoDataType timeInfoDataType = getDrillDownDateTimeRange(Long.toString(selectedDate.getTime()),
                false);

        final String drillDownWindowId = getDrillDownWindowId(drillDownWindowType);
        /** If the Drill Down Window Id is empty then return as we will have no Meta Data for this DrillDown. The Drill Down Window id is a String
         * suffixed with the node type from the search data **/
        if (drillDownWindowId.isEmpty()) {
            return;
        }
        final ChartDrillDownInfoDataType drillDownInfo = getMetaReader().getChartDrillDownWindowType(drillDownWindowId);

        // chartDrillDownWindows
        final String targetDisplayId = drillDownInfo.getDrillTargetDisplayId();

        /* this is all about getting summary screen 
         * and follow up with drilldown screen - csv string required for gridId in meta data for this to work*/

        final String[] targetDisplayIds = targetDisplayId.split(COMMA);
        // even if no comma will be a 0
        final MetaMenuItem item = new MetaMenuItem(getMetaReader().getMetaMenuItemFromID(targetDisplayIds[0]));

        configureSearchParams();

        if (targetDisplayIds.length == 1) {
            item.setWidgetSpecificParams(getSearchData().getSearchFieldURLParams(false)
                    + getQueryParameters(chartMetaId));
        } else {
            // fall over if not 2 items  - can still only drill down one extra
            item.setWidgetSpecificParams(getSearchData().getSearchFieldURLParams(false));

            // no need for carrying extra search into call (already in widget params from first call) 
            item.setForLaterWidgetSpecificParams(targetDisplayIds[1], getQueryParameters(chartMetaId));
        }

        final AbstractWindowLauncher windowLauncher = new GridLauncher(item, getEventBus(), tabView.getCenterPanel(), tabView.getMenuTaskBar());

        // window exists already
        BaseWindow window = tabView.getMenuTaskBar().getWindow(item.getID());

        /*  have to hide a open multiple instance version too - 
         * if click ACTIVATE or DEACTIVE don't want same window to come to front if exists 
         */
        if (window == null) {
            window = tabView.getMenuTaskBar().getWindow(
                    item.getID() + MULTI_IDENTIFER + this.getSearchData().searchFieldVal);

        }
        WindowState prevWindowState = null;
        if (window != null) {
            prevWindowState = window.getWindowState();
            window.hide();
        }
        /** Launch the window, with toggling flag set as we have toggled the meta menu item to suit a grid so we want the Window Launcher to use
         * this altered meta data rather than retrieving the original copy. This particular method call will prefix the Node Type Name to the title 
         */
        windowLauncher.launchWindow(timeInfoDataType, getSearchData(), true, prevWindowState);
    }

    protected void configureSearchParams() {
        final List<String> urlParamList = new ArrayList<String>();
        /** Need search data for this drill down so no need to create one if null **/
        if (getSearchData() != null) {
            if (getSearchData().urlParams != null) {
                final List<String> currentParams = new ArrayList<String>();
                for (final String param : getSearchData().urlParams) {
                    /** Need to take out the node param as it is incorrect for this service **/
                    if (!param.contains("node")) {
                        currentParams.add(param);
                    } else {
                        final String replacement = param.replaceFirst("node", getSearchData().getType().toLowerCase());
                        currentParams.add(replacement);
                    }
                }

                urlParamList.addAll(currentParams);
            }
            getSearchData().urlParams = urlParamList.toArray(new String[0]);
        }
    }

    /**
     * @param chartMetaId
     * @return
     */
    private String getQueryParameters(final String chartMetaId) {
        final URLParamUtils urlParameters = new URLParamUtils();

        if (chartMetaId != null) { // prevent drills broken in GXT

            /** Get the Event Id of the Chart for use in the Query. Its a fixed chart param **/

            // TODO there is a case here for adding a widgitSpecificParams tag to ChartDataType so not restricted to just one

            final ChartDataType currentChartData = getMetaReader().getChartConfigInfo(chartMetaId);
            if (currentChartData != null && currentChartData.itemInfo != null) {
                for (final ChartItemDataType chartItem : currentChartData.itemInfo) {
                    if (!chartItem.queryParam.isEmpty()) {

                        urlParameters.addOutBoundRegularParameter(chartItem.queryParam + EQUAL_STRING,
                                chartItem.queryParamValue);
                    }
                }
            }
        }
        /* can be pretty specific need search field data for Core KPI (SGSN, APN drilldown) */
        final SearchFieldDataType searchData = getSearchData();

        /* no need to carry search data into the "later" call as will be there already from previous call */
        if (searchData != null && searchData.isGroupMode()) {
            urlParameters.addOutBoundRegularParameter("key=", "SUM");
        }
        return urlParameters.getWidgetSpecificParams();
    }

    /**
     * @param drillDownWindowType
     * @return
     */
    protected String getDrillDownWindowId(final String drillDownWindowType) {
        String fullDrillDownWindowType = "";
        if (getSearchData() == null) {
            return fullDrillDownWindowType;
        } else if (getSearchData().isGroupMode()) {
            fullDrillDownWindowType = getSearchData().getType() == null ? drillDownWindowType + UNDER_SCORE_GROUP
                    : drillDownWindowType + UNDERSCORE + getSearchData().getType() + UNDER_SCORE_GROUP;
        } else {
            fullDrillDownWindowType = drillDownWindowType
                    + (((getSearchData().getType() == null) || getSearchData().getType().equalsIgnoreCase(EMPTY_STRING)) ? EMPTY_STRING
                            : UNDERSCORE + getSearchData().getType());
        }
        return fullDrillDownWindowType;
    }
}
