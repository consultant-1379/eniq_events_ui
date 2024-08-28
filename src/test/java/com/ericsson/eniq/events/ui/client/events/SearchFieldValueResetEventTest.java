/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author eeicmsy
 *
 */
public class SearchFieldValueResetEventTest extends TestEniqEventsUI {

    private final String tabId = "tabId";

    private final String winId = "queryId";

    private final SearchFieldDataType data = new SearchFieldDataType("data", new String[] { "imsi=" }, null, null,
            false, "", null, false);

    private SearchFieldValueResetEvent objectUnderTest;

    private SearchFieldValueResetEventHandler mockedSearchFieldValueResetEventHandler;

    private final String defaultUrl = "http://localhost:8080/EniqEventsServices/NETWORK/EVENT_ANALYSIS";

    @Before
    public void setUp() {

        mockedSearchFieldValueResetEventHandler = context.mock(SearchFieldValueResetEventHandler.class);

        objectUnderTest = new SearchFieldValueResetEvent(tabId, winId, data, defaultUrl);
    }

    @After
    public void tearDown() {
        objectUnderTest = null;
    }

    @Test
    public void searchUpDateNotificationOccursOnDispatch() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockedSearchFieldValueResetEventHandler).handleSearchFieldParamUpdate(tabId, winId, data,
                        defaultUrl);
            }
        });
        objectUnderTest.dispatch(mockedSearchFieldValueResetEventHandler);
    }

    @Test
    public void getAssociatedTypeasExpected() throws Exception {
        assertEquals("Type is as excepted", SearchFieldValueResetEvent.TYPE, objectUnderTest.getAssociatedType());
    }
}
