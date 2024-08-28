/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.main;

import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.ui.client.common.ExclusiveTacItem;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.MenuTaskBar;
import com.ericsson.eniq.events.ui.client.common.listeners.ExclusiveTacButtonListener;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.DashBoardDataType;
import com.ericsson.eniq.events.ui.client.events.SearchFieldTypeChangeEvent;
import com.ericsson.eniq.events.ui.client.events.SearchFieldTypeChangeEventHandler;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import java.util.List;
import java.util.logging.Logger;

/**
 * MVP pattern presenter. We are using meta data to develop the tabs. The is the
 * generic presenter for each tab item defined as tabs are created in the
 * MainPresenter.
 *
 * This class will add menu listeners to all menu items in its tab
 *
 * @see {@link com.ericsson.eniq.events.ui.client.main.MainPresenter}
 * @author eeicmsy
 * @since Feb 2010
 *
 */
public class GenericTabPresenter extends BasePresenter<IGenericTabView> {

    private static final Logger LOGGER = Logger.getLogger(GenericTabPresenter.class.getName());

    private final String tabOwnerId;

    private MenuTaskBar menuTaskBar;

    /*
    * Tile windows action from menu item as opposed to button (non-generic action
     * we know about)
    */
    private final SelectionListener<MenuEvent> tileListener = new TileListener();

    /*
     * Cascade windows action from menu item as opposed to button (non-generic
     * action we know about)
     */
    private final SelectionListener<MenuEvent> cascadeListener = new CascadeListener();

    /**
     * Generic presenter for tab build up from meta data. Have no need to pass in
     * particular tab information
     *
     * @param display
     * @param eventBus
     */
    public GenericTabPresenter(final IGenericTabView display, final EventBus eventBus) {
        super(display, eventBus);
        this.tabOwnerId = display.getTabItem().getItemId();
        bind();

    }

    @Override
    protected void onBind() {
        menuTaskBar = getView().getMenuTaskBar();
        menuTaskBar.initiateWithEventBus();
        addGenericListenersToMenuItems();
        addGenericListeners();
    }

    /* extracted for junit test */
    List<MetaMenuItem> getMetaMenuItems() {
        return menuTaskBar.getMetaMenuItems();
    }

    /**
     * Utility to check if opened "search field user" windows existing in the tab
     *
     * @return true if any open search field user windows exist in the tab
     */
    public boolean containsOpenSearchFieldUserWindows() {
        return menuTaskBar.containsOpenSearchFieldUserWindows();
    }

    /**
     * Launches any dashboard for tab (This needs to be called AFTER the tab is
     * rendered)
     */
    public void checkAndLaunchDashBoard() {
        //        PerformanceUtil.getSharedInstance().clear("checkAndLaunchDashBoard");
        final DashBoardDataType dashBoardData = getView().getDashBoardData();
        final List<String> allLicences = getView().getAllCurrentLicencesVoiceData();
        if (dashBoardData != null) {
            menuTaskBar.launchDashBoard(dashBoardData, allLicences);

        }
        //        PerformanceUtil.getSharedInstance().logTimeTaken("Time taken in launching dashboard tab : ",
        //                "checkAndLaunchDashBoard");
    }

    /**
     * Launch SessionBrowser view
     *
     * @param sessionBrowserView
     */
    public void checkAndLaunchCustomView(final Widget sessionBrowserView) {
        final ContentPanel centerPanel = menuTaskBar.getCenterPanel();
        centerPanel.add(sessionBrowserView);
        centerPanel.layout();
    }

    /**
     *
     */
    public void onMetaDataUpDate() {
        getView().handleMetaDataUpdate();
    }

    /**
     *  Add generic listeners...
     */
    public void addGenericListeners() {
        this.getEventBus().addHandler(SearchFieldTypeChangeEvent.TYPE, new GTPSearchFieldTypeChangeImpl());
    }

    /**
     * Menu items are all meta data driven. The listener on a MetaMenuItem is only
     * there to launch windows (of correct type on request (we will disable the
     * option until window is closed again using the "id" in meta data for the
     * menu item). The real population of windows is to be done via search field
     * when a search field is required.
     */
    public void addGenericListenersToMenuItems() {

        final List<MetaMenuItem> metaMenuItems = getMetaMenuItems();
        final ContentPanel centerPanel = getView().getCenterPanel();

        for (final MetaMenuItem item : metaMenuItems) {

            if (item != null) {
                final String id = item.getID();
                if ("TILE".equals(id)) {
                    item.addSelectionListener(tileListener);
                    continue;
                } else if ("CASCADE".equals(id)) {
                    item.addSelectionListener(cascadeListener);
                    continue;
                }else if (ExclusiveTacItem.EXC_TAC_ID.equals(id)) {
                    item.addSelectionListener(new ExclusiveTacButtonListener(getEventBus(),
                            menuTaskBar.getTabOwnerId(), id));
                    continue;
                } else {
                    addGenericSelectionListener(item, centerPanel);
                }

            } else {
                LOGGER.warning("GenericTabPresenter : Item is null");
            }
        }

    }

    /**
     * Remove all listeners from menu items (to avoid memory leaks) Call this
     * method before replacing menu items
     */
    public void removeAllListeners() {

        final List<MetaMenuItem> metaMenuItems = getMetaMenuItems();

        for (final MetaMenuItem item : metaMenuItems) {

            if (item != null) {
                item.removeAllListeners();

            } else {
                LOGGER.warning("GenericTabPresenter : Item is null");
            }
        }

    }

    /*
     * Open window from menu item selection. Grid and ranking grids can be treated
     * the same as both tables (the item passed to the presenter can be used to
     * register as a ranking grid later) Uses specialised Launchers so that the
     * windows may modify the MetaMenuItem as they see fit without effecting
     * future invocations of the window from the start menu
     */
    private void addGenericSelectionListener(final MetaMenuItem item, final ContentPanel centerPanel) {

        /* display check (pie, grid, etc) as want no listeners on items
         * that own submenus
         * (assumes they would not define a display for those in meta data) */
        if (!item.getDisplay().isEmpty()) {

            switch (item.getWindowType()) {
            case GRID:
            case RANKING:
                // there is risk here with MetaMenuItem state (used over-ride view
                // creation
                // here but removing it with multiple instance windows work)

                item.addSelectionListener(new GridLauncher(item, getEventBus(), centerPanel, menuTaskBar) {

                });
                break;
            case CHART:
                item.addSelectionListener(new ChartLauncher(item, getEventBus(), centerPanel, menuTaskBar) {

                });
                break;
            }
        }
    }

    /*
     * This is a listener for all changes on the SearchType. There are some search types which are not valid
     * for some menu items. The invalid menu items are disabled, based on the excludedSearchField entry
     * in the JsonObjectWrapper.
     */
    /* access for junit */
    class GTPSearchFieldTypeChangeImpl implements SearchFieldTypeChangeEventHandler {

        @Override
        public void handleTypeChanged(final String tabId, final String typeSelected, final boolean isGroup,
                final String typeText) {
            //step through the list of menu items. for each menu item read it's "excludedSearchTypes"
            //if the new search field type is present in this list, then disable the menu item.
            if (!tabOwnerId.equals(tabId)) {
                return;
            }
            final List<MetaMenuItem> metaMenuItems = getMetaMenuItems();
            for (final MetaMenuItem item : metaMenuItems) {
                //Check if menu item should be disabled because of the search type selected...
                if (item.getExcludedSearchTypes().contains(typeSelected)) {
                    item.disable();
                }
            }
        }
    }

    /* access for junit */
    class CascadeListener extends SelectionListener<MenuEvent> {

        @Override
        public void componentSelected(final MenuEvent ce) {
            menuTaskBar.cascade();
        }
    }

    /* access for junit */
    class TileListener extends SelectionListener<MenuEvent> {

        @Override
        public void componentSelected(final MenuEvent ce) {
            menuTaskBar.tile();
        }
    }

}
