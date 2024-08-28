/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch;

import com.google.gwt.view.client.HasData;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface SelectedItemHandler<T> {

    /**
     * Get current count of selected items
     * @return
     */
    int getSelectedCount();

    /**
     * Clear other categories. The default action on the current category will take care of clearing items within the current category
     * @param hasData - the cell list of the current category
     */
    void clearOtherCategorySelections(HasData<T> hasData);

}
