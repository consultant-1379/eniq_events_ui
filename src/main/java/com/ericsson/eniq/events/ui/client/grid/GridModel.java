/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.CellType;
import com.ericsson.eniq.events.ui.client.datatype.GridCellColorType;
import com.ericsson.eniq.events.ui.client.datatype.grid.IColumnState;
import com.ericsson.eniq.events.ui.client.grid.filters.DateTimeFilter;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.grid.filters.NumericFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;

import java.util.*;

import static com.ericsson.eniq.events.common.client.CommonConstants.Received_Date_Format;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;

/*
 * 
 * Initialises the grid columns based on the
 * metaData held in the columns object
 * 
* @author eendmcm
* @since  Feb 2010
*/

public class GridModel {

    /*
     * This is a (hack) list added because we have changed some columns to be 
     * strings rather than ints in the meta data in order to be able to present
     * "blanks". "Blanks" suit charting "null" data better, in corresponding chart
     * for the grid, as we can set a nullValue to bypass dot - ref LineDataProviderWithToolTip
     * 
     */
    private final static List<String> alwaysRightColumns = new ArrayList<String>();

    private final static List<String> kpiheaderColumns = new ArrayList<String>();

    /**
     * A list of all the columns whos cell should be coloured.
     */
    private final static List<String> coloredColumns = new ArrayList<String>();

    /**
     * A list of the columns which should not be hyperlinked if their value is zero (0)
     */
    private final static List<String> hyperlinkIfNotZeroColumns = new ArrayList<String>();

    private final static List<String> columnsWithNoHyperlinkForUnknownValues = new ArrayList<String>();

    public static final String TREND_COL_HEADER = "Failure Trend";

    static {
        alwaysRightColumns.add(IMPACTED_SUBSCRIBERS_COLUMN_HEADER);
        alwaysRightColumns.add(IMPACTED_SUBSCRIBERS_LTE_COLUMN_HEADER);

        kpiheaderColumns.add(KPI_COLUMN_HEADER);
        kpiheaderColumns.add(KPI_SUCCESS_RATIO_COLUMN_HEADER);

        //These columns will have colour blocks too :)
        coloredColumns.add(EVENT_RESULT_HEADER);

        hyperlinkIfNotZeroColumns.add(FAILURES_COLUMN_HEADER);
        hyperlinkIfNotZeroColumns.add(NUMBER_ERABS_COLUMN_HEADER);
        hyperlinkIfNotZeroColumns.add(NO_ERABS_COLUMN_HEADER);
        columnsWithNoHyperlinkForUnknownValues.add(MANUFACTURER_COLUMN_HEADER);
        columnsWithNoHyperlinkForUnknownValues.add(MODEL_COLUMN_HEADER);
    }

    private static final String DATE = "date";

    private static final String FLOAT = "float";

    private static final String INT = "int";

    private static final String LONG = "long";

    /* (default access for junit) */
    final List<ColumnConfig> arrayColumns = new ArrayList<ColumnConfig>();

    /**
     * Definition of the grid's columns
     */
    public final ColumnModel cm;

    /*
     * Definition of the grid's column filters
     */
    private final List<Filter> filterModelList;

    /**
     * Definition of the grid's store which is the input data for the grid
     */
    public final ModelType type = new ModelType();

    private final GridInfoDataType gridMetaData;

    /**
     * @param gridMetaData
     */
    public GridModel(final GridInfoDataType gridMetaData) {
        this(gridMetaData, null);
    }

    /**
     * Initialises the grid columns based on the
     * metaData held in the columns object
     * 
     * @param gridMetaData  - meta data for grid
     * @param stateManager 
     */
    public GridModel(final GridInfoDataType gridMetaData, final JSONGridStateManager stateManager) {

        this.type.setRoot(JSON_ROOTNODE);

        this.gridMetaData = gridMetaData;
        /*initialise a new filter model list*/
        filterModelList = new ArrayList<Filter>();

        setupColumns(gridMetaData, stateManager); // NOPMD by eeicmsy on 05/09/11 11:55

        //Calling private class below.
        cm = new GridColumnModel(arrayColumns);

    }

    /*

    The following bug exists with grids as the header is a seperate table from the body of the grid and so the alignment of both is out by 1px for each column
    See: http://www.sencha.com/forum/showthread.php?177768-Grid-method-setColumnLines(true)-offset-by-1px-the-header-lines.-Bug-How-to-fix

    If we upgrade to a newer version of GXT and this is fixed then we can remove this hack

    See DEFTFTMIT-252 & DEFTFTMIT-442
     */

    private class GridColumnModel extends ColumnModel {

        public GridColumnModel(List<ColumnConfig> columns) {
            super(columns);
        }

        /**
         * Override this method by adding 1px for every column.
         * @param includeHidden
         * @return
         */
        @Override
        public int getTotalWidth(boolean includeHidden) {
            return super.getTotalWidth(includeHidden) + super.getColumnCount();
        }


    }
    /**
     * @retrun Definition of the grid's column filters
     */
    public List<Filter> getFilterModelList() {
        return filterModelList;
    }

    /**
     * Utility fetching gridMetaData the model was created with
     * @return  grid meta data
     */
    public GridInfoDataType getGridInfoDataType() {
        return gridMetaData;
    }

    ///////////////////////
    ///////////////////////   private methods and classes /////////////////////////////////////
    ///////////////////////

    private final void setupColumns(final GridInfoDataType gridMetaData, final JSONGridStateManager stateManager) {
        final Map<String, ColumnInfoDataType> columnMap = new HashMap<String, ColumnInfoDataType>();

        final List<ColumnInfoDataType> columnInfoDataTypes = new ArrayList<ColumnInfoDataType>(
                Arrays.asList(gridMetaData.columnInfo));
        for (final ColumnInfoDataType colElement : columnInfoDataTypes) {
            final String key = getColumnKey(colElement);
            columnMap.put(key, colElement);
        }

        /** Retrieve any Column States **/
        if (stateManager != null && stateManager.hasSavedState()) {
            for (final IColumnState columnState : stateManager.getColumnStates()) {
                ColumnInfoDataType colElement = null;
                if ((colElement = columnMap.get(columnState.getColumnTypeId())) != null) {
                    configureColumn(colElement, columnState.isHidden(), columnState.getWidth());
                    /** Remove element from list so as only unSaved Columns will be configured from meta data **/
                    columnInfoDataTypes.remove(colElement);
                }
            }
        }
        /** Configure any remaining columns i.e. where state is not saved using the meta data **/
        for (final ColumnInfoDataType colElement : columnInfoDataTypes) {
            configureColumn(colElement, colElement.isHidden, Integer.parseInt(colElement.columnWidth));
        }
    }

    protected String getColumnKey(final ColumnInfoDataType colElement) {
        return colElement.columnType.isEmpty() ? colElement.columnID : colElement.columnType;
    }

    protected void configureColumn(final ColumnInfoDataType colElement, final boolean hidden, final int width) {
        final ColumnConfig gridColumn = new EventGridColumnConfig(colElement.columnID, colElement.columnID, width,
                colElement.columnType);

        DataField field = new DataField(colElement.columnID); //NOPMD (eemecoy 1/6/10, necessary evil)
        field.setName(colElement.columnID);

        /* check if this column is hidden from user by default but user has ability to redisplay */

        /* system columns are not available to the end user*/
        if (colElement.isSystem) {
            gridColumn.setFixed(true);
            gridColumn.setHidden(true); // always when system
            gridColumn.setMenuDisabled(true);

        } else {
            gridColumn.setHidden(hidden);
        }

        gridColumn.setHeader(colElement.columnHeader);

        setGridCellRenderer(colElement, gridColumn);

        /* specify the data type if not string */
        field = checkForNumericAndDate(colElement, gridColumn, field);
        this.arrayColumns.add(gridColumn);
        this.type.addField(field);
        filterModelList.add(getColumnFilter(colElement.columnDataType, colElement.columnID)); // NOPMD by eendmcm on 08/12/10 16:28
    }

    private void setGridCellRenderer(final ColumnInfoDataType colElement, final ColumnConfig gridColumn) {

        if (colElement.getDrillDownTypeKey().length() > 0) {
            /* if the cell contains a hyperLink use specific renderer */
            gridColumn.setRenderer(getHyperLinkCellDrillDownRenderer(colElement));
            return;
        }
        if (colElement.launchWindowType.length() > 0) {
            /* is the cell a hyperlink that will launch a new pop up window */
            gridColumn.setRenderer(getHyperLinkLaunchCellRenderer(colElement));
            return;
        }

        // hyper links are covered already now
        final ColorGridCellRenderer plainRenderer = getPlainCellRenderer(colElement);
        if (plainRenderer != null) {
            gridColumn.setRenderer(plainRenderer);
            return;
        }
    }

    /* exposed for junit */
    final HyperLinkLaunchCellRenderer getHyperLinkLaunchCellRenderer(final ColumnInfoDataType colElement) {
        if (hyperlinkIfNotZeroColumns.contains(colElement.columnHeader) || columnsWithNoHyperlinkForUnknownValues.contains(colElement.columnHeader) ) {
            // Don't create a hyperlink & associated styling if the value is 0  or Unknown
            return new HyperLinkLaunchCellRenderer(colElement.launchWindowType, false);
        }
        return new HyperLinkLaunchCellRenderer(colElement.launchWindowType,true);
    }

    /*
     * Introduce colour coding on renderer.
     * This is fetching "drilldown renderer" (color code if required), as 
     * Opposed to new window launch 
     * 
     * @param colElement  column cell to render
     * @return renderer to use            
     */
    final ColorGridCellRenderer getHyperLinkCellDrillDownRenderer(final ColumnInfoDataType colElement) {

        // Specifically will be color coding KPI columns
        if (kpiheaderColumns.contains(colElement.columnHeader)) {
            return new ColorGridCellRenderer(colElement.getDrillDownTypeKey(),
                    GridCellColorType.KPI_SUCCESS_RATIO_DRILLDOWN);
        }

        if (hyperlinkIfNotZeroColumns.contains(colElement.columnHeader) || columnsWithNoHyperlinkForUnknownValues.contains(colElement.columnHeader) ) {
            // Don't create a hyperlink & associated styling if the value is 0 or Unknown
            return new ColorGridCellRenderer(colElement.getDrillDownTypeKey(), false);
        }

        return new ColorGridCellRenderer(colElement.getDrillDownTypeKey()); // blue hyperllink
    }

    /*
     * Fetch renderer for cell (value is internal to the renderer) or return null
     * if no speicial color rendering is required
     * @param colElement   column to check
     * @return             plain render (no hyperlink)
     */
    final ColorGridCellRenderer getPlainCellRenderer(final ColumnInfoDataType colElement) {
        if (kpiheaderColumns.contains(colElement.columnHeader)) {
            return new ColorGridCellRenderer(GridCellColorType.KPI_SUCCESS_RATIO_PLAIN_CELL);
        }

        if (coloredColumns.contains(colElement.columnHeader)){
            return new ColorGridCellRenderer(CellType.EVENT_CELL);
        }

        if (colElement.columnHeader.equalsIgnoreCase(TREND_COL_HEADER)){
            return new ColorGridCellRenderer(CellType.TREND_CELL);
        }
        return null;
    }

    /*
     * Account for Numerics and Date Types
     * @param colElement - DataType with Column Information
     * @param gridColumn - Grid Column been configured
     * @param field - Grid Field been initialised
     * 
     * exposed for junit only
     */
    final DataField checkForNumericAndDate(final ColumnInfoDataType colElement, final ColumnConfig gridColumn,
            final DataField field) {

        final boolean isAlwaysRight = alwaysRightColumns.contains(colElement.columnHeader);

        if (isAlwaysRight || colElement.columnDataType.equalsIgnoreCase(INT)) {
            gridColumn.setAlignment(HorizontalAlignment.RIGHT);

            /* represent Integers as Long Values to account for large values */
            field.setType(Long.class);

        } else if (colElement.columnDataType.equalsIgnoreCase(FLOAT)) {
            gridColumn.setAlignment(HorizontalAlignment.RIGHT);
            field.setType(Float.class);
        } else if (colElement.columnDataType.equalsIgnoreCase(DATE)) {
            field.setType(Date.class);
            field.setFormat(Received_Date_Format);
        }
        return field;
    }

    /*
     * protected to allow for junit override
     */
    Filter getColumnFilter(final String colDataType, final String colID) {

        if (colDataType.equalsIgnoreCase(INT) || colDataType.equalsIgnoreCase(LONG)
                || colDataType.equalsIgnoreCase(FLOAT)) {
            return createNumericFilterFilter(colID);
        } else if (colDataType.equalsIgnoreCase(DATE)) {
            return createDateTimeFilter(colID);
        }
        //TODO: add dataTypes to utilise BooleanFilter and ListFilter 
        return createStringFilter(colID);

    }

    /* junit for GXT 2.4 */
    StringFilter createStringFilter(final String colID) {
        return new StringFilter(colID);
    }

    NumericFilter createNumericFilterFilter(final String colID) {
        return new NumericFilter(colID);
    }

    DateTimeFilter createDateTimeFilter(final String colID) {
        return new DateTimeFilter(colID);
    }
}
