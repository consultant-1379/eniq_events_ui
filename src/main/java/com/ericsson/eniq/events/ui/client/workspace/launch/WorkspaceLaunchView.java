/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.workspace.launch;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.workspace.events.GroupPopupChangeEvent;
import com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu.WindowsMenuView;
import com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu.WorkspaceMenuView;
import com.extjs.gxt.ui.client.core.XDOM;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Workspace Launch Menu
 *
 * @author ecarsea
 * @since May 2012
 */
public class WorkspaceLaunchView extends BaseView<WorkspaceLaunchPresenter> {

    private static final int FOOTER_HEIGHT = 21 + 1;

    private static final int HEADER_HEIGHT = 24 + 1;

    private static final int VIEW_PADDING = 10 + 2;

    private static final int TABS_HEIGHT = 27;

    /**
     * Not using 100000 because we have 5000 all other launch menus and need no specific handling in DropDwonTimeComponent
     */
    private static final int LAUNCH_MENU_Z_INDEX = 5000;

    private static final String WINDOWS_TAB_HEADER = "Windows";

    private static final String WORKSPACES_TAB_HEADER = "Workspaces";

    private static final int SLIDE_LEFT_OFFSET = 0;

    private static final String SLIDE_IN_TITLE = "Hide";

    private static final String SLIDE_OUT_TITLE = "Show";

    private static final String SLIDE_IN_PINNED_TITLE = "Launcher is pinned";

    public static final String PIN_UP_ALT_TEXT = "Pin";

    public static final String PIN_DOWN_ALT_TEXT = "Unpin";

    @UiField
    ToggleButton pin;

    @UiField
    Label statusLabel;

    interface WorkspaceLaunchViewUiBinder extends UiBinder<Widget, WorkspaceLaunchView> {
    }

    private static WorkspaceLaunchViewUiBinder uiBinder = GWT.create(WorkspaceLaunchViewUiBinder.class);

    @UiField(provided = true)
    WorkspaceLaunchMenuResourceBundle resourceBundle;

    @UiField
    DecoratedTabPanel tabPanel;

    @UiField
    FlowPanel container;

    @UiField
    Label header;

    @UiField
    Image handle;

    @UiField
    SimplePanel menuContent;

    private boolean isSlideInState;

    private boolean isWindowsTabSelected;

    private WindowsMenuView windowsMenuView;

    private WorkspaceMenuView workspaceMenuView;

    @Inject
    public WorkspaceLaunchView(final WorkspaceLaunchMenuResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.isWindowsTabSelected = true;
        resourceBundle.style().ensureInjected();
        resourceBundle.workspaceLaunchStyle().ensureInjected();
        initWidget(uiBinder.createAndBindUi(this));
        /** Start with slide in **/
        slideIn();
        tabPanel.getDeckPanel().setStylePrimaryName(resourceBundle.workspaceLaunchStyle().tabPanelBottom());
        tabPanel.getTabBar().setStylePrimaryName(resourceBundle.workspaceLaunchStyle().tabBar());
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        onResize(getOffsetHeight());

    }

    /**
    *
    * @param windowsMenuView
    * @param workspaceMenuView
    */
    public void init(final WindowsMenuView windowsMenuView, final WorkspaceMenuView workspaceMenuView) {
        this.windowsMenuView = windowsMenuView;
        this.workspaceMenuView = workspaceMenuView;
        tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(final SelectionEvent<Integer> selectionEvent) {
                isWindowsTabSelected = tabPanel.getWidget(selectionEvent.getSelectedItem()).equals(windowsMenuView) ? true
                        : false;
                getPresenter().getEventBus().fireEvent(
                        new GroupPopupChangeEvent(getPresenter().getWorkspaceId(), isWindowsTabSelected));
            }
        });
        tabPanel.add(windowsMenuView, WINDOWS_TAB_HEADER);
        tabPanel.add(workspaceMenuView, WORKSPACES_TAB_HEADER);
        tabPanel.selectTab(0);

        pin.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                setPinText();
                getPresenter().savePinnedState(pin.isDown());
            }
        });

    }

    @SuppressWarnings("unused")
    @UiHandler("handle")
    void onHandleClick(final ClickEvent event) {
        if (!pin.isDown()) {
            if (isSlideInState) {
                getPresenter().getEventBus().fireEvent(
                        new GroupPopupChangeEvent(getPresenter().getWorkspaceId(), false));
                slideOut();
            } else {
                slideIn();
                getPresenter().getEventBus().fireEvent(
                        new GroupPopupChangeEvent(getPresenter().getWorkspaceId(), isWindowsTabSelected));
            }
        } else {

        }
    }

    public void onResize(final int launchMenuHeight) {
        final int viewHeight = launchMenuHeight - (HEADER_HEIGHT + TABS_HEIGHT + VIEW_PADDING + FOOTER_HEIGHT);
        windowsMenuView.setFilterPanelHeight(viewHeight);
        workspaceMenuView.setFilterPanelHeight(viewHeight);
    }

    public void slideIn() {
        container.getElement().getStyle().setLeft(0, Unit.PX);
        handle.setResource(resourceBundle.handleClose());
        handle.addStyleName(resourceBundle.style().handle());
        handle.setTitle(SLIDE_IN_TITLE);
        isSlideInState = true;
        /** Ensure ZIndex is greater than the windows. tiling and cascading windows increases zIndex exponentially (GXT internals) **/
        container.getElement().getStyle().setZIndex(Math.max(LAUNCH_MENU_Z_INDEX, XDOM.getTopZIndex()));
        setPinText();
    }

    public void slideOut() {
        container.getElement().getStyle().setLeft(-container.getOffsetWidth() + SLIDE_LEFT_OFFSET, Unit.PX);
        handle.setResource(resourceBundle.handleOpen());
        handle.addStyleName(resourceBundle.style().handle());
        handle.setTitle(SLIDE_OUT_TITLE);
        isSlideInState = false;
    }

    public boolean isSlideInState() {
        return isSlideInState;
    }

    public void windowLaunchCompleted() {
        if (!pin.isDown()) {
            slideOut();
            ToggleButtonIsUpTitle();
        }

    }

    public void updateStatusLabel(final String status) {
        statusLabel.setText(status);
    }

    /**
     * @return
     */

    /** Launch Menu Pin Tooltip **/
    public boolean isPinned() {
        return pin.isDown();
    }

    public void ToggleButtonIsDownTitle() {
        pin.setTitle(PIN_DOWN_ALT_TEXT);
    }

    public void ToggleButtonIsUpTitle() {
        pin.setTitle(PIN_UP_ALT_TEXT);
    }

    public void setPinText() {
        if (!pin.isDown()) {
            ToggleButtonIsUpTitle();
            handle.setTitle(SLIDE_IN_TITLE);
        } else {
            ToggleButtonIsDownTitle();
            handle.setTitle(SLIDE_IN_PINNED_TITLE);
        }
    }
}