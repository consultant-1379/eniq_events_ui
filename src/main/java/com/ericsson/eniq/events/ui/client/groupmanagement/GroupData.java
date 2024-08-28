/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

import java.util.Collection;

import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public class GroupData {
    private final String groupName;

    private final Collection<GroupListItem> groupElements;

    /**
     * @param groupName
     * @param groupElements
     */
    public GroupData(final String groupName, final Collection<GroupListItem> groupElements) {
        this.groupName = groupName;
        this.groupElements = groupElements;
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @return the groupElements
     */
    public Collection<GroupListItem> getGroupElements() {
        return groupElements;
    }
}
