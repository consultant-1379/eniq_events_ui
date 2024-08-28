/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.multiselect;

import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface GroupItemsAggregator {
    GroupListItem getAggregateGroupListItem(GroupListItem firstItem, GroupListItem secondItem);
}
