/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.charts.ChartElementDetails;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

/**
 * @author eeicmsy
 *
 */
public class HideShowChartElementEventTest extends TestEniqEventsUI {

    private final String winId = "winId";

    private final String tabId = "tabId";

    private final String elementId = "1";

    private final Set<ChartElementDetails> tickedMenuItems = new HashSet<ChartElementDetails>();

    private HideShowChartElementEvent objectUnderTest;

    private HideShowChartElementEventHandler mockedHideShowChartHandler;

    @Before
    public void setUp() {
        mockedHideShowChartHandler = context.mock(HideShowChartElementEventHandler.class);

        tickedMenuItems.add(new ChartElementDetails(elementId));
        objectUnderTest = new HideShowChartElementEvent(createMultipleWinId(tabId, winId), tickedMenuItems);
    }

    private HideShowChartElementEvent getShowElementObjectToTest() {
        return new HideShowChartElementEvent(createMultipleWinId(tabId, winId), tickedMenuItems);
    }

    private HideShowChartElementEvent getShowHideLegendObjectToTest() {
        return new HideShowChartElementEvent(createMultipleWinId(tabId, winId));
    }

    @After
    public void tearDown() {
        objectUnderTest = null;
    }

    @Test
    public void hideShowNotificationOccursOnDispatch() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockedHideShowChartHandler).handleShowChartElementsEvent(createMultipleWinId(tabId, winId),
                        tickedMenuItems);

            }
        });
        getShowElementObjectToTest().dispatch(mockedHideShowChartHandler);
    }

    @Test
    public void hideShowLegendNotificationOccursOnDispatch() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockedHideShowChartHandler).handleHideShowChartLegend(createMultipleWinId(tabId, winId));

            }
        });
        getShowHideLegendObjectToTest().dispatch(mockedHideShowChartHandler);
    }

    @Test
    public void getAssociatedTypeasExpected() throws Exception {
        assertEquals("Type is as excepted", HideShowChartElementEvent.TYPE, objectUnderTest.getAssociatedType());
    }

    private MultipleInstanceWinId createMultipleWinId(final String tabId, final String winId) {
        return new MultipleInstanceWinId(tabId, winId/*, null*/);
    }
}
