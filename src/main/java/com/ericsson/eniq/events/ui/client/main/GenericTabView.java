/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.main;

import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.comp.MenuTaskBar;
import com.ericsson.eniq.events.ui.client.common.widget.MetaDataChangeComponent;
import com.ericsson.eniq.events.ui.client.common.widget.SpacerComponent;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.DashBoardDataType;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.ui.client.search.GroupSingleToggleComponent;
import com.ericsson.eniq.events.ui.client.search.ISearchComponent;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import java.util.List;

import static com.ericsson.eniq.events.ui.client.common.Constants.NO_LICENSE_FOUND_TO_DISPLAY_VOICE_DATA;
import static com.ericsson.eniq.events.ui.client.common.Constants.NO_LICENSE_MESSAGE_TITLE;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.DASHBOARDS;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.DASHBOARDS_LTE;

/**
 * Generic tab view in the Model View Presenter (MVP) pattern. There is one of
 * these for each tab.
 *
 * @author eeicmsy
 * @since Feb 2010
 */
public class GenericTabView extends LayoutContainer implements IGenericTabView {

    private static final String BUSINESS_OBJECTS_TAB = "BUSINESS_OBJECTS_TAB";

    private final IMetaReader metaReader = MainEntryPoint.getInjector().getMetaReader();

    private EventBus eventBus;

    /*
    * Menu task bar is present for each tab and holds menu options and
    * appropriate search field. Windows are also minimised to this area
    */
    private MenuTaskBar taskBar;

    private String tabId;

    /*
    * Reference to current menu items in for ce for this tab. So we know that
    * when this is populated w have populated menu items for this tab before
    * (if replacing menu items will have to remove all these first)
    */
    private List<Component> currentButtonList;

    /*
    * Unfortunate addon for Voice-Data - specific components can remove again
    * (so can removing components which are children of their containers)
    */

    private final SeparatorToolItem seperator_1 = new SeparatorToolItem();

    private final SeparatorToolItem seperator_2 = new SeparatorToolItem();

    private final SeparatorToolItem seperator_3 = new SeparatorToolItem();

    private SpacerComponent spacerButton1, spacerButton2, spacerButton3;

    private MetaDataChangeComponent metaDataChangeComponent;

    private Component groupComponent;

    private Component searchComp;

    private Component groupSingleToggleComp;

    /*
    * Center panel is separate as it is the constrain area for floating windows
    * (we go not want the windows to float outside this area (into taskbar))
    */
    private final ContentPanel centerPanel = new ContentPanel();

    /*
    * Information for Dashboard component for tab which may be null.
    * (final in constructor
    * as do not believe will change for Voice/Data toggle)
    */
    private DashBoardDataType dashBoardData;

    private DashBoardDataType dashBoardDataLTE;

    /**
     * Panel which is the constrain area for the tab windows when the full center panel is not being
     * used. *
     */
    private ContentPanel windowContainer;

    private TabItem tabItem;

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * View representing content of each tab
     *
     * @param tabId   ID for tab as defined in meta data Must be consistant across
     *                multiple meta data if replacing a tab's menu item content
     * @param tabItem - container tab item
     */
    @SuppressWarnings("hiding")
    public void init(final String tabId, final TabItem tabItem) {

        setLayout(new RowLayout(Orientation.VERTICAL));

        this.tabId = tabId;
        this.tabItem = tabItem;
        taskBar = metaReader.getMenuTaskBar(this, tabId);

        dashBoardData = metaReader.getDashBoardData(tabId, DASHBOARDS);

        dashBoardDataLTE = metaReader.getDashBoardData(tabId, DASHBOARDS_LTE);

        initTaskBar(); // NOPMD by eeicmsy on 12/10/11 20:13

        if (hasLicenceToDisplayUI()) { // extra

            // We don't need this mega control for this tab
            if (!"SESSION_BROWSER_TAB".equals(tabId) && !"KPI_ANALYSIS_TAB".equals(tabId)) {
                add(taskBar, new RowData(-1, -1));
            }

            showMultiWinCheckBox(true); // mostly true // NOPMD by EEICMSY on 27/09/11 16:45
            taskBar.addSubmitSearchHandler();
        } else {
            // one per tab click (but thats ok)
            MessageDialog.get().show(NO_LICENSE_MESSAGE_TITLE, NO_LICENSE_FOUND_TO_DISPLAY_VOICE_DATA,
                    MessageDialog.DialogType.WARNING);
        }

        centerPanel.addStyleName("tabContentPanel");

        centerPanel.setHeaderVisible(false);
        add(centerPanel, new RowData(-1, -1));

        addStyleName("tabComponent");
        /** The window container panel defaults to the centre panel of the tab **/
        windowContainer = centerPanel;
    }

    private void initTaskBar() {
        addTaskBarItemsFromMetaData(); // NOPMD by eeicmsy on 29/06/11 20:30
        addSearchComponents(true);

        final ISearchComponent searchSelect = taskBar.getSearchComp();
        final ISearchComponent groupSelect = taskBar.getGroupSelectComp();
        setMetaDataRefOnSearchComponents(searchSelect, groupSelect);

        if (isDashBoard()) { // NOPMD by EEICMSY on 30/09/11 17:00

            /*
            * Because we added PS and CS to every search type in the dashboard tab,
            * we must hide the ones not applicable for licence
            */
            showLicencedTypesOnly(searchSelect, groupSelect);

            /* assumes all dashboards will
            * take time component on right on taskbar (no need for dashBoardData for now)*/

            taskBar.add(new FillToolItem());
            taskBar.addDashboardTimeComponent(dashBoardData.getWinId(), dashBoardData);

        }
    }

    /**
     * Handle notification of Meta date change. Meta Data change means replacing
     * menu items and search components for the previously created Generic Tab
     * View with this tab id.
     * <p/>
     * Keeping this view class - i.e. want to keep existing windows open and
     * minimised(buttons) but change the options offered for menu items and
     * search field etc.
     */
    @Override
    public void handleMetaDataUpdate() {

        // if any windows are minimised already on the task bar
        // we need to put them back in same positions after we are done
        // or they will end up on far left

        final List<Button> buttons = taskBar.handleAddRemoveButtonsOnRight(false, null);

        removeTaskBarItems();

        addTaskBarItemsFromMetaData();

        replaceSearchFieldFromMetaData();

        showMultiWinCheckBox(true); // mostly true

        // put open window buttons back to right of search field etc
        taskBar.handleAddRemoveButtonsOnRight(true, buttons);

    }

    /**
     * access for "internal" key used in maps, (should be same as metaDataKey =
     * getSearchComp().getMetaChangeComponentRef(); used in MenuTaskBar)
     *
     * @return Current key in use for current selection in
     *         MetaDataChangeComponent, CS or PS
     * @see com.ericsson.eniq.events.ui.client.common.comp.MenuTaskBar
     */
    public String getCurrentSelectedMetaDataKey() {
        return metaDataChangeComponent.getKey();
    }

    @Override
    public DashBoardDataType getDashBoardData() {
        return dashBoardData;
    }

    @Override
    public DashBoardDataType getDashBoardDataLTE() {
        return dashBoardDataLTE;
    }

    /**
     * Utility (readablity) to see if dashboard is being displayed on this tab
     *
     * @return true if view contains dashboard data
     */
    public boolean isDashBoard() {
        return dashBoardData != null;
    }

    /*
    * Method to check if no licences to display any thing
    *
    * @return false if no licence to display (both PS and CS not present)
    */
    private boolean hasLicenceToDisplayUI() {
        return (metaDataChangeComponent.getLicenceCount() != 0);
    }

    /**
     * Utility to return number of licences (e.g. 2 for Voice and Data) -
     * equivalent to number of licenced meta datas in force
     *
     * @return number of meta data's licences (CS and PS or just 1)
     */
    public int getMetaDataLicenceCount() {
        return metaDataChangeComponent.getLicenceCount();

    }

    /*
    * Add Generic Task Bar items . Launch/Tile/Cascade
    */
    private void addTaskBarItemsFromMetaData() {

        currentButtonList = metaReader.getMenuItems(tabId);

        // defined intially from this call in constructor - can NEVER be null
        metaDataChangeComponent = metaReader.getMetaDataChangeComponent(); // redefined
        final String metaChangeCompRef = getCurrentSelectedMetaDataKey();

        for (final Component menuButton : currentButtonList) {
            taskBar.addMenuButton(menuButton, metaChangeCompRef);
        }

        // after start button (before an existing floating window buttons)
        showMetaDataChangeComponent(true); // tab independent but if putting
        // here will need one per tab
    }

    /*
    * Remove "start" button (and any more available in meta data), etc..
    */
    private void removeTaskBarItems() {
        if (currentButtonList != null) {
            for (final Component menuButton : currentButtonList) {
                taskBar.remove(menuButton);
            }
        }

        taskBar.clearMenuCache();

        showMetaDataChangeComponent(false);
        showMultiWinCheckBox(false);
    }

    /*
    * When recieve notification of meta data change need to reset the menu task
    * bar for this tab so use the new search components
    */
    private void replaceSearchFieldFromMetaData() {

        addSearchComponents(false);

        // read in new componts towards new meta data
        final MenuTaskBar newMetaDataTaskBar = metaReader.getMenuTaskBar(this, tabId);

        final ISearchComponent searchSelect = newMetaDataTaskBar.getSearchComp();
        final ISearchComponent groupSelect = newMetaDataTaskBar.getGroupSelectComp();
        final GroupSingleToggleComponent groupSingleToggler = newMetaDataTaskBar.getGroupSingleToggler();
        final String defaultMenu = newMetaDataTaskBar.getDefaultMenuItemID();

        // keeping current task bar (so have all window etc)

        setMetaDataRefOnSearchComponents(searchSelect, groupSelect);

        taskBar.replaceSearchFieldComponents(searchSelect, groupSelect, groupSingleToggler, defaultMenu);
        addSearchComponents(true);

    }

    private void setMetaDataRefOnSearchComponents(final ISearchComponent... searchComps) { // NOPMD Jenkins build

        final String metaDataRef = getCurrentSelectedMetaDataKey();
        for (final ISearchComponent comp : searchComps) {
            if (comp != null) { // ranking tab
                comp.setMetaChangeComponentRef(metaDataRef);
            }
        }

    }

    /*
    * New approach (for dashboard). Not showing voice-data toggle (metaDataChangeComponent), so want
    * MSC etc NOT to be visible if no CS licence (packet switch, circuit switch)
    *
    * (This will require special meta data where each search component gets defined, e.g. APN is PS,
    * MSC is CS, which is currently only in dashboard tab)
    */
    private void showLicencedTypesOnly(final ISearchComponent... searchComps) { // NOPMD Jenkins build

        final List<String> licencedKeys = metaDataChangeComponent.getLicencedKeys();

        for (final ISearchComponent comp : searchComps) {
            if (comp != null) { // ranking tab
                comp.setLicencedTypesVisibleOnly(licencedKeys);
            }
        }

    }

    @Override
    public final List<String> getAllCurrentLicencesVoiceData() {
        //  metaDataChangeComponent not null defined via contructor
        return metaDataChangeComponent.getLicencedKeys();

    }

    /*
    * Menu Switch (PS to CS) will not be visible at all if only licensed for
    * one
    *
    * NEVER show multiple checkbox in a dashboard tab
    *
    * @param isShow true to show the component, false to remove it
    */
    private void showMetaDataChangeComponent(final boolean isShow) {

        if (!requiresMetaDataUpdate()) { // never show meta data change component when dashboard or business object tab
            return;
        }

        // assume always call add before call remove
        if (metaDataChangeComponent.shouldBeDisplayed()) {
            taskBar.show(seperator_3, isShow);
            taskBar.show(metaDataChangeComponent, isShow);
            spacerButton2 = taskBar.showSpacer(spacerButton2, isShow);

        }
    }

    /*
    * Add (and remove) Multi win checkbox, i.e. need to remove and add again
    * following a task bar item replacement following voice to data toggle
    *
    * NEVER show multiple checkbox in a dashboard tab
    *
    * @param isShow true to show the component, false to remove it
    *               EVER show multiple checkbox in a dashboard tab (call ignored)
    */
    private void showMultiWinCheckBox(final boolean isShow) {

        // never when dashboard, business object tab and session browser
        if (isDashBoard() || BUSINESS_OBJECTS_TAB.equals(tabId) || "SESSION_BROWSER_TAB".equals(tabId)
                || "KPI_ANALYSIS_TAB".equals(tabId)) {
            return;
        }

        spacerButton1 = taskBar.showSpacer(spacerButton1, isShow);

    }

    /*
    * add (or remove) search appropriate search components to task bar,
    * including group component and toggle group components as applicable for
    * tab
    * (we are removing search components because of the complication of the Voice-Data combobox, where
    * by the whole taskbar is getting replaced with new search components
    * (whilst keeping any launch buttons in the taskbar)))
    *
    * @param isShow true to show the component, false to remove it
    */
    private void addSearchComponents(final boolean isShow) {

        taskBar.show(seperator_1, isShow);
        if (isShow) {

            groupComponent = taskBar.getGroupSearchComponent();
            searchComp = taskBar.getSearchComponent();
            groupSingleToggleComp = taskBar.getGroupSingleToggleComp();

        }

        if (groupComponent == null && searchComp == null && groupSingleToggleComp == null) {

            // happy with the one separator (ranking tab)
            return;
        }

        /*
        * order of display of search and group components (switched order so
        * type component in paired search type will always be far left to
        * support when component parts set invisible
        */

        if (groupSingleToggleComp != null) { // some tabs have group to single
            // mode toggle opto
            taskBar.show(groupSingleToggleComp, isShow);
        }
        if (searchComp != null) {
            taskBar.show(searchComp, isShow);
        }
        if (groupComponent != null) {
            taskBar.show(groupComponent, isShow);
        }

        taskBar.show(seperator_2, isShow);
        spacerButton3 = taskBar.showSpacer(spacerButton3, isShow);

    }

    @Override
    public MenuTaskBar getMenuTaskBar() {
        return taskBar;
    }

    @Override
    public ContentPanel getCenterPanel() {
        return centerPanel;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void startProcessing() {
    }

    @Override
    public void stopProcessing() {
    }

    /**
     * Allow the Tab View user to set a different window container, i.e. if we want to add widgets to the
     * center panel that will not contain windows.
     *
     * @param windowContainer
     */
    @Override
    public void setWindowContainer(final ContentPanel windowContainer) {
        this.windowContainer = windowContainer;
    }

    /** @return  */
    @Override
    public ContentPanel getWindowContainer() {
        return windowContainer;
    }

    /** @return the tabItem */
    @Override
    public TabItem getTabItem() {
        return tabItem;
    }

    /**
     * @return
     */
    public boolean requiresMetaDataUpdate() {
        return !(isDashBoard() || BUSINESS_OBJECTS_TAB.equals(tabId) || "SESSION_BROWSER_TAB".equals(tabId) || "KPI_ANALYSIS_TAB".equals(tabId));
    }
}
