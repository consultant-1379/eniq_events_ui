/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype.group;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Group Management Component JsonObjectWrapper
 * @author ecarsea
 * @since 2011
 *
 */
public class GroupMgmtConfigDataType {
    private final Map<String, GroupManagementItemDataType> groupManagementItemMap = new HashMap<String, GroupManagementItemDataType>();

    private String groupManagementConfigurationUrl;

    private String groupElementLoadUrl;

    public void addGroupManagementItem(final GroupManagementItemDataType item) {
        groupManagementItemMap.put(item.getId(), item);
    }

    public GroupManagementItemDataType getGroupManagementItem(final String itemId) {
        return groupManagementItemMap.get(itemId);
    }

    public Collection<GroupManagementItemDataType> getGroupManagementItems() {
        return groupManagementItemMap.values();
    }

    public void setGroupConfigurationUrl(final String url) {
        this.groupManagementConfigurationUrl = url;
    }

    public String getGroupConfigurationUrl() {
        return groupManagementConfigurationUrl;
    }

    public void setGroupElementLoadUrl(final String groupElementLoadUrl) {
        this.groupElementLoadUrl = groupElementLoadUrl;
    }

    public String getGroupElementLoadUrl() {
        return groupElementLoadUrl;
    }
}
