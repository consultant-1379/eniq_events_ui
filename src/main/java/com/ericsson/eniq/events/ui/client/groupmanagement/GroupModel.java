/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.json.IJSONArray;
import com.ericsson.eniq.events.common.client.json.IJSONObject;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.ui.client.datatype.SearchGroupModelData;
import com.google.gwt.json.client.JSONValue;

import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.ID;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.JSON_ROOTNODE;

/**
 * Cache Group Names and Elements to prevent repeated unnecessary calls to the server
 * @author ecarsea
 * @since 2011
 */
public class GroupModel {

    /** Group data keyed by node type and group name **/
    private final Map<GroupKey, SearchGroupModelData> groupMap = new HashMap<GroupKey, SearchGroupModelData>();

    /** List of groups keyed by node type **/
    private final Map<String, List<String>> groupList = new HashMap<String, List<String>>();

    @SuppressWarnings("PMD.CyclomaticComplexity")
    public static class GroupKey {
        private final String nodeType;

        private final String groupName;

        /**
         * @param nodeType
         * @param groupName
         */
        public GroupKey(final String nodeType, final String groupName) {
            this.nodeType = nodeType;
            this.groupName = groupName;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
            result = prime * result + ((nodeType == null) ? 0 : nodeType.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final GroupKey other = (GroupKey) obj;
            if (groupName == null) {
                if (other.groupName != null) {
                    return false;
                }
            } else if (!groupName.equals(other.groupName)) {
                return false;
            }
            if (nodeType == null) {
                if (other.nodeType != null) {
                    return false;
                }
            } else if (!nodeType.equals(other.nodeType)) {
                return false;
            }
            return true;
        }

        /**
         * @return the nodeType
         */
        protected String getNodeType() {
            return nodeType;
        }

        /**
         * @return the groupName
         */
        protected String getGroupName() {
            return groupName;
        }

    }

    /**
     * Add Groups to cache for a selected group type by parsing JSON returned from a server call, 
     * @param nodeType
     * @param groupData
     */
    public void addGroups(final String nodeType, final JSONValue groupData) {
        final JsonObjectWrapper metaData = new JsonObjectWrapper(groupData.isObject());

        final IJSONArray data = metaData.getArray(JSON_ROOTNODE);

        final List<String> groupNames = new ArrayList<String>();
        for (int i = 0; i < data.size(); i++) {
            final IJSONObject parent = data.get(i);
            final String name = parent.getString(SearchGroupModelData.DISPLAY_FIELD);
            final boolean isVIP = CommonConstants.TRUE.equals(parent.getString(SearchGroupModelData.VIP));
            final IJSONArray values = parent.getArray(SearchGroupModelData.VALUES);

            final Collection<String> groups = extractGroupContents(values);
            addEntryToGroupMap(nodeType, name, isVIP, groups);
            groupNames.add(name);
        }
        groupList.put(nodeType, groupNames);
    }

    protected void addEntryToGroupMap(final String nodeType, final String name, final boolean isVIP,
            final Collection<String> groups) {
        groupMap.put(new GroupKey(nodeType, name), (new SearchGroupModelData(name, isVIP, groups)));
    }

    protected Collection<String> extractGroupContents(final IJSONArray values) {
        final Collection<String> groups = new ArrayList<String>();

        for (int v = 0; v < values.size(); v++) {
            final IJSONObject vparent = values.get(v);
            groups.add(vparent.getString(ID));
        }
        return groups;
    }

    /**
     * Get the group List for this node Type
     * @param nodeType - APN,MSC,IMSI etc
     * @return
     */
    public boolean hasGroupNameList(final String nodeType) {
        return groupList.containsKey(nodeType);
    }

    /**
     * Get the list of group names for this node type
     * @param nodeType
     * @return
     */
    public Collection<String> getGroupNameList(final String nodeType) {
        return groupList.get(nodeType) == null ? Collections.<String> emptyList() : getSortedList(groupList
                .get(nodeType));
    }

    /**
     * Get the group elements for this group
     * @param groupType
     * @param groupName
     * @return
     */
    public Collection<String> getGroupContents(final String groupType, final String groupName) {
        final SearchGroupModelData data = groupMap.get(new GroupKey(groupType, groupName));
        return data == null ? Collections.<String> emptyList() : getSortedList(data.getGroupValues());
    }

    /**
     * @param groupValues
     * @return
     */
    private List<String> getSortedList(final Collection<String> groupValues) {
        final List<String> sortedList = new ArrayList<String>(groupValues);
        Collections.sort(sortedList, new Comparator<String>() {

            @Override
            public int compare(final String arg0, final String arg1) {
                if (arg0 == null) {
                    return -1;
                }
                return arg0.compareToIgnoreCase(arg1);
            }
        });
        return sortedList;
    }
}
