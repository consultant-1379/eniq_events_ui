/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid.listeners;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.events.RefreshWindowEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;

/**
 * @author eendmcm
 *
 */
public class RefreshGridFromServerListenerTest extends TestEniqEventsUI {

    private RefreshGridFromServerListener objectToTest;

    private final MultipleInstanceWinId multiWinId = new MultipleInstanceWinId("testMyTab", "testMyWin"/*, null*/);

    private ButtonEvent mockedButtonEvent;

    @Before
    public void setUp() {
        mockedButtonEvent = context.mock(ButtonEvent.class);
        objectToTest = new RefreshGridFromServerListener(mockedEventBus, multiWinId);
    }

    @Test
    public void clickRefreshRaisesCorrectEventToBus() {
        context.checking(new Expectations() {
            {
                one(mockedEventBus).fireEvent(with(any(RefreshWindowEvent.class)));
            }
        });
        objectToTest.componentSelected(mockedButtonEvent);
    }
}
