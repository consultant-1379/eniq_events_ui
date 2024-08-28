package com.ericsson.eniq.events.ui.client.events;

import static junit.framework.Assert.*;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;

/**
 * Test ToggleEvent  (well code cover)
 * @author eeicmsy
 * @since june 2011
 *
 */
public class ToggleEventTest extends TestEniqEventsUI {

    private ToggleEvent objectUnderTest;

    private ToggleEventHandler mockedToggleEventHandler;

    private final static String CURRENT_TAB = "MY_TAB";

    @Before
    public void setUp() {
        mockedToggleEventHandler = context.mock(ToggleEventHandler.class);
        objectUnderTest = new ToggleEvent(true, CURRENT_TAB); // to test gain with false
    }

    @After
    public void tearDown() {
        objectUnderTest = null;
    }

    @Test
    public void eventIsDispatched() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockedToggleEventHandler).handleToggleEvent(true, CURRENT_TAB);
            }
        });
        objectUnderTest.dispatch(mockedToggleEventHandler);
    }

    @Test
    public void getAssociatedTypeasExpected() throws Exception {
        assertEquals("Type is as excepted", ToggleEvent.TYPE, objectUnderTest.getAssociatedType());
    }

}
