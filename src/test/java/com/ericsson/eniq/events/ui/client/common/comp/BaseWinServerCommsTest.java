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
package com.ericsson.eniq.events.ui.client.common.comp;

import static junit.framework.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jmock.Expectations;
import org.junit.*;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.widget.AbstractBaseWindowDisplay;
import com.ericsson.eniq.events.ui.client.datatype.*;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.web.bindery.event.shared.EventBus;

public class BaseWinServerCommsTest extends TestEniqEventsUI {

    private static final String TIMEZONE_OFFSET = new SimpleDateFormat("Z").format(new Date());

    private StubBaseWinServerComms objectUnderTest;

    RequestBuilder mockedRequestBuilder;

    AbstractBaseWindowDisplay mockedDisplay;

    Request mockedRequest;

    BaseWindowPresenter mockedBaseWindowPresenter;

    BreadCrumbMenuItem mockedBreadCrumbMenuItem;

    MenuTaskBar mockedMenuTaskBar;

    final String expectedInternal = "?time=30&method=toolong&key=ERR&imsi=12121212121&display=chart&whatever=something&type=IMSI&tzOffset="
            + new SimpleDateFormat("Z").format(new Date()) + "&maxRow=500";

    final SearchFieldDataType expectedSearchDataType = new SearchFieldDataType("12121212121", new String[] { ("imsi=12121212121"), "type=IMSI" },
            "IMSI", null, false, "CS,PS", null, false);

    @Before
    public void setUp() {
        mockedDisplay = context.mock(AbstractBaseWindowDisplay.class);
        mockedRequestBuilder = context.mock(RequestBuilder.class);
        mockedRequest = context.mock(Request.class);
        mockedBaseWindowPresenter = context.mock(BaseWindowPresenter.class);
        mockedBreadCrumbMenuItem = context.mock(BreadCrumbMenuItem.class);
        mockedMenuTaskBar = context.mock(MenuTaskBar.class);

        objectUnderTest = new StubBaseWinServerComms(mockedBaseWindowPresenter, mockedEventBus, mockedDisplay, null);
    }

    @After
    public void tearDown() {
        objectUnderTest = null;
    }

    @Test
    public void getInternalRequestData() {

        setupExpectationsForGetInternalRequestData();
        final String actual = objectUnderTest.getInternalRequestData();

        assertEquals("Internal data a s expected ", expectedInternal, actual);
    }

    @Test
    public void makeServerCallWithURLParamsWithNoWidgetParams() {

        context.checking(new Expectations() {
            {
                allowing(mockedBaseWindowPresenter).getCurrentBreadCrumbMenuItem();
                will(returnValue(mockedBreadCrumbMenuItem));

                allowing(mockedBreadCrumbMenuItem).getWidgetURLParameters();
                will(returnValue(null));

                allowing(mockedDisplay).getWidgetSpecificURLParams();
                will(returnValue(null));

                allowing(mockedBaseWindowPresenter).cleanUpBreadCrumbMenu();

                allowing(mockedBreadCrumbMenuItem).setWidgetURLParameters(with(any(String.class)), with(any(SearchFieldDataType.class)));
                allowing(mockedDisplay).updateWidgetSpecificURLParams(expectedInternal);

                allowing(mockedBaseWindowPresenter).getWsURL();

                one(mockedBaseWindowPresenter).getMultipleInstanceWinId();
            }
        });
        setupExpectationsForGetInternalRequestData();

        objectUnderTest.makeServerCallWithURLParams();

        assertEquals("None widget data as expected ", expectedInternal, (objectUnderTest).requestData);

    }

    @Test
    public void makeServerCallWithReuseParams() {

        final String widgetParams = "?time=30&whatever=something";
        final String expectedwidgetParams = "?time=30&tzOffset=" + TIMEZONE_OFFSET + "&maxRow=500&whatever=something";

        context.checking(new Expectations() {
            {
                allowing(mockedBaseWindowPresenter).getCurrentBreadCrumbMenuItem();
                will(returnValue(mockedBreadCrumbMenuItem));

                allowing(mockedBreadCrumbMenuItem).getWidgetURLParameters();
                will(returnValue(null));

                allowing(mockedDisplay).getWidgetSpecificURLParams();
                will(returnValue(widgetParams));

                allowing(mockedBaseWindowPresenter).cleanUpBreadCrumbMenu();

                allowing(mockedBreadCrumbMenuItem).setWidgetURLParameters(with(any(String.class)), with(any(SearchFieldDataType.class)));
                allowing(mockedDisplay).updateWidgetSpecificURLParams(widgetParams);

                allowing(mockedDisplay).getWorkspaceController();
                will(returnValue(mockedMenuTaskBar));

                allowing(mockedMenuTaskBar).getTabOwnerId();
                will(returnValue("NETWORK_TAB"));

                allowing(mockedBaseWindowPresenter).getQueryId();
                allowing(mockedBaseWindowPresenter).getWsURL();

                one(mockedBaseWindowPresenter).getMultipleInstanceWinId();

                one(mockedBaseWindowPresenter).getDataTieredDelayURLParameter();

            }
        });
        setupExpectationsForGetResuseableData();

        objectUnderTest.makeServerCallWithURLParams();

        assertEquals("Widget data as expected ", expectedwidgetParams, (objectUnderTest).requestData);

    }

    private void setupExpectationsForGetResuseableData() {

        final String widgetParams = "?time=30&tzOffset=" + TIMEZONE_OFFSET + "&maxRow=500&whatever=something";

        context.checking(new Expectations() {
            {
                allowing(mockedBaseWindowPresenter).getTimeData();
                will(returnValue(TimeInfoDataType.DEFAULT));

                allowing(mockedBaseWindowPresenter).isSearchFieldDataRequired();
                will(returnValue(true));

                allowing(mockedBaseWindowPresenter).getSearchData();
                will(returnValue(getDummySearchData()));

                allowing(mockedBreadCrumbMenuItem).setWidgetURLParameters(with(any(String.class)), with(any(SearchFieldDataType.class)));

                allowing(mockedDisplay).updateWidgetSpecificURLParams(widgetParams);

                one(mockedBaseWindowPresenter).getMaxRowsURLParameter();
                will(returnValue("&maxRow=500"));

            }
        });
    }

    private void setupExpectationsForGetInternalRequestData() {
        context.checking(new Expectations() {
            {
                allowing(mockedBaseWindowPresenter).getTimeData();
                will(returnValue(TimeInfoDataType.DEFAULT));

                allowing(mockedBaseWindowPresenter).getIsDrillDown();
                will(returnValue(false));

                allowing(mockedBaseWindowPresenter).isSearchFieldDataRequired();
                will(returnValue(true));

                allowing(mockedBaseWindowPresenter).getSearchData();
                will(returnValue(getDummySearchData()));

                allowing(mockedBaseWindowPresenter).getOutBoundDisplayTypeParameter();
                will(returnValue("chart"));

                allowing(mockedBaseWindowPresenter).isDrilledDownScreen();
                will(returnValue(false));

                one(mockedBaseWindowPresenter).getViewType();
                will(returnValue("ERR"));

                one(mockedBaseWindowPresenter).getWidgetSpecificURLParams();
                will(returnValue("&whatever=something&method=toolong"));

                one(mockedBaseWindowPresenter).getMaxRowsURLParameter();
                will(returnValue("&maxRow=500"));

                one(mockedBaseWindowPresenter).getDataTieredDelayURLParameter();

            }
        });
    }

    SearchFieldDataType getDummySearchData() {
        return new SearchFieldDataType("12121212121", new String[] { "imsi=12121212121", "type=IMSI" }, null, null, false, "", null, false);
    }

    private class StubBaseWinServerComms extends BaseWinServerComms {

        String requestData = null;

        public StubBaseWinServerComms(final BaseWindowPresenter baseWindowPresenter, final EventBus eventBus, final IBaseWindowView display,
                                      final BaseWinSearchFieldValueResetHandler searchFieldResetHandler) {
            super(baseWindowPresenter, eventBus, display, searchFieldResetHandler);
        }

        @Override
        public void makeServerRequest(final MultipleInstanceWinId multiWinId, final String wsURL, final String requestData) {

            this.requestData = requestData;

        }

    }
}