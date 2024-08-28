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

import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;
import static junit.framework.Assert.*;

import java.util.List;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.ToolBarStateManager;
import com.ericsson.eniq.events.ui.client.common.widget.EventGridView;
import com.ericsson.eniq.events.ui.client.datatype.*;
import com.ericsson.eniq.events.ui.client.events.TimeParameterValueChangeEventHandler;
import com.ericsson.eniq.events.ui.client.events.handlers.ServerFailedResponseHandler;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.EventBus;

public class BaseWindowPresenterTest extends TestEniqEventsUI {

    protected static final String TAB_OWNER_ID = "tabID";

    protected static final boolean IS_GROUP_MODE = false;

    EventGridView mockedView;

    BaseToolBar mockedToolBar;

    MenuTaskBar mockedMenuTaskBar;

    MetaMenuItem mockedMenuItem;

    BaseWinServerComms mockedBaseWinServerComms;

    public String expectedButtonIdForGroupSingleToggle = BTN_SUBSCRIBER_DETAILS;

    public String expectedButtonIdForSearchFieldTypeChangeHandler = BTN_SAC;

    SearchFieldDataType mockedSearchData;

    Button mockedButton;

    Menu mockedMenu;

    BreadCrumbMenuItem mockedBreadCrumbMenuItem;

    BaseWinTimeParameterValueHandler mockedTimeParameterValueChangeEventHandler;

    BaseWinRefreshWindowHandler mockedRefreshWindowEventHandler;

    @Before
    public void setUp() {

        setUpGeneralExpectationsOnBusWhenDontCareAboutTypes();
        mockedView = context.mock(EventGridView.class);
        mockedMenuTaskBar = context.mock(MenuTaskBar.class);
        mockedMenuItem = context.mock(MetaMenuItem.class);
        mockedToolBar = context.mock(BaseToolBar.class);

        mockedTimeParameterValueChangeEventHandler = context.mock(BaseWinTimeParameterValueHandler.class);
        mockedRefreshWindowEventHandler = context.mock(BaseWinRefreshWindowHandler.class);

        mockedSearchData = context.mock(SearchFieldDataType.class);

        mockedButton = context.mock(Button.class);
        mockedMenu = context.mock(Menu.class);
        mockedBreadCrumbMenuItem = context.mock(BreadCrumbMenuItem.class);
        mockedBaseWinServerComms = context.mock(BaseWinServerComms.class);
    }

    @Test
    public void testThatNullSearchDataDoesntCauseNullPointer() throws Exception {

        setupExpectationsNullSearchField();
        setupExpectionsForInitWindow();
        final MetaMenuItemDataType.Type windowType = MetaMenuItemDataType.convertType("RANKING");

        final MetaMenuItem menuItem = new MetaMenuItem(new MetaMenuItemDataType.Builder().text("Terminal Rankings").id("someWinId")
                .url("http: //localhost: 41993/RestTest/TERMINAL_EVENT_ANALYSIS").windowType(windowType).display("grid").type("IMSI").key("SUM")
                .toolBarHandler(new ToolBarStateManager("TOOLBAR2", ToolBarStateManager.BottomToolbarType.PAGING, "TOOLBAR1"))
                .maxRowsParam("MAXROWSPARAM").minimizedButtonName("Miminised name").build());

        final BaseWindowPresenter objToTest = getObjectToTest();
        objToTest.initWindow(menuItem, null, null, TimeInfoDataType.DEFAULT, null, true, "");
    }

    @Test
    public void timeRangeIsSetToExpectedDefaultOnConstruct() throws Exception {

        final BaseWindowPresenter baseWindowPresenter = getObjectToTest();
        assertEquals("Time is set to expected default", String.valueOf(30), baseWindowPresenter.getTimeData().timeRange);
    }

    private void setupExpectationsWhenInitWithSearchData(final SearchFieldDataType searchData) {
        context.checking(new Expectations() {
            {

                one(mockedView).updateTimeFromPresenter(with(any(TimeInfoDataType.class)));
                one(mockedMenuItem).getSearchFieldUser();
                will(returnValue(SearchFieldUser.PATH));

                one(mockedMenuItem).getWindowType();
                one(mockedMenuItem).hasMultiResult();
                one(mockedMenuItem).getWizardId();
                one(mockedBaseWinServerComms).potentiallyMakeCallOnWindowLaunch(searchData);
                one(mockedMenuItem).setLaunchedFromCellHyperlink(false);
                one(mockedMenuItem).isDisablingTime();
                one(mockedMenuItem).getTimeValue();
                one(mockedMenuItem).getID();
                one(mockedMenuItem).isSearchFieldUser();
            }
        });
    }

    @Test
    public void initWindowTakesCorrectData() throws Exception {

        final BaseWindowPresenter objtoTest = getObjectToTest();

        final MetaMenuItemDataType.Type windowType = MetaMenuItemDataType.convertType("RANKING");

        final MetaMenuItem menuItem = new MetaMenuItem(new MetaMenuItemDataType.Builder().text("Terminal Rankings").id("someWinId")
                .url("http: //localhost: 41993/RestTest/TERMINAL_EVENT_ANALYSIS").windowType(windowType).display("grid").type("IMSI").key("SUM")
                .toolBarHandler(new ToolBarStateManager("TOOLBAR2", ToolBarStateManager.BottomToolbarType.PAGING, "TOOLBAR1"))
                .maxRowsParam("MAXROWSPARAM").minimizedButtonName("Miminised name").build());

        context.checking(new Expectations() {
            {
                one(mockedBaseWinServerComms).potentiallyMakeCallOnWindowLaunch(null);
                one(mockedView).updateTimeFromPresenter(with(any(TimeInfoDataType.class)));
                one(mockedSearchData).setPathMode(false);
                one(mockedView).resetSearchData(mockedSearchData);
                one(mockedView).updateSearchFieldDataType(mockedSearchData);
            }
        });

        objtoTest.resetSearchData(mockedSearchData);
        setupExpectionsForInitWindow();

        objtoTest.initWindow(menuItem, null, null, TimeInfoDataType.DEFAULT, null, true, "");

        assertEquals("Correct base window title", "Terminal Rankings", objtoTest.getMetaMenuItem().getText());
        assertEquals("Correct QueryId (winId) passed to window", "someWinId", objtoTest.getQueryId());
        assertEquals("Correct QueryId (winId) passed to window", "someWinId", objtoTest.getQueryId());
        assertEquals("Correct toolbar type passed to window", "TOOLBAR2", objtoTest.getMetaMenuItem().getCurrentToolBarType());
        assertEquals("Correct url passed to window", "http: //localhost: 41993/RestTest/TERMINAL_EVENT_ANALYSIS", objtoTest.getWsURL());
        assertEquals("Correct window type", MetaMenuItemDataType.Type.RANKING, objtoTest.getMetaMenuItem().getWindowType());
        assertEquals("Set to read from search field", false, objtoTest.getMetaMenuItem().isSearchFieldUser());

    }

    private void setupExpectationsNullSearchField() {
        context.checking(new Expectations() {
            {
                one(mockedBaseWinServerComms).potentiallyMakeCallOnWindowLaunch(null);
                one(mockedView).updateTimeFromPresenter(with(any(TimeInfoDataType.class)));
            }
        });
    }

    private void setupExpectionsForInitWindow() {
        context.checking(new Expectations() {
            {
                allowing(mockedView).getBaseWindowID();
                one(mockedView).appendTitle(with(any(String.class)));
                one(mockedView).getWindowState();
            }
        });
    }

    private void setUpExpectationsOnMenuTaskBar() {
        context.checking(new Expectations() {
            {
                allowing(mockedView).getWorkspaceController();
                will(returnValue(mockedMenuTaskBar));
                allowing(mockedMenuTaskBar).getTabOwnerId();
                will(returnValue(TAB_OWNER_ID));

            }
        });
    }

    @Test
    public void buildFixedQueryIdIsOkWithNullSearchData() throws Exception {
        final BaseWindowPresenter objtoTest = getObjectToTest();
        context.checking(new Expectations() {
            {
                one(mockedMenuItem).getId();
            }
        });

        objtoTest.buildFixedQueryId(null);
    }

    @Test
    public void buildFixedQueryIdChangeFixedIdWhenIsAGroupAndNoType() throws Exception {
        final BaseWindowPresenter objtoTest = getObjectToTest();
        context.checking(new Expectations() {
            {

                one(mockedMenuItem).setId("QUERY_ID");
                one(mockedMenuItem).isLaunchedFromCellHyperlink();
                one(mockedMenuItem).getId();
                will(returnValue("QUERY_ID"));
            }
        });
        final String QUERY_ID = "QUERY_ID";
        objtoTest.setQueryId(QUERY_ID);
        final String imsi = "12345678";
        final SearchFieldDataType searchData = new SearchFieldDataType(imsi, new String[] { "imsi=" }, null, null, true, "", null, false);

        objtoTest.buildFixedQueryId(searchData);
        assertEquals("Expect query id saame as fixed query id", QUERY_ID + "_GROUP", objtoTest.getFixedQueryId());

    }

    @Test
    public void buildFixedQueryIdChangesFixedIdGroupAndNoType() throws Exception {
        final BaseWindowPresenter objtoTest = getObjectToTest();
        context.checking(new Expectations() {
            {
                one(mockedMenuItem).setId("QUERY_ID");
                one(mockedMenuItem).isLaunchedFromCellHyperlink();
                one(mockedMenuItem).getId();
                will(returnValue("QUERY_ID"));
            }
        });
        final String QUERY_ID = "QUERY_ID";
        objtoTest.setQueryId(QUERY_ID);
        final String imsi = "12345678";
        final SearchFieldDataType searchData = new SearchFieldDataType(imsi, new String[] { "imsi=" }, null, null, true, "", null, false);

        objtoTest.buildFixedQueryId(searchData);
        final String expectedQueryID = QUERY_ID + "_GROUP";
        assertEquals("Expect fixed query id with group appended", expectedQueryID, objtoTest.getFixedQueryId());

    }

    @Test
    public void initRankingWindowRegistorsWithTimer() throws Exception {

        final StubbedBaseWindowPresenter stubWindowClass = getObjectToTest();

        setupExpectationsNullSearchField();
        setupExpectionsForInitWindow();
        stubWindowClass.initWindow(getRankingMetaMenuItem("someId"), null, null, TimeInfoDataType.DEFAULT, null, true, "");
        assertEquals("Call made to registor with ranking timer", true, stubWindowClass.isRankingRegistored);
    }

    @Test
    public void initNonRankingWindowWillNotRegisterWithTimer() {

        final StubbedBaseWindowPresenter stubWindowClass = getObjectToTest();

        setupExpectationsNullSearchField();
        setupExpectionsForInitWindow();
        stubWindowClass.initWindow(getGridMetaMenuItem(), null, null, TimeInfoDataType.DEFAULT, null, true, "");
        assertEquals("Call not made to registor with ranking timer", false, stubWindowClass.isRankingRegistored);
    }

    @Test
    public void shutDownRankingWindowWillUnRegisterWithTimer() throws Exception {
        final String winId = "someId";
        final StubbedBaseWindowPresenter stubWindowClass = getObjectToTest();

        setupExpectationsNullSearchField();
        setupExpectionsForInitWindow();
        setUpExpectationsOnMenuTaskBar();
        stubWindowClass.initWindow(getRankingMetaMenuItem(winId), null, null, TimeInfoDataType.DEFAULT, null, true, "");
        stubWindowClass.handleShutDown();
        assertEquals("Call made to unregistor with ranking timer", true, stubWindowClass.isRankingRemoved);
    }

    @Test
    public void shutDownNonRankingWindowWillUnRegisterWithTimer() {
        final StubbedBaseWindowPresenter stubWindowClass = getObjectToTest();

        setupExpectationsNullSearchField();
        setUpExpectationsOnMenuTaskBar();
        setupExpectionsForInitWindow();
        stubWindowClass.initWindow(getGridMetaMenuItem(), null, null, TimeInfoDataType.DEFAULT, null, true, "");

        stubWindowClass.handleShutDown();
        assertEquals("Call not made to unregistor with ranking timer", false, stubWindowClass.isRankingRemoved);
    }

    @Test
    public void emptySearchFieldUpdateWillNotUpDateCacheAndWindowTitle() throws Exception {

        final String emptyStr = "";
        final String winID = "winID";
        final String defaultUrl = "http://localhost:8080/EniqEventsServices/NETWORK/EVENT_ANALYSIS";
        final BaseWindowPresenter objtoTest = getObjectToTest();

        context.checking(new Expectations() {
            {
                ignoring(mockedMenuItem);
                setUpExpectationsOnMenuTaskBar();
            }
        });
        objtoTest.getMetaMenuItem().setSearchFieldUser(SearchFieldUser.TRUE);
        objtoTest.setQueryId(winID);
        final SearchFieldDataType searchfieldData = new SearchFieldDataType(emptyStr, new String[] { "node=myNode", "nodeType=SGSN" }, null, null,
                false, "", null, false);

        objtoTest.handleSearchFieldParamUpdate(TAB_OWNER_ID, winID, searchfieldData, defaultUrl);

        assertEquals("Cached searchFieldParm not updated ", null, objtoTest.searchData);
        assertEquals("Does not make server call", false, ((StubbedBaseWindowPresenter) objtoTest).makesServerCall);
    }

    @Test
    public void searchFieldUpdateForDifferentWindowWillNotUpDateCacheAndWindowTitle() throws Exception {

        final String winID = "winID";
        final String defaultUrl = "http://localhost:8080/EniqEventsServices/NETWORK/EVENT_ANALYSIS";
        final BaseWindowPresenter objtoTest = getObjectToTest();

        context.checking(new Expectations() {
            {
                ignoring(mockedMenuItem);
                setUpExpectationsOnMenuTaskBar();
            }
        });

        objtoTest.getMetaMenuItem().setSearchFieldUser(SearchFieldUser.TRUE);
        objtoTest.setQueryId(winID);
        final SearchFieldDataType searchfieldData = new SearchFieldDataType("whatever query", new String[] { "node=myNode", "nodeType=SGSN" }, null,
                null, false, "", null, false);

        objtoTest.handleSearchFieldParamUpdate(TAB_OWNER_ID, "Different win ID", searchfieldData, defaultUrl);
        assertEquals("Cached searchFieldParm not updared ", null, objtoTest.searchData);
        assertEquals("Does not make server call", false, ((StubbedBaseWindowPresenter) objtoTest).makesServerCall);
    }

    @Test
    public void searchFieldUpdateWhenNotInterestedWillNotUpDateCacheAndWindowTitle() throws Exception {

        final String winID = "winID";
        final String defaultUrl = "http://localhost:8080/EniqEventsServices/NETWORK/EVENT_ANALYSIS";
        final BaseWindowPresenter objtoTest = getObjectToTest();

        context.checking(new Expectations() {
            {
                ignoring(mockedMenuItem);
                setUpExpectationsOnMenuTaskBar();
            }
        });
        objtoTest.getMetaMenuItem().setSearchFieldUser(SearchFieldUser.FALSE);
        objtoTest.setQueryId(winID);
        final SearchFieldDataType searchfieldData = new SearchFieldDataType("whatever query", new String[] { "node=myNode", "nodeType=SGSN" }, null,
                null, false, "", null, false);

        objtoTest.handleSearchFieldParamUpdate(TAB_OWNER_ID, winID, searchfieldData, defaultUrl);
        assertEquals("Cached searchFieldParm not updated ", null, objtoTest.searchData);
        assertEquals("Does not make se, rver call", false, ((StubbedBaseWindowPresenter) objtoTest).makesServerCall);
    }

    @Test
    public void handleTimeParamUpdateWithCorrectWinIdMakesServerCall() throws Exception {

        final String winID = "winID";
        final StubbedBaseWindowPresenter objtoTest = getObjectToTestII();
        context.checking(new Expectations() {
            {

                allowing(mockedTimeParameterValueChangeEventHandler).handleTimeParamUpdate(with(any(MultipleInstanceWinId.class)),
                        with(any(TimeInfoDataType.class)));

            }
        });

        objtoTest.tabOwnerId = TAB_OWNER_ID;

        objtoTest.searchData = new SearchFieldDataType("whatever query", new String[] { "node=myNode", "nodeType=SGSN" }, null, null, false, "",
                null, false);

        final TimeInfoDataType timeInfo = new TimeInfoDataType();
        timeInfo.timeRange = "30";

        final MultipleInstanceWinId multiID = createMultipleInstanceWinId(TAB_OWNER_ID, winID, objtoTest.searchData);
        objtoTest.setQueryId(multiID.getWinId());
        objtoTest.handleTimeParamUpdate(createMultipleInstanceWinId(TAB_OWNER_ID, winID, objtoTest.searchData), timeInfo);

        objtoTest.handleTimeParamUpdate(multiID, timeInfo);

        assertEquals("Windows &time parameter ok ", "&time=30", objtoTest.getTimeData().getQueryString(false));
        assertEquals("Windows ?time parameter ok ", "?time=30", objtoTest.getTimeData().getQueryString(true));
    }

    @Test
    public void canFetchInitializeToolbarHandler() throws Exception {
        final BaseWindowPresenter objtoTest = getObjectToTest();
        assertEquals("InitializeToolbarHandler not null", true, null != objtoTest.getInitToolBarHandler());
        objtoTest.initializeToolbar(true);
    }

    private MetaMenuItem getGridMetaMenuItem() {
        final MetaMenuItemDataType.Type windowType = MetaMenuItemDataType.convertType("GRID");

        final MetaMenuItem menuItem = new MetaMenuItem(new MetaMenuItemDataType.Builder().text("Cause Code Analysis").id("someWinId")
                .url("RestTest/NETWORK_CAUSE_CODE_ANALYSIS").windowType(windowType).display("grid").type("IMSI").key("SUM")
                .toolBarHandler(new ToolBarStateManager("TOOLBAR2", ToolBarStateManager.BottomToolbarType.PAGING, "TOOLBAR1"))
                .maxRowsParam("MAXROWSPARAM").minimizedButtonName("Miminised name").build());

        return menuItem;
    }

    private MetaMenuItem getRankingMetaMenuItem(final String winId) {

        final MetaMenuItemDataType.Type windowType = MetaMenuItemDataType.convertType("RANKING");

        final MetaMenuItem menuItem = new MetaMenuItem(new MetaMenuItemDataType.Builder().text("Cause Code Analysis").id("someWinId")
                .url("RestTest/WHATEVER").windowType(windowType).display("grid").type("IMSI").key("SUM")
                .toolBarHandler(new ToolBarStateManager("TOOLBAR2", ToolBarStateManager.BottomToolbarType.PAGING, "TOOLBAR1"))
                .maxRowsParam("MAXROWSPARAM").minimizedButtonName("Miminised name").build());

        return menuItem;
    }

    private StubbedBaseWindowPresenter getObjectToTest() {

        context.checking(new Expectations() {
            {

                one(mockedView).updateTimeFromPresenter(with(any(TimeInfoDataType.class)));

                one(mockedView).getWorkspaceController();

            }
        });
        final StubbedBaseWindowPresenter stubbedBaseWindowPresenter = new StubbedBaseWindowPresenter(mockedView, mockedEventBus);
        stubbedBaseWindowPresenter.setMetaItem();

        return stubbedBaseWindowPresenter;
    }

    private StubbedBaseWindowPresenter getObjectToTestII() {

        context.checking(new Expectations() {
            {
                one(mockedView).updateTimeFromPresenter(with(any(TimeInfoDataType.class)));
                one(mockedView).getWorkspaceController();

            }
        });
        final StubbedBaseWindowPresenter stubbedBaseWindowPresenter = new StubbedBaseWindowPresenter(mockedView, mockedEventBus);
        stubbedBaseWindowPresenter.setMetaItem(getFakeMetaMenuItemWithoutWizard("winID"));

        return stubbedBaseWindowPresenter;
    }

    private MultipleInstanceWinId createMultipleInstanceWinId(final String tabId, final String winId, final SearchFieldDataType searchFieldData) {
        return new MultipleInstanceWinId(tabId, winId/* , searchFieldData */);
    }

    private MetaMenuItem getFakeMetaMenuItemWithoutWizard(final String winId) {

        final MetaMenuItemDataType.Type windowType = MetaMenuItemDataType.Type.CHART;

        final MetaMenuItem menuItem = new MetaMenuItem(new MetaMenuItemDataType.Builder().text("Cause Code Analysis").id(winId)
                .url("RestTest/WHATEVER").windowType(windowType).display("grid").type("IMSI").key("SUM")
                .toolBarHandler(new ToolBarStateManager("TOOLBAR2", ToolBarStateManager.BottomToolbarType.PAGING, "TOOLBAR1"))
                .maxRowsParam("MAXROWSPARAM").minimizedButtonName("Miminised name").build());

        return menuItem;
    }

    private class StubbedBaseWindowPresenter extends BaseWindowPresenter {

        private static final String MAX_ROWS = "40";

        boolean isRankingRegistored = false;

        boolean isRankingRemoved = false;

        boolean makesServerCall = false;

        String stubRequestData = null;

        public StubbedBaseWindowPresenter(final IBaseWindowView display, final EventBus eventBus) {
            super(display, null, eventBus);

        }

        public void setMetaItem() {
            this.metaMenuItem = mockedMenuItem;
            // cheat for test only
            replaceTimeAndRefreshHandlers(mockedTimeParameterValueChangeEventHandler, mockedRefreshWindowEventHandler);
        }

        public void setMetaItem(final MetaMenuItem item) {
            metaMenuItem = item;
        }

        @Override
        public void initializeToolbar(final boolean displayTime) {

        }

        @Override
        BaseWinTimeParameterValueHandler<?> createDefaultTimeParameterValueHandler() {
            return mockedTimeParameterValueChangeEventHandler;
        }

        @Override
        BaseWinRefreshWindowHandler<?> createDefaultRefreshWindowHandler() {
            return mockedRefreshWindowEventHandler;
        }

        @Override
        public MetaMenuItem getMenuTaskBarMenuItemByID(final String id) {
            return mockedMenuItem;
        }

        @Override
        public String getOutBoundDisplayTypeParameter() {
            return (metaMenuItem.getWindowType() == MetaMenuItemDataType.Type.CHART) ? OUT_BOUND_CHART_DISPLAY_PARAM : OUT_BOUND_GRID_DISPLAY_PARAM;

        }

        @Override
        public TimeParameterValueChangeEventHandler getTimeParameterValueChangeEventHandler() {
            return mockedTimeParameterValueChangeEventHandler;
        }

        @Override
        public void registerWindowAsRankingWindow() {
            this.isRankingRegistored = true;
        }

        @Override
        void removeWindowAsRankingWindow() {
            this.isRankingRemoved = true;
        }

        @Override
        public void cleanUpBreadCrumbMenu() {
        }

        @Override
        public BreadCrumbMenuItem getCurrentBreadCrumbMenuItem() {
            return null;
        }

        @Override
        public void cleanUpOnClose() {
        }

        @Override
        public BaseWinServerComms getServerComm() {
            return mockedBaseWinServerComms;
        }

        @Override
        public ServerFailedResponseHandler getServerFailedResponseHandler() {
            return null;
        }

        @Override
        public int handleSuccessResponse(final Response response) {
            return 0;
        }

        @Override
        public int handleSuccessResponseWithJSONValue(final Response response, final JSONValue data, final List<Filter> filters) {
            return 0;
        }

        @Override
        public void initializeWidgit(final String fixedWinQueryId) {
        }

        @Override
        public void initializeWidgitWithGridInfo(final GridInfoDataType gridMetaData, final boolean resetColumns, final String title) {
        }

        @Override
        public String getMaxRowsURLParameter() {
            return MAX_ROWS;
        }

        @Override
        protected ButtonEnableParametersDataType getButtonEnableParameters(final int rowCount) {
            return new ButtonEnableParametersDataType();
        }

        @Override
        public void cleanUpWindowForCancelRequest() {

        }

        @Override
        public TimeInfoDataType getWindowTimeDate() {
            return null;
        }

        @Override
        public void setWindowTimeDate(TimeInfoDataType timeInfoDataType) {

        }
    }
}
