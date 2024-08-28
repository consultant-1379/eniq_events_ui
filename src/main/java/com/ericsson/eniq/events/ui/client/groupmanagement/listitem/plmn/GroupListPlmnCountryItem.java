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
public class GroupListPlmnCountryItem implements GroupListItem {
    private final String name;

    private final String mcc;

    /**
     * @param name
     * @param mcc
     */
    public GroupListPlmnCountryItem(String name, String mcc) {
        super();
        this.name = name;
        this.mcc = mcc;
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
    public String getMcc() {
        return mcc;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.groupelement.GroupListItem#getStringValue()
     */
    @Override
    public String getStringValue() {
        return name + "(" + mcc + ")";
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.groupelement.GroupListItem#getKeyValues()
     */
    @Override
    public String[] getKeyValues() {
        return new String[] { mcc };
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mcc == null) ? 0 : mcc.hashCode());
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
        GroupListPlmnCountryItem other = (GroupListPlmnCountryItem) obj;
        if (mcc == null) {
            if (other.mcc != null)
                return false;
        } else if (!mcc.equals(other.mcc))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
