/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.charts.window.ChartWindowView;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.google.web.bindery.event.shared.EventBus;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author eeicmsy
 *
 */
public class BaseWinGraphToGridToggleHandlerTest extends TestEniqEventsUI {

    private StubbedBaseWinGraphToGridToggleHandler objectToTest;

    private BaseWindowPresenter mockedPresenter;

    private ChartWindowView chartWindowView;

    @Before
    public void setUp() {
        mockedPresenter = context.mock(BaseWindowPresenter.class);
        chartWindowView = context.mock(ChartWindowView.class);

    }

    @Test
    public void toggleGraphToGridMakesNewWindow() throws Exception {
        final StubbedBaseWinGraphToGridToggleHandler objToTest = getObjectToTest();

        context.checking(new Expectations() {
            {
                one(chartWindowView).getToggleWindowLauncher(mockedEventBus);
                one(mockedPresenter).getFixedQueryId();
                one(mockedPresenter).getWsURL();
                one(mockedPresenter).getPresetResponseDisplayData();
                one(mockedPresenter).getWindowTimeDate();
                one(mockedPresenter).isThisWindowGuardCheck(with(any(MultipleInstanceWinId.class)));
                will(returnValue(true));
                one(chartWindowView).getWindowState();

            }
        });

        objToTest.handleGraphToGridToggle(createMultipleWinId("tabid", "winID"), true, null, "");
        assertTrue("new window killed", objToTest.isWindowKilled());
    }

    private StubbedBaseWinGraphToGridToggleHandler getObjectToTest() {

        return new StubbedBaseWinGraphToGridToggleHandler(mockedPresenter, mockedEventBus, chartWindowView);
    }

    private class StubbedBaseWinGraphToGridToggleHandler extends BaseWinGraphToGridToggleHandler {

        public boolean isWindowKilled;

        /**
         * @param baseWindowPresenter
         * @param eventBus
         * @param display
         */
        public StubbedBaseWinGraphToGridToggleHandler(final BaseWindowPresenter baseWindowPresenter,
                final EventBus eventBus, final IBaseWindowView display) {
            super(baseWindowPresenter, eventBus, display);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void killWindowBeforeReplace() {
            isWindowKilled = true;
        }

        public boolean isWindowKilled() {
            return isWindowKilled;
        }

    }

    private MultipleInstanceWinId createMultipleWinId(final String tabId, final String winId) {
        return new MultipleInstanceWinId(tabId, winId/*, null*/);
    }

}
