package com.ericsson.eniq.events.ui.client.groupmanagement;

import com.ericsson.eniq.events.ui.client.groupmanagement.resources.GroupMgmtCellListResources;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellList;

public class GroupManagementCellList<S> extends CellList<S> {

    private static GroupMgmtCellListResources resource = GWT.create(GroupMgmtCellListResources.class);

    public GroupManagementCellList(final Cell<S> cell) {
        super(cell, resource.cellStyle());
    }
}
