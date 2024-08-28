/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import static junit.framework.Assert.*;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.widget.IEventGridView;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.extjs.gxt.ui.client.widget.Component;

/**
 * @author eeicmsy
 */
public class WindowToolbarEventTest extends TestEniqEventsUI {

    private WindowToolbarEvent objectUnderTest;

    WindowToolbarEventHandler mockedWindowToolbarEventHandler;

    final EventType eventId = EventType.KPI;

    IEventGridView refView;

    Component mockedMenuItem;

    @Before
    public void setUp() {
        mockedWindowToolbarEventHandler = context.mock(WindowToolbarEventHandler.class);
        mockedMenuItem = context.mock(Component.class);
        refView = context.mock(IEventGridView.class);
        objectUnderTest = new WindowToolbarEvent(refView, eventId, mockedEventBus, mockedMenuItem, null);
    }

    @After
    public void tearDown() {
        objectUnderTest = null;
    }

    @Test
    public void toolbatButtonEventIsDispatched() {
        context.checking(new Expectations() {
            {
                one(mockedWindowToolbarEventHandler).handleToolBarEvent(refView, eventId, mockedEventBus,
                        mockedMenuItem, null);
            }
        });
        objectUnderTest.dispatch(mockedWindowToolbarEventHandler);
    }

    @Test
    public void getAssociatedTypeasExpected() {
        assertEquals("Type is as excepted", WindowToolbarEvent.TYPE, objectUnderTest.getAssociatedType());
    }
}
