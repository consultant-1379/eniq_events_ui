/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;

/**
 * @author edivkir
 * @since 2011
 *
 */
public class GridColumnView extends GridView implements IGridView { // NOPMD by edivkir on 27/06/11 16:17

    public GridInfoDataType gridInfo = null;

    final String[] licenceType;

    protected boolean licenceCreated = false;

    protected int cols;

    protected CheckMenuItem[][] columnsWithMultiLicence;

    final Map<String, String> columnIdMap = new HashMap<String, String>();

    final Map<String, Boolean> columnStateMap = new HashMap<String, Boolean>();

    protected GridColumnsData gridColumndata;

    protected Map<String, Boolean> selectAllStateMap = new HashMap<String, Boolean>();

    Map<String, Object> state;

    private JSONGridStateManager stateManager;

    public GridColumnView(final GridInfoDataType columnInfo) {
        super(); // GXT 2.2.4 calls  XDOM.getScrollBarWidth() so UNTESTABLE
        this.gridInfo = columnInfo;
        licenceType = gridInfo.licenceTypes.split(",");

    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void init(final Grid columnGrid) {
        super.init(columnGrid);
        cols = grid.getColumnModel().getColumnCount();
        gridColumndata = new GridColumnsData(gridInfo, getColumnModel());
        columnsWithMultiLicence = getCheckMultiMenuItems();
        state = grid.getState();
    }

    /**
     * This method creates the contextMenu. There are 3 Menu Items in this menu:
     * 1 - Sort Ascending - createAscMenuItem()
     * 2 - Sort Descending - createDescMenuItem()
     * 3 - Columns - This menuItem expands and open up another Menu - licenceMenu()
     */
    @Override
    protected Menu createContextMenu(final int colIndex) {
        /*
         *As column reordering is done on the base of column index, so each time reordering take place column index change.
         *So license menu must be configured on the bases of columnid rather than column index.
         */
        final Menu menu = getMenu();
        if (getColumnModel().isSortable(colIndex)) {
            MenuItem item = new MenuItem();
            item.setText(GXT.MESSAGES.gridView_sortAscText());
            item.setIcon(getImages().getSortAsc());
            item.addSelectionListener(new SelectionListener<MenuEvent>() {
                @Override
                public void componentSelected(final MenuEvent ce) {
                    doSort(colIndex, SortDir.ASC);
                }
            });
            menu.add(item);

            item = new MenuItem();
            item.setText(GXT.MESSAGES.gridView_sortDescText());
            item.setIcon(getImages().getSortDesc());
            item.addSelectionListener(new SelectionListener<MenuEvent>() {
                @Override
                public void componentSelected(final MenuEvent ce) {
                    doSort(colIndex, SortDir.DESC);
                }
            });
            menu.add(item);
        }
        final MenuItem columns = new MenuItem();
        columns.setText(GXT.MESSAGES.gridView_columnsText());
        columns.setIcon(getImages().getColumns());
        columns.setSubMenu(createLicenceMenu());

        menu.add(columns);
        return menu;
    }

    /*
     * This creates the Licence Menu which will contain all Licences as per JsonObjectWrapper.
     * These Menu items will further expand and add actual columns or SubLicence. 
     */
    protected Menu createLicenceMenu() {

        final Menu licenceMenu = getMenu();
        licenceMenu.add(createActiveColumnsMenu());
        licenceMenu.add(new SeparatorMenuItem());
        final Map<String, Set<String>> licenceMap = gridColumndata.getlicenceGroupingMap();
        for (final String licence : licenceType) {
            final MenuItem licenceMenuItem = new MenuItem(); // NOPMD by edivkir on 28/07/11 15:10
            licenceMenuItem.setText(licence);
            final Set<String> licenceGroup = licenceMap.get(licence);
            licenceMenuItem.setSubMenu((licenceGroup != null) ? createSubLicenceMenu(licence, licenceGroup)
                    : createColumnMenu(licence, null));
            licenceMenu.add(licenceMenuItem);
        }
        licenceMenu.add(new SeparatorMenuItem());

        final MenuItem defaultMenuItem = new MenuItem();
        final Menu columnMenuForDefault = createColumnMenu(EMPTY_STRING, EMPTY_STRING);
        defaultMenuItem.setText(DEFAULT_COLUMN_GROUP);
        defaultMenuItem.setSubMenu(columnMenuForDefault);
        licenceMenu.add(defaultMenuItem);

        return licenceMenu;
    }

    /*
     * For 2G, 3G and 4G core, where sublicence menu is required.
     */
    private Menu createSubLicenceMenu(final String licence, final Set<String> menus) {
        final Menu subLicenceMenu = getMenu();

        final Iterator<String> it = menus.iterator();

        while (it.hasNext()) {
            final String subMenu = it.next();
            final MenuItem subLicenceMenuItem = new MenuItem(); // NOPMD by edivkir on 28/07/11 15:10
            subLicenceMenuItem.setText(subMenu);

            subLicenceMenuItem.setSubMenu(createColumnMenu(licence, subMenu));
            subLicenceMenu.add(subLicenceMenuItem);
        }
        return subLicenceMenu;
    }

    /*
     * Each licence group or subgroup should have 1 checkBox "select all"
     * And checking it should select all the columns in the group/subgroup. 
     */
    protected CheckMenuItem createSelectAll(final Menu columnMenu, final String key) {
        final CheckMenuItem selectAll = new CheckMenuItem();
        selectAll.setText(SELECT_ALL);
        selectAll.setHideOnClick(false);
        selectAll.setChecked((Boolean) state.get(key));
        selectAll.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(final MenuEvent ce) {
                checkLicenceMenu(columnMenu, (CheckMenuItem) ce.getItem(), key);
                if (key.equals(ACTIVE_COLUMN_GROUP) && !((CheckMenuItem) ce.getItem()).isChecked()) {
                    gridColumndata.reset(state, selectAllStateMap);
                }
            }
        });
        return selectAll;
    }

    /*
     * 1st group in column chooser is "Active Columns" and it contains the list of columns which are shown on the grid at that moment.
     * Note: that this group gets updated everytime you call createContextMenu; not at run time!
     */
    protected MenuItem createActiveColumnsMenu() {
        final MenuItem activeColMenuItem = new MenuItem();
        activeColMenuItem.setText(ACTIVE_COLUMN_GROUP);

        final Menu columnMenu = getMenu();
        state.put(ACTIVE_COLUMN_GROUP, true);

        final CheckMenuItem selectAll = createSelectAll(columnMenu, activeColMenuItem.getText());
        columnMenu.add(selectAll);
        columnMenu.add(new SeparatorMenuItem());

        for (int i = 0; i < getGridColumnCount(); i++) {
            final int fcol = i;
            final CheckMenuItem check = new CheckMenuItem(); // NOPMD by edivkir on 28/07/11 15:10
            check.setHideOnClick(false);
            check.setText(getColumnModel().getColumnHeader(i));
            final boolean isColumnHidden = getColumnModel().isHidden(i);
            check.setChecked(!isColumnHidden);

            restrictMenu(check);
            if (!isColumnHidden) {
                columnMenu.add(check);
            }

            check.addSelectionListener(new SelectionListener<MenuEvent>() { // NOPMD by edivkir on 28/07/11 15:10
                @Override
                public void componentSelected(final MenuEvent ce) {
                    if (selectAll != null) {
                        selectAll.setChecked(shouldCheckSelectAll(columnMenu));
                    }
                    cm.setHidden(fcol, !cm.isHidden(fcol));
                }
            });
        }
        activeColMenuItem.setSubMenu(columnMenu);
        return activeColMenuItem;
    }

    /* 
     * This method adds actual column list to the groups as per the metaData - 
     * Eg: "licence" : "3G", will add this column to 3G licence Menu.
     */
    protected Menu createColumnMenu(final String licence, final String subLicenceMenu) { // NOPMD by edivkir on 28/07/11 15:07

        final Menu columnMenu = getMenu();
        String licenceKey = EMPTY_STRING;

        licenceKey = (licence != null && !licence.isEmpty()) ? ((subLicenceMenu == null || subLicenceMenu
                .equals(OTHER_COLUMN_GROUP)) ? licence : (licence + "-" + subLicenceMenu)) : EMPTY_STRING;

        if (!state.containsKey(licenceKey)) {
            state.put(licenceKey, false);
            state.put(EMPTY_STRING, true);
            selectAllStateMap.put(licenceKey, false);
            selectAllStateMap.put(EMPTY_STRING, true);
        }

        final CheckMenuItem selectAll = createSelectAll(columnMenu, licenceKey);
        columnMenu.add(selectAll);
        columnMenu.add(new SeparatorMenuItem());

        //menu should be configured on base of columnid as column index keep changing while column reording takes place
        for (final ColumnInfoDataType columnInfo : gridInfo.columnInfo) {
            final String columnLicence = columnInfo.columnLicence;
            final String[] multipleColumnLicences = columnLicence.split(",");

            final ColumnModel columnModel = getColumnModel();
            final ColumnConfig column = columnModel.getColumnById(columnInfo.columnID);
            final String headerStr = column.getHeader();
            if (headerStr == null || headerStr.isEmpty() || column.isFixed()) {
                continue;
            }

            final CheckMenuItem check = getCheckMenuForColumns(column, selectAll, columnMenu, multipleColumnLicences,
                    licenceKey);
            restrictMenu(check);
            final int i = getColumnIndexById(columnInfo.columnID);
            if (multipleColumnLicences.length > 1) {
                for (int j = 0; j < multipleColumnLicences.length; j++) {
                    if (multipleColumnLicences[j].equals(licenceKey)) {
                        columnsWithMultiLicence[i][j] = check;
                        columnMenu.add(columnsWithMultiLicence[i][j]);
                    }
                }

            } else if (columnLicence.equals(licenceKey)) {
                columnMenu.add(check);
            }
        }
        return columnMenu;
    }

    protected CheckMenuItem getCheckMenuForColumns(final ColumnConfig column, final CheckMenuItem selectAll,
            final Menu columnMenu, final String[] multipleColumnLicences, final String key) {

        final CheckMenuItem check = new CheckMenuItem();
        check.setHideOnClick(false);
        check.setText(column.getHeader());
        check.setChecked(!column.isHidden());
        final String colId = column.getId();

        check.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(final MenuEvent ce) {

                //Licence Menu Item should also be checked or unchecked based on column selection; as per shouldCheckLicenceMenu.
                if (selectAll != null) {
                    final boolean shouldCheckSelectAll = shouldCheckSelectAll(columnMenu);
                    selectAll.setChecked(shouldCheckSelectAll);
                    state.put(key, shouldCheckSelectAll);
                }
                checkAllColumnsWithSameLicence((CheckMenuItem) ce.getItem(), multipleColumnLicences);

                final int fcol = getColumnIndexById(colId);
                cm.setHidden(fcol, !cm.isHidden(fcol));
            }
        });

        return check;
    }

    /**
     * Retrieve current column index of column in grid by its column id
     *  
     * @param id column id
     * @return column index
     */
    private int getColumnIndexById(final String id) {
        final List<ColumnConfig> columns = cm.getColumns();
        final int size = columns.size();
        for (int i = 0; i < size; i++) {
            final ColumnConfig column = columns.get(i);
            if (column != null && column.getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    /*=========================================================================================
     * Private/protected methods: mainly for conditions - when to check or uncheck all column or licence menu etc 
     * Or  What should happen when check
    ========================================================================================= */
    /*
    * Check/uncheck "Select All":
    * 1 - check/uncheck all columns inside
    * 2 - unhide/hide all the columns in grid 
    */
    protected void checkLicenceMenu(final Menu columns, final CheckMenuItem checkItem, final String key) { // NOPMD by edivkir on 28/07/11 15:08
        final boolean check = checkItem.isChecked();
        state.put(key, check);
        //        for (int i = 0; i < cm.getColumnCount(); i++) {
        //            columnIndexMap.put(cm.getColumnHeader(i), i);
        //            columnStateMap.put(cm.getColumnHeader(i), cm.isHidden(i));
        for (final ColumnConfig col : cm.getColumns()) {
            final String colHeader = col.getHeader();
            columnIdMap.put(colHeader, col.getId());
            columnStateMap.put(colHeader, col.isHidden());
        }
        /* Dirty hack to check/uncheck column-checkboxes for exact licence. Can't put it on the next loop, 
         * since it overrides the checking. May be a TODO to avoid double looping.*/
        for (final Component item : columns.getItems()) {
            if (!(item instanceof SeparatorMenuItem)) {
                final CheckMenuItem cmi = (CheckMenuItem) item;
                if (!GridColumnsData.isColumnEventTime(cmi.getText())) {
                    cmi.setChecked(check);
                }
            }
        }

        for (final Component item : columns.getItems()) {
            if (!(item instanceof SeparatorMenuItem)) {
                final CheckMenuItem cmi = (CheckMenuItem) item;
                //                if (cmi.getText().equals(SELECT_ALL) || GridColumnsData.isColumnEventTime(cmi.getText())) {
                final String colHeader = cmi.getText();
                if (colHeader.equals(SELECT_ALL) || GridColumnsData.isColumnEventTime(colHeader)) {
                    continue;
                }
                //                if (columnStateMap.get(cmi.getText()) != !check) {
                //                    cm.getColumn(columnIndexMap.get(cmi.getText())).setHidden(!check);
                //                    this.onHiddenChange(columnIndexMap.get(cmi.getText()), !check);
                if (columnStateMap.get(colHeader) != !check) {
                    final String id = columnIdMap.get(colHeader);
                    cm.getColumnById(id).setHidden(!check);
                    this.onHiddenChange(id, !check);
                }
            }
        }
        this.refresh(true);
    }

    /*
     * Save the state on checking "Select all" same as individual columns.
     */
    //    protected void onHiddenChange(final int col, final boolean hidden) {
    protected void onHiddenChange(final String colId, final boolean hidden) {
        if (grid.isStateful()) {
            //            state.put("hidden" + cm.getColumnId(col), hidden);
            state.put("hidden" + colId, hidden);
            grid.saveState();
        }
    }

    /**
     * This gets called on each and every check/uncheck of individual columns.
     * If any one is unchecked then let the "select all" be checked - return false.
     * Or if all the columns are checked by user then the licence menu item should also be checked. - return true
     */
    private boolean shouldCheckSelectAll(final Menu columnMenu) {
        for (final Component item : columnMenu.getItems()) {

            if ((item instanceof SeparatorMenuItem)) {
                continue;
            }
            final CheckMenuItem columnMenuci = (CheckMenuItem) item;
            if (!columnMenuci.getText().equals(SELECT_ALL) && !columnMenuci.isChecked()) {
                return false;
            }
        }
        return true;
    }

    /*
     * When 1 column-menu item is checked and the same is present in another licence Menu
     * Then all should be checked or unchecked based on the grid event.
     */
    private void checkAllColumnsWithSameLicence(final CheckMenuItem itemClicked, final String[] multipleColumnLicences) {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < multipleColumnLicences.length; j++) {
                if (columnsWithMultiLicence != null && itemClicked.equals(columnsWithMultiLicence[i][j])) {
                    for (int k = 0; k < multipleColumnLicences.length; k++) {
                        columnsWithMultiLicence[i][k].setChecked(cm.isHidden(i));
                    }
                }
            }
        }
    }

    /*
     * Column "Event Time" should always be available on the grid and user should not be able to deselect it from the grid.
     */
    protected void restrictMenu(final CheckMenuItem check) {
        if (check.getText().equals("Event Time")) {
            check.setChecked(true);
            check.disable();
        }
    }

    /*
     * Methods extracted for JUNITS...............
     */

    int getGridColumnCount() {
        return grid.getColumnModel().getColumnCount();
    }

    protected CheckMenuItem[][] getCheckMultiMenuItems() {
        return new CheckMenuItem[cols][licenceType.length];
    }

    protected Menu getMenu() {
        return new Menu();
    }

    @Override
    public void setStateManager(final JSONGridStateManager stateManager) {
        this.stateManager = stateManager;
    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.grid.GridView#onColumnMove(int)
     */
    @Override
    protected void onColumnMove(final int newIndex) {
        super.onColumnMove(newIndex);
        stateManager.saveGridState();
    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.grid.GridView#onHiddenChange(com.extjs.gxt.ui.client.widget.grid.ColumnModel, int, boolean)
     */
    @SuppressWarnings("hiding")
    @Override
    protected void onHiddenChange(final ColumnModel cm, final int col, final boolean hidden) {
        super.onHiddenChange(cm, col, hidden);
        stateManager.saveGridState();
    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.grid.GridView#onColumnWidthChange(int, int)
     */
    @Override
    protected void onColumnWidthChange(final int column, final int width) {
        super.onColumnWidthChange(column, width);
        cm.getColumn(column).setWidth(width);
        stateManager.saveGridState();
    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.grid.GridView#onDataChanged(com.extjs.gxt.ui.client.store.StoreEvent)
     */
    @Override
    protected void onDataChanged(final StoreEvent<ModelData> se) {
        super.onDataChanged(se);
        stateManager.saveDataState();
    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.grid.GridView#updateHeaderSortState()
     */
    @Override
    protected void updateHeaderSortState() {
        super.updateHeaderSortState();
        stateManager.saveSortState();
    }

    public ColumnModel getColumnModel() {
        return cm;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.grid.IGridView#getSortInfo()
     */
    @Override
    public SortInfo getSortInfo() {
        return super.getSortState();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.grid.IGridView#getStore()
     */
    @Override
    public ListStore<ModelData> getStore() {
        return ds;
    }
}
