/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid.listeners;

import java.util.Map;

import com.extjs.gxt.ui.client.Style;
import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.grid.JSONGrid;
import com.ericsson.eniq.events.ui.client.grid.JSONGridStateManager;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.google.gwt.dev.util.collect.HashMap;

/**
 * @author esuslyn
 *
 */
public class PagingFilterListenerTest extends TestEniqEventsUI {

    private PagingFilterListener objToTest;

    private JSONGrid mockedJSONGrid;

    PagingLoader<PagingLoadResult<ModelData>> mockedPagingLoader;

    private GridEvent<ModelData> mockedGridEvent;

    private final String SORT_DIR = "sortDir";

    private final String SORT_FIELD = "sortField";

    private final String LIMIT = "limit";

    private final String OFFSET = "offset";

    private JSONGridStateManager mockedStateManager;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
        mockedJSONGrid = context.mock(JSONGrid.class);
        mockedStateManager = context.mock(JSONGridStateManager.class);
        mockedPagingLoader = context.mock(PagingLoader.class);
        mockedGridEvent = context.mock(GridEvent.class);
        context.mock(Map.class);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() {
        objToTest = null;
    }

    /**
     * Test method for {@link com.ericsson.eniq.events.ui.client.grid.listeners.PagingFilterListener#handleEvent(com.extjs.gxt.ui.client.event.GridEvent)}.
     */
    @Test
    public void testHandleEventTrue() {
        context.checking(new Expectations() {
            {
                allowing(mockedJSONGrid).getStateManager();will(returnValue(mockedStateManager));
                one(mockedJSONGrid).getRowsPerPage();will(returnValue(3));
                one(mockedStateManager).hasSavedState();will(returnValue(true));
                one(mockedPagingLoader).load(with(any(PagingLoadConfig.class)));
                one(mockedStateManager).getLimit();will(returnValue(3));
                one(mockedStateManager).getSortField();will(returnValue("Failure"));
                one(mockedStateManager).getSortDir();will(returnValue("ASC"));
            }
        });
        objToTest = new PagingFilterListener(mockedJSONGrid, mockedPagingLoader);

        objToTest.handleEvent(mockedGridEvent);
    }

    @Test
    public void testHandleEventFalse() {
        context.checking(new Expectations() {
            {
                allowing(mockedJSONGrid).getStateManager();will(returnValue(mockedStateManager));
                one(mockedJSONGrid).getRowsPerPage();will(returnValue(3));
                one(mockedStateManager).hasSavedState();will(returnValue(false));
                one(mockedPagingLoader).load(with(any(PagingLoadConfig.class)));
            }
        });
        objToTest = new PagingFilterListener(mockedJSONGrid, mockedPagingLoader);

        objToTest.handleEvent(mockedGridEvent);
    }
}