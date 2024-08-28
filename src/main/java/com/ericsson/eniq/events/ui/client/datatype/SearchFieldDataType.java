/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.common.client.url.UrlUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.ericsson.eniq.events.common.client.CommonConstants.FIRST_URL_PARAM_DELIMITOR;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.SUMMARY;

/**
 * DataType for search field type information.
 * The user will enter a search field information and we will
 * convert that into parameters for a URL call, e.g.
 * http://localhost:8888/NETWORK_EVENT_ANALYSIS&type=CELL&node=myCellName
 * Might add to HashSets for caching.
 * 
 * Class would be final except for junit
 * 
 * @author eeicmsy
 * @since Feb 2010
 */
public class SearchFieldDataType { // NOPMD by eeicmsy on 03/12/10 11:27

    public String[] urlParams;

    public final String searchFieldVal;

    /* type which may need to return different grids for the same url 
     * (e.g. different grids for APN, CELL, SGSN, etc)
     */
    private final String type;

    private final String titlePrefix;
    
    private String titlePostfix = "";

    private final boolean isGroupMode;

    // TODO move? 
    private final static String SEARCH_PATH_DRIVEN_NODE_PARAM = "searchParam=";

    private boolean isPathMode;

    /*
     * Populated (sometimes)from metadata, e.g. "PS, CS",
     * to indicate this search field type selection is valid 
     * across CS and PS (or whatever)
     * 
     */
    private List<String> metaDataKeys = null;

    /*
     * We need sometimes group values(e.g. for the map portlet)
     * Comma seperated content of group  : Can be null
     */
    private final Collection<String> groupValues;

    private boolean isGroupAsCsv;

    public SearchFieldDataType(final String searchFieldVal, final String[] urlParams, final String type,
                               final String titlePrefix, final String titlePostfix, final boolean isGroupMode, final String splitStringMetaDataKeys,
                               final Collection<String> groupValues, boolean isPathMode){
        if (isSummaryType(type)) {
            /* urlParams for completeness but wont be used when PATH mode */
            this.searchFieldVal = SUMMARY; // value does not matter - just important for  #isEmpty
            this.urlParams = new String[] { SEARCH_PATH_DRIVEN_NODE_PARAM + SUMMARY };

        } else {

            this.searchFieldVal = searchFieldVal;
            this.urlParams = urlParams;
        }
        this.type = type;
        this.titlePrefix = titlePrefix;
        this.titlePostfix = titlePostfix;
        this.isGroupMode = isGroupMode;
        this.groupValues = groupValues;
        this.isPathMode = isPathMode;

        setMetaDataKeys(splitStringMetaDataKeys);
    }
    /**
     * Data type for search field information
     *
     * @param searchFieldVal          Actual value selected in the search field, e.g for the
     *                                title bar etc
     * @param urlParams               Strings with URL parameter format, for example
     *                                "node=myNode", "type=SGSN", "imsi=12121212121"
     * @param type                    (when required) The unique type (e.g. SGSN, APN, CELL) -
     *                                ?ONLY applicable when the search component is a "paired" search type
     * @param titlePrefix             name of type; see {@link #getTitlePrefix()}
     * @param isGroupMode             true when search field information is for a group (e.g. IMSI Group) as opposed
     *                                to single node
     *                                (best to read off current search data for window instead of whatever mnuTaskBar
     *                                may be showing - pre play press)
     * @param splitStringMetaDataKeys - e.g. "CS,PS" - JsonObjectWrapper string to say what meta data from meta data change
     *                                component this
     *                                search field type is valid for (e.g. controller is valid for CS and PS)
     *                                If empty search field will be applicable for search field where window was
     *                                created (e.g. PS only or CS only)
     * @param groupValues             collection of group values
     * @param isPathMode              is path mode or not
     */
    public SearchFieldDataType(final String searchFieldVal, final String[] urlParams, final String type,
            final String titlePrefix, final boolean isGroupMode, final String splitStringMetaDataKeys,
            final Collection<String> groupValues, boolean isPathMode) {
        if (isSummaryType(type)) {
            /* urlParams for completeness but wont be used when PATH mode */
            this.searchFieldVal = SUMMARY; // value does not matter - just important for  #isEmpty
            this.urlParams = new String[] { SEARCH_PATH_DRIVEN_NODE_PARAM + SUMMARY };

        } else {

            this.searchFieldVal = searchFieldVal;
            this.urlParams = urlParams;
        }
        this.type = type;
        this.titlePrefix = titlePrefix;
        this.isGroupMode = isGroupMode;
        this.groupValues = groupValues;
        this.isPathMode = isPathMode;

        setMetaDataKeys(splitStringMetaDataKeys);
    }

    /**
     * Factory method to make a defensive copy of an instance of search data
     * (reseting any run time changes made, i.e. like PATH - which can be one thing for summary and another for 
     * drilldown) 
     * @param copy
     * @return
     */
    public static SearchFieldDataType newInstance(final SearchFieldDataType copy) {
        return new SearchFieldDataType(copy.searchFieldVal, copy.urlParams, copy.type, copy.titlePrefix, copy.isGroupMode,
                joinStrings(copy.getMetaDataKeys()), copy.groupValues, copy.isPathMode);
    }

    /**
     * Create a comma separated list of strings from a collection
     * @param strings
     * @return
     */
    private static String joinStrings(final Collection<String> strings) {
        String delim = "";
        final StringBuilder sb = new StringBuilder();
        if (strings != null) {
            for (final String s : strings) {
                sb.append(delim).append(s);
                delim = ",";
            }
        }
        return sb.toString();
    }

    /**
     * Utility to test if the search field value - user inputed value part - 
     * is empty 
     * @return  true if no search field value part exists
     */
    public boolean isEmpty() {
        return isEmpty(searchFieldVal);
    }

    /* java trim() not cacthing all and can't use any other jars (apphe has better methods)
     * (Seeing empty string length 1 causing issues in drilldown (title bar), 
     * even though the string looks empty*/
    private static boolean isEmpty(String someString) {

        if (someString == null) {
            return true;
        }
        someString = someString.trim();
        if (someString.isEmpty()) {
            return true;
        }

        // trim not doing it for me some strange non white space char present- this is some kind of work around        
        for (int i = 0; i < someString.length(); i++) {
            final boolean isValid = Character.isLetterOrDigit(someString.charAt(i)); // GXT restricted Character methods

            if (isValid) { // at least something valid (not empty)  
                return false;
            }
        }
        return true;

    }

    /**
     * Get type which may need to return different grids for the same url 
     * (e.g. different grids for APN, CELL, SGSN, etc.)
     * @return   type (CELL, SGSN, ETC). Return null if not set in constructor. 
     *           Only applicable for paired search field component.
     */
    public String getType() {
        // includes "INPUT" (and "TAC" when terminal tab)
        return type;
    }

    /**
     * @return type name (e.g. Access Area, APN, SGSN-MME, Tracking Area, APN Group, Controller group, Access Area
     *         Group, SGSN-MME Group, Tracking Area Group, etc.); can be <tt>null</tt>
     */
    public String getTitlePrefix() {
        return titlePrefix;
    }

    public String getSearchFieldVal() {
        return searchFieldVal;
    }

    /**
     * Utility to know if window search field information is for a group
     * Set when SearchFieldDataType is being build up from
     * GroupComponennt or have established we are in group mode (IMSI group etc)
     * 
     * @return true is group mode(e.g. IMSI Group), false if single mode (e.g. IMSI)
     */
    public boolean isGroupMode() {
        return isGroupMode;
    }

    /**
     * Temporary (hopefully) method to allow group data to be sent in the searchParam as a string of csv, to facilitate services
     * that cannot handle the Group Name
     * @param groupAsCsv
     */
    public void setIsGroupAsCsv(final boolean groupAsCsv) {
        this.isGroupAsCsv = groupAsCsv;
    }

    /**
     * @return the groupValues
     */
    public Collection<String> getGroupValues() {
        return groupValues;
    }

    /**
     * When search field type is supported across several meta datas this list is
     * populated (e.g. if updating a controller you update both PS and CS windows).
     * 
     * See defined in meta data
     * 
     * "winMetaSupport" : "CS,PS" 
     * 
     * against the search field type, to indicate both CS and PS windows react to this 
     * search type change
     * 
     * @see com.ericsson.eniq.events.ui.client.common.comp.MenuTaskBar
     * @see com.ericsson.eniq.events.ui.client.common.widget.MetaDataChangeComponent
     * @see com.ericsson.eniq.events.ui.client.main.GenericTabView
     * 
     * 
     * @return  collection of meta data keys (e.g. "CS", "PS"). 
     *          Same key as would get from metaDataChangeComponent.getText();
     *          or null 
     */
    public List<String> getMetaDataKeys() {
        return metaDataKeys;
    }

    /*
     * 
     * See defined in meta data
     * 
     * "winMetaSupport" : "CS,PS" 
     * 
     * @param splitString  defined when want selection of this search field tpye
     *                     to update CS and PS open windows (not jjust PS or just CS)
     *                     
     * @see  #getMetaDataKeys()                    
     */
    final void setMetaDataKeys(final String splitString) {
        if (splitString != null && !splitString.isEmpty()) {

            metaDataKeys = new ArrayList<String>();
            for (final String index : splitString.split(COMMA)) { // CS, PS
                if (!index.isEmpty()) {
                    metaDataKeys.add(index.trim());
                }
            }
        }
    }

    /**
     * Quick Hack to counter hack the EventGridPresenter hack for 
     * EventGridPresenter.buildSearchDataFromHperLinkDetails.
     * 
     * For some launch window types in meta data see 
     * "queryParamName": "key" being added to drilldownWindows
     * but (when we call 
     * this method we know) that we don't want this in URL parameters 
     *  
     * i.e. Call in some cases we know we never want "key=Sum" or "key=Anything"
     * inside our SearchData 
     */
    public void clean() {
        final List<String> reducedParams = new ArrayList<String>();
        boolean isFound = false;
        if (urlParams != null) {
            for (final String param : urlParams) { // nothing if searchData.urlParams.length == 0

                if (param.startsWith(KEY_PARAM)) {
                    isFound = true;
                    continue;
                }
                reducedParams.add(param);

            }

            if (isFound) {
                urlParams = reducedParams.toArray(new String[reducedParams.size()]);
            }
        }

    }

    /**
     * Set path mode, meaning 
     * adjust for Path driven search user. 
     * This will mean will not add "type=bla" into the URL parameter when gathering search data
     * URL parameters
     * 
     * e.g. when true use /DASHBOARD/HOMER_ROAMER/APN  or /DASHBOARD/HOMER_ROAMER/SUMMARY 
     * not /DASHBOARD/HOMER_ROAMER?time=bla@type=APN
     * 
     * @param isPathMode  true to turn on path mode
     */
    public void setPathMode(final boolean isPathMode) {
        this.isPathMode = isPathMode;
    }

    public boolean isPathMode() {
        return isPathMode;
    }

    /**
     * Gather URL search field parameters (group or single as applicable)
     * Also used by drilldown call to pick up groupname when applicable
     * 
     * @param isFirstParameter  - true if search if first parameter in URL outbuond (when no time)
     * @return empty string or URL parameters
     */
    public String getSearchFieldURLParams(final boolean isFirstParameter) {

        if (isEmpty() || isSummaryType(type)) { // no paramters wanted for summary
            return EMPTY_STRING;
        }
        char cInitialDelimiter = (isFirstParameter) ? FIRST_URL_PARAM_DELIMITOR
                : CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR;

        final StringBuffer buff = new StringBuffer();

        if (isPathMode) {
            buff.append(cInitialDelimiter);
            buff.append(SEARCH_PATH_DRIVEN_NODE_PARAM);
            /** Send Comma Separated String of Group Names if GroupAsCsv set and search field is in group mode **/
            buff.append(UrlUtils.checkForAndRemoveAmpersand(isGroupAsCsv && isGroupMode ? joinStrings(groupValues) : searchFieldVal));

        } else {

            for (final String param : urlParams) { // nothing if searchData.urlParams.length == 0
                if (param.equals ("")){    //Prevents adding & to a blank string
                }

                else{
                buff.append(cInitialDelimiter);

                    if (!param.contains(DRILL_CAT)) {  //values from intercepting config window have multiple params included already, and no invalid "&", so do not remove
                        buff.append(UrlUtils.checkForAndRemoveAmpersand(param));  //remove any textual "&" from appended param
                    } else {
                        buff.append(param);
                    }

                cInitialDelimiter = CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR;
                }
            }
        }

        return buff.toString();
    }

    /**
     * Future proofing for different kinds of "Network", i.e assume all 
     * ids will begin at least with SUMMARY, e.g. SUMMARY_WRAN, SUMMARY_CORE"
     * 
     * Meaning the expected URL endsWith for server call has to have same
     * DASHBOARD/ROAMERS/SUMMARY_CORE?date=etc
     * 
     * @param type  The unique type (e.g. SGSN, APN, CELL) (for paired search only)
     */
    public static boolean isSummaryType(final String type) {
        return type != null && type.startsWith(SUMMARY);
    }

    @Override
    public String toString() {
        return searchFieldVal; // not url
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isGroupMode ? 1231 : 1237);
        result = prime * result + ((searchFieldVal == null) ? 0 : searchFieldVal.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + Arrays.hashCode(urlParams);
        return result;
    }

    @Override
    public boolean equals(final Object obj) { // NOPMD autgenerated
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SearchFieldDataType other = (SearchFieldDataType) obj;
        if (isGroupMode != other.isGroupMode) {
            return false;
        }
        if (searchFieldVal == null) {
            if (other.searchFieldVal != null) {
                return false;
            }
        } else if (!searchFieldVal.equals(other.searchFieldVal)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (!Arrays.equals(urlParams, other.urlParams)) {
            return false;
        }
        return true;
    }

    public String getTitlePostfix() {
        return this.titlePostfix;
    }

    public void setTitlePostfix(String postFix) {
        this.titlePostfix = postFix;
    }
}
