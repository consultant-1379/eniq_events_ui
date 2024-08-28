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
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.ToolBarStateManager;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.common.comp.MenuTaskBar;
import com.ericsson.eniq.events.ui.client.common.comp.WindowState;
import com.ericsson.eniq.events.ui.client.common.widget.AbstractBaseWindowDisplay;
import com.ericsson.eniq.events.ui.client.common.widget.EventGridPresenter;
import com.ericsson.eniq.events.ui.client.common.widget.EventGridView;
import com.ericsson.eniq.events.ui.client.common.widget.IEventGridView;
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
 * @since March 2010
 *
 */
@Ignore
public class GridLauncherTest extends TestEniqEventsUI {

    ContentPanel mockedContaining;

    MenuTaskBar mockedMenuTaskBar;

    EventGridView mockedEventGridView;

    EventGridPresenter mockedEventGridPresenter;

    @Before
    public void setUp() {
        mockedContaining = context.mock(ContentPanel.class);
        mockedMenuTaskBar = context.mock(MenuTaskBar.class);
        mockedEventGridView = context.mock(EventGridView.class);
        mockedEventGridPresenter = context.mock(EventGridPresenter.class);

    }

    @Test
    public void canCreateGridLauncher() {
        new GridLauncher(getAnyMenuItem(), mockedEventBus, mockedContaining, mockedMenuTaskBar);
    }

    @Test
    public void selectingGridLaunchMenuItemsCreatesGridView() {

        /* if these expectations happen - we pass */
        setUpGeneralExpectationsOnBusWhenDontCareAboutTypes();
        setUpExpectationsForEventGridAddedToWindow();

        final GridLauncher objToTest = createStubObjectToTest();
        final MenuEvent fakeEvent = new MenuEvent(getAnyMenuItem().getContextMenu());

        objToTest.componentSelected(fakeEvent);

    }

    @Test
    public void selectingGridLaunchMenuItemsCreatesRankingGridView() {

        /* if these expectations happen - we pass */
        setUpGeneralExpectationsOnBusWhenDontCareAboutTypes();

        setUpExpectationsForEventGridAddedToRankingWindow();

        final GridLauncher objToTest = createStubObjectToTestRanking();
        final MenuEvent fakeEvent = new MenuEvent(getAnyMenuItemForRanking().getContextMenu());

        objToTest.componentSelected(fakeEvent);

    }

    private void setUpExpectationsForEventGridAddedToWindow() {
        context.checking(new Expectations() {
            {
                //                one(mockedEventGridView).setPresenter(mockedEventGridPresenter);
                //                one(mockedEventGridView).asWidget();
                //                will(returnValue(mockedEventGridView));
                //                one(mockedContaining).add(mockedEventGridView);
                //                one(mockedContaining).layout();
                //one(mockedMenuTaskBar).getSearchComponentValue();
                //                one(mockedEventGridPresenter).initWindow(with(any(MetaMenuItem.class)),
                //                        with(any(SearchFieldDataType.class)), with(any(Response.class)),
                //                        with(any(TimeInfoDataType.class)), with(any(String.class)), with(any(Boolean.class)));
                one(mockedEventGridView).addLaunchButton();
                //                one(mockedEventGridView).toFront();
                //                one(mockedEventGridView).fitIntoContainer();
                one(mockedMenuTaskBar).getTabOwnerId();

            }
        });
    }

    private void setUpExpectationsForEventGridAddedToRankingWindow() {
        context.checking(new Expectations() {
            {
                //                one(mockedEventGridView).setPresenter(mockedEventGridPresenter);
                //                one(mockedEventGridView).asWidget();
                //                will(returnValue(mockedEventGridView));
                //                one(mockedContaining).add(mockedEventGridView);
                //                one(mockedContaining).layout();
                //                one(mockedMenuTaskBar).getSearchComponentValue();
                //                one(mockedEventGridPresenter).initWindow(with(any(MetaMenuItem.class)),
                //                        with(any(SearchFieldDataType.class)), with(any(Response.class)),
                //                        with(any(TimeInfoDataType.class)), with(any(String.class)), with(any(Boolean.class)));
                one(mockedEventGridView).addLaunchButton();
                //                one(mockedEventGridView).toFront();
                //                one(mockedEventGridView).fitIntoContainer();
                one(mockedMenuTaskBar).getTabOwnerId();

            }
        });
    }

    private MetaMenuItem getAnyMenuItem() {

        return new MetaMenuItem(new MetaMenuItemDataType.Builder()
                .text("Cause Code Analysis")
                .id("NETWORK_CAUSE_CODE_ANALYSIS")
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

    private StubbedGenericMenuActionGridLaunch createStubObjectToTest() {
        return new StubbedGenericMenuActionGridLaunch(getAnyMenuItem(), mockedEventBus, mockedContaining,
                mockedMenuTaskBar);

    }

    private StubbedGenericMenuActionGridLaunch createStubObjectToTestRanking() {
        return new StubbedGenericMenuActionGridLaunch(getAnyMenuItemForRanking(), mockedEventBus, mockedContaining,
                mockedMenuTaskBar);

    }

    private class StubbedGenericMenuActionGridLaunch extends GridLauncher {

        /**
         * @param item
         * @param eventBus
         * @param containingPanel
         * @param menuTaskBar
         */
        public StubbedGenericMenuActionGridLaunch(final MetaMenuItem item, final EventBus eventBus,
                final ContentPanel containingPanel, final MenuTaskBar menuTaskBar) {
            super(item, eventBus, containingPanel, menuTaskBar);
        }

        @Override
        public AbstractBaseWindowDisplay createView(final MultipleInstanceWinId searchFieldData,
                final WindowState windowState) {
            return mockedEventGridView;
        }

        @Override
        public BaseWindowPresenter<IEventGridView> createPresenter(final AbstractBaseWindowDisplay view,
                MultipleInstanceWinId winId) {
            return mockedEventGridPresenter;
        }

    }

}
