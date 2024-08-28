/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowView;
import com.ericsson.eniq.events.ui.client.datatype.TabInfoDataType;
import com.ericsson.eniq.events.ui.client.resources.EniqResourceBundle;
import com.ericsson.eniq.events.ui.client.workspace.component.CategoryTaskBarButton;
import com.ericsson.eniq.events.widgets.client.button.ImageButton;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class WorkspaceView extends BaseView<WorkspacePresenter> {
    private static final int LAUNCH_MENU_MIN_HEIGHT = 500 + 2;/*including top and bottom border*/

    private static final int LAUNCH_MENU_TOP_OFFSET = 82;

    private static final int LAUNCH_MENU_BOTTOM_OFFSET = 116;

    private static WorkspaceViewUiBinder uiBinder = GWT.create(WorkspaceViewUiBinder.class);

    interface WorkspaceViewUiBinder extends UiBinder<Widget, WorkspaceView> {
    }

    @UiField
    AbsolutePanel workspaceContainer;

    @UiField
    ToolBar toolbar;

    @UiField
    ContentPanel windowContainer;

    private ImageButton cascadeButton;

    private ImageButton tileButton;

    private IWindowContainer windowContainerWrapper;

    private HandlerRegistration resizeHandlerRegistration;

    /** Map window categorys to their corresponding buttons **/
    private final Map<String, CategoryTaskBarButton> categoryButtons = new HashMap<String, CategoryTaskBarButton>();

    /** Map a window id to its category and thus to its category Button. required if changing category or title **/
    private final Map<String, String> windowCategoryMap = new HashMap<String, String>();

    @Inject
    public WorkspaceView(final EniqResourceBundle eniqResourceBundle) {
        initWidget(uiBinder.createAndBindUi(this));
        addToolbarButtons(eniqResourceBundle);
        windowContainerWrapper = new IWindowContainer() {

            @Override
            public ContentPanel getWindowContainerPanel() {
                return windowContainer;
            }
        };
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();
        resizeHandlerRegistration = Window.addResizeHandler(resizeHandler);
        resizeHandler.onResize(null);
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onDetach()
     */
    @Override
    protected void onDetach() {
        super.onDetach();
        resizeHandlerRegistration.removeHandler();
    }

    public void addLaunchMenu(final Widget launchMenu) {
        workspaceContainer.add(launchMenu);
    }

    private void addToolbarButtons(final EniqResourceBundle eniqResourceBundle) {
        cascadeButton = new ImageButton(eniqResourceBundle.cascadeIconToolbar());
        cascadeButton.setHoverImage(eniqResourceBundle.cascadeIconToolbarHover());
        cascadeButton.setDisabledImage(eniqResourceBundle.cascadeIconToolbarDisable());
        setMargins(cascadeButton);
        cascadeButton.setTitle("Cascade Windows");
        cascadeButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                getPresenter().cascade();
            }
        });

        tileButton = new ImageButton(eniqResourceBundle.tileIconToolbar());
        tileButton.setHoverImage(eniqResourceBundle.tileIconToolbarHover());
        tileButton.setDisabledImage(eniqResourceBundle.tileIconToolbarDisable());
        setMargins(tileButton);
        tileButton.setTitle("Tile Windows");
        tileButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                getPresenter().tile();
            }
        });

        toolbar.add(createGxtComponent(cascadeButton));
        toolbar.add(createGxtComponent(tileButton));
        setToolBarButtonsEnabled(false);
    }

    /**
     * to allow adding to GXT Toolbar
     * @param button
     * @return
     */
    protected HorizontalPanel createGxtComponent(final ImageButton button) {
        final HorizontalPanel panel = new HorizontalPanel();
        panel.add(button);
        return panel;
    }

    protected void setMargins(final ImageButton button) {
        button.getElement().getStyle().setMarginLeft(5, Unit.PX);
        button.getElement().getStyle().setMarginRight(5, Unit.PX);
        button.getElement().getStyle().setMarginTop(2, Unit.PX);
    }

    /**
     * @return
     */
    public IWindowContainer getWindowContainer() {
        return windowContainerWrapper;
    }

    public void setToolBarButtonsEnabled(final boolean enabled) {
        cascadeButton.setEnabled(enabled);
        tileButton.setEnabled(enabled);
    }

    protected void addCategoryMenuButton(final String category, final String title, final String icon,
            final IBaseWindowView window) {

        CategoryTaskBarButton menuButton = categoryButtons.get(category);

        if (menuButton == null) {
            menuButton = new CategoryTaskBarButton(category, icon, getPresenter());
            categoryButtons.put(category, menuButton);

            menuButton.setItemId(category);

            toolbar.add(menuButton);
        }
        /** Link window id to category button - for changing category (drill down windows) **/
        windowCategoryMap.put(window.getBaseWindowID(), category);
        // add menu item to an existing button
        menuButton.addInstance(title, icon, window);
    }

    /** 
     * For title or category changes after window opening.
     * @param category
     * @param window
     * @param title
     * @param icon
     */
    protected void updateCategoryButton(final String category, final IBaseWindowView window, final String title,
            final String icon) {
        final String currentCategory = windowCategoryMap.get(window.getBaseWindowID());
        final CategoryTaskBarButton menuButton = categoryButtons.get(currentCategory);

        if (menuButton != null) {
            /** Remove and add rather than update, as we need to remove the icon style and can't do that as we do not know the previous style **/
            removeCategoryMenuButton(window);
            addCategoryMenuButton(category, title, icon, window);
        }
    }

    protected void removeCategoryMenuButton(final IBaseWindowView window) {
        final String currentCategory = windowCategoryMap.get(window.getBaseWindowID());
        final CategoryTaskBarButton menuButton = categoryButtons.get(currentCategory);

        if (menuButton != null) {
            menuButton.removeInstance(window.getBaseWindowID()); //assume won't call with type not added
            if (menuButton.getMenu().getItemCount() == 0) {
                categoryButtons.remove(currentCategory);
                toolbar.remove(menuButton);
            }
            windowCategoryMap.remove(window.getBaseWindowID());
        }
    }

    public TabInfoDataType getTabInfo(final String workspaceId, final String workspaceName) {
        return new TabInfoDataType(workspaceId, workspaceName, "", "", "", false, false);
    }

    /**
     * Workspace resize handler used to keep the glass the proper size.
     */
    private final ResizeHandler resizeHandler = new ResizeHandler() {
        @Override
        public void onResize(final ResizeEvent event) {
            final int clientHeight = Window.getClientHeight();
            final int height = clientHeight - (toolbar.getAbsoluteTop() + toolbar.getOffsetHeight());//TODO not working with workspace tab which are not selected
            windowContainer.setHeight(height);
            //this calculation is done based on the launch menu css if css changes this calculation must also change
            final int launchMenuHeight = Math.max((clientHeight - LAUNCH_MENU_TOP_OFFSET - LAUNCH_MENU_BOTTOM_OFFSET),
                    LAUNCH_MENU_MIN_HEIGHT);
            getPresenter().getLaunchMenu().onResize(launchMenuHeight);
        }
    };
}
