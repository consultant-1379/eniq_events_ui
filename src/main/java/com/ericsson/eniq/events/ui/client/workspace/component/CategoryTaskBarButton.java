package com.ericsson.eniq.events.ui.client.workspace.component;

import com.ericsson.eniq.events.ui.client.common.Constants;
import com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowView;
import com.ericsson.eniq.events.ui.client.workspace.WorkspacePresenter;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

import static com.ericsson.eniq.events.ui.client.common.Constants.SELENIUM_TAG;

/**
 * GXT Menu Button for holding Window Names on Taskbar 
 * @author ecarsea
 */
public class CategoryTaskBarButton extends Button {

    private final String category;
    private final WorkspacePresenter workspacePresenter;

    @Override
    public Menu getMenu() {
        return super.getMenu();    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * @param category
     * @param icon
     */
    public CategoryTaskBarButton(final String category, String icon, WorkspacePresenter workspacePresenter) {
        super(category);
        this.category = category;
        this.workspacePresenter = workspacePresenter;

        setIconStyle(icon);
        setId(SELENIUM_TAG + "MenuTaskBarButton_" + category);
        setMenu(new Menu());
        this.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                /** Menu sits above launcher and above any windows **/
                getMenu().setZIndex(Math.max(Constants.ENIQ_EVENTS_BASE_ZINDEX, XDOM.getTopZIndex()));
            }
        });
    }

    /**
     * Add menu item to a menu for the button if not there previously
     * and step the button text, e.g. to read "Ranking (2)".
     * @param name
     * @param icon
     * @param window
     */
    public void addInstance(final String name, final String icon, final IBaseWindowView window) {
        /** Is it there already **/
        if ((MenuItem) getMenu().getItemByItemId(window.getBaseWindowID()) != null) {
            return;
        }
        final MenuItem item = new MenuItem(name);
        item.setItemId(window.getBaseWindowID());
        item.addSelectionListener(new MenuItemListener(window));
        item.addListener(Events.OnMouseOver, new MenuItemMouseListener(window));
        item.addListener(Events.OnMouseOut, new MenuItemMouseListener(window));
        item.addStyleName(icon);
        getMenu().add(item);
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
            item.removeAllListeners();
            setButtonText();
            return true;
        }
        return false;
    }

    /**
     * Set button text (include number of menu items i.e. windows)
     */
    private void setButtonText() {
        this.setText(category + "(" + this.getMenu().getItemCount() + ")");
    }

    private class MenuItemListener extends SelectionListener<MenuEvent> {
        private final IBaseWindowView window;

        public MenuItemListener(final IBaseWindowView window) {
            this.window = window;
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            window.noticeMe(true);
            window.bringToFront();
            window.noticeMeEnd();
        }
    }
    
        private class MenuItemMouseListener implements Listener<BaseEvent>{

        private final IBaseWindowView window;

        public MenuItemMouseListener(final IBaseWindowView window){
            this.window = window;
        }
        
        @Override
        public void handleEvent(BaseEvent baseEvent) {
            if(baseEvent.getType().equals(Events.OnMouseOver)){
                window.noticeMe(true);
            }
            else if(baseEvent.getType().equals(Events.OnMouseOut)){
                window.noticeMe(false);
            }
        }
    }
}
