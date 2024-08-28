package com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu;

import java.util.Collection;

import com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu.WorkspaceFilter.WorkspaceStateItem;
import com.ericsson.eniq.events.widgets.client.textbox.ExtendedTextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class WorkspaceFilterPanel extends Composite {
    private static final int TEXTBOX_HEIGHT = 36 + 1;

    private static final int HEADER_HEIGHT = 20;

    private static final int BORDERS_SPACE = 2;

    private static WorkspaceFilterPanelUiBinder uiBinder = GWT.create(WorkspaceFilterPanelUiBinder.class);

    interface WorkspaceFilterPanelUiBinder extends UiBinder<Widget, WorkspaceFilterPanel> {
    }

    @UiField
    Label header;

    @UiField
    ExtendedTextBox filter;

    @UiField
    HTMLPanel filterBoxHolder;

    @UiField
    ScrollPanel scrollPanel;

    @UiField
    HTMLPanel filterContent;

    private WorkspaceFilter workspaceFilter;

    public WorkspaceFilterPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void init(final WorkspaceFilter wf) {
        this.workspaceFilter = wf;
        filter.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(final KeyUpEvent event) {
                if (filter.containsDefaultText()) {
                    workspaceFilter.filter("");
                } else {
                    workspaceFilter.filter(filter.getText());
                }
            }
        });
    }

    public void setScrollPanelHeight(final int filterPanelHeight) {
        scrollPanel.setHeight((filterPanelHeight - HEADER_HEIGHT - TEXTBOX_HEIGHT - BORDERS_SPACE) + "px");//- 2
    }

    public void setHeader(final String header) {
        this.header.setText(header);
    }

    public void setId(final String id) {
        getElement().setId(id);
    }

    public void clear() {
        filter.setText("");
        filter.enableDefaultText();
    }

    public Collection<WorkspaceStateItem> getSelectedItems() {
        return workspaceFilter.getView().getSelectedItems();
    }

    public HTMLPanel getFilterContent() {
        return filterContent;
    }
}
