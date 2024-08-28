/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.events.FailedEvent;
import com.ericsson.eniq.events.ui.client.events.SucessResponseEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;

/**
 * 
 * @author eeicmsy
 * @since April 2010
 *
 */
public class RequestCallbackImplTest extends TestEniqEventsUI {

    private RequestCallbackImpl objectUnderTest;

    private final String queryId = "queryId";

    private final String tabId = "SUBS_TAB";

    private Request mockedRequest;

    Response mockedResponse;

    Throwable mockedThrowbale;

    @Before
    public void setUp() {
        mockedRequest = context.mock(Request.class);
        mockedResponse = context.mock(Response.class);
        mockedThrowbale = context.mock(Throwable.class);

        objectUnderTest = new RequestCallbackImpl(createMultipleInstanceWinId(tabId, queryId), mockedEventBus, null);
    }

    private final MultipleInstanceWinId createMultipleInstanceWinId(final String tabId, final String winID) {
        return new MultipleInstanceWinId(tabId, winID/*, null*/);
    }

    @After
    public void tearDown() {
        objectUnderTest = null;
    }

    @Test
    public void errorResponseFiresFailToEventBus() throws Exception {
        objectUnderTest.setRequestData("origional params");
        final String errorMsg = "oh no";
        final Exception exception = new RequestCallbackImpl.RequestCallbackException(errorMsg);

        context.checking(new Expectations() {
            {
                one(mockedEventBus).fireEvent(with(any(FailedEvent.class)));
            }
        });
        objectUnderTest.onError(mockedRequest, exception);
    }

    @Test
    public void statusNotOKResponseFiresFailToEventBus() throws Exception {

        context.checking(new Expectations() {
            {
                one(mockedResponse).getStatusCode();
                will(returnValue(404)); // bad
                one(mockedEventBus).fireEvent(with(any(FailedEvent.class)));
            }
        });
        objectUnderTest.onResponseReceived(mockedRequest, mockedResponse);
    }

    @Test
    public void statusOKResponseFiresSucesssReponseToEventBus() throws Exception {

        context.checking(new Expectations() {
            {
                one(mockedResponse).getStatusCode();
                will(returnValue(200));
                one(mockedResponse).getText();
                one(mockedEventBus).fireEvent(with(any(SucessResponseEvent.class)));
            }
        });
        objectUnderTest.onResponseReceived(mockedRequest, mockedResponse);
    }

}
