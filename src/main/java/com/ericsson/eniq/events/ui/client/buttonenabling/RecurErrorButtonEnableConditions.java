/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.buttonenabling;

import java.util.HashSet;
import java.util.Set;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.datatype.ButtonEnableParametersDataType;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;

/**
 * Implementation of enabling conditions suitable for Recurring error button on grid toolbar.
 * (User clicks on grid row and presses recurring error button to see the recurring error summary window)
 * 
 * This class ensures that the button becomes disabled when:
 * - No row is selected 
 * - There is no data on the grid
 * - If all required columns are not present in the grid
 * 
 * Note RowSelectListener is also involved
 * 
 * @author edivkir
 * @since Jan 2011
 *
 */
public class RecurErrorButtonEnableConditions implements IButtonEnableConditions {

    private final IMetaReader metaReader = MainEntryPoint.getInjector().getMetaReader();

    private GridInfoDataType gridInfo;

    private static Set<String> recurErrHeaders;

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.buttonenabling.IButtonEnableConditions#shouldEnableButton(com.ericsson.eniq.events.ui.client.datatype.ButtonEnableParametersDataType)
     */
    @Override
    public boolean shouldEnableButton(final ButtonEnableParametersDataType params) {

        gridInfo = params.columnsMetaData;

        if (params.rowCount == 0) {
            return false;
        }
        if (!params.isRowSelected) {
            return false;
        }
        return shouldEnableBasedOnColumnHeaders(getAllGridColumnHeaders());
    }

    /**
     * Utility to check if button should be enabled solely based on
     * current column headers on the grid (e.g to call from RowSelection Listener)
     * 
     * @param headers   - all column headers on the current grid
     * @return true if row should be selected based on column headers
     */
    public boolean shouldEnableBasedOnColumnHeaders(final Set<String> headers) {
        return headers.containsAll(getRecurErrHeaders());
    }

    /*
     * Lazy instantiate recurring headers (once).
     * Extracted for junit 
     * @return  meta data for fixed column headers we are interested in 
     */
    Set<String> getRecurErrHeaders() {
        if (recurErrHeaders == null) {
            recurErrHeaders = metaReader.getRecurErrHeadersParameters(false).keySet();
        }
        return recurErrHeaders;
    }

    /*
     * returns all column header of the current open grid.
     */
    private final Set<String> getAllGridColumnHeaders() {
        final ColumnInfoDataType[] allColumns = gridInfo.columnInfo;
        final Set<String> columnHeaders = new HashSet<String>();

        for (final ColumnInfoDataType column : allColumns) {
            final String header = column.columnHeader;
            columnHeaders.add(header);
        }
        return columnHeaders;
    }
}
