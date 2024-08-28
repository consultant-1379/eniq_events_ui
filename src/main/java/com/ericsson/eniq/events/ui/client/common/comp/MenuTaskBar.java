/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.common.client.PerformanceUtil;
import com.ericsson.eniq.events.ui.client.charts.PieChartHelper;
import com.ericsson.eniq.events.ui.client.common.ExclusiveTacItem;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.listeners.ExclusiveTacButtonListener;
import com.ericsson.eniq.events.ui.client.common.service.WindowManager;
import com.ericsson.eniq.events.ui.client.common.widget.SpacerComponent;
import com.ericsson.eniq.events.ui.client.dashboard.DashboardModule;
import com.ericsson.eniq.events.ui.client.dashboard.DashboardTimeComponent;
import com.ericsson.eniq.events.ui.client.dashboard.IDashboardTaskbarHelper;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.DashBoardDataType;
import com.ericsson.eniq.events.ui.client.events.GraphDrillDownLaunchEvent;
import com.ericsson.eniq.events.ui.client.events.SearchFieldValueResetEvent;
import com.ericsson.eniq.events.ui.client.events.window.*;
import com.ericsson.eniq.events.ui.client.main.AbstractWindowLauncher;
import com.ericsson.eniq.events.ui.client.main.GenericTabView;
import com.ericsson.eniq.events.ui.client.main.GridLauncher;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.ui.client.search.GroupSingleToggleComponent;
import com.ericsson.eniq.events.ui.client.search.ISearchComponent;
import com.ericsson.eniq.events.ui.client.search.ISubmitSearchHandler;
import com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController;
import com.ericsson.eniq.events.widgets.client.button.ImageButton;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;

import java.util.*;

import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.INPUT;

/**
 * Menu Task bar which is hard-coded to always sit over the center panel of the main tab views (GenericTabView).
 * There will be one of these per tab (each having different menu items and buttons and
 * search-field in most cases (but not always)
 * <p/>
 * This class will control all the windows under it in the tab since owned windows directly
 * map to launch buttons on this taskbar (and menu items used to launch the windows)
 *
 * @author eeicmsy
 * @see {@link com.ericsson.eniq.events.ui.client.main.GenericTabPresenter}
 * @since Feb 2010
 */
@SuppressWarnings("PMD.ExcessiveClassLength")
public class MenuTaskBar extends ToolBar implements ISubmitSearchHandler, IDashboardTaskbarHelper,
        IWorkspaceController, WindowLaunchButtonTitleUpdateEventHandler {

    /*
    * Specific buttons on taskbar (These are the cascade and tile buttons)
    */
    final List<ImageButton> buttonDependantOnWindowCount = new ArrayList<ImageButton>();

    private final IMetaReader metaReader = MainEntryPoint.getInjector().getMetaReader();

    /*
    * Taskbar (on a tab) keeping track of menuitems associated with windows in center panel of tab
    * Access for junit
    */
    final Map<String, MetaMenuItem> metaMenuItems = new HashMap<String, MetaMenuItem>();

    /** Reference between window instances and menu task bars */
    final Map<BaseWindow, MenuTaskBarButton> windowButtons = new HashMap<BaseWindow, MenuTaskBarButton>();

    /*
    * The singlton event bus injection and presenter (MVP) forces us to pass everywhere
    */
    protected EventBus eventBus; // see setter

    /*
    * unique ideintity of tab where this menu bar is sitting
    * (read up from meta data at creation)
    */
    private final String tabId;

    /*
    * identity of the menuItem that will launch
    * by default when the end user provides search criteria
    * and hits enter or search
    */
    private String defaultMenuItemID;

    private ISearchComponent searchComp;

    /* a  GroupTypeSearchComponent (but only using interface) */
    private ISearchComponent groupSelectComp;

    /* some menu bars will have toggle from group to single node search selector */
    private GroupSingleToggleComponent groupSingleToggler;

    /* extracts as null when switch from PS to CS (causing null pointer for submit button press) */
    private final GenericTabView genericTabViewParent;

    /* help out with owned windows */
    private final MenuTaskBarOwnedWindowsHelper ownedWindowsHelper;

    private Point lastOpenedWindowPosition = new Point(0, 0);

    private WindowManager windowManager;

    /* can be one in the tab */
    private String dashBoardWindId = null;

    private DashboardTimeComponent dashboardTimeComponent = null;

    private boolean handlersRegistered;

    /**
     * MenuTask bar build form MetaReader -
     * taking search component and group search components via composition
     * <p/>
     * This class will own all windows so it will handle update for
     * search field selection
     *
     * @param parentView         - view (none null) using the menu taskbar - needed for CS-PS switch over
     * @param tabId              - Identity of the tab where this menu item is sitting. This will be necessary to
     *                           know for the case where we are launching windows out of tabs and want to ensure that
     *                           the same window does not get launched erroneously across two tabs
     * @param searchComp         - can be null if no search component
     * @param groupSelectComp    - can be null if no group search component
     * @param groupSingleToggler - can be null if no group-single toggle component
     * @param defaultMenu        - identity of the menu item id that will be opened user searches but does
     *                           not select a menu option
     */
    public MenuTaskBar(final GenericTabView parentView, final String tabId, final ISearchComponent searchComp,
            final ISearchComponent groupSelectComp, final GroupSingleToggleComponent groupSingleToggler,
            final String defaultMenu) {

        this.searchComp = searchComp;
        this.groupSingleToggler = groupSingleToggler;
        this.groupSelectComp = groupSelectComp;
        this.defaultMenuItemID = defaultMenu;
        this.tabId = tabId;
        this.genericTabViewParent = parentView;
        this.ownedWindowsHelper = new MenuTaskBarOwnedWindowsHelper(this, genericTabViewParent);
    }

    /**
     * Utility for windows to add themselves as direct handlers to
     * search field updated (without being a SearchFieldValueResetEventHandler
     * and getting the full event bus treatment
     * this menu task bar does on all its owned windows)
     * <p/>
     * (used to exclude a "doubling up" scenario)
     *
     * @param submitSearchHandler class cabable of handling search field submit
     */
    @Override
    public void addSubmitSearchHandler(final ISubmitSearchHandler submitSearchHandler) { // NO PMD

        if (searchComp != null) {
            searchComp.addSubmitSearchHandler(submitSearchHandler);
        }
        if (groupSelectComp != null) {
            groupSelectComp.addSubmitSearchHandler(submitSearchHandler);
        }
    }

    @Override
    public void removeSubmitSearchHandler(final ISubmitSearchHandler submitSearchHandler) {
        if (searchComp != null) {
            searchComp.removeSubmitSearchHandler(submitSearchHandler);
        }
        if (groupSelectComp != null) {
            groupSelectComp.removeSubmitSearchHandler(submitSearchHandler);
        }

    }

    public void addSubmitSearchHandler() {
        addSubmitSearchHandler(this);
    }

    public void removeSubmitSearchHandler() {
        removeSubmitSearchHandler(this);
    }

    /**
     * Set event bus (GWT singleton) which will use to fire search field events
     * Over-ride when need to do functionality that needs the event bus to be
     * set
     */
    public void initiateWithEventBus() {
        if (searchComp != null) {
            searchComp.registerWithEventBus(eventBus);
        }
        if (groupSelectComp != null) {
            groupSelectComp.registerWithEventBus(eventBus);
        }
        if (groupSingleToggler != null) {
            groupSingleToggler.initiateWithEventBus(eventBus);
        }
        enableButtonsDependantOnWindowCount(); // all false until windows added (by user or automatically)

        if (!handlersRegistered) { // Handlers should be registered once per Menu instance
            // add handler for eventBus, as new window is been launched want to
            // handle in menTaskbar as the menuTaskbar will own the window

            eventBus.addHandler(GraphDrillDownLaunchEvent.TYPE, new PieChartHelper(this, eventBus));

            // ensure relation between taskBar buttons and active windows
            eventBus.addHandler(WindowOpenedEvent.TYPE, this);
            eventBus.addHandler(WindowClosedEvent.TYPE, this);
            eventBus.addHandler(WindowLaunchButtonTitleUpdateEvent.TYPE, this);
            handlersRegistered = true;
        }
    }

    @Override
    public Point getLastOpenedWindowPosition() {
        return lastOpenedWindowPosition;
    }

    @Override
    public void setLastOpenedWindowPosition(final Point position) {
        lastOpenedWindowPosition = position;
    }

    @Override
    public String getTabOwnerId() {
        return tabId;
    }

    /**
     * Utility to fetch EventBus from taskbar
     * (EventBus is ready after main taskbar up)
     * (also using for junit over-ride)
     *
     * @return evemtBus  the default event bus
     */
    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Get MenuItems for tab
     *
     * @return the metadata menu items in the menu drop down in this taskbar
     *         (e.g. with a view to the caller adding action listeners to them)
     */
    public List<MetaMenuItem> getMetaMenuItems() {
        return new ArrayList<MetaMenuItem>(metaMenuItems.values());
    }

    /**
     * Gets the launched window with the given id
     *
     * @param id The instance window id
     *
     * @return The BaseWindow instance associated with the id or null if no window of that id exists
     */
    @Override
    public BaseWindow getWindow(final String id) {
        return windowManager.getWindow(id);
    }

    /**
     * Utility returning contrain area from menu taskbar
     * as apposed to asking any window
     *
     * @return contrain area
     */
    @Override
    public ContentPanel getCenterPanel() {
        return genericTabViewParent.getCenterPanel();
    }

    /**
     * Utility to check if opened "search field user"
     * windows existing in the tab
     *
     * @return true if tab contains search field user windows
     */
    public boolean containsOpenSearchFieldUserWindows() {
        return ownedWindowsHelper.containsOpenSearchFieldUserWindows();
    }

    /**
     * Note overloading #add for case of launch button.
     * Additions for case of launch buttons to disable menu item selection
     * and track owned windows
     *
     * @param launchButton the item to add
     *
     * @return true/false if really added
     */
    public boolean add(final MenuTaskBarButton launchButton) {
        return ownedWindowsHelper.add(launchButton);

    }

    /* extracted for junit */
    boolean callToRealToolBarAdd(final MenuTaskBarButton launchButton) {
        return super.add(launchButton);
    }

    /**
     * Note overloading #remove for case of launch button.
     *
     * @param launchButton button on taskbar related to window
     *
     * @return boolean from collection contract
     */
    public boolean remove(final MenuTaskBarButton launchButton) {
        return ownedWindowsHelper.remove(launchButton);
    }

    /* extracted for junit */
    boolean callToRealToolBarRemove(final MenuTaskBarButton launchButton) {
        return super.remove(launchButton);
    }

    /* Disable extra non generic menu items which depend on window count.
    * This also includes the cascade and tile buttons on the task bar */
    void enableButtonsDependantOnWindowCount() {

        final boolean isThereWindows = windowManager.isThereWindows();
        //enableMetaMenuItem(CloseTabWindowsMetaMenuItem.CLOSE_ALL_ID, isThereWindows);
        enableMetaMenuItem("CASCADE", isThereWindows);
        enableMetaMenuItem("TILE", isThereWindows);

        for (final ImageButton button : buttonDependantOnWindowCount) {
            button.setEnabled(isThereWindows);
        }
    }

    /**
     * Launch dashboard in entire tab
     *
     * @param dashBoardData      - dashboard data (indicating dashboard is required for tab
     * @param allCurrentLicences - CS, PS,  or just one  (packet swith and circuit switch)
     */
    @Override
    public void launchDashBoard(final DashBoardDataType dashBoardData, final List<String> allCurrentLicences) {

        if (dashBoardData != null) { // extra cautious (will not be calling method without data)
            PerformanceUtil.getSharedInstance().clear("DashboardPresenter");
            final DashboardModule dashboardModule = new DashboardModule();
            //final DashboardPresenter dashboardPresenter = MainEntryPoint.getInjector().getDashboardPresenter();
            //final DashboardView dashboardView = dashboardPresenter.getView();
            dashboardModule.init(this, dashBoardData); // (DashboardTaskbarHelper)
            PerformanceUtil.getSharedInstance().logTimeTaken("Time taken in creating dashboard : ",
                    "DashboardPresenter");

            final ContentPanel containingPanel = getCenterPanel();

            // dashboard window always interested in search field
            for (final String metaKeyRef : allCurrentLicences) {
                addRelatedWindowsForSearchField(dashBoardData.getWinId(), metaKeyRef);
            }

            dashBoardWindId = dashBoardData.getWinId();
            containingPanel.add(dashboardModule.getView());
            containingPanel.layout();
        }
    }

    /**
     * For dashboard - sets reference for time component in taskbar
     * (used when re-open porlets)
     *
     * @param dashBoardWinId - identification for dashoard (one) in this tab
     * @param dashboardData  - extra data for sashboard (like max time allowed back)
     */
    public void addDashboardTimeComponent(final String dashBoardWinId, final DashBoardDataType dashboardData) {
        this.dashboardTimeComponent = new DashboardTimeComponent(getTabOwnerId(), dashBoardWinId, dashboardData,
                eventBus);
        add(dashboardTimeComponent);

    }

    @Override
    public TimeInfoDataType getCurrentDashBoardTimeData() {
        if (dashboardTimeComponent != null) {
            return dashboardTimeComponent.getCurrentDashBoardTimeData();
        }
        return null;
    }

    protected void enableMetaMenuItem(final String id, final boolean enabled) {

        final MetaMenuItem menuItem = metaMenuItems.get(id);
        if (menuItem != null) {
            final SearchFieldDataType searchFieldData = getSearchComponentValue();
            if (searchFieldData != null) {
                if (!isExcludedSearchType(id, searchFieldData)) {
                    menuItem.setEnabled(enabled);
                }
            } else {
                menuItem.setEnabled(enabled);
            }
        }
    }

    /**
     * Adds a Menu item to the tool bar
     * which may have a drop down menu on it
     * e.g start menu (fed from meta data)
     * <p/>
     * These are the menu items (i.e. added before any window
     * is opened).
     *
     * @param component   - the item to add (start button etc)
     * @param metaDataRef - e.g. CS or PS string. Support dual search fields,
     *                    because we only want search field changed to change windows appropriate
     *                    to the metadata (meaning only CS windows react or PS windows react to
     *                    search field changes - not all windows)
     *
     * @return true/false if really added the button
     */
    public boolean addMenuButton(final Component component, final String metaDataRef) {

        if (component instanceof HorizontalPanel) {
            final HorizontalPanel panel = (HorizontalPanel) component;
            final ImageButton button = (ImageButton) panel.getWidget(0);

            final String itemId = button.getElement().getId();
            if ("CASCADE".equals(itemId)) {
                button.addClickHandler(new CascadeListener());
                buttonDependantOnWindowCount.add(button);

            } else if ("TILE".equals(itemId)) {
                button.addClickHandler(new TileListener());
                buttonDependantOnWindowCount.add(button);

            } else if (ExclusiveTacItem.EXC_TAC_ID.equals(itemId)) {
                button.addClickHandler(new ExclusiveTacButtonListener(eventBus, tabId, itemId));
            }

        } else if (component instanceof Button) {
            final Button button = (Button) component;

            final Menu menu = button.getMenu();

            if (menu != null) { // button has menu items (i.e. a split button)

                menu.setShadow(false);
                saveMetaMenuItemsFromMenu(menu, metaDataRef);
            }
        }

        return callToRealAddButton(component);
    }

    /* recursively add owned meta menu items (i.e.
    * to include sub menus owned by menus in MetaMenuItem stroage)
    */
    private void saveMetaMenuItemsFromMenu(final Menu menu, final String metaDataRef) {

        final int menuItemCount = menu.getItemCount();

        for (int i = 0; i < menuItemCount; i++) {
            // could pass in a type in json for seperator (but trying to hold off on a new generic button type for now)
            final Component item = menu.getItem(i);

            if ((item instanceof MetaMenuItem)) { // not the seperator
                final MetaMenuItem menuItem = (MetaMenuItem) item;
                metaMenuItems.put(menuItem.getId(), menuItem);

                if (menuItem.isSearchFieldUser()) {
                    addRelatedWindowsForSearchField(menuItem.getID(), metaDataRef); // i.e. menu items in button
                }
                /* recursive */
                final Menu subMenu = menuItem.getSubMenu();
                if (subMenu != null) {
                    saveMetaMenuItemsFromMenu(subMenu, metaDataRef);
                }
            }
        }
    }

    /*
    * Because many be sharing search field in tab (CS and PS) need
    * to split windows interested in search field so PS does not react to CS and vice versa
    *
    * (so basically if just supporting CS and PS, then there are two keys in the map)
    *
    * @param menuID          - id of menu items in button
    * @param metaDataRefKey  - CS or PS (same key add to searchComp)
    */
    void addRelatedWindowsForSearchField(final String menuID, final String metaDataRefKey) {
        ownedWindowsHelper.addRelatedWindowsForSearchField(menuID, metaDataRefKey);
    }

    /* extracted for junit  (lays out horizontal buttons on panel (start, cascade, tile)*/
    boolean callToRealAddButton(final Component button) {
        return super.add(button);
    }

    /* extracted for junit */
    boolean callToRealRemoveButton(final Component button) {
        return super.remove(button);
    }

    // ************************************************
    //
    //   SEARCH FIELD COMPONENT  (including group)
    //
    // ************************************************

    /**
     * Utility to know if in group selection or single ode selection
     * (can not really trust this to call it publicly in case user
     * just changes group type without changing the search data for a window (presses play)
     * <p/>
     * Read #isGroupMode from SearchFieldDataType instead
     *
     * @return true if group selection, false if single selection
     */
    private boolean isGroupMode() {
        return groupSelectComp != null && groupSelectComp.isVisible();
    }

    /**
     * Return value in menu taskbars search field
     * Some MenuTaskBars may not contain any search component
     * (otherwise would have left this method as abstract)
     * <p/>
     * Other task bars may contain a "group" selection as well,
     * but only displaying one at a time so the one currently displayed
     * (search field or group) takes precedence
     *
     * @param id - ignore here. Used for workspaces
     * @return Search field parameter data
     */
    @Override
    public SearchFieldDataType getSearchComponentValue(final String id) {
        // can still be null return
        return (isGroupMode()) ? getSearchComponentValueGroup() : getSearchComponentValueSingle();
    }

    /**
     * Return value in menu taskbars search field
     * Some MenuTaskBars may not contain any search component
     * (otherwise would have left this method as abstract)
     * <p/>
     * Other task bars may contain a "group" selection as well,
     * but only displaying one at a time so the one currently displayed
     * (search field or group) takes precedence
     * @return Search field parameter data
     */
    @Override
    public SearchFieldDataType getSearchComponentValue() {
        // can still be null return
        return (isGroupMode()) ? getSearchComponentValueGroup() : getSearchComponentValueSingle();
    }

    /**
     * Toggle component
     * to switch between group selection component and search field component
     * (used in tabs supporting single and group selection
     * - when group and singles not sharing existing "type" menu item
     * from paired search component instead)
     *
     * @return group-single toggle search component
     */
    public Component getGroupSingleToggleComp() {
        return (groupSingleToggler == null) ? null : groupSingleToggler.getComponent();
    }

    /**
     * Extra "search" component which might be present on the tab which will
     * allow user to choose to select groups instead of using the "individual"
     * search component
     *
     * @return "group" search component
     */
    public Component getGroupSearchComponent() {
        return (groupSelectComp == null) ? null : groupSelectComp.getSearchComponent();
    }

    /**
     * Method to over-ride if have a search component
     * <p/>
     * Return search component for presentation in the task bar
     * Some MenuTaskBars may not contain any search component
     *
     * @return search component for placement on tab (or next to) or NULL if
     *         no search field to display
     *
     * @see {@link com.ericsson.eniq.events.ui.client.main.GenericTabView}
     */
    @Override
    public Component getSearchComponent() {
        return (searchComp == null) ? null : searchComp.getSearchComponent();
    }

    ////////////////
    // Implement ISubmitSearchHandler
    ////////////////
    @Override
    public void submitSearchFieldInfo() {

        final SearchFieldDataType val = getSearchComponentValue();
        boolean sameModeWindowsOpen = false;
        if (val != null) {

            final List<String> metaDataKeys = this.getMetaDataKeysRelatedToSearchField(val);

            /* we can only launch the default window (event analsis) from play button if no other windows open
            * However, for PS, CS scenario will not be counting PS windows as open windows, such that first
            * play press in CS mode will also launch the default window (CS event analysis)
            *
            * Only react when looping though key for this view
            */
            final String viewMetaDataKey = getSearchComp().getMetaChangeComponentRef();

            for (final String metaDataKey : metaDataKeys) {

                final Set<String> relatedWindowsForSearchField = ownedWindowsHelper
                        .getRelatedWindowsForSearchFieldMap().get(metaDataKey);
                /* update all open windows (or present as launch button)
                * interested in search field in this tab */
                if (relatedWindowsForSearchField != null) {
                    for (final String winId : relatedWindowsForSearchField) {

                        // only one window in the tab (the dashboard)
                        if (dashBoardWindId != null && dashBoardWindId.equals(winId)) { // e.g. "NETWORK_DASHBOARD"

                            getEventBus().fireEvent(new SearchFieldValueResetEvent(getTabOwnerId(), winId, val, "")); // NOPMD by eeicmsy on 21/02/10 14:59
                            return;
                        }

                        final BaseWindow ownedWindow = windowManager.getWindow(winId);

                        if (ownedWindow != null) {

                            if (isExcludedSearchType(ownedWindow.getBaseWindowID(), val)) {
                                continue;
                            }

                            /* the orgional url to use when the submit button is clicked.
                            * required when search field is changed after an already launched windows url is changed due to drill down */
                            final String origonalURL = getOrigionalMenuItemURL(winId);

                            /*at least one window exists - (for this meta data mode)*/
                            if (metaDataKey.equals(viewMetaDataKey)) {
                                sameModeWindowsOpen = true;
                                /* The repainted(updated) window interested in search
                                * must always come in front*/
                                ownedWindow.toFront();
                            }
                            getEventBus().fireEvent(
                                    new SearchFieldValueResetEvent(getTabOwnerId(), winId, val, origonalURL)); // NOPMD by eeicmsy on 21/02/10 14:59
                        }
                    }
                }

            }

            /* should the default menu for this tab be launched
            * NOTE this also suits multiple instance mode to ensure that
            * a new Event Analysis window is launched on play press in "multiple mode"
            * (the windows in ownedBaseWindows carry a different id for different instances)*/
            determineIfLaunchDefaultMenu(sameModeWindowsOpen);
        }
    }

    /*
    * This method gets the metaMenuItem corresponding to the windowID. It then checks the
    * metaMenuItem's excluded searchTypes to see if the entered searchFieldDataType is in this list.
    * If it is, then it returns true, otherwise false.
    */
    private boolean isExcludedSearchType(final String windowID, final SearchFieldDataType searchFieldDataType) {
        if (this.metaMenuItems.get(windowID) == null) {
            return false;
        }
        return this.metaMenuItems.get(windowID).isExcludedSearchType(searchFieldDataType.getType());
    }

    private List<String> getMetaDataKeysRelatedToSearchField(final SearchFieldDataType val) {

        // When read (all) metadatas eventually got to read "CS, PS"
        // for this selection type (e.g. controller)
        List<String> metaDatakeys = val.getMetaDataKeys();

        if (metaDatakeys == null || metaDatakeys.isEmpty()) {
            // no specifics defined - only update CS windows (or PS windows) on own
            metaDatakeys = new ArrayList<String>();
            final String metaDataKey = getSearchComp().getMetaChangeComponentRef();
            metaDatakeys.add(metaDataKey);
        }

        return metaDatakeys;
    }

    /*
    * if no interested window exists - (for mode, CS or PS,  we are in)
    * attempt to launch the default menu item (window) for the tab
    */
    private void determineIfLaunchDefaultMenu(final boolean windowsOpen) {
        if (!windowsOpen) {

            //check if the defaultMenuItem is on the excludedSearchParams list for the selected search Component.
            //boolean value = metaMenuItems.get(defaultMenuItemID).getExcludedSearchTypes().contains(getSearchComponentValue().getType());

            /* build the metaMenuItem for the Default Menu Item */
            final MetaMenuItem defaultItem = getDefaultWindowMenuItemById();

            /* Determine if the default menu item is an excluded search type for the selected search type. */
            if (!isDefaultMenuItemExcluded(defaultItem)) {

                if (defaultItem != null) { // default window for this tab exists
                    /*launch the default menu with the search criteria provided by the end user */
                    launchDefaultMenuItem(defaultItem, genericTabViewParent.getCenterPanel());
                }

            }

        }
    }

    /*
    * Determine if the default menu item is an excluded search type for the selected search type.
    */
    private boolean isDefaultMenuItemExcluded(final MetaMenuItem defaultItem) {
        //extra check as the method getSearchComponentValue could return null.
        if (getSearchComponentValue() == null || defaultItem == null) {
            return false;
        }
        return defaultItem.getExcludedSearchTypes().contains(getSearchComponentValue().getType());
    }

    /*
    * check the value in the group directly
    * @return  value from group search field
    */
    private SearchFieldDataType getSearchComponentValueGroup() {
        return (groupSelectComp == null) ? null : groupSelectComp.getSearchComponentValue();
    }

    /*
    * check the value in the single search field direclty
    * @return  value from single search field
    */
    private SearchFieldDataType getSearchComponentValueSingle() {
        return (searchComp == null) ? null : searchComp.getSearchComponentValue();
    }

    /**
     * Really asking if search field component (if has one) contains
     * Input text (implying usually that we do not have a node type to be
     * even attempting to search for grids with in meta data (multi result sets,
     * e.g. fixedId "bla_APN").  Can not always ask a searchfield data directly for
     * type being "INPUT" to support this request because of "perminant" type
     * (TAC) added in terminal component to support live load for terminals
     * (so ask the toggler (pairedSearchComponent and Group component first,
     * since when toggler is present it will have to hide both components when
     * "input" present)
     *
     * @return true if search component in menu taskbar says "Input"
     */
    @Override
    public boolean justSaysInputInSearchFieldNoSelection() {

        if (groupSingleToggler != null) {
            // "TAC" issue for type covered here
            return (!groupSingleToggler.isAnyComponentVisible());
        }
        // IMSI tab
        final SearchFieldDataType searchData = getSearchComponentValue();
        if (searchData != null) {
            return INPUT.equals(searchData.getType());
        }

        return false;
    }

    /**
     * Replace search field component with new information (from new read of meta data)
     *
     * @param searchSelect      - can be null if no search component
     * @param groupSelect       - can be null if no group search component
     * @param groupSingleToggle - can be null if no group-single toggle component
     * @param defaultMenu       - identity of the menu item id that will be opened user searches but does
     *                          not select a menu option
     */
    public void replaceSearchFieldComponents(final ISearchComponent searchSelect, final ISearchComponent groupSelect,
            final GroupSingleToggleComponent groupSingleToggle, final String defaultMenu) {

        // towards new meta data
        removeSubmitSearchHandler();
        this.searchComp = searchSelect;
        this.groupSelectComp = groupSelect;
        this.groupSingleToggler = groupSingleToggle;
        this.defaultMenuItemID = defaultMenu;

        addSubmitSearchHandler();
        initiateWithEventBus();

    }

    /**
     * Add menus to taskbar. Including support for
     * removing menu items which will only be happening in
     * situation where support Meta Data change handling.
     *
     * @param item   component to add or remove
     * @param isShow true to add, false to remove
     *
     * @return @see ToolBar
     */
    public boolean show(final Component item, final boolean isShow) {
        return isShow ? add(item) : remove(item);
    }

    /**
     * Utility for some cosmetic spacing on the toolbar
     * with container reference to support removal.
     * (removal included to support support Meta Data change handling)
     *
     * @param spacer - spacer as button - should be moved to another component
     * @param isAdd  true to add, false to remove
     *
     * @return the same button
     */
    public SpacerComponent showSpacer(SpacerComponent spacer, final boolean isAdd) {
        if (isAdd) {
            spacer = new SpacerComponent(9);
            add(spacer);

            return spacer;
        }
        remove(spacer);
        return null;
    }

    /** Clear cached menu items for taskbar (e.g. on menu item replace) */
    public void clearMenuCache() {
        metaMenuItems.clear();
    }

    /**
     * Taskbars adds components in flow left to right.
     * When need to temporally swap search component and other buttons, we must
     * take existing buttons
     * 1) remove them.
     * 2) Add in new components
     * 3) add them back (on right of new components)
     *
     * @param isAdd        true when using this method to add (3), false when using this method to remove (1)
     * @param buttonsComps null when removing, previous return value from remove when adding
     *
     * @return buttons being (temp) removed
     */
    public List<Button> handleAddRemoveButtonsOnRight(final boolean isAdd, List<Button> buttonsComps) {

        if (isAdd) {
            for (final Button button : buttonsComps) {

                this.enableMetaMenuItem(button.getItemId(), false);
                this.callToRealAddButton(button);
            }

        } else {
            /* Temp removal while replace search fields etc */
            buttonsComps = new ArrayList<Button>();
            final Set<String> allIds = windowManager.getAllOwnedBaseWindowsIds();
            for (final String buttonId : allIds) { // even in multi-instance button some id was assigned
                final Component comp = getItemByItemId(buttonId);

                if (comp instanceof Button) {
                    final Button button = (Button) comp;
                    buttonsComps.add(button);
                    this.callToRealRemoveButton(button);

                }
            }
        }
        return buttonsComps; // null for add - values for remove (so can add again)
    }

    //////////////////////////
    //  getters supporting JsonObjectWrapper Change
    //////////////////////////

    public ISearchComponent getSearchComp() {
        return searchComp;
    }

    public ISearchComponent getGroupSelectComp() {
        return groupSelectComp;
    }

    public GroupSingleToggleComponent getGroupSingleToggler() {
        return groupSingleToggler;
    }

    public String getDefaultMenuItemID() {
        return defaultMenuItemID;
    }

    // ************************************************
    //
    //   CASCADE AND TILE
    //
    // ************************************************

    /**
     * Tile all windows (relying on this taskbar
     * sitting on the center panel)
     * <p/>
     * This  method can be called as part of
     * listener on button or listener on a menu item.
     */
    public void tile() {
        ownedWindowsHelper.tile();
    }

    /**
     * Cascade all windows.
     * This  method can be called as part of
     * listener on button or listener on a menu item.
     */
    public void cascade() {
        ownedWindowsHelper.cascade();
    }


    @Override
    public void onWindowOpened(final WindowOpenedEvent event) {
        final WindowModel model = event.getModel();

        // Only accept events related to this tab
        if (!tabId.equals(model.getTabId())) {
            return;
        }
        final BaseWindow window = event.getWindow();
        final MenuTaskBarButton launchButton = createTaskBarButton(model, window);
        windowButtons.put(window, launchButton);
        add(launchButton);
    }

    @Override
    public void onWindowTitleUpdate(final WindowLaunchButtonTitleUpdateEvent event) {

        // Only accept events related to this tab
        if (!tabId.equals(event.getTabId())) {
            return;
        }
        final BaseWindow window = event.getWindow();
        final MenuTaskBarButton button = windowButtons.get(window);
        if (button != null) {
            button.setText(event.getTitle());
        }

    }

    @Override
    public void onWindowClosed(final WindowClosedEvent event) {
        final WindowModel model = event.getModel();

        // Only accept events related to this tab
        if (!tabId.equals(model.getTabId())) {
            return;
        }

        final BaseWindow window = event.getWindow();
        final MenuTaskBarButton buttonToRemove = windowButtons.remove(window);
        remove(buttonToRemove);
    }

    private MenuTaskBarButton createTaskBarButton(final WindowModel model, final BaseWindow window) {
        final String title = model.getTitle();
        final String icon = model.getIcon();

        final MenuTaskBarButton launchButton = new MenuTaskBarButton(title, window);
        // launch button and window (and menu item) share icon
        if (icon != null && icon.length() > 0) {
            launchButton.setIconStyle(icon);
        }
        return launchButton;
    }

    public void setWindowManager(final WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    private class CascadeListener extends SelectionListener<ButtonEvent> implements ClickHandler {
        @Override
        public void componentSelected(final ButtonEvent ce) {
            cascade();
        }

        @Override
        public void onClick(final ClickEvent event) {
            cascade();
        }
    }

    private class TileListener extends SelectionListener<ButtonEvent> implements ClickHandler {
        @Override
        public void componentSelected(final ButtonEvent ce) {
            tile();
        }

        @Override
        public void onClick(final ClickEvent event) {
            tile();
        }
    }


    /* extracted for junit*/
    MetaMenuItem getDefaultWindowMenuItemById() {
        return metaReader.getMetaMenuItemFromID(defaultMenuItemID);
    }

    /*
    * Get an origional URL  (not one changed by drilldowns)
    * Won't work on say KPI button, only ones supported by MetaReader method
    * (but as KPI not updating for the search field directly - don't care - we hope)
    */
    String getOrigionalMenuItemURL(final String winId) {
        final MetaMenuItem item = metaReader.getMetaMenuItemFromID(winId);
        return (item == null) ? EMPTY_STRING : item.getWsURL();
    }

    /*
    * split for junit
    * create a new instance of AbstractWindow Launcher
    * and invoke the menu item click for the default menu item
    */
    void launchDefaultMenuItem(final MetaMenuItem defaultMnuItem, final ContentPanel holder) {
        final AbstractWindowLauncher launcher = new GridLauncher(defaultMnuItem, getEventBus(), holder, this);
        launcher.launchWindow(false);
    }

    /*
    * only here to supporting unit test
    * @return   direct access to owned window map
    */
    Map<String, BaseWindow> getOwnedBaseWindowsMap() {
        return windowManager.getOwnedBaseWindowsMap();
    }

    /*
    * only here to supporting unit test
    * @return   direct access to owned window map
    */
    Map<String, Set<String>> getRelatedWindowsForSearchFieldMap() {
        return ownedWindowsHelper.getRelatedWindowsForSearchFieldMap();
    }

}
