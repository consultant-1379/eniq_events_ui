/********************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 ******************************************************************************* */
package com.ericsson.eniq.events.ui.client.grid.listeners;

import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.HashMap;
import java.util.Map;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.client.buttonenabling.RecurErrorButtonEnableConditions;
import com.ericsson.eniq.events.ui.client.buttonenabling.SACButtonEnableConditions;
import com.ericsson.eniq.events.ui.client.common.comp.BaseToolBar;
import com.ericsson.eniq.events.ui.client.common.widget.IEventGridView;
import com.ericsson.eniq.events.ui.client.grid.JSONGrid;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;

/**
 * 
 *  RowSelectListener provides a listener for when a row in a grid is selected
 *  (e.g. required when need to enable or disable some window buttons in the toolbar)
 * 
 * @author esuslyn
 * @since March 2010
 */
public class RowSelectListener implements Listener<GridEvent<ModelData>> {

    private final JSONGrid grid;

    private final IEventGridView gridView;

    private final RecurErrorButtonEnableConditions recurErrConditions = getRecurErrButtonEnableConditions();

    private final SACButtonEnableConditions SACButtonEnableConditions = getSACButtonEnableConditions();

    /**
     * Construct row selection listener 
     * @param grid - JSONGrid to access it's setRecord method
     * @param gridView - IEventGridView interface to access methods in the EventGridView class 
     */
    public RowSelectListener(final JSONGrid grid, final IEventGridView gridView) {
        super();
        this.grid = grid;
        this.gridView = gridView;

    }

    @Override
    public void handleEvent(final GridEvent<ModelData> be) {

        final ModelData record = be.getModel();
        grid.setRecord(record);
        gridView.setToolbarButtonEnabled(BTN_PROPERTIES, true);

        if (recurErrorButtonExists()) {
            handleEnablingRecurErrorButton(record);
        }
        handleSACButtonEnabling(record);
    }

    /*
     * ONLY Calling this when Recur Error button exists 
     * 
     * Not enough for Recurring Error button that the row is selected.
     * We want to check all other conditions when row selected too.
     * (only start expensive method if button exists)
     * 
     * @param record   record for row selected 
     */
    private void handleEnablingRecurErrorButton(final ModelData record) {

        final Map<String, String> columnDetails = getAllGridColumnHeaderIds();

        // if column headers we are interested in exist
        boolean shouldEnable = recurErrConditions.shouldEnableBasedOnColumnHeaders(columnDetails.keySet());

        if (shouldEnable) { // checked column headers - now check value in row

            /* is Event Result REJECT on selected row - if so enable */
            final String idForHeader = columnDetails.get(EVENT_RESULT_HEADER); // e.g. "7"          
            shouldEnable = REJECT_CELL_VALUE.equals(record.get(idForHeader));

        }
        gridView.setToolbarButtonEnabled(BTN_RECUR_ERROR, shouldEnable);
    }

    /* 
     * Utility to check 'Access Area' column header present when row selected too.
     * And some Access Area value in the selected row.
     * 
     * @param record   record for row selected. Will use this to verify the Access Area 
     *                 column has a not null value for the row selected. 
     */
    private void handleSACButtonEnabling(final ModelData record) {
        final Map<String, String> columnDetails = getAllGridColumnHeaderIds();

        boolean shouldEnable = false;

        if (record != null) {
            if (SACButtonEnableConditions.shouldEnableBasedOnColumnHeaders(columnDetails.keySet())) {
                final String idForHeader = columnDetails.get(ACCESS_AREA);
                final String cellValue = record.get(idForHeader);

                if (cellValue != null && !cellValue.isEmpty()) {
                    shouldEnable = (record.get(idForHeader) != null); //enable only when not null.
                }
               if(columnDetails.containsKey(RAT_ID)) {
                final String idForHeaderRAT = columnDetails.get(RAT_ID) ;
                if(RAT_VALUE_FOR_LTE.equals(record.get(idForHeaderRAT).toString()) || RAT_VALUE_FOR_GSM.equals(record.get(idForHeaderRAT).toString())){
                    shouldEnable = false;
                }
               }
            }
        }
        gridView.setToolbarButtonEnabled(BTN_SAC, shouldEnable);
    }

    /*
     * Unfortunately can not cache the column headers 
     * - as same grid is used following drilldown (and we are not 
     * passing indication that the grid has changed)
     * 
     * If do add this indication will need to clear columnHeaders
     * both for data changed from server call or breadcrumb change
     * 
     * @return Map of column headers as the keys, and with "Ids" (1,3,5, etc) as the values
     *         the current open grid.
     */
    protected final Map<String, String> getAllGridColumnHeaderIds() {

        final Map<String, String> headerIdColumnsMap = new HashMap<String, String>();
        final GridInfoDataType gridInfo = gridView.getColumns();
        final ColumnInfoDataType[] allColumnInfos = gridInfo.columnInfo;

        for (final ColumnInfoDataType column : allColumnInfos) {

            headerIdColumnsMap.put(column.columnHeader, column.columnID);

        }

        return headerIdColumnsMap;
    }

    /// junit extractions

    boolean recurErrorButtonExists() {
        final BaseToolBar winToolBar = gridView.getWindowToolbar();
        return (null != winToolBar.getItemByItemId(BTN_RECUR_ERROR));
    }

    RecurErrorButtonEnableConditions getRecurErrButtonEnableConditions() {
        return new RecurErrorButtonEnableConditions();
    }

    SACButtonEnableConditions getSACButtonEnableConditions() {
        return new SACButtonEnableConditions();
    }

}
