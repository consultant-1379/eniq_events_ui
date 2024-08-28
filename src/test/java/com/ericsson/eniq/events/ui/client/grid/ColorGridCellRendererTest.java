/**
 /*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.eniq.events.ui.client.grid;

import static junit.framework.Assert.*;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.store.ListStore;
import org.junit.Test;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.datatype.GridCellColorType;
import com.extjs.gxt.ui.client.widget.Html;
import org.jmock.Expectations;
import org.junit.Before;
import java.util.*;

public class ColorGridCellRendererTest extends TestEniqEventsUI {

    ModelData mockedModelData;
    ColumnData mockedColumnData;
    ListStore mockedListStore;
    Grid mockedGrid;

    @Before
    public void setUp() {
        mockedModelData = context.mock(ModelData.class);
        mockedColumnData = context.mock(ColumnData.class);
        mockedListStore = context.mock(ListStore.class);
        mockedGrid = context.mock(Grid.class);
    }

    private void setExpectations() {
        final HashMap rowValues = new HashMap();
        rowValues.put("NO_LINK", "locationServices");
        context.checking(new Expectations() {
            {
                allowing(mockedModelData).get(with(any(String.class)));
                will(returnValue("Call Drops"));
                allowing(mockedModelData).getProperties().values();
                will(returnValue(rowValues));
            }
        });
    }

    @Test
    public void createMultiMap() throws Exception {

        String url = "*,CS_NETWORK_EVENT_ANALYSIS_DRILL_ON_EVENTTYPE_BY_CELL,locationServices,CS_NETWORK_EVENT_ANALYSIS_DRILL_ON_EVENTTYPE_BY_CELL_locationServices,mSOriginatingSMSinMSC,CS_NETWORK_EVENT_ANALYSIS_DRILL_ON_EVENTTYPE_BY_CELL_SMS,mSTerminatingSMSinMSC,CS_NETWORK_EVENT_ANALYSIS_DRILL_ON_EVENTTYPE_BY_CELL_SMS";
        GridCellColorType colorInfo = null;
        final ColorGridCellRenderer objToTest = new ColorGridCellRenderer(url,
                colorInfo);
        objToTest.setupMultipleRowMap();
        assertNotNull(objToTest.mulipleURLsMap);

    }

    @Test
    public void multiMapNotCreated() throws Exception {

        String url = "NETWORK_EVENT_ANALYSIS_DRILL_ON_EVENTTYPE_BY_SGSN";
        GridCellColorType colorInfo = null;
        final ColorGridCellRenderer objToTest = new ColorGridCellRenderer(url,
                colorInfo);
        objToTest.setupMultipleRowMap();
        assertNull(objToTest.mulipleURLsMap);

    }

    @Test
    public void renderZeroColumnNoHyperlink() throws Exception {
        ColorGridCellRenderer objToTest = new ColorGridCellRenderer("myUrl",
                false);
        Html html = objToTest.renderHyperLink("text");
        System.out.println(html.getHtml());
    }

    @Test
    public void createMultiMapForNoLink() throws Exception {

        String url = "*,CS_NETWORK_EVENT_ANALYSIS_DRILL_ON_EVENTTYPE_BY_CELL,locationServices,NO_LINK";
        GridCellColorType colorInfo = null;
        final ColorGridCellRenderer objToTest = new ColorGridCellRenderer(url,
                colorInfo);
        objToTest.setupMultipleRowMap();
        assertNotNull(objToTest.mulipleURLsMap);

    }

    @Test
    public void testRenderForNoLink() throws Exception {
        String url = "*,CS_NETWORK_EVENT_ANALYSIS_DRILL_ON_EVENTTYPE_BY_CELL,locationServices,NO_LINK";
        GridCellColorType colorInfo = null;
        setExpectations();
        final ColorGridCellRenderer objToTest = new ColorGridCellRenderer(url,
                colorInfo);
        objToTest.render(mockedModelData, "locationServices", mockedColumnData,
                2, 1, mockedListStore, mockedGrid);
    }

}