/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid.listeners;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.grid.JSONGrid;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.StoreEvent;

/**
 * @author esuslyn
 *
 */
public class RefreshListenerTest extends TestEniqEventsUI {

    private RefreshListener objToTest;

    private JSONGrid mockedJSONGrid;

    private StoreEvent<ModelData> mockedStoreEvent;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        mockedJSONGrid = context.mock(JSONGrid.class);
        mockedStoreEvent = context.mock(StoreEvent.class);
        createRefreshListener();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        objToTest = null;
    }

    private void createRefreshListener() {
        objToTest = new RefreshListener(mockedJSONGrid);
    }

    /**
     * Test method for {@link com.ericsson.eniq.events.ui.client.grid.listeners.RefreshListener#handleEvent(com.extjs.gxt.ui.client.store.StoreEvent)}.
     */
    @Test
    public void testHandleEvent() {
        context.checking(new Expectations() {
            {
                one(mockedJSONGrid).refreshGrid();
            }
        });
        objToTest.handleEvent(mockedStoreEvent);
        assertEquals(objToTest.isViewUpdated, true);

    }

    /**
     * Test method for {@link com.ericsson.eniq.events.ui.client.grid.listeners.RefreshListener#setRefreshView(boolean)}.
     */
    @Test
    public void testSetRefreshView() {
        objToTest.setRefreshView(true);
    }

}
