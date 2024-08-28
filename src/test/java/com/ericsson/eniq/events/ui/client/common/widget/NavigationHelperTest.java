/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.comp.BreadCrumbMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.datatype.IHyperLinkDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.grid.JSONGrid;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author esuslyn
 *
 */
public class NavigationHelperTest extends TestEniqEventsUI {

    private NavigationHelper objToTest;

    IExtendedWidgetDisplay mockIExtendedWidgetDisplay;

    private BreadCrumbMenuItem stubBreadCrumbItem;

    private IBaseWindowPresenter mockedPresenter;

    JSONGrid mockedGrid;

    TimeInfoDataType mockedTime;

    @Before
    public void setUp() {

        mockIExtendedWidgetDisplay = context.mock(IExtendedWidgetDisplay.class);
        mockedPresenter = context.mock(IBaseWindowPresenter.class);
        stubBreadCrumbItem = new StubbedBreadCrumbMenuItem("", 0, null, null);
        mockedGrid = context.mock(JSONGrid.class);
        mockedTime = context.mock(TimeInfoDataType.class);
        objToTest = new StubbedNavigationHelper(mockIExtendedWidgetDisplay, mockedEventBus);
    }

    @After
    public void tearDown() {
        objToTest = null;
    }

    @Test
    public void constructNavigationHelper() {
        new NavigationHelper(mockIExtendedWidgetDisplay, mockedEventBus);
    }

    @Test
    public void displayItemCallsToChangeURLAndResetsSearchDataWhenRequested() {

        context.checking(new Expectations() {
            {
                allowing(mockIExtendedWidgetDisplay).getPresenter();
                will(returnValue(mockedPresenter));

                one(mockedGrid).setColumns(null);
                allowing(mockedGrid).setData(null);
                one(mockedGrid).setupStateful("", "");
                one(mockIExtendedWidgetDisplay).updateTime(null);
                allowing(mockIExtendedWidgetDisplay).getTimeData();
                will(returnValue(mockedTime));
                one(mockIExtendedWidgetDisplay).updateLastRefreshedTimeStamp(null);
                one(mockedGrid).bind();

                one(mockedGrid).asWidget();
                one(mockedGrid).getColumnModel();
                one(mockedGrid).reconfigureGrid();
                one(mockIExtendedWidgetDisplay).addWidget(with(any(Component.class)));

                one(mockedPresenter).resetSearchData(with(any(SearchFieldDataType.class)));
                one(mockedPresenter).setWsURL(with(any(String.class)));
            }
        });
        objToTest.displayItem(stubBreadCrumbItem, true);

    }

    private class StubbedBreadCrumbMenuItem extends BreadCrumbMenuItem {

        /**
         * @param title
         * @param index
         * @param winID
         * @param drillInfo
         */
        public StubbedBreadCrumbMenuItem(final String title, final int index, final String winID,
                final IHyperLinkDataType drillInfo) {
            super(title, index, winID, drillInfo);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void setIconStyle(final String icon) {

        }

    }

    private class StubbedNavigationHelper extends NavigationHelper {

        /**
         * @param display
         * @param eventBus
         */
        public StubbedNavigationHelper(final IExtendedWidgetDisplay display, final EventBus eventBus) {
            super(display, eventBus);
            // TODO Auto-generated constructor stub
        }

        @Override
        Component getItemCurrentlyDisplayed(final Menu menu) {
            menu.add(new BreadCrumbMenuItem("Menu 1", 0, "", null));
            return menu.getItem(0);

        }

        @Override
        JSONGrid getDisplayedGrid() {
            return mockedGrid;
        }

        @Override
        Component getWindowToolbarButton(final String buttonID) {
            return new BoxComponent();
        }

        @Override
        Menu getBreadCrumbMenu(final Component menuItem) {
            return new Menu();
        }
    }
}
