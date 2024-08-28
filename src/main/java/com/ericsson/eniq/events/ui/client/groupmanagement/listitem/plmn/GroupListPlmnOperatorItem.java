/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.listitem.plmn;

import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class GroupListPlmnOperatorItem implements GroupListItem {
    private final String name;

    private final String mnc;

    /**
     * @param name
     * @param mnc
     */
    public GroupListPlmnOperatorItem(String name, String mnc) {
        super();
        this.name = name;
        this.mnc = mnc;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the mcc
     */
    public String getMnc() {
        return mnc;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.groupelement.GroupListItem#getStringValue()
     */
    @Override
    public String getStringValue() {
        return name + "(" + mnc + ")";
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.groupelement.GroupListItem#getKeyValues()
     */
    @Override
    public String[] getKeyValues() {
        return new String[] { mnc };
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mnc == null) ? 0 : mnc.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        GroupListPlmnOperatorItem other = (GroupListPlmnOperatorItem) obj;
        if (mnc == null) {
            if (other.mnc != null)
                return false;
        } else if (!mnc.equals(other.mnc))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
