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
import com.extjs.gxt.ui.client.event.MenuEvent;

/**
 * @author eeicmsy
 *
 */
public class ToolBarMenuItemListenerTest extends TestEniqEventsUI {

    private IExtendedWidgetDisplay mockedView;

    private ToolBarMenuItemListener objectUnderTest;

    @Before
    public void setUp() {

        mockedView = context.mock(IExtendedWidgetDisplay.class);
        objectUnderTest = new ToolBarMenuItemListener(mockedEventBus, mockedView, EventType.SHOW_HIDE_CHART_LEGEND);
    }

    @After
    public void tearDown() {
        objectUnderTest = null;
    }

    @Test
    public void itemSelectionFiresWindowToolBarEvent() throws Exception {

        context.checking(new Expectations() {
            {
                one(mockedEventBus).fireEvent(with(any(WindowToolbarEvent.class)));
            }
        });

        objectUnderTest.componentSelected(new MenuEvent(null));
    }
}
