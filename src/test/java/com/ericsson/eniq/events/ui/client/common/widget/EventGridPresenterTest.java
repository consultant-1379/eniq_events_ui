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
package com.ericsson.eniq.events.ui.client.common.widget;

import static org.junit.Assert.*;

import com.extjs.gxt.ui.client.event.BaseEvent;
import org.jmock.Expectations;
import org.junit.*;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.*;
import com.ericsson.eniq.events.ui.client.datatype.*;
import com.ericsson.eniq.events.ui.client.grid.JSONGrid;
import com.ericsson.eniq.events.ui.client.grid.listeners.*;
import com.ericsson.eniq.events.ui.client.main.AbstractWindowLauncher;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.web.bindery.event.shared.EventBus;

public class EventGridPresenterTest extends TestEniqEventsUI {

    private static final String WIN_ID = "winID";

    EventGridView mockedView;

    MenuTaskBar mockedMenuTaskBar;

    JSONGrid mockedJSONGrid;

    InitializeToolbarHandler mockedInitializeToolbarHandler;

    AbstractBaseWindowDisplay mockedBaseWindow;

    ContentPanel mockedContentPanel;

    Button mockedButton;

    Menu mockedMenu;

    BaseToolBar mockedBaseToolBar;

    StoreFilter<ModelData> mockedStoreFilter;

    MetaMenuItem mockedMenuItem;

    MetaMenuItemDataType mockedMetaMenu;

    GridInfoDataType mockedGridInfoDataType;

    @Before
    public void setUp() {
        setUpGeneralExpectationsOnBusWhenDontCareAboutTypes();
        mockedView = context.mock(EventGridView.class);
        mockedMenuTaskBar = context.mock(MenuTaskBar.class);
        mockedBaseWindow = context.mock(AbstractBaseWindowDisplay.class);
        mockedContentPanel = context.mock(ContentPanel.class);
        mockedJSONGrid = context.mock(JSONGrid.class);
        mockedInitializeToolbarHandler = context.mock(InitializeToolbarHandler.class);
        mockedButton = context.mock(Button.class);
        mockedMenu = context.mock(Menu.class);
        mockedStoreFilter = context.mock(StoreFilter.class);
        mockedBaseToolBar = context.mock(BaseToolBar.class);
        mockedMenuItem = context.mock(MetaMenuItem.class);
        mockedMetaMenu = context.mock(MetaMenuItemDataType.class);
        mockedGridInfoDataType = context.mock(GridInfoDataType.class);
        new StubbedBreadCrumbMenuItem("", 0, "WinID 1", null);
    }

    @Test
    public void constructEventGridPresenter() {

        context.checking(new Expectations() {
            {
                one(mockedView).getWorkspaceController();
                one(mockedView).updateTimeFromPresenter(with(any(TimeInfoDataType.class)));
            }
        });
        new EventGridPresenter(mockedView, null, mockedEventBus);
    }

    @Test
    public void initializeWidgit() {
        context.checking(new Expectations() {
            {

                allowing(mockedJSONGrid).setColumns(with(any(GridInfoDataType.class)));
                one(mockedJSONGrid).addListener(with(any(EventType.class)), with(any(RowSelectListener.class)));

                one(mockedJSONGrid).replaceRefreshBtnListener(with(any(RefreshGridFromServerListener.class)));
                one(mockedJSONGrid).removeListener(with(any(EventType.class)), with(any(RefreshGridFromServerListener.class)));

                one(mockedJSONGrid).addListener(with(any(EventType.class)), with(any(HyperLinkCellClickListener.class)));

                allowing(mockedView).setWidgetTitle(with(any(String.class)));
                one(mockedJSONGrid).setupStateful(with(any(String.class)), with(any(String.class)));


                one(mockedMenuItem).getWizardId();

                one(mockedView).getWindowCategoryId();

                one(mockedMenuItem).getID();

                one(mockedView).setWindowCategoryId("");

                one(mockedJSONGrid).getBottomToolbar();

                one(mockedView).getViewSettings(); will(returnValue(mockedMenuItem));
                one(mockedMenuItem).getWindowType(); will(returnValue(MetaMenuItemDataType.Type.RANKING));
                one(mockedJSONGrid).setRowsPerPage(20);
            }
        });
        getObjectToTest().initializeWidgit("queryID");
    }

    @Test
    public void initializeWidgitWithGridInfo(){
        context.checking(new Expectations() {
            {

               one(mockedView).getViewSettings();will(returnValue(mockedMenuItem));
                one(mockedMenuItem).getWindowType(); will(returnValue(MetaMenuItemDataType.Type.RANKING));
                one(mockedJSONGrid).setupStateful(with(any(String.class)), with(any(String.class)));
                one(mockedJSONGrid).resetColumns(mockedGridInfoDataType);
                one(mockedView).getWindowCategoryId(); will(returnValue(""));
               one(mockedMenuItem).getID(); will(returnValue((mockedMetaMenu.id)));
                one(mockedView).setWindowCategoryId(null);
                one(mockedView).setWidgetTitle(with(any(String.class)));
                allowing(mockedJSONGrid).addListener(with(any(EventType.class)), with(any(RowSelectListener.class)));
                one(mockedJSONGrid).replaceRefreshBtnListener(with(any(RefreshGridFromServerListener.class)));
                one(mockedJSONGrid).removeListener(with(any(EventType.class)),with(any(RowSelectListener.class)));
                one(mockedJSONGrid).getBottomToolbar();
                one(mockedJSONGrid).setRowsPerPage(20);
            }
        } );
        getObjectToTest().initializeWidgitWithGridInfo((mockedGridInfoDataType), true, "Ranking");

    }

    @Test
    public void handleGridCellLinkClickWithCorrectParametersMakesNoServerCall() {

        final String winID = WIN_ID;
        final String tabId = "SUBSCRIBER_TAB";
        final String val = "cellContent";
        final String url = "sampleDrillDownWindow";
        final int index = 1;

        final EventGridPresenter objtoTest = getObjectToTest(tabId);

        setupExpectationsForDrillDown(index);

        objtoTest.setQueryId(winID);
        objtoTest.setDisplayType("grid");
        objtoTest.setFixedQueryId(winID);

        objtoTest.handleCellLinkClick(createMultipleWinId(tabId, winID), val, url, index);

    }

    private void setupExpectationsForDrillDown(final int index) {
        context.checking(new Expectations() {
            {

                allowing(mockedMenuItem).setId(WIN_ID);
                allowing(mockedMenuItem).setDisplay("grid");
                one(mockedView).getParentWindow();
                will(returnValue(mockedBaseWindow));
                one(mockedBaseWindow).getBaseWindowID();
            }
        });
    }

    private void setupExpectationsForDrillDownWithAllExpectations(final int index) {
        context.checking(new Expectations() {
            {

                allowing(mockedMenuItem).setId(WIN_ID);
                allowing(mockedMenuItem).setDisplay("grid");
                allowing(mockedView).getWindowToolbar();
                allowing(mockedButton).getMenu();
                allowing(mockedView).getBaseWindowTitle();
                allowing(mockedMenuItem).getWsURL();
                allowing(mockedJSONGrid).getCacheStore();
                allowing(mockedJSONGrid).getColumns();
                allowing(mockedJSONGrid).getData();
                allowing(mockedView).getTimeData();
                allowing(mockedView).getLastRefreshTimeStamp();
                allowing(mockedJSONGrid).getStateId();
                allowing(mockedJSONGrid).saveState();
                allowing(mockedJSONGrid).getColumnModel();
                allowing(mockedView).setToolbarButtonEnabled("btnForward", false);
                one(mockedView).getWindowState();
                allowing(mockedMenuItem).setSearchFieldUser(SearchFieldUser.FALSE);
                allowing(mockedMenuItem).setWsURL("www.ericsson.se");
                allowing(mockedView).getViewSettings();
                allowing(mockedJSONGrid).setupStateful("winID", "gridCategoryId");
                allowing(mockedJSONGrid).setColumns(with(any(GridInfoDataType.class)));
                allowing(mockedView).setWidgetTitle("My network event analysis grid");
                allowing(mockedJSONGrid).addListener(with(any(EventType.class)), with(any(RowSelectListener.class)));
                allowing(mockedJSONGrid).replaceRefreshBtnListener((with(any(RefreshGridFromServerListener.class))));
                allowing(mockedJSONGrid).removeListener(with(any(EventType.class)), with(any(HyperLinkCellClickListener.class)));
                allowing(mockedJSONGrid).setupStateful("winID-null", "gridCategoryId");
                allowing(mockedJSONGrid).getGridType();
                allowing(mockedView).getGridCellValue(1, "1");
                allowing(mockedInitializeToolbarHandler).updateToolBarNavigation(with(any(String.class)), with(any(IHyperLinkDataType.class)));
                one(mockedMenuItem).getWizardId();
                one(mockedView).getParentWindow();
                will(returnValue(mockedBaseWindow));
                one(mockedBaseWindow).getBaseWindowID();
                will(returnValue(WIN_ID));
            }
        });
    }

    @Test
    public void handleGridCellLinkClickWithInCorrectParametersMakesNoServerCall() {

        final String winID = WIN_ID;
        final String tabId = "SUBSCRIBER_TAB";
        final String value = "cellContent";
        final String url = "sampleDrillDownWindow";
        final int index = 1;

        final EventGridPresenter objtoTest = getObjectToTest();

        context.checking(new Expectations() {
            {

                one(mockedMenuItem).setId("NotWinID");
                one(mockedMenuItem).setDisplay("grid");

                allowing(mockedJSONGrid).setColumns(with(any(GridInfoDataType.class)));
            }
        });

        objtoTest.setQueryId("NotWinID");
        objtoTest.setDisplayType("grid");
        objtoTest.setFixedQueryId("DiffID");

        objtoTest.handleCellLinkClick(createMultipleWinId(tabId, winID), value, url, index);

    }

    @Test
    @Ignore
    public void handleGridCellLinkClickMakesServerCall() {

        final String winID = WIN_ID;
        final String tabId = "SUBSCRIBER_TAB";
        final String val = "cellContent";
        final String url = "sampleDrillDownWindow";
        final int index = 1;

        final EventGridPresenter objtoTest = getObjectToTest(tabId);

        setupExpectationsForDrillDownWithAllExpectations(index);

        objtoTest.setQueryId(winID);
        objtoTest.setDisplayType("grid");
        objtoTest.setFixedQueryId(winID);
        objtoTest.handleCellLinkClick(createMultipleWinId(tabId, winID), val, url, index);

        assertEquals("server call made ", true, ((StubbedEventGridPresenter) objtoTest).isDrillDownServerCallMade);

    }

    @Test
    public void kpiButtonDisabledWhenNoSearchDataForWindow() {
        context.checking(new Expectations() {
            {
                one(mockedView).getWorkspaceController();
                allowing(mockedView).updateTimeFromPresenter(with(any(TimeInfoDataType.class)));
            }
        });

        final EventGridPresenter objtoTest = new StubbedEventGridPresenter(mockedView, mockedEventBus) {

            @Override
            public SearchFieldDataType getSearchData() {
                return null;
            }
        };

        objtoTest.handleButtonEnabling(50);
    }

    @Test
    public void searchDependantButtonsEnabledWhenSearchFieldChanged() {
        final EventGridPresenter objtoTest = getObjectToTest();
        objtoTest.cachedLastRecievedSearchData = new SearchFieldDataType("SGSN01", null, null, null, false, "", null, false);
        objtoTest.handleButtonEnabling(50);

        objtoTest.cachedLastRecievedSearchData = null;
        objtoTest.handleButtonEnabling(50);
    }

    @Test
    public void cleanUpBreadCrumbMenuTest() {
        final EventGridPresenter objtoTest = getObjectToTest();
        context.checking(new Expectations() {
            {
                one(mockedView).getWindowToolbar();
                one(mockedButton).getMenu();
                will(returnValue(mockedMenu));
                one(mockedMenu).getItemCount();
            }
        });
        objtoTest.cleanUpBreadCrumbMenu();
    }

    @Test
    @Ignore
    public void handleCellLauncherClickLaunchesWindowWhenTabIdCorrect() {
        final EventGridPresenter objtoTest = getObjectToTest("NETWORK_TAB");
        final String winID = WIN_ID;

        context.checking(new Expectations() {
            {
                one(mockedMenuItem).setId(WIN_ID);
                one(mockedMenuItem).setDisplay("grid");
                one(mockedMenuItem).getWizardId();

                one(mockedBaseWindow).getBaseWindowID();
                will(returnValue(WIN_ID));
            }
        });

        objtoTest.setQueryId(winID);
        objtoTest.setDisplayType("grid");
        objtoTest.setFixedQueryId(winID);

        final int rowIndex = 4;

        setupExpectationsForCellClick("NETWORK_TAB", rowIndex);

        objtoTest.handleCellLauncherClick(createMultipleWinId("NETWORK_TAB", winID), "BSC02", "NETWORK_EVENT_ANALYSIS_BSC", rowIndex);

        assertEquals("window should be launched as have correct tab id", true, ((StubbedEventGridPresenter) objtoTest).isWindowLaunchAttempted);

    }

    @Test
    public void handleCellLauncherClickDoesNotLaunchWinowInDifferentTab() {
        final EventGridPresenter objtoTest = getObjectToTest();
        final String winID = WIN_ID;

        context.checking(new Expectations() {
            {
                one(mockedMenuItem).setId(WIN_ID);
                one(mockedMenuItem).setDisplay("grid");

            }
        });

        objtoTest.setQueryId(winID);
        objtoTest.setDisplayType("grid");
        objtoTest.setFixedQueryId(winID);

        final int rowIndex = 4;
        setupExpectationsForCellClick("Different tab", rowIndex);

        objtoTest.handleCellLauncherClick(createMultipleWinId("NETWORK_TAB", winID), "BSC02", "NETWORK_EVENT_ANALYSIS_BSC", rowIndex);

        assertEquals("window should not be launched as in wrong tab", false, ((StubbedEventGridPresenter) objtoTest).isWindowLaunchAttempted);

    }

    private void setupExpectationsForCellClick(final String tabID, final int rowIndex) {
        context.checking(new Expectations() {
            {
                allowing(mockedView).getWorkspaceController();
                will(returnValue(mockedMenuTaskBar));

                allowing(mockedMenuTaskBar).getTabOwnerId();
                will(returnValue(tabID));

                allowing(mockedView).getParentWindow();
                will(returnValue(mockedBaseWindow));

                allowing(mockedBaseWindow).getConstraintArea();
                will(returnValue(mockedContentPanel));

                allowing(mockedMenuTaskBar).getWindow(WIN_ID);

                allowing(mockedView).getGridCellValue(rowIndex, "2"); // see 2 in stub class
                will(returnValue("random string title"));
            }
        });
    }

    private EventGridPresenter getObjectToTest() {
        context.checking(new Expectations() {
            {
                allowing(mockedView).getWorkspaceController();
                will(returnValue(mockedMenuTaskBar));
                allowing(mockedMenuTaskBar).getTabOwnerId();

                allowing(mockedView).updateTimeFromPresenter(with(any(TimeInfoDataType.class)));
            }
        });

        final StubbedEventGridPresenter s = new StubbedEventGridPresenter(mockedView, mockedEventBus);
        s.setMetaItem();
        return s;
    }

    private EventGridPresenter getObjectToTest(final String tabId) {
        context.checking(new Expectations() {
            {
                allowing(mockedView).getWorkspaceController();
                will(returnValue(mockedMenuTaskBar));
                allowing(mockedMenuTaskBar).getTabOwnerId();
                will(returnValue(tabId));
                allowing(mockedJSONGrid).getFilters();
                allowing(mockedView).updateTimeFromPresenter(with(any(TimeInfoDataType.class)));
            }
        });

        final StubbedEventGridPresenter s = new StubbedEventGridPresenter(mockedView, mockedEventBus);
        s.setMetaItem();
        return s;
    }

    private class StubbedBreadCrumbMenuItem extends BreadCrumbMenuItem {
        public StubbedBreadCrumbMenuItem(final String title, final int index, final String winID, final IHyperLinkDataType drillInfo) {
            super(title, index, winID, drillInfo);
        }

        @Override
        public void setIconStyle(final String icon) {
        }

    }

    private DrillDownInfoDataType getDummyDrillInfo(final String url) {
        final DrillDownInfoDataType drillInfo = new DrillDownInfoDataType(url);
        drillInfo.displayType = "grid";
        drillInfo.id = "drill";
        drillInfo.isEnabled = true;
        drillInfo.name = "TEST DRILL";
        drillInfo.style = "TEST STYLE";
        drillInfo.toolBarType = "ToolbarType3";
        drillInfo.url = "www.ericsson.se";
        drillInfo.queryParameters = new DrillDownParameterInfoDataType[1];

        for (int x = 0; x < 1; x++) {

            drillInfo.queryParameters[x] = new DrillDownParameterInfoDataType();
            drillInfo.queryParameters[x].parameterName = "val";
            drillInfo.queryParameters[x].parameterValue = "1";
        }
        return drillInfo;
    }

    private class StubbedEventGridPresenter extends EventGridPresenter {

        boolean isWindowLaunchAttempted = false;

        int testRowCount = 0;

        boolean isDrillDownServerCallMade;

        public StubbedEventGridPresenter(final IEventGridView display, final EventBus eventBus) {
            super(display, null, eventBus);
        }

        public void setMetaItem() {
            this.metaMenuItem = mockedMenuItem;
        }

        @Override
        public void handleButtonEnabling(final int rowCount) {
        }

        @Override
        LaunchWinDataType getLaunchDetailsFromMetaData(final String launchId) {
            final LaunchWinDataType returnVal = new LaunchWinDataType(launchId);
            returnVal.searchValFromCol = "2";
            return returnVal;
        }

        @Override
        public void launchWindowFromHyperLink(final AbstractWindowLauncher launcher, final SearchFieldDataType searchVal,
                                              final TimeInfoDataType initialTime) {
            isWindowLaunchAttempted = true;
        }

        @Override
        MetaMenuItem getMetaMenuItemFromLaunchDetails(final String launchDetailsMenuItem) {

            final MetaMenuItemDataType data = new MetaMenuItemDataType.Builder().build();
            data.setWinId(WIN_ID);
            return new MetaMenuItem(data);
        }

        @Override
        public GridInfoDataType getGridInfoFromMetaReader(final String value) {
            return getDummyGridInfo();

        }

        @Override
        public SearchFieldDataType getSearchData() {
            return new SearchFieldDataType("different", null, null, null, false, "", null, false);
        }

        @Override
        JSONGrid getDisplayedGrid() {
            return mockedJSONGrid;
        }

        @Override
        Component getComponent(final Menu breadCrumbMenu) {
            return new StubbedBreadCrumbMenuItem("Item 1", 1, "WinID 1", null);
        }

        private GridInfoDataType getDummyGridInfo() {

            final ColumnInfoDataType columnInfo = new ColumnInfoDataType();

            final GridInfoDataType gridInfoType = new GridInfoDataType();
            gridInfoType.gridType = "GRID";
            gridInfoType.gridTitle = "GRID title";
            gridInfoType.gridType = "SUBSCRIBER_OVERVIEW";
            gridInfoType.gridTitle = "My network event analysis grid";
            gridInfoType.colAutoExpand = "1";
            gridInfoType.sortColumn = "1";
            gridInfoType.columnInfo = new ColumnInfoDataType[] { columnInfo };
            gridInfoType.categoryId = "gridCategoryId";
            return gridInfoType;

        }

        @Override
        protected DrillDownInfoDataType getDrillDownWindowInfoFromMetaData(final String url) {
            return getDummyDrillInfo(url);
        }

        @Override
        void getServerDataForDrillDown(final String value) {
            isDrillDownServerCallMade = true;

        }

        @Override
        public void initializeToolbar(final boolean displaytime) {

        }

        @Override
        public InitializeToolbarHandler<? extends IExtendedWidgetDisplay> getInitToolBarHandler() {
            return mockedInitializeToolbarHandler;
        }

        @Override
        public Button getNavButton(final BaseToolBar winToolBar) {
            return mockedButton;
        }

    }

    private MultipleInstanceWinId createMultipleWinId(final String tabId, final String winId) {
        return new MultipleInstanceWinId(tabId, winId);
    }

}
