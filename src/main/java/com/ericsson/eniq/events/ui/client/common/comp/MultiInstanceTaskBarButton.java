/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Button for menu taskbar supporting multiple window instances
 * for the same action. The button must support a drop down menu
 * to allow user choose window instance to highlight (e.g
 * different instances of same window per node types).
 * 
 * The button label changes to count current number of menu items (windows) held.
 * (differs from regular MenuTaskBarButton on taskbar) 
 * 
 * Sample use is for KPI button placed on main taskbar.
 * 
 * @author eeicmsy
 * @since April 2010
 *
 */
public class MultiInstanceTaskBarButton extends Button {

    private static final String OPEN_BRACKET = " ("; // with space

    private static final String CLOSE_BRACKET = ")";

    /*
     * Map only introduced to ensure no duplicate menu items
     * and hold a reference to listeners added to menu items 
     * so can remove again for memory cleanup
     * (access for junit)
     */
    final Map<String, SelectionListener<MenuEvent>> storedMenuIds = new HashMap<String, SelectionListener<MenuEvent>>();

    /**
     * Add menu item to a menu for the button if not there previously
     * and step the button text, e.g. to read "KPI (2)".
     * 
     * @param id  unique name to appear in drop down  
     *            menu item (e.g. node name)
     */
    public void addInstance(final String id, final BaseWindow winRef) {
        final MenuItem item = new MenuItem(id);
        item.setItemId(id);

        final SelectionListener<MenuEvent> menuItemListener = new InstanceMenuItemListener(winRef);
        item.addSelectionListener(menuItemListener);
        getMenu().add(item); // use #getMenu so can over-ride for test
        storedMenuIds.put(id, menuItemListener);
        setButtonText();
    }

    /**
     * Remove menu item from button if present and 
     * decrement the button text, e.g. to read "KPI (1)"
    *  @param id  unique name appearing in drop down  
     *            menu item (e.g. node name)  
     * @return true if anything is actually removed
     */
    public boolean removeInstance(final String id) {
        final MenuItem item = (MenuItem) getMenu().getItemByItemId(id);
        if (item != null) {
            getMenu().remove(item);
            item.removeSelectionListener(storedMenuIds.get(id));
            storedMenuIds.remove(id);
            setButtonText();
            return true;
        }
        return false;
    }

    /* button label changes to count current number of menu items (windows) held */
    private void setButtonText() {
        this.setText(OPEN_BRACKET + (storedMenuIds.size()) + CLOSE_BRACKET);
    }

    /**
     * Looks like duplicate code from MenuTaskBarButton, but remember we 
     * are not a "one window one button kind of button", we have menuitem per window
     */
    private class InstanceMenuItemListener extends SelectionListener<MenuEvent> {
        private final BaseWindow winRef;

        public InstanceMenuItemListener(final BaseWindow winRef) {
            this.winRef = winRef;
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            winRef.bringToFront();
        }
    }
}
