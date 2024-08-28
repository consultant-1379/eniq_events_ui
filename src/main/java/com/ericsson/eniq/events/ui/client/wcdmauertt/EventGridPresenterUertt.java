/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 ******************************************************************************* */
package com.ericsson.eniq.events.ui.client.wcdmauertt;

import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.ui.client.common.widget.EniqWindow;
import com.ericsson.eniq.events.ui.client.grid.GridPagingToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class EventGridPresenterUertt extends BasePresenter<EventGridViewUertt>{

    private static final String TIMESTAMP = "TIMESTAMP";
    private static final String DIRECTION = "DIRECTION";
    private static final String EVENT_ID = "EVENT_NAME";
    private static final String PROTOCOL_ID = "PROTOCOL_NAME";
    private static final String DATA_TAG = "data";
    private static final String UERTT_TAG = "WCDMA-UERTT";
    private static final int WIDTH_OFFSET = 355;
    private static final int HEIGHT_OFFSET = 60;
    private GridPagingToolBar gridPagingToolbar;

    public GridPagingToolBar getGridPagingToolbar() {
        return ((GridPagingToolBar) getView().getJsonGrid().getBottomToolbar());
    }

    public void setGridPagingToolbar(GridPagingToolBar gridPagingToolbar) {
        this.gridPagingToolbar = gridPagingToolbar;
    }

    public EventGridPresenterUertt(EventGridViewUertt view, EventBus eventBus) {
        super(view, eventBus);
    }

    protected SplitLayoutPanel createSplitLayoutPanel()
    {
        return new SplitLayoutPanel(0);
    }

    protected ScrollPanel createScrollPanel(FlexTable flexTable)
    {
        return new ScrollPanel(flexTable);
    }

    public void handleUerttResponse(final JSONValue responseValue) {
        final EventCacheUertt eventCacheUertt = new EventCacheUertt();
        getView().setEventCacheUertt(eventCacheUertt);
        getView().getEventCacheUertt().setResponseEventRawData(responseValue);
        addEventsToEventCache(responseValue, eventCacheUertt);
        getView().setEniqWindow((EniqWindow) getView().getGridView().getWindow());
        resetActivePageForRefresh();
        final int eventCount = responseValue.isObject().get(DATA_TAG).isArray().size();
        if (eventCount > 0)
        {
            getView().init();
            addCachedEventsToDisplay(eventCacheUertt, getView());
        }
        clearPreviousData((EniqWindow) getView().getGridView().getWindow());
        changeWindowSizeAndPosition((EniqWindow) getView().getGridView().getWindow());
        if(eventCount > 0)
        {
            getView().getGridView().addWidget(getWidget(eventCacheUertt,getView()));
            createPaging(eventCacheUertt, getView());
        }

    }

    private void changeWindowSizeAndPosition(EniqWindow window) {
        if(window.isMaximized() == false)
        {
            int WINDOW_WIDTH = window.getContentPanel().getOffsetWidth() - (window.getContentPanel().getOffsetWidth() / 2) + WIDTH_OFFSET;
            int WINDOW_HEIGHT = window.getContentPanel().getOffsetHeight() - (window.getContentPanel().getOffsetHeight() / 2) + HEIGHT_OFFSET;
            window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        }
        window.setResizable(false);
        window.fitIntoContainer();
         final int newX, newY;
        if ((window.getPosition(true).x + 40) + window.getOffsetWidth() > window.getContentPanel().getInnerWidth())
        {
            if((window.getPosition(true).y + 40) + window.getOffsetHeight() > window.getContentPanel().getInnerHeight())
            {
                newX = window.getContentPanel().getInnerWidth() - window.getOffsetWidth();
                newY = window.getContentPanel().getInnerHeight() - window.getOffsetHeight();
                window.setPosition(newX, newY);
            }
            else
            {
                newX = window.getContentPanel().getInnerWidth() - window.getOffsetWidth();
                newY = window.getPosition(true).y;
                window.setPosition(newX, newY);
            }
        }
        else if ((window.getPosition(true).y + 40) + window.getOffsetHeight() > window.getContentPanel().getInnerHeight())
        {
            if((window.getPosition(true).x + 40) + window.getOffsetWidth() > window.getContentPanel().getInnerWidth())
            {
                newX = window.getContentPanel().getInnerWidth() - window.getOffsetWidth();
                newY = window.getContentPanel().getInnerHeight() - window.getOffsetHeight();
                window.setPosition(newX, newY);
            }
            else
            {
                newX = window.getPosition(true).x;
                newY = window.getContentPanel().getInnerHeight() - window.getOffsetHeight();
                window.setPosition(newX, newY);
            }
        }
    }

    private void resetActivePageForRefresh() {
        setGridPagingToolbar(gridPagingToolbar);
        gridPagingToolbar = getGridPagingToolbar();
        gridPagingToolbar.setActivePage(1);
    }

    protected void addEventsToEventCache(final JSONValue responseValue, final EventCacheUertt eventCacheUertt) {
        final int eventCount = responseValue.isObject().get(DATA_TAG).isArray().size();
        if(eventCount > 0)
        {
            getView().init();
        }
        for (int i = 0; i < eventCount; i++) {
            eventCacheUertt.addToEventList(getEventPOJOFromJson(responseValue, i));
        }
    }

    protected void addCachedEventsToDisplay(final EventCacheUertt eventCacheUertt, final EventGridViewUertt eventGridViewUertt) {
        int rowCount = 1;
        int i = ((GridPagingToolBar) eventGridViewUertt.getJsonGrid().getBottomToolbar()).getActivePage();
        int activePage = (i < 1) ? 1 : i;
        for (final EventPojo eventPojoData : eventCacheUertt.getSubSetElement(activePage)) {
            rowCount = addEventsToDisplay(eventGridViewUertt, eventPojoData, rowCount, activePage);
        }
    }

    private int getActivePage() {
        int i = ((GridPagingToolBar) getView().getJsonGrid().getBottomToolbar()).getActivePage();
        int activePage = (i < 1) ? 1 : i;
        return activePage;
    }

    private void clearPreviousData(EniqWindow window) {
        int j = window.getItemCount();
        if (j == 1) {
            window.getItem(0).removeFromParent();
        }
    }

    private void createPaging(final EventCacheUertt eventCacheUertt, final EventGridViewUertt eventGridViewUertt) {
        if (eventGridViewUertt.getJsonGrid().getBottomToolbar() instanceof GridPagingToolBar) {
            ClickHandler clickHandlerNext = new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    clickHandlerAction(eventCacheUertt, eventGridViewUertt);
                }
            };
            ClickHandler clickHandlerPrev = new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    clickHandlerAction(eventCacheUertt, eventGridViewUertt);
                }
            };
            ((GridPagingToolBar) eventGridViewUertt.getJsonGrid().getBottomToolbar()).getNext().addHandler(clickHandlerNext, ClickEvent.getType());
            ((GridPagingToolBar) eventGridViewUertt.getJsonGrid().getBottomToolbar()).getPrev().addHandler(clickHandlerPrev, ClickEvent.getType());
        }
    }

    private void clickHandlerAction(final EventCacheUertt eventCacheUertt, final EventGridViewUertt eventGridViewUertt) {
        int activePage = ((GridPagingToolBar) eventGridViewUertt.getJsonGrid().getBottomToolbar()).getActivePage();
        int rowCount = 1;
        eventGridViewUertt.init();
        for (final EventPojo eventPojoData : eventCacheUertt.getSubSetElement(activePage)) {
            rowCount = addEventsToDisplay(eventGridViewUertt, eventPojoData, rowCount, activePage);
        }
        clearPreviousData((EniqWindow) getView().getGridView().getWindow());
        getView().getGridView().addWidget(getWidget(eventCacheUertt, eventGridViewUertt));
    }

    protected int addEventsToDisplay(final EventGridViewUertt eventGridViewUertt, final EventPojo eventPojoData, int rowCount, int activePage) {
        eventGridViewUertt.setActivePage(activePage);
        rowCount = eventGridViewUertt.addBlankRowAfterNodes(rowCount);
        rowCount = eventGridViewUertt.addEventWithDirectionInformaton(rowCount, eventPojoData.getDirection(), eventPojoData.getProtocol_id(),
                eventPojoData.getEvent_id(), eventPojoData.getTimestamp());
        rowCount = eventGridViewUertt.addBlankRowAfterNodes(rowCount);
        return rowCount;
    }

    protected EventPojo getEventPOJOFromJson(final JSONValue responseValue, final int jsonIndex) {
        final String protocolId = getSpecifiedTagFromJson(responseValue, jsonIndex, PROTOCOL_ID);
        final String eventId = getSpecifiedTagFromJson(responseValue, jsonIndex, EVENT_ID);
        final String direction = getSpecifiedTagFromJson(responseValue, jsonIndex, DIRECTION);
        final String timestamp = getSpecifiedTagFromJson(responseValue, jsonIndex, TIMESTAMP);

        return new EventPojo(timestamp, eventId, protocolId, direction);
    }

    private String getSpecifiedTagFromJson(final JSONValue responseValue, final int jsonIndex, final String field) {

        final String string = responseValue.isObject().get(DATA_TAG).isArray().get(jsonIndex).isObject().get(field).toString();
        return removeDoubleQuotes(string);
    }

    private String removeDoubleQuotes(final String string) {
        return string.substring(1, string.length() - 1);
    }

    private Widget getWidget(final EventCacheUertt eventCacheUertt, final EventGridViewUertt eventGridViewUertt) {
        
        SplitLayoutPanel split = createSplitLayoutPanel();
        split.addNorth(eventGridViewUertt.getDisplayTableHeader(), 50);
        final ScrollPanel scrollPanel = createScrollPanel(eventGridViewUertt.getDisplayTable());
        scrollPanel.setWidth("100%");
        split.add(scrollPanel);
        split.setWidth("100%");
        split.setWidgetSize(eventGridViewUertt.getDisplayTableHeader(), 50);
        final Widget asWidget = split.asWidget();
        return asWidget;
    }
}
