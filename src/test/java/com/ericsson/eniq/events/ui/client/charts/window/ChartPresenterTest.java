/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.charts.window;

import static junit.framework.Assert.*;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ericsson.eniq.events.common.client.datatype.ChartDataType;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.charts.IChartElementDrillDownListener;
import com.ericsson.eniq.events.ui.client.charts.IChartPresenter;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.BaseToolBar;
import com.ericsson.eniq.events.ui.client.datatype.ButtonEnableParametersDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.events.HideShowChartElementEventHandler;
import com.ericsson.eniq.events.ui.client.events.SucessResponseEventHandler;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author eeicmsy
 *
 */
public class ChartPresenterTest extends TestEniqEventsUI {

    ChartWindowPresenter objectToTest;

    IChartPresenter mockedJSONChart;

    ChartWindowView mockedView;

    Response mockedResponse;

    JSONValue mockedJSONValue;

    MetaMenuItem mockedMetaMenuItem;

    BaseToolBar mockedBaseToolBar;

    Button mockedButton;

    private final String tabId = "tabId";

    private IChartElementDrillDownListener mockedChartElementDrillDownListener;

    @Before
    public void setUp() {
        StubbedChartPresenter.exportInterface = false;
        mockedView = context.mock(ChartWindowView.class);
        mockedJSONChart = context.mock(IChartPresenter.class);
        mockedResponse = context.mock(Response.class);
        mockedJSONValue = context.mock(JSONValue.class);
        mockedMetaMenuItem = context.mock(MetaMenuItem.class);
        mockedBaseToolBar = context.mock(BaseToolBar.class);

        mockedButton = context.mock(Button.class);

        mockedChartElementDrillDownListener = context.mock(IChartElementDrillDownListener.class);

        context.checking(new Expectations() {
            {

                one(mockedJSONChart).addChartDrillDownListener(with(any(IChartElementDrillDownListener.class)));

                one(mockedEventBus).addHandler(with(any(Type.class)), with(any(SucessResponseEventHandler.class)));
                one(mockedEventBus)
                        .addHandler(with(any(Type.class)), with(any(HideShowChartElementEventHandler.class)));

                allowing(mockedEventBus).addHandler(with(any(Type.class)), with(any(EventHandler.class)));

                one(mockedView).updateTimeFromPresenter(with(any(TimeInfoDataType.class)));

            }
        });

        objectToTest = new StubbedChartPresenter(mockedView, mockedEventBus);

    }

    @After
    public void tearDown() {
        StubbedChartPresenter.exportInterface = true;
        objectToTest = null;
    }

    @Test
    @Ignore
    public void serverResponseUpDatesChartWithOutAddingWidgit() throws Exception {

        context.checking(new Expectations() {
            {

                one(mockedMetaMenuItem).isSearchFieldUser();

                //                allowing(mockedMetaMenuItem).isEmptyAndFullSearchFieldUser();
                one(mockedMetaMenuItem).getID();
                one(mockedMetaMenuItem).getWizardId();
                //
                //     allowing(mockedView).getChartControl();
                //    will(returnValue(mockedJSONChart));
                //
                one(mockedJSONChart).setConfigData(with(any((ChartDataType.class))));
                exactly(2).of(mockedJSONChart).updateData(with(any(JSONValue.class)));
                exactly(2).of(mockedJSONChart).getChartRowCount();
                one(mockedJSONChart).asWidget();
                one(mockedView).addWidget(with(any(Widget.class)));
                exactly(2).of(mockedView).stopProcessing();
                //
                exactly(2).of(mockedResponse).getText();
                //
                exactly(2).of(mockedMetaMenuItem).setWidgetSpecificParams("");

                one(mockedChartElementDrillDownListener).setEventId(with(any(String.class)));
                one(mockedView).getBaseWindowID();
                allowing(mockedView).getBaseWindowTitleWithoutParams();
            }
        });

        objectToTest.initializeWidgit("queryid");
        /* Note 2 calls */
        objectToTest.handleSuccessResponse(mockedResponse);
        objectToTest.handleSuccessResponse(mockedResponse);
    }

    @Test
    public void cleanUpWindowForCancelRequest() throws Exception {

        context.checking(new Expectations() {
            {
                one(mockedMetaMenuItem).setWidgetSpecificParams("");

            }
        });
        objectToTest.cleanUpWindowForCancelRequest();

    }

    @Test
    public void getButtonEnableParameters() throws Exception {

        context.checking(new Expectations() {
            {
                //      one(mockedMetaMenuItem).setWidgetSpecificParams("");

            }
        });
        final ButtonEnableParametersDataType dataType = objectToTest.getButtonEnableParameters(4);
        assertEquals("rowCount ok ", dataType.rowCount, 4);
        assertEquals("search field changed ok ", dataType.hasSearchFieldChanged, true);
        assertEquals("columnsMetaData null ", dataType.columnsMetaData, null);
        assertEquals("no row selected ", dataType.isRowSelected, false);
        assertEquals("not enabled ", dataType.isCurrentlyEnabled, false);
        assertEquals("no widget info ", dataType.widgetSpecificInfo, null);

    }

    /////////////////////////////////////////////////////////////////////////////////////////

    private class StubbedChartPresenter extends ChartWindowPresenter {

        /**
         * @param display
         * @param eventBus
         */
        public StubbedChartPresenter(final IChartWindowView display, final EventBus eventBus) {
            super(display, null, eventBus);
            metaMenuItem = mockedMetaMenuItem;
        }

        @Override
        public ChartDataType getChartConfigInfo(final String winId) {
            return new ChartDataType();
        }

        @Override
        IChartPresenter getDisplayedChart() {
            return mockedJSONChart;
        }

        @Override
        JSONValue parseText(final String s) {
            return mockedJSONValue;
        }

        @Override
        public String getTabOwnerId() {
            return tabId;
        }

        @Override
        boolean validate(final JSONValue responseValue) {
            return true;
        }

        @Override
        public void handleButtonEnabling(final int rowCount) {

        }

        @Override
        IChartElementDrillDownListener getChartElementDrillDownListener() {
            return mockedChartElementDrillDownListener;
        }

    }

}
