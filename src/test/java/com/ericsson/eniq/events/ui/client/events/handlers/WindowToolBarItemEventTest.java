/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events.handlers;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.MetaReaderConstants;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.common.comp.MenuTaskBar;
import com.ericsson.eniq.events.ui.client.common.widget.EventGridView;
import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.ericsson.eniq.events.ui.client.common.widget.TwoColumnGridDialogPresenter;
import com.ericsson.eniq.events.ui.client.datatype.*;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.ericsson.eniq.events.ui.client.events.ChangeChartGridEvent;
import com.ericsson.eniq.events.ui.client.main.AbstractWindowLauncher;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author eeicmsy
 *
 */

@Ignore
public class WindowToolBarItemEventTest extends TestEniqEventsUI {

    private WindowToolBarItemEvent objUnderTest;

    EventGridView mockedEventGridView;

    BaseWindow mockedBaseWindow;

    ContentPanel mockedContentPanel;

    private Component mockedMenuItem;

    private ModelData mockedRecord;

    MenuTaskBar mockedMenuTaskBar;

    AbstractWindowLauncher mockedWindowLauncher;

    MetaMenuItem mockedMetaMenuItem;

    ToolBarURLChangeDataType mockedUrlInfo;

    String expectedMenuItemId;

    IBaseWindowPresenter mockedPresenter;

    TwoColumnGridDialogPresenter<IExtendedWidgetDisplay> mockedGridDialogPresenter;

    private final static String DUMMY_NODE_NAME = "SGSN02";

    private final static String KPI_ANALYSIS_ID = "KPI_ANALYSIS";

    @Before
    public void setUp() {
        mockedEventGridView = context.mock(EventGridView.class);
        mockedMenuTaskBar = context.mock(MenuTaskBar.class);
        mockedBaseWindow = context.mock(BaseWindow.class);
        mockedContentPanel = context.mock(ContentPanel.class);
        mockedMenuItem = context.mock(Component.class);
        mockedRecord = context.mock(ModelData.class);
        mockedWindowLauncher = context.mock(AbstractWindowLauncher.class);
        mockedUrlInfo = context.mock(ToolBarURLChangeDataType.class);
        mockedMetaMenuItem = context.mock(MetaMenuItem.class);
        mockedGridDialogPresenter = context.mock(TwoColumnGridDialogPresenter.class);
        mockedPresenter = context.mock(IBaseWindowPresenter.class);
        expectedMenuItemId = null;

        context.checking(new Expectations() {
            {
                //                allowing(mockedEventGridView).getMenuTaskBar();
                //                will(returnValue(mockedMenuTaskBar));
                //
                //                allowing(mockedMenuTaskBar).getTabOwnerId();
                //                will(returnValue("tabID"));
                //
                //                allowing(mockedEventGridView).getViewSettings();
                //                will(returnValue(mockedMetaMenuItem));
                //
                //                allowing(mockedMetaMenuItem).getID();
                //                will(returnValue("winID"));

            }
        });

        objUnderTest = new StubbedWindowToolBarItemEvent();
    }

    @After
    public void tearDown() {
        objUnderTest = null;
    }

    @Test
    public void testSACEventDisplaysGridDialogWithSACMenuItem() {
        expectedMenuItemId = MetaReaderConstants.SAC_MENU_ITEM;
        expectRequestGridDataOnGridDialogPresenter();
        objUnderTest.handleToolBarEvent(mockedEventGridView, EventType.SAC, mockedEventBus, mockedMenuItem, null);

    }

    private void expectRequestGridDataOnGridDialogPresenter() {
        context.checking(new Expectations() {
            {
                allowing(mockedEventGridView).getGridRecordSelected();
                will(returnValue(mockedRecord));
                allowing(mockedGridDialogPresenter).requestGridData(mockedRecord);

                allowing(mockedEventGridView).getPresenter();
            }
        });

    }

    @Test
    public void testKPIWindowToFrontCalledWhenExistsAlready() {
        context.checking(new Expectations() {
            {

                allowing(mockedEventGridView).getPresenter();
                will(returnValue(mockedPresenter));
                one(mockedPresenter).getSearchData();
                will(returnValue(getDummySearchFieldDataType()));

                one(mockedPresenter).getTabOwnerId();

                one(mockedEventGridView).getWorkspaceController();
                will(returnValue(mockedMenuTaskBar));

                one(mockedMenuTaskBar).getWindow("KPIMULTI_IDENTIFER" + DUMMY_NODE_NAME);
                will(returnValue(mockedBaseWindow)); // has win already
                one(mockedBaseWindow).toFront();

            }
        });
        objUnderTest.handleToolBarEvent(mockedEventGridView, EventType.KPI, mockedEventBus, mockedMenuItem, null);

    }

    @Test
    public void fireChangeChartGridDisplayedIncludeTime() {

        context.checking(new Expectations() {
            {

                allowing(mockedEventGridView).getPresenter();
                exactly(3).of(mockedUrlInfo).setTempTimeInfoDataType(with(any(TimeInfoDataType.class)));

                exactly(3).of(mockedEventBus).fireEvent(with(any(ChangeChartGridEvent.class)));

            }
        });

        objUnderTest.handleToolBarEvent(mockedEventGridView, EventType.SUB_BI_BUSY_DAY, mockedEventBus, mockedMenuItem,
                mockedUrlInfo);

        objUnderTest.handleToolBarEvent(mockedEventGridView, EventType.SUB_BI_BUSY_HOUR, mockedEventBus,
                mockedMenuItem, mockedUrlInfo);
        objUnderTest.handleToolBarEvent(mockedEventGridView, EventType.SUB_BI_FAILED_EVENTS, mockedEventBus,
                mockedMenuItem, mockedUrlInfo);

    }

    SearchFieldDataType getDummySearchFieldDataType() {
        return new SearchFieldDataType(DUMMY_NODE_NAME, null, null, null, false, "", null, false);
    }

    class StubbedWindowToolBarItemEvent extends WindowToolBarItemEvent {

        private TimeInfoDataType testTimeData = TimeInfoDataType.DEFAULT;

       /* @Override
        MetaMenuItemDataType createKPIMetaMenuItemDataType(*//*final InstanceWindowType instanceWindowType*//*) {

            final String wsURL = "my KPI url";
            final String toolBarType = "KPI_TOOLBAR";
            final String toggleToolBarType = "TOOLBAR1";

            final String display = "line";
            final SearchFieldUser isSearchFieldUser = SearchFieldUser.TRUE;

            return new MetaMenuItemDataType.Builder().text(instanceWindowType.getDisplayText()).id(KPI_ANALYSIS_ID)
                    .url(wsURL).isSearchFieldUser(isSearchFieldUser).windowType(Type.CHART).display(display).type(
                            "IMSI").key("SUM").toolBarHandler(
                            new ToolBarStateManager(toolBarType, ToolBarStateManager.BottomToolbarType.PAGING,
                                    toggleToolBarType)).minimizedButtonName("Miminised name").build();
        }*/

        public void setTestTimeData(final TimeInfoDataType t) {
            this.testTimeData = t;
        }

       /* public AbstractWindowLauncher createMultiInstanceChartLauncher(final InstanceWindowType instanceType,
                final MetaMenuItem generatedMetaMenuItem, final EventBus bus, final MenuTaskBar menuTaskBar,
                final ContentPanel constrainArea, final IExtendedWidgetDisplay launchingGrid,
                final String launchButtonId) {

            return mockedWindowLauncher;
        }*/

        @Override
        TimeInfoDataType getViewRefTimeData() {
            return testTimeData;
        }

        /* (non-Javadoc)
         * @see com.ericsson.eniq.events.ui.client.events.handlers.WindowToolBarItemEvent#createMetaMenuItemDataType(java.lang.String)
         */
        @Override
        MetaMenuItem createMetaMenuItem(final String menuItemId) {
            assertThat(menuItemId, is(expectedMenuItemId));
            return null;
        }

        /* (non-Javadoc)
         * @see com.ericsson.eniq.events.ui.client.events.handlers.WindowToolBarItemEvent#createVerticalGridColumnHeaders(java.lang.String)
         */
        @Override
        VerticalGridColumnHeaders createVerticalGridColumnHeaders(final String menuItemId) {
            assertThat(menuItemId, is(expectedMenuItemId));
            return null;
        }

        /* (non-Javadoc)
         * @see com.ericsson.eniq.events.ui.client.events.handlers.WindowToolBarItemEvent#createGridDialogPresenter(com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType, com.ericsson.eniq.events.ui.client.datatype.VerticalGridColumnHeaders)
         */
        @Override
        TwoColumnGridDialogPresenter<IExtendedWidgetDisplay> createGridDialogPresenter(
                final MetaMenuItemDataType winData, final VerticalGridColumnHeaders keyValues) {
            return mockedGridDialogPresenter;
        }

    }

}
