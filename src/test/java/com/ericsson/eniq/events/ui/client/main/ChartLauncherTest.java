/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.main;

import org.junit.Ignore;
import org.junit.Ignore;
import org.junit.Ignore;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.charts.window.ChartWindowPresenter;
import com.ericsson.eniq.events.ui.client.charts.window.ChartWindowView;
import com.ericsson.eniq.events.ui.client.charts.window.IChartWindowView;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.ToolBarStateManager;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.common.comp.MenuTaskBar;
import com.ericsson.eniq.events.ui.client.common.comp.WindowState;
import com.ericsson.eniq.events.ui.client.common.widget.AbstractBaseWindowDisplay;
import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldUser;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.web.bindery.event.shared.EventBus;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

/**
 * @author eeicmsy
 *
 */
@Ignore
public class ChartLauncherTest extends TestEniqEventsUI {

    ContentPanel mockedContaining;

    MenuTaskBar mockedMenuTaskBar;

    ChartWindowView mockedChartView;

    ChartWindowPresenter mockedChartPresenter;

    @Before
    public void setUp() {
        mockedContaining = context.mock(ContentPanel.class);
        mockedMenuTaskBar = context.mock(MenuTaskBar.class);
        mockedChartView = context.mock(ChartWindowView.class);
        mockedChartPresenter = context.mock(ChartWindowPresenter.class);

    }

    @Test
    public void canCreateChartLauncher() {
        new ChartLauncher(getAnyMenuItem(), mockedEventBus, mockedContaining, mockedMenuTaskBar);
    }

    @Test
    public void selectingGridLaunchMenuItemsCreatesGridView() {

        /* if these expectations happen - we pass */
        setUpGeneralExpectationsOnBusWhenDontCareAboutTypes();
        setUpExpectationsForEventGridAddedToWindow();

        final StubbedChartLauncher objToTest = createStubObjectToTest();
        final MenuEvent fakeEvent = new MenuEvent(getAnyMenuItem().getContextMenu());

        objToTest.componentSelected(fakeEvent);

    }

    @Test
    public void selectingGridLaunchMenuItemsCreatesRankingGridView() {

        /* if these expectations happen - we pass */
        setUpGeneralExpectationsOnBusWhenDontCareAboutTypes();
        setUpExpectationsForEventGridAddedToRankingWindow();

        final StubbedChartLauncher objToTest = createStubObjectToTestRanking();
        final MenuEvent fakeEvent = new MenuEvent(getAnyMenuItemForRanking().getContextMenu());

        objToTest.componentSelected(fakeEvent);

    }

    private void setUpExpectationsForEventGridAddedToWindow() {
        context.checking(new Expectations() {
            {
                one(mockedChartView).addLaunchButton();

                one(mockedMenuTaskBar).getTabOwnerId();

            }
        });
    }

    private void setUpExpectationsForEventGridAddedToRankingWindow() {
        context.checking(new Expectations() {
            {

                one(mockedChartView).addLaunchButton();

                one(mockedMenuTaskBar).getTabOwnerId();

            }
        });
    }

    private MetaMenuItem getAnyMenuItem() {
        return new MetaMenuItem(new MetaMenuItemDataType.Builder()
                .text("Cause Code Analysis")
                .id("someWinId")
                .url("RestTest/WHATEVER")
                .windowType(MetaMenuItemDataType.Type.GRID)
                .display("grid")
                .type("IMSI")
                .key("SUM")
                .toolBarHandler(
                        new ToolBarStateManager("TOOLBAR2", ToolBarStateManager.BottomToolbarType.PAGING, "TOOLBAR1"))
                .maxRowsParam("MAXROWSPARAM").minimizedButtonName("Miminised name")
                .isSearchFieldUser(SearchFieldUser.TRUE).build());
    }

    private MetaMenuItem getAnyMenuItemForRanking() {

        return new MetaMenuItem(new MetaMenuItemDataType.Builder()
                .text("eNodeB Ranking")
                .id("NETWORK_RAN_LTE_CFA_ENODEB_RANKING")
                .url("RestTest/WHATEVER")
                .windowType(MetaMenuItemDataType.Type.GRID)
                .display("grid")
                .type("BSC")
                .toolBarHandler(
                        new ToolBarStateManager("TOOLBAR2", ToolBarStateManager.BottomToolbarType.PAGING, "TOOLBAR1"))
                .maxRowsParam("MAXROWSPARAM").minimizedButtonName("Miminised name")
                .isSearchFieldUser(SearchFieldUser.FALSE).build());
    }

    private StubbedChartLauncher createStubObjectToTest() {
        return new StubbedChartLauncher(getAnyMenuItem(), mockedEventBus, mockedContaining, mockedMenuTaskBar);

    }

    private StubbedChartLauncher createStubObjectToTestRanking() {
        return new StubbedChartLauncher(getAnyMenuItemForRanking(), mockedEventBus, mockedContaining, mockedMenuTaskBar);

    }

    private class StubbedChartLauncher extends ChartLauncher {

        /**
         * @param item
         * @param eventBus
         * @param containingPanel
         * @param menuTaskBar
         */
        public StubbedChartLauncher(final MetaMenuItem item, final EventBus eventBus,
                final ContentPanel containingPanel, final MenuTaskBar menuTaskBar) {
            super(item, eventBus, containingPanel, menuTaskBar);
        }

        @Override
        public AbstractBaseWindowDisplay createView(final MultipleInstanceWinId searchFieldData,
                final WindowState windowState) {
            return mockedChartView;
        }

        @Override
        public BaseWindowPresenter<IChartWindowView> createPresenter(final AbstractBaseWindowDisplay view,
                MultipleInstanceWinId winId) {
            return mockedChartPresenter;
        }

    }

}
