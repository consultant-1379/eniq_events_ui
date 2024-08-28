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
 *
 */
public class HyperLinkCellClickEventTest extends TestEniqEventsUI {

    private final String winId = "queryId";

    private final String tabId = "RANKINGS_TAB";

    private final String cellValue = "cellValue";

    private final String url = "url";

    private final int rowIndex = 1;

    private HyperLinkCellClickEvent objectUnderTest;

    private HyperLinkCellClickEventHandler mockedHyperLinkCellClickEventHandler;

    @Before
    public void setUp() {
        mockedHyperLinkCellClickEventHandler = context.mock(HyperLinkCellClickEventHandler.class);
        objectUnderTest = new HyperLinkCellClickEvent(createMultipleWinId(tabId, winId), cellValue, url, rowIndex);
    }

    @After
    public void tearDown() {
        objectUnderTest = null;
    }

    @Test
    public void clickEventIsDispatched() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockedHyperLinkCellClickEventHandler).handleCellLinkClick(createMultipleWinId(tabId, winId),
                        cellValue, url, rowIndex);
            }
        });
        objectUnderTest.dispatch(mockedHyperLinkCellClickEventHandler);
    }

    @Test
    public void getAssociatedTypeasExpected() throws Exception {
        assertEquals("Type is as excepted", HyperLinkCellClickEvent.TYPE, objectUnderTest.getAssociatedType());
    }

    private MultipleInstanceWinId createMultipleWinId(final String tabId, final String winId) {
        return new MultipleInstanceWinId(tabId, winId/*, null*/);
    }
}
