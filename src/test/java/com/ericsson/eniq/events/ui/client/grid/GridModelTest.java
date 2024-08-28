/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;
import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.datatype.GridCellColorType;
import com.ericsson.eniq.events.ui.client.grid.filters.DateTimeFilter;
import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.filters.NumericFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;

/**
 * @author eendmcm
 *
 */
public class GridModelTest extends TestEniqEventsUI {

    private GridInfoDataType mockedGridInfoDataType;

    private StringFilter mockedFilter;

    private NumericFilter mockedNumericFilter;

    private DateTimeFilter mockedDateTimeFilter;

    @Before
    public void setUp() {

        mockedGridInfoDataType = context.mock(GridInfoDataType.class);
        mockedFilter = context.mock(StringFilter.class);
        mockedNumericFilter = context.mock(NumericFilter.class);
        mockedDateTimeFilter = context.mock(DateTimeFilter.class);

    }

    @Test
    public void testGridModelConstructionWithHyperLinkColumn() throws Exception {

        final ColumnInfoDataType columnInfoDataType = new ColumnInfoDataType();
        columnInfoDataType.columnDataType = "String";
        columnInfoDataType.columnHeader = "link";
        columnInfoDataType.columnWidth = "100";
        columnInfoDataType.setDrillDownTypeKey("drillDownWin");
        mockedGridInfoDataType.columnInfo = new ColumnInfoDataType[] { columnInfoDataType };

        new StubbedGridModel(mockedGridInfoDataType);

    }

    @Test
    public void testGridModelConstructionWithoutHyperLinkColumn() throws Exception {

        final ColumnInfoDataType columnInfoDataType = new ColumnInfoDataType();
        columnInfoDataType.columnDataType = "String";
        columnInfoDataType.columnHeader = "Test Column";
        columnInfoDataType.columnWidth = "50";
        columnInfoDataType.setDrillDownTypeKey("");
        mockedGridInfoDataType.columnInfo = new ColumnInfoDataType[] { columnInfoDataType };

        new StubbedGridModel(mockedGridInfoDataType);

    }

    @Test
    public void testKPIGridColorDrillDownRenderer() throws Exception {

        final ColumnInfoDataType columnInfoDataType = new ColumnInfoDataType();
        columnInfoDataType.columnDataType = "float";
        columnInfoDataType.columnHeader = "Success Ratio";
        columnInfoDataType.columnWidth = "50";
        columnInfoDataType.setDrillDownTypeKey("1");
        mockedGridInfoDataType.columnInfo = new ColumnInfoDataType[] { columnInfoDataType };

        final GridModel objectToTest = new StubbedGridModel(mockedGridInfoDataType);

        final ColorGridCellRenderer expectedDrillDownRenderer = new ColorGridCellRenderer("1",
                GridCellColorType.KPI_SUCCESS_RATIO_DRILLDOWN);

        // defined a #equals in the renderer
        assertEquals("expected a KPI drilldown renderer", expectedDrillDownRenderer,
                objectToTest.getHyperLinkCellDrillDownRenderer(columnInfoDataType));
    }

    @Test
    public void testKPIGridColorPlainCellRenderer() throws Exception {

        final ColumnInfoDataType columnInfoDataType = new ColumnInfoDataType();
        columnInfoDataType.columnDataType = "float";
        columnInfoDataType.columnHeader = "Success Ratio";
        columnInfoDataType.columnWidth = "50";
        columnInfoDataType.setDrillDownTypeKey("");
        mockedGridInfoDataType.columnInfo = new ColumnInfoDataType[] { columnInfoDataType };

        final GridModel objectToTest = new StubbedGridModel(mockedGridInfoDataType);

        final ColorGridCellRenderer expectedDrillDownRenderer = new ColorGridCellRenderer(
                GridCellColorType.KPI_SUCCESS_RATIO_PLAIN_CELL);

        // defined a #equals in the renderer
        assertEquals("expected a KPI plain renderer", expectedDrillDownRenderer,
                objectToTest.getPlainCellRenderer(columnInfoDataType));
    }

    @Test
    public void testFailuresColumnCellRenderer() throws Exception {
        final ColumnInfoDataType column = new ColumnInfoDataType();
        column.columnDataType = "int";
        column.columnHeader = FAILURES_COLUMN_HEADER;
        column.columnWidth = "50";
        column.setDrillDownTypeKey("drill");

        mockedGridInfoDataType.columnInfo = new ColumnInfoDataType[] { column };

        final GridModel objectToTest = new StubbedGridModel(mockedGridInfoDataType);

        final ColorGridCellRenderer expectedDrillDownRenderer = new ColorGridCellRenderer(
                "drill", false);

        // defined a #equals in the renderer
        assertEquals("expected a ColourGridCellRenderer which does not hyperlink 0 values", expectedDrillDownRenderer,
                objectToTest.getHyperLinkCellDrillDownRenderer(column));

    }

    @Test
    public void testManufacturerColumnCellRenderer() throws Exception {
        final String expectedLaunchRL = "EXPECTED_URL";
        final ColumnInfoDataType columnInfoDataType = new ColumnInfoDataType();
        columnInfoDataType.columnDataType = "String";
        columnInfoDataType.columnHeader = MANUFACTURER_COLUMN_HEADER;
        columnInfoDataType.columnWidth = "50";
        columnInfoDataType.setDrillDownTypeKey("1");
        columnInfoDataType.launchWindowType = expectedLaunchRL;
        mockedGridInfoDataType.columnInfo = new ColumnInfoDataType[] { columnInfoDataType };

        final GridModel objectToTest = new StubbedGridModel(mockedGridInfoDataType);

        final HyperLinkLaunchCellRenderer expectedRenderer = new HyperLinkLaunchCellRenderer(expectedLaunchRL,false);

        // defined a #equals in the renderer
        assertEquals("expected a launch renderer", expectedRenderer,
                objectToTest.getHyperLinkLaunchCellRenderer(columnInfoDataType));
    }


    @Test
    public void testModelColumnCellRenderer() throws Exception {
        final String expectedLaunchRL = "EXPECTED_URL";
        final ColumnInfoDataType columnInfoDataType = new ColumnInfoDataType();
        columnInfoDataType.columnDataType = "String";
        columnInfoDataType.columnHeader = MODEL_COLUMN_HEADER;
        columnInfoDataType.columnWidth = "50";
        columnInfoDataType.setDrillDownTypeKey("1");
        columnInfoDataType.launchWindowType = expectedLaunchRL;
        mockedGridInfoDataType.columnInfo = new ColumnInfoDataType[] { columnInfoDataType };

        final GridModel objectToTest = new StubbedGridModel(mockedGridInfoDataType);

        final HyperLinkLaunchCellRenderer expectedRenderer = new HyperLinkLaunchCellRenderer(expectedLaunchRL,false);

        // defined a #equals in the renderer
        assertEquals("expected a launch renderer", expectedRenderer,
                objectToTest.getHyperLinkLaunchCellRenderer(columnInfoDataType));
    }

    @Test
    public void testFailureColumnCellRenderer() throws Exception {
        final String expectedLaunchRL = "EXPECTED_URL";
        final ColumnInfoDataType columnInfoDataType = new ColumnInfoDataType();
        columnInfoDataType.columnDataType = "String";
        columnInfoDataType.columnHeader = FAILURES_COLUMN_HEADER;
        columnInfoDataType.columnWidth = "50";
        columnInfoDataType.setDrillDownTypeKey("1");
        columnInfoDataType.launchWindowType = expectedLaunchRL;
        mockedGridInfoDataType.columnInfo = new ColumnInfoDataType[] { columnInfoDataType };

        final GridModel objectToTest = new StubbedGridModel(mockedGridInfoDataType);

        final HyperLinkLaunchCellRenderer expectedRenderer = new HyperLinkLaunchCellRenderer(expectedLaunchRL,false);

        // defined a #equals in the renderer
        assertEquals("expected a launch renderer", expectedRenderer,
                objectToTest.getHyperLinkLaunchCellRenderer(columnInfoDataType));
    }

    @Test
    public void testLaunchRenderer() throws Exception {

        final String expectedLaunchRL = "EXPECTED_URL";

        final ColumnInfoDataType columnInfoDataType = new ColumnInfoDataType();
        columnInfoDataType.columnDataType = "String";
        columnInfoDataType.columnHeader = "whateer";
        columnInfoDataType.columnWidth = "50";
        columnInfoDataType.setDrillDownTypeKey("");
        columnInfoDataType.setDrillDownTypeKey("1");
        columnInfoDataType.launchWindowType = expectedLaunchRL;
        mockedGridInfoDataType.columnInfo = new ColumnInfoDataType[] { columnInfoDataType };

        final GridModel objectToTest = new StubbedGridModel(mockedGridInfoDataType);

        final HyperLinkLaunchCellRenderer expectedRenderer = new HyperLinkLaunchCellRenderer(expectedLaunchRL,true);

        // defined a #equals in the renderer
        assertEquals("expected a launch renderer", expectedRenderer,
                objectToTest.getHyperLinkLaunchCellRenderer(columnInfoDataType));
    }

    @Test
    public void testSubscribersToRight() throws Exception {

        final ColumnInfoDataType columnElement = new ColumnInfoDataType();
        columnElement.columnDataType = "String";
        columnElement.columnHeader = "Impacted Subscribers";
        columnElement.columnWidth = "50";
        columnElement.setDrillDownTypeKey("");
        columnElement.launchWindowType = "";
        mockedGridInfoDataType.columnInfo = new ColumnInfoDataType[] { columnElement };

        final GridModel objectToTest = new StubbedGridModel(mockedGridInfoDataType);

        final ColumnConfig gridColumn = new ColumnConfig(columnElement.columnID, columnElement.columnID, Integer // NOPMD by esuslyn on 16/03/10 11:15
                .parseInt(columnElement.columnWidth));

        final DataField field = new DataField(columnElement.columnID); //NOPMD (eemecoy 1/6/10, necessary evil)
        field.setName(columnElement.columnID);

        objectToTest.checkForNumericAndDate(columnElement, gridColumn, field);

    }

    @Test
    public void renderCellHyperLink() throws Exception {

        final ColorGridCellRenderer test = new ColorGridCellRenderer("TEST");
        test.renderHyperLink("My Cell Value");
    }

    private class StubbedGridModel extends GridModel {
        public StubbedGridModel(final GridInfoDataType gridMetaData) {
            super(gridMetaData);
            // TODO Auto-generated constructor stub
        }

        @Override
        public StringFilter createStringFilter(final String colID) {
            return mockedFilter;
        }

        @Override
        NumericFilter createNumericFilterFilter(final String colID) {
            return mockedNumericFilter;
        }

        @Override
        DateTimeFilter createDateTimeFilter(final String colID) {
            return mockedDateTimeFilter;
        }
    }

}
