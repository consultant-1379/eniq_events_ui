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
package com.ericsson.eniq.events.ui.client.common;

import com.ericsson.eniq.events.common.client.datatype.ChartDataType;
import com.ericsson.eniq.events.common.client.datatype.ChartDrillDownInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.client.common.comp.MenuTaskBar;
import com.ericsson.eniq.events.ui.client.common.widget.MetaDataChangeComponent;
import com.ericsson.eniq.events.ui.client.datatype.*;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.DashBoardDataType;
import com.ericsson.eniq.events.ui.client.datatype.group.GroupMgmtConfigDataType;
import com.ericsson.eniq.events.ui.client.datatype.kpi.KPIConfigurationPanelDataType;
import com.ericsson.eniq.events.ui.client.main.GenericTabView;
import com.ericsson.eniq.events.widgets.client.drill.DrillCategoryType;
import com.extjs.gxt.ui.client.widget.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IMetaReader {

    /**
     * NB: Has to be called to load at UI start up. Theoretically could call this
     * method even with null parameter later once singleton is created. However we
     * will assume it is called (as we call it), and will access class methods
     * statically once we know meta data is loaded (i.e. once MetaDataReadyEvent
     * has been handled)
     */
    void loadMetaData();

    void loadMetaData(final String metaDataPath);

    /*
    * Complete a URL using host and port prefix
    *
    * @param urlEnd String from meta data with partial url
    *
    * @return Completed URL with base location of service
    */
    String getCompletedURL(final String urlEnd);

    /**
     * Fetch number of rows in grid to display in one page
     *
     * @return number of records to display in a grid page
     */
    Integer getGridRowsPerPage();

    /**
     * Return localised String to use for loading message
     *
     * @return String to use with loading indicator (e.g Loading please wait..)
     */
    String getLoadingMessage();

    /**
     * Waht to display when are rendering the data (Slight overkill using services
     * to supply loading message)
     */
    String getLoadingRenderingMessage();

    /**
     * Read up the ranking interval from meta data (so we can configure it to test
     * loading)
     *
     * @return an int specifying timer between refreshes in milliseconds (this
     *         would be a long but the GWT timer wants ints)
     */
    int getRankingTimerInterval();

    /**
     * Read up the max mulitple instance windows allowed per type e.g. 10 windows
     * per instance type allowed
     *
     * @return an int specifying max number of instance windows
     */
    int getMaxInstanceWindowsPerType();

    /**
     * Read up if we have turned ranking autorefresh on or off from metadata (in
     * short term may be off for load testing, but later we may which to release
     * with it set permanently on)
     *
     * @return boolean from meta data indicating if if automated update of ranking
     *         tables is required (otherwise we will not initiate the timer)
     */
    boolean getIsAutoRefreshOn();

    /**
     * MenuTaskBar contains the search field type for the tab defined in meta data
     * (change of plan). Not all MenuTaskBars contain a search field so can
     * instantiate a MenuTaskBar with out a search field (i.e. it is not abstract)
     *
     * @param parentView - view (none null) using the menu taskbar - needed for CS-PS
     *                   switch over
     * @param tabId      - tab id defined in meta data (TabInfoDataType)
     */
    MenuTaskBar getMenuTaskBar(final GenericTabView parentView, final String tabId);

    /**
     * Build data required for KPI Window Ids will be set a runtime, i.e. there
     * will be multiple KPI windows and will be using selected node to
     * distinguish)
     * <p/>
     * When Circuit Switch is involved split the KPI into two buttons (rather than
     * changing node names)
     * <p/>
     * Note we can not trust that a KPI button pressed on a PS window will always
     * be reading the PS meta data by default (user may have switched to CS mode)
     * (so either the parent windows will have to know if they are CS or PS or we
     * use a different name in meta data and our code will find the correct meta
     * data (going for that approach byt adding the intstanceWindowType (bit of
     * hardcoding)
     *
     *
     * @return MetaMenuItemDataType with null winId
     */
    MetaMenuItemDataType getKPIMetaMenuItemDataType();

    MetaMenuItemDataType getKPICSMetaMenuItemDataType();

    /**
     * Meta Menu for Wizard windows specific
     *
     * @return specific Menu Item for Wizard specific windows
     */
    WizardInfoDataType getWizardMetaMenuItemDataType(final String wizardID);

    /**
     * Get URL required for recurring error
     *
     * @param eventID RECUR_ERR_SUBSCRIBER, RECUR_ERR_MENU_ITEM_NETWORK Recur Error
     *                button event Ids (depends on type of grid it will launch
     *
     * @return URL
     */
    String getRecurringErrSummaryWebServiceURL(final EventType eventID);

    /**
     * Build up MetaMenuItems (containing URL and need for search parameter for
     * restful service etc) for each tab. Cater for all buttons on each main menu
     * item taskbar in each tab (At this time it is a Start menu (with menu items)
     * with cascade and time buttons)
     *
     * @param tabId id defined in meta data (TabInfoDataType)
     *
     * @return MenuItems for a given tab to add to taskbar in each tab
     */
    List<Component> getMenuItems(final String tabId);

    /**
     * builds up the buttons for display for a given toolBar
     *
     * @param toolbarOwnerId - id of parent where the toolBar will reside
     *
     * @return - Custom DataType containing the toolbar buttons
     */
    ToolBarInfoDataType getToolBarItems(final String toolbarOwnerId);

    /**
     * NOTE THIS METHOD reads up menu items both from TAB_MENU_ITEMS and also
     * (upgraded) to read up all extra menu items such as KPI etc, from
     * EXTRA_MENU_ITEMS
     * <p/>
     * Search each tabs menu items and sub menu items and builds MetaMenuItem
     * (uncurrupted one) when fines it
     *
     * @param id window id specified in meta data, e.g. SUBSCRIBER_EVENT_ANALYSIS
     *
     * @return new MetaMenuItem direct from cached meta data (unspoilt by actions
     *         on windows)
     */
    MetaMenuItem getMetaMenuItemFromID(final String id);

    /**
     * Fetch MetaMenuItemDataType  (#see getMetaMenuItemFromID)
     * NOTE THIS METHOD reads up menu items both from TAB_MENU_ITEMS and also
     * to read up all extra MetaMenuItemDataTypes from EXTRA_MENU_ITEMS
     *
     * @param id window id specified in meta data
     *
     * @return data type without conversion to MetaMenuItem
     */
    MetaMenuItemDataType getMetaMenuItemDataType(final String id);

    /*
    * gets the JSON Data that will be used as the datasource for the Time Combo
    */
    String getTimeComboData();

    /**
     * read "drilldownWindows" section and return drill down parameters
     *
     * @param drillWinTypeID id
     *
     * @return drilldown parameters
     */
    DrillDownInfoDataType getDrillDownWindowType(final String drillWinTypeID);

    /**
     * Get all the properties relating to a specific chart drill down
     *
     * @param chartDrillWinTypeID - The id of the chart drilldown type to get info about
     *
     * @return ChartDrillDownInfoDataType - A collection of data describing the
     *         chart drilldown
     */
    ChartDrillDownInfoDataType getChartDrillDownWindowType(final String chartDrillWinTypeID);

    /**
     * Get a DatType containing all info needed to configure the chart for
     * rendering to the screen
     *
     * @param chartID - the unique id of the chart
     *
     * @return - dataType will all specific chart info populated
     */
    ChartDataType getChartConfigInfo(final String chartID);

    GridInfoDataType getGridInfo(String gridType);

    MetaMenuItemDataType getRecurErrMetaMenuItemDataType();

    MetaDataChangeComponent getMetaDataChangeComponent();

    List<TabInfoDataType> getTabDataMetaInfo();

    Collection<LiveLoadTypeDataType> getLiveLoadTypes(final String searchTypeJSONResponseText);

    Map<String, String> getRecurErrHeadersParameters(final boolean includeOptional);

    LaunchWinDataType getLaunchWinFromHyperLink(final String launchWinID);

    VerticalGridColumnHeaders getVerticalGridColumnHeaders(final String menuItem);

    List<LicenceGroupTypeDataType> getLicenceGroupTypeDataType();

    GroupMgmtConfigDataType getGroupManagementConfigData();

    KpiPanelType getKpiPanelMetaData();

    KPIConfigurationPanelDataType getKPIConfigurationPanelMetaData();

    /** Licenses of all installed applications */
    List<LicenseInfoDataType> getLicenses();

    DashBoardDataType getDashBoardData(final String tabId, final String WCDMAorLTE);

    ChartDataType getChartConfigInfo(String chartID, boolean isGroup);

    Map<String, List<DrillCategoryType>> getDrillManagerData();

    List<String> getSupportedAccessGroups();
}