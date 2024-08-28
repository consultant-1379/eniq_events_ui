/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu;

import com.ericsson.eniq.events.widgets.client.utilities.ZIndexHelper;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import java.util.List;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class GroupElementPopupPanel {

    private static GroupElementPopupPanelUiBinder uiBinder = GWT.create(GroupElementPopupPanelUiBinder.class);

    interface GroupElementPopupPanelUiBinder extends UiBinder<Widget, GroupElementPopupPanel> {
    }

    @UiField
    PopupPanel popupPanel;

    @UiField
    Label headerLabel;

    @UiField(provided = true)
    CellList<String> groupElementList;

    private final ListDataProvider<String> dataProvider;

    private boolean isVisable;

    public GroupElementPopupPanel() {
        groupElementList = new CellList<String>(new TextCell());
        groupElementList.setVisibleRange(0, 1000); // Max Elements in groups is currently 250
        groupElementList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
        dataProvider = new ListDataProvider<String>();
        dataProvider.addDataDisplay(groupElementList);
        uiBinder.createAndBindUi(this);
        popupPanel.setHeight("100%");
    }

    public void show() {
        if (!popupPanel.isShowing()) {
            popupPanel.show();
            int zIndex= ZIndexHelper.getHighestZIndex();
            popupPanel.getElement().getStyle().setZIndex(zIndex);
            isVisable = true;
        }
    }

    public void setPopupPosition(final int left, final int top) {
        popupPanel.setPopupPosition(left, top);
    }

    public void hide() {
        if (popupPanel.isShowing()) {
            popupPanel.hide();
            isVisable = false;
        }
    }

    public boolean isVisable(){
        return this.isVisable;
    }

    public void setGroupElements(List<String> groupElements) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(groupElements);
    }

    /**
     * @param header
     */
    public void setHeader(String header) {
        headerLabel.setText(header);
    }
}
