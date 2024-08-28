/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

import com.ericsson.eniq.events.common.client.service.RestfulRequestBuilder;
import com.ericsson.eniq.events.common.client.service.RestfulRequestBuilder.State;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.impl.SchedulerImpl;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.event.shared.EventBus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author eeicmsy
 *
 */
@Ignore
public class ServerCommsTest extends TestEniqEventsUI {

    EventBus eventBus;

    RestfulRequestBuilder requestBuilder;

    ServerComms serverComms;

    @Before
    public void setUp() {
        eventBus = mock(EventBus.class);
        requestBuilder = mock(RestfulRequestBuilder.class);

        serverComms = new ServerComms(eventBus) {
            @Override
            String encodeData(final State method, final String requestData) {
                return requestData;
            }

            @Override
            String getEniqEventsServicesURI() {
                return "http://localhost";
            }

            /* (non-Javadoc)
             * @see com.ericsson.eniq.events.ui.client.common.ServerComms#getScheduler()
             */
            @Override
            protected Scheduler getScheduler() {
                return new SchedulerImpl() {

                    @Override
                    public void scheduleDeferred(final ScheduledCommand cmd) {
                        cmd.execute();
                    }
                };
            }
        };
    }

    @Test
    public void shouldMakeRequest() throws Exception {
        final Request request = mock(Request.class);
        when(requestBuilder.sendRequest(eq("a=1"), any(RequestCallback.class))).thenReturn(request);

        serverComms.makeServerRequest(createWinId(), "url", "a=1");

        verify(eventBus, times(1)).fireEvent(any(GwtEvent.class));
    }

    @Test
    public void shouldCatchException() throws Exception {
        when(requestBuilder.sendRequest(eq("a=1"), any(RequestCallback.class))).thenThrow(new RequestException());

        serverComms.makeServerRequest(createWinId(), "url", "a=1");
        verify(eventBus, times(2)).fireEvent(any(GwtEvent.class));
    }

    @Test
    public void shouldCancelRequest() throws Exception {
        final Request request = mock(Request.class);
        when(requestBuilder.sendRequest(eq("a=1"), any(RequestCallback.class))).thenReturn(request);

        serverComms.makeServerRequest(createWinId(), "url", "a=1");
        serverComms.sendCancelRequestCall();

        verify(request).cancel();
    }

    @Test
    public void shouldSafelyCallCancelWithoutRegisteredRequest() {
        serverComms.sendCancelRequestCall();
    }

    private MultipleInstanceWinId createWinId() {
        return new MultipleInstanceWinId("tabId", "winId"/*, null*/);
    }
}
