/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.service.TabManager;
import com.ericsson.eniq.events.ui.client.common.service.WindowManager;
import com.ericsson.eniq.events.ui.client.common.widget.AbstractBaseWindowDisplay;
import com.ericsson.eniq.events.ui.client.gin.EniqEventsUIGinjector;
import com.ericsson.eniq.events.ui.client.main.GenericTabView;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.extjs.gxt.ui.client.widget.Dialog;

import java.util.*;

/**
 * Class to help out the MenuTaskBar (reduce its size)
 * Generally this class is taking over handling of
 * ownedBaseWindows
 * 
 * @author eeicmsy
 * @since June 2011
 */
public class MenuTaskBarOwnedWindowsHelper {

    /*
     * The Menu Task bar for a tab
     * (the class we are helping out)
     */
    private final MenuTaskBar menuTaskBar;

    /*
     * View owning the menu taskbar
     */
    private final GenericTabView genericTabViewParent;

    /*
     * Keep windows which care about search field changes
     * NOW because introducing a CS and PS switch we don't want a submit search press 
     * in CS taksbar affecting all the PS windows and vice versa (so need to map 
     * search field type (CS, PS_ to owned windows interested in search field
     */
    final Map<String, Set<String>> relatedWindowsForSearchFieldMap = new HashMap<String, Set<String>>();

    /*
     * Keeping track of all opened property windows for this tab
     * (these are dialogs which will not be part of tile and cascade but
     * will always need to be on top after cascade or tile)
     */
    private final List<Dialog> dialogsOnFront = new ArrayList<Dialog>();

    /*
     * Cascade and Tile hanlder
     */
    private final CascadeTileHelper cascadeTileHelper;

	private final WindowManager windowManager;

    /**
     * Construct helper class for MenuTaskBar
     * @param menuTaskBar     the MenuTaskBar (one per tab)
     * @param parentView      the view that owns the taskbar
     */
    public MenuTaskBarOwnedWindowsHelper(final MenuTaskBar menuTaskBar, final GenericTabView parentView) {
        this.menuTaskBar = menuTaskBar;
        this.genericTabViewParent = parentView;
        this.cascadeTileHelper = new CascadeTileHelper(genericTabViewParent);

        final EniqEventsUIGinjector injector = MainEntryPoint.getInjector();
        final TabManager tabManager = injector.getTabManager();
        final String tabId = menuTaskBar.getTabOwnerId();
        this.windowManager = tabManager.getWindowManager(tabId);
    }

    protected Map<String, Set<String>> getRelatedWindowsForSearchFieldMap() {
        return relatedWindowsForSearchFieldMap;
    }

    /**
     * Utility to check if opened "search field user" or if the windowID is in a member of the InstanceTypeWindows (all
     * windows that have "needSearchParam" : TRUE should be here).
     * windows existing in the tab
     *
     * @return  true if tab contains search field user windows
     */
    protected boolean containsOpenSearchFieldUserWindows() {

        if (!windowManager.isThereWindows()) {
            return false;
        }

        for (final BaseWindow win : windowManager.getAllWindows()) {
            if (win instanceof AbstractBaseWindowDisplay) {
                // TODO clumsy  - prefer presenter only access
                final MetaMenuItem item = ((AbstractBaseWindowDisplay) win).getViewSettings();
                if (item != null && (item.isSearchFieldUser())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
    * Note overloading #add for case of launch button.
    * Additions for case of launch buttons to disable menu item selection
    * and track owned windows
    *
    * @param launchButton the item to add
    */
    protected boolean add(final MenuTaskBarButton launchButton) {
        final String id = launchButton.getWindowID();

        launchButton.setItemId(id);
        menuTaskBar.enableMetaMenuItem(id, false);
        menuTaskBar.enableButtonsDependantOnWindowCount();
        return menuTaskBar.callToRealToolBarAdd(launchButton);
    }

    /**
     * Note overloading #remove for case of launch button.
     * @param launchButton  button on taskbar related to window
     * @return boolean from collection contract
     */
    protected boolean remove(final MenuTaskBarButton launchButton) {
        final String id = launchButton.getWindowID();

        menuTaskBar.enableMetaMenuItem(id, true);
        menuTaskBar.enableButtonsDependantOnWindowCount();
        return menuTaskBar.callToRealToolBarRemove(launchButton);
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
    protected void addRelatedWindowsForSearchField(final String menuID, final String metaDataRefKey) {

        Set<String> menuItemIds = relatedWindowsForSearchFieldMap.get(metaDataRefKey);
        if (menuItemIds == null) {
            menuItemIds = new HashSet<String>();
            menuItemIds.add(menuID);
        } else {
            menuItemIds.add(menuID);
        }
        relatedWindowsForSearchFieldMap.put(metaDataRefKey, menuItemIds);
    }

    /**
     * Tile all windows (relying on this taskbar
     * sitting on the center panel)
     *
     * This  method can be called as part of
     * listener on button or listener on a menu item.
     */
    protected void tile() {
        cascadeTileHelper.tile(windowManager.getAllWindows(), dialogsOnFront);
    }

    /**
     * Cascade all windows.
     * This  method can be called as part of
     * listener on button or listener on a menu item.
     */
    protected void cascade() {
        cascadeTileHelper.cascade(windowManager.getAllWindows(), dialogsOnFront);
    }

}
