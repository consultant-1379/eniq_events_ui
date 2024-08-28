package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import java.util.Map;

import com.ericsson.eniq.events.common.client.datatype.ChartDrillDownInfoDataType;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.main.GridLauncher;
import com.ericsson.eniq.events.ui.client.main.IGenericTabView;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.highcharts.client.ChartConstants.CHART_ELEMENT_DRILLDOWN_KEY;

public class NetworkKpiPortletToTabDrillDownHandler extends AbstractPortletToTabDrillHandler {

    public NetworkKpiPortletToTabDrillDownHandler(final IMetaReader metaReader, final EventBus eventBus,
            final PortletDataType descriptor) {
        super(metaReader, eventBus, descriptor);
    }

    @Override
    protected void drillDownChart(final Map<String, String> drillDownDataMap, final IGenericTabView tabView) {
        final String drillDownWindowType = drillDownDataMap.get(CHART_ELEMENT_DRILLDOWN_KEY);

        final ChartDrillDownInfoDataType drillDownInfo = getMetaReader().getChartDrillDownWindowType(
                drillDownWindowType);
        final MetaMenuItem item = new MetaMenuItem(getMetaReader().getMetaMenuItemFromID(
                drillDownInfo.getDrillTargetDisplayId()));

        /** Launch the window, with toggling flag set as we have toggled the meta menu item to suit a grid so we want the Window Launcher to use
         * this altered meta data rather than retrieving the original copy 
         */
        final BaseWindow window = tabView.getMenuTaskBar().getWindow(item.getID());
        if (window != null) {
            window.hide();
        }

        final GridLauncher windowLauncher = new GridLauncher(item, getEventBus(), tabView.getCenterPanel(),
                tabView.getMenuTaskBar());
        /** Launch the window, with toggling flag set as we have toggled the meta menu item to suit a grid so we want the Window Launcher to use
         * this altered meta data rather than retrieving the original copy. This particular method call will prefix the Node Type Name to the title 
         */
        // TODO: consider to pass WindowsState as the last parameter to launchWindow(...): it is especially required
        // when it was drilled down from another window
        windowLauncher.launchWindow(getTimeData(), getSearchData(), true);
    }

}
