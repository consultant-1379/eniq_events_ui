/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import org.junit.Before;
import org.junit.Ignore;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;

/**
 * 
 * eemecoy 20/7/10 - tried to get this test working, and GridPagingToolBar under test, but gave up in the end
 * Problem is that GridPagingToolBar directly extends a GXT component, that makes lots of calls to native methods in 
 * GXT code
 * 
 * @author eendmcm
 * @since Mar 2010
 */
@Ignore
public class GridPagingToolBarTest extends TestEniqEventsUI {

    StubbedGridPagingToolBar objToTest;

    GridPagingToolBar mockedPagingToolBar;

    @Before
    public void setUp() {
        mockedPagingToolBar = context.mock(GridPagingToolBar.class);

        /* cannot stub out as constructor of PagingToolBar calls GXT code */
        //objToTest = new StubbedGridPagingToolBar();
    }

    class StubbedGridPagingToolBar extends GridPagingToolBar {

        /**
         * @param pageSize
         */
        public StubbedGridPagingToolBar() throws Exception {
            super(10);
        }
    }

}
