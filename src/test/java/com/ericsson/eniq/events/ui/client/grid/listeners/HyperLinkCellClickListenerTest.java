/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.grid.listeners;

import java.util.*;

import org.jmock.Expectations;
import org.junit.*;

import com.ericsson.eniq.events.common.client.datatype.*;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.MenuTaskBar;
import com.ericsson.eniq.events.ui.client.common.widget.MetaDataChangeComponent;
import com.ericsson.eniq.events.ui.client.datatype.*;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.DashBoardDataType;
import com.ericsson.eniq.events.ui.client.datatype.group.GroupMgmtConfigDataType;
import com.ericsson.eniq.events.ui.client.datatype.kpi.KPIConfigurationPanelDataType;
import com.ericsson.eniq.events.ui.client.events.HyperLinkCellClickEvent;
import com.ericsson.eniq.events.ui.client.main.GenericTabView;
import com.ericsson.eniq.events.widgets.client.drill.DrillCategoryType;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.grid.Grid;

public class HyperLinkCellClickListenerTest extends TestEniqEventsUI {

    private HyperLinkCellClickListener objectToTest;

    GridEvent<ModelData> mockedBaseEvent;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        mockedBaseEvent = context.mock(GridEvent.class);
        objectToTest = new HyperLinkCellClickListener(createMultipleInstanceWinId("NETWORK_TAB", "myWinID", null), mockedEventBus,
                new StubbedMetaReader());
    }

    @Test
    @Ignore
    public void clickingCellHyperLinkFiresEvent() {
        final Grid grid = new StubbedGrid();
        context.checking(new Expectations() {
            {
                one(mockedBaseEvent).getClientX();
                one(mockedBaseEvent).getClientY();
                one(mockedBaseEvent).getRowIndex();
                one(mockedBaseEvent).getTarget(".gridCellLink", 1);
                one(mockedBaseEvent).getGrid();
                will(returnValue(grid));
                one(mockedEventBus).fireEvent(with(any(HyperLinkCellClickEvent.class)));
            }
        });
        objectToTest.handleEvent(mockedBaseEvent);
    }

    private MultipleInstanceWinId createMultipleInstanceWinId(final String tabId, final String winId, final SearchFieldDataType searchData) {
        return new MultipleInstanceWinId(tabId, winId);
    }
}

class StubbedGrid extends Grid {
}

class StubbedMetaReader implements IMetaReader {

    @Override
    public void loadMetaData() {
    }

    @Override
    public void loadMetaData(final String metaDataPath) {
    }

    @Override
    public String getCompletedURL(final String urlEnd) {
        return null;
    }

    @Override
    public Integer getGridRowsPerPage() {
        return null;
    }

    @Override
    public String getLoadingMessage() {
        return null;
    }

    @Override
    public String getLoadingRenderingMessage() {
        return null;
    }

    @Override
    public int getRankingTimerInterval() {
        return 0;
    }

    @Override
    public int getMaxInstanceWindowsPerType() {
        return 0;
    }

    @Override
    public boolean getIsAutoRefreshOn() {
        return false;
    }

    @Override
    public MenuTaskBar getMenuTaskBar(final GenericTabView parentView, final String tabId) {
        return null;
    }

    @Override
    public MetaMenuItemDataType getKPIMetaMenuItemDataType() {
        return null;
    }

    @Override
    public MetaMenuItemDataType getKPICSMetaMenuItemDataType() {
        return null;
    }

    @Override
    public WizardInfoDataType getWizardMetaMenuItemDataType(final String wizardID) {
        return null;
    }

    @Override
    public String getRecurringErrSummaryWebServiceURL(final EventType eventID) {
        return null;
    }

    @Override
    public List<Component> getMenuItems(final String tabId) {
        return null;
    }

    @Override
    public ToolBarInfoDataType getToolBarItems(final String toolbarOwnerId) {
        return null;
    }

    @Override
    public MetaMenuItem getMetaMenuItemFromID(final String id) {
        return null;
    }

    @Override
    public MetaMenuItemDataType getMetaMenuItemDataType(final String id) {
        return null;
    }

    @Override
    public String getTimeComboData() {
        return null;
    }

    @Override
    public DrillDownInfoDataType getDrillDownWindowType(final String drillWinTypeID) {
        return null;
    }

    @Override
    public ChartDrillDownInfoDataType getChartDrillDownWindowType(final String chartDrillWinTypeID) {
        return null;
    }

    @Override
    public ChartDataType getChartConfigInfo(final String chartID) {
        return null;
    }

    @Override
    public GridInfoDataType getGridInfo(final String gridType) {
        return null;
    }

    @Override
    public MetaMenuItemDataType getRecurErrMetaMenuItemDataType() {
        return null;
    }

    @Override
    public MetaDataChangeComponent getMetaDataChangeComponent() {
        return null;
    }

    @Override
    public List<TabInfoDataType> getTabDataMetaInfo() {
        return null;
    }

    @Override
    public Collection<LiveLoadTypeDataType> getLiveLoadTypes(final String searchTypeJSONResponseText) {
        return null;
    }

    @Override
    public Map<String, String> getRecurErrHeadersParameters(final boolean includeOptional) {
        return null;
    }

    @Override
    public LaunchWinDataType getLaunchWinFromHyperLink(final String launchWinID) {
        return null;
    }

    @Override
    public VerticalGridColumnHeaders getVerticalGridColumnHeaders(final String menuItem) {
        return null;
    }

    @Override
    public List<LicenceGroupTypeDataType> getLicenceGroupTypeDataType() {
        return null;
    }

    @Override
    public GroupMgmtConfigDataType getGroupManagementConfigData() {
        return null;
    }

    @Override
    public KpiPanelType getKpiPanelMetaData() {
        return null;
    }

    @Override
    public KPIConfigurationPanelDataType getKPIConfigurationPanelMetaData() {
        return null;
    }

    @Override
    public List<LicenseInfoDataType> getLicenses() {
        return null;
    }

    @Override
    public DashBoardDataType getDashBoardData(final String tabId, final String WCDMAorLTE) {
        return null;
    }

    @Override
    public ChartDataType getChartConfigInfo(final String chartID, final boolean isGroup) {
        return null;
    }

    @Override
    public Map<String, List<DrillCategoryType>> getDrillManagerData() {
        return null;
    }

    @Override
    public List<String> getSupportedAccessGroups() {
        return null;
    }

}