/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.datatype;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static java.util.Arrays.*;

import java.util.*;

/**
 * DataType used to configure the panels of each toolBars from metaData
 */
public class ToolbarPanelInfoDataType {

    public enum EventType {
        BACK, FORWARD, EXPORT, TIME, PROPERTIES, KPI, KPI_CS, SHOW_HIDE_CHART_LEGEND, NAVIGATION, REFRESH, TOGGLE_TO_GRID, ROAMING_BY_OPERATOR, ROAMING_BY_COUNTRY, SUB_BI_FAILED_EVENTS, SUB_BI_TAU, SUB_BI_HANDOVER, SUB_BI_APN_USAGE, SUB_BI_CELL, SUB_BI_ROUTING_AREA, SUB_BI_BUSY_DAY, SUB_BI_BUSY_HOUR, SUB_BI_TERMINALS, TERMINAL_GA_MOST_POPULAR, TERMINAL_GA_MOST_POPULAR_SUMMARY, TERMINAL_GA_PDP_SESSION_STATS, TERMINAL_GA_MOST_ATTACHED_FAILURES, TERMINAL_GA_MOST_PDP_SESSION_SETUP_FAILURES, TERMINAL_GA_MOST_MOBILITY_ISSUES, TERMINAL_GA_MOST_SWAPPED_TO, TERMINAL_GA_MOST_SWAPPED_FROM, TERMINAL_MOST_POPULAR, TERMINAL_MOST_POPULAR_SUMMARY, TERMINAL_MOST_ATTACHED_FAILURES, TERMINAL_PDP_SESSION_STATS, TERMINAL_MOST_PDP_SESSION_SETUP_FAILURES, TERMINAL_MOST_MOBILITY_ISSUES, TERMINAL_MOST_SWAPPED_TO, TERMINAL_MOST_SWAPPED_FROM, CAUSE_CODE_TABLE_CC, CAUSE_CODE_TABLE_SCC, SUBSCRIBER_DETAILS_TABLE, SUBSCRIBER_DETAILS_TABLE_PTMSI, SUBSCRIBER_DETAILS_TABLE_CS, SUBSCRIBER_DETAILS_TABLE_MSISDN_CS, SUBSCRIBER_DETAILS_TABLE_WCDMA_CFA, SAC, RECUR_ERR_NETWORK, RECUR_ERR_SUBSCRIBER, CS_CAUSE_CODE_TABLE_CC, CS_CAUSE_CODE_TABLE_SCC, TERMINAL_HIGHEST_DATAVOLUME_VIEW, TERMINAL_GA_HIGHEST_DATAVOLUME_SUMMARY, CS_SUB_BI_FAILED_EVENTS, CS_SUB_BI_BUSY_HOUR, CS_SUB_BI_BUSY_DAY, CS_SUB_BI_BUSY_CELL, CS_SUB_BI_BUSY_TERMINALS, CS_TERMINAL_MOST_ATTACHED_FAILURES, CS_TERMINAL_MOST_MOBILITY_ISSUES, CS_TERMINAL_MOST_PDP_SESSION_SETUP_FAILURES, CS_TERMINAL_MOST_POPULAR, CS_TERMINAL_GA_MOST_ATTACHED_FAILURES, CS_TERMINAL_GA_MOST_MOBILITY_ISSUES, CS_TERMINAL_GA_MOST_PDP_SESSION_SETUP_FAILURES, CS_TERMINAL_GA_MOST_POPULAR,
        //Call Failures
        TERMINAL_GROUP_ANALYSIS_RAN_WCDMA_CFA_CALL_DROPS, TERMINAL_GROUP_ANALYSIS_RAN_WCDMA_CFA_CALL_SETUP_FAILURES, TERMINAL_ANALYSIS_RAN_WCDMA_CALLFAILURE_MOST_SETUP_FAILURES, TERMINAL_ANALYSIS_RAN_WCDMA_CALLFAILURE_MOST_DROPS, LTE_CALL_FAILURE_CAUSE_CODE_TABLE_CC, LTE_HANDOVER_FAILURE_CAUSE_CODE_TABLE_CC, GSM_CAUSE_CODE_TABLE_CC, GSM_CAUSE_CODE_TABLE_SCC, DISCONNECTION_CODE_TABLE_DC_WCDMA,
        //Handover Failures
        TERMINAL_ANALYSIS_RAN_WCDMA_HFA_SOHO, TERMINAL_ANALYSIS_RAN_WCDMA_HFA_HSDSCH, TERMINAL_ANALYSIS_RAN_WCDMA_HFA_IFHO, TERMINAL_ANALYSIS_RAN_WCDMA_HFA_IRAT, TERMINAL_GROUP_ANALYSIS_RAN_WCDMA_HFA_SOHO, TERMINAL_GROUP_ANALYSIS_RAN_WCDMA_HFA_HSDSCH, TERMINAL_GROUP_ANALYSIS_RAN_WCDMA_HFA_IFHO, TERMINAL_GROUP_ANALYSIS_RAN_WCDMA_HFA_IRAT, SUB_BI_FAILED_EVENTS_CFA, CAUSE_CODE_TABLE_CC_WCDMA, CAUSE_CODE_TABLE_SCC_WCDMA, CAUSE_CODE_TABLE_CC_WCDMA_HFA, CAUSE_CODE_TABLE_SCC_WCDMA_HFA
    };

    /* Temp fix until all toolbar buttons are imagebuttons - lists all toolbar buttons that are imageButtons */
    public List<String> imageButtonType = new ArrayList<String>(asList("btnTime", "btnProperties", "btnRefresh", "btnBack", "btnForward", "btnCC",
            "btnSCC", "btnDC", "btnSubscriberDetails", "btnSubscriberDetailsPTMSI", "btnRecur", "btnSac", "btnKPI", "btnSubscriberDetailsWcdmaCFA",
            "btnHideShowLegend", "btnExport"));

    public Map<String, EventType> supportedEventTypes;

    public String id, name, style, toolTip, toolBarButtons, disableWhen, visibleWhen = EMPTY_STRING;

    /*
     * extras for toolbar menu items which can make new server calls inside there window (e.g. switching from roaming by operator to country) This is
     * assuming the "needSearchParam" and enabled params will be controlled by base window the toolbar is launching windows inside)
     */
    public ToolBarURLChangeDataType urlInfo = null;

    public boolean isEnabled = true;

    public boolean isVisible = true; // default for nearly all components

    public boolean isToggle = false;

    public EventType eventID = null;

    public boolean isImageButton = false;

    public List<ToolbarPanelInfoDataType> subItems = new ArrayList<ToolbarPanelInfoDataType>();

    /* determines if a separator icon follows the display of this ToolBarItem */
    public boolean hasSeperator = false;

    public ToolbarPanelInfoDataType() {
        initEventEnum();

    }

    private void initEventEnum() {
        if (supportedEventTypes == null) {
            supportedEventTypes = new HashMap<String, EventType>();

            for (final EventType sVal : EventType.values()) {
                supportedEventTypes.put(sVal.toString(), sVal);
            }
        }

    }
}
