/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.StoreSorter;

/**
 * CaselessGroupingStore is substituted for gwt's GroupingStore,
 * to make the sorting case-insensitive. Comparator is
 * defined in CaselessComparator.java
 * Written to fix TR HN16892
 * 
 * @author eriwals
 * @since Nov 2010
 *
 */
public class CaselessGroupingStore<M extends ModelData> extends GroupingStore<M> {

    /**
     * setup the override for the case-insensitive comparator
     */
    public CaselessGroupingStore() {
        super();
        setupStoreSorter();
    }

    /**
     * setup the override for the case-insensitive comparator
     * @param loader - the loader instance
     */
    public CaselessGroupingStore(final ListLoader<ListLoadResult<ModelData>> loader) {
        super(loader);
        setupStoreSorter();
    }

    /**
     * sets the case-insensitive comparator
     */
    private void setupStoreSorter() {
        storeSorter = new StoreSorter<M>(new CaselessComparator<Object>());

    }
}
