/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.buttonenabling;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.ButtonEnableParametersDataType;
import com.ericsson.eniq.events.ui.client.datatype.DrillDownParameterInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.IHyperLinkDataType;

/**
 * Concrete implementation of enabling conditions suitable for KPI button.
 * The KPI button should not be available at raw drilldown level
 * 
 * As with all these conditions we are assuming the button exists only where 
 * relevant - i.e. we don't need all checks that won't be an issue for this button
 * 
 * @author eeicmsy
 * @since Nov 2010
 *
 */
public class KPIButtonEnableConditions implements IButtonEnableConditions {

    @Override
    public boolean shouldEnableButton(final ButtonEnableParametersDataType params) {

        if (params.rowCount == 0) {
            return false;
        }

        if (params.searchData == null || params.searchData.isEmpty()) {
            return false;
        }

        //  to stop being enabled for drilldown on Failures (won't contain success ratio)
        if (shouldDisableBasedOnColumnHeaders(params.columnsMetaData)) {
            return false;
        }

        // for Success Ratio drill (which still contain "Success Ratio"
        if (shouldDisableBasedOnDrillDownState(params.widgetSpecificInfo)) {
            return false;
        }

        if (disableForImsiPtmsiMsisdnEventAnalysis(params.searchData.urlParams)) {
            return false;
        }

        if (disableForImsiGroupEventAnalysis(params.searchData.urlParams)) {
            return false;
        }

        if (params.isCurrentlyEnabled) {
            // fine leave it enabled
            return true;
        }

        return true;
    }

    /*
     * Do not want KPI button enabled for raw events from Failures 
     * and not trusting breadcrumb popolution to find "ERR" in widet params)
     * (and KPI Ratio press but that won't help here)
     * @param columnsMetaData    columns on grid to be presented
     * @return                   false if "Success Ratio" is NOT present - meaning disable button
     */
    private boolean shouldDisableBasedOnColumnHeaders(final GridInfoDataType columnsMetaData) {

        for (final ColumnInfoDataType columnInfo : columnsMetaData.columnInfo) {
            if (KPI_SUCCESS_RATIO_COLUMN_HEADER.equals(columnInfo.columnHeader)) {
                return false;
            }
        }
        return true;
    }

    /*
     * Disable for raw events from Failures and KPI Ratio press
     * 
     * @param  widgetSpecificInfo - informatiion in URL outbound parameters window was launched with
     * @return                    - true if KPI button should be disabled
     */
    private boolean shouldDisableBasedOnDrillDownState(final IHyperLinkDataType widgetSpecificInfo) {

        if (widgetSpecificInfo == null) {
            return false;
        }

        final DrillDownParameterInfoDataType[] urlParams = widgetSpecificInfo.getParams();
        for (final DrillDownParameterInfoDataType urlParam : urlParams) {

            /* eventId present is raw events for KPI (and failure)*/
            if (PARAM_EVENT_ID.equals(urlParam.parameterName)) {
                return true;
            }

            /* precaution really - eventId would be present in here too*/
            /* key=ERR is always a raw event screen 0 will want to be disabled */
            if (PARAM_KEY.equals(urlParam.parameterName) && ERR.equals(urlParam.parameterValue)) {
                return true;
            }

        }
        return false;
    }

    /*
     * No KPI btn needed for imsi & ptmsi event analysis. 
     * Check if search field is a imsi or ptmsi value and if yes, disable KPI btn.
     * 
     * @param  urlParams - Strings with URL parameter format, for example 
     *                         "node=myNode", "nodeType=SGSN", "imsi=12121212121"
     * @return                    - true if KPI button should be disabled
     */
    private boolean disableForImsiPtmsiMsisdnEventAnalysis(final String[] urlParams) {

        if (urlParams != null) {
            for (final String urlParam : urlParams) {
                if (urlParam.contains(SEARCH_FIELD_IMSI_PARAM) || urlParam.contains(SEARCH_FIELD_PTMSI_PARAM)
                        || urlParam.contains(SEARCH_FIELD_MSISDN_PARAM)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * No KPI btn needed for imsi group event analysis. 
     * Check if type is IMSI and search field is a groupname value. If yes, disable KPI btn.
     * 
     * @param  urlParams - Strings with URL parameter format, for example 
     *                         "node=myNode", "nodeType=SGSN", "imsi=12121212121"
     * @return                    - true if KPI button should be disabled
     */
    private boolean disableForImsiGroupEventAnalysis(final String[] urlParams) {

        if (urlParams != null) {
            for (final String urlParam1 : urlParams) {
                if (urlParam1.contains(PARAM_IMSI_TYPE)) {
                    for (final String urlParam2 : urlParams) {
                        if (urlParam2.contains(GROUP_VALUE_PARAM)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
