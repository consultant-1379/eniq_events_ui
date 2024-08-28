/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.listitem;

/**
 * Interface for elements displayed in a list within the Group Management component.
 * Can be a group names, group element items, objects such as PLMNGroupElement
 * @author ecarsea
 * @since 2012
 *
 */
public interface GroupListItem {

    /**
     * @return
     */
    String getStringValue();

    /**
     * @return
     */
    String[] getKeyValues();

}
