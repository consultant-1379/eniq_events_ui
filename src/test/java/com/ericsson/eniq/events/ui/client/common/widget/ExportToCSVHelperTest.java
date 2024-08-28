/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.common.widget;

import static junit.framework.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jmock.Expectations;
import org.junit.*;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.BreadCrumbMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

public class ExportToCSVHelperTest extends TestEniqEventsUI {

    private static final String TIMEZONE_OFFSET = new SimpleDateFormat("Z").format(new Date());

    ExportToCSVHelperStub objToTest;

    IEventGridView mockedEventGridView;

    Frame mockedFrame;

    IBaseWindowPresenter mockedPresenter;

    BreadCrumbMenuItem mockedBreadCrumbMenuItem;

    RootPanel mockedRootPanel;

    MetaMenuItem mockedMetaMenuItem;

    @Before
    public void setUp() {
        mockedEventGridView = context.mock(EventGridView.class);
        mockedFrame = context.mock(Frame.class);

        mockedPresenter = context.mock(IBaseWindowPresenter.class);
        mockedBreadCrumbMenuItem = context.mock(BreadCrumbMenuItem.class);
        mockedRootPanel = context.mock(RootPanel.class);
        mockedMetaMenuItem = context.mock(MetaMenuItem.class);

        objToTest = getObjectToTest();
    }

    @After
    public void tearDown() {
        objToTest = null;
    }

    @Test
    public void removeRedundantParamsFromURLContainingTime() throws Exception {

        // can not really test from and to for relative time
        final String before = "?time=30&type=APN&node=blackberry.net&display=grid&key=SUM&tzOffset=+0000&maxRows=550";

        final String expectedStartAfter = "?type=APN&node=blackberry.net&display=grid&key=SUM"; // &dataTimeFrom=1297948620000&dataTimeTo=1297950420000&maxRows=0&tzOffset=+0000";    

        final String afterCall = objToTest.removeRedundantParamsFromURL(before);

        assertEquals(afterCall + " is the same starts as expected", true, afterCall.startsWith(expectedStartAfter));

    }

    @Test
    public void removeRedundantParamsFromURLContainingDates() throws Exception {

        // can not really test from and to for relative time
        final String before = "?dateFrom=15022011&type=APN&node=blackberry.net&display=grid&key=SUM&tzOffset=+0000&maxRows=550&dateTo=17022011&timeFrom=0015&timeTo=0015";

        final String expectedStartAfter = "?type=APN&node=blackberry.net&display=grid&key=SUM&tzOffset=+0000&maxRows=550";

        final String afterCall = objToTest.removeRedundantParamsFromURL(before);

        assertEquals("widget parameters formatted as expected", expectedStartAfter, afterCall);

    }

    @Test
    public void exportCSVFromBreadCrumb() throws Exception {

        final String widgetParams = "?time=30&type=APN&node=blackberry.net&display=grid&key=SUM&tzOffset=" + TIMEZONE_OFFSET + "&maxRows=550";
        final String url = "NETWORK/EVENT_ANALYSIS";
        initialiseExpectationsOnGWTComponents();

        context.checking(new Expectations() {
            {

                one(mockedPresenter).getCurrentBreadCrumbMenuItem();
                will(returnValue(mockedBreadCrumbMenuItem));

                one(mockedBreadCrumbMenuItem).getWidgetURLParameters();
                will(returnValue(widgetParams));

            }
        });

        setExpectaionsForTraversedViews(url);

        objToTest.exportDataToCSV(mockedEventGridView);
    }

    @Test
    public void exportCSVWhenBreadCrumbHasNoURLInfo() throws Exception {

        final String widgetParams = "?time=30&type=APN&node=blackberry.net&display=grid&key=SUM&tzOffset=" + TIMEZONE_OFFSET + "&maxRows=550";
        final String url = "NETWORK/EVENT_ANALYSIS";
        initialiseExpectationsOnGWTComponents();

        context.checking(new Expectations() {
            {

                one(mockedPresenter).getCurrentBreadCrumbMenuItem();
                will(returnValue(mockedBreadCrumbMenuItem));

                one(mockedBreadCrumbMenuItem).getWidgetURLParameters();
                will(returnValue(""));

                one(mockedEventGridView).getWidgetSpecificURLParamsForCSV();
                will(returnValue(widgetParams));

            }
        });

        setExpectaionsForTraversedViews(url);

        objToTest.exportDataToCSV(mockedEventGridView);
    }

    @Test
    public void exportCSVWhenViewRefHasNoURLInfo() throws Exception {

        final String searchParams = "&type=APN&node=blackberry.net&key=SUM";
        final String url = "NETWORK/EVENT_ANALYSIS";
        initialiseExpectationsOnGWTComponents();

        context.checking(new Expectations() {
            {

                one(mockedPresenter).getCurrentBreadCrumbMenuItem();
                will(returnValue(mockedBreadCrumbMenuItem));

                one(mockedBreadCrumbMenuItem).getWidgetURLParameters();
                will(returnValue(""));

                one(mockedEventGridView).getWidgetSpecificURLParamsForCSV();
                will(returnValue(""));

                one(mockedPresenter).getSearchURLParameters();
                will(returnValue(searchParams));

            }
        });

        setExpectaionsForTraversedViews(url);

        objToTest.exportDataToCSV(mockedEventGridView);
    }

    @Test
    public void exportCSVFromRegularView() throws Exception {

        final String widgetParams = "?time=30&type=APN&maxRows=999&node=blackberry.net&display=grid&key=SUM";
        final String url = "NETWORK/EVENT_ANALYSIS";
        initialiseExpectationsOnGWTComponents();

        context.checking(new Expectations() {
            {

                one(mockedPresenter).getCurrentBreadCrumbMenuItem();
                will(returnValue(null));

                one(mockedEventGridView).getWidgetSpecificURLParamsForCSV();
                will(returnValue(widgetParams));

            }
        });

        setExpectationsForRegularViews(url);

        objToTest.exportDataToCSV(mockedEventGridView);
    }

    @Test
    public void exportCSVFromRegularViewWhenBreadCrumbHasNoURLInfo() throws Exception {

        final String widgetParams = "?time=30&type=APN&maxRows=999&node=blackberry.net&display=grid&key=SUM";
        final String url = "NETWORK/EVENT_ANALYSIS";
        initialiseExpectationsOnGWTComponents();

        context.checking(new Expectations() {
            {

                one(mockedPresenter).getCurrentBreadCrumbMenuItem();
                will(returnValue(null));

                one(mockedEventGridView).getWidgetSpecificURLParamsForCSV();
                will(returnValue(""));

                one(mockedPresenter).getSearchURLParameters();
                will(returnValue(widgetParams));

            }
        });

        setExpectationsForRegularViews(url);

        objToTest.exportDataToCSV(mockedEventGridView);
    }

    @Test
    public void exportCSVFromRegularViewWhenViewRefHasNoURLInfo() throws Exception {

        final String searchParams = "&type=APN&node=blackberry.net&key=SUM";
        final String url = "NETWORK/EVENT_ANALYSIS";
        initialiseExpectationsOnGWTComponents();

        context.checking(new Expectations() {
            {

                one(mockedPresenter).getCurrentBreadCrumbMenuItem();
                will(returnValue(null));

                one(mockedEventGridView).getWidgetSpecificURLParamsForCSV();
                will(returnValue(""));

                one(mockedPresenter).getSearchURLParameters();
                will(returnValue(searchParams));

            }
        });

        setExpectationsForRegularViews(url);

        objToTest.exportDataToCSV(mockedEventGridView);
    }

    private TimeInfoDataType createTimeData() {
        TimeInfoDataType timeData = new TimeInfoDataType();

        timeData.timeTo = new Time(10, 15);
        timeData.timeFrom = new Time(10, 0);
        timeData.dateFrom = new Date();
        timeData.dateTo = new Date();
        timeData.dataTimeFrom = "1399971600000";
        timeData.dataTimeTo = "1399972500000";

        return timeData;
    }

    private ExportToCSVHelperStub getObjectToTest() {

        return new ExportToCSVHelperStub();
    }

    private void initialiseExpectationsOnGWTComponents() {
        context.checking(new Expectations() {
            {
                one(mockedFrame).setVisible(false);
                allowing(mockedEventGridView).getPresenter();
                will(returnValue(mockedPresenter));
            }
        });
    }

    private void setExpectationsForRegularViews(final String url) {
        context.checking(new Expectations() {
            {

                one(mockedPresenter).getWindowTimeDate();
                will(returnValue(createTimeData()));

                one(mockedEventGridView).getViewSettings();
                will(returnValue(mockedMetaMenuItem));

                one(mockedMetaMenuItem).getWsURL();
                will(returnValue(url));

                one(mockedFrame).setUrl(
                        "myhost:EniqEventsCSV.jsp?dataTimeFrom=1399971600000&node=blackberry.net&tzOffset=" + TIMEZONE_OFFSET
                                + "&maxRows=0&userName=dummyUser&dataTimeTo=1399972500000&display=grid&type=APN&url=NETWORK/EVENT_ANALYSIS&key=SUM");

                one(mockedRootPanel).add(mockedFrame);
            }
        });
    }

    private void setExpectaionsForTraversedViews(final String url) {
        context.checking(new Expectations() {
            {
                one(mockedBreadCrumbMenuItem).getTimeData();
                will(returnValue(TimeInfoDataType.DEFAULT));

                one(mockedBreadCrumbMenuItem).getURL();
                will(returnValue(url));

                one(mockedFrame).setUrl(
                        "myhost:EniqEventsCSV.jsp?dataTimeFrom=&node=blackberry.net&tzOffset=" + TIMEZONE_OFFSET
                                + "&maxRows=0&userName=dummyUser&dataTimeTo=&display=grid&type=APN&url=NETWORK/EVENT_ANALYSIS&key=SUM");

                one(mockedRootPanel).add(mockedFrame);
            }
        });
    }

    class ExportToCSVHelperStub extends ExportToCSVHelper {
        @Override
        public Frame getCSVFrame() {
            return mockedFrame;
        }

        @Override
        public String getEniqEventsServicesURI() {
            return "http://atc3000bl3.athtem.eei.ericsson.se:18080/EniqEventsServices/";
        }

        @Override
        String getHostPageBaseURL() {
            return "myhost:";
        }

        @Override
        String encode(final String s) {
            return s;
        }

        @Override
        RootPanel getRootPanel() {
            return mockedRootPanel;
        }

        @Override
        String getLoginUserName() {
            return "dummyUser";
        }
    }

}
