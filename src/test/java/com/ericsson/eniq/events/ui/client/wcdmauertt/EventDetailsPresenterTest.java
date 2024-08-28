/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 ******************************************************************************* */

package com.ericsson.eniq.events.ui.client.wcdmauertt;

import static org.junit.Assert.*;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.EventBus;

public class EventDetailsPresenterTest extends TestEniqEventsUI {

    private StubbedEventDetailsPresenter eventDetailsPresenter;
    private EventCacheUertt eventCacheUertt;
    EventDetailsView mockedView;
    JSONValue mockedJsonValue;

    @Before
    public void setUp() throws Exception {
        eventCacheUertt = new EventCacheUertt();
        mockedJsonValue = context.mock(JSONValue.class);
        eventDetailsPresenter = new StubbedEventDetailsPresenter(null, mockedEventBus, mockedJsonValue);
    }

    @Test
    public void testGo() {
        setUpExpectationsOnPresenterClassComponents();
        int size = eventDetailsPresenter.go(0, eventCacheUertt).size();
        assertEquals("The size of Collapsible section is identical", 4, size);
    }

    @Test 
    public void testformatProtocolId()
    {
        String protocol_Id = "RNC_EVENT_RRC";
        assertEquals("The protocol name is formatted", "RRC", eventDetailsPresenter.formatProtocolId(protocol_Id));
    }
    
    @Test
    public void testFormatEventId()
    {
        String event_Id = "RNC_EVENT_RRC_CONNECTION_RELEASE";
        assertEquals("The event name is formatted", "CONNECTION RELEASE", eventDetailsPresenter.formatEventId(event_Id));
    }
    
    @SuppressWarnings("unchecked")
    private void setUpExpectationsOnPresenterClassComponents() {
        context.checking(new Expectations() {
            {
                allowing(mockedJsonValue).isObject().get(with(any(String.class))).isArray().get(with(any(Integer.class))).isObject()
                        .get(with(any(String.class))).toString();
                ;
            }
        });
    }

    class StubbedEventDetailsPresenter extends EventDetailsPresenter {

        public StubbedEventDetailsPresenter(final EventDetailsView view, final EventBus eventBus, final JSONValue responseValue) {
            super(view, eventBus, responseValue);
        }

        @Override
        protected String removeDoubleQuotes(String data) {
            data = "EVENT PROTOCOL NAME";
            return data.substring(1, data.length() - 1);
        }

        @Override
        protected String formatProtocolId(String protocol_Id)
        {
            protocol_Id = "RNC_EVENT_RRC";
            String[] subStringProtocol = protocol_Id.split("_", 3);
            return subStringProtocol[2];
        }

        @Override
        protected String formatEventId(String event_Id)
        {
            event_Id = "RNC_EVENT_RRC_CONNECTION_RELEASE";
            String eventNameModified = "";
            final String[] subStringeventId = event_Id.split("_", 4);
            eventNameModified += subStringeventId[3].charAt(0);
            for (int i = 1; i < subStringeventId[3].length(); i++) {
                if (subStringeventId[3].charAt(i) == '_') {
                    eventNameModified += " " + subStringeventId[3].charAt(i + 1);
                    i++;
                } else {
                    eventNameModified += subStringeventId[3].charAt(i);
                }
            }
            return eventNameModified;
        }
    }

}
