/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.buttonenabling;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.MetaReaderConstants;
import com.ericsson.eniq.events.ui.client.common.comp.BaseToolBar;
import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.ericsson.eniq.events.ui.client.datatype.ButtonEnableParametersDataType;
import com.ericsson.eniq.events.ui.client.datatype.DrillDownInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.DrillDownParameterInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.web.bindery.event.shared.EventBus;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

/**
 * @author eeicmsy
 *
 */
@Ignore("eendmcm, Need to Ignore as Constructor of ContentPanel calls into Native Method setParent, will revisit... ")
public class ToolBarButtonManagerTest extends TestEniqEventsUI {

    private IExtendedWidgetDisplay mockedDisplay;

    private BaseToolBar baseToolBarStub;

    private Button mockedButton;

    @Before
    public void setUp() {

        mockedDisplay = context.mock(IExtendedWidgetDisplay.class);
        mockedButton = context.mock(Button.class);

        baseToolBarStub = new BaseToolBarStub(createMultipleInstanceWinId("tabId", "winId", null), null);

    }

    private MultipleInstanceWinId createMultipleInstanceWinId(final String tabId, final String winId,
            final SearchFieldDataType searchFieldData) {
        return new MultipleInstanceWinId(tabId, winId/*, searchFieldData*/);
    }

    @Test
    public void testAllButtonsEnableStatusChecked() throws Exception {

        final ButtonEnableParametersDataType emptySettings = new ButtonEnableParametersDataType();

        expectedAllEnablingSet(false);
        ToolBarButtonManager.handleToolbarButtonEnabling(mockedDisplay, baseToolBarStub, emptySettings);

    }

    @Test
    public void testButtonNotCoveredReturnsFalse() throws Exception {
        assertEquals("button should be disabled !", false, ToolBarButtonManager.shouldEnableButton("RANDOM", null));

    }

    @Test
    public void SACButtonEnabledOnceSearchDataTypeIsCell() throws Exception {

        final ButtonEnableParametersDataType currentSettings = new ButtonEnableParametersDataType();

        final SearchFieldDataType searchData = new SearchFieldDataType("some val", null, "CELL", null, false, "CS,PS", null, false);

        currentSettings.searchData = searchData;

        assertEquals("Sac button should be enabled !", true,
                ToolBarButtonManager.shouldEnableButton("btnSac", currentSettings));

    }

    @Test
    public void SACButtonEnabledOnceDrillDownToCell() throws Exception {

        final ButtonEnableParametersDataType currentSettings = new ButtonEnableParametersDataType();

        // currentSettings.widgetSpecificInfo = getDummyDrillInfo("type", "CELL");
        currentSettings.widgetSpecificInfo = getDummyDrillInfo("cell", "someVal");

        final SearchFieldDataType searchData = new SearchFieldDataType("some val", null, "NOT A CELL", null, false,
                "CS,PS", null, false);
        currentSettings.searchData = searchData;

        assertEquals("Sac button should be enabled !", true,
                ToolBarButtonManager.shouldEnableButton("btnSac", currentSettings));

    }

    @Test
    public void SACButtonDisabledIfNotDrilledToCell() throws Exception {

        final ButtonEnableParametersDataType currentSettings = new ButtonEnableParametersDataType();

        currentSettings.widgetSpecificInfo = getDummyDrillInfo("type", "NOT A CELL");

        final SearchFieldDataType searchData = new SearchFieldDataType("some val", null, "NOT A CELL", null, false,
                "CS,PS", null, false);
        currentSettings.searchData = searchData;

        assertEquals("Sac button should be disabled !", false,
                ToolBarButtonManager.shouldEnableButton("btnSac", currentSettings));

    }

    @Test
    public void SACButtonDisabledIfSearchDataNotACell() throws Exception {

        final ButtonEnableParametersDataType currentSettings = new ButtonEnableParametersDataType();

        final SearchFieldDataType searchData = new SearchFieldDataType(null, null, null, null, false, "CS,PS", null, false);

        currentSettings.searchData = searchData;

        assertEquals("Sac button should be disabled !", false,
                ToolBarButtonManager.shouldEnableButton("btnSac", currentSettings));

    }

    @Test
    public void SACButtonDisabledForGroups() throws Exception {

        final ButtonEnableParametersDataType currentSettings = new ButtonEnableParametersDataType();

        final SearchFieldDataType searchData = new SearchFieldDataType("some val", null, "CELL", null, true, "CS,PS", null, false);
        currentSettings.searchData = searchData;

        assertEquals("Sac button should be disabled for groups !", false,
                ToolBarButtonManager.shouldEnableButton("btnSac", currentSettings));

    }

    @Test
    public void testCorrectButtonsDisabledWhenNoData() throws Exception {

        final ButtonEnableParametersDataType currentSettings = new ButtonEnableParametersDataType();
        final SearchFieldDataType searchData = new SearchFieldDataType("", null, "CELL", null, false, "CS,PS", null, false);

        currentSettings.searchData = searchData;
        currentSettings.rowCount = 0;

        assertEquals("SAC button should be enabled !", true,
                ToolBarButtonManager.shouldEnableButton("btnSac", currentSettings));

        // TODO TEMP OFF WHILE PERMINANTLY ENABLE KPI BUTTON FOR MSS BRANCH
        //        assertEquals("KPI button should be disabled !", false, ToolBarButtonManager.shouldEnableButton("btnKPI",
        //                currentSettings));
        assertEquals("Properties button should be disabled !", false,
                ToolBarButtonManager.shouldEnableButton("btnProperties", currentSettings));
        assertEquals("Recur Error button should be disabled !", false,
                ToolBarButtonManager.shouldEnableButton("btnRecur", currentSettings));
        assertEquals("Export button should be disabled !", false,
                ToolBarButtonManager.shouldEnableButton("btnExport", currentSettings));
        assertEquals("SubscriberDetails button should be disabled !", false,
                ToolBarButtonManager.shouldEnableButton("btnSubscriberDetails", currentSettings));
        assertEquals("Legend button should be disabled !", false,
                ToolBarButtonManager.shouldEnableButton("btnHideShowLegend", currentSettings));
        assertEquals("ToggleToGrid button should be disabled !", false,
                ToolBarButtonManager.shouldEnableButton("btnToggleToGrid", currentSettings));

    }

    @Test
    public void testCorrectButtonsDisabledWhenHaveData() throws Exception {

        final ButtonEnableParametersDataType currentSettings = new ButtonEnableParametersDataType();
        final SearchFieldDataType searchData = new SearchFieldDataType(null, null, "CELL", null, false, "CS,PS", null, false);

        currentSettings.searchData = searchData;
        currentSettings.rowCount = 40;

        assertEquals("Export button should be enabled !", true,
                ToolBarButtonManager.shouldEnableButton("btnExport", currentSettings));
        assertEquals("Legend button should be enabled !", true,
                ToolBarButtonManager.shouldEnableButton("btnHideShowLegend", currentSettings));
        assertEquals("ToggleToGrid button should be enabled !", true,
                ToolBarButtonManager.shouldEnableButton("btnToggleToGrid", currentSettings));
    }

    @Test
    public void subscriberDetailsButtonChecks() throws Exception {
        final ButtonEnableParametersDataType currentSettings = new ButtonEnableParametersDataType();

        final SearchFieldDataType groupSearchData = new SearchFieldDataType("somedata", null, null, null, true, "CS,PS", null, false);

        currentSettings.searchData = groupSearchData;
        assertEquals("SubscriberDetails button should be disabled for group mode !", false,
                ToolBarButtonManager.shouldEnableButton("btnSubscriberDetails", currentSettings));

        final SearchFieldDataType imsiSearchData = new SearchFieldDataType("12345", new String[] { "imsi=" }, null, null,
                false, "CS,PS", null, false);
        currentSettings.searchData = imsiSearchData;
        assertEquals("SubscriberDetails button should be enabled for IMSI search data !", true,
                ToolBarButtonManager.shouldEnableButton("btnSubscriberDetails", currentSettings));

    }

    @Test
    public void recurringErrorButtonCheck() throws Exception {

        final ButtonEnableParametersDataType currentSettings = new ButtonEnableParametersDataType();

        currentSettings.rowCount = 40;

        currentSettings.columnsMetaData = new GridInfoDataType();
        currentSettings.columnsMetaData.columnInfo = getDummyColumnHeaders();

        ToolBarButtonManager.getButtonEnableMap().put(MetaReaderConstants.BTN_RECUR_ERROR,
                new RButtonEnableConditionsStub(false));
        assertEquals("Recurring Error button should be disabled!", false,
                ToolBarButtonManager.shouldEnableButton("btnRecur", currentSettings));

        ToolBarButtonManager.getButtonEnableMap().put(MetaReaderConstants.BTN_RECUR_ERROR,
                new RButtonEnableConditionsStub(true));
        currentSettings.isRowSelected = true;
        assertEquals("Recurring Error button should be enabled!", true,
                ToolBarButtonManager.shouldEnableButton("btnRecur", currentSettings));

    }

    // TODO  TEMP OFF WHILE PERMINANTLY ENABLE KPI BUTTON FOR MSS BRANCH 
    // @Test
    public void kpiButtonChecks() throws Exception {
        final ButtonEnableParametersDataType currentSettings = new ButtonEnableParametersDataType();

        final SearchFieldDataType groupSearchData = new SearchFieldDataType("somedata", null, null, null, true, "CS,PS", null, false);

        currentSettings.searchData = groupSearchData;
        currentSettings.rowCount = 1;
        assertEquals("KPI button should be enabled for group mode !", true,
                ToolBarButtonManager.shouldEnableButton("btnKPI", currentSettings));

        currentSettings.widgetSpecificInfo = this.getDummyDrillInfo("eventID", "whatever");

        assertEquals("KPI button should be disabled for raw events !", false,
                ToolBarButtonManager.shouldEnableButton("btnKPI", currentSettings));

        currentSettings.widgetSpecificInfo = this.getDummyDrillInfo("key", "ERR");

        assertEquals("KPI button should be disabled for Failures drilldown !", false,
                ToolBarButtonManager.shouldEnableButton("btnKPI", currentSettings));

        currentSettings.widgetSpecificInfo = this.getDummyDrillInfo("type", "BSC");

        assertEquals("KPI button should be enabled when not raw events !", true,
                ToolBarButtonManager.shouldEnableButton("btnKPI", currentSettings));

        currentSettings.searchData = new SearchFieldDataType("", null, null, null, false, "CS,PS", null, false);

        assertEquals("KPI button should be disabled for empty search data !", false,
                ToolBarButtonManager.shouldEnableButton("btnKPI", currentSettings));

        final String[] urlParams = { "something=xyz", "imsi=123" };
        currentSettings.searchData = new SearchFieldDataType("somedata", urlParams, null, null, false, "CS,PS", null, false);
        currentSettings.widgetSpecificInfo = this.getDummyDrillInfo("something", "123");
        assertEquals("KPI button should be disabled for imsi and ptmsi search values !", false,
                ToolBarButtonManager.shouldEnableButton("btnKPI", currentSettings));

        final String[] urlPar = { "something=xyz", "groupname=imsi_grp", "type=IMSI" };
        currentSettings.searchData = new SearchFieldDataType("somedata", urlPar, null, null, false, "CS,PS", null, false);
        //currentSettings.widgetSpecificInfo = this.getDummyDrillInfo("something", "123");

        assertEquals("KPI button should be disabled for imsi group values !", false,
                ToolBarButtonManager.shouldEnableButton("btnKPI", currentSettings));

    }

    private void expectedAllEnablingSet(final boolean aFlag) {
        context.checking(new Expectations() {
            {
                allowing(mockedButton).isEnabled();
                one(mockedDisplay).setToolbarButtonEnabled("btnProperties", aFlag);
                one(mockedDisplay).setToolbarButtonEnabled("btnRecur", aFlag);
                one(mockedDisplay).setToolbarButtonEnabled("btnKPI", aFlag);
                one(mockedDisplay).setToolbarButtonEnabled("btnKPI_CS", aFlag);
                one(mockedDisplay).setToolbarButtonEnabled("btnSac", aFlag);
                one(mockedDisplay).setToolbarButtonEnabled("btnExport", aFlag);
                one(mockedDisplay).setToolbarButtonEnabled("btnSubscriberDetails", aFlag);
                one(mockedDisplay).setToolbarButtonEnabled("btnSubscriberDetailsPTMSI", aFlag);
                one(mockedDisplay).setToolbarButtonEnabled("btnSubscriberDetails_CS", aFlag);
                one(mockedDisplay).setToolbarButtonEnabled("btnSubscriberDetailsMSISDN_CS", aFlag);
                one(mockedDisplay).setToolbarButtonEnabled("btnHideShowLegend", aFlag);
                one(mockedDisplay).setToolbarButtonEnabled("btnToggleToGrid", aFlag);
            }
        });

    }

    private DrillDownInfoDataType getDummyDrillInfo(final String parameterName, final String parameterValue) {
        final DrillDownInfoDataType drillInfo = new DrillDownInfoDataType("url");
        drillInfo.displayType = "grid";
        drillInfo.id = "drill";
        drillInfo.isEnabled = true;
        drillInfo.name = "TEST DRILL";
        drillInfo.style = "TEST STYLE";
        drillInfo.toolBarType = "ToolbarType3";
        drillInfo.url = "www.ericsson.se";
        drillInfo.type = "IMSI";
        drillInfo.queryParameters = new DrillDownParameterInfoDataType[1];

        for (int x = 0; x < 1; x++) {

            drillInfo.queryParameters[x] = new DrillDownParameterInfoDataType();
            drillInfo.queryParameters[x].parameterName = parameterName;
            drillInfo.queryParameters[x].parameterValue = parameterValue;
        }
        return drillInfo;
    }

    private ColumnInfoDataType[] getDummyColumnHeaders() {
        final ColumnInfoDataType columnInfoDataType1 = new ColumnInfoDataType();
        columnInfoDataType1.columnHeader = "DummyHeader1";
        final ColumnInfoDataType columnInfoDataType2 = new ColumnInfoDataType();
        columnInfoDataType2.columnHeader = "DummyHeader2";
        final ColumnInfoDataType[] columnInfoData = { columnInfoDataType1, columnInfoDataType2 };
        return columnInfoData;
    }

    private class BaseToolBarStub extends BaseToolBar {

        public BaseToolBarStub(final MultipleInstanceWinId multiWinId, final EventBus eventBus) {
            super(multiWinId, eventBus);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Button getItemByItemId(final String itemId) {
            return mockedButton;
        }

    }

    private class RButtonEnableConditionsStub extends RecurErrorButtonEnableConditions {

        private final boolean isColumnHeaderSetSame;

        public RButtonEnableConditionsStub(final boolean isColumnHeaderSetSame) {
            this.isColumnHeaderSetSame = isColumnHeaderSetSame;
        }

        @Override
        Set<String> getRecurErrHeaders() {

            final Set<String> columns = new HashSet<String>();

            if (isColumnHeaderSetSame) {
                columns.add("DummyHeader1");
                columns.add("DummyHeader2");

            } else {
                columns.add("RecureHeader1");
                columns.add("RecureHeader2");

            }
            return columns;
        }
    }

}
