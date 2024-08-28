/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.service;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

public class TabManagerImplTest extends TestEniqEventsUI {

    WindowManager windowManager;
    TabManagerImpl tabManager;

    @Before
    public void setUp() {
        tabManager = new TabManagerImpl(mockedEventBus);
    }

    @Test
    public void shouldCreateNewManager() {
        WindowManager result = tabManager.getWindowManager("1");

        assertThat(result, is(WindowManagerImpl.class));
    }

    @Test
    public void shouldReuseWindowManager() {
        WindowManager first = tabManager.getWindowManager("1");
        WindowManager second = tabManager.getWindowManager("1");

        assertThat(first, sameInstance(second));
    }

}
