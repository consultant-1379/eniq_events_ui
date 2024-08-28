/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.google.gwt.http.client.Response;
import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author eeicmsy
 *
 */
public class SucessResponseEventTest extends TestEniqEventsUI {

    private final String queryId = "queryId";

    private final String tabId = "tabId";

    private final String requestData = "requestData";

    private SucessResponseEvent objectUnderTest;

    private SucessResponseEventHandler mockedSucessResponseEventHandler;

    private Response response;

    @Before
    public void setUp() {
        mockedSucessResponseEventHandler = context.mock(SucessResponseEventHandler.class);
        response = context.mock(Response.class);
        objectUnderTest = new SucessResponseEvent(createMultipleInstanceWinId(tabId, queryId), requestData, response);
    }

    @After
    public void tearDown() {
        objectUnderTest = null;
    }

    @Test
    public void successIsDispatched() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockedSucessResponseEventHandler).handleResponse(createMultipleInstanceWinId(tabId, queryId),
                        requestData, response);
            }
        });
        objectUnderTest.dispatch(mockedSucessResponseEventHandler);
    }

    @Test
    public void getAssociatedTypeasExpected() throws Exception {
        assertEquals("Type is as excepted", SucessResponseEvent.TYPE, objectUnderTest.getAssociatedType());
    }

    private final MultipleInstanceWinId createMultipleInstanceWinId(final String tabId, final String winID) {
        return new MultipleInstanceWinId(tabId, winID/*, null*/);
    }
}
