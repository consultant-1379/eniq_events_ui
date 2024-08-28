/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.event;

import com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.GroupOperation;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public class GroupOperationCompletedEvent extends GwtEvent<GroupOperationCompletedEventHandler> {
    public static final Type<GroupOperationCompletedEventHandler> TYPE = new Type<GroupOperationCompletedEventHandler>();

    private final boolean operationSuccess;

    private final GroupOperation groupOperation;

    /**
     * @param operationSuccess
     * @param currentAction 
     */
    public GroupOperationCompletedEvent(final boolean operationSuccess, final GroupOperation groupOperation) {
        this.operationSuccess = operationSuccess;
        this.groupOperation = groupOperation;
    }

    /**
     * @return the success
     */
    public boolean isOperationSuccess() {
        return operationSuccess;
    }

    /**
     * @return the groupOperation
     */
    public GroupOperation getGroupOperation() {
        return groupOperation;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<GroupOperationCompletedEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(final GroupOperationCompletedEventHandler handler) {
        handler.onGroupOperationCompleted(this);
    }
}
