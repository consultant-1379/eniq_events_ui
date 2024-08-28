/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;

/**
 * Handle double clicking an element in a filter panel
 * @author ecarsea
 * @since 2011
 */
public interface IElementDoubleClickHandler {
    void onElementDoubleClicked(GroupListItem value);
}
