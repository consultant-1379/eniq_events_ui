/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

/**
 * Interface for the shared business logic of the edit presenter where it needs to interact with 
 * the edit view
 * @author ecarsea
 * @since 2011
 *
 */
public interface IEditGroupView {

    /**
     * @param enabled
     */
    void setSaveButtonEnabled(boolean enabled);
    
    void setSaveAsButtonEnabled(boolean enabled);

    /**
     * Configure the view with its group name
     * @param groupName 
     * @param isNewGroup
     */
    void configure(String groupName, boolean isNewGroup);

    /**
     * @return
     */
    String getGroupName();

    /**
     * @return
     */
    boolean isGroupNameEntryBoxEmpty();

    /**
     * @param enabled
     */
    void setDeleteButtonEnabled(boolean enabled);

    /**
     * Clean up view
     */
    void close();

}
