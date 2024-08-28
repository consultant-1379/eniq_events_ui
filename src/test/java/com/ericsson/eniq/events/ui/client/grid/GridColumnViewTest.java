/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;

/**
 * @author edivkir
 * @since 2011
 *
 */
public class GridColumnViewTest extends TestEniqEventsUI {

    /* GXT 2.2.4 sticks a   protected int scrollOffset = XDOM.getScrollBarWidth();
     * into GridView class variable definition which pretty much makes us untestable 
     */

    public GridColumnView objToTest;

    protected ColumnModel mockedColumnModel;

    protected Menu mockedMenu;

    protected Grid mockedGrid;

    GridInfoDataType testData;

    // GXT 2.2.4 makes this untestable  XDOM.getScrollBarWidth(); called in GridView creation
    @Before
    @Ignore
    public void setUp() {
        mockedColumnModel = context.mock(ColumnModel.class);
        mockedMenu = context.mock(Menu.class);
        mockedGrid = new StubbedGrid();
        setData();
    }

    // GXT 2.2.4 makes this untestable  XDOM.getScrollBarWidth(); called in GridView creation
    @Test
    @Ignore
    public void testCreateContextMenu() {
        final int colIndex = 1;
        context.checking(new Expectations() {
            {
                allowing(objToTest.getColumnModel()).isSortable(colIndex);
                will(returnValue(true));
                allowing(objToTest.getColumnModel()).getColumnCount();
                will(returnValue(1));
            }
        });
        setExpectations(false);
        objToTest.createContextMenu(colIndex);
    }

    // GXT 2.2.4 makes this untestable  XDOM.getScrollBarWidth(); called in GridView creation
    @Test
    @Ignore
    public void testcolumnMenuWithWrongHeaders() {
        setExpectations(true);
        objToTest.createColumnMenu(null, "");
    }

    // GXT 2.2.4 makes this untestable  XDOM.getScrollBarWidth(); called in GridView creation
    @Test
    @Ignore
    public void testcolumnMenuWithMultiColumnLicences() {
        testData.columnInfo[0].columnLicence = "3G,Voice";

        setExpectations(false);
        objToTest.getCheckMultiMenuItems();
        objToTest.createColumnMenu(null, "3G");
    }

    // GXT 2.2.4 makes this untestable  XDOM.getScrollBarWidth(); called in GridView creation
    @Test
    @Ignore
    public void testcolumnMenuWithSameLicences() {
        testData.columnInfo[0].columnLicence = "3G";
        setExpectations(false);
        objToTest.createColumnMenu(null, "3G");
    }

    /*
       * Private Methods - 
       * 1) Set test data
       * 2) Set test expectations
       */
    private void setData() {
        final ColumnInfoDataType colElement = new ColumnInfoDataType();
        colElement.columnWidth = "50";

        testData = new GridInfoDataType();
        testData.licenceTypes = "3G";
        testData.columnInfo = new ColumnInfoDataType[] { colElement };
        testData.columnInfo[0].columnLicence = "3GG";

        objToTest = new StubbedGridColumnView(testData);

        objToTest.cols = 1;
    }

    private void setExpectations(final boolean FixedVal) {
        context.checking(new Expectations() {
            {
                allowing(objToTest.getColumnModel()).getColumnHeader(0);
                will(returnValue("Dummy"));
                allowing(objToTest.getColumnModel()).isFixed(0);
                will(returnValue(FixedVal));
                allowing(objToTest.getColumnModel()).isHidden(0);
            }
        });
        objToTest.init(mockedGrid);
    }

    /*
     * Mocked classes to support GXT unit testing.
     */
    class StubbedMenu extends Menu {
        @Override
        public boolean add(final Component item) {
            return true;
        }
    }

    class StubbedGrid extends Grid {
    }

    class StubbedGridColumnView extends GridColumnView {

        @Override
        protected void init(final Grid grid) {
            columnsWithMultiLicence = objToTest.getCheckMultiMenuItems();
            gridColumndata = new GridColumnsData(testData, getColumnModel());
            state = grid.getState();
        }

        public StubbedGridColumnView(final GridInfoDataType columnInfo) {
            super(columnInfo);
        }

        @Override
        public ColumnModel getColumnModel() {
            return mockedColumnModel;
        }

        @Override
        public int getGridColumnCount() {
            return 1;
        }

        @Override
        protected CheckMenuItem[][] getCheckMultiMenuItems() {
            final CheckMenuItem[][] check = new CheckMenuItem[1][1];
            check[0][0] = new CheckMenuItem();
            check[0][0].setHideOnClick(false);
            check[0][0].setText("Dummy");
            check[0][0].setChecked(true);
            return check;
        }

        @Override
        protected Menu getMenu() {
            return new StubbedMenu();
        }
    }

}
