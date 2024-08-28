/********************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 ******************************************************************************* */
package com.ericsson.eniq.events.ui.client.common;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;

/**
 * Holds general constants used throughout the application
 * <p/>
 * PLEASE NOTE - any constants that are directly the same as Strings defined in
 * Meta data, e.g. button ids etc, should NOT be added to this class
 * (i.e. that is what the MetaReaderConstants file is there for).
 *
 * @since Mar 2010
 */
public final class Constants {

    private Constants() {
    }

    /** Base Zindex for popups, drop downs etc in EniqEvents. Needs to be huge as GXT window ZIndexes can increase exponentially when
     * tiling or cascading.
     */
    public static final int ENIQ_EVENTS_BASE_ZINDEX = 5000; //1000000 was the old setting.

    /** Base Zindex for error popups, drop downs etc in EniqEvents. Needs to be huge as GXT window ZIndexes can increase exponentially when
     * tiling or cascading.
     */
    public static final int ENIQ_EVENTS_BASE_ZINDEX_ERROR = 5000; //10000000 was the old setting.

    public final static String Y_AXIS_MIN_VAL = "yaxis_min";

    /*
    * live load search paging
    */
    public static final int LIVE_LOAD_PAGING_SIZE = 10;

    /**
     * hard-coded icon name for a blank icon in a combobox,
     * matching icon in resource folder
     * to support for combobox javascript template pattern
     */
    public final static String BLANK_ICON_NAME = "BLANK";

    /**
     * hard-coded icon name for a VIP icon in a combobox,
     * matching icon in resource folder
     * to support for combobox javascript template pattern
     */
    public final static String VIP_ICON_NAME = "VIP";

    /*
    * Used for SELENIUM automated testing */
    public static final String SELENIUM_TAG = CommonConstants.SELENIUM_TAG;

    public static final String URL_QUERY_PARAM_WITHOUT_EQUALS = "url";

    public static final String SUMMARY_LTE = "SUMMARY_LTE";

    public static final String DASHBOARD_TAB = "DASHBOARD_TAB";

    public static final String DEFAULT_CHART_DRILL_DOWN_PARAM = "drilldown=";

    public static final String SEARCH_FIELD_IMSI_PARAM = "imsi=";

    public static final String SEARCH_FIELD_PTMSI_PARAM = "ptmsi=";

    public static final String SEARCH_FIELD_MSISDN_PARAM = "msisdn=";

    public static final String PARAM_IMSI_TYPE = "type=IMSI";

    /**
     * URL outbound parameter for group type
     * (same for all groups = so don't add to metadata)
     * hard-code "type=" once here for all groups
     * (e.g. grouptype=APN)
     */
    public final static String GROUP_TYPE_PARAM = "type=";

    public static final String UNGROUPED = "Uncategorized";

    /**
     * URL outbound parameter for group name
     * (same for all groups = so don't add to metadata)
     */
    public final static String GROUP_VALUE_PARAM = "groupname=";

    public static final String DATE_IN_JSON_FORMAT = "ddMMyyyy";

    public static final String TIME_IN_HH_MM_FORMAT = "HH:mm";

    public static final String DEFAULT_TIME_PARAMETER = "30";

    public static final String KPIPANEL_DISPLAY_TAB = "NETWORK_TAB";

    /* always default to a week */

    public static final String ONE_WEEK_MS_TIME_PARAMETER = "10080";

    public static final String ONE_DAY_MS_TIME_PARAMETER = "1440";

    public static final String TWELVE_HOURS_MS_TIME_PARAMETER = "720";

    public static final String SIX_HOURS_MS_TIME_PARAMETER = "360";

    public static final String TWO_HOURS_MS_TIME_PARAMETER = "120";

    public static final String ONE_HOUR_MS_TIME_PARAMETER = "60";

    public static final String THIRTY_MINS_MS_TIME_PARAMETER = "30";

    public static final String FIFTEEN_MINS_MS_TIME_PARAMETER = "15";

    public static final String DEFAULT_BUSY_DAY_TIME_PARAMETER = CommonConstants.ONE_WEEK_MS_TIME_PARAMETER;

    /* always default to a day */
    public static final String DEFAULT_BUSY_HOUR_TIME_PARAMETER = ONE_DAY_MS_TIME_PARAMETER;

    /** index position on time combobox for "30 mins" (i.e. brittle for meta data change) */
    public static final int DEFAULT_TIME_RANGE_SELECTED_INDEX = 1;

    public static final int ONE_WEEK_TIME_RANGE_SELECTED_INDEX = 7;

    public static final String ONE_WEEK_TIME_RANGE_DISPLAY = "1 week";

    public static final int ONE_DAY_TIME_RANGE_SELECTED_INDEX = 6;

    public static final String ONE_DAY_TIME_RANGE_DISPLAY = "1 day";

    /** index position on time combobox for "1 day" (i.e. brittle for meta data change) */
    public static final int DEFAULT_BUSY_HOUR_TIME_RANGE_SELECTED_INDEX = 6;

    /** index position on time combobox for "1 week" (i.e. brittle for meta data change) */
    public static final int DEFAULT_BUSY_DAY_TIME_RANGE_SELECTED_INDEX = 7;

    public static final String DISPLAY_TYPE_PARAM = "display=";

    public static final String TIME_ZONE_PARAM_WITHOUT_EQUALS = "tzOffset";

    public static final String MAX_ROWS_URL_PARAM = "maxRows=";

    public static final String MAX_ROWS_URL_PARAM_WITHOUT_EQUALS = "maxRows";

    public static final String DEFAULT_MAX_ROWS_VALUE = "ENIQ_EVENTS_MAX_JSON_RESULT_SIZE";

    public static final String TERMINAL_ANALYSIS_WIN_ID_PREFIX = "TERMINAL_MOST_";

    public static final String LTE_HFA_QOS_QCI_SUMM_WIN_ID_PREFIX = "NETWORK_RAN_LTE_HFA_QOS_QCI_SUMM";

    public static final String LTE_CFA_QOS_QCI_SUMM_WIN_ID_PREFIX = "NETWORK_RAN_LTE_CFA_QOS_QCI_SUMM";

    public static final String OUT_BOUND_CHART_DISPLAY_PARAM = "chart";

    public static final String OUT_BOUND_GRID_DISPLAY_PARAM = "grid";

    public static final String TYPE_PARAM = "type=";

    public static final String KEY_PARAM = "key=";

    public static final String DATA_TIERED_DELAY_PARAM = "dataTieredDelay=";

    /*
    * live load search field parameters
    */
    public static final String TOTAL_COUNT_NAME = "totalCount";

    public final static String LIVE_LOAD_ID = "id";

    public static final int STATUS_CODE_OK = 200;

    /*
    * Dash for example used on title bar
    */
    public final static String DASH = " - ";

    //used in window titles.
    public final static String ARROW = " > ";

    public static final int CIRCUIT_SWITCHED_RAB_FAILURES = 0;
    public static final int PACKET_SWITCED_RAB_RAILURES = 1;
    public static final int MULTI_RAB_FAILURES = 2;
    public static final int TOTAL_RAB_FAILURES = 3;
    //Titles
    public static final String TITLE_TOTAL_RAB_FAILURES = "Total RAB Failures";
    public static final String TITLE_CIRCUIT_SWITCHED_RAB_FAILURES = "Circuit Switched RAB Failures";
    public static final String TITLE_PACKET_SWITCED_RAB_RAILURES = "Packet Switched RAB Failures";
    public static final String TITLE_MULTI_RAB_FAILURES = "Multi RAB Failures";

    public static final String CATEGORY_ID = "categoryId=";

    public final static String UNDERSCORE = "_";

    public final static String EQUAL_STRING = "=";

    public final static String AMPERSAND = "&";

    public final static String EMPTY_STRING = "";

    public final static String COMMA = ",";

    public final static String COMMA_SPACE = COMMA + " ";

    public final static String SEMI_COLON = ":";

    public final static String SINGLE_SPACE = " ";

    public final static String OPEN_BRACKET = "(";

    public final static String CLOSE_BRACKET = ")";

    public final static String MISSING_INPUT_DATA = "Input data";

    public final static String NEED_SEARCH_FIELD_MESSAGE = "Valid search field data required to populate window";

    public final static int MIN_COLUMN_WIDTH = 80;

    /* Must match with header "Occurrences" for CAUSE_CODE_ANALYSIS in UIMetaData for totals to be calculated correctly */
    public final static String OCCURRENCES = "Occurrences";

    public final static String CAUSE_CODE_ID = "Cause Code ID";

    public final static String CAUSE_CODE_ID_LOWER = "Cause Code Id";

    public final static String OFFSET = "offset";

    public final static String NULL = "null";

    public final static String ENIQ_EVENTS_UI = "EniqEventsUI";

    public final static String TIME_FROM_URL_PARAM = "timeFrom";

    public final static String TIME_TO_URL_PARAM = "timeFrom";

    public final static String TIME_URL_PARAM = "time";

    public final static String DATA_TIME_FROM_PARMA_JSON_RESPONSE = "dataTimeFrom";

    public final static String DATA_TIME_TO_PARMA_JSON_RESPONSE = "dataTimeTo";

    public final static String DATA_TIMEZONE_PARMA_JSON_RESPONSE = "timeZone";

    public final static String EXCLUSIVE_TAC = "List of excluded TACs";

    public final static String UNDER_SCORE_GROUP = "_GROUP";

    public final static String CSV_MAX_ROWS_VALUE = "0";

    public final static String CANCEL_MASK_BTN = "btncancelMask";

    public static final String SUCCESS = "success";

    public static final String ERROR_DESCRIPTION = "errorDescription";

    public static final String LICENSE_ERROR_DESCRIPTION_SUBSTRING = "not licensed";

    public static final String UNKNOWN_ERROR = "unknown error";

    public static final String INSTALL_ERROR ="not been installed";

    public static final String TECHPACK_NOT_INSTALLED ="The Techpack has not been installed";

    public static final String EMPTY_RESPONSE ="Empty Response";

    public static final java.lang.String TAC_NOT_SAVED =  "\t Could not save the group. \n A possible reason is a TAC might have been added as an Exclusive TAC. \n Check the EXCLUSIVE_TAC group.";

    public static final String NO_LICENSE_MESSAGE_TITLE = "No licence";

    public static final String LICENSE_ERROR = "This Feature is not licensed";

    public static final String NO_LICENSE_FOUND_TO_DISPLAY_VOICE_DATA = "No licence found to show menu options.\nPlease contact the System Administrator";

    public static final String NO_LICENSE_FOUND_TO_DISPLAY_ROLE_BASED = "No role licence found to show menu options.\nPlease contact the System Administrator";

    public final static String DEFAULT_COLUMN_GROUP = "Default";

    public final static String ACTIVE_COLUMN_GROUP = "Active Columns";

    public final static String OTHER_COLUMN_GROUP = "Others";

    public final static String SELECT_ALL = "Select All";

    public static final String CHECK_GLASSFISH_LOG_MESSAGE = "A services error has occurred. Please check glassfish logs.\n";

    public static final String SERVICES_ERROR = "A Services error has occurred";

    public static final String PARSE_ERROR = "Error parsing result, \n please contact the System Administrator ";

    public static final String LOGIN_AGAIN = "Your session has expired please log in again. \n Contact the System Administrator if issue persists.";

    public static final String LOAD_REPORTS_MESSAGE = "Load Reports failed, please check server logs: ";

    public static final String LOAD_DATA_MESSAGE ="Load data failed, please check server logs";

    public static final String NO_CAUSE_CODES_FOUND_WIZARD_MESSAGE = "No cause codes found for selected time range";

    public static final String SERVER_CORRUPT_RESPONSE = "Corrupted response received from server";

    public static final String SERVER_ERROR = "Server Error";

    public static final String ERROR = "Error";

    public static final String WARNING = "Warning";

    public final static String UNDEFINED_ERROR_FROM_SERVER_MESSAGE = "An undefined error message was received! \n"
            + "Please refer to the browser logs for more details. ";

    public final static String EMPTY_RESPONSE_FROM_SERVER ="Empty Response Received from Server";

    public static final String TIMEOUT_EXCEPTION = "Request timeout exceeded";

    public static final String INVALID_IMSI ="Invalid IMSI Format";

    public static final String INVALID_IMSI_MESSAGE ="IMSI must be numeric, with a maximum of 18 digits.";

    public static final String INVALID_MSISDN ="Invalid MSISDN Format";

    public static final String INVALID_MSISDN_MESSAGE ="MSISDN must be numeric, with a maximum of 18 digits.";

    public static final String INVALID_PTMSI ="Invalid PTMSI Format";

    public static final String INVALID_PTMSI_MESSAGE ="PTMSI must be numeric and less than 4294967295.";
    /*
    * internal runtime seperator putting in winId for multiple instances function(not for cookie)
    */
    public static final String MULTI_IDENTIFER = "MULTI_IDENTIFER";

    /* used in cancel requests, etc, when don't care about window ids */
    public final static MultipleInstanceWinId EMPTY_WIN_ID = new MultipleInstanceWinId(EMPTY_STRING, EMPTY_STRING);

    public final static String CC_WIZARD_QUERY_STRING = "&causeCodeIds=";

    public final static String LAUNCH_GRID = "launchGrid";

    public final static String LAUNCH_CHART = "launchChart";

    public final static String IS_DRILL_IDENTIFIER = "_DRILL";

    public final static String NO_DATA_MESSAGE_DASHBOARD = "No Data";

    public final static String DIMENSION_NOT_SUPPORTED_MESSAGE_DASHBOARD = "Dimension Not Applicable";

    //initial url from UI to Services(and only once) if kpi license is not installed
    public final static String KPI_URL = "NETWORK/KPI_NOTIFICATION/NOTIFICATION_COUNT";

    //icon constant to differentiate windows from kpi side panel with others
    public final static String KPI_PANEL_ICON = "SEVERITY";

    //Regular Expression for imsi: 1 - 18 digits.
    public static final String IMSI_PATTERN = "^\\d{1,18}$";

    //Regular Expression for msisdn: 1 - 15 digits.
    public static final String MSISDN_PATTERN = "^\\d{1,18}$";

    //Regular Expression for P-TMSI: 32bits (4Octets). Stored as
    // decimal in the DB
    public static final String PTMSI_PATTERN = "^\\d{1,10}$";

    public static final String IMSI = "IMSI";

    public static final String MSISDN = "MSISDN";

    public static final String PTMSI = "PTMSI";
    
    public static final String NOT_FOR_DISPLAY = "NOT_FOR_DISPLAY";
    
    public static final String CHART_TITLE_PARAM = "chartTitle";

    public static final String CHART_TIME_TICK_INTERVAL_PARAM = "timeTickInterval";

    public static final String CHART_START_TIME_PARAM = "dataTimeFrom";

    public static final String CHART_END_TIME_PARAM = "dataTimeTo";

    public static final String RAT_VALUE_FOR_LTE = "2";

    public static final String RAT_VALUE_FOR_GSM = "0";
    
    public static final String NETWORK_CAUSE_CODE_ANALYSIS = "NETWORK_CAUSE_CODE_ANALYSIS";

    public static final String DRILL_CAT = "drillCat=";

    public static final String A_PROBLEM_OCCURRED = "A problem occurred";

}
