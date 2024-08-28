/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype.dashboard;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.datatype.ParametersDataType;
import com.ericsson.eniq.events.common.client.datatype.ThresholdDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldUser;

/**
 * Holder for  portal properties (in dashboard)
 *
 * @author eeicmsy
 * @since Sept 2011
 */
public class PortletDataType implements IPortletDataType {

    private final static int DEFAULT_PORTAL_HEIGHT = 160;

    /* meta data information for porlet */
    private final String tabOwnerId;

    private final String portletId;

    private final String portletName;

    private final String wsURL;

    private final SearchFieldUser isSearchFieldUser;

    /* map built up from comma separated string specifying relative 
    * time (mins) to set as dateFrom (relative to user selected dateTo).
    * Split to cater for node selection, e.g. ""*,1440,CELL,10080,BSC,2880" 
    * 
    * key: nodeType or wildcard 
    * val: minutes as string
    */
    private final Map<String, String> relativeDateFromMap = new HashMap<String, String>();

    /*
     * populated when searhc type must be ignore, e..g BSC,CELL
     */
    private final List<String> excludedSearchTypes;

    /*
     * e.g. line, bar
     */
    private final String displayType;

    /*
     * Portal height attribute: assuming will want even spaced widths fiting container
     * so not adding a "width attribute
     */
    private final int portletHeight;

    //TODO use or loose
    private PortletType type;

    /* run time position */
    private int columnIndex;

    /* run time position */
    private int rowIndex;

    private final ParametersDataType parameters;

    private final List<ThresholdDataType> thresholds = new ArrayList<ThresholdDataType>();

    public PortletDataType(final String tabOwnerId, final String portalId, final String portalName,
            final String height, final String wsURL, final SearchFieldUser isSearchFieldUser, final String displayType,
            final String commaSeperatedDateFrom, final ParametersDataType parameters, final PortletType type,
            final String commaSeperatedExcludedSearchTypes) {

        this.tabOwnerId = tabOwnerId;
        this.portletId = portalId;
        this.portletName = portalName;
        this.wsURL = wsURL;
        this.isSearchFieldUser = isSearchFieldUser;
        this.displayType = displayType;
        this.type = type;

        populateTimeFromMap(commaSeperatedDateFrom);

        excludedSearchTypes = commaSeperatedExcludedSearchTypes.isEmpty() ? null : new ArrayList<String>(
                Arrays.asList(commaSeperatedExcludedSearchTypes.split(COMMA)));

        this.parameters = parameters;

        // assume number in meta data
        this.portletHeight = height.isEmpty() ? DEFAULT_PORTAL_HEIGHT : Integer.parseInt(height);
    }

    /*
     * Depending on node selection DateFrom can be different, 
     * this will be set up via meta data in comma seperated string like:
     *   
     * @param commaSeperatedTimeFrom  e.g.   "*,1440,CELL,10080,BSC,2880"
     */
    private void populateTimeFromMap(final String commaSeperatedTimeFrom) {
        final String[] metaDataVal = commaSeperatedTimeFrom.split(COMMA);

        final int len = metaDataVal.length;
        if (len % 2 == 0 && len >= 2) {
            for (int i = 0; i < len; i = i + 2) {
                relativeDateFromMap.put(metaDataVal[i].trim(), metaDataVal[i + 1].trim());
            }
        }

    }

    ////////////////    run time postion info  ////////////////
    @Override
    public int getColumnIndex() {
        return columnIndex;
    }

    @Override
    public void setColumnIndex(final int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public int getRowIndex() {
        return rowIndex;
    }

    @Override
    public void setRowIndex(final int rowIndex) {
        this.rowIndex = rowIndex;
    }

    ////////////////    end run time postion info  ////////////////

    @Override
    public String getTabOwnerId() {
        return tabOwnerId;
    }

    @Override
    public String getPortletId() {
        return portletId;
    }

    @Override
    public String getPortletTitle() {
        return portletName;
    }

    @Override
    public int getPortletHeight() {
        return portletHeight;
    }

    @Override
    public String getURL() {
        return wsURL;
    }

    @Override
    public boolean isSearchFieldUser() {
        return (SearchFieldUser.FALSE != isSearchFieldUser);
    }

    public SearchFieldUser getSearchFieldUserInfo() {
        return isSearchFieldUser;
    }

    /**
     * String suiting ChartView Chart Configurations
     *
     * @return "line", "bar","horzbar", etc
     */
    @Override
    public String getDisplayType() {
        return displayType;
    }

    /**
     * Utility to check if portlet excluded from handling a particular node type selection
     * (indicating don't make server call for this porlet (and react in some way for 
     * widget to change to show no data to display )
     * 
     * @param nodeType  BSC, CELL, (id defined in searchData.json)
     * @return          true if not able to handle this node type selection (indicating
     *                  
     */
    public boolean isExcludedNodeType(final String nodeType) {
        return excludedSearchTypes != null && nodeType != null && excludedSearchTypes.contains(nodeType);
    }

    /**
     * Time back from timeTo data, set from meta data
     * for node type or default
     *
     * @param nodeType node type defined in meta data. e.g. BSC.
     * @return time (mins) to use for relative timeFrom (relative to timeTo)
     */
    public String getTimeFromValFromNodeType(final String nodeType) {

        String result = (nodeType == null) ? null : relativeDateFromMap.get(nodeType);

        if (result == null) {
            result = relativeDateFromMap.get(CommonConstants.WILDCARD);
        }
        return result; // can still be null if nothing set in meta data;
    }

    @Override
    public PortletType getType() {
        return type;
    }

    @Override
    public void setType(final PortletType type) {
        this.type = type;
    }

    @Override
    public ParametersDataType getParameters() {
        return this.parameters;
    }

    public List<ThresholdDataType> getThresholds() {
        return thresholds;
    }

    @Override
    public String toString() {
        return "PortletDataType{" + "portletName='" + portletName + '\'' + ", columnIndex=" + columnIndex
                + ", rowIndex=" + rowIndex + ", portletId='" + portletId + '\'' + ", type=" + type + ", tabOwnerId='"
                + tabOwnerId + '\'' + '}';
    }
}
