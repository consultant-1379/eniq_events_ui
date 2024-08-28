/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.multiselect;

import java.util.List;

import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;

/**
 * @author ekurshi
 * @since 2012
 *
 */
public interface IMultiSelectHandler {

    void onClear();

    void mask();

    void unMask();

    /**
     * When an item on the first panel is selected, load the corresponding items into the middle panel.
     * @param selectedElement
     * @param items
     */
    void onMultiItemSelect(GroupListItem selectedElement, List<GroupListItem> items);

    void setGroupItemsAggregator(GroupItemsAggregator groupItemsAggregator);
}
