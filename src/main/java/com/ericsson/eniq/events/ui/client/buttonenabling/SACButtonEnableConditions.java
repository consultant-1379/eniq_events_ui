/********************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 ******************************************************************************* */
package com.ericsson.eniq.events.ui.client.buttonenabling;

import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;

import java.util.HashSet;
import java.util.Set;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.ButtonEnableParametersDataType;
import com.ericsson.eniq.events.ui.client.datatype.DrillDownParameterInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.IHyperLinkDataType;

/**
 * Concrete implementation of enabling conditions suitable for SAC button
 * Enable SAC button for any Cell type (no logic for 2G, 3G seperation here, 
 * even though SAC is relevant more for 3G)
 * 
 * As with all these conditions we are assuming the button exists only where 
 * relevant - i.e. we don't need all checks that won't be an issue for this button
 * 
 * @author eeicmsy
 * @since Nov 2010
 *
 */
public class SACButtonEnableConditions implements IButtonEnableConditions {

    private GridInfoDataType gridInfo;

    @Override
    public boolean shouldEnableButton(final ButtonEnableParametersDataType params) {

        gridInfo = params.columnsMetaData;

        if (shouldDisableBasedOnNetworkType(params)) {
            return false;
        }

        if (params.isRowSelected) {
            return shouldEnableBasedOnColumnHeaders(getAllGridColumnHeaders());
        }

        if (shouldEnableBasedOnDrillDownState(params.widgetSpecificInfo)) {
            return true;
        }

        if (params.searchData == null) { // its ok if search data is empty (must not null)
            return false;
        }

        if (params.searchData.isGroupMode()) {
            return false;
        }

        // e.g. NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=10080&tzOffset=+0000&type=CELL&node=00,ONRM_RootMo_R:RNC01:RNC01,Ericsson,3G

        return SEARCH_FIELD_CELL_TYPE.equals(params.searchData.getType());
    }

    /**
     * Utility to check if button should be enabled if there is a column names Access Area present.
     * (e.g to call from RowSelection Listener)
     * 
     * @param headers   - all column headers on the current grid
     * @return true if row is selected when Access Area is one of the column header and the value there is not null.
     */
    public boolean shouldEnableBasedOnColumnHeaders(final Set<String> headers) {
        return headers.contains(ACCESS_AREA);
    }

    /*
     * returns all column header of the current open grid.
     */
    private final Set<String> getAllGridColumnHeaders() {
        final ColumnInfoDataType[] allColumns = gridInfo.columnInfo;
        final Set<String> columnHeaders = new HashSet<String>();
        if (allColumns != null) {
            for (final ColumnInfoDataType column : allColumns) {
                final String header = column.columnHeader;
                columnHeaders.add(header);
            }
        }
        return columnHeaders;
    }

    /*
     * Enable for Cell type searches or drill on cell level
     * 
     * <li>
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&node=szyskj.gd&type=APN&cell=CELL68513
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=10080&tzOffset=+0000&node=dhl-ddn.bj&type=APN&cell=952
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=SGSN&sgsn=SGSN1&cell=CELL68639 
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=CELL&groupname=DG_GroupNameRATVENDHIER321_250&cell=1055
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=TAC&tac=33003467&cell=CELL1765 
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=10080&tzOffset=+0000&type=CELL&groupname=DG_GroupNameRATVENDHIER321_250&cell=1132 
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=SGSN&groupname=DG_GroupNameEVENTSRC_250&cell=CELL20202 
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=SGSN&groupname=DG_GroupNameEVENTSRC_250&cell=CELL20202
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=BSC&groupname=DG_GroupNameRATVENDHIER3_250&cell=1416 
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=APN&apn=epc.tmobile.com&cell=1559
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=10080&tzOffset=+0000&type=APN&groupname=DG_GroupNameAPN_250&cell=CELL129967 
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=APN&groupname=DG_GroupNameAPN_250&cell=CELL885 
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=SGSN&sgsn=SGSN1&cell=CELL31801 
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=BSC&groupname=DG_GroupNameRATVENDHIER3_250&cell=1351 
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=SGSN&sgsn=SGSN1&cell=CELL39707
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=TAC&tac=35327901&cell=CELL68492 /* NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=TAC&tac=33000953&cell=CELL68699 
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=SGSN&sgsn=SGSN1&cell=CELL25009/* NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&RAT=0&vendor=ERICSSON&bsc=BSC179&cell=CELL35602&type=CELL
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&RAT=0&vendor=ERICSSON&bsc=BSC101&cell=CELL20002&type=CELL
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=CELL&cell=CELL68492&vendor=ERICSSON&bsc=BSC343&RAT=0
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=BSC&vendor=ERICSSON&bsc=BSC343&RAT=0&cell=CELL68416
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=BSC&vendor=ERICSSON&bsc=BSC341&RAT=0&cell=CELL68157
     * NETWORK/TOPOLOGY/3G/CONNECTED_CELLS?display=grid&time=30&tzOffset=+0000&type=BSC&vendor=ERICSSON&bsc=BSC339&RAT=0&cell=CELL67784 
     * </li>
     * 
     * @param  widgetSpecificInfo - information in URL outbound parameters window was launched with
     * @return                    - true if SAC button should be enabled
     */
    private boolean shouldEnableBasedOnDrillDownState(final IHyperLinkDataType widgetSpecificInfo) {
        if (widgetSpecificInfo == null) {
            return false;
        }

        final DrillDownParameterInfoDataType[] urlParams = widgetSpecificInfo.getParams();
        for (final DrillDownParameterInfoDataType urlParam : urlParams) {

            /* whenever drill to cell level (cell=CELL129947) - want SAC enabled
             * checking "cell="  - not bothering checking type=CELL*/
            if (SEARCH_FIELD_CELL_TYPE.equalsIgnoreCase(urlParam.parameterName)) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldDisableBasedOnNetworkType(final ButtonEnableParametersDataType params) {
        final String[] urlParams = params.searchData.urlParams;

        for (String  urlParam : urlParams) {
            if (urlParam.contains(RAT_PARAM_FOR_LTE)  || urlParam.contains(RAT_PARAM_FOR_GSM)) {
                return true;
            }
        }
        return false;
    }
}
