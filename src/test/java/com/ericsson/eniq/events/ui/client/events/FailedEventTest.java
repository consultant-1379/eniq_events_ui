/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.RequestCallbackImpl;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author eeicmsy
 *
 */
public class FailedEventTest extends TestEniqEventsUI {

    private final String queryId = "queryId";

    private final String tabId = "tabId";

    private final String requestData = "requestData";

    private FailedEvent objectUnderTest;

    private FailedEventHandler mockedFailedEventHandler;

    private final Exception exception = new RequestCallbackImpl.RequestCallbackException("hello");

    @Before
    public void setUp() {
        mockedFailedEventHandler = context.mock(FailedEventHandler.class);
        objectUnderTest = new FailedEvent(createMultipleInstanceWinId(tabId, queryId), requestData, exception);
    }

    @After
    public void tearDown() {
        objectUnderTest = null;
    }

    @Test
    public void failureIsDispatched() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockedFailedEventHandler).handleFail(createMultipleInstanceWinId(tabId, queryId), requestData,
                        exception);
            }
        });
        objectUnderTest.dispatch(mockedFailedEventHandler);
    }

    @Test
    public void getAssociatedTypeasExpected() throws Exception {
        assertEquals("Type is as excepted", FailedEvent.TYPE, objectUnderTest.getAssociatedType());
    }

    final MultipleInstanceWinId createMultipleInstanceWinId(final String tabId, final String winID) {
        return new MultipleInstanceWinId(tabId, winID/*, null*/);
    }
}
