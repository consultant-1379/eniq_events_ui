/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import java.util.Map;

import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.common.comp.WindowState;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.main.ChartLauncher;
import com.ericsson.eniq.events.ui.client.main.IGenericTabView;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.highcharts.client.ChartConstants.CHART_ELEMENT_DRILLDOWN_KEY;
import static com.ericsson.eniq.events.highcharts.client.ChartConstants.CHART_ELEMENT_SELECTED_KEY;
import static com.ericsson.eniq.events.ui.client.common.Constants.UNDERSCORE;

/**
 * This class handles drill down from the Data Volume Portlet Charts to a Grid based on the time of the selected chart point
 * @author ecarsea
 * @since 2011
 *
 */
public class DataVolumePortletToTabDrillDownHandler extends AbstractPortletToTabDrillHandler {

    private static final String NETWORK_CHART_ID_PARAM = "networkChartId", ENTITY_CHART_ID = "entityChartId";

    /**
     * @param eventBus 
     * @param metaReader 
     * 
     */
    public DataVolumePortletToTabDrillDownHandler(final IMetaReader metaReader, final EventBus eventBus,
            final PortletDataType descriptor) {
        super(metaReader, eventBus, descriptor);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.dashboard.portlet.AbstractPortletToTabDrillHandler#drillDownChart(java.util.Map, com.ericsson.eniq.events.ui.client.main.IGenericTabView)
     */
    @Override
    protected void drillDownChart(final Map<String, String> drillDownDataMap, final IGenericTabView tabView) {
        final String chartElementClicked = drillDownDataMap.get(CHART_ELEMENT_SELECTED_KEY);
        final String drillDownWindowType = drillDownDataMap.get(CHART_ELEMENT_DRILLDOWN_KEY);

        /** Time range is today + 1 days **/
        final TimeInfoDataType timeInfoDataType = getDrillDownDateTimeRange(chartElementClicked, true);

        final MetaMenuItem generatedMetaMenuItem = getChartMetaMenuItem(drillDownWindowType);

        /** Launch the window, with toggling flag set as we have toggled the meta menu item to suit a grid so we want the Window Launcher to use
         * this altered meta data rather than retrieving the original copy 
         */
        final BaseWindow window = tabView.getMenuTaskBar().getWindow(generatedMetaMenuItem.getID());
        WindowState prevWindowState = null;
        if (window != null) {
            prevWindowState = window.getWindowState();
            window.hide();
        }
        final ChartLauncher windowLauncher = new ChartLauncher(generatedMetaMenuItem, getEventBus(),
                tabView.getCenterPanel(), tabView.getMenuTaskBar());
        /** Launch the window, with toggling flag set as we have toggled the meta menu item to suit a grid so we want the Window Launcher to use
         * this altered meta data rather than retrieving the original copy. This particular method call will prefix the Node Type Name to the title 
         */
        windowLauncher.launchWindow(timeInfoDataType, getSearchData(), true, prevWindowState);
    }

    /**
     * Get Menu item for chart (from extra menu items, i.e. without the wizard)
     * The drill down window type suffix indicates whether it is an uplink or downlink or pdp chart, 
     * 
     * @param drillDownWindowType    e.g UPLINK or DOWNLINK
     * @return        Menu item for chart (from extra menu items, i.e. without the wizard)
     */
    protected MetaMenuItem getChartMetaMenuItem(final String drillDownWindowType) {

        /* for data volume (legacy) "no" search data is treated as network view and we have separate
         * charts for network view and views by entity.  3G and Radio Network Views not distinguished, 
         * we just want the legacy calls to work 
         */

        final SearchFieldDataType searchData = getSearchData();
        final boolean isNetworkView = searchData == null || SearchFieldDataType.isSummaryType(searchData.getType());

        /* Different chart used if there is search data, i.e. NETWORK_DATAVOL_ANALYSIS or  DATAVOL_ANALYSIS */

        final String chartId = isNetworkView ? getDescriptor().getParameters().getParameter(NETWORK_CHART_ID_PARAM)
                : getDescriptor().getParameters().getParameter(ENTITY_CHART_ID);

        final MetaMenuItem chartMetaMenuItem = new MetaMenuItem(getMetaReader().getMetaMenuItemFromID(
                chartId + UNDERSCORE + drillDownWindowType));

        return chartMetaMenuItem;
    }
}
