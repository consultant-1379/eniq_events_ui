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
package com.ericsson.eniq.events.ui.client.events.handlers;

import static junit.framework.Assert.*;

import com.extjs.gxt.ui.client.widget.form.Time;
import org.jmock.Expectations;
import org.junit.*;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.charts.window.ChartWindowView;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.common.comp.BreadCrumbMenuItem;
import com.ericsson.eniq.events.ui.client.common.widget.EventGridView;
import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType.Type;
import com.ericsson.eniq.events.ui.client.datatype.*;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.google.web.bindery.event.shared.EventBus;

import java.util.Date;

/**
 * @author eeicmsy
 * 
 */
public class ChartGridChangeEventHandlerTest extends TestEniqEventsUI {

    BaseWindowPresenter mockedBaseWindowPresenter;

    BreadCrumbMenuItem mockedBreadCrumbMenuItem;

    IExtendedWidgetDisplay mockedIExtendedWidgetDisplay;

    EventGridView mockedEventGridView;

    ChartWindowView mockedChartView;

    ChartGridChangeEventHandler objUnderTest;

    @Before
    public void setUp() {
        mockedBaseWindowPresenter = context.mock(BaseWindowPresenter.class);
        mockedBreadCrumbMenuItem = context.mock(BreadCrumbMenuItem.class);
        mockedIExtendedWidgetDisplay = context.mock(IExtendedWidgetDisplay.class);
        mockedEventGridView = context.mock(EventGridView.class);
        mockedChartView = context.mock(ChartWindowView.class);
        objUnderTest = new StubbedChartGridChangeEventHandler(mockedEventBus, "tabId", mockedBaseWindowPresenter);
    }

    @After
    public void tearDown() {
        objUnderTest = null;
    }

    @Test
    public void handleChangeGridChartWithDifferentWinIDDoesNothing() {

        context.checking(new Expectations() {
            {
                allowing(mockedBaseWindowPresenter).getMultipleInstanceWinId();
                will(returnValue(createMultipleInstanceWinId("not some id")));
                allowing(mockedBaseWindowPresenter).getSearchData();
                allowing(mockedBaseWindowPresenter).isSearchFieldDataRequired();

            }
        });
        objUnderTest.handleChangeGridChart(createMultipleInstanceWinId("some id"), EventType.ROAMING_BY_COUNTRY, new ToolBarURLChangeDataType(), "",
                "");

    }

    @Test
    public void handleChangingToSameTypeOfChartRefreshesWithNewWinId() {

        final String winId = "same";

        context.checking(new Expectations() {
            {

                one(mockedBaseWindowPresenter).setTempTimeData(null);
                allowing(mockedBaseWindowPresenter).getSearchURLParameters();
                will(returnValue("&imsi=12345"));

                allowing(mockedBaseWindowPresenter).getDisplayType();
                will(returnValue("barOneline"));
                one(mockedBaseWindowPresenter).setFixedQueryId("ROAMING_BY_COUNTRY");
                one(mockedBaseWindowPresenter).setWsURL("sameChartTypeURL");
                one(mockedBaseWindowPresenter).setMaxRowsParam("MAXROWSPARAM");
                allowing(mockedBaseWindowPresenter).getView();
                will(returnValue(mockedIExtendedWidgetDisplay));

                one(mockedBaseWindowPresenter).setIsDrillDown(false);

                one(mockedBaseWindowPresenter).getWindowTimeDate();
                will(returnValue(createTimeData()));

                one(mockedBaseWindowPresenter).initializeWidgit("ROAMING_BY_COUNTRY");
                one(mockedBaseWindowPresenter).handleWindowRefresh();

                allowing(mockedBaseWindowPresenter).getMultipleInstanceWinId();
                will(returnValue(createMultipleInstanceWinId(winId)));

                one(mockedIExtendedWidgetDisplay).updateWidgetSpecificURLParams(with(any(String.class)));

            }
        });
        objUnderTest.handleChangeGridChart(createMultipleInstanceWinId(winId), EventType.ROAMING_BY_COUNTRY, getDummyBarOneLineChartType(), "", "");

    }

    @Test
    public void handleChangingToChartWhenFormallyAGrid() {

        final String winId = "same";

        context.checking(new Expectations() {
            {

                allowing(mockedBaseWindowPresenter).getSearchURLParameters();
                will(returnValue("&imsi=12345"));
                //
                allowing(mockedBaseWindowPresenter).getMultipleInstanceWinId();
                will(returnValue(createMultipleInstanceWinId(winId)));
                //
                allowing(mockedBaseWindowPresenter).getDisplayType();
                will(returnValue("barOneline"));
                allowing(mockedBaseWindowPresenter).getView();
                will(returnValue(mockedEventGridView));
                one(mockedBaseWindowPresenter).clearCurrentBreadCrumbMenuItem();

                one(mockedBaseWindowPresenter).setTempTimeData(null);

                one(mockedBaseWindowPresenter).setIsDrillDown(false);

                one(mockedBaseWindowPresenter).getWindowTimeDate();
                will(returnValue(createTimeData()));

                one(mockedBaseWindowPresenter).setFixedQueryId("ROAMING_BY_COUNTRY");
                one(mockedBaseWindowPresenter).setWsURL("sameChartTypeURL");
                one(mockedBaseWindowPresenter).setMaxRowsParam("MAXROWSPARAM");

                allowing(mockedEventGridView).updateWidgetSpecificURLParams(with(any(String.class)));

                allowing(mockedBaseWindowPresenter).isSearchFieldDataRequired();
                will(returnValue(true));
                one(mockedBaseWindowPresenter).initializeWidgit("ROAMING_BY_COUNTRY");
                one(mockedBaseWindowPresenter).handleWindowRefresh();

                one(mockedEventGridView).getColumns();

            }
        });
        objUnderTest.handleChangeGridChart(createMultipleInstanceWinId(winId), EventType.ROAMING_BY_COUNTRY, getDummyBarOneLineChartType(), "", "");

        assertEquals(" registered to update when server returns", true,
                ((StubbedChartGridChangeEventHandler) objUnderTest).registeredForSuccessReturn);
        assertEquals(" set up to change from grid (to chart) after server returns", true, objUnderTest.wasFormallyAGrid);
    }

    @Test
    public void handleChangingToGridFromChart() {

        final String winId = "same";

        context.checking(new Expectations() {
            {

                allowing(mockedBaseWindowPresenter).getSearchURLParameters();
                will(returnValue("&imsi=12345"));

                allowing(mockedBaseWindowPresenter).getMultipleInstanceWinId();
                will(returnValue(createMultipleInstanceWinId(winId)));

                one(mockedBaseWindowPresenter).setResponseObj(null);
                one(mockedBaseWindowPresenter).setWindowType(Type.GRID);
                one(mockedBaseWindowPresenter).setFixedQueryId("ROAMING_BY_COUNTRY");
                allowing(mockedBaseWindowPresenter).getDisplayType();
                will(returnValue("barOneline"));
                one(mockedBaseWindowPresenter).setWsURL("gridURL");
                allowing(mockedBaseWindowPresenter).getView();
                will(returnValue(mockedChartView));
                one(mockedChartView).changeToggleWindowLauncher(with(any(EventBus.class)), with(any(ToolBarURLChangeDataType.class)));
                one(mockedBaseWindowPresenter).clearCurrentBreadCrumbMenuItem();
                allowing(mockedBaseWindowPresenter).getCurrentBreadCrumbMenuItem();
                will(returnValue(null));

                allowing(mockedBaseWindowPresenter).setCurrentToolBarType("");

                one(mockedBaseWindowPresenter).setIsDrillDown(false);

                one(mockedBaseWindowPresenter).getWindowTimeDate();
                will(returnValue(createTimeData()));


                allowing(mockedBaseWindowPresenter).handleGraphToGridToggle("", " : ");
                allowing(mockedBaseWindowPresenter).initializeToolbar(true);
            }
        });
        objUnderTest.handleChangeGridChart(createMultipleInstanceWinId(winId), EventType.ROAMING_BY_COUNTRY, getDummyGridType(), "", "");

    }

    @Test
    public void handleChangingToGridFromAGrid() {

        final String winId = "same";

        context.checking(new Expectations() {
            {
                allowing(mockedBaseWindowPresenter).getSearchURLParameters();
                will(returnValue("&imsi=12345"));
                allowing(mockedBaseWindowPresenter).getMultipleInstanceWinId();
                will(returnValue(createMultipleInstanceWinId(winId)));

                one(mockedBaseWindowPresenter).setResponseObj(null);
                one(mockedBaseWindowPresenter).setWindowType(Type.GRID);
                allowing(mockedBaseWindowPresenter).setFixedQueryId("TERMINAL_MOST_MOBILITY_ISSUES");
                allowing(mockedBaseWindowPresenter).setWsURL("gridURL"); // ok so sets twice - no biggy
                one(mockedBaseWindowPresenter).setMaxRowsParam("MAXROWSPARAM");
                allowing(mockedBaseWindowPresenter).getView();
                will(returnValue(mockedEventGridView));
                one(mockedEventGridView).updateWidgetSpecificURLParams(with(any(String.class)));
                one(mockedBaseWindowPresenter).initializeWidgit("TERMINAL_MOST_MOBILITY_ISSUES");
                one(mockedBaseWindowPresenter).handleWindowRefresh();
                allowing(mockedBaseWindowPresenter).setCurrentToolBarType("");
                one(mockedBaseWindowPresenter).clearCurrentBreadCrumbMenuItem();
                allowing(mockedBaseWindowPresenter).getCurrentBreadCrumbMenuItem();
                will(returnValue(mockedBreadCrumbMenuItem));
                one(mockedBreadCrumbMenuItem).setURL("gridURL");

                one(mockedBaseWindowPresenter).setIsDrillDown(false);

                one(mockedBaseWindowPresenter).getWindowTimeDate();
                will(returnValue(createTimeData()));


                allowing(mockedBaseWindowPresenter).initializeToolbar(true);

            }
        });
        objUnderTest.handleChangeGridChart(createMultipleInstanceWinId(winId), EventType.TERMINAL_MOST_MOBILITY_ISSUES, getDummyGridType(), "", "");

    }

    private TimeInfoDataType createTimeData(){
        TimeInfoDataType timeData = new TimeInfoDataType();

        timeData.timeTo = new Time(10,15);
        timeData.timeFrom = new Time(10,0);
        timeData.dateFrom = new Date();
        timeData.dateTo = new Date();
        timeData.dataTimeFrom = "1399971600000";
        timeData.dataTimeTo = "1399972500000";

        return timeData;
    }


    private ToolBarURLChangeDataType getDummyGridType() {
        final ToolBarURLChangeDataType rv = new ToolBarURLChangeDataType();
        rv.displayType = "grid";
        rv.windowType = "GRID";
        rv.url = "gridURL";
        rv.maxRowsParam = "MAXROWSPARAM";
        return rv;
    }

    private ToolBarURLChangeDataType getDummyBarOneLineChartType() {
        final ToolBarURLChangeDataType rv = new ToolBarURLChangeDataType();
        rv.displayType = "barOneline";
        rv.windowType = "CHART";
        rv.url = "sameChartTypeURL";
        rv.maxRowsParam = "MAXROWSPARAM";
        return rv;

    }

    final MultipleInstanceWinId createMultipleInstanceWinId(final String winID) {
        return new MultipleInstanceWinId("tabId", winID);
    }

    private class StubbedChartGridChangeEventHandler extends ChartGridChangeEventHandler {

        public StubbedChartGridChangeEventHandler(final EventBus eventBus, final String tabId, final BaseWindowPresenter winPresenter) {
            super(eventBus, winPresenter);
        }

        boolean registeredForSuccessReturn;

        @Override
        public void registerHandlerForSuccessServerResult() {
            registeredForSuccessReturn = true;
        }

    }
}
