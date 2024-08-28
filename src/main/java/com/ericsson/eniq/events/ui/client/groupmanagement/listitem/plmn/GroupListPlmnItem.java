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
public class GroupListPlmnItem implements GroupListItem {
    private final GroupListPlmnCountryItem country;

    private final GroupListPlmnOperatorItem operator;

    /**
     * @param country
     * @param operator
     */
    public GroupListPlmnItem(GroupListPlmnCountryItem country, GroupListPlmnOperatorItem operator) {
        super();
        this.country = country;
        this.operator = operator;
    }

    /**
     * @return the country
     */
    public GroupListPlmnCountryItem getCountry() {
        return country;
    }

    /**
     * @return the operator
     */
    public GroupListPlmnOperatorItem getOperator() {
        return operator;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.groupelement.GroupListItem#getStringValue()
     */
    @Override
    public String getStringValue() {
        return country.getStringValue() + "," + operator.getStringValue();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.groupelement.GroupListItem#getKeyValues()
     */
    @Override
    public String[] getKeyValues() {
        return new String[] { country.getMcc(), operator.getMnc() };
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((operator == null) ? 0 : operator.hashCode());
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
        GroupListPlmnItem other = (GroupListPlmnItem) obj;
        if (country == null) {
            if (other.country != null)
                return false;
        } else if (!country.equals(other.country))
            return false;
        if (operator == null) {
            if (other.operator != null)
                return false;
        } else if (!operator.equals(other.operator))
            return false;
        return true;
    }

}
