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

import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType;
import com.ericsson.eniq.events.ui.client.gin.EniqEventsUIGinjector;
import com.ericsson.eniq.events.ui.client.grid.listeners.PagingFilterListener;
import com.ericsson.eniq.events.ui.client.grid.listeners.RefreshGridFromServerListener;
import com.ericsson.eniq.events.ui.client.grid.listeners.RefreshListener;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ericsson.eniq.events.common.client.json.MetaDataParserUtils.GRID_GROUPING_VIEW;
import static com.ericsson.eniq.events.ui.client.common.CSSConstants.*;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;

/**
 * Component Class - Grid Control that gets JSON objects that are used to
 * initialise control.
 *
 */
public class JSONGrid extends Grid<ModelData> {

    private static final String GRID_STATE_STANDALONE_ID_PREFIX = "STANDALONE_";

    private final static Logger LOGGER = Logger.getLogger(JSONGrid.class.getName());

    private final EniqEventsUIGinjector injector = MainEntryPoint.getInjector();

    private GridInfoDataType columns;

    private GridModel gridModel;

    private GridFilters gridFilters;

    CacheStore cacheStore;

    private static int rowsPerPage;

    private final ToolBar bottomToolBar;

    private JSONValue data;

    private RefreshListener refreshListener;

    private ModelData record;

    private String sortField;

    private SortDir sortDirection;

    public void setWindowType(MetaMenuItemDataType.Type windowType) {
        this.windowType = windowType;
    }

    private String gridCategoryId;

    private final JSONGridStateManager stateManager;
    private AllGridViewConfig allGridViewconfig;

    private MetaMenuItemDataType.Type windowType;

    public JSONGrid() {
        super();
        stateManager = MainEntryPoint.getInjector().getJsonGridStateManager();
        super.view = getGridView();
        super.sm = new GridSelectionModel<ModelData>();
        super.sm.bindGrid(null);

        rowsPerPage = getGridRowsPerPageFromMetaReader();
        bottomToolBar = createGridPagingToolBar();

        /* wait until table population until button toolbar (inc refresh) enabled */
        bottomToolBar.setEnabled(false);
        setColumnReordering(true);
    }

    /**
     * Stateful grid preserving user hidden columns and width changes etc when
     * open window next time.
     * <p/>
     * Ensure ID for stateful cookie is unique per grid definition, e.g.
     * NETWORK_EVENT_ANALYSIS is bad one to use in a cookie for hidden columns or
     * width changes as the hidden1, hidden2 etc is not in the same place across
     * APN, BSC, etc
     * <p/>
     * <p/>
     * NETWORK_EVENT_ANALYSIS_APN={"state":{"limit":"i:50", "hidden6":false,
     * "sortField":"s:4", "hidden5":false, "offset":"i:0", "sortDir":"s:ASC"}}
     * <p/>
     * (The cookies can be seen in local development mode console (when using
     * liveload as it happens, or else debug
     * StateManager.get().getMap(fixedQueryId)
     *
     * @param fixedQueryId unique id to provide for stateful id, r.h.
     */
    public void setupStateful(final String fixedQueryId, final String gridCategoryId) {
        setStateId(GRID_STATE_STANDALONE_ID_PREFIX + fixedQueryId);
        this.gridCategoryId = gridCategoryId;
        stateManager.init((IGridView) getView(), this);
    }

    /**
     * Resets grids state
     */
    public void resetState() {
        getState().put(OFFSET, 0);
    }

    @Override
    protected void afterRenderView() {
        super.afterRenderView();

        if (this.sortDirection != null && this.sortField != null) {
            super.getView().getHeader().updateSortIcon(cm.getIndexById(this.sortField), sortDirection);
        }
    }

    /*
    * Called from GXT, even if GXT state is not being saved
    * (non-Javadoc)
    * @see com.extjs.gxt.ui.client.widget.Component#initState()
    */
    @Override
    protected void initState() {
        super.initState();

        if (stateManager.hasSavedState()) {

            this.sortField = stateManager.getSortField();
            this.sortDirection = SortDir.valueOf(stateManager.getSortDir());
            /** Sort the GroupingView here **/
            if ((store.getLoader() == null || !store.getLoader().isRemoteSort()) && (sortField != null || !sortField.isEmpty())) {
                store.sort(sortField, sortDirection);
            }

            /** Paging sorting **/
            final PagingLoadConfig config = new BasePagingLoadConfig();
            config.setSortField(sortField);
            config.setSortDir(sortDirection);
            /** No Data State for Group Grid View **/
            config.setLimit(stateManager.getLimit());
            config.setOffset(stateManager.getOffset());

            if (cacheStore != null) {
                cacheStore.getPagingLoader().load(config);
            }

        }
    }

    ;

    /*
    * Added to support junit test
    *
    * @return paging tool bar wrapper
    */
    GridPagingToolBar createGridPagingToolBar() {
        return new GridPagingToolBar(rowsPerPage);
    }

    /* extracted for unit test */
    ToolBar createFooterToolBar() {
        return new FooterToolBar();
    }

    /**
     * Added to support junit test
     *
     * @return number of rows per page from configuration set up
     */
    Integer getGridRowsPerPageFromMetaReader() {
        return injector.getMetaReader().getGridRowsPerPage();
    }

    /**
     * @return the data
     */
    public JSONValue getData() {
        return data;
    }

    /**
     * @return the refreshListener
     */
    public RefreshListener getRefreshListener() {
        return refreshListener;
    }

    /**
     * @return the cacheStore
     */
    public CacheStore getCacheStore() {
        return cacheStore;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    public ModelData getRecord() {
        return record;
    }

    /**
     * @param record the record to set
     */
    public void setRecord(final ModelData record) {
        this.record = record;
    }

    /**
     * Fetch footer toolbar to place on window
     *
     * @return toolbar to place at footer of window
     */
    public ToolBar getBottomToolbar() {
        return bottomToolBar;
    }

    /**
     * sets the JSON object containing columns
     *
     * @param cols
     */
    public void setColumns(final GridInfoDataType cols) {

        /* needed when goin from standard view to grouping grid */
        boolean hasGridTypechanged = false;
        if (columns != null && !GRID_GROUPING_VIEW.equals(columns.gridType)) {
            hasGridTypechanged = true;
        }
        columns = cols;
        if (columns.gridType != null && GRID_GROUPING_VIEW.equals(columns.gridType) && !hasGridTypechanged) {
            initGroupingView();
        }
    }

    protected AllGridViewConfig setAllGridViewConfig(GridInfoDataType columns){
        return new AllGridViewConfig(windowType, columns);
    }

    /**
     * Apply Time Gap grid rendering if required.
     * @param view
     * @param columns
     */
    private void setGridRendering(GridView view, GridInfoDataType columns) {
          setGridStyles(columns);
          setHeaderWidth();
          allGridViewconfig = setAllGridViewConfig(columns);
          view.setViewConfig(allGridViewconfig);
    }

    private void setGridStyles(GridInfoDataType gridInfoDataType){
        if(!gridInfoDataType.timeGapWithColumn.equals("0"))   {
            this.addStyleName(TIME_GAP_GRID_CSS);
        }
    }

    /**
     *  Adjusts the header width to match the cell width.
     *  relevant for Core PS -Failed Event Analysis grids.
     *  Caused by color block added to Event result column.
     */
    private void  setHeaderWidth(){
        if(columns.gridId.equals("NETWORK_EVENT_ANALYSIS_DRILL_ON_EVENTTYPE_BY_ELEMENT") || columns.gridId.equals("SUBSCRIBER_EVENT_ANALYSIS")){
            this.addStyleName(FIX_HEADER);
        }
    }

    /**
     * sets the JSON object containing columns does not reset set the view used if
     * this is a grouping grid as a reset is causing issues hence reason for this
     * method and setColumns
     *
     * @param cols
     */
    public void resetColumns(final GridInfoDataType cols) {
        columns = cols;
    }

    public void setColumnModel(final ColumnModel cm) {
        this.cm = cm;
    }

    /**
     * gets the JSON object containing columns
     *
     * @return JSONValue
     */
    public GridInfoDataType getColumns() {
        return columns;
    }

    /**
     * gets the GridFilters object that has been bound to the grid as a plugin
     *
     * @return
     */
    public GridFilters getFilters() {
        return gridFilters;
    }

    /**
     * sets the listener to use for the refresh button on the grid paging tool bar
     * (with current search data for guard in multiple instances mode) needed to
     * do it this way in order for handler to have access to the eventBus
     *
     * @param refreshListener - RefreshGridFromServerListener
     */
    @SuppressWarnings("hiding")
    public void replaceRefreshBtnListener(final RefreshGridFromServerListener refreshListener) {
        if (bottomToolBar instanceof GridPagingToolBar) {
            ((GridPagingToolBar) bottomToolBar).replaceRefreshBtnListener(refreshListener);
        }
    }

    /**
     * Method to call on window close down
     */
    public void cleanUpOnClose() {
        if (bottomToolBar instanceof GridPagingToolBar) {
            ((GridPagingToolBar) bottomToolBar).cleanUpOnClose();
        }
        removeAllListeners();

        /*
        * add a #clearState() here if want to avoid problems from state manager -
        * if they arise. If don't clear cookies are controlling future opening of
        * grid - hidden columns, etc (but would we really want this done with
        * cookies)
        */

    }

    /**
     * Sets the JSON object containing columns
     * <p/>
     * Also updates the maximum size label on the tool bar if the configured tool
     * bar displays this label (i.e. called after successful response from server
     * call).
     *
     * @param gridData data for grid (from refresh call or from initial population
     */
    public void setData(final JSONValue gridData) {
        data = gridData;
    }

    /**
     * Only do for (success) server calls - i.e. resets current TimeStamp for
     * window
     *
     * @param response response from server call
     */
    public void upDateLastRefreshedLabel(Response response) {
        ((IBottomToolBar) bottomToolBar).upDateLastRefreshedLabel(response);
    }

    /**
     * get the number of rows of data that the grid contains in its bound data
     * object
     *
     * @return
     */
    public int getGridRowCount() {
        if (cacheStore != null) {
            return (cacheStore.getStore() == null ? 0 : cacheStore.getStore().getCount());
        }
        return 0;
    }

    /* Method added for unit testing */
    void setSuperStore(final ListStore<ModelData> store) {
        super.store = store;
    }

    /**
     * Binds the Grid for display based on the properties that have been provided
     */
    public void bind() {

        if (columns != null) {
            super.view = getGridView();

            setGridRendering(view, columns);

            // Check if columns have been supplied
            gridModel = createGridModelIfRequired();

            // No Columns equals no data so place this code inside if statement
            if (data != null) {
                initData();
            }

        }
    }

    /**
     * Return GridColumnView if the columns need grouping. Return existing
     * GridView otherwise.
     */
    GridView getGridView() {

        final boolean isAlreadyGridColumnView = view != null && view instanceof GridColumnView;

        if (isAlreadyGridColumnView) {
            /* using  breadcrumb navigation from column view to another column vieww (two steps back)
            * creating a new one hear crashes with nullpointer others wise)
            */
            return view;
        }

        if (columns != null && columns.licenceTypes != null && !columns.licenceTypes.isEmpty()) {
            return createGridColumnView();
        }
        return super.view != null ? super.view : new JSONGridView();

    }

    /* GXT 2.2.4 junit */
    GridColumnView createGridColumnView() {
        final GridColumnView gridColumnView = new GridColumnView(columns);
        stateManager.init(gridColumnView, this);
        return gridColumnView;
    }

    /* extract for junit to override */
    String getColumnHeader(final int colIndex) {
        return cm.getColumn(colIndex).getHeader();
    }

    /* extract for junit to override */
    String getColumnID(final int colIndex) {
        return cm.getColumn(colIndex).getId();
    }

    /* extract for junit to override */
    CacheStore createCacheStore() {
        return new CacheStore(data, gridModel.type, gridFilters);
    }

    /* extract for junit to override */
    PagingFilterListener createPagingFilterListener(final JSONGrid jsongrid,
            final PagingLoader<PagingLoadResult<ModelData>> loader) {
        return new PagingFilterListener(jsongrid, loader);
    }

    /* extract for junit to override */
    public String getGridType() {
        return (columns == null ? EMPTY_STRING : columns.gridType);
    }

    public void refreshGrid() {
        /* Returns a loader for a page-able set of data */
        final BasePagingLoader<PagingLoadResult<ModelData>> pagingloader = cacheStore.getPagingLoader();
        /* sorting applies to full recordset as opposed to individual pages */
        pagingloader.setRemoteSort(true);

        ListStore<ModelData> listStore = null;
        if (columns.groupingColumn.length() > 0) {
            listStore = new CaselessGroupingStore<ModelData>(cacheStore.getCacheloader());
            groupByWithOffCheck((CaselessGroupingStore<ModelData>) listStore); // columnid
            // will
            // be
            // read
            // from
            // metadata
            // for
            // the
            // grid
            cacheStore.getCacheloader().load();
        } else {
            listStore = new CaselessListStore<ModelData>(pagingloader);
            if (bottomToolBar instanceof PagingToolBar) { // wrong base toolbar
                // marginally better than
                // falling over
                ((PagingToolBar) bottomToolBar).bind(pagingloader);
                pagingloader.load();
                ((PagingToolBar) bottomToolBar).refresh();
            }
        }
        listStore.sort(columns.sortColumn, SortDir.findDir(columns.sortDirection));

        super.store = listStore;

        this.reconfigure(super.store, gridModel.cm);
    }

    /*
    * Know its a grouping grid when call this Turning off "show in groups" as
    * default start up for grouping grid (identified by presence of
    * columns.groupingColumn).
    *
    * Set grouping column to negiative number means turn off at start up (mostly
    * used for cause code grid which was grouped by cause code column, so setting
    * all these numbers negative)
    */
    private void groupByWithOffCheck(final GroupingStore<ModelData> listStore) {

        if (Integer.parseInt(columns.groupingColumn) > 0) {
            listStore.groupBy(columns.groupingColumn);
        }
    }

    /*
    * reconfigure the grid
    */
    public void reconfigureGrid() {
        this.reconfigure(super.store, gridModel.cm);

    }

    /*
    * To enable Select+Copy text in grids for Chrome browser. GWT avoids
    * mousedown event's default action for Chrome. Below override ensures it
    * doesn't.
    */
    @Override
    protected void onMouseDown(final GridEvent<ModelData> e) {

        if (e.getRowIndex() != -1) {
            fireEvent(Events.RowMouseDown, e);
            if (e.getColIndex() != -1) {
                fireEvent(Events.CellMouseDown, e);
            }
        }
    }

    /*
    * initialises the view that is used to represent the data in a grouping grid
    */
    private void initGroupingView() {
        final JSONGridGroupingView view1 = new JSONGridGroupingView();
        view1.setShowGroupedColumn(false);
        view1.setStartCollapsed(true);
        view1.setForceFit(false);
        view1.setGroupRenderer(new GridGroupingCellRenderer(this));
        if (columns != null) { // Addition for GXT upgrade
            gridModel = createGridModelIfRequired();
            this.setColumnModel(gridModel.cm);
        }
        super.setView(view1);
        stateManager.init(view1, this);
        super.setBorders(true);
    }

    /**
     * Initialises the grid data based on the JSON data object that has been
     * supplied
     */
    private void initData() {
        try {
            cacheStore = createCacheStore();

            /*
            * encapsulating the client side cache of ModelData objects, assigning it
            * to the grid store
            */
            final BasePagingLoader<PagingLoadResult<ModelData>> pagingloader = cacheStore.getPagingLoader();
            // sorting applied on full resultset rather than page specific
            pagingloader.setRemoteSort(true);
            ListStore<ModelData> listStore = null;
            if (columns.groupingColumn != null && columns.groupingColumn.length() > 0) {
                listStore = new GroupingStore<ModelData>(cacheStore.getCacheloader());
                groupByWithOffCheck((GroupingStore<ModelData>) listStore);
                if (stateManager.hasSavedState()) {

                    cacheStore.getStore().sort(stateManager.getSortField(), SortDir.valueOf(stateManager.getSortDir()));
                    cacheStore.getCacheloader().load();
                } else {
                    //Add For TR HQ44733.
                    //When there is groupingColumn in the json file, if there is no data in the UserPreferences,
                    // no data is showing in the Core Network Cause Code Analysis gird view.
                    cacheStore.getCacheloader().load();
                }
                /*
                * for grouping grid - leave the refresh button enabled always (and
                * remove refresh button from upper toolbar - to avoid two refresh
                * buttons)
                */
                if (bottomToolBar instanceof GridPagingToolBar) {
                    ((GridPagingToolBar) bottomToolBar).setEnabled(false, true);
                }

            } else {
                listStore = new ListStore<ModelData>(pagingloader);
                if (bottomToolBar instanceof GridPagingToolBar) {
                    ((GridPagingToolBar) bottomToolBar).bind(pagingloader);
                }
                if (stateManager.hasSavedState()) {
                    pagingloader.setSortDir(SortDir.valueOf(stateManager.getSortDir()));
                    pagingloader.setSortField(stateManager.getSortField());
                }
                pagingloader.load();
                this.addListener(Events.Attach, createPagingFilterListener(this, pagingloader));
                bottomToolBar.setEnabled(true);
            }

            super.setAutoExpandColumn(columns.colAutoExpand);
            /* GXT has a max set that renders the autoexpand useless, up the ante !!! */
            super.setAutoExpandMax(Integer.MAX_VALUE);

            int autoExpandMin;
            try {
                final int colAutoExpand = Integer.parseInt(columns.colAutoExpand);
                autoExpandMin = Integer.parseInt(columns.columnInfo[colAutoExpand - 1].columnWidth);
            } catch (final NumberFormatException e) {
                autoExpandMin = MIN_COLUMN_WIDTH;
            }
            super.setAutoExpandMin(autoExpandMin);

            super.setMinColumnWidth(MIN_COLUMN_WIDTH);
            /* need to reconfigure for drilldown screens to ensure changes are bound */
            this.reconfigure(listStore, gridModel.cm);

            this.refreshListener = new RefreshListener(this);

            setLoadMask(true);
            setBorders(true);
            setStripeRows(true);
            // TODO need to check
            //            cacheStore.getStore().addListener(Store.Filter, refreshListener);

        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, "JSONGrid: Error in initData", e);
        }
    }

    /*
    * Don't always create a new grid model e.g. don't create a new model for say
    * time update or a refresh of same grid or will loose user settings for
    * hidden fields.
    *
    * @return new GridModel or existing grid model if determine columns are not
    * changed. Returns null if GridInfoDataType is not set (columns)
    */
    private GridModel createGridModelIfRequired() {

        if (columns == null) {
            return null;
        }

        if (gridModel == null) {
            final GridModel model = createGridModel();
            /* initialise the filters to match the columns */
            gridFilters = new PagingGridFilters();
            gridFilters.setLocal(true);

            /* initialise the filters */
            final List<Filter> filters = getFilterModelList(model);
            for (final Filter objFilter : filters) {
                gridFilters.addFilter(objFilter);
            }
            // first time in bind the grid to the filters plugin
            super.addPlugin(gridFilters);
            return model;

        }
        /* this is a Column definition same as current i.e. Refresh grid */
        final GridInfoDataType existingColunms = gridModel.getGridInfoDataType();
        if (columns.equals(existingColunms)) {
            return gridModel;
        }

        /* the column defition has changed i.e. Drilldown to a different display */

        // Remove all the existing Filters
        for (int x = 0; x < gridModel.arrayColumns.size(); x++) {
            final Filter oFilter = gridFilters.getFilter(gridModel.cm.getColumnId(x));
            gridFilters.removeFilter(oFilter);

        }

        // Create a new GridModel as grid definition will be different;
        final GridModel newModel = new GridModel(columns, stateManager);
        // Create the new Filters to match the new column definition
        final List<Filter> filters = getFilterModelList(newModel);
        for (final Filter oNewFilter : filters) {
            gridFilters.addFilter(oNewFilter);
        }
        return newModel;

    }

    /* junit */
    List<Filter> getFilterModelList(final GridModel model) {
        return model.getFilterModelList();
    }

    /* Gxt junit 2.2.4 */
    GridModel createGridModel() {
        return new GridModel(columns, stateManager);
    }

    /**
     * @return the gridCategoryId
     */
    public String getGridCategoryId() {
        return gridCategoryId;
    }

    /**
     * @return the stateManager
     */
    public JSONGridStateManager getStateManager() {
        return stateManager;
    }

    /**
     * Whole result-set-aware filtering.
     *
     * @author ejedmar
     * @since 2012
     */
    private class PagingGridFilters extends GridFilters {

        @Override
        protected void reload() {
        }

        @Override
        protected void onStateChange(final Filter filter) {
            filterResultSet();
            super.onStateChange(filter);
        }

        private void filterResultSet() {
            final BasePagingLoader<PagingLoadResult<ModelData>> pagingLoader = cacheStore.getPagingLoader();

            final ListStore<ModelData> filteredListStore = new ListStore<ModelData>(pagingLoader);

            ((PagingToolBar) bottomToolBar).bind(pagingLoader);
            pagingLoader.load(JSONGrid.this.store.getLoadConfig());

            JSONGrid.this.reconfigure(filteredListStore, JSONGrid.this.gridModel.cm);
        }
    }

    public  int getRowsPerPage() {
        return rowsPerPage;
    }

    public void setRowsPerPage(int pageSize){
        this.rowsPerPage = pageSize;
        if (bottomToolBar instanceof GridPagingToolBar)
            ( (GridPagingToolBar)bottomToolBar).setPageSize(pageSize);
    }
}