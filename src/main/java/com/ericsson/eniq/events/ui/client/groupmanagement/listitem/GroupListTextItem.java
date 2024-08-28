/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.listitem;

import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.*;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class GroupListTextItem implements GroupListItem {

    private final String textItem;

    /**
     * @param textItem
     */
    public GroupListTextItem(String textItem) {
        this.textItem = textItem;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.groupelement.GroupListItem#getStringValue()
     */
    @Override
    public String getStringValue() {
        return textItem;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.groupelement.GroupListItem#getKeyValues()
     */
    @Override
    public String[] getKeyValues() {
        return textItem.split(GROUP_KEYS_DELIMITER);
    }

    /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((textItem == null) ? 0 : textItem.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GroupListTextItem other = (GroupListTextItem) obj;
        if (textItem == null) {
            if (other.textItem != null)
                return false;
        } else if (!textItem.equals(other.textItem))
            return false;
        return true;
    }

}
