/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.ericsson.eniq.events.common.client.CommonConstants.ALL_TIME_PARAMS;
import static com.ericsson.eniq.events.common.client.CommonConstants.FIRST_URL_PARAM_DELIMITOR;
import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;
import static com.ericsson.eniq.events.ui.client.common.Constants.EQUAL_STRING;
import static com.ericsson.eniq.events.ui.client.common.Constants.TIME_FROM_URL_PARAM;
import static com.ericsson.eniq.events.ui.client.common.Constants.TIME_TO_URL_PARAM;
import static com.ericsson.eniq.events.ui.client.common.Constants.TIME_URL_PARAM;

/**
 * 
 * Contains one map per instantiation, 
 * to ensure no duplication of wigit specific parameters
 * 
 * @author eeicmsy
 * @since Aug 2010
 *
 */
public class URLParamUtils {

    /* ensure no duplicate params added with map */
    private final Map<String, String> paramsMap = new HashMap<String, String>();

    // TODO this class was a bad idea

    /*
     * Static map to gather (learn) together all search field meta data ever used 
     * (e.g. "imsi=", "bsc=", etc), with a view to swapping out search field data from 
     * widgetSpecificUrlParams  entirely
     */
    private final static Map<String, String> searchFieldParamsMap = new HashMap<String, String>();

    private final static String FIRST_URL_PARAM_DELIMITOR_STRING = EMPTY_STRING + FIRST_URL_PARAM_DELIMITOR;

    /**
     * Add or over-write URL parameters to existing URL (after current initial params, excluding ? param)
    
     * addOutBoundRegularParameter("display=", "grid");
     * addOutBoundRegularParameter("groupname=", chartElementClicked);
     * 
     * NOTE ASSUMES KEY HAS "="
     *   
     * @param keyParam  key contain =, e.g. "display="
     * @param value     value for key
     */
    public void addOutBoundRegularParameter(final String keyParam, final String value) {
        addOutBoundParameter(keyParam, value, CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR, paramsMap);
    }

    /**
     * Keep all paramters starting from & delimiter (ignore ? param)
     * 
     * @param params  stringified parameters, e.g. &imsi=12345&display=chart  
     */
    public void keepExistingRegularParams(final String params) {
        keepExistingParams(params, CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR, paramsMap, true);

    }

    /**
     * Expect when called values are the same in both (if not will take new param)
     * This method is to ensure no duplication of keys
     * 
     * NOTE : value are taken from newParams if conflict
     * 
     * @param oldParams  e.g.  ?time=30&imsi=1243&display=grid
     * @param newParams  e.g.  &drilldown=EV1&imsi=1243&display=grid
     * @return merge of two, e.g. ?time=30&imsi=1243&display=grid&drilldown=EV1
     */
    public void replaceParams(final String oldParams, final String newParams, final boolean isDrill) {
        removeAllTimeInfoFromParameterMap();

        if (isDrill){
            //if it's a drill, then keep whatever params that have timeFrom param...
            if (oldParams.contains(TIME_FROM_URL_PARAM) && oldParams.contains(TIME_TO_URL_PARAM)){
                keepTimeParams(oldParams, FIRST_URL_PARAM_DELIMITOR, paramsMap);
            }else{
                keepTimeParams(newParams, FIRST_URL_PARAM_DELIMITOR, paramsMap);
            }
        }else{
            if (newParams.contains(TIME_URL_PARAM)){
                keepTimeParams(newParams, FIRST_URL_PARAM_DELIMITOR, paramsMap);
            }else{
                keepTimeParams(oldParams, FIRST_URL_PARAM_DELIMITOR, paramsMap);
            }
        }

        keepExistingRegularParams(oldParams);
        keepExistingRegularParams(newParams);
    }

    public void replaceTimeParams(final TimeInfoDataType timeData) {
        if (timeData != null) {
            final String timeDataStr = timeData.getQueryString(true);
            replaceParams("", timeDataStr, false);
        }

    }

    /**
     * Replace the time drill params with: ?dateFrom=09052014&timeTo=1445&timeFrom=1430&dateTo=09052014
     * @param windowTimeDate
     */
    public void replaceTimeDrillParams(TimeInfoDataType windowTimeDate) {
        if (windowTimeDate != null) {

            final String timeDataStr = windowTimeDate.getDrillQueryString();
            replaceParams("", timeDataStr, true);

        }
    }


    /*
     * Contains time paramter check
     * @param newParams  e.g ?time=
     * @return  true if any of the time parameters present
     */
    private boolean containsTimeParams(final String newParams) {
        for (final String timeParam : ALL_TIME_PARAMS) {
            if (newParams.indexOf(timeParam) != -1) {
                return true;
            }
        }
        return false;
    }

    private void removeAllTimeInfoFromParameterMap() {

        final Set<String> paramsMapKeySet = getParametersMap().keySet();
        final Set<String> keysToRemove = new HashSet<String>();

        for (final String paramKey : paramsMapKeySet) {
            for (final String timeParam : ALL_TIME_PARAMS) { // not so efficient
                if (paramKey.indexOf(timeParam) != -1) {
                    keysToRemove.add(paramKey);
                    break;
                }
            }
        }
        for (final String key : keysToRemove) {
            getParametersMap().remove(key);
        }

    }

    private void replaceSearchFieldParams(final String oldParams, final String newParams) {
        keepExistingParams(oldParams, FIRST_URL_PARAM_DELIMITOR, paramsMap, false);
        keepExistingParams(newParams, FIRST_URL_PARAM_DELIMITOR, paramsMap, true);
        keepExistingParams(oldParams, CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR, paramsMap, false);
        keepExistingRegularParams(newParams);
    }

    /**
     * Specifically for use for searchfield update (only).
     * e.g. to avoid existing methods concating search field data like
     * 
     * ?time=10080&type=BSC&imsi=460010000000005&display=grid&key=SUM&tzOffset=+0100&maxRows=500&bsc=BSC122&vendor=ERICSSON&RAT=0
     * 
     * 
     * @param widgetSpecificUrlParams  - e.g. original prior to search field change, e.g. 
     *                                     ?time=10080&key=TOTAL&tzOffset=+0100&imsi=460040000699261&display=grid&maxRows=5000&type=IMSI
     * @param data                     - search field data to replace the current search field data 
     *                                   (which may have a completely different signature to existing search data)
     */
    public void replaceSearchDataParams(final String widgetSpecificUrlParams, final SearchFieldDataType data) {

        removeExistingSearchParameters();
        upDateSearchMetaData(data);

        final String newParams = data.getSearchFieldURLParams(false); // assume ? limitor is time
        replaceSearchFieldParams(widgetSpecificUrlParams, newParams);

    }

    /**
     * Method included to build up static searchFieldParamsMap 
     * to have a Set of all search field types (with a view to being able to 
     * isolate and remove search data from a widget params string
     * 
     * @param searchMetaData  current search data (containing meta data such as "imsi=" or "type=" etc
     */
    public void upDateSearchMetaData(final SearchFieldDataType searchMetaData) {

        if (searchMetaData != null) {
            final String searchParams = searchMetaData.getSearchFieldURLParams(false);
            keepExistingParams(searchParams, CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR, searchFieldParamsMap, true);
        }

    }

    private void removeExistingSearchParameters() {
        for (final String searchKey : searchFieldParamsMap.keySet()) {
            paramsMap.remove(searchKey);
        }
    }

    /**
     * Get extra stored widget parameters (don't loose ? paramter if its 
     * there - see BaseWindowPresenter will keep control on time parameter
     * @return parameters 'starting paramter if there (with ?) and 
     *         remaining parameter separated by &
     */
    public String getWidgetSpecificParams() {

        final StringBuilder buff = new StringBuilder();
        if (!paramsMap.isEmpty()) {
            String firstKey = null;
            for (final Map.Entry<String, String> entry : paramsMap.entrySet()) {
                final String key = entry.getKey();
                if (!key.startsWith(FIRST_URL_PARAM_DELIMITOR_STRING)) {
                    buff.append(entry.getKey());
                    buff.append(entry.getValue());
                } else {
                    firstKey = key;
                }
            }
            /* if ? param exists - put in to the top always */
            if (firstKey != null) {
                final StringBuilder buffWithStartKey = new StringBuilder();
                buffWithStartKey.append(firstKey);
                buffWithStartKey.append(paramsMap.get(firstKey));
                buffWithStartKey.append(buff.toString());

                return buffWithStartKey.toString();
            }
        }

        return buff.toString();

    }

    /**
     * URL parameter and key return utility (for test mostly)
     * @return  parameters being cached
     */
    public Map<String, String> getParametersMap() {
        return paramsMap;
    }

    private void addOutBoundParameter(final String keyParam, final String value, final char delimitor,
            final Map<String, String> map) {

        /* don't want ?display= and &display= */
        if (map.containsKey(FIRST_URL_PARAM_DELIMITOR + keyParam)) {
            map.remove(FIRST_URL_PARAM_DELIMITOR + keyParam);
        }
        if (map.containsKey(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR + keyParam)) {
            map.remove(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR + keyParam);
        }

        map.put(delimitor + keyParam, value);
    }

    private void keepTimeParams(final String params, char delimiter, final Map<String, String> map) {

        if (params != null && params.length() > 0) {

            int begin = params.indexOf(delimiter) + 1; // + char length
            while (begin != 0) {
                final int end = params.indexOf(EQUAL_STRING, begin);
                final String key = params.substring(begin, end + 1); // keep the = in key

                final int valBegin = end + 1; // '=' lenght
                // second parameter now (always has to be "&" - not the "?"
                begin = params.indexOf(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR, begin) + 1; // + char length
                // always a value if key
                final int endVal = (begin == 0) ? params.length() : params.indexOf(
                        CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR, valBegin);

                final String value = params.substring(valBegin, endVal);

                final boolean isTimeData = containsTimeParams(key); // this method no good for time data as all different
                if (isTimeData) {
                    addOutBoundParameter(key, value, delimiter, map);
                }
                delimiter = CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR; // no repeat of "?" passing to method above
            }
        }

    }

    private void keepExistingParams(final String params, char delimiter, final Map<String, String> map,
            final boolean includeSearchData) {

        if (params != null && params.length() > 0) {

            int paramBegin = params.indexOf(delimiter) + 1; // + char length

            while (paramBegin != 0 && paramBegin!=params.length()) {
                final int paramEnd = params.indexOf(EQUAL_STRING, paramBegin);
                final String key = params.substring(paramBegin, paramEnd + 1); // keep the = in key

                final int valBegin = paramEnd + 1; // '=' lenght
                // second parameter now (always has to be "&" - not the "?"
                paramBegin = params.indexOf(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR, paramBegin) + 1; // + char length
                // always a value if key
                final int valEnd = (paramBegin == 0) ? params.length() : params.indexOf(
                        CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR, valBegin);

                final String value = params.substring(valBegin, valEnd);

                final boolean isTimeData = containsTimeParams(key); // this method no good for time data as all different
                if (isTimeData) {
                    delimiter = CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR;//fixing failure recurrence info issue http://jira.eei.ericsson.se:8081/browse/EEUNM-1248
                    continue;
                }

                if (includeSearchData) {
                    addOutBoundParameter(key, value, delimiter, map);
                } else {
                    // have previous meta data for search data
                    // hack for "key=SUM" getting into search data
                    final boolean isSearchData = (!"key=".equals(key))
                            && (searchFieldParamsMap.containsKey(delimiter + key));

                    if (!isSearchData) {
                        addOutBoundParameter(key, value, delimiter, map);
                    }

                }

                delimiter = CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR; // no repeat of "?" passing to method above
            }
        }

    }

}
