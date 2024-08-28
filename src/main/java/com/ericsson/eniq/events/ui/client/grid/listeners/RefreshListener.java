/**
 * -----------------------------------------------------------------------
 /**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid.listeners;

import com.ericsson.eniq.events.ui.client.grid.JSONGrid;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.StoreEvent;

/**
 * 
 * Listener that is called when a change is made to the grid's store.
 * The handle event will then call a refresh on the grid view and 
 * tool bar if the flag is false.
 * 
 * @author esuslyn
 * @since March 2010
 */
public class RefreshListener implements Listener<StoreEvent<ModelData>> {

    private final JSONGrid jsonGrid;

    boolean isViewUpdated = false;

    /**
     * 
     * @param grid - JSONGrid is passed across to access it's refreshGrid method
     */
    public RefreshListener(final JSONGrid grid) {
        super();
        this.jsonGrid = grid;
    }

    @Override
    public void handleEvent(final StoreEvent<ModelData> be) {
        if (!isViewUpdated) {
            jsonGrid.refreshGrid();
            isViewUpdated = true;
        }
    }

    /**
     * @param isRefreshView is a flag to check if a gird's view is updated 
     * with changes to it's store. When changes are applied to the grid's
     * store the flag is set to false to say the grid view is out of syn 
     * with the grid store 
     */
    public void setRefreshView(final boolean isRefreshView) {
        this.isViewUpdated = isRefreshView;
    }

}