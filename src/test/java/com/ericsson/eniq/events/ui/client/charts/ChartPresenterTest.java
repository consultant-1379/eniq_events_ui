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
package com.ericsson.eniq.events.ui.client.charts;

import static junit.framework.Assert.*;

import java.util.*;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.common.client.datatype.*;
import com.ericsson.eniq.events.highcharts.client.HighChartsJS;
import com.ericsson.eniq.events.highcharts.client.config.AbstractChartConfiguration;
import com.ericsson.eniq.events.highcharts.client.config.ChartConfigTemplate;
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
import com.ericsson.eniq.events.ui.client.grid.FooterToolBar;
import com.ericsson.eniq.events.ui.client.main.GenericTabView;
import com.ericsson.eniq.events.widgets.client.drill.DrillCategoryType;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;

public class ChartPresenterTest extends TestEniqEventsUI {

    ChartPresenter objectToTest;

    Map<String, Object[]> seriesMap = new HashMap<String, Object[]>();

    AbstractChartConfiguration mockedAbstractChartConfiguration;

    IChartElementDrillDownListener mockedDrillDownListener;

    FooterToolBar mockedFooterToolBar;

    HighChartsJS mockedHighChartsJS;

    IChartConfigTemplateRegistry templateRegistry;

    ChartView chartView;

    @Before
    public void setUp() {
        mockedDrillDownListener = context.mock(IChartElementDrillDownListener.class);
        mockedAbstractChartConfiguration = context.mock(AbstractChartConfiguration.class);
        mockedFooterToolBar = context.mock(FooterToolBar.class);
        mockedHighChartsJS = context.mock(HighChartsJS.class);
        templateRegistry = context.mock(IChartConfigTemplateRegistry.class);
        chartView = context.mock(ChartView.class);
    }

    @Test
    public void pieChartCreatedWhenUseCorrectString() throws Exception {

        setUpExpectationsOnChartConfig();

        objectToTest = new StubbedChartPresenter("pie");
        objectToTest.setConfigData(getDummyChartDataType());
        objectToTest.updateData(seriesMap);

        assertEquals("pie chart created", ChartDisplayType.STANDARD_PIE, ((StubbedChartPresenter) objectToTest).getChartDisplayType());
    }

    @Test
    public void barChartCreatedWhenUseCorrectString() throws Exception {

        setUpExpectationsOnChartConfig();

        objectToTest = new StubbedChartPresenter("bar");
        objectToTest.setConfigData(getDummyChartDataType());
        objectToTest.updateData(seriesMap);

        assertEquals("bar chart created", ChartDisplayType.STANDARD_BAR, ((StubbedChartPresenter) objectToTest).getChartDisplayType());
    }

    @Test
    public void barOneLineChartCreatedWhenUseCorrectString() throws Exception {

        setUpExpectationsOnChartConfig();

        objectToTest = new StubbedChartPresenter("barOneline");
        objectToTest.setConfigData(getDummyChartDataType());
        objectToTest.updateData(seriesMap);

        assertEquals("bar and line chart created", ChartDisplayType.STANDARD_BAR_WITH_LINE,
                ((StubbedChartPresenter) objectToTest).getChartDisplayType());
    }

    @Test
    public void horizontalBarChartCreatedWhenUseCorrectString() throws Exception {

        setUpExpectationsOnChartConfig();

        objectToTest = new StubbedChartPresenter("horzbar");
        objectToTest.setConfigData(getDummyChartDataType());
        objectToTest.updateData(seriesMap);

        assertEquals("horizontal bar created", ChartDisplayType.STANDARD_HORIZONTAL_BAR, ((StubbedChartPresenter) objectToTest).getChartDisplayType());
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void exceptionOccursWhenUseWrongStringToCreate() throws Exception {
        setUpExpectationsOnConstruction();
        context.checking(new Expectations() {
            {
                allowing(mockedEventBus).addHandler(with(any(Type.class)), with(any(EventHandler.class)));
                one(mockedHighChartsJS).clearJS();

            }
        });
        objectToTest = new StubbedChartPresenter("badString");
        objectToTest.setConfigData(getDummyChartDataType());
        objectToTest.updateData(seriesMap);
    }

    @Test
    public void addChartDrillDownListener() throws Exception {
        setUpExpectationsOnConstruction();
        objectToTest = new StubbedChartPresenter("bar");
        objectToTest.addChartDrillDownListener(mockedDrillDownListener);

    }

    @Test
    public void getChartRowCountNormal() throws Exception {

        setUpExpectationsOnChartConfig();

        objectToTest = new StubbedChartPresenter("horzbar");
        objectToTest.setConfigData(getDummyChartDataType());
        objectToTest.updateData(seriesMap);

        assertEquals("horizontal bar created", ChartDisplayType.STANDARD_HORIZONTAL_BAR, ((StubbedChartPresenter) objectToTest).getChartDisplayType());

        context.checking(new Expectations() {
            {
                one(mockedAbstractChartConfiguration).getRowCount();
                will(returnValue(6));

            }
        });

        final int actual = objectToTest.getChartRowCount();
        assertEquals("6 count returns 6 ", 6, actual);

    }

    @Test
    public void hideShowChartLegendFail() throws Exception {
        setUpExpectationsOnConstruction();
        objectToTest = new StubbedChartPresenter("bar");
        final boolean actual = objectToTest.hideShowChartLegend();

        assertEquals("expect false return - no model  ", false, actual);
    }

    @Test
    public void hideShowChartLegend() throws Exception {
        setUpExpectationsOnConstruction();
        objectToTest = new StubbedChartPresenter("bar");
        setUpExpectationsOnChartConfig();
        context.checking(new Expectations() {
            {
                one(mockedAbstractChartConfiguration).toggleLegendEnabled();
                one(mockedHighChartsJS).doRender();
            }
        });
        objectToTest.setConfigData(getDummyChartDataType());
        objectToTest.updateData(seriesMap);

        final boolean actual = objectToTest.hideShowChartLegend();

        assertEquals("expect true return - have model  ", true, actual);

    }

    /////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    private void setUpExpectationsOnChartConfig() {

        context.checking(new Expectations() {
            {
                allowing(mockedEventBus).addHandler(with(any(Type.class)), with(any(EventHandler.class)));
                allowing(mockedHighChartsJS).clearJS();
                allowing(mockedAbstractChartConfiguration).buildChart();
                allowing(mockedAbstractChartConfiguration).showChartElements(with(any(Set.class)));
                allowing(mockedHighChartsJS).getChartName();
                will(returnValue("testId"));
                allowing(chartView).isRendered();
                will(returnValue(false));
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void setUpExpectationsOnConstruction() {

        context.checking(new Expectations() {
            {
                allowing(mockedEventBus).addHandler(with(any(Type.class)), with(any(EventHandler.class)));
                allowing(mockedHighChartsJS).getChartName();
                will(returnValue("testId"));
            }
        });
    }

    private ChartDataType getDummyChartDataType() {
        final ChartDataType type = new ChartDataType();
        type.id = "KPI_ANALYSIS";
        type.chartTitle = "KPI Analysis";
        type.xlabel = "time internal";
        type.ylabel = "num of events";

        final ChartItemDataType line1 = new ChartItemDataType();
        line1.id = "1";
        line1.color = "347C2C";
        line1.name = "PDP Context Attach SR";

        final ChartItemDataType line2 = new ChartItemDataType();
        line2.id = "2";
        line2.color = "347C3C";
        line2.name = " Attach Success Rate";

        final ChartItemDataType line3 = new ChartItemDataType();
        line3.id = "3";
        line3.color = "347C4C";
        line3.name = " Attach Failure Rate";

        type.itemInfo = new ChartItemDataType[] { line1, line2, line3 };

        return type;

    }

    private class StubbedChartPresenter extends ChartPresenter {

        private final String displayType;
        private ChartDisplayType chartDisplayType;

        public StubbedChartPresenter(final String displayType) {
            super(templateRegistry, mockedEventBus, new StubbedMetaReader());
            this.displayType = displayType;
            init(displayType);
        }

        @SuppressWarnings("hiding")
        @Override
        public void init(final String displayType) {
        }

        @Override
        protected ChartView getView() {
            return chartView;
        }

        @Override
        protected HighChartsJS getHighChartsJS() {
            return mockedHighChartsJS;
        }

        @Override
        protected ChartConfigTemplate getChartConfiguration() {
            chartDisplayType = ChartDisplayType.fromString(displayType);
            return mockedAbstractChartConfiguration;
        }

        protected ChartDisplayType getChartDisplayType() {
            return chartDisplayType;
        }
    }

    private class StubbedMetaReader implements IMetaReader {

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
}
