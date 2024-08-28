/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.datatype.IHyperLinkDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.json.client.JSONValue;

/**
 * @author esuslyn
 *
 */
public class BreadCrumbMenuItemTest extends TestEniqEventsUI {

    private BreadCrumbMenuItem objToTest;

    private JSONValue mockedGridDate;

    private final String sTitle = "My Sample Window";

    private List<Filter> mockedFilters;

    private Map<Integer, Menu> mockedfilterMenus;

    private StoreFilter<ModelData> mockedStoreFilter;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        mockedGridDate = context.mock(JSONValue.class);
        mockedFilters = context.mock(List.class);
        mockedfilterMenus = context.mock(Map.class);
        mockedStoreFilter = context.mock(StoreFilter.class);
        objToTest = new StubbedBreadCrumbMenuItem("", 0, sTitle, null);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        objToTest = null;
    }

    /**
     * Test method for {@link com.ericsson.eniq.events.ui.client.common.comp.BreadCrumbMenuItem#saveGridConfigurations(com.ericsson.eniq.events.common.client.datatype.GridInfoDataType, com.google.gwt.json.client.JSONValue)}.
     */
    @Test
    public void testSaveGridConfigurations() {
        final TimeInfoDataType dateLabel = new TimeInfoDataType();
        dateLabel.timeRange = "30";
        final Map<Integer, StoreFilter<ModelData>> datefilters = new HashMap<Integer, StoreFilter<ModelData>>();
        datefilters.put(0, mockedStoreFilter);
        objToTest.saveGridConfigurations(new GridInfoDataType(), mockedGridDate, mockedFilters, sTitle, dateLabel,
                "last refreshed label", null, "stateId");
    }

    /**
     * Test method for {@link com.ericsson.eniq.events.ui.client.common.comp.BreadCrumbMenuItem#setGridDisplayed(boolean)}.
     */
    @Test
    public void testSetGridDisplayed() {
        objToTest.setGridDisplayed(true);
    }

    /**
     * Test method for {@link com.ericsson.eniq.events.ui.client.common.comp.BreadCrumbMenuItem#getIndex()}.
     */
    @Test
    public void testGetIndex() {
        objToTest.getIndex();
    }

    /**
     * Test method for {@link com.ericsson.eniq.events.ui.client.common.comp.BreadCrumbMenuItem#isGridDisplayed()}.
     */
    @Test
    public void testIsGridDisplayed() {
        objToTest.isGridDisplayed();
    }

    /**
     * Test method for {@link com.ericsson.eniq.events.ui.client.common.comp.BreadCrumbMenuItem#getGridInfoDataType()}.
     */
    @Test
    public void testGetGridInfoDataType() {
        objToTest.getGridInfoDataType();
    }

    /**
     * Test method for {@link com.ericsson.eniq.events.ui.client.common.comp.BreadCrumbMenuItem#getData()}.
     */
    @Test
    public void testGetData() {
        objToTest.getData();
    }

    private class StubbedBreadCrumbMenuItem extends BreadCrumbMenuItem {

        /**
         * @param title
         * @param index
         * @param winID
         * @param drillInfo
         */
        public StubbedBreadCrumbMenuItem(final String title, final int index, final String winID,
                final IHyperLinkDataType drillInfo) {
            super(title, index, winID, drillInfo);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void setIconStyle(final String icon) {

        }

    }

}
