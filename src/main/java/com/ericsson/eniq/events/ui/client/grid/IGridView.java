/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.store.ListStore;

/**
 * Implemented by all the Grid Views to interact with the State Manager
 * @author ecarsea
 * @since 2012
 *
 */
public interface IGridView {

    SortInfo getSortInfo();

    ListStore<ModelData> getStore();

    void setStateManager(final JSONGridStateManager stateManager);
}
