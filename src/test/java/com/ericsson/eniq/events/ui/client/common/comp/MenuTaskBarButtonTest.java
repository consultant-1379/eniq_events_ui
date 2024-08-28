/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author eeicmsy
 * @since March 2010
 *
 *
 */
public class MenuTaskBarButtonTest extends TestEniqEventsUI {

    BaseWindow mockedBaseWindow;

    private ButtonEvent mockedButtonEvent;

    @Before
    public void setUp() {
        // failed to mock id into base window so mocking its parts:

        mockedButtonEvent = context.mock(ButtonEvent.class);
        mockedBaseWindow = context.mock(BaseWindow.class);
    }

    private MenuTaskBarButton getObjUnderTest() {

        context.checking(new Expectations() {
            {
                allowing(mockedBaseWindow).toFront();
                allowing(mockedBaseWindow).focus();
            }
        });
        return new StubbedMenuTaskBarButton("My window title", mockedBaseWindow);
    }

    @Test
    public void listenerAddedToTheButton() {
        final MenuTaskBarButton button = getObjUnderTest();
        final List<Listener<? extends BaseEvent>> listners = button.getListeners(Events.Select);
        assertEquals("have added a listener ", 1, listners.size());
    }

    @Test
    public void clickWhenWindowMaximisedBlinks() {

        context.checking(new Expectations() {
            {
                one(mockedBaseWindow).bringToFront();
            }
        });
        final MenuTaskBarButton button = getObjUnderTest();
        button.launchButtonListener.componentSelected(mockedButtonEvent);

    }

    private class StubbedMenuTaskBarButton extends MenuTaskBarButton {

        public StubbedMenuTaskBarButton(final String title, final BaseWindow winRef) {
            super(title, winRef);
        }

        @Override
        public String getWindowID() {
            return "ID";
        }

    }

}
