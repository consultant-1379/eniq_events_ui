/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget;

import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.WindowState;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.grid.IBottomToolBar;
import com.ericsson.eniq.events.ui.client.grid.JSONGrid;
import com.ericsson.eniq.events.ui.client.main.AbstractWindowLauncher;
import com.ericsson.eniq.events.ui.client.main.ChartLauncher;
import com.ericsson.eniq.events.ui.client.main.GridLauncher;
import com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Element;
import com.google.web.bindery.event.shared.EventBus;
import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;

/**
 * Generic Grid View in MVP pattern. Window to display grids
 * which can have a toolbar also.
 *
 * @author eendmcm
 * @since Feb 2010
 */
public class EventGridView extends AbstractBaseWindowDisplay implements IEventGridView {

    private final JSONGrid grid;

    protected final MetaMenuItem metaItem;

    /**
     * Construct generic grid view supporting passing of instance type such
     * that instance type will be available to base class when over-riding methods
     * to create special launch buttons
     *
     * @param multiWinId    - Multi-instance support id. Contains id we put on base window (which is same as what will use for menu item,
     *                      launch button and query
     * @param metaItem      - Menu Item which launched the window (chart) containing all information
     *                      needed to submit URL query etc)
     * @param workspaceController     
     * @param constrainArea - area window (chart) is to sit in.
     * @param windowState  
     */
    public  EventGridView(final MultipleInstanceWinId multiWinId,
            final MetaMenuItem metaItem, final IWorkspaceController workspaceController,
            final ContentPanel constrainArea, final WindowState windowState) {

        super(multiWinId, workspaceController, constrainArea, metaItem
                .getTaskBarButtonAndInitialTitleBarName(), metaItem.getStyle(), windowState);

        this.metaItem = metaItem;
        this.grid = new JSONGrid();
        grid.setId(metaItem.getID());

        final ToolBar tb = grid.getBottomToolbar();
        if (tb != null) {
            getWidget().setBottomComponent(tb);
        }

    }

    @Override
    public Grid<ModelData> getGridControl() {
        return grid;
    }

    /*
    * gets the DataType that holds all the initial
    * details that was used to initialise the widget
    */
    @Override
    public MetaMenuItem getViewSettings() {
        return metaItem;
    }

    @Override
    public ModelData getGridRecordSelected() {
        return grid.getRecord();
    }

    @Override
    public String getGridCellValue(final int row, final String columnId) {
        final int col = grid.getColumnModel().getIndexById(columnId);
        final Element ele = grid.getView().getCell(row, col);
        return (ele == null ? EMPTY_STRING : ele.getInnerText());
    }

    @Override
    public String getGroupingGridCellValue(final int row, final String col) {
        final Object oVal = grid.getStore().getAt(row).get(col);
        return (oVal == null ? EMPTY_STRING : String.valueOf(oVal));
    }

    @Override
    public GridInfoDataType getColumns() {
        return grid.getColumns();
    }

    @Override
    public AbstractWindowLauncher getToggleWindowLauncher(final EventBus eventBus) {
        if (toggleWindowLauncher == null) {
            toggleWindowLauncher = new ChartLauncher(metaItem, eventBus, constraintArea, workspaceController);
        }
        return toggleWindowLauncher;
    }

    @Override
    public AbstractWindowLauncher getWindowLauncher(final EventBus eventBus) {
        return new GridLauncher(metaItem, eventBus, constraintArea, workspaceController);
    }

    @Override
    public String getLastRefreshTimeStamp() {
        final ToolBar tb = grid.getBottomToolbar();
        if (tb != null && tb instanceof IBottomToolBar) {
            return ((IBottomToolBar) tb).getLastRefreshTimeStamp();
        }
        return EMPTY_STRING;
    }

    @Override
    public void updateLastRefreshedTimeStamp(final String timeStamp) {
        final ToolBar tb = grid.getBottomToolbar();
        if (tb != null && tb instanceof IBottomToolBar) {
            ((IBottomToolBar) tb).updateLastRefreshedTimeStamp(timeStamp);
        }
    }
}
