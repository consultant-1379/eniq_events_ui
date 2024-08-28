/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.common;

/**
 * Complete this class when MetaReader more complete. All String constants used in meta JSON file should be maintained from here (only)
 * <p/>
 * MetaReader should be only user of these (to maintain).
 */
public abstract class MetaReaderConstants {

    private MetaReaderConstants() {
    }

    /* the identifier of the node that holds the data in the returned JSON array */
    public final static String JSON_ROOTNODE = "data";

    public static final String ID = "id";

    /**
     * Paired search field for "- select input -" selection (requiring no group or search value input display in paired selection)
     */
    public static final String INPUT = "INPUT";

    /**
     * Paired search field for "Network" selection (requiring no group or search value input display in paired selection)
     * <p/>
     * Note if ever have different Network Types (e.g. Core Network, WRAN Network), then agree that URL address tag (id) startsWith SUMMARY for
     * SearchFiledDataType to work) e.g DASHBOARD/ROAMER/SUMMARY_CORE? DASHBOARD/ROAMER/SUMMARY_WRAN?
     */
    public static final String SUMMARY = "SUMMARY";

    /**
     * search field component special case
     */
    public static final String TERMINAL = "Terminal";

    /**
     * button Id for properties button in toolbar
     */
    public static final String BTN_PROPERTIES = "btnProperties";

    /**
     * button Id for KPI button in toolbar
     */
    public static final String BTN_KPI = "btnKPI";

    /**
     * KPI button in CS mode. Because KPI is launched from a window (as apposed to the search field) and that window can be in a CS view or a PS view
     * - we want no confusion about which meta data will be read - we are give CS KPI is own Id (so goes to different event_Id and don't need window
     * to own CS or PS propery)
     */
    public static final String BTN_KPI_CS = "btnKPI_CS";

    /**
     * button Id for SAC button in toolbar
     */
    public final static String BTN_SAC = "btnSac";

    /**
     * button Id for subscriber details (little man icon) in toolbar
     */
    public final static String BTN_SUBSCRIBER_DETAILS = "btnSubscriberDetails";

    /**
     * Subscriber details button in CS mode (must for to a different URL tan PS one so give button a whole new identity)
     */
    public final static String BTN_SUBSCRIBER_DETAILS_CS = "btnSubscriberDetails_CS";

    /**
     * MSISDN Subscriber details button Id in CS mode
     */
    public final static String BTN_SUBSCRIBER_DETAILS_MSISDN_CS = "btnSubscriberDetailsMSISDN_CS";

    /**
     * MSISDN Subscriber details button Id in CS mode
     */
    public final static String BTN_SUBSCRIBER_DETAILS_WCDMA_CFA = "btnSubscriberDetailsWcdmaCFA";

    /**
     * button Id for subscriber details (little man icon) in toolbar for PTMSI
     */
    public final static String BTN_SUBSCRIBER_DETAILS_PTMSI = "btnSubscriberDetailsPTMSI";

    /**
     * button Id for CSV export in toolbar
     */
    public final static String BTN_EXPORT_BUTTON = "btnExport";

    /**
     * button Id for hide show legend in toolbar
     */
    public final static String BTN_LEGEND = "btnHideShowLegend";

    /**
     * button Id for toggle from graph to grid in toolbar
     */
    public final static String BTN_GRAPH_TO_GRID_TOGGLE = "btnToggleToGrid";

    /**
     * button Id for recurring error information button
     */
    public final static String BTN_RECUR_ERROR = "btnRecur";

    /**
     * button Id for the Back button on the toolBar
     */
    public static final String BTN_BACK = "btnBack";

    /**
     * button Id for the Forward button on the toolBar
     */
    public static final String BTN_FORWARD = "btnForward";

    /**
     * button Id for the Navigation button on the toolBar (Bread crumb implemented as a splitter button)
     */
    public static final String BTN_NAV = "btnNav";

    /**
     * searchFields id used for cell search type,"id" : "CELL",
     */
    public static final String SEARCH_FIELD_CELL_TYPE = "CELL";

    public static final String RAT_PARAM_FOR_LTE = "RAT=2";

    public static final String RAT_PARAM_FOR_GSM = "RAT=0";

    public static final String RAT_ID = "RAT ID";

    /**
     * Failures column header name
     */

    public static final String FAILURES_COLUMN_HEADER = "Failures";
    
    public static final String NUMBER_ERABS_COLUMN_HEADER = "Number of ERABs";

    public static final String NO_ERABS_COLUMN_HEADER = "No of ERABS";

    /**
     * Column header names for Manufaturer and Model
     */
    public static final String MANUFACTURER_COLUMN_HEADER = "Manufacturer";

    public static final String MODEL_COLUMN_HEADER = "Model";

    /**
     * Headers are named as "Access Area"
     */
    public static final String ACCESS_AREA = "Access Area";

    /**
     * Impacted subscribers column header in metadata file (needed to ensure it will be right justified)
     */
    public static final String IMPACTED_SUBSCRIBERS_COLUMN_HEADER = "Impacted Subscribers";

    public static final String IMPACTED_SUBSCRIBERS_LTE_COLUMN_HEADER = "Impacted Subscribers (LTE)";

    // collumn headers which must remain in synch with JsonObjectWrapper
    public static final String KPI_COLUMN_HEADER = "KPI";

    public static final String KPI_SUCCESS_RATIO_COLUMN_HEADER = "Success Ratio";

    /**
     * HACK but cannot be avoided, need a placeholder menuItem in order to render the navigational button as a splitter
     */
    public static final String BTN_NAV_PLACEHOLDER = "DUMMY_VALUE";

    /**
     * Display tag in metadata indicating indicating pie chart required
     */
    public static final String PIE_CHART_DISPLAY = "pie";

    public static final String CC_MENU_ITEM = "ccMenuItem";

    public static final String SCC_MENU_ITEM = "sccMenuItem";

    public static final String CS_CC_MENU_ITEM = "ccMenuItemCS";

    public static final String CS_SCC_MENU_ITEM = "sccMenuItemCS";

    public static final String CCWCDMAMenuItem = "ccWCDMAMenuItem";

    public static final String SCCWCDMAMenuItem = "sccWCDMAMenuItem";

    public static final String DCWCDMAMenuItem = "dcWCDMAMenuItem";

    public static final String CCLTECFAMenuItem = "ccLTECFAMenuItem";

    public static final String CCLTEHFAMenuItem = "ccLTEHFAMenuItem";

    public static final String CCWCDMAHFAMenuItem = "ccWCDMAHFAMenuItem";

    public static final String SCCWCDMAHFAMenuItem = "sccWCDMAHFAMenuItem";

    public static final String CCGSMMenuItem = "ccGSMMenuItem";

    public static final String SCCGSMMenuItem = "sccGSMMenuItem";

    /*
     * subscriber details menu item - packet switched
     */
    public static final String SD_MENU_ITEM = "sdMenuItem";

    /*
     * subscriber details menu item - PTMSI
     */
    public static final String SD_MENU_ITEM_PTMSI = "ptmsiSdMenuItem";

    /*
     * subscriber details menu items (IMSI) - circuit switched
     */
    public static final String CS_SD_MENU_ITEM = "sdMenuItemCS";

    public static final String CS_SD_MENU_ITEM_MSISDN = "msisdnSdMenuItemCS";

    public static final String WCDMA_CFA_SD_MENU_ITEM = "wcdmaCfaSdMenuItem";

    public static final String SAC_MENU_ITEM = "sacMenuItem";

    public static final String PARAM_TYPE = "type";

    public static final String DRILL_TARGET_TYPE = "drillTargetType";

    public static final String PARAM_KEY = "key";

    public final static String ERR = "ERR";

    public final static String PARAM_EVENT_ID = "eventID";

    public static final String REJECT_CELL_VALUE = "REJECT";

    public static final String EVENT_RESULT_HEADER = "Event Result";

    public static final String EVENT_TIME = "Event Time";

    public static final String NO_URL_LINK = "NO_LINK";

    /**
     * Time (int) in days for which a dashboard time component allows user to select from today = e.g. 45 days back from today
     */
    public static final String MAX_DAYS_BACK = "maxDaysBack";

    /*
     * controller type in meta data (searchFields.json)
     */
    public static final String CONTROLLER_TYPE = "BSC";

    //////////////////////////////////////////////////////////////////
    /* not public below here */

    static final String LOADING_MSG = "loadingMsg";

    static final String LOADING_MSG_RENDERING = "renderingMsg";

    static final String TOOLBARS = "toolBars";

    static final String TAB_MENU_ITEMS = "tabMenuItems";

    static final String TABS = "tabs";

    static final String TAB_OWNER = "tabOwner";

    static final String TASK_BAR_BUTTONS = "taskBarButtons";

    static final String NAME = "name";

    static final String HEIGHT = "height";

    static final String PARAMS = "params";

    static final String THRESHOLDS = "thresholds";

    static final String THRESHOLD_ID = "id";

    static final String THRESHOLD_NAME = "name";

    static final String THRESHOLD_LOWEST = "lowest";

    static final String THRESHOLD_HIGHEST = "highest";

    static final String THRESHOLD_FORMAT = "format";

    /*
     * To date the "name" tag has been used in "tabMenuItems" to support both the name used in the menu item and the related name of the minimised
     * button on the menu taskbar Now (in some cases) we want the name on the minimized button on the taskbar to be different than the menu item name.
     * When that is needed add this key to the menu in the "tabMenuItems" section
     */
    static final String MIN_MENU_NAME_ON_TASKBAR = "nameForTaskBar";

    /*
     * search field support, when window must react to search field for windows created from multiple meta data sources (open PS and CS windows react
     * when say Controller changes in search field)
     */
    static final String WINDOW_META_SUPPORT = "winMetaSupport";

    static final String ITEMS = "items";

    static final String TIP = "tip";

    static final String STYLE = "style";

    static final String IS_ROLE_ENABLED = "isRoleEnabled";

    static final String IS_MODULE = "isModule";

    static final String IS_FORCIBLY_ENABLED = "isForciblyEnabled";

    /**
     * Ignored and not shown tabs after introducing user tabs. Note: see {@link #IS_FORCIBLY_ENABLED} to include them from metadata.
     * 
     * @see #IS_FORCIBLY_ENABLED
     */
    static final String[] IGNORED_BY_DEFAULT_TABS = { "NETWORK_TAB", "TERMINAL_TAB", "SUBSCRIBER_TAB", "RANKINGS_TAB" };

    static final String SEPARATOR = "SEPARATOR";

    static final String URL = "url";

    static final String PORTLET_TYPE = "portletType";

    static final String ISVISIBLE = "isVisible"; // default True

    /*
     * toolbar button visibility based on search field type
     */
    static final String VISIBLE_WHEN = "visibleWhen";

    static final String ISENABLED = "isEnabled";

    /**
     * Windows which need search parm interested in search field updates. Values can be TRUE, PATH or FALSE (default false if not present)
     */
    static final String NEED_SEARCH_PARAM = "needSearchParam";

    /**
     * Extra for search field user, in place of specifing "needSearchParam":"TRUE" - can also use "needSearchParam":"PATH", meaning you want to concat
     * node type onto path address rather than use say, URL_PATH?time=xx&type=APN replaced with URL_PATH/APN
     */
    static final String PATH = "PATH";

    /**
     * Coded for dashboard winow only. When search type (id from searchFields.json) is not applicable for window function (e.g. CELL for data volumne)
     * then add these to comma seperated string as excluded
     */
    static final String EXCLUDED_SEARCH_TYPES = "excludedSearchTypes";

    // TODO support for this in code has been removed so probably shoudl remove all referecnes */
    static final String IS_EMPTY_AND_FULL_SEARCH_FIELD_USER = "isEmptyAndFullSearchFieldUser";

    static final String HAS_MULTIDISPLAY = "hasMultiGridResults";

    static final String LIVE_LOAD_SEARCH_FIELD_URL = "liveLoadURL";

    /* is it a combobox or menu item being used for paired search component */
    static final String USE_MENU_FOR_TYPE = "isUsingMenuForType";

    static final String TYPE_EMPTY_TEXT = "typeEmptyText";

    static final String WINDOW_TYPE = "windowType";

    static final String TAB_ITEM_CENTER_STYLE = "tabItemCenterStyle";

    /* upper */
    static final String TOOL_BAR_TYPE = "toolBarType";

    static final String TOGGLE_TOOL_BAR_TYPE = "toggleToolBarType";

    static final String BOTTOM_TOOL_BAR_TYPE = "bottomToolBarType";

    static final String DRILL_TOOLBAR_TYPE = "drillToolBarType";

    static final String CHART_CLICK_URL_PARAM_KEY = "chartClickedURLParam";

    static final String WINDOW_DISPLAY = "display";

    /**
     * (Used in porlets) Comma seperated string specifing relative time (mins) to set as timeFrom (relative to user selected timeTo). Split to cater
     * for node selection, e.g. ""*,1440,CELL,10080,BSC,2880"
     */
    static final String DATE_FROM = "dateFrom";

    static final String ISTOGGLE = "isToggle";

    static final String PANELS = "panels";

    static final String TOOL_BAR_BUTTONS = "toolBarButtons";

    static final String SEARCH_FIELDS = "searchFields";

    static final String WIZARD_ID = "wizardID";

    static final String GROUP_COMPONENTS = "groupComponents";

    static final String SEARCH_TYPE = "searchType";

    static final String INT_SEARCH_TYPE = "INT";

    static final String NUMBER_TYPE = "isNumberSearchType";

    static final String EMPTY_TEXT = "emptyText";

    static final String URL_PARAM = "urlParam";

    static final String STRING_SEARCH_TYPE = "STRING";

    static final String PAIRED_SEARCH_TYPE = "PAIRED";

    static final String PAIRED_LIVE_TYPE = "PAIRED_LIVE_TYPE";

    static final String IS_GROUP = "isGroup";

    static final String TYPE = "type";

    static final String GROUP_TYPE = "groupType";

    /*
     * hack - for terminal type (make) when want to ignore type selected on outbound parameter and pass type = TAC
     */
    static final String PERIMINENT_TYPE = "perminentType";

    static final String GROUP_SINGLE_TOGGLE = "groupSingleToggles";

    static final String DEFAULT_ID_TYPE = "defaultIDType";

    static final String INDEX = "index";

    static final String LIVE_TYPE_URL = "liveTypeURL";

    static final String EVENTID = "eventID";

    static final String DISABLE_WHEN = "disableWhen";

    static final String DISABLE_TIME = "disableTime";

    static final String TIMEDATA = "comboTimeData";

    static final String MAIN_TABITEM_STYLE = "main_tab_item_style";

    static final String GRID_CONSTANT_SETTINGS = "gridConstants";

    static final String MASTER_META_DATA_CHANGER = "masterMetaDataChanger";

    static final String SHORT_TEXT = "shortText";

    static final String KEY = "key";

    static final String META_DATA_PATH = "metaDataPath";

    static final String IS_LICENCED = "isLicenced";

    static final String ROWS_PER_PAGE = "rowsPerPage";

    static final String IS_AUTO_REFRESH_FEATURE_ON = "autoRefreshFeatureOn";

    static final String AUTO_REFRESH_RANKING_INTERVAL_MS = "autoRefreshIntervalMS";

    static final String MAX_INSTANCE_WINDOWS_PER_TYPE = "maxWindowsPerInstanceType";

    static final String DRILL_DOWN_WINDOW_SECTION = "drilldownWindows";

    static final String CHART_DRILL_DOWN_WINDOW_SECTION = "chartDrillDownWindows";

    static final String DRILL_PARAMETER_SECTION = "params";

    static final String DRILL_PARAMETER_NAME = "queryParamName";

    static final String DRILL_PARAMETER_VALUE = "id";

    /* read parameter if there, if not there don't worry about it */
    static final String OPTIONAL = "optional";

    static final String GRID_SECTION = "grids";

    static final String WIDGET_SPECIFIC_PARAMS = "widgetSpecificParams";

    static final String DRILL_DOWN_GRID_DISPLAY = "gridDisplayID";

    static final String DRILL_DOWN_TARGET_DISPLAY_ID = "drillTargetDisplayId";

    static final String DRILL_WINDOW_NAME = "drillWindowName";

    /*
     * Seperate menu items section (outside tabMenuItems) to be able to find extra launch windows, etc, for #findMetaMenuItemForTaskBarButtonsList
     */
    static final String EXTRA_MENU_ITEMS = "extraMenuItems";

    static final String KPI_MENU_ITEM = "kpiMenuItem";

    static final String KPI_MENU_ITEM_CS = "kpiMenuItemCS";

    static final String CS_KPI_ANALYSIS_GRID_NAME = "CS_KPI_ANALYSIS";

    static final String KPI_ANALYSIS_GRID_NAME = "KPI_ANALYSIS";

    static final String RECUR_ERR_MENU_ITEM = "recurErrMenuItem";

    static final String RECUR_ERR_MENU_ITEM_NETWORK = "recurErrMenuItemNetwork";

    static final String RECUR_ERR_MENU_ITEM_SUBSCRIBER = "recurErrMenuItemSubscriber";

    static final String CHARTS_SECTION = "charts";

    static final String FIXEDVALUE = "isFixedVal";

    static final String TITLEPARAM = "isTitleParam";

    static final String INFO_TIP = "infoTip";

    static final String LOAD_GROUP_URL = "loadGroupURL";

    static final String LAUNCH_WINS = "launchWindows";

    public static final String DASHBOARDS = "dashboards";

    public static final String DASHBOARDS_LTE = "dashboardsLTE";

    static final String LAUNCH_WIN_TYPEID = "launchWindowTypeId";

    static final String LAUNCH_WIN_MENUITEM = "menuItem";

    static final String LAUNCH_WIN_SEARCH_PARAM = "searchValFromCol";

    static final String BLANK = "BLANK";

    static final String DEFAULT_MENU = "defaultMenuItemId";

    static final String COLUMN_HEADER_TITLE = "columnHeaderPartOfTitle";

    static final String MAX_ROWS_PARAM = "maxRowsParam";

    public static final String CC_WIZARD_ID = "1";

    public static final String CC_WIZARD_DESC = "2";

    public static final String CC_WIZARD_DESC_TEXT = "CC";

    public static final String DATA_TIERED_DELAY = "dataTieredDelay";

    static final String WIZARD_SECTION = "wizards";

    static final String WIZARD_URL = "loadURL";

    static final String WIZARD_CONTENT_STYLE = "wizardContentStyle";

    /**
     * determine if the type of drilldown
     */
    static final String DRILL_TYPE = "drillType";

    static final String LICENCE_GROUP_TYPES = "licenceGroupTypes";

    static final String GROUP_MANAGEMENT_CONFIGURATION = "groupManagementConfiguration";

    static final String KPIPANEL_SECTION = "kpiPanel";

    static final String KPI_CONFIGURATION_PANEL = "kpiConfigurationPanel";
}
