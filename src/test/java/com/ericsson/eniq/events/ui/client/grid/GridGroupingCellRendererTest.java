package com.ericsson.eniq.events.ui.client.grid;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;

public class GridGroupingCellRendererTest extends TestEniqEventsUI {

    private GridGroupingCellRenderer gridGroupingCellRenderer;

    private GroupColumnData mockedGroupColumnData;

    String columnHeader = "some column header";

    ColumnModel mockedColumnModel;

    @Before
    public void create() {
        mockedGroupColumnData = context.mock(GroupColumnData.class);
        mockedColumnModel = context.mock(ColumnModel.class);
        gridGroupingCellRenderer = new StubbedGridGroupingCellRenderer(null);
    }

    @Test
    public void testRender() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockedColumnModel).getColumnById(null);
                one(mockedColumnModel).getColumnCount();
            }
        });

        final String result = gridGroupingCellRenderer.render(mockedGroupColumnData);
        Assert.assertEquals(": null (0 Occurrences)", result);
    }

    class StubbedGridGroupingCellRenderer extends GridGroupingCellRenderer {

        public StubbedGridGroupingCellRenderer(final JSONGrid grid) {
            super(grid);
        }

        @Override
        ColumnModel getColumnModelFromGrid() {
            return mockedColumnModel;
        }

    }

}
