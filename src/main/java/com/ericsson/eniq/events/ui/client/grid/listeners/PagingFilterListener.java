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
package com.ericsson.eniq.events.ui.client.grid.listeners;

import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.grid.JSONGrid;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;

/**
 * 
 *  PagingFilterListener uses a PagingListLoader to get the offset and limit parameter saved with the Grid's
 *  state. Loads the data using the paging load configurations to set the offset of the first 
 * record and the limit of the number of rows displayed on each page
 * 
 */
public class PagingFilterListener implements Listener<GridEvent<ModelData>> {

    private final IMetaReader metaReader = MainEntryPoint.getInjector().getMetaReader();

    private final JSONGrid jsongrid;

    private static int rowsPerPage;

    private final PagingLoader<PagingLoadResult<ModelData>> loader;

    /**
     * 
     * @param jsongrid - JSONGrid used to access it's getState method
     * @param loader - PagingListLoader to get the offset and limit parameter's of the grid
     */
    public PagingFilterListener(final JSONGrid jsongrid, final PagingLoader<PagingLoadResult<ModelData>> loader) {
        this.jsongrid = jsongrid;
        this.loader = loader;

        rowsPerPage = this.jsongrid.getRowsPerPage();
    }

    @Override
    public void handleEvent(final GridEvent<ModelData> be) {
        final PagingLoadConfig config = new BasePagingLoadConfig();
        config.setOffset(0);
        config.setLimit(rowsPerPage);
        if (jsongrid.getStateManager().hasSavedState()) {
            config.setLimit(jsongrid.getStateManager().getLimit());
            config.setSortField(jsongrid.getStateManager().getSortField());
            config.setSortDir(SortDir.valueOf(jsongrid.getStateManager().getSortDir()));
        }
        loader.load(config);
    }

}