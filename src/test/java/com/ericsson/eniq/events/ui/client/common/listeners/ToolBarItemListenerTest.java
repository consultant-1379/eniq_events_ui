/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.listeners;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.ericsson.eniq.events.ui.client.events.WindowToolbarEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;

/**
 * @author eeicmsy
 *
 */
public class ToolBarItemListenerTest extends TestEniqEventsUI {

    private IExtendedWidgetDisplay mockedView;

    private ToolBarItemListener objectUnderTest;

    @Before
    public void setUp() {
        mockedView = context.mock(IExtendedWidgetDisplay.class);
        objectUnderTest = new ToolBarItemListener(mockedEventBus, mockedView, EventType.KPI);
    }

    @After
    public void tearDown() {
        objectUnderTest = null;
    }

    @Test
    public void buttonSelectionFiresWindowToolBarEvent() {

        context.checking(new Expectations() {
            {
                one(mockedEventBus).fireEvent(with(any(WindowToolbarEvent.class)));
            }
        });

        objectUnderTest.componentSelected(new ButtonEvent(null));
    }
}
