/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.main;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.UIComponentFactory;
import com.ericsson.eniq.events.ui.client.datatype.TabInfoDataType;
import com.ericsson.eniq.events.ui.client.northpanel.NorthPanelPresenter;
import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabItem.HeaderItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.web.bindery.event.shared.EventBus;
import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author eemecoy
 * @author eeicmsy
 *
 */
public class MainPresenterTest extends TestEniqEventsUI {

    IMainView mockedMainDisplay;

    IEniqEventsModuleRegistry mockedModuleRegistry;

    public List<TabInfoDataType> tabInfoDataTypeList;

    private TabInfoDataType tabInfoDataType;

    TabItem mockedTabItem;

    TabPanel mockedContainerTab;

    GenericTabView mockedGenericTabView;

    ComponentEvent mockedComponentEvent;

    GenericTabPresenter mockedGenericTabPresenter;

    NorthPanelPresenter mockedNorthPanelPresenter;

    UIComponentFactory mockFactory;

    TabbedWorkspaceContainer tabbedWsContainer;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        setUpGeneralExpectationsOnBusWhenDontCareAboutTypes();
        mockedMainDisplay = context.mock(IMainView.class);
        mockedModuleRegistry = context.mock(IEniqEventsModuleRegistry.class);
        mockedContainerTab = context.mock(TabPanel.class);
        mockedComponentEvent = context.mock(ComponentEvent.class);
        mockFactory = context.mock(UIComponentFactory.class);
        mockedNorthPanelPresenter = context.mock(NorthPanelPresenter.class);
        mockedTabItem = context.mock(TabItem.class);
        mockedGenericTabView = context.mock(GenericTabView.class);
        mockedGenericTabPresenter = context.mock(GenericTabPresenter.class);
    }

    @After
    public void tearDown() {
        mockedMainDisplay = null;
        mockedContainerTab = null;
        mockedComponentEvent = null;
        mockFactory = null;
        mockedTabItem = null;
        mockedModuleRegistry = null;
        mockedGenericTabView = null;
        mockedGenericTabPresenter = null;
    }

    @Ignore
    @Test
    public void handleMetaDataReadyEventSetsUpCorrectTab() throws Exception {

        final String id = "id";
        final String name = "some name";
        final String tooltip = "a tooltip";
        final String style = "a style";
        final String tabItemCenterStyle = "center style";

        setUpMetaDataForTab(id, name, tooltip, style, tabItemCenterStyle);

        setUpMocksAndExpectationsOnTabItem(id, tooltip, tabItemCenterStyle);

        context.checking(new Expectations() {
            {

                one(mockedTabItem).setHideMode(HideMode.OFFSETS);
                allowing(mockedTabItem).setEnabled(with(any(Boolean.class)));
                exactly(2).of(mockedMainDisplay).getContainerTab();
                allowing(mockedContainerTab).findItem(with(any(String.class)), with(any(Boolean.class)));
                one(mockedContainerTab).setSelection(with(any(TabItem.class)));
                allowing(mockedContainerTab).add(with(any(TabItem.class)));

            }

        });

        context.checking(new Expectations() {
            {
                allowing(mockedMainDisplay).getContainerTab();
                will(returnValue(mockedContainerTab));
            }
        });

        context.checking(new Expectations() {
            {
                allowing(mockedModuleRegistry).containsModule("id");
            }
        });
        getTestObject().handleMetaDataReadyEvent();
    }

    @Ignore
    @Test
    public void testTabListenerCreatesGenericTabView() throws Exception {
        initWsContainer();

        final SelectionListener<TabPanelEvent> tabListener = tabbedWsContainer
                .createTabSelectionListener(mockedTabItem);

        context.checking(new Expectations() {
            {
                allowing(mockedTabItem).isEnabled();
                will(returnValue(true));

                allowing(mockedTabItem).getHeader();
                allowing(mockedTabItem).getId();

                allowing(mockFactory).createGenericTabView(with(any(String.class)), with(any(TabItem.class)));
                will(returnValue(mockedGenericTabView));

                allowing(mockedGenericTabView).init(with(any(String.class)), with(mockedTabItem));
                allowing(mockedGenericTabView).asWidget();
                will(returnValue(mockedGenericTabView));
                allowing(mockedGenericTabView).requiresMetaDataUpdate();
                allowing(mockedGenericTabView).getTabItem();
                //
                allowing(mockedGenericTabView).getDashBoardData();
                allowing(mockedGenericTabView).isDashBoard();
                //
                allowing(mockedGenericTabPresenter).checkAndLaunchDashBoard();
                allowing(mockedTabItem).add(mockedGenericTabView);
                allowing(mockedTabItem).layout();

            }
        });

        assertEquals("Visited tabs should be empty", 0, TabViewRegistry.get().getAllTabViews().size());

        tabListener.componentSelected(new TabPanelEvent(mockedContainerTab, mockedTabItem));
        assertEquals("Visited tabs should be increased", 1, TabViewRegistry.get().getAllTabViews().size());

        tabListener.componentSelected(new TabPanelEvent(mockedContainerTab, mockedTabItem));
        assertEquals("Visited tabs should be same for same id", 1, TabViewRegistry.get().getAllTabViews().size());
    }


    /////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    private void setUpMetaDataForTab(final String id, final String name, final String tooltip, final String style,
            final String tabItemCenterStyle) {
        tabInfoDataType = new TabInfoDataType(id, name, tooltip, style, tabItemCenterStyle, true, true);
        tabInfoDataTypeList = new ArrayList<TabInfoDataType>();
        tabInfoDataTypeList.add(tabInfoDataType);
    }

    private void setUpMocksAndExpectationsOnTabItem(final String id, final String tooltip,
            final String tabItemCenterStyle) {
        final HeaderItem mockedHeader = context.mock(HeaderItem.class);
        context.checking(new Expectations() {
            {
                one(mockedTabItem).setId(id);
                allowing(mockedTabItem).getHeader();
                will(returnValue(mockedHeader));
                one(mockedHeader).setToolTip(tooltip);
                one(mockedTabItem).setStyleName(tabItemCenterStyle);
                one(mockedTabItem).addListener(with(any(EventType.class)), with(any(Listener.class)));
            }
        });

    }

    class StubbedMainPresenter extends MainPresenter {

        boolean isOpenWindowsExist;

        boolean stubIsDialogDisplayed;

        public void stubSetOpenWindowsExist(final boolean aFlag) {
            this.isOpenWindowsExist = aFlag;
        }

        public StubbedMainPresenter(final IMainView mainDisplay, final EventBus eventBus,
                final TabbedWorkspaceContainer tabbedWsContainer) {
            super(mainDisplay, eventBus, mockFactory, mockedModuleRegistry, mockedNorthPanelPresenter);
        }

        @Override
        protected void loadMetaDataFromServer() {
            // avoiding URL server contact
        }

        @Override
        List<TabInfoDataType> getTabDataMetaInfoFromMetaReader() {
            return tabInfoDataTypeList;
        }

        @Override
        boolean openSearchFieldWindowsExist() {
            return isOpenWindowsExist;
        }
    }

    private MainPresenter getTestObject() {
        initWsContainer();
        return new StubbedMainPresenter(mockedMainDisplay, mockedEventBus, tabbedWsContainer);
    }

    private void initWsContainer() {
        tabbedWsContainer = new TabbedWorkspaceContainer(null, mockedMainDisplay.getContainerTab(), mockFactory,
                mockedEventBus) {
            @Override
            TabItem createTabItem(final TabInfoDataType tabInfo) {
                return mockedTabItem;
            }

            @Override
            SelectionListener<TabPanelEvent> createTabSelectionListener(final TabItem tabItem) {
                return new SetupTabSectionListener(tabItem, moduleRegistry, componentFactory, eventBus,
                        currentTabPresenters, tabStyleMap) {
                    @Override
                    GenericTabPresenter createGenericTabPresenter(final GenericTabView genericView) {
                        return mockedGenericTabPresenter;
                    }
                };
            }
        };
    }

}
