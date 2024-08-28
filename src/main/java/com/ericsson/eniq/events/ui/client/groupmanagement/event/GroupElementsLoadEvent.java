/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.event;

import java.util.List;
import java.util.Map;

import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class GroupElementsLoadEvent extends GwtEvent<GroupElementsLoadEventHandler> {

    public static final Type<GroupElementsLoadEventHandler> TYPE = new Type<GroupElementsLoadEventHandler>();

    private final Map<String, List<GroupListItem>> groupElementMap;

    private final boolean deletingGroup;

    private final String groupName;

    /**
     * @param groupName
     * @param groupElementMap
     * @param deletingGroup
     */
    public GroupElementsLoadEvent(final String groupName, final Map<String, List<GroupListItem>> groupElementMap,
            final boolean deletingGroup) {
        this.groupName = groupName;
        this.groupElementMap = groupElementMap;
        this.deletingGroup = deletingGroup;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<GroupElementsLoadEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(final GroupElementsLoadEventHandler handler) {
        handler.onGroupElementsLoaded(this);
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @return the groupElementMap
     */
    public Map<String, List<GroupListItem>> getGroupElementMap() {
        return groupElementMap;
    }

    /**
     * @return the deletingGroup
     */
    public boolean isDeletingGroup() {
        return deletingGroup;
    }
}
