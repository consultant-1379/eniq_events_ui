/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.eniq.events.ui.client.common; // NOPMD

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.PerformanceUtil;
import com.ericsson.eniq.events.common.client.datatype.*;
import com.ericsson.eniq.events.common.client.datatype.ThresholdDataType.Format;
import com.ericsson.eniq.events.common.client.json.IJSONArray;
import com.ericsson.eniq.events.common.client.json.IJSONObject;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.common.client.json.MetaDataParserUtils;
import com.ericsson.eniq.events.ui.client.common.comp.MenuTaskBar;
import com.ericsson.eniq.events.ui.client.common.widget.MetaDataChangeComponent;
import com.ericsson.eniq.events.ui.client.common.widget.SpacerComponent;
import com.ericsson.eniq.events.ui.client.datatype.*;
import com.ericsson.eniq.events.ui.client.datatype.LaunchWinDataType.DrillTargetType;
import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType.Type;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.DashBoardDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletType;
import com.ericsson.eniq.events.ui.client.datatype.group.GroupManagementItemDataType;
import com.ericsson.eniq.events.ui.client.datatype.group.GroupMgmtConfigDataType;
import com.ericsson.eniq.events.ui.client.datatype.group.WizardDataType;
import com.ericsson.eniq.events.ui.client.datatype.kpi.KPIConfigurationPanelDataType;
import com.ericsson.eniq.events.ui.client.main.GenericTabView;
import com.ericsson.eniq.events.ui.client.resources.EniqResourceBundle;
import com.ericsson.eniq.events.ui.client.search.*;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceUtils;
import com.ericsson.eniq.events.widgets.client.button.ImageButton;
import com.ericsson.eniq.events.widgets.client.drill.DrillCategoryType;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import java.util.*;
import java.util.logging.Logger;

import static com.ericsson.eniq.events.common.client.CommonConstants.TRUE;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;

/**
 * MetaReader is using the eventBus to let listeners know when it is ready to be read
 * (particular to case of start up)
 * <p/>
 * Want all meta data strings to be internal to this class.
 *
 * @author eeicmsy
 * @since Feb 2010
 */

@SuppressWarnings("PMD")
/* reading JSON never going to be pretty) */
public class MetaReader implements IMetaReader { // NOPMD by eeicmsy on

    private final EniqResourceBundle eniqResourceBundle;

    private final UIComponentFactory componentFactory;

    private static final Logger LOGGER = Logger.getLogger(MetaReader.class.getName());

    /**
     * Supporting multiple meta datas
     */
    private final IMultiMetaDataHelper metaData;

    private final EventBus eventBus;

    private String cachedLoadingMessage = null;

    private String cachedLoadingRenderingMessage = null;

    private int maxRecursiveCount = -1;

    private int getExtraMenuItemRecursiveCount = 0;

    /**
     * Map holder for drill manager data. No point in parsing it from JSON every time we drill. Just do it once.
     */
    private HashMap<String, List<DrillCategoryType>> drillManagerDataMap;

    /**
     * constructor - will create the MetaDataRetriever object, which contacts the
     * services layer to retrieve the meta data
     *
     * @param eventBus            standard eventBus used though out Application
     * @param multiMetaDataHelper Meta Data root node reference
     */
    @SuppressWarnings("PMD.CouplingBetweenObjects")
    @Inject
    public MetaReader(final EventBus eventBus, final IMultiMetaDataHelper multiMetaDataHelper,
            final EniqResourceBundle eniqResourceBundle, final UIComponentFactory componentFactory) {
        this.eventBus = eventBus;
        this.metaData = multiMetaDataHelper;
        this.eniqResourceBundle = eniqResourceBundle; // TODO Remove this dependency
        this.componentFactory = componentFactory;
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#loadMetaData(com.google.web.bindery.event.shared.EventBus, com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay)
    */
    @Override
    public void loadMetaData() {
        PerformanceUtil.getSharedInstance().logCurrentTime("loadMetaData : ");
        loadMetaData(null);
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#loadMetaData(com.google.web.bindery.event.shared.EventBus, com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay, java.lang.String)
    */
    @Override
    public void loadMetaData(final String metaDataPath) {
        new MetaDataRetriever(eventBus, metaDataPath);
    }

    /*
    * Complete a URL using host and port prefix
    *
    * @param urlEnd String from meta data with partial url
    *
    * @return Completed URL with base location of service
    */
    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getCompletedURL(java.lang.String)
    */
    @Override
    public String getCompletedURL(final String urlEnd) {
        return ReadLoginSessionProperties.getEniqEventsServicesURI() + urlEnd;
    }

    // *********************************************************************************
    // //
    // ASSUME static methods are called after #loadMetaData called once
    // (after MetaDataReadyEvent handled)
    // *********************************************************************************
    // //

    // WANT THIS AS ONLY CLASS WITH META DATA STRINGS (ie. not passing as
    // parameters)

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getGridRowsPerPage()
    */
    @Override
    public Integer getGridRowsPerPage() {
        return getGridConfigurationInt(ROWS_PER_PAGE, 25);
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getLoadingMessage()
    */
    @Override
    public String getLoadingMessage() {
        if (cachedLoadingMessage == null) {
            cachedLoadingMessage = getGridConfigurationString(LOADING_MSG);
        }
        return cachedLoadingMessage;
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getLoadingRenderingMessage()
    */
    @Override
    public String getLoadingRenderingMessage() {
        if (cachedLoadingRenderingMessage == null) {
            cachedLoadingRenderingMessage = getGridConfigurationString(LOADING_MSG_RENDERING);
        }
        return cachedLoadingRenderingMessage;
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getRankingTimerInterval()
    */
    @Override
    public int getRankingTimerInterval() {
        return getGridConfigurationInt(AUTO_REFRESH_RANKING_INTERVAL_MS, 60 * 1000 * 30);
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getMaxInstanceWindowsPerType()
    */
    @Override
    public int getMaxInstanceWindowsPerType() {
        return getGridConfigurationInt(MAX_INSTANCE_WINDOWS_PER_TYPE, 5);
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getIsAutoRefreshOn()
    */
    @Override
    public boolean getIsAutoRefreshOn() {
        return getGridConfigurationBoolean(IS_AUTO_REFRESH_FEATURE_ON);

    }

    /*
    * read int from gridConstants part of meta data (which could add to a
    * configuration file in time)
    */
    private Integer getGridConfigurationInt(final String param, final int defaultVal) {
        final IJSONArray gridSettings = metaData.getArray(GRID_CONSTANT_SETTINGS);
        final IJSONObject parent = gridSettings.get(0);
        final String foundValue = parent.getString(param);
        return (foundValue.length() == 0) ? defaultVal : Integer.parseInt(foundValue);
    }

    /*
    * read boolean from gridConstants part of meta data (which could add to a
    * configuration file in time)
    */
    private boolean getGridConfigurationBoolean(final String param) {
        final IJSONArray gridSettings = metaData.getArray(GRID_CONSTANT_SETTINGS);
        final IJSONObject parent = gridSettings.get(0);
        return TRUE.equalsIgnoreCase(parent.getString(param));
    }

    /*
    * read string from gridConstants part of meta data (which could add to a
    * configuration file in time)
    */
    private String getGridConfigurationString(final String param) {
        final IJSONArray gridSettings = metaData.getArray(GRID_CONSTANT_SETTINGS);
        final IJSONObject parent = gridSettings.get(0);
        return parent.getString(param);
    }

    /**
     * When live load types are not preloaded (into JsonObjectWrapper), this utility method
     * exists to read out of other metadata results
     *
     * @param searchTypeJSONResponseText alternative meta data file
     *
     * @return Collection of information objects for live load type
     */
    @Override
    public Collection<LiveLoadTypeDataType> getLiveLoadTypes(final String searchTypeJSONResponseText) {
        final List<LiveLoadTypeDataType> result = new ArrayList<LiveLoadTypeDataType>();
        final JsonObjectWrapper localMetaData = new JsonObjectWrapper(JSONUtils
                .parseLenient(searchTypeJSONResponseText).isObject());

        final IJSONArray searchTypes = localMetaData.getArray("data"); // constant not part of main meta data

        /*
        * IE bug on compiled code "null": searchTypes = $getArray(localMetaData,
        * 'data', null); for (i = 0; i < searchTypes.jsArray.length; ++i) {
        */
        if (searchTypes != null) {
            for (int i = 0; i < searchTypes.size(); i++) {
                final IJSONObject parent = searchTypes.get(i);
                if (parent != null) { // IE bug on run compiled run time if this is not here
                    final String id = parent.getString(ID);
                    final String url = parent.getString(LIVE_LOAD_SEARCH_FIELD_URL);
                    final LiveLoadTypeDataType liveloadType = new LiveLoadTypeDataType(id, url);

                    result.add(liveloadType);
                }
            }
        }
        return result;

    }

    /**
     * Reduced iteration call passing all tab info in one return data object
     *
     * @return TabDataMetaInfo list containing tab metadata
     */
    @Override
    public List<TabInfoDataType> getTabDataMetaInfo() {
        final List<TabInfoDataType> tabMetaData = new ArrayList<TabInfoDataType>();
        final IJSONArray tabs = metaData.getArray(TABS);

        nextTab: for (int i = 0; i < tabs.size(); i++) {
            final IJSONObject parent = tabs.get(i);

            final String id = parent.getString(ID);
            final boolean isForciblyEnabled = TRUE.equals(parent.getString(IS_FORCIBLY_ENABLED));

            if (!isForciblyEnabled) {
                for (final String ignoredByDefaultTab : IGNORED_BY_DEFAULT_TABS) {
                    if (ignoredByDefaultTab.equals(id)) {
                        continue nextTab;
                    }
                }
            }

            final String name = parent.getString(NAME);
            final String tip = parent.getString(TIP);
            final String style = parent.getString(STYLE);
            final String tabItemCenterStyle = parent.getString(TAB_ITEM_CENTER_STYLE);
            final boolean isRoleEnabled = TRUE.equals(parent.getString(IS_ROLE_ENABLED));
            final boolean isModule = TRUE.equals(parent.getString(IS_MODULE));
            final TabInfoDataType tabInfoDataType = new TabInfoDataType(id, name, tip, style, tabItemCenterStyle,
                    isRoleEnabled, isModule);
            if (parent.containsKey(PARAMS)) {
                final IJSONObject parameters = parent.getObject(PARAMS);
                final Set<String> keySet = parameters.keySet();
                for (final String key : keySet) {
                    final String value = parameters.getString(key);
                    tabInfoDataType.addParameter(key, value);
                }
            }
            tabMetaData.add(tabInfoDataType);
        }
        return tabMetaData;
    }

    /**
     * Read the "grids" section of meta data looking for a grid
     *
     * @param gridType grid id
     *
     * @return Grid information
     */
    @Override
    public GridInfoDataType getGridInfo(String gridType) {
        final List<String> checkedMetaData = new ArrayList<String>();
        final IJSONArray gridMeta = metaData.getArray(GRID_SECTION);

        /*
           * this is here because kpi uses fixedId and winId in opposite way
        * to rest, and so not working when add wizard overlay and say launch
           * directly as grid - breaks multiple minimised button if unravel for fixed id
        * and win id
        */
        if (gridType.startsWith(EventType.KPI_CS.toString())) {
            gridType = CS_KPI_ANALYSIS_GRID_NAME;
        } else if (gridType.equals(EventType.KPI.toString())) {
            gridType = KPI_ANALYSIS_GRID_NAME;
        }
        return getGridInfo(gridType, gridMeta, checkedMetaData);
    }

    /*
    * [Adapted to look up "grids" array in multiple metadatas scenario]
    *
    * Recursive call to find grid info from "girds" section when can not trust
    * that current metadata in force (e.g. if grid created from other grid) will
    * return the correct grid array
    *
    * @param gridId grid id
    *
    * @return Grid information
    */
    private GridInfoDataType getGridInfo(final String gridId, IJSONArray gridMeta, final List<String> checkedMetaDatas) {
        GridInfoDataType gridMetaData = null;

        boolean isFound = false;
        // iterate to the grid collection for the type that has been requested
        for (int i = 0; i < gridMeta.size(); i++) {
            final IJSONObject parent = gridMeta.get(i);

            final String currentGridId = parent.getString(ID);
            if (gridId.equals(currentGridId)) {
                gridMetaData = MetaDataParserUtils.getGridDataTypeFromJson(parent);
                isFound = true;
                break;
            }
        }
        if (!isFound) {

            /*
            * if info is displayed we must correct it for production release
             * (no worse than a recursive situation where nothing happens if operator sees this)
            */
            LOGGER.info("searching alternative metadata grids section for  " + gridId);

            gridMeta = metaData.alternativeMetaData_getArray(GRID_SECTION, checkedMetaDatas);

            if (stoppingRecursionAndWarning(gridId, GRID_SECTION, checkedMetaDatas, true)) {
                return null;
            }

            return getGridInfo(gridId, gridMeta, checkedMetaDatas);
        }
        return gridMetaData;

    }

    /**
     * Place meta data for this in master meta data only (we can read off both to
     * find it when in other mode)
     * <p/>
     * A component which is independant of tab (even if do put one on each tab)
     * Introduced to allow switching view menu for the packet switched options
     * (original) and CS (MSS) options.
     * <p/>
     * This will not be visible if only one licence present. If no master meta
     * data is used this can be null
     *
     * @return Meta data component from master meta data. Null if master meta data
     *         not present
     */
    @Override
    @SuppressWarnings("unchecked")
    public MetaDataChangeComponent getMetaDataChangeComponent() {

        final IJSONArray metaDataItems = metaData.getArray(MASTER_META_DATA_CHANGER);
        final int menuItemsSize = metaDataItems.size();

        final List<MetaDataChangeDataType> menuItems = new ArrayList<MetaDataChangeDataType>();
        for (int i = 0; i < menuItemsSize; i++) {
            final IJSONObject parent = metaDataItems.get(i);

            final IJSONArray items = parent.getArray(ITEMS);
            for (int k = 0; k < items.size(); k++) {
                final IJSONObject itemParent = items.get(k);

                final String name = itemParent.getString(NAME);
                final String shortText = itemParent.getString(SHORT_TEXT);
                final String style = itemParent.getString(STYLE);
                final String tip = itemParent.getString(TIP);
                final String metaDataPath = itemParent.getString(META_DATA_PATH);
                final String key = itemParent.getString(KEY);
                final boolean isLicenced = TRUE.equals(itemParent.getString(IS_LICENCED));

                final MetaDataChangeDataType menuItem = new MetaDataChangeDataType(name, shortText, style, tip,
                        metaDataPath, key, isLicenced); // NOPMD by eeicmsy

                menuItems.add(menuItem);
            }
        }
        // so back to user selection when user switches mode
        final String startupPath = metaData.getCurrentMetaDataPath();

        final MetaDataChangeComponent metaChangeComp = new MetaDataChangeComponent(eventBus, menuItems, startupPath);

        if (maxRecursiveCount == -1) {
            // (can not create a static instance of metaChangeComp but will all have
            // same val)
            maxRecursiveCount = metaChangeComp.getLicenceCount();
        }
        return metaChangeComp;

    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getMenuTaskBar(com.ericsson.eniq.events.ui.client.main.GenericTabView, java.lang.String)
    */
    @Override
    public MenuTaskBar getMenuTaskBar(final GenericTabView parentView, final String tabId) {

        final IJSONArray groupSearchFields = metaData.getArray(GROUP_COMPONENTS);
        // group component (can remain null) - split out to method in case ever want
        // as part of a window
        final GroupTypeSearchComponent groupSelectComponent = getGroupTypeSearchComponent(groupSearchFields, tabId); // can
        // remain
        // null

        // search-group toggle component (can remain null) - - split out to method
        // in case ever want as part of a window
        final IJSONArray groupToggleInfo = metaData.getArray(GROUP_SINGLE_TOGGLE);
        final GroupSingleToggleComponent toggleComp = getGroupSingleToggleComponent(groupToggleInfo, tabId); // can
        // remain
        // null

        /* search component */
        final IJSONArray searchFields = metaData.getArray(SEARCH_FIELDS);
        for (int i = 0; i < searchFields.size(); i++) {
            final IJSONObject parent = searchFields.get(i);
            final String tabOwner = parent.getString(TAB_OWNER);
            if (tabOwner.equals(tabId)) {
                final String searchType = parent.getString(SEARCH_TYPE);
                // its meta data can not use switch so much (have to have some
                // predefined)
                // (final String emptyText, final String param, final String style)
                final String urlParam = parent.getString(URL_PARAM);

                return createMenuTaskBar(parentView, tabId, groupSelectComponent, toggleComp, parent, searchType,
                        urlParam);
            }
        }
        return componentFactory.createMenuTaskBar(parentView, tabId, null, groupSelectComponent, toggleComp,
                EMPTY_STRING);
    }

    /*
    *
    * @param groupSearchFields
    *
    * @param tabId
    *
    * @return
    */
    private GroupTypeSearchComponent getGroupTypeSearchComponent(final IJSONArray groupSearchFields, final String tabId) {

        GroupTypeSearchComponent groupSelectComponent = null;
        for (int i = 0; i < groupSearchFields.size(); i++) {
            final IJSONObject parent = groupSearchFields.get(i);
            final String tabOwner = parent.getString(TAB_OWNER);
            if (tabOwner.equals(tabId)) {
                final GroupSelectInfoDataType groupSetupInfo = new GroupSelectInfoDataType(); // NOPMD

                groupSetupInfo.setEmptyText(parent.getString(EMPTY_TEXT));
                groupSetupInfo.setInfoTip(parent.getString(INFO_TIP));
                groupSetupInfo.setLoadGroupURL(getCompletedURL(parent.getString(LOAD_GROUP_URL)));
                groupSetupInfo.setTip(parent.getString(TIP));
                // extra for handsets and imsi change
                groupSetupInfo.setGroupType(parent.getString(GROUP_TYPE));
                groupSetupInfo.setSplitStringMetaDataKeys(parent.getString(WINDOW_META_SUPPORT));

                /* may be different types involved for nodes */

                final IJSONArray types = parent.getArray(TYPE);
                final int typesSize = types.size();
                if (typesSize > 0) {
                    groupSetupInfo.setDefaultIDType(parent.getString(DEFAULT_ID_TYPE));
                    for (int k = 0; k < typesSize; k++) {

                        final IJSONObject typeParent = types.get(k);
                        groupSetupInfo.addTypeInfo(typeParent.getString(ID), groupSetupInfo.new TypeInfo(
                                // NOPMD by eeicmsy on 15/07/10

                                getCompletedURL(typeParent.getString(LOAD_GROUP_URL)),
                                typeParent.getString(EMPTY_TEXT), typeParent.getString(WINDOW_META_SUPPORT)));

                    }
                }
                groupSelectComponent = new GroupTypeSearchComponent(tabId, groupSetupInfo); // NOPMD

            }
        }
        return groupSelectComponent;
    }

    /*
    * Get group toggle componet for menu task bar if one is required
    *
    * @return null if no group toggle component required for search field
    */
    private GroupSingleToggleComponent getGroupSingleToggleComponent(final IJSONArray groupToggleInfo,
            final String tabId) {

        GroupSingleToggleComponent toggleComp = null;

        final int menuItemsSize = groupToggleInfo.size();
        final List<GroupSingleToggleMenuItem> toggleMenuData = new ArrayList<GroupSingleToggleMenuItem>(menuItemsSize);

        for (int i = 0; i < menuItemsSize; i++) {
            final IJSONObject parent = groupToggleInfo.get(i);
            final String tabOwner = parent.getString(TAB_OWNER);
            if (tabOwner.equals(tabId)) {
                final IJSONArray items = parent.getArray(ITEMS);
                for (int k = 0; k < items.size(); k++) {
                    final IJSONObject itemParent = items.get(k);
                    toggleMenuData.add(new GroupSingleToggleMenuItem(itemParent.getString(ID), // NOPMD
                            itemParent.getString(NAME), itemParent.getString(STYLE), TRUE.equals(itemParent
                                    .getString(IS_GROUP))));
                }
            }
        }

        if (!toggleMenuData.isEmpty()) {
            toggleComp = new GroupSingleToggleComponent(tabId, toggleMenuData);
        }
        return toggleComp;
    }

    private MenuTaskBar createMenuTaskBar(final GenericTabView parentView, final String tabId,
            final GroupTypeSearchComponent groupSelectComponent, final GroupSingleToggleComponent toggleComp,
            final IJSONObject parent, final String searchType, final String urlParam) {

        final String defaultMenuID = parent.getString(DEFAULT_MENU);
        if (searchType.equals(INT_SEARCH_TYPE)) { // NOT USED TO DATE (used be when
            // had imsi on own)
            // there is only one url param
            final NumberTypeSearchComponent searchComp = new NumberTypeSearchComponent(tabId,
                    parent.getString(EMPTY_TEXT), urlParam, parent.getString(TIP));
            return componentFactory.createMenuTaskBar(parentView, tabId, searchComp, groupSelectComponent, toggleComp,
                    defaultMenuID);

        } else if (searchType.equals(PAIRED_SEARCH_TYPE)) {

            // these non final params can be in two places
            String valParam = parent.getString(URL_PARAM);
            String winMetaSupport = parent.getString(WINDOW_META_SUPPORT);

            final String submitTip = parent.getString(TIP);

            // default will be combobox for type select pulldown (when a combo need
            // empty text for type)
            final boolean isUsingMenuForType = TRUE.equals(parent.getString(USE_MENU_FOR_TYPE));
            final String typeEmptyText = parent.getString(TYPE_EMPTY_TEXT);
            final boolean isNumberType = TRUE.equals(parent.getString(NUMBER_TYPE));
            final List<LiveLoadTypeMenuItem> mappedTypes = new ArrayList<LiveLoadTypeMenuItem>();
            final String typeIndex = parent.getString(INDEX);

            final IJSONArray types = parent.getArray(TYPE);

            for (int k = 0; k < types.size(); k++) {
                final IJSONObject typeParent = types.get(k);
                final String menuItemId = typeParent.getString(ID);
                final boolean isSeparator = (SEPARATOR.equals(menuItemId));
                valParam = typeParent.getString(URL_PARAM);
                winMetaSupport = typeParent.getString(WINDOW_META_SUPPORT);

                final LiveLoadTypeMenuItem typeMenuItem;
                if (isSeparator) {
                    /*
                    * meta data will use separator to separate live-load types from
                    * groups
                    */
                    typeMenuItem = LiveLoadTypeMenuItem.getSeperator();
                } else {
                    typeMenuItem = new LiveLoadTypeMenuItem(menuItemId, typeParent.getString(GROUP_TYPE), // NOPMD

                            typeParent.getString(NAME), getCompletedURL(typeParent.getString( // NOPMD
                                    LIVE_LOAD_SEARCH_FIELD_URL)), typeParent.getString(STYLE), // NOPMD
                            typeParent.getString(EMPTY_TEXT), winMetaSupport); // NOPMD

                    typeMenuItem.setValParam(valParam);
                }
                mappedTypes.add(typeMenuItem);
            }
            if (isNumberType) {
                return componentFactory.createMenuTaskBar(parentView, tabId, new PairedNumberTypeSearchComponent(tabId,
                        mappedTypes, Integer.valueOf(typeIndex), submitTip, isUsingMenuForType, typeEmptyText),
                        groupSelectComponent, toggleComp, defaultMenuID);
            }
            final PairedTypeSearchComponent searchComp = new PairedTypeSearchComponent(tabId, mappedTypes,
                    Integer.valueOf(typeIndex), submitTip, isUsingMenuForType, typeEmptyText);
            return componentFactory.createMenuTaskBar(parentView, tabId, searchComp, groupSelectComponent, toggleComp,
                    defaultMenuID);

        } else if (searchType.equals(PAIRED_LIVE_TYPE)) { // Terminal Tab types are
            // not hard-coded in meta
            // data

            final boolean isUsingMenuForType = TRUE.equals(parent.getString(USE_MENU_FOR_TYPE));
            final String typeEmptyText = parent.getString(TYPE_EMPTY_TEXT);
            /* types not available in meta data need a server call */
            final LiveLoadTypeUnreadyHelper typeFetcher = new LiveLoadTypeUnreadyHelper(tabId,
                    parent.getString(URL_PARAM), parent.getString(TIP), parent.getString(EMPTY_TEXT),
                    parent.getString(STYLE), getCompletedURL(parent.getString(LIVE_TYPE_URL)),
                    parent.getString(PERIMINENT_TYPE), parent.getString(WINDOW_META_SUPPORT));

            final PairedTypeSearchComponent comp = typeFetcher.createSearchFieldPairedType(eventBus,
                    isUsingMenuForType, typeEmptyText);
            toggleComp.setComponents(comp, typeFetcher, groupSelectComponent, typeFetcher.needServerCall);

            return componentFactory.createMenuTaskBar(parentView, tabId, comp, groupSelectComponent, toggleComp,
                    defaultMenuID);

        } else { // default STRING_SEARCH_TYPE)) - NOT USED TO DATE
            final StringTypeSearchComponent searchComp = new StringTypeSearchComponent(tabId,
                    parent.getString(EMPTY_TEXT), urlParam);
            return componentFactory.createMenuTaskBar(parentView, tabId, searchComp, groupSelectComponent, toggleComp,
                    defaultMenuID);

        }
    }

    @Override
    public MetaMenuItemDataType getKPIMetaMenuItemDataType() {
        final IJSONArray kpiItems= getExtraMenuItemArray(KPI_MENU_ITEM);
        return getKPIMetaMenuItemDataType(kpiItems);
    }

    @Override
    public MetaMenuItemDataType getKPICSMetaMenuItemDataType() {
        final IJSONArray kpiItems= getExtraMenuItemArray(KPI_MENU_ITEM_CS);
        return getKPIMetaMenuItemDataType(kpiItems);
    }

    private MetaMenuItemDataType getKPIMetaMenuItemDataType(IJSONArray kpiItems){
        final IJSONObject parent = kpiItems.get(0);
        final String wsURL = getCompletedURL(parent.getString(URL));
        final String toolBarType = parent.getString(TOOL_BAR_TYPE);
        final String toggleToolBarType = parent.getString(TOGGLE_TOOL_BAR_TYPE);
        final String display = parent.getString(WINDOW_DISPLAY);
        final SearchFieldUser isSearchFieldUser = SearchFieldUser.TRUE; // known specific for function to
        // work
        final String type = parent.getString(PARAM_TYPE);
        final String key = parent.getString(PARAM_KEY);
        final String maxRowsParam = parent.getString(MAX_ROWS_PARAM); // Shouldn't
        // exist
        final String minimizedButtonName = parent.getString(MIN_MENU_NAME_ON_TASKBAR);
        final String wizardId = parent.getString(WIZARD_ID);
        final boolean isDisablingTime = TRUE.equals(parent.getString(DISABLE_TIME));
        /*
        * We would like win id set at run time (as need node name to preserve
        * uniqueness) but we also need a fixed id to get chart metadata so this id
        * is temporary until have meta data for chart fetched
        */
        final String id = parent.getString(ID);

        // KPI or KP_CS
        return new MetaMenuItemDataType.Builder()
                .id(id)
                .url(wsURL)
                .isSearchFieldUser(isSearchFieldUser)
                .windowType(Type.CHART)
                .display(display)
                .type(type)
                .key(key)
                .toolBarHandler(
                        new ToolBarStateManager(toolBarType, ToolBarStateManager.BottomToolbarType.PLAIN,
                                toggleToolBarType)).maxRowsParam(maxRowsParam).wizardID(wizardId)
                .minimizedButtonName(minimizedButtonName).isDisablingTime(isDisablingTime).build();
    }

    /**
     * Meta Menu for Recurring Error window specific - button on toolbar launching
     * a grid from selected row parameters
     *
     * @return specific Menu Item for recurring error window
     */
    @Override
    public MetaMenuItemDataType getRecurErrMetaMenuItemDataType() {

        final IJSONArray recurErrItems = getExtraMenuItemArray(RECUR_ERR_MENU_ITEM);
        final IJSONObject parent = recurErrItems.get(0);
        final String name = parent.getString(NAME);
        final String id = parent.getString(ID);
        final String style = parent.getString(STYLE);
        final String wsURL = EMPTY_STRING; // populate later //
        // getCompletedURL(metaData.getString(URL,
        // parent));
        final String toolBarType = parent.getString(TOOL_BAR_TYPE);

        final String display = parent.getString(WINDOW_DISPLAY);

        final SearchFieldUser isSearchFieldUser = SearchFieldUser.TRUE; // always
        final boolean hasMultiResult = TRUE.equals(parent.getString(HAS_MULTIDISPLAY));
        final String minimizedButtonName = parent.getString(MIN_MENU_NAME_ON_TASKBAR);
        final boolean isDisablingTime = TRUE.equals(parent.getString(DISABLE_TIME));

        // can be pretty specific - know its Recurring event summary
        return new MetaMenuItemDataType.Builder()
                .text(name)
                .id(id)
                .url(wsURL)
                .style(style)
                .isSearchFieldUser(isSearchFieldUser)
                .windowType(Type.GRID)
                .display(display)
                .multiResult(hasMultiResult)
                .toolBarHandler(
                        new ToolBarStateManager(toolBarType, ToolBarStateManager.BottomToolbarType.PAGING, EMPTY_STRING))
                .minimizedButtonName(minimizedButtonName).isDisablingTime(isDisablingTime).build();
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getWizardMetaMenuItemDataType(java.lang.String)
    */
    @Override
    public WizardInfoDataType getWizardMetaMenuItemDataType(final String wizardID) {
        final IJSONArray wizardItems = metaData.getArray(WIZARD_SECTION);
        // get handle to specific wizard
        if (wizardItems != null) {

            for (int x = 0; x < wizardItems.size(); x++) {
                final IJSONObject objWizard = wizardItems.get(x);

                if (objWizard.getString(WIZARD_ID).equalsIgnoreCase(wizardID)) {

                    final String wsURL = getCompletedURL(objWizard.getString(URL));

                    // Allowing this load URL to be empty for fixed data case
                    final String metaLoadURL = objWizard.getString(WIZARD_URL);
                    final String loadURL = metaLoadURL.isEmpty() ? EMPTY_STRING : getCompletedURL(metaLoadURL);
                    final String wizardContentStyle = objWizard.getString(WIZARD_CONTENT_STYLE);
                    return new WizardInfoDataType(wizardID, loadURL, wsURL, wizardContentStyle);

                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getRecurringErrSummaryWebServiceURL(com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType)
    */
    @Override
    public String getRecurringErrSummaryWebServiceURL(final EventType eventID) {
        final IJSONArray recurErrURLItem;
        switch (eventID) {
        case RECUR_ERR_SUBSCRIBER:

            recurErrURLItem = getExtraMenuItemArray(RECUR_ERR_MENU_ITEM_SUBSCRIBER);

            break;
        case RECUR_ERR_NETWORK:

            recurErrURLItem = getExtraMenuItemArray(RECUR_ERR_MENU_ITEM_NETWORK);
            break;

        default:
            recurErrURLItem = null;
            break;
        }
        if (recurErrURLItem != null) {
            final IJSONObject parent = recurErrURLItem.get(0);
            return getCompletedURL(parent.getString(URL));
        }

        return EMPTY_STRING;
    }

    /*
    * Moving all "spare" meta menu items (not those in tabMenuItems sections into
    * there own tag - (primaraly so that can define meta menu items for launch
    * windows as being different from those in main meta menu items)
    *
    * Search all EXTRA_MENU_ITEMS sections inside all meta datas
    *
    * @param id MetaMenu Item id inside EXTRA_MENU_ITEMS section
    *
    * @return result of search (or recursive errors if screwed up meta data)
    */
    private IJSONArray getExtraMenuItemArray(final String id) {
        final List<String> checkedMetaDatas = new ArrayList<String>();
        final IJSONArray extraMenuItems = metaData.getArray(EXTRA_MENU_ITEMS);
        return getExtraMenuItemArray(id, extraMenuItems, checkedMetaDatas);
    }

    private IJSONArray getExtraMenuItemArray(final String menuItemId, IJSONArray extraMenuItems,
            final List<String> checkedMetaDatas) {

        getExtraMenuItemRecursiveCount++;

        if (getExtraMenuItemRecursiveCount > maxRecursiveCount) {
            // ok to find blank dno't recurse
            getExtraMenuItemRecursiveCount = 0; // reset
            return null;
        }

        for (int i = 0; i < extraMenuItems.size(); i++) {
            final IJSONObject extraMenuparent = extraMenuItems.get(i);

            IJSONArray metaMenuItem = extraMenuparent.getArray(menuItemId);

            if (metaMenuItem == null || metaMenuItem.size() == 0) {
                // Hack to get the correct menu item, as there are some cases when menuItemId is looked up and there are cases when id, that both might be different
                final Set<String> keys = extraMenuparent.keySet();
                for (final String key : keys) {
                    final IJSONArray extraMenuparentArray = extraMenuparent.getArray(key);
                    if (extraMenuparentArray.size() > 0
                            && extraMenuparentArray.get(0).getString("id").equals(menuItemId)) {
                        metaMenuItem = extraMenuparentArray;
                        break;
                    }
                }
            }

            if (metaMenuItem != null && metaMenuItem.size() != 0) { // found
                getExtraMenuItemRecursiveCount = 0; // reset
                return metaMenuItem;
            }

        }
        // still here - check other meta datas
        extraMenuItems = metaData.alternativeMetaData_getArray(EXTRA_MENU_ITEMS, checkedMetaDatas);
        return getExtraMenuItemArray(menuItemId, extraMenuItems, checkedMetaDatas);

    }

    /**
     * Return Recur Error query parameters and headers of interest Map pairing
     * column header name and query parameter name <Sub Cause Code Value,
     * subCauseCode>,
     * <p/>
     * i.e. colleciton of column headers of interest (for say button enabling)
     * would be #getRecurErrHeadersParameters().keySet();
     *
     * @param includeOptional - (late hack) true to include columns identified as "optional" in
     *                        JsonObjectWrapper, i.e. columns which may not actually be on current grid
     *
     * @return Map pairing column header name and query parameter name e.g
     *         <"Sub Cause Code Value", "subCauseCode">
     */
    @Override
    public Map<String, String> getRecurErrHeadersParameters(final boolean includeOptional) {

        final Map<String, String> headerQueryParms = new HashMap<String, String>();

        final IJSONArray recurErrItems = getExtraMenuItemArray(RECUR_ERR_MENU_ITEM);
        final IJSONObject parent = recurErrItems.get(0);
        // not really a drill down - but gathering parameters from the grid row from
        // button press

        final IJSONArray arrParams = parent.getArray(DRILL_PARAMETER_SECTION);

        if (arrParams != null) {

            for (int x = 0; x < arrParams.size(); x++) {
                final IJSONObject objParameter = arrParams.get(x);

                // mostly false
                final boolean isOptionalHeader = TRUE.equals(objParameter.getString(OPTIONAL));

                if ((isOptionalHeader && (!includeOptional))) {
                    continue;
                }

                final String columnHeader = objParameter.getString(DRILL_PARAMETER_VALUE);

                final String queryParameter = objParameter.getString(DRILL_PARAMETER_NAME);

                headerQueryParms.put(columnHeader, queryParameter);

            }
        }

        return headerQueryParms;
    }

    /*
    * More generic Meta Menu Item build - requiring full details in section. Not
    * all of this EXTRA_MENU_ITEMS will completely suit converting to a meta menu
    * item type but obviously won't be calling this method when know its not
    * suitable
    *
    * @param menuItemId tag in EXTRA_MENU_ITEMS for menu item
    *
    * @return built meta menu item
    */
    private MetaMenuItemDataType getMetaMenuItemFromExtraMenuItemsSection(final String menuItemId) {

        final IJSONArray extraMenu = getExtraMenuItemArray(menuItemId);
        if (extraMenu != null && extraMenu.size() > 0) {
            final IJSONObject parent = extraMenu.get(0);
            final String menuItemName = parent.getString(NAME);

            // Added to support using standard metaMenuItem method for
            // extra menu items grids as well (the id of the "grid" to launch is not the button id used to launch it)
            final String id = parent.getString(ID);
            return buildMenuItemFromObject(id, menuItemName, parent);
        }
        return null;

    }

    /**
     * Build up DashBoard Information for tab (if exists)
     *
     * @param tabId tab id defined in meta data (tabowner)
     *
     * @return Dashboard data built from json for tab or null if no daashboard
     */
    @Override
    @SuppressWarnings("PMD")
    public DashBoardDataType getDashBoardData(final String tabId, final String WCDMAOrLTE) {
        LOGGER.fine("getDashBoardData");
        LOGGER.finer("tabId=" + tabId);
        IJSONArray dashboardItems;
        if (WCDMAOrLTE.equals(DASHBOARDS)) {
            dashboardItems = metaData.getArray(DASHBOARDS);
        } else {
            dashboardItems = metaData.getArray(DASHBOARDS_LTE);
        }

        for (int i = 0; i < dashboardItems.size(); i++) {
            final IJSONObject parent = dashboardItems.get(i);

            final String tabOwner = parent.getString(TAB_OWNER);
            if (tabOwner.equals(tabId)) {
                final String winId = parent.getString(ID);
                final String title = parent.getString(NAME);
                final String maxDaysBack = parent.getString(MAX_DAYS_BACK);

                final List<PortletDataType> portals = new ArrayList<PortletDataType>();

                final IJSONArray items = parent.getArray(ITEMS);
                for (int k = 0; k < items.size(); k++) {
                    final IJSONObject itemParent = items.get(k);

                    final String portalId = itemParent.getString(ID);
                    final String portalName = itemParent.getString(NAME);

                    final String height = itemParent.getString(HEIGHT);
                    final String url = itemParent.getString(URL);

                    final SearchFieldUser isSearchFieldUser = isSearchFieldUser(itemParent);

                    final String displayType = itemParent.getString(WINDOW_DISPLAY); // line, bar, etc

                    final String commaSeperatedDateFrom = itemParent.getString(DATE_FROM);

                    final String portletTypeJSON = itemParent.getString(PORTLET_TYPE);
                    final PortletType portletType = PortletType.fromString(portletTypeJSON);

                    final String commaSeperatedExcludedSearchTypes = itemParent.getString(EXCLUDED_SEARCH_TYPES);

                    final ParametersDataType parameters = MetaDataParserUtils.parseParameters(itemParent);
                    final List<ThresholdDataType> thresholds = parseThresholds(itemParent);

                    final PortletDataType portalInfo = new PortletDataType(tabId, portalId, portalName, height, url,
                            isSearchFieldUser, displayType, commaSeperatedDateFrom, parameters, portletType,
                            commaSeperatedExcludedSearchTypes);
                    portalInfo.getThresholds().addAll(thresholds);

                    portals.add(portalInfo);

                }

                return new DashBoardDataType(tabId, winId, title, maxDaysBack, portals);
            }

        }
        return null;
    }

    private List<ThresholdDataType> parseThresholds(final IJSONObject object) {
        final List<ThresholdDataType> result = new ArrayList<ThresholdDataType>();
        if (object.containsKey(THRESHOLDS)) {
            final IJSONArray thresholds = object.getArray(THRESHOLDS);
            for (int i = 0; i < thresholds.size(); i++) {
                final IJSONObject thresholdJson = thresholds.get(i);
                final String idJson = thresholdJson.getString(THRESHOLD_ID);
                final String nameJson = thresholdJson.getString(THRESHOLD_NAME);
                final String formatJson = thresholdJson.getString(THRESHOLD_FORMAT);
                final Format format = Format.parse(formatJson);

                Double lowest = null;
                if (thresholdJson.containsKey(THRESHOLD_LOWEST)) {
                    lowest = thresholdJson.getNumber(THRESHOLD_LOWEST);
                }

                Double highest = null;
                if (thresholdJson.containsKey(THRESHOLD_HIGHEST)) {
                    highest = thresholdJson.getNumber(THRESHOLD_HIGHEST);
                }

                final ThresholdDataType threshold = new ThresholdDataType(idJson, format, nameJson, lowest, highest);
                result.add(threshold);
            }
        }
        return result;
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getMenuItems(java.lang.String)
    */
    @Override
    public List<Component> getMenuItems(final String tabId) {

        // future proof some what (i.e. if more than one split button on taskbar)
        final List<Component> taskBarButtons = new ArrayList<Component>();

        final IJSONArray tabMenuItems = metaData.getArray(TAB_MENU_ITEMS);
        for (int i = 0; i < tabMenuItems.size(); i++) {
            final IJSONObject parent = tabMenuItems.get(i);

            final String tabOwner = parent.getString(TAB_OWNER);

            if (tabOwner.equals(tabId)) {

                final IJSONArray taskBarforTabParent = parent.getArray(TASK_BAR_BUTTONS);
                for (int k = 0; k < taskBarforTabParent.size(); k++) {
                    final IJSONObject tParent = taskBarforTabParent.get(k);
                    final String buttonId = tParent.getString(ID);

                    if (BLANK.equals(buttonId)) {
                        taskBarButtons.add(new SpacerComponent(3));
                        continue;
                    }

                    final Component taskBarComponent;

                    // This will return an Empty array if no items
                    final IJSONArray menuItems = tParent.getArray(ITEMS);
                    if (menuItems.size() == 0) {
                        /* deal with other buttons which are not to be menu items */
                        taskBarComponent = createButtonsWithoutMenu(tParent, buttonId);
                    } else {
                        final Button button = new Button(tParent.getString(NAME));
                        button.setId(buttonId);
                        final String style = tParent.getString(STYLE);
                        button.setIconStyle(style);
                        button.addStyleName(style);

                        final Menu menu = new Menu() {
                            @Override
                            public void show(final Element elem, final String pos, final int[] offsets) {
                                offsets[0] = 7;
                                offsets[1] = -3;
                                super.show(elem, pos, offsets);
                            }

                        }; // NOPMD by eeicmsy on 15/07/10 18:17

                        /* Menu items (and sub menu items) for button */
                        addTaskBarMenuItemsToParent(menu, menuItems);

                        if (menu.getItemCount() > 0) { // button may not have menu items
                            button.setMenu(menu);
                        }

                        taskBarComponent = button;
                    }

                    taskBarButtons.add(taskBarComponent);
                }
            }
        }

        return taskBarButtons;
    }

    private Component createButtonsWithoutMenu(final IJSONObject tParent, final String buttonId) {
        if ("CASCADE".equals(buttonId) || ExclusiveTacItem.EXC_TAC_ID.equals(buttonId)
                || "TILE".equals(buttonId)) {
            ImageButton button;

            if ("CASCADE".equals(buttonId)) {
                button = new ImageButton(eniqResourceBundle.cascadeIconToolbar());
                button.setHoverImage(eniqResourceBundle.cascadeIconToolbarHover());
                button.setDisabledImage(eniqResourceBundle.cascadeIconToolbarDisable());
                button.getElement().getStyle().setMarginLeft(5, Unit.PX);
                button.getElement().getStyle().setMarginRight(5, Unit.PX);
                button.getElement().getStyle().setMarginTop(2, Unit.PX);

            } else if (ExclusiveTacItem.EXC_TAC_ID.equals(buttonId)) {
                button = new ImageButton(eniqResourceBundle.exclTacIconToolbar());
                button.setEnabled(true);

            } else {
                button = new ImageButton(eniqResourceBundle.tileIconToolbar());
                button.setHoverImage(eniqResourceBundle.tileIconToolbarHover());
                button.setDisabledImage(eniqResourceBundle.tileIconToolbarDisable());
                button.getElement().getStyle().setMarginLeft(5, Unit.PX);
                button.getElement().getStyle().setMarginRight(5, Unit.PX);
                button.getElement().getStyle().setMarginTop(2, Unit.PX);
            }

            button.setTitle(tParent.getString(TIP));
            button.getElement().setId(buttonId);

            final HorizontalPanel panel = new HorizontalPanel();
            panel.add(button);

            return panel;
        }
        return new Button(tParent.getString(NAME));

    }

    /*
    * Build up MetaMenuItems (containing URL and need for search parameter for
    * restful service etc). These will be menu items for the taskbar which only
    * support one action and one window (i.e. can be used for menu task bar
    * "single" type buttons)
    *
    * Add menu items to main task bar with special note of hardcoded cascade and
    * tile options. Recursive for sub menu items
    *
    * @param menu menu to add items to
    *
    * @para m menuItems menu items to add ("items" in meta data relating to menu
    * items in drop down menu)
    */
    @SuppressWarnings("PMD")
    private void addTaskBarMenuItemsToParent(final Menu menu, final IJSONArray menuItems) {

        for (int j = 0; j < menuItems.size(); j++) {
            final IJSONObject menuParent = menuItems.get(j);

            final String menuItemId = menuParent.getString(ID);
            // slight hack to ensure won't try to read Id on a separator (as not one
            // in JSON)
            final boolean isSeparator = (SEPARATOR.equals(menuItemId));

            final String menuItemName = (isSeparator) ? EMPTY_STRING : menuParent.getString(NAME);

            if (isSeparator) {
                // no enabled to read (adding a name to json to keep code sweet)
                menu.add(new SeparatorMenuItem());
            } else { // leave more types until have em

                final boolean isEnabled = TRUE.equalsIgnoreCase(menuParent.getString(ISENABLED));

                final MenuItem menuItem = new MetaMenuItem(
                        buildMenuItemFromObject(menuItemId, menuItemName, menuParent)); // NOPMD
                // (eemecoy
                // 1/6/10,
                // necessary
                // evil)

                // TODO added style in param (for window button) -- might as well add
                // enabled ?
                menuItem.setEnabled(isEnabled);
                menu.add(menuItem);

                // Sub menus possible
                final IJSONArray subMenuItems = menuParent.getArray(ITEMS);
                if (subMenuItems.size() != 0) {
                    final Menu sub = new Menu(); // NOPMD (eemecoy 1/6/10, necessary evil)
                    /* Recursive call for sub menu items */
                    addTaskBarMenuItemsToParent(sub, subMenuItems); // recursive call
                    menuItem.setSubMenu(sub);
                }

            }

        }
    }

    /*
    * Extra for search field user, in place of specifing
    *  "needSearchParam":"TRUE" - can also use  "needSearchParam":"PATH",
    *  meaning you want to concat node type onto path address rather than use say,
    *  URL_PATH?time=xx&type=APN  replaced with URL_PATH/APN
    *
    *  @return true if item needs to read read search field data
    */
    private SearchFieldUser isSearchFieldUser(final IJSONObject parent) {
        SearchFieldUser returnVal = SearchFieldUser.FALSE;
        final String searchFieldUserVal = parent.getString(NEED_SEARCH_PARAM);
        if (TRUE.equalsIgnoreCase(searchFieldUserVal)) {
            returnVal = SearchFieldUser.TRUE;
        } else if (PATH.equalsIgnoreCase(searchFieldUserVal)) {
            returnVal = SearchFieldUser.PATH;
        }
        return returnVal;
    }

    /*
    * builds uo the Menu Item Data Type based on the provided JSONObject
    */
    private MetaMenuItemDataType buildMenuItemFromObject(final String menuItemId, final String menuItemName,
            final IJSONObject menuParent) {
        final String url = getCompletedURL(menuParent.getString(URL));
        final SearchFieldUser isSearchFieldUser = isSearchFieldUser(menuParent);

        final boolean isEmptyAndFullSearchFieldUser = TRUE.equalsIgnoreCase(menuParent
                .getString(IS_EMPTY_AND_FULL_SEARCH_FIELD_USER));

        final Type windowType = MetaMenuItemDataType
                .convertType(menuParent.getString(WINDOW_TYPE));
        final String style = menuParent.getString(STYLE);

        final String toolBarType = menuParent.getString(TOOL_BAR_TYPE);

        final String toggleToolBarType = menuParent.getString(TOGGLE_TOOL_BAR_TYPE);

        final String displayType = menuParent.getString(WINDOW_DISPLAY);

        // Get wizardID (If any) and append services URL
        final String wizard = menuParent.getString(WIZARD_ID);

        final String type = menuParent.getString(PARAM_TYPE);
        final String key = menuParent.getString(PARAM_KEY);

        final String maxRowsParam = menuParent.getString(MAX_ROWS_PARAM);

        final boolean isMultiDisplay = TRUE.equalsIgnoreCase(menuParent.getString(HAS_MULTIDISPLAY));

        /*
        * preloaded specific default when launched from menu task bar as opposed to
        * a view menu (e.g. &count=100)
        */
        final String widgetSpecificParams = menuParent.getString(WIDGET_SPECIFIC_PARAMS);

        final ToolBarStateManager.BottomToolbarType bottomToolBar = ToolBarStateManager.convertBottomToolbarType(
                menuParent.getString(BOTTOM_TOOL_BAR_TYPE), windowType);

        // if not set use the default menu name
        final String minimizedButtonName = menuParent.getString(MIN_MENU_NAME_ON_TASKBAR);
        // timeValue should get one of the provided values in the time combo box
        final String timeValue = menuParent.getString(DISABLE_TIME);
        // disableTime is set to true whenever it contains a non-empty string, e.g. legacy "true" or one of the time combo values.
        final boolean isDisablingTime = timeValue.isEmpty() ? false : true;

        final String excludedSearchTypes = menuParent.getString(EXCLUDED_SEARCH_TYPES);

        final boolean isDataTieredDelay = TRUE.equalsIgnoreCase(menuParent.getString(DATA_TIERED_DELAY));


        return new MetaMenuItemDataType.Builder().text(menuItemName).id(menuItemId).url(url).style(style)
                .isSearchFieldUser(isSearchFieldUser).windowType(windowType).display(displayType).type(type).key(key)
                .multiResult(isMultiDisplay).isEmptyAndFullSearchFieldUser(isEmptyAndFullSearchFieldUser)
                .widgetSpecificParams(widgetSpecificParams).excludedSearchTypes(excludedSearchTypes)
                .toolBarHandler(new ToolBarStateManager(toolBarType, bottomToolBar, toggleToolBarType))
                .wizardID(wizard).maxRowsParam(maxRowsParam).minimizedButtonName(minimizedButtonName)
                .isDisablingTime(isDisablingTime).timeValue(timeValue).build();
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getToolBarItems(java.lang.String)
    */
    @Override
    public ToolBarInfoDataType getToolBarItems(final String toolbarOwnerId) {
        final List<String> checkedMetaDatas = new ArrayList<String>();
        // get JSON array of metaData for ToolBars
        final IJSONArray arrToolBars = metaData.getArray(TOOLBARS);
        return getToolBarItems(toolbarOwnerId, arrToolBars, checkedMetaDatas);
    }

    /*
    * If is not good finding "toolbars" as non-empty when it does not contain the
    * toolabr we want, i.e. when it must be in a different meta data
    *
    * Recursive call to find a satisfactory "toolbars" section
    *
    * [Adapted to look up "toolbars" array in multiple metadatas scenario]
    *
    * @see #getToolBarItems
    */
    private ToolBarInfoDataType getToolBarItems(final String toolbarOwnerId, IJSONArray arrToolBars,
            final List<String> checkedMetaDatas) {

        /* initialise the return object */
        final ToolBarInfoDataType objToolBar = new ToolBarInfoDataType();

        boolean isFound = false;
        /* iterate and get a handle to the requested toolbar */
        for (int i = 0; i < arrToolBars.size(); i++) {
            final IJSONObject objToolBarItem = arrToolBars.get(i);
            final String toolBarOwner = objToolBarItem.getString(TOOL_BAR_TYPE);
            /* check if this is the correct ToolBar */
            if (toolBarOwner.equals(toolbarOwnerId)) {
                /* get the collection of panels for this toolBar */
                final IJSONArray arrPanels = objToolBarItem.getArray(PANELS);
                /* iterate this collection to build up each distinct DataType */
                for (int x = 0; x < arrPanels.size(); x++) {
                    /* get handle to this section on buttons */
                    final IJSONObject objPanel = arrPanels.get(x);
                    /* get the array of buttons */
                    final IJSONArray arrButtons = objPanel.getArray(TOOL_BAR_BUTTONS);
                    /* iterate the buttons object to get the properties */
                    for (int y = 0; y < arrButtons.size(); y++) {
                        final IJSONObject objToolBarButton = arrButtons.get(y);
                        final ToolbarPanelInfoDataType toolBarItemInfo = getToolBarPanelInfo(objToolBarButton);
                        /* if last item in the panel need to flag for a separator */
                        toolBarItemInfo.hasSeperator = (y == (arrButtons.size() - 1));
                        objToolBar.toolBarPanels.add(toolBarItemInfo);
                    }
                }
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            // recursive i know its there somewhere
            LOGGER.info("Searching alternative metadata toolBars section for: " + toolbarOwnerId);
            arrToolBars = metaData.alternativeMetaData_getArray(TOOLBARS, checkedMetaDatas);

            if (stoppingRecursionAndWarning(toolbarOwnerId, TOOLBARS, checkedMetaDatas, true)) {
                return null;
            }

            return getToolBarItems(toolbarOwnerId, arrToolBars, checkedMetaDatas);
        }

        return objToolBar;
    }

    @Override
    public MetaMenuItemDataType getMetaMenuItemDataType(final String id) {

        // Check extra menu items section outside of
        // tabMenuItems (shorter check first - LIMIT RECURSION ON THIS MAY NOT FIND)
        MetaMenuItemDataType foundVal = getMetaMenuItemFromExtraMenuItemsSection(id);

        if (foundVal == null) {
            foundVal = getMetaMenuItemDataTypeFromTabMenuItems(id);
        }

        return foundVal; // can still be null

    }

    /* check all meta data for MenuItem Id
    * @param menuItem id to search tab menu items section for
    */
    private MetaMenuItemDataType getMetaMenuItemDataTypeFromTabMenuItems(final String id) {
        final List<String> checkedMetaDatas = new ArrayList<String>();
        final IJSONArray tabMenuItems = metaData.getArray(TAB_MENU_ITEMS);
        return getMetaMenuItemDataType(id, tabMenuItems, checkedMetaDatas);
    }

    private MetaMenuItemDataType getMetaMenuItemDataType(final String id, IJSONArray tabMenuItems,
            final List<String> checkedMetaDatas) {

        MetaMenuItemDataType foundVal = null;
        for (int i = 0; i < tabMenuItems.size(); i++) {

            final IJSONObject parent = tabMenuItems.get(i);
            /* get the collection of buttons for this tab */
            final IJSONArray tabBtns = parent.getArray(TASK_BAR_BUTTONS);

            /* iterate collection of buttons for current tab */
            for (int x = 0; x < tabBtns.size(); x++) {
                final IJSONObject tabStartItems = tabBtns.get(x);

                /* get the items that are detailed under the start button for this tab */
                foundVal = findMetaMenuItemForTaskBarButtonsList(id, tabStartItems);
                if (foundVal != null) {
                    return foundVal;
                }
            }
        }
        // recursive i know its there somewhere
        LOGGER.info("Searching alternative metadata tabMenuItems  section for: " + id);
        tabMenuItems = metaData.alternativeMetaData_getArray(TAB_MENU_ITEMS, checkedMetaDatas);
        if (stoppingRecursionAndWarning(id, TAB_MENU_ITEMS, checkedMetaDatas, false)) {
            return null;
        }

        return getMetaMenuItemDataType(id, tabMenuItems, checkedMetaDatas);

    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getMenuTaskBarMenuItemByID(java.lang.String)
    */
    @Override
    public MetaMenuItem getMetaMenuItemFromID(final String id) {

        final MetaMenuItemDataType foundVal = getMetaMenuItemDataType(id);
        return (foundVal == null) ? null : new MetaMenuItem(foundVal); // can still be null
    }

    /*
    * Recursive search of menu items and submenus items for ID (json items block)
    * Also fetch metaMenuItem for buttons with no menus like EXC_TAC button on
    * Ranking taskbar
    */
    private MetaMenuItemDataType findMetaMenuItemForTaskBarButtonsList(final String id, final IJSONObject itemParent) {

        final IJSONArray menuItems = itemParent.getArray(ITEMS);
        if (menuItems.size() != 0) { // menu item id could be down a sub menu

            for (int k = 0; k < menuItems.size(); k++) {
                final IJSONObject parent = menuItems.get(k);
                final String subMenuItemId = parent.getString(ID);

                /*
                * Recursive call for sub menu items (and potentially their submenu
                * items if had such a thing
                */
                final MetaMenuItemDataType metaItemInSubFolder = findMetaMenuItemForTaskBarButtonsList(id, parent);
                if (metaItemInSubFolder != null) {
                    return metaItemInSubFolder;
                }
                if (subMenuItemId.equalsIgnoreCase(id)) {
                    /* found on plain menu item (not sub menu */
                    final String menuItemName = parent.getString(NAME);
                    return buildMenuItemFromObject(subMenuItemId, menuItemName, parent);
                }
            }

        }
        // for buttons with no menu items
        else if (itemParent.getString(ID).equalsIgnoreCase(id)) {
            return buildMenuItemFromObject(id, itemParent.getString(NAME), itemParent);
        }
        return null;
    }

    /**
     * Populate the ToolbarPanelInfo DataType Object with the values held in the
     * JSON
     *
     * @param objToolBarButton - JSON Object containing the metaData for this ToolBar button
     *
     * @return ToolbarPanelInfo with data that defines the ToolBar button
     */
    private ToolbarPanelInfoDataType getToolBarPanelInfo(final IJSONObject objToolBarButton) {
        final ToolbarPanelInfoDataType panelInfo = new ToolbarPanelInfoDataType();
        /* populate the properties based on the JSON */
        panelInfo.id = objToolBarButton.getString(ID);
        panelInfo.eventID = panelInfo.supportedEventTypes.get(objToolBarButton.getString(EVENTID));

        panelInfo.name = objToolBarButton.getString(NAME);
        panelInfo.style = objToolBarButton.getString(STYLE);
        panelInfo.toolTip = objToolBarButton.getString(TIP);
        panelInfo.disableWhen = objToolBarButton.getString(DISABLE_WHEN);
        panelInfo.visibleWhen = objToolBarButton.getString(VISIBLE_WHEN);
        panelInfo.isImageButton = panelInfo.imageButtonType.contains(panelInfo.id);

        /*
        * default visibility to true always if not present (unless set based on
        * search type above)
        */
        if (CommonConstants.FALSE.equals(objToolBarButton.getString(ISVISIBLE))) {
            panelInfo.isVisible = false;
        }

        panelInfo.isEnabled = TRUE.equals(objToolBarButton.getString(ISENABLED));
        panelInfo.isToggle = TRUE.equals(objToolBarButton.getString(ISTOGGLE));

        /* extras in menu item in toolbar makes server calls */

        final String url = objToolBarButton.getString(URL);
        if (url.length() > 0) {
            panelInfo.urlInfo = new ToolBarURLChangeDataType(getCompletedURL(url),
                    objToolBarButton.getString(WINDOW_DISPLAY), objToolBarButton.getString(WINDOW_TYPE),
                    objToolBarButton.getString(TOOL_BAR_TYPE), objToolBarButton.getString(MAX_ROWS_PARAM));

        }
        final IJSONArray objItems = objToolBarButton.getArray(ITEMS);
        /* determine if this object has subItems - i.e. splitter button - */
        if (objItems != null && objItems.size() > 0) {
            for (int x = 0; x < objItems.size(); x++) {
                final IJSONObject objToolBarSubItem = objItems.get(x);
                /* recursive call to account for subitems */
                final ToolbarPanelInfoDataType subItemInfo = getToolBarPanelInfo(objToolBarSubItem);
                /* add returned subItem DataType to collection */
                panelInfo.subItems.add(subItemInfo);
            }
        }
        return panelInfo;
    }

    /*
    * gets the JSON Data that will be used as the datasource for the Time Combo
    */
    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getTimeComboData()
    */
    @Override
    public String getTimeComboData() {
        final IJSONArray cmbTimeData = metaData.getArray("comboTimeData");
        return cmbTimeData.get(0).getNativeObject().toString();
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getDrillDownWindowType(java.lang.String)
    */
    @Override
    public DrillDownInfoDataType getDrillDownWindowType(final String drillWinTypeID) {
        final List<String> checkedMetaDatas = new ArrayList<String>();
        final IJSONArray arrDrillDownWins = metaData.getArray(DRILL_DOWN_WINDOW_SECTION);
        return getDrillDownWindowType(drillWinTypeID, arrDrillDownWins, checkedMetaDatas);

    }

    /*
    * [Adapted to look up "grids" array in multiple metadatas scenario] get all
    * the properties relating to a specific
    *
    * @param drillWinTypeID
    *
    * @return
    */
    private DrillDownInfoDataType getDrillDownWindowType(final String drillWinTypeID, IJSONArray arrDrillDownWins,
            final List<String> checkedMetaDatas) {

        // DRILL_DOWN_WINDOW_SECTION
        final DrillDownInfoDataType drillInfo = new DrillDownInfoDataType(drillWinTypeID);
        boolean isFound = false;

        for (int i = 0; i < arrDrillDownWins.size(); i++) {
            final IJSONObject objDrillWin = arrDrillDownWins.get(i);
            /* check if this is the correct DrillDown Window */
            if (objDrillWin.getString(ID).equals(drillInfo.id)) {
                drillInfo.name = objDrillWin.getString(NAME);
                drillInfo.url = getCompletedURL(objDrillWin.getString(URL));

                // add two below so can take search data from grid (for KPI button
                // press)
                drillInfo.searchValFromCol = objDrillWin.getString(LAUNCH_WIN_SEARCH_PARAM);
                drillInfo.type = objDrillWin.getString(PARAM_TYPE);

                drillInfo.style = objDrillWin.getString(STYLE);
                drillInfo.displayType = objDrillWin.getString(WINDOW_DISPLAY);
                drillInfo.toolBarType = objDrillWin.getString(TOOL_BAR_TYPE);
                drillInfo.gridDisplayID = objDrillWin.getString(DRILL_DOWN_GRID_DISPLAY);
                drillInfo.isEnabled = TRUE.equalsIgnoreCase(objDrillWin.getString(ISENABLED));
                drillInfo.isDisablingTime = TRUE.equals(objDrillWin.getString(DISABLE_TIME));
                drillInfo.needSearchParameter = isSearchFieldUser(objDrillWin);
                drillInfo.maxRowsParam = objDrillWin.getString(MAX_ROWS_PARAM);

                /* check for parameters that will be provided on this hyperlink */
                final IJSONArray arrParams = objDrillWin.getArray(DRILL_PARAMETER_SECTION);

                if (arrParams != null) {
                    /* Populate the dataType with the column meta */
                    drillInfo.queryParameters = new DrillDownParameterInfoDataType[arrParams.size()]; // NOPMD

                    for (int x = 0; x < arrParams.size(); x++) {
                        final IJSONObject objParameter = arrParams.get(x);
                        drillInfo.queryParameters[x] = new DrillDownParameterInfoDataType(); // NOPMD

                        drillInfo.queryParameters[x].parameterName = objParameter.getString(DRILL_PARAMETER_NAME);
                        drillInfo.queryParameters[x].parameterValue = objParameter.getString(DRILL_PARAMETER_VALUE);
                        drillInfo.queryParameters[x].isFixedType = TRUE.equalsIgnoreCase(objParameter
                                .getString(FIXEDVALUE));
                        drillInfo.queryParameters[x].isTitleParam = TRUE.equalsIgnoreCase(objParameter
                                .getString(TITLEPARAM));

                    }
                }
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            // recursive i know its there somewhere
            LOGGER.info("Searching alternative metadata grids drilldownWindows section for: " + drillWinTypeID);
            arrDrillDownWins = metaData.alternativeMetaData_getArray(DRILL_DOWN_WINDOW_SECTION, checkedMetaDatas);

            if (stoppingRecursionAndWarning(drillWinTypeID, DRILL_DOWN_WINDOW_SECTION, checkedMetaDatas, true)) {
                return null;
            }
            return getDrillDownWindowType(drillWinTypeID, arrDrillDownWins, checkedMetaDatas);
        }

        return drillInfo;
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getChartDrillDownWindowType(java.lang.String)
    */
    @Override
    public ChartDrillDownInfoDataType getChartDrillDownWindowType(final String chartDrillWinTypeID) {
        final List<String> checkedMetaDatas = new ArrayList<String>();
        final IJSONArray arrDrillDownWins = metaData.getArray(CHART_DRILL_DOWN_WINDOW_SECTION);
        return getChartDrillDownWindowType(chartDrillWinTypeID, arrDrillDownWins, checkedMetaDatas);
    }

    /*
    * The main "chartDrillDownWindows" may not be empty, but may not contain the
    * chart drilldown info we are looking for so have to search though other meta
    * data
    *
    * [Adapted to look up "chartDrillDownWindows" array in multiple metadatas
    * scenario]
    *
    * @see #getChartDrillDownWindowType
    */
    private ChartDrillDownInfoDataType getChartDrillDownWindowType(final String chartDrillWinTypeID,
            IJSONArray arrDrillDownWins, final List<String> checkedMetaDatas) {
        final ChartDrillDownInfoDataType drillInfo = new ChartDrillDownInfoDataType(chartDrillWinTypeID);
        boolean isFound = false;

        for (int i = 0; i < arrDrillDownWins.size(); i++) {
            final IJSONObject objDrillWin = arrDrillDownWins.get(i);
            /* check if this is the correct DrillDown Window */
            if (objDrillWin.getString(ID).equals(drillInfo.getChartDrillWinTypeID())) {
                drillInfo.setDrillTargetDisplayId(objDrillWin.getString(DRILL_DOWN_TARGET_DISPLAY_ID));
                drillInfo.setMaxRowsParam(objDrillWin.getString(MAX_ROWS_PARAM));
                drillInfo.setDrillDownWindowName(objDrillWin.getString(DRILL_WINDOW_NAME));
                drillInfo.setDrillToolbarType(objDrillWin.getString(DRILL_TOOLBAR_TYPE));
                // use default ("drilldown=" unless specified otherwise in meta data for
                // chart (eg terminals)
                final String chartDrillDownParam = objDrillWin.getString(CHART_CLICK_URL_PARAM_KEY);
                drillInfo.setChartClickedURLParam((chartDrillDownParam.length() == 0) ? DEFAULT_CHART_DRILL_DOWN_PARAM
                        : (chartDrillDownParam + EQUAL_STRING));

                drillInfo.setDrillType(objDrillWin.getString(DRILL_TYPE));

                drillInfo.setNameForTaskBar(objDrillWin.getString(MIN_MENU_NAME_ON_TASKBAR));
                drillInfo.setWsURL(objDrillWin.getString(URL));
                drillInfo.setWidgetSpecificParams(objDrillWin.getString(WIDGET_SPECIFIC_PARAMS));

                final IJSONArray arrParams = objDrillWin.getArray(DRILL_PARAMETER_SECTION);

                if (arrParams != null) {
                    /* Populate the dataType with the column meta */
                    final ChartDrillDownParameterInfoDataType[] queryParameters = new ChartDrillDownParameterInfoDataType[arrParams
                            .size()]; // NOPMD

                    for (int x = 0; x < arrParams.size(); x++) {
                        final IJSONObject objParameter = arrParams.get(x);
                        final ChartDrillDownParameterInfoDataType chartDrillDownParameterInfoDataType = new ChartDrillDownParameterInfoDataType(
                                objParameter.getString(DRILL_PARAMETER_NAME),
                                objParameter.getString(DRILL_PARAMETER_VALUE),
                                CommonConstants.TRUE.equalsIgnoreCase(objParameter.getString(FIXEDVALUE))); // NOPMD
                        queryParameters[x] = chartDrillDownParameterInfoDataType;
                    }
                    drillInfo.setParameters(queryParameters);
                }

                isFound = true;
                break;
            }
        }
        if (!isFound) {
            // recursive i know its there somewhere
            LOGGER.info("Searching alternative metadata chartDrillDownWindows section for: " + chartDrillWinTypeID);
            arrDrillDownWins = metaData.alternativeMetaData_getArray(CHART_DRILL_DOWN_WINDOW_SECTION, checkedMetaDatas);

            if (stoppingRecursionAndWarning(chartDrillWinTypeID, CHART_DRILL_DOWN_WINDOW_SECTION, checkedMetaDatas,
                    true)) {
                return null;
            }
            return getChartDrillDownWindowType(chartDrillWinTypeID, arrDrillDownWins, checkedMetaDatas);
        }

        return drillInfo;
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getChartConfigInfo(java.lang.String)
    */
    @Override
    public ChartDataType getChartConfigInfo(final String chartID, final boolean isGroup) {
        ChartDataType chartDataType = null;
        /** If in group mode search for the group version of the chart for this chart id. Some charts are different for groups than single nodes.
         * If its not a group or the group chart has not been found (in the case where the chart is the same of single and groups) do the normal search
         */
        if (isGroup) {
            if (chartID != null && !chartID.endsWith("_GROUP")) {
                chartDataType = getChartConfigInfo(chartID + "_GROUP", false);
            } else {
                chartDataType = getChartConfigInfo(chartID, false);
            }
        }
        if (chartDataType == null || !isGroup) {
            chartDataType = getChartConfigInfo(chartID);
        }
        return chartDataType;
    }

    /*
    * If is not good finding "charts" as non-empty when it does not contain the
    * chart we want, i.e. when chart is in a different meta data
    *
    * (chart launched from buttton, e.g. KPI - can be launched from a window not
    * "from" the current meta data)
    *
    * Recursive call to find chart info from "charts" section when can not trust
    * that current metadata in force (e.g. if in voice mode and launch KPI chart
    * on a CS window) will return the correct charts array
    *
    * #getChartConfigInfo [Adapted to look up "charts" array in multiple
    * metadatas scenario]
    */
    @Override
    public ChartDataType getChartConfigInfo(final String chartID) {
        ChartDataType info = null;
        final List<String> checkedMetaDatas = new ArrayList<String>();
        IJSONArray arrChart = metaData.getArray(CHARTS_SECTION);
        do {
            {
                info = searchChartJsonArray(chartID, arrChart);
                if (info != null) {
                    return info;
                }
                LOGGER.info("Searching alternative metadata charts section for  " + chartID);

                /* get the alterntive "charts" section */
                arrChart = metaData.alternativeMetaData_getArray(CHARTS_SECTION, checkedMetaDatas);
            }
        } while (!stoppingRecursionAndWarning(chartID, CHARTS_SECTION, checkedMetaDatas, true));
        return null;
    }

    /**
     * @param chartID
     * @param arrChart
     *
     * @return
     */
    private ChartDataType searchChartJsonArray(final String chartID, final IJSONArray arrChart) {
        for (int i = 0; i < arrChart.size(); i++) {
            final IJSONObject objChart = arrChart.get(i);
            /* check if this is the correct Chart Section */
            final String metaDataChartId = objChart.getString(ID);
            /** If the chart id is found or if the chart id with the group indicator suffix appended is found (where the chart is for a group node). **/
            if (metaDataChartId.equals(chartID)) {
                return MetaDataParserUtils.getChartDataTypeFromJson(objChart);
            }
        }
        return null;
    }

    /*
    * Showing warning up to Operator (us), i.e so we know what meta data to fix before customer sees this
    * Careful when call - i.e. some time we expect result from meta data to be empty - this is only
    * for when looking for a specific grid id or chart id etc
    *
    *
    * @param searchID                - id in meta data being search for
    * @param section                 -  section being searched (e.g. charts)
    * @param checkedMetaDatas        - checked meta data so far  (a non-duplicate list can grow over max licence count for meta datas in force)
    *                                  (see #alternativeMetaData_getArray)
    * @param showWarning             - show a wanring (to our own designers), or false when not appropriate
    * @return    true if stopping recursion
    */
    private boolean stoppingRecursionAndWarning(final String searchID, final String section,
            final List<String> checkedMetaDatas, final boolean showWarning) {

        // empty when one meta data in force. e.g. no licence or not choosen voice yet (ref alternativeMetaData_getArray)
        // (meaning never goign to find it)

        if (checkedMetaDatas.isEmpty() || checkedMetaDatas.size() > maxRecursiveCount) {
            if (showWarning) {
                //we shouldn't have this situation - this need cleaning up, changing for now from dialog to s.o.p
                System.out.println("Meta Data Error: "+ searchID + " not found in " + section + " section ");
            }
            return true;
        }
        return false;
    }

    @Override
    public LaunchWinDataType getLaunchWinFromHyperLink(final String launchWinID) {
        final List<String> checkedMetaDatas = new ArrayList<String>();
        final IJSONArray arrLaunchWins = metaData.getArray(LAUNCH_WINS);
        return getLaunchWinFromHyperLink(launchWinID, arrLaunchWins, checkedMetaDatas);
    }

    private LaunchWinDataType getLaunchWinFromHyperLink(final String launchWinID, IJSONArray arrLaunchWins,
            final List<String> checkedMetaDatas) {

        final LaunchWinDataType launchDataType = new LaunchWinDataType(launchWinID);

        for (int i = 0; i < arrLaunchWins.size(); i++) {
            final IJSONObject objLaunchWin = arrLaunchWins.get(i);

            if (objLaunchWin.getString(LAUNCH_WIN_TYPEID).equals(launchWinID)) {
                launchDataType.menuItem = objLaunchWin.getString(LAUNCH_WIN_MENUITEM);
                launchDataType.searchValFromCol = objLaunchWin.getString(LAUNCH_WIN_SEARCH_PARAM);
                launchDataType.type = objLaunchWin.getString(PARAM_TYPE);
                launchDataType.drillTargetType = DrillTargetType.fromString(objLaunchWin.getString(DRILL_TARGET_TYPE));
                /* check for parameters that will be provided on this hyperlink */
                final IJSONArray arrParams = objLaunchWin.getArray(DRILL_PARAMETER_SECTION);

                if (arrParams != null) {
                    /* Populate the dataType with the column meta */
                    launchDataType.params = new DrillDownParameterInfoDataType[arrParams.size()]; // NOPMD

                    for (int x = 0; x < arrParams.size(); x++) {
                        final IJSONObject objParameter = arrParams.get(x);
                        launchDataType.params[x] = new DrillDownParameterInfoDataType(); // NOPMD

                        launchDataType.params[x].parameterName = objParameter.getString(DRILL_PARAMETER_NAME);
                        launchDataType.params[x].parameterValue = objParameter.getString(DRILL_PARAMETER_VALUE);
                        launchDataType.params[x].isFixedType = TRUE
                                .equalsIgnoreCase(objParameter.getString(FIXEDVALUE));
                        launchDataType.params[x].isTitleParam = TRUE.equalsIgnoreCase(objParameter
                                .getString(TITLEPARAM));

                    }
                }
                return launchDataType; // found
            }
        }

        LOGGER.info("Searching alternative metadata launchWindows section for  " + launchWinID);

        /* get the alterntive "launchWindows" section */
        arrLaunchWins = metaData.alternativeMetaData_getArray(LAUNCH_WINS, checkedMetaDatas);
        if (stoppingRecursionAndWarning(launchWinID, LAUNCH_WINS, checkedMetaDatas, true)) {
            return null;
        }

        return getLaunchWinFromHyperLink(launchWinID, arrLaunchWins, checkedMetaDatas);

    }

    /**
     * Retrieve key, values grid information for a vertical grid (e.g. sub bi
     * details)
     *
     * @param menuItem - reference in UIMetaData
     *
     * @return - information for a grid with vertical columns, such as column
     *         headers and if column header in title
     */
    @Override
    public VerticalGridColumnHeaders getVerticalGridColumnHeaders(final String menuItem) {

        final IJSONArray items = getExtraMenuItemArray(menuItem);

        final IJSONObject parent = items.get(0);
        final String columnHeaderPartOfTitle = parent.getString(COLUMN_HEADER_TITLE);

        final IJSONArray subParent = parent.getArray(JSON_ROOTNODE);
        final IJSONObject columnHeaders = subParent.get(0);
        final String keys[] = new String[columnHeaders.size()];
        for (int k = 0; k < columnHeaders.size(); k++) {
            final int tmp = k + 1;
            final String key = Integer.toString(tmp);
            keys[k] = columnHeaders.getString(key);
        }

        return new VerticalGridColumnHeaders(keys, columnHeaderPartOfTitle);
    }

    @Override
    public List<LicenceGroupTypeDataType> getLicenceGroupTypeDataType() {
        final IJSONArray licenceGroupTypes = metaData.getArray(LICENCE_GROUP_TYPES);

        final List<LicenceGroupTypeDataType> typesList = new ArrayList<LicenceGroupTypeDataType>();
        for (int i = 0; i < licenceGroupTypes.size(); i++) {
            final IJSONObject licenceType = licenceGroupTypes.get(i);
            final String id = licenceType.getString("id");
            final String name = licenceType.getString("name");

            final LicenceGroupTypeDataType type = new LicenceGroupTypeDataType(id, name);

            typesList.add(type);
        }

        return typesList;
    }

    @Override
    public GroupMgmtConfigDataType getGroupManagementConfigData() {
        final IJSONArray groupManagementConfig = metaData.getArray(GROUP_MANAGEMENT_CONFIGURATION);
        final IJSONObject parent = groupManagementConfig.get(0);
        final GroupMgmtConfigDataType groupManagementConfigurationDataType = new GroupMgmtConfigDataType();
        List<LicenseInfoDataType>  licenses = getLicenses();
        groupManagementConfigurationDataType.setGroupConfigurationUrl(parent.getString("groupConfigurationUrl"));
        groupManagementConfigurationDataType.setGroupElementLoadUrl(parent.getString("groupElementLoadUrl"));
        final IJSONArray groupItems = parent.getArray("groupItems");
        for (int i = 0; i < groupItems.size(); i++) {

            final IJSONObject item = groupItems.get(i);
            final IJSONArray groupLicenses = item.getArray("supportedLicenses");
            final List<String> groupLicensesList = getLicensesList(groupLicenses);
            final IJSONArray accessLicenses = item.getArray("accessLicenses");
            final List<String> accessLicensesList = getLicensesList(accessLicenses);
            final String id = item.getString("nodeTypeId");
            final String name = item.getString("nodeTypeDisplayName");
            final String header = item.getString("header");
            final String loadGroupUrl = item.getString("loadGroupURL");
            final String liveloadUrl = item.getString("liveLoadURL");
            final String liveloadRoot = item.getString("liveLoadRoot");
            final IJSONObject wizardObj = item.getObject("wizard");
            final boolean localFiltering = TRUE.equalsIgnoreCase(item.getString("localFiltering"));
            final WizardDataType wizardDataType = getWizardDataType(wizardObj);

            final IJSONArray groupElementKeyNamesArray = item.getArray("groupElementKeyNames");
            final String groupEditUrl = item.getString("groupEditUrl");
            final List<String> groupElementKeyNameList = getGroupElementKeyNames(groupElementKeyNamesArray);

            if(isLicensed(licenses,groupLicensesList) && isLicensed(licenses,accessLicensesList)) {
                final GroupManagementItemDataType itemDataType = new GroupManagementItemDataType(id, name, header,
                        loadGroupUrl, liveloadUrl, liveloadRoot, groupElementKeyNameList, wizardDataType, groupEditUrl,
                        localFiltering);
                groupManagementConfigurationDataType.addGroupManagementItem(itemDataType);
            }
        }
        return groupManagementConfigurationDataType;
    }

   private boolean isLicensed( List<LicenseInfoDataType>  licenses, List<String> supLicense){
       for(LicenseInfoDataType license: licenses){
            if (supLicense.contains(license.getFeatureName())){
                return true;
           }
       }
        return false;
    }

    private WizardDataType getWizardDataType(final IJSONObject wizardObj) {
        if (wizardObj == null || wizardObj.size() == 0) {
            return null;
        }
        final WizardDataType dataType = new WizardDataType();
        dataType.setHeader(wizardObj.getString("header"));
        dataType.setItemSelectURL(wizardObj.getString("itemSelectURL"));
        dataType.setResultRoot(wizardObj.getString("root"));
        dataType.setLocalFiltering(TRUE.equalsIgnoreCase(wizardObj.getString("localFiltering")));
        dataType.setUrlParam(wizardObj.getString("urlParam"));
        final IJSONObject object = wizardObj.getObject("wizard");
        dataType.setWizard(getWizardDataType(object));
        return dataType;
    }

    /**
     * @param groupElementKeyNamesArray
     *
     * @return
     */
    protected List<String> getGroupElementKeyNames(final IJSONArray groupElementKeyNamesArray) {
        final List<String> groupElementKeyNameList = new ArrayList<String>();
        for (int i = 0; i < groupElementKeyNamesArray.size(); i++) {
            final IJSONObject groupElementKeyNameJson = groupElementKeyNamesArray.get(i);
            final String groupElementKeyName = groupElementKeyNameJson.getString("groupElementKeyName");
            groupElementKeyNameList.add(groupElementKeyName);
        }
        return groupElementKeyNameList;
    }

    protected List<String> getLicensesList(final IJSONArray groupLicensesArray) {
        final List<String> groupLicensesList = new ArrayList<String>();
        for (int i = 0; i < groupLicensesArray.size(); i++) {
            final IJSONObject groupLicJson = groupLicensesArray.get(i);
            final String groupLicenseName = groupLicJson.getString("license");
            groupLicensesList.add(groupLicenseName);
        }
        return groupLicensesList;
    }


    @Override
    public KpiPanelType getKpiPanelMetaData() {
        final IJSONArray gridMeta = metaData.getArray(KPIPANEL_SECTION);
        KpiPanelType result = null;
        if (gridMeta.size() > 0 && gridMeta.get(0) != null) {
            final IJSONObject parent = gridMeta.get(0);
            final String id = parent.getString(ID);
            final String name = parent.getString(NAME);
            final String url = parent.getString(URL);
            final String style = parent.getString(STYLE);
            final boolean isEnabled = TRUE.equalsIgnoreCase(parent.getString(ISENABLED));
            final KpiPanelSeverityType[] kpiPanelSeverityTypes = KpiPanelSeverityType.getNotificationSeverities(parent
                    .getArray("notificationSeverities"));
            result = KpiPanelType.createKpiPanelType(id, name, url, style, isEnabled, kpiPanelSeverityTypes);
        } else {
            result = KpiPanelType.createDisabledPanelType();
        }

        return result;
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.IMetaReader#getKPIConfigurationPanelMetaData()
    */
    @Override
    public KPIConfigurationPanelDataType getKPIConfigurationPanelMetaData() {
        final IJSONArray array = metaData.getArray(KPI_CONFIGURATION_PANEL);
        return new KPIConfigurationMetaReader().getKPIConfigurationPanelMetaData(array);

    }

    @Override
    public List<LicenseInfoDataType> getLicenses() {
        final IJSONArray array = metaData.getArray("licenses");

        if (array == null || array.size() == 0) {
            return null;
        }

        final ArrayList<LicenseInfoDataType> licenses = new ArrayList<LicenseInfoDataType>();
        final int count = array.size();
        for (int i = 0; i < count; i++) {
            final IJSONObject license = array.get(i);

          if (license.getString("description").equals(EMPTY_STRING)) {
                // do nothing
           } else {
                licenses.add(new LicenseInfoDataType(license.getString("featureName"), license.getString("description")));
           }
        }
        return licenses;
    }

    @Override
    public Map<String, List<DrillCategoryType>> getDrillManagerData() {
        if (drillManagerDataMap == null) {
            createDrillManagerDataMap();
        }
        return drillManagerDataMap;
    }

    private void createDrillManagerDataMap() {
        final IJSONArray array = metaData.getArray("drillManager");

        drillManagerDataMap = new HashMap<String, List<DrillCategoryType>>();
        for (int i = 0; i < array.size(); i++) {
            final IJSONObject drillConfiguration = array.get(i);
            final String key = drillConfiguration.getString("key");
            final List<DrillCategoryType> categoryList = new ArrayList<DrillCategoryType>();

            final IJSONArray categories = drillConfiguration.getArray("categories");
            for (int j = 0; j < categories.size(); j++) {
                categoryList.add(getCategory(categories.get(j)));
            }
            drillManagerDataMap.put(key, categoryList);
        }
    }

    /**
     * @param category
     *
     * @return
     */
    private DrillCategoryType getCategory(final IJSONObject category) {
        final String categoryId = category.getString("id");
        /** If null, set it to empty String **/
        final String categoryName = WorkspaceUtils.getString(category.getString("name"));
        final IJSONObject criteria = category.getObject("criteria");
        DrillCategoryType.DrillCriteria drillCriteria = null;
        if (criteria != null) {
            drillCriteria = getDrillCriteria(criteria);
        }
        return new DrillCategoryType(categoryId, categoryName, drillCriteria);
    }

    /**
     * @param criteria
     *
     * @return
     */
    private DrillCategoryType.DrillCriteria getDrillCriteria(final IJSONObject criteria) {
        DrillCategoryType.DrillCriteria drillCriteria;
        final DrillCategoryType.DrillCriteriaType criteriaType = DrillCategoryType.DrillCriteriaType.fromString(criteria.getString("type"));
        final IJSONArray seriesMatchers = criteria.getArray("seriesMatchers"); // should never be null so let it break if it is.
        final List<DrillCategoryType.SeriesMatcherType> seriesMatcherList = new ArrayList<DrillCategoryType.SeriesMatcherType>();
        for (int k = 0; k < seriesMatchers.size(); k++) {
            seriesMatcherList.add(getSeriesMatcher(seriesMatchers.get(k)));
        }
        drillCriteria = new DrillCategoryType.DrillCriteria(criteriaType, seriesMatcherList);
        return drillCriteria;
    }

    /**
     * Extracts the seriesId and seriesValue String array from the drillManager.json file
     *
     * @param obj
     *
     * @return
     */
    private DrillCategoryType.SeriesMatcherType getSeriesMatcher(final IJSONObject obj) {
        final String seriesId = obj.getString("seriesId");
        final JSONArray seriesValueJsonArray = obj.getNativeObject().get("seriesValue").isArray();

        final List<String> seriesValues = new ArrayList<String>();

        for (int i = 0; i < seriesValueJsonArray.size(); i++) {
            seriesValues.add(seriesValueJsonArray.get(i).isString().stringValue()); //Need the isString().stringValue, otherwise we get back the string with string quotes around it
        }

        return new DrillCategoryType.SeriesMatcherType(seriesId, seriesValues);
    }

    /**
     * @return SupportedAccessGroups list containing groups that the user has access to
     *
     */
    @Override
    public List<String> getSupportedAccessGroups() {
        final List<String> supportedAccessGroups = new ArrayList<String>();

        final IJSONArray tabs = metaData.getArray(TABS);

        for (int i = 0; i < tabs.size(); i++) {
            final IJSONObject parent = tabs.get(i);

            final boolean isRoleEnabled = TRUE.equals(parent.getString(IS_ROLE_ENABLED));
            if (isRoleEnabled) {
                supportedAccessGroups.add(parent.getString(ID));
            }
        }
        return supportedAccessGroups;
    }
}
