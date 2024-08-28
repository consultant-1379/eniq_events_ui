/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.Collection;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * ComboBox data element for search group combobox
 * which also holds information for a group.
 * 
 * Calls #set methods required by BaseModelData
 * 
 * Display DISPLAY_FIELD and VALUES are expected Meta data 
 * strings to be returned from services: e..g: 
 * 
 * <pre>
 * 
 * "data": [
 *  {
 *   "name" : "eric group a",
 *   "VIP"  : "TRUE",
 *   "values" : [
 *               {
 *                    "id" : "12121212" 
 *                },
 *                {
 *                    "id" : "2222222" 
 *                } 
 *            ] 
 *        },
 *        {
 *            "name" : "group b",
 *            "values" : [
 *                {
 *                    "id" : "44444" 
 *                },
 *                {
 *                   "id" : "5555" 
 *                } 
 *            ] 
 *        }
 *    ] 
 * }
 * </pre>
 * 
 * @author eeicmsy
 * @since May 2010
 *
 */
public class SearchGroupModelData extends BaseModelData implements Comparable<SearchGroupModelData> {

    /**
     * String in meta data for group name (and displayed by combobox)
     */
    public final static String DISPLAY_FIELD = "name";

    /**
     * String in meta data returned for Group info for the group name
     */
    public final static String VALUES = "values";

    /**
     * String in JSON returned from server for VIP group name
     */
    public final static String VIP = "VIP";

    /**
     * Field to used to set VIP icon using combobox template
     * @see com.ericsson.eniq.events.ui.client.search.GroupTypeSearchComponent
     */
    public final static String VIP_ICON = "VIP_ICON";

    /**
     * Cached group values for group name
     */
    private final Collection<String> groupValues;

    /**
     * Name of VIP icon -- BLANK.png to exist when not a VIP
     */
    public String VIPicon = BLANK_ICON_NAME;

    private final String groupName;

    private final boolean isVIP;

    /**
     * Data type for group combo box to display group name, 
     * with values and VIP stored for each group name so they are
     * available for a "info" press  
     * 
     * @param groupName     name of group for display in group combobox 
     * @param isVIP         true if group is a VIP group
     * @param groupValues   Associated values for group (e.g. a list of IMSIs) to be 
     *                      displayed when press for information on the group
     *                      
     * @see com.ericsson.eniq.events.ui.client.search.GroupTypeSearchComponent
     */
    public SearchGroupModelData(final String groupName, final boolean isVIP, final Collection<String> groupValues) {

        this.groupValues = groupValues;

        this.groupName = groupName;

        this.isVIP = isVIP;

        if (isVIP) {
            VIPicon = VIP_ICON_NAME;
        }
        /* to be available for template code in search component */
        set(DISPLAY_FIELD, groupName);
        set(VIP_ICON, VIPicon);

    }

    /**
     * Utility to know if group is a VIP
     * @return  true if user group has been identified as a VIP (VIP set in JSON returned from server)
     */
    public boolean isVIPGroup() {
        return isVIP;
    }

    /**
     * Utility to fetch values in group
     * @return values for group (e.g. a list of IMSIs)
     */
    public Collection<String> getGroupValues() {
        return groupValues;
    }

    /**
     * Utility to fetch name of group
     * @return   name of group
     */
    public String getGroupName() {
        return groupName;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final SearchGroupModelData other) {
        return groupName.compareToIgnoreCase(other.getGroupName());
    }

}