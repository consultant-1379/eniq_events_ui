/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author eeicmsy
 */
public class RefreshWindowEventTest extends TestEniqEventsUI {

    private RefreshWindowEvent objectUnderTest;

    private RefreshWindowEventHandler mockedRefreshGridEventHandler;

    private final MultipleInstanceWinId multiWinID = new MultipleInstanceWinId("tabId", "windId"/*, null*/);

    @Before
    public void setUp() {
        mockedRefreshGridEventHandler = context.mock(RefreshWindowEventHandler.class);
        objectUnderTest = new RefreshWindowEvent(multiWinID);
    }

    @After
    public void tearDown() {
        objectUnderTest = null;
    }

    @Test
    public void sendsNotificationToRefreshCorrectWindow() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockedRefreshGridEventHandler).handleWindowRefresh(multiWinID);
            }
        });
        objectUnderTest.dispatch(mockedRefreshGridEventHandler);
    }

    @Test
    public void getAssociatedTypeasExpected() throws Exception {
        assertEquals("Type is as excepted", RefreshWindowEvent.TYPE, objectUnderTest.getAssociatedType());
    }
}
