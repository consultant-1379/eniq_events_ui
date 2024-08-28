/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreSorter;

/**
 * CaselessListStore is substituted for gwt's ListStore,
 * to make the sorting case-insensitive. Comparator is
 * defined in CaselessComparator.java
 * Written to fix TR HN14512
 * 
 * @author eriwals
 * @since Nov 2010
 *
 */
public class CaselessListStore<M extends ModelData> extends ListStore<M> {

    /**
     * setup the override for the case-insensitive comparator
     */
    public CaselessListStore() {
        super();
        setupStoreSorter();
    }

    /**
     * setup the override for the case-insensitive comparator
     * @param loader - the loader instance
     */
    public CaselessListStore(final ListLoader<PagingLoadResult<ModelData>> loader) {
        super(loader);
        setupStoreSorter();
    }

    /**
     * overrides Liststore.applySort in order to call ListStore.sortData
     * @param supressEvent - boolean controlling if a Sort Event is fired in ListStore.applySort
     */
    @Override
    protected void applySort(final boolean supressEvent) {
        super.applySort(supressEvent);
        sortData(super.getSortField(), super.getSortDir());
    }

    /**
     * sets the case-insensitive comparator
     */
    private void setupStoreSorter() {
        storeSorter = new StoreSorter<M>(new CaselessComparator<Object>());
    }
}
