/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.ServerComms;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldUser;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.StubbedPortletDataType;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletRefreshEvent;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;
import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author eeicmsy
 * @since 2011
 *
 */
public class PortletWindowTest extends TestEniqEventsUI {

    private PortletWindow objToTest;

    private Label mockedTitleLabel; //  @UiField focing this ?

    private SimplePanel mockedSimplePanel;

    private Image mockedCloseButton;

    private ServerComms mockedServerComms;

    private final String serverName = "serverName";

    @Before
    public void setUp() {
        mockedTitleLabel = context.mock(Label.class);
        mockedSimplePanel = context.mock(SimplePanel.class);
        mockedCloseButton = context.mock(Image.class);
        mockedServerComms = context.mock(ServerComms.class);

    }

    @After
    public void tearDown() {
        objToTest = null;
    }

    @Test
    public void porletWindowCanSetPathModeOnSearchData() throws Exception {
        setupExpectationsForConstruction();
        PortletDataType data = new StubbedPortletDataType.Builder().isSearchFieldUser(SearchFieldUser.PATH).build();
        final SearchFieldDataType srchData = getAPNSearchData();

        objToTest = getPortletWindow(data);
        objToTest.refreshSearchData(srchData);

        // porlet window takes a copy
        assertEquals("Search data has been changed to path mode", true, objToTest.getCurrentSearchData().isPathMode());

        data = new StubbedPortletDataType.Builder().isSearchFieldUser(SearchFieldUser.TRUE).build(); // not path both
        objToTest = getPortletWindow(data);
        objToTest.refreshSearchData(srchData);

        assertEquals("Search data has NOT been changed to path mode", false, objToTest.getCurrentSearchData()
                .isPathMode());

    }

    @Test
    public void refreshTimeDataChangesDateFrom() throws Exception {

        setupExpectationsForConstruction();

        final int NUM_DAYS_BACK_MINS = 2880; // two days

        final long NUM_DAYS_BACK_MINS_MS = NUM_DAYS_BACK_MINS * 60L * 1000L;

        final PortletDataType data = new StubbedPortletDataType.Builder().commaSeperatedDateFrom(
                "*," + NUM_DAYS_BACK_MINS).wsURL("DASHBOARD/ROAMER").build();

        objToTest = getPortletWindow(data);

        final TimeInfoDataType timeData = getInitial_18Oct2011_Date();

        final String expectPathDrivenArgs = "?dateFrom=16102011&dateTo=18102011&timeFrom=0200&timeTo=0200"
                + CommonParamUtil.getTimeZoneURLParameter() + "&display=chart&maxRows=500";
        final String expectURL = serverName + "DASHBOARD/ROAMER";

        context.checking(new Expectations() {
            {
                one(mockedSimplePanel).isVisible();
                one(mockedServerComms).makeServerRequest(objToTest.getMultipleInstanceWinId(), expectURL,
                        expectPathDrivenArgs);

            }
        });

        objToTest.refreshTimeData(timeData);

        final long actual = (timeData.dateTo.getTime() - timeData.dateFrom.getTime());
        assertEquals("dateFrom from has been moved back " + NUM_DAYS_BACK_MINS_MS + " ms for call",
                NUM_DAYS_BACK_MINS_MS, actual);
    }

    @Test
    public void directCalltoUpDateSearchDataNeedsTimeData() throws Exception {

        setupExpectationsForConstruction();

        final PortletDataType data = new StubbedPortletDataType.Builder().isSearchFieldUser(SearchFieldUser.TRUE)
                .build();

        objToTest = getPortletWindow(data);
        final SearchFieldDataType srchData = getAPNSearchData();

        // no call made
        objToTest.refreshSearchData(srchData);
    }

    @Test
    public void refreshWithPathTypeSearchDataAppendsNodeTypeToURL() throws Exception {

        setupExpectationsForConstruction();

        final int TIME_BACK_FOR_APN_MINS = 10080; // week

        final long TIME_BACK_FOR_APN_MS = TIME_BACK_FOR_APN_MINS * 60L * 1000L;

        final PortletDataType data = new StubbedPortletDataType.Builder().isSearchFieldUser(SearchFieldUser.PATH)
                .commaSeperatedDateFrom("*,2,APN," + TIME_BACK_FOR_APN_MINS).wsURL("DASHBOARD/ROAMER").build();

        final SearchFieldDataType srchData = getAPNSearchData();
        final TimeInfoDataType timeData = getInitial_18Oct2011_Date();

        objToTest = getPortletWindow(data);

        final String expectPathDrivenArgs = "?dateFrom=11102011&dateTo=18102011&timeFrom=0200&timeTo=0200&searchParam=blackberry.net"
                + CommonParamUtil.getTimeZoneURLParameter() + "&display=chart&maxRows=500";
        final String expectURL = serverName + "DASHBOARD/ROAMER" + "/APN";

        context.checking(new Expectations() {
            {
                one(mockedSimplePanel).isVisible();
                one(mockedServerComms).makeServerRequest(objToTest.getMultipleInstanceWinId(), expectURL,
                        expectPathDrivenArgs);

            }
        });

        objToTest.refresh(srchData, timeData);

        final long actual = (timeData.dateTo.getTime() - timeData.dateFrom.getTime());
        assertEquals("dateFrom for APN specifically has been moved back " + TIME_BACK_FOR_APN_MS + " ms for call",
                TIME_BACK_FOR_APN_MS, actual);

    }

    @Test
    public void refreshWithRegularTypeSearchDataHasRegularURL() throws Exception {

        setupExpectationsForConstruction();

        final int TIME_BACK_FOR_APN_MINS = 10080; // week

        final long TIME_BACK_FOR_APN_MS = TIME_BACK_FOR_APN_MINS * 60L * 1000L;

        final PortletDataType data = new StubbedPortletDataType.Builder().isSearchFieldUser(SearchFieldUser.TRUE)
                .commaSeperatedDateFrom("*,2,APN," + TIME_BACK_FOR_APN_MINS).wsURL("DASHBOARD/ROAMER").build();

        final SearchFieldDataType srchData = getAPNSearchData();
        final TimeInfoDataType timeData = getInitial_18Oct2011_Date();

        objToTest = getPortletWindow(data);

        final String expectPathDrivenArgs = "?dateFrom=11102011&dateTo=18102011&timeFrom=0200&timeTo=0200&node=blackberry.net&type=APN"
                + CommonParamUtil.getTimeZoneURLParameter() + "&display=chart&maxRows=500";
        final String expectURL = serverName + "DASHBOARD/ROAMER";

        context.checking(new Expectations() {
            {
                one(mockedSimplePanel).isVisible();
                one(mockedServerComms).makeServerRequest(objToTest.getMultipleInstanceWinId(), expectURL,
                        expectPathDrivenArgs);

            }
        });

        objToTest.refresh(srchData, timeData);

        final long actual = (timeData.dateTo.getTime() - timeData.dateFrom.getTime());
        assertEquals("dateFrom for APN specifically has been moved back " + TIME_BACK_FOR_APN_MS + " ms for call",
                TIME_BACK_FOR_APN_MS, actual);

    }

    @Test
    public void isSamePortletDoesNotCareAboutMultiInstances() throws Exception {
        setupExpectationsForConstruction();
        final PortletDataType data = new StubbedPortletDataType.Builder().build();
        objToTest = getPortletWindow(data);

        final PortletWindow objToCompare = getPortletWindow(data);
        assertEquals("Condider these to be the same window  ", true, objToTest.isSamePorlet(objToCompare
                .getMultipleInstanceWinId()));

    }

    //////////////////////////////////////////////  
    ////////////   End of Tests
    //////////////////////////////////////////////  

    private SearchFieldDataType getAPNSearchData() {

        final String searchFieldVal = "blackberry.net";
        final String[] urlParams = new String[] { "node=blackberry.net", "type=APN" };
        final String type = "APN";
        final boolean isGroupMode = false;
        final String splitStringMetaDataKeys = "CS,PS";

        return new SearchFieldDataType(searchFieldVal, urlParams, type, null, isGroupMode, splitStringMetaDataKeys, null, false);
    }

    private TimeInfoDataType getInitial_18Oct2011_Date() {
        final TimeInfoDataType timeData = new TimeInfoDataType();
        timeData.dateTo = roundToLastMidNite(new Date(1318961787810L)); // 18 Oct 2011

        timeData.timeFrom = new Time(2, 0);
        timeData.timeTo = new Time(2, 0);
        timeData.dateFrom = timeData.dateTo; // test it changes
        return timeData;
    }

    private final Date roundToLastMidNite(final Date date) {
        /* not importing java.util.Calendar project */
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        return date;
    }

    private PortletWindow getPortletWindow(final PortletDataType data) {
        // TODO Auto-generated method stub
        return new StubbedPortletWindow(data, mockedEventBus);
    }

    private void setupExpectationsForConstruction() {
        context.checking(new Expectations() {
            {
                allowing(mockedTitleLabel).setText("testPorlet");

                allowing(mockedSimplePanel).setHeight("-29px");
                allowing(mockedSimplePanel).add(with(any(Image.class)));
                allowing(mockedEventBus).addHandler(with(PortletRefreshEvent.TYPE), with(any(PortletWindow.class)));
            }
        });
    }

    //    private class Builder {
    //        private String tabOwnerId = "tabId";
    //
    //        private String portalId = "porletId";
    //
    //        private String portalName = "testPorlet";
    //
    //        private String height = "1";
    //
    //        private String wsURL = "DASHBOARD/ROAMER";
    //
    //        private SearchFieldUser isSearchFieldUser = SearchFieldUser.FALSE;
    //
    //        private String displayType = "bar";
    //
    //        private String commaSeperatedDateFrom = "*,1440,CELL,10080,BSC,2880";
    //
    //        private ParametersDataType parameters = new ParametersDataType();
    //
    //        private PortletType type = PortletType.CHART;
    //
    //        private String commaSeperatedExcludedSearchTypes = "";
    //
    //        public StubbedPortletDataType build() {
    //            return new StubbedPortletDataType(this);
    //        }
    //
    //        public Builder tabOwnerId(final String tabOwnerId) {
    //            this.tabOwnerId = tabOwnerId;
    //            return this;
    //        }
    //
    //        public Builder portalId(final String porletId) {
    //            this.portalId = porletId;
    //            return this;
    //        }
    //
    //        public Builder portalName(final String portalName) {
    //            this.portalName = portalName;
    //            return this;
    //        }
    //
    //        public Builder height(final String height) {
    //            this.height = height;
    //            return this;
    //        }
    //
    //        public Builder wsURL(final String wsURL) {
    //            this.wsURL = wsURL;
    //            return this;
    //        }
    //
    //        public Builder isSearchFieldUser(final SearchFieldUser isSearchFieldUser) {
    //            this.isSearchFieldUser = isSearchFieldUser;
    //            return this;
    //        }
    //
    //        public Builder displayType(final String displayType) {
    //            this.displayType = displayType;
    //            return this;
    //        }
    //
    //        public Builder commaSeperatedDateFrom(final String commaSeperatedDateFrom) {
    //            this.commaSeperatedDateFrom = commaSeperatedDateFrom;
    //            return this;
    //        }
    //
    //        public Builder parameters(final ParametersDataType parameters) {
    //            this.parameters = parameters;
    //            return this;
    //        }
    //
    //        public Builder type(final PortletType type) {
    //            this.type = type;
    //            return this;
    //        }
    //
    //        public Builder commaSeperatedExcludedSearchTypes(final String commaSeperatedExcludedSearchTypes) {
    //            this.commaSeperatedExcludedSearchTypes = commaSeperatedExcludedSearchTypes;
    //            return this;
    //        }
    //
    //    }
    //
    //    private class StubbedPortletDataType extends PortletDataType {
    //
    //        public StubbedPortletDataType(final String tabOwnerId, final String portalId, final String portalName,
    //                final String height, final String wsURL, final SearchFieldUser isSearchFieldUser,
    //                final String displayType, final String commaSeperatedDateFrom, final ParametersDataType parameters,
    //                final PortletType type, final String commaSeperatedExcludedSearchTypes) {
    //            super(tabOwnerId, portalId, portalName, height, wsURL, isSearchFieldUser, displayType,
    //                    commaSeperatedDateFrom, parameters, type, commaSeperatedExcludedSearchTypes);
    //
    //        }
    //
    //        public StubbedPortletDataType(final Builder builder) {
    //            this(builder.tabOwnerId, builder.portalId, builder.portalName, builder.height, builder.wsURL,
    //                    builder.isSearchFieldUser, builder.displayType, builder.commaSeperatedDateFrom, builder.parameters,
    //                    builder.type, builder.commaSeperatedExcludedSearchTypes);
    //        }
    //
    //    }

    private class StubbedPortletWindow extends PortletWindow {

        StubbedPortletWindow(final PortletDataType porletData, final EventBus eventBus) {
            super(porletData, eventBus, null);

        }

        @Override
        void setId(final String portletId) {
        }

        @Override
        void setPortalHeight(final PortletDataType porletData) {
        }

        @Override
        public void initWidget() {
        }

        @Override
        public String getCompleteURL(final String url) {
            return serverName + url;
        }

        @Override
        public Label getTitleLabel() {
            return mockedTitleLabel;

        }

        @Override
        SimplePanel getBodyContainer() {
            return mockedSimplePanel;
        }

        @Override
        SimplePanel getMessageContainer() {
            return mockedSimplePanel;
        }

        @Override
        SimplePanel getCloseButtonContainer() {
            return mockedSimplePanel;
        }

        @Override
        public Image createCloseButton(final String portletId) {
            return mockedCloseButton;
        }

        @Override
        ServerComms getServerComms() {
            return mockedServerComms;
        }

    }
}
