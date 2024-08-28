/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid.listeners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.buttonenabling.RecurErrorButtonEnableConditions;
import com.ericsson.eniq.events.ui.client.buttonenabling.SACButtonEnableConditions;
import com.ericsson.eniq.events.ui.client.common.widget.IEventGridView;
import com.ericsson.eniq.events.ui.client.grid.JSONGrid;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;

/**
 * @author esuslyn
 *
 */
public class RowSelectListenerTest extends TestEniqEventsUI {

    private RowSelectListener objToTest;

    private JSONGrid mockedJSONGrid;

    private IEventGridView mockedGridView;

    private GridEvent<ModelData> mockedGridEvent;

    private ModelData mockedModelData;

    private RecurErrorButtonEnableConditions mockedRecurErrButtonEnableConditions;

    private SACButtonEnableConditions mockedSACButtonEnableConditions;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        mockedJSONGrid = context.mock(JSONGrid.class);
        mockedGridView = context.mock(IEventGridView.class);
        mockedGridEvent = context.mock(GridEvent.class);
        mockedModelData = context.mock(ModelData.class);
        mockedRecurErrButtonEnableConditions = context.mock(RecurErrorButtonEnableConditions.class);
        mockedSACButtonEnableConditions = context.mock(SACButtonEnableConditions.class);
        objToTest = new RowSelectListenerStub(mockedJSONGrid, mockedGridView);

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        objToTest = null;
    }

    /**
     * Test method for {@link com.ericsson.eniq.events.ui.client.grid.listeners.RowSelectListener#handleEvent(com.extjs.gxt.ui.client.event.GridEvent)}.
     */
    @Test
    public void testHandleEventWhenNoRecurErrButton() {

        setupGeneralExpectations();

        final GridInfoDataType incorrectCols = getIncorrectColumnheaders();
        final Set<String> dummyheaders = new HashSet<String>();//These would be overridden by incorrectCols from getAllGridColumnHeaderIds().
        dummyheaders.add("incorrect1");
        dummyheaders.add("incorrect2");

        context.checking(new Expectations() {
            {
                one(mockedGridView).getColumns();
                will(returnValue(incorrectCols));

                one(mockedSACButtonEnableConditions).shouldEnableBasedOnColumnHeaders(dummyheaders);
                one(mockedGridView).setToolbarButtonEnabled("btnSac", false);
            }
        });

        objToTest.handleEvent(mockedGridEvent);
    }

    /**
     * Test method for {@link com.ericsson.eniq.events.ui.client.grid.listeners.RowSelectListener#handleEvent(com.extjs.gxt.ui.client.event.GridEvent)}.
     */
    @Test
    public void testHandleEventWhenHaveRecurErrButton() {

        setupGeneralExpectations();
        ((RowSelectListenerStub) objToTest).setRecurErrButtonExists(true);

        final List<String> incorrectRecurErrorColumns = new ArrayList<String>();
        incorrectRecurErrorColumns.add("incorrect");

        final GridInfoDataType incorrectCols = getIncorrectColumnheaders();

        final Set<String> headers = new HashSet<String>();
        headers.add("incorrect1");
        headers.add("incorrect2");

        context.checking(new Expectations() {
            {
                allowing(mockedGridView).getColumns();
                will(returnValue(incorrectCols));

                one(mockedRecurErrButtonEnableConditions).shouldEnableBasedOnColumnHeaders(headers);
                one(mockedGridView).setToolbarButtonEnabled("btnRecur", false);

                one(mockedSACButtonEnableConditions).shouldEnableBasedOnColumnHeaders(headers);
                one(mockedGridView).setToolbarButtonEnabled("btnSac", false);
            }
        });

        objToTest.handleEvent(mockedGridEvent);
    }

    private GridInfoDataType getIncorrectColumnheaders() {
        final GridInfoDataType incorrectCols = new GridInfoDataType();
        incorrectCols.columnInfo = new ColumnInfoDataType[2];
        incorrectCols.columnInfo[0] = new ColumnInfoDataType();
        incorrectCols.columnInfo[0].columnHeader = "incorrect1";
        incorrectCols.columnInfo[1] = new ColumnInfoDataType();
        incorrectCols.columnInfo[1].columnHeader = "incorrect2";

        return incorrectCols;
    }

    private void setupGeneralExpectations() {
        context.checking(new Expectations() {
            {
                one(mockedGridEvent).getModel();
                will(returnValue(mockedModelData));
                one(mockedJSONGrid).setRecord(mockedModelData);
                one(mockedGridView).setToolbarButtonEnabled("btnProperties", true);
            }
        });
    }

    private class RowSelectListenerStub extends RowSelectListener {

        private boolean recurErrButtonExists;

        /**
         * @param grid
         * @param gridView
         */
        public RowSelectListenerStub(final JSONGrid grid, final IEventGridView gridView) {
            super(grid, gridView);
            // TODO Auto-generated constructor stub
        }

        void setRecurErrButtonExists(final boolean aFlag) {
            recurErrButtonExists = aFlag;
        }

        @Override
        public boolean recurErrorButtonExists() {
            return recurErrButtonExists;
        }

        @Override
        RecurErrorButtonEnableConditions getRecurErrButtonEnableConditions() {
            return mockedRecurErrButtonEnableConditions;
        }

        @Override
        SACButtonEnableConditions getSACButtonEnableConditions() {
            return mockedSACButtonEnableConditions;
        }

    }
}
