/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.grid;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.junit.*;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.ToolBarStateManager;
import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType;
import com.ericsson.eniq.events.ui.client.grid.filters.DateTimeField;
import com.ericsson.eniq.events.ui.client.grid.listeners.PagingFilterListener;
import com.ericsson.eniq.events.ui.client.grid.listeners.RefreshGridFromServerListener;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.json.client.JSONValue;

public class JSONGridTest extends TestEniqEventsUI {

    JSONGrid objToTest;

    final Integer gridRowsPerPage = 1;

    JSONValue mockedGridData;

    BasePagingLoader<PagingLoadResult<ModelData>> mockedPagingloader;

    GridPagingToolBar mockedGridPagingToolBar;

    FooterToolBar mockedFooterToolBar;

    private ModelData mockedModelData;

    PagingFilterListener mockedPagingFilterListener;

    RefreshGridFromServerListener mockedRefreshGridFromServerListener;

    DateTimeField mockedDateFilterField;

    CacheStore mockedCacheStore;

    ListStore<ModelData> mockedCacheStoreInnerStore;

    StoreFilter<ModelData> mockedStoreFilterField;

    Menu mockedMenu;

    GridView mockedGridView;

    GridModel mockedGridModel;

    GridColumnView mockedGridColumnView;

    ColumnModel mockedColumnModel;

    public JsonObjectWrapper mockedMetaData;

    @Before
    public void setUp() {
        mockedGridData = context.mock(JSONValue.class);

        mockedPagingloader = context.mock(BasePagingLoader.class);
        mockedGridPagingToolBar = context.mock(GridPagingToolBar.class);
        mockedFooterToolBar = context.mock(FooterToolBar.class);
        mockedPagingFilterListener = context.mock(PagingFilterListener.class);
        mockedRefreshGridFromServerListener = context.mock(RefreshGridFromServerListener.class);
        mockedColumnModel = context.mock(ColumnModel.class);
        mockedModelData = context.mock(ModelData.class);
        mockedDateFilterField = context.mock(DateTimeField.class);
        mockedStoreFilterField = context.mock(StoreFilter.class);
        mockedCacheStore = context.mock(CacheStore.class);

        mockedCacheStoreInnerStore = context.mock(ListStore.class);
        mockedMenu = context.mock(Menu.class);
        mockedGridView = context.mock(GridView.class);

        mockedGridModel = context.mock(GridModel.class);
        mockedGridColumnView = context.mock(GridColumnView.class);

        mockedMetaData = context.mock(JsonObjectWrapper.class);
        context.checking(new Expectations() {
            {
                one(mockedGridPagingToolBar).setEnabled(false);

            }
        });
        objToTest = new StubbedJSONGrid(ToolBarStateManager.BottomToolbarType.PAGING);
    }

    @After
    public void tearDown() {
        objToTest = null;
    }

    private void testBindGrid(final String licenceTypes) {
        final ColumnInfoDataType colElement = new ColumnInfoDataType();
        colElement.columnWidth = "50";
        final GridInfoDataType testData = new GridInfoDataType();
        testData.licenceTypes = licenceTypes;
        testData.columnInfo = new ColumnInfoDataType[] { colElement };
        testData.gridId = "";

        context.checking(new Expectations() {
            {

                allowing(mockedGridView).addListener(with(any(EventType.class)), with(any(GridSelectionModel.class)));
                allowing(mockedGridView).setViewConfig(with(any(GridViewConfig.class)));
                allowing(mockedColumnModel).addListener(with(any(EventType.class)), with(any(GridSelectionModel.class)));

                ignoring(mockedGridView);
                ignoring(mockedCacheStore);
                ignoring(mockedGridPagingToolBar);

            }
        });
        objToTest.setColumns(testData);
        objToTest.setData(mockedGridData);
        objToTest.bind();
    }

    @Test
    public void bindGridToStore() {
        objToTest.setWindowType(MetaMenuItemDataType.Type.GRID);
        testBindGrid(null);
    }

    @Test
    public void bindGridWithLicenceToStore() {
        objToTest.setWindowType(MetaMenuItemDataType.Type.GRID);
        context.checking(new Expectations() {
            {
                ignoring(mockedGridColumnView);

            }
        });

        testBindGrid("3G,Voice");
    }

    @Test
    public void setDataJustSetsDataAndNothingElse() {
        objToTest.setData(mockedGridData);
    }

    @Test
    public void getJSONGridAsWidget() {
        objToTest.asWidget();
    }

    @Test
    public void getGridRowAsRecord() {
        objToTest.getRecord();
    }

    @Test
    public void saveRowSelectedInGrid() {
        objToTest.setRecord(mockedModelData);
    }

    @Test
    public void getPagingToolbar() {
        objToTest.getBottomToolbar();
    }

    @Test
    public void getGridMetaData() {
        objToTest.getColumns();
    }

    @Test
    public void setGridRefreshListener() {
        context.checking(new Expectations() {
            {
                one(mockedGridPagingToolBar).replaceRefreshBtnListener(mockedRefreshGridFromServerListener);
            }
        });
        objToTest.replaceRefreshBtnListener(mockedRefreshGridFromServerListener);
    }

    class StubbedJSONGrid extends JSONGrid {

        public StubbedJSONGrid(final ToolBarStateManager.BottomToolbarType bottomToolbar) {
            super();

        }

        @Override
        public List<Filter> getFilterModelList(final GridModel model) {
            return new ArrayList<Filter>();
        }

        @Override
        public GridView getGridView() {
            return mockedGridView;
        }

        @Override
        public GridColumnView createGridColumnView() {
            return mockedGridColumnView;
        }

        @Override
        public GridModel createGridModel() {
            return mockedGridModel;
        }

        @Override
        public CacheStore getCacheStore() {
            return mockedCacheStore;
        }

        @Override
        Integer getGridRowsPerPageFromMetaReader() {
            return gridRowsPerPage;
        }

        @Override
        GridPagingToolBar createGridPagingToolBar() {
            return mockedGridPagingToolBar;
        }

        @Override
        ToolBar createFooterToolBar() {
            return mockedFooterToolBar;
        }

        @Override
        CacheStore createCacheStore() {
            return mockedCacheStore;
        }

        @Override
        public ColumnModel getColumnModel() {
            return mockedColumnModel;
        }

        @Override
        PagingFilterListener createPagingFilterListener(final JSONGrid jsongrid, final PagingLoader<PagingLoadResult<ModelData>> loader) {
            return mockedPagingFilterListener;
        }

        @Override
        String getColumnHeader(final int colIndex) {
            return "col1";
        }

        @Override
        String getColumnID(final int colIndex) {
            return "col1";
        }

        @Override
        public  int getRowsPerPage() {
            return gridRowsPerPage;
        }

        @Override
        protected AllGridViewConfig setAllGridViewConfig(GridInfoDataType columns) {
            return new StubbedAllGridViewConfig(columns);
        }
    }

    class StubbedAllGridViewConfig extends AllGridViewConfig{

        public StubbedAllGridViewConfig(GridInfoDataType gridMetaData){
            super(MetaMenuItemDataType.Type.GRID, gridMetaData);
        }

        @Override
        public void setTimeGap() {
            //do nothing. calling JavaScript from JUnit doesn't work!
        }
    }
}
