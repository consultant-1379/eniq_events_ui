/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.preferences.IUserPreferencesHelper;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.JsonAutoBeanDataFactory;
import com.ericsson.eniq.events.ui.client.datatype.grid.IColumnState;
import com.ericsson.eniq.events.ui.client.datatype.grid.IGridState;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.google.inject.Inject;

/**
 * State Manager for the JSON Grid, saving and restoring the Grid and Column State. A grid can be saved/restored in two modes:
 * Standalone: Grid is saved/restored based on its grid id, the saved grid properties are specific to this grid only.
 * Category: Grid is a member of a category of grids. The Grid properties are saved/restored from the template for this category and saved
 * properties apply to all grid in the category.
 *
 * @author ecarsea
 * @since 2012
 */
public class JSONGridStateManager {
    private final IUserPreferencesHelper userPreferencesHelper;

    private static final String GRID_CATEGORY_TEMPLATE_KEY_PREFIX = "GRID_CATEGORY_TEMPLATE_KEY_";

    /**
     * Saved properties for the Grid Category template *
     */
    private IGridState gridCategoryState = null;

    /**
     * Saved properties for this grid only *
     */
    private IGridState gridStandaloneState;

    private IGridView gridView;

    private JSONGrid grid;

    /**
     * Id of the Category Template for this grid *
     */
    private String gridCategoryTemplateId = "";

    /**
     * Is there current saved state for this grid *
     */
    private boolean hasState;

    private final JsonAutoBeanDataFactory factory;

    /**
     * Flags to indicate if we need to save standalone state or category template state or both *
     */
    private boolean standAloneStateUnsaved, categoryStateUnsaved;

    /**
     * @param userPreferencesHelper
     * @param jsonAutoBeanDataFactory
     */
    @Inject
    public JSONGridStateManager(final IUserPreferencesHelper userPreferencesHelper,
                                final JsonAutoBeanDataFactory jsonAutoBeanDataFactory) {
        this.userPreferencesHelper = userPreferencesHelper;
        this.factory = jsonAutoBeanDataFactory;
    }

    /**
     * @param gridView
     * @param grid
     */
    @SuppressWarnings("hiding")
    public void init(final IGridView gridView, final JSONGrid grid) {
        this.gridView = gridView;
        this.grid = grid;
        gridView.setStateManager(this);
        /** Retrieve the Saved Properties for this grid only. **/
        gridStandaloneState = userPreferencesHelper.getStateById(grid.getStateId(), IGridState.class);
        /** If no standalone state for this grid, create object to hold state **/
        if (gridStandaloneState == null) {
            gridStandaloneState = factory.gridState().as();
        } else {
            hasState = true;
        }

        /** Get Grid Category Template State **/
        if (!grid.getGridCategoryId().isEmpty()) {
            gridCategoryTemplateId = GRID_CATEGORY_TEMPLATE_KEY_PREFIX + grid.getGridCategoryId();
            gridCategoryState = userPreferencesHelper.getStateById(gridCategoryTemplateId, IGridState.class);
            if (gridCategoryState == null) {
                gridCategoryState = factory.gridState().as();
            } else {
                hasState = true;
            }
        }
    }

    /**
     * Save state of grid
     */
    public void saveGridState() {
        saveStandaloneGridColumnState();
        if (gridCategoryState != null) {
            saveGridCategoryColumnState();
        }
        performSaveGridSortState();
        performSaveGridDataState();
        /** May not need to save both standalone and category state **/
        if (standAloneStateUnsaved) {
            userPreferencesHelper.setState(grid.getStateId(), IGridState.class, gridStandaloneState);
        }
        if (categoryStateUnsaved) {
            userPreferencesHelper.setState(gridCategoryTemplateId, IGridState.class, gridCategoryState);
        }
    }

    /**
     * Save the column properties of the grid to the category template for that grid
     */
    private void saveGridCategoryColumnState() {
        final List<IColumnState> columnStates = new ArrayList<IColumnState>();
        for (final ColumnInfoDataType columnInfo : grid.getColumns().columnInfo) {
            if (!columnInfo.columnType.isEmpty() && !isSystemColumn(columnInfo)) {
                final ColumnConfig columnConfig = grid.getColumnModel().getColumnById(columnInfo.columnID);
                columnStates.add(setColumnState(((EventGridColumnConfig) columnConfig).getColumnType(), columnConfig,
                        grid.getColumnModel().getIndexById(columnConfig.getId())));
            }
        }
        if (!columnStates.isEmpty()) {
            gridCategoryState.setColumnsState(columnStates);
            categoryStateUnsaved = true;
        }
    }

    /**
     * Save column properties of grid to standalone grid template
     */
    private void saveStandaloneGridColumnState() {
        final List<IColumnState> columnStates = new ArrayList<IColumnState>();
        for (final ColumnInfoDataType columnInfo : grid.getColumns().columnInfo) {
            if (columnInfo.columnType.isEmpty() && !isSystemColumn(columnInfo)) {
                final ColumnConfig columnConfig = grid.getColumnModel().getColumnById(columnInfo.columnID);
                columnStates.add(setColumnState(columnConfig.getId(), columnConfig,
                        grid.getColumnModel().getIndexById(columnConfig.getId())));
            }
        }

        if (!columnStates.isEmpty()) {
            gridStandaloneState.setColumnsState(columnStates);
            standAloneStateUnsaved = true;
        }
    }

    /**
     * @param columnInfo
     * @return
     */
    private boolean isSystemColumn(final ColumnInfoDataType columnInfo) {
        return columnInfo.isSystem;
    }

    /**
     * Create a column state object and set the properties to be saved
     *
     * @param columnType
     * @param columnConfig
     * @param index
     * @return
     */
    private IColumnState setColumnState(final String columnType, final ColumnConfig columnConfig, final int index) {
        final IColumnState columnState = factory.columnState().as();
        columnState.setColumnTypeId(columnType);
        columnState.setHidden(columnConfig.isHidden());
        columnState.setWidth(columnConfig.getWidth());
        columnState.setColumnIndex(index);
        return columnState;
    }

    /**
     * Save the sort field and sort direction of the grid to either the category template or the standalone template.
     */
    private void performSaveGridSortState() {
        final int sortColumn = grid.getColumnModel().getIndexById(gridView.getSortInfo().getSortField());
        if (sortColumn >= 0) {
            final EventGridColumnConfig columnConfig = (EventGridColumnConfig) grid.getColumnModel().getColumns()
                    .get(sortColumn);

            /** Save to template or standalone **/
            if (isGridInCategory() && !columnConfig.getColumnType().isEmpty()) {
                gridCategoryState.setSortDir(gridView.getSortInfo().getSortDir().toString());
                gridCategoryState.setSortField(columnConfig.getColumnType());
                categoryStateUnsaved = true;
            } else {
                gridStandaloneState.setSortField(gridView.getSortInfo().getSortField());
                gridStandaloneState.setSortDir(gridView.getSortInfo().getSortDir().toString());
                standAloneStateUnsaved = true;
            }
        }
    }

    /**
     * If its a paging grid, save limit and offset state to either the standalone or category template depending on whether the grid
     * belongs to a category.
     */
    private void performSaveGridDataState() {
        if (grid != null && gridView.getStore() != null && gridView.getStore().getLoadConfig() != null
                && gridView.getStore().getLoadConfig() instanceof PagingLoadConfig) {
            final PagingLoadConfig config = (PagingLoadConfig) gridView.getStore().getLoadConfig();
            IGridState gridState = null;
            if (isGridInCategory()) {
                gridState = gridCategoryState;
                categoryStateUnsaved = true;
            } else {
                gridState = gridStandaloneState;
                standAloneStateUnsaved = true;
            }
            gridState.setOffset(config.getOffset());
            gridState.setLimit(config.getLimit());
        }
    }

    /**
     * Has the grid any current saved state.
     *
     * @return
     */
    public boolean hasSavedState() {
        return hasState;
    }

    /**
     * Get the saved Sort Field from either the category template or the standalone saved properties
     *
     * @return
     */
    public String getSortField() {
        /** If sort field is saved in a category template, it will be saved by column type, need to convert to column id 
         * as the JSON Grid columns are indexed by the column id. If no state just return from metadata
         */
        if (gridCategoryState != null && gridCategoryState.getSortField() != null) {
            for (final ColumnInfoDataType columnInfo : grid.getColumns().columnInfo) {
                if (columnInfo.columnType.equals(gridCategoryState.getSortField())) {
                    return columnInfo.columnID;
                }
            }
        }
        return gridStandaloneState.getSortField() == null ? grid.getColumns().sortColumn.toString()
                : gridStandaloneState.getSortField();
    }

    /**
     * Get the saved Sort Direction from category template or standalone state. If no state just return from metadata
     *
     * @return
     */
    public String getSortDir() {
        if (gridStandaloneState != null) {
            return gridStandaloneState.getSortDir() == null ? grid.getColumns().sortDirection.toString()
                    : gridStandaloneState.getSortDir();
        }

        return gridCategoryState.getSortDir() == null ? grid.getColumns().sortDirection.toString()
                : gridCategoryState.getSortDir();


    }

    /**
     * Get the row limit per page from category template or standalone state. Check for null if no state. Return the meta data default
     * if we have no state for the limit
     *
     * @return
     */
    public int getLimit() {
        if (gridCategoryState != null) {
            return gridCategoryState.getLimit() == null ? grid.getGridRowsPerPageFromMetaReader() : gridCategoryState
                    .getLimit();
        }
        return gridStandaloneState.getLimit() == null ? grid.getGridRowsPerPageFromMetaReader() : gridStandaloneState
                .getLimit();
    }

    /**
     * Get the page offset from category template or standalone state. Check for null if no state.
     * Default offset is 0 so no need to save state if still 0;
     *
     * @return
     */
    public int getOffset() {
        if (gridCategoryState != null) {
            return gridCategoryState.getOffset() == null ? 0 : gridCategoryState.getOffset();
        }
        return gridStandaloneState.getOffset() == null ? 0 : gridStandaloneState.getOffset();
    }

    /**
     * Get the column states by retrieving the saved properties for each column from both standalone and category template (a column state will be saved in
     * either of these states, but not both).
     *
     * @return
     */
    public List<IColumnState> getColumnStates() {
        final List<IColumnState> columnStates = new ArrayList<IColumnState>();

        /** Standalone Column states **/
        if (gridStandaloneState.getColumnsState() != null) {
            try {
                columnStates.addAll(gridStandaloneState.getColumnsState());
            } catch (final Exception e) {
                // ignore, this exception is only thrown is old GXT state is attempted to be restored here
            }
        }

        if (isGridInCategory()) {
            /** Template Column States **/
            if (gridCategoryState.getColumnsState() != null) {
                columnStates.addAll(gridCategoryState.getColumnsState());
            }
        }
        /** Sort the columns by their position in the grid. This saves the column position in the grid which can be changed by reordering **/
        Collections.sort(columnStates, ColumnStateComparator.getInstance());
        return columnStates;
    }

    protected boolean isGridInCategory() {
        return !gridCategoryTemplateId.isEmpty();
    }

    /**
     * If grid sort state has changed, save grid state.
     */
    public void saveSortState() {
        /** Sort State is null in the view straight after rendering, the sorting is applied later **/
        if (gridView.getSortInfo().getSortField() != null && gridView.getSortInfo().getSortDir() != SortDir.NONE) {
            if (!gridView.getSortInfo().getSortDir().toString().equals(getSortDir())
                    || !gridView.getSortInfo().getSortField().equals(getSortField())) {
                saveGridState();
            }
        }
    }

    /**
     * if data state has changed save grid state
     */
    public void saveDataState() {
        if (grid != null && gridView.getStore() != null && gridView.getStore().getLoadConfig() != null
                && gridView.getStore().getLoadConfig() instanceof PagingLoadConfig) {
            final PagingLoadConfig config = (PagingLoadConfig) gridView.getStore().getLoadConfig();
            if (!(config.getOffset() == getOffset()) || !(config.getLimit() == getLimit())) {
                saveGridState();
            }
        }
    }
}
