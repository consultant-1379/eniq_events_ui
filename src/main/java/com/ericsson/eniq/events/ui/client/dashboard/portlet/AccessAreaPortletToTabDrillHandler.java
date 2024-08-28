/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import java.util.Map;

import com.ericsson.eniq.events.common.client.datatype.ChartDataType;
import com.ericsson.eniq.events.common.client.datatype.ChartItemDataType;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.common.comp.WindowState;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.main.GridLauncher;
import com.ericsson.eniq.events.ui.client.main.IGenericTabView;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.highcharts.client.ChartConstants.CHART_ELEMENT_DRILLDOWN_KEY;
import static com.ericsson.eniq.events.highcharts.client.ChartConstants.CHART_META_ID;
import static com.ericsson.eniq.events.ui.client.common.Constants.AMPERSAND;
import static com.ericsson.eniq.events.ui.client.common.Constants.COMMA;
import static com.ericsson.eniq.events.ui.client.common.Constants.EQUAL_STRING;

/**
 * @author echimma
 * @since 2012
 *
 */
public class AccessAreaPortletToTabDrillHandler extends AbstractPortletToTabDrillHandler {

    private static final String WCDMA_NODES = "wcdmaNodes";

    /** Append this suffix to drillWindowType to retrieve correct meta items and grids for WCDMA nodes.
     * Probably not all the nice, but no point in defining lots of the exact same grids for different node types **/
    private static final String WCDMA_SUFFIX = "_WCDMA";

    public AccessAreaPortletToTabDrillHandler(final IMetaReader metaReader, final EventBus eventBus,
            final PortletDataType descriptor) {
        super(metaReader, eventBus, descriptor);

    }

    @Override
    protected void drillDownChart(final Map<String, String> drillDownDataMap, final IGenericTabView tabView) {
        final String drillDownWindowType = drillDownDataMap.get(CHART_ELEMENT_DRILLDOWN_KEY);
        final String gridWindowId = getMetaMenuItemId(drillDownWindowType);
        final BaseWindow window = tabView.getMenuTaskBar().getWindow(gridWindowId);

        WindowState prevWindowState = null;
        if (window != null) {
            prevWindowState = window.getWindowState();
            window.hide();
        }

        final MetaMenuItem gridMetaMenuItem = new MetaMenuItem(getMetaReader().getMetaMenuItemFromID(gridWindowId));

        gridMetaMenuItem.setWidgetSpecificParams(getWidgetSpecificParams(drillDownDataMap));

        final GridLauncher gridLauncher = new GridLauncher(gridMetaMenuItem, getEventBus(), tabView.getCenterPanel(),
                tabView.getMenuTaskBar());

        gridLauncher.launchWindow(getTimeData(), getSearchData(), true, prevWindowState);

    }

    private String getMetaMenuItemId(final String elementDrillDownId) {
        final String wcdmaNodeList = getDescriptor().getParameters().getParameter(WCDMA_NODES);
        if (wcdmaNodeList != null && !wcdmaNodeList.isEmpty()) {
            final String[] wcdmaNodes = wcdmaNodeList.split(COMMA);
            for (final String wcdmaNode : wcdmaNodes) {
                if (getSearchData().getType().equals(wcdmaNode.trim())) {
                    return elementDrillDownId + WCDMA_SUFFIX;
                }
            }
        }
        return elementDrillDownId;
    }

    /*
     * Adding widget specfic parameter to new grid call
     * (Not requiring to be a search field user to add parameters)
     */
    private String getWidgetSpecificParams(final Map<String, String> drillDownDataMap) {
        final String chartMetaID = drillDownDataMap.get(CHART_META_ID);
        final StringBuilder chartQueryParms = new StringBuilder();

        if (chartMetaID != null) {
            final ChartDataType currentChartData = getMetaReader().getChartConfigInfo(chartMetaID);
            for (final ChartItemDataType chartItem : currentChartData.itemInfo) {
                if (!chartItem.queryParam.isEmpty()) {
                    chartQueryParms.append(AMPERSAND);
                    chartQueryParms.append(chartItem.queryParam);
                    chartQueryParms.append(EQUAL_STRING);
                    chartQueryParms.append(drillDownDataMap.get(chartItem.queryParam));
                }
            }
        }
        return chartQueryParms.toString();
    }

}
