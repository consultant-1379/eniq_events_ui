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

package com.ericsson.eniq.events.ui.client.wcdmauertt;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.web.bindery.event.shared.EventBus;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.widget.EniqWindow;
import com.ericsson.eniq.events.ui.client.grid.GridPagingToolBar;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class EventGridPresenterUerttTest extends TestEniqEventsUI{

    private StubbedEventGridPresenterUertt objUnderTest;
    JSONValue mockedJsonValue;
    EventGridViewUertt mockedView;
    GridPagingToolBar mockGridPagingToolbar;

    @Before
    public void setUp() throws Exception {
        mockedJsonValue = context.mock(JSONValue.class);
        mockGridPagingToolbar = context.mock(GridPagingToolBar.class);
        mockedView = context.mock(EventGridViewUertt.class);
        objUnderTest = new StubbedEventGridPresenterUertt(mockedView, mockedEventBus);
    }

    @Test
    public void testHandleUerttResponse() {
        setUpExpectationsOnPresenterClassComponents();
        objUnderTest.setUpExpectationsOnDerivedPresenterClassComponents();
        objUnderTest.handleUerttResponse(mockedJsonValue);
    }

    private void setUpExpectationsOnPresenterClassComponents() {
        context.checking(new Expectations() {
            {
                allowing(mockedView).setEventCacheUertt(with(any(EventCacheUertt.class)));
                allowing(mockedView).getEventCacheUertt();
                allowing(mockedView).init();
                allowing(mockedView).getGridView();
                allowing(mockedView).getGridView().addWidget(with(any(Widget.class)));
                allowing(mockedView).setActivePage(with(any(Integer.class)));
                allowing(mockedView).addBlankRowAfterNodes(with(any(Integer.class)));
                allowing(mockedView).addEventWithDirectionInformaton(with(any(Integer.class)), with(any(String.class)), with(any(String.class)), with(any(String.class)), with(any(String.class)));
                allowing(mockedJsonValue).isObject().get("data").isArray().size();
                allowing(mockedView).getJsonGrid().getBottomToolbar();
                allowing(mockedView).getDisplayTableHeader();
                allowing(mockedView).getDisplayTable();
                allowing(mockedView).setEniqWindow(with(any(EniqWindow.class)));
            }
        });
    }

    class StubbedEventGridPresenterUertt extends EventGridPresenterUertt {

        protected Mockery context = new JUnit4Mockery();

        {
            // we need to mock classes, not just interfaces.
            context.setImposteriser(ClassImposteriser.INSTANCE);
        }
        GridPagingToolBar gridPagingToolbar = context.mock(GridPagingToolBar.class);
        SplitLayoutPanel splitLayoutPanel = context.mock(SplitLayoutPanel.class);
        ScrollPanel scrollPanel = context.mock(ScrollPanel.class);
        private void setUpExpectationsOnDerivedPresenterClassComponents() {
            context.checking(new Expectations() {
                {
                    allowing(gridPagingToolbar).setActivePage(with(any(Integer.class)));
                    allowing(splitLayoutPanel).addNorth(with(any(Widget.class)), with(any(Integer.class)));
                    allowing(splitLayoutPanel).add(with(any(Widget.class)));
                    allowing(splitLayoutPanel).setWidth(with(any(String.class)));
                    allowing(splitLayoutPanel).setWidgetSize(with(any(Widget.class)), with(any(Integer.class)));
                    allowing(splitLayoutPanel).asWidget();
                    allowing(scrollPanel).setWidth(with(any(String.class)));
                }
            });
        }

        public StubbedEventGridPresenterUertt(final EventGridViewUertt view, final EventBus eventBus) {
            super(view, eventBus);
            this.setGridPagingToolbar(gridPagingToolbar);
        }

        @Override
        protected void addEventsToEventCache(final JSONValue responseValue, final EventCacheUertt eventCacheUertt) {
            final int eventCount = responseValue.isObject().get("data").isArray().size();
            EventPojo eventPojo = new EventPojo("TIMESTAMP", "1234567890", "EVENT_PROTOCOL_RRC", "SENT");
            for(int i = 0; i < 600; i++)
            {
                eventCacheUertt.addToEventList(eventPojo);
            }
        }

        @Override
        public GridPagingToolBar getGridPagingToolbar() {
            return gridPagingToolbar;
        }

        @Override
        protected SplitLayoutPanel createSplitLayoutPanel()
        {
            return splitLayoutPanel;
        }

        @Override
        protected ScrollPanel createScrollPanel(FlexTable flexTable)
        {
            return scrollPanel;
        }

        @Override
        protected void addCachedEventsToDisplay(final EventCacheUertt eventCacheUertt, final EventGridViewUertt eventGridViewUertt) {
            int rowCount = 1;
            int i = 2;
            int activePage = (i < 1) ? 1 : i;
            for (final EventPojo eventPojoData : eventCacheUertt.getSubSetElement(activePage)) {
                rowCount = addEventsToDisplay(mockedView, eventPojoData, rowCount, activePage);
            }
        }
    }
}
