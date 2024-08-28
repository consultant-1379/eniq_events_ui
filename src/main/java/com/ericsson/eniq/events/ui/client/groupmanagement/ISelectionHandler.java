/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

import java.util.List;

import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;

/**
 * Interface for handling of selected items from a Filter Panel
 * @author ecarsea
 * @since 2011
 *
 */
public interface ISelectionHandler {

    /**
     * @param selectedItems
     */
    void onItemsSelected(List<GroupListItem> selectedItems);

}
