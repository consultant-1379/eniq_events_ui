/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;

/**
 * Gather all parameters of interest than any toolbar button might be interested in, 
 * with a view to setting the enabled status of the buttton
 * 
 * @author eeicmsy
 * @since Nov 2010
 *
 */
public class ButtonEnableParametersDataType {

    /**
     * Row count in data returned
     */
    public int rowCount;

    /**
     * Is row currently selected (at time of asking)
     */
    public boolean isRowSelected;

    /**
     * true if button is currently enabled
     */
    public boolean isCurrentlyEnabled;

    /**
     * Search field data changed from time of previous checking
     */
    public boolean hasSearchFieldChanged;

    /**
     * Search data associated with window currently (not necessarily value in MenuTaskBar) 
     * For Ranking it actually does not matter if no search component, because windows 
     * launched from hyperlink (containing say KPI button) should be taking in search data different
     * from that presently in search field and thats what we want. 
     * 
     * We read search data from the window itself (read isGroupMode, search data type, etc
     * from search data). 
     */
    public SearchFieldDataType searchData;

    /**
     * Outbound server call information was used to populate this window
     * following a drilldown hyperlink click. This is like information stored in 
     * widgetSpecificParams (except widgetSpecificParams will be wiped on
     * success server calls - which is where would want this information 
     * for enabling toolbar buttons)
     * @see com.ericsson.eniq.events.ui.client.datatype.DrillDownParameterInfoDataType
     * 
     * e.g. can use to fetch data such as 
     * <li>&key=SUM&type=CELL&cell=CELL146889&vendor=ERICSSON&bsc=BSC735&RAT=1</li>
     * <li>&key=ERR&type=BSC&groupname=Another_Group_HIER3&eventID=1</li>
     * 
     * and use to check enabling conditions that should be applied to specific toolbar buttons
     * in new window following server call
     */
    public IHyperLinkDataType widgetSpecificInfo;

    /**
     * Column information (column header), e.g. as used to enable or disable "Recurring Error" button.
     * @see com.ericsson.eniq.events.ui.client.buttonenabling.RecurErrorButtonEnableConditions.java
     */
    public GridInfoDataType columnsMetaData;

}
