/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GridView;

/**
 * Extends GridView to handle view property changes that need to be saved
 * @author ecarsea
 * @since 2012
 *
 */
public class JSONGridView extends GridView implements IGridView {

    private JSONGridStateManager stateManager;

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

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.grid.IGridView#getSortInfo()
    */
    @Override
    public SortInfo getSortInfo() {
        return getSortState();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.grid.IGridView#getStore()
     */
    @Override
    public ListStore<ModelData> getStore() {
        return ds;
    }
}
