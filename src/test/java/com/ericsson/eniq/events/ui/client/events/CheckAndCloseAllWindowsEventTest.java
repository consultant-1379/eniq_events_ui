package com.ericsson.eniq.events.ui.client.events;

import static junit.framework.Assert.*;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.extjs.gxt.ui.client.event.ComponentEvent;

/**
 * Test CheckAndCloseAllWindowsEvent  (well code cover)
 * @author eeicmsy
 * @since june 2011
 *
 */
public class CheckAndCloseAllWindowsEventTest extends TestEniqEventsUI {

    private CheckAndCloseAllWindowsEvent objectUnderTest;

    private CheckAndCloseAllWindowsEventHandler mockedCheckAndCloseAllWindowsEventHandler;

    private ComponentEvent mockedComponentEvent;

    private final static String CURRENT_TAB = "MY_TAB";

    @Before
    public void setUp() {
        mockedCheckAndCloseAllWindowsEventHandler = context.mock(CheckAndCloseAllWindowsEventHandler.class);
        mockedComponentEvent = context.mock(ComponentEvent.class);

        objectUnderTest = new CheckAndCloseAllWindowsEvent(mockedComponentEvent, CURRENT_TAB);
    }

    @After
    public void tearDown() {
        objectUnderTest = null;
    }

    @Test
    public void eventIsDispatched() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockedCheckAndCloseAllWindowsEventHandler).handleCheckForMultiWinSelectComp(mockedComponentEvent,
                        CURRENT_TAB);
            }
        });
        objectUnderTest.dispatch(mockedCheckAndCloseAllWindowsEventHandler);
    }

    @Test
    public void getAssociatedTypeasExpected() throws Exception {
        assertEquals("Type is as excepted", CheckAndCloseAllWindowsEvent.TYPE, objectUnderTest.getAssociatedType());
    }

}
