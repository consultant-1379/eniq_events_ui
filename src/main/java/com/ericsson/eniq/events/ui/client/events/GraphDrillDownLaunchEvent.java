/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.common.client.datatype.ChartDrillDownInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldUser;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to launch a seperate window based on a click event
 * with a Graph
 *
 * @author eendmcm
 * @since 2011
 */
public class GraphDrillDownLaunchEvent extends GwtEvent<GraphDrillDownLaunchEventHandler> {

    public final static Type<GraphDrillDownLaunchEventHandler> TYPE = new Type<GraphDrillDownLaunchEventHandler>();

    private final ChartDrillDownInfoDataType chartDrillDownInfo;

    private final String chartElementSelected;

    private final SearchFieldDataType searchData;

    private final String parentStyle;

    private final TimeInfoDataType parentTime;

    private final String queryParameters;

    private final String tabOwnerId;

    private final SearchFieldUser searchFieldUser;

    /**
     * @param info             - data type containing the info the the drill down
     * @param chartElementSelected      - the identity of the chart that raised the event
     * @param searchFieldUser  - the search data as provided on the original window
     * @param windowStyle      - the style used on the parent window
     * @apram tabOwnerId - tab owener guard
     * @queryParameters - holds the query parameters that are passed from the Graph click
     */
    public GraphDrillDownLaunchEvent(final String tabOwnerId, final ChartDrillDownInfoDataType info,
                                     final String chartElementSelected, final SearchFieldDataType searchData,
                                     final String windowStyle, final TimeInfoDataType time, final String queryParameters, final SearchFieldUser searchFieldUser) {

        this.searchFieldUser = searchFieldUser;
        this.tabOwnerId = tabOwnerId;
        this.chartDrillDownInfo = info;
        this.chartElementSelected = chartElementSelected;
        this.searchData = searchData;
        this.parentStyle = windowStyle;
        this.parentTime = time;
        this.queryParameters = queryParameters;
    }

    @Override
    public Type<GraphDrillDownLaunchEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final GraphDrillDownLaunchEventHandler handler) {
        handler.handleLaunchFromGraphDrillDown(this);

    }

    public ChartDrillDownInfoDataType getChartDrillDownInfo() {
        return chartDrillDownInfo;
    }

    public String getChartElementSelected() {
        return chartElementSelected;
    }

    public SearchFieldDataType getSearchData() {
        return searchData;
    }

    public String getParentStyle() {
        return parentStyle;
    }

    public TimeInfoDataType getParentTime() {
        return parentTime;
    }

    public String getQueryParameters() {
        return queryParameters;
    }

    public String getTabOwnerId() {
        return tabOwnerId;
    }

    public SearchFieldUser getSearchFieldUser() {
        return searchFieldUser;
    }
}
