/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.common.widget.window.EniqWindowGwt;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Saved Workspace Launch Dialog
 *
 * @author ecarsea
 * @since June 2012
 */
public class WorkspaceLaunchDialogView extends BaseView<WorkspaceLaunchDialogPresenter> {

    private static final String DEFAULT_HEIGHT = "350px";

    private static final String DEFAULT_WIDTH = "600px";

    interface WorkspaceLaunchDialogViewUiBinder extends UiBinder<Widget, WorkspaceLaunchDialogView> {
    }

    private static WorkspaceLaunchDialogViewUiBinder uiBinder = GWT.create(WorkspaceLaunchDialogViewUiBinder.class);

    @UiField
    Button launchBtn;

    @UiField
    Button cancelBtn;

    @UiField
    VerticalPanel launchPanel;

    private final EniqWindowGwt window;

    @UiHandler("launchBtn")
    public void onLaunchClicked(@SuppressWarnings("unused") final ClickEvent event) {
        getPresenter().onLaunchWindows();
        window.close();
    }

    @UiHandler("cancelBtn")
    public void onCancelClicked(@SuppressWarnings("unused") final ClickEvent event) {
        window.close();
        getPresenter().onCancelWindows();
    }

    public WorkspaceLaunchDialogView() {
        initWidget(uiBinder.createAndBindUi(this));
        window = new EniqWindowGwt();
        window.setDraggable(false);
        window.setContent(this.asWidget());
        window.setGlassEnabled(true);
        window.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        window.getCloseButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getPresenter().onCancelWindows();
            }
        });
    }

    public void launch(WorkspaceView workspaceView) {
        window.setBoundaryPanel(workspaceView.getWindowContainer().getWindowContainerPanel());
        workspaceView.getWindowContainer().getWindowContainerPanel().add(window);
        workspaceView.getWindowContainer().getWindowContainerPanel().layout();
        window.center();
        launchPanel.clear();
    }

    public void setWindowTitle(String title) {
        window.setTitle(title);
    }

    public void addWindowConfigPanel(Widget windowConfigPanel) {
        launchPanel.add(windowConfigPanel);
    }
}