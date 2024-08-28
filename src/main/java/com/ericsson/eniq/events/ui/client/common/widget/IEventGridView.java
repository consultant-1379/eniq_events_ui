/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget;

import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowView;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.grid.Grid;

/**
 * Interface for grid view
 * 
 * @author eendmcm
 * @since Feb 2010
 *
 */
public interface IEventGridView extends IBaseWindowView, IExtendedWidgetDisplay {

    /**
     * Fetch grid object (e.g. JSONGrid)
     * @return grid object (e.g. JSONGrid)
     */
    Grid<ModelData> getGridControl();

    /**
     * Fetch information for selected row (record)
     * @return  Information for selected row (record) 
     */
    ModelData getGridRecordSelected();

    /**
     * Fetch grid meta data
     * @return GridInfoDataType object for grid containing column information and more
     */
    GridInfoDataType getColumns();

    /**
     * Utility fetching  the cell value for the row column
     * @param row   row in grid to locate cell  
     * @param columnId   columnId in grid to llocate cell
     * @return      value in cell 
     */
    String getGridCellValue(final int row, final String columnId);

    /**
     * Utility fetching  the cell value for the row column
     * from a Grouping Grid View
     * @param row   row in grid to locate cell  
     * @param col   column in grid to llocate cell
     * @return      value in cell 
     */
    String getGroupingGridCellValue(final int row, final String col);

}
