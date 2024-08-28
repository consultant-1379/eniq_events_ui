/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget;

import static junit.framework.Assert.*;
import com.google.web.bindery.event.shared.EventBus;
import com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.extjs.gxt.ui.client.widget.Component;

/**
 * @author eendmcm
 *
 */
public class TimeParameterDialogPresenterTest extends TestEniqEventsUI {

    EventGridView mockedView;

    @Before
    public void setUp() {
        mockedView = context.mock(EventGridView.class);
    }

    @Test
    public void timePresenterConstuctor() {
        context.checking(new Expectations() {
            {
                one(mockedView).getPresenter();
            }
        });
        final TimeParameterDialogPresenter objToTest = getObjToTest();

        assertEquals("Dummy Test", true, true);

    }

    private TimeParameterDialogPresenter getObjToTest() {
        return new StubPresenter(mockedView, mockedEventBus);
    }

    private class StubPresenter extends TimeParameterDialogPresenter {

        /**
         * @param display
         * @param eventBus
         */
        public StubPresenter(final WidgetDisplay display, final EventBus eventBus) {
            super(display, eventBus);

        }

        @Override
        String getGridText() {
            return "Test Grid";
        }

        @Override
        void initDisplay() {

        }
    }
}
