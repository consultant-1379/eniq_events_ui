/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.eniq.events.ui.client.common.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.events.window.WindowClosedEvent;
import com.ericsson.eniq.events.ui.client.events.window.WindowOpenedEvent;

public class WindowManagerImplTest extends TestEniqEventsUI {

    private WindowManagerImpl manager;
    private BaseWindow baseWindow;

    @Before
    public void setUp() throws Exception {
        manager = new WindowManagerImpl(mockedEventBus, "") {
            @Override
            int getMaxInstanceCount() {
                return 1;
            }

        };
        baseWindow = context.mock(BaseWindow.class);
    }

    @Test
    public void shouldBeEmptyAtTheBeginning() {
        assertThat(manager.isThereWindows(), is(false));
    }

    @Test
    public void testOpenWindow() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockedEventBus).fireEvent(with(any(WindowOpenedEvent.class)));
                one(baseWindow).getBaseWindowID();
                will(returnValue("1"));
            }
        });

        final boolean result = manager.openWindow(baseWindow, "title", "icon");
        assertThat(result, equalTo(true));
    }

    @Test
    public void testCloseWindow() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockedEventBus).fireEvent(with(any(WindowClosedEvent.class)));
                one(baseWindow).getBaseWindowID();
                will(returnValue("1"));
            }
        });

        manager.closeWindow(baseWindow, "title", "icon");
    }
}
