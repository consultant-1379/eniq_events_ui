/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.charts;

import java.util.Map;

import com.ericsson.eniq.events.common.client.datatype.ChartDataType;
import com.ericsson.eniq.events.common.client.datatype.ChartDrillDownInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.ChartDrillDownParameterInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.ChartItemDataType;
import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.ui.client.charts.window.ChartWindowPresenter;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.datatype.*;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.ericsson.eniq.events.ui.client.events.ChangeChartGridEvent;
import com.ericsson.eniq.events.ui.client.events.GraphDrillDownLaunchEvent;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.highcharts.client.ChartConstants.*;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;

/**
 * Drill down listener on charts (all types)
 * <p/>
 * NOTE : When used the caller must ensure specific chart components are enabled,
 * e.g <code>line.setEnableEvents(true)</code> we want listener to to something.
 * <p/>
 * Charts are to be drillable my element so the meta data (drillDownWindowType) added to
 * chart element items
 * <p/>
 * Clicking on enabled chart element must send server call to create new grid in same window
 * Required sample formats of call
 * <p/>
 * /EniqEventsServices/TERMINAL/GROUP_ANALYSIS/MOST_POPULAR?display=grid&time=30&groupname=val
 * /EniqEventsServices/TERMINAL/GROUP_ANALYSIS/MOST_POPULAR_EVENT_SUMMARY?display=grid&time=30&groupname=val
 * <p/>
 * /EniqEventsServices/SUBSCRIBER/SUBBI/APN?time=10080&display=grid&imsi=454090057976716&apn=<APN>
 * <p/>
 * i.e. this listener must identify the chart we are in and append the selected element into the call ("groupname" above)
 *
 * @author eeicmsy
 * @since June 2010
 */
public class ChartElementDrillDownListener implements IChartElementDrillDownListener {

    /* static used to convert EventID string */
    private static ToolbarPanelInfoDataType toolbarPanelInfoDataType;

    private final IMetaReader metaReader = MainEntryPoint.getInjector().getMetaReader();

    private EventType eventID;

    private final EventBus eventBus;

    private final ChartWindowPresenter win;

    /* exposing for text purposes only */ ToolBarURLChangeDataType newWindowInfo;

    /**
     * Click listener on elements in charts (all types)
     *
     * @param eventBus - the default event bus
     * @param win      - chart presenter
     */
    public ChartElementDrillDownListener(final EventBus eventBus, final ChartWindowPresenter win) {
        this.win = win;
        this.eventBus = eventBus;

    }

    MultipleInstanceWinId getMultipleInstanceWinId() {
        return win.getMultipleInstanceWinId();
    }

    @Override
    public void setEventId(final String id) {
        this.eventID = getToolbarPanelInfoDataType().supportedEventTypes.get(id);
    }

    /* note ce.getChartConfig().getText() would be the elementName - e.g. "Event Failures",
     * which may be useful to know later (the ce.getValues() is only the current number, i.e. no good for drilldown
     * hence the extra parameter added to pull out x axis info (e.g. a group name like 
     * "Sony", "Nokia" etc) 
     */
    @Override
    public void drillDown(final Map<String, String> drillDownDataMap) {

        /* gather chart element specific information required for call 
         * i.e. clicked element and drilldown metadata for element*/

        final String chartElementClicked = drillDownDataMap.get(CHART_ELEMENT_SELECTED_KEY);
        final String drillDownWindowType = drillDownDataMap.get(CHART_ELEMENT_DRILLDOWN_KEY);
        final String chartMetaID = drillDownDataMap.get(CHART_META_ID);
        final StringBuilder chartQueryParms = new StringBuilder();

        if (chartMetaID != null) { // prevent drills broken in GXT

            //get chart queryString parameters if any

            final ChartDataType currentChartData = win.getChartConfigInfo(chartMetaID);
            if (currentChartData != null && currentChartData.itemInfo != null) {
                for (final ChartItemDataType chartItem : currentChartData.itemInfo) {
                    if (!chartItem.queryParam.isEmpty()) {
                        chartQueryParms.append(AMPERSAND);
                        chartQueryParms.append(chartItem.queryParam);
                        chartQueryParms.append(EQUAL_STRING);
                        chartQueryParms.append(drillDownDataMap.get(chartItem.queryParam));
                    }
                }
            }
        }
        drillDownChart(chartElementClicked, drillDownWindowType, chartQueryParms.toString());
    }

    private void drillDownChart(final String chartElementClicked, final String drillDownWindowType,
            final String queryParameters) {

        if (drillDownWindowType != null && drillDownWindowType.length() > 0) {
            final ChartDrillDownInfoDataType drillDownData = getChartDrillDownInfo(drillDownWindowType);

            //Determine if this is launched in a seperate window, if it is take a different route
            final String drillType = drillDownData.getDrillType();
            //Extra query parameters added as static value
            final StringBuilder extraQueryParam = new StringBuilder();

            for (final ChartDrillDownParameterInfoDataType queryParameter : drillDownData.getParameters()) {
                final String parameterName = queryParameter.getParameterName();
                final String paramVal = queryParameter.getParameterValue();
                final boolean isFixedType = queryParameter.isFixedType();

                // determine if the parameter needs to be added to the query url
                if ((parameterName != null) && !parameterName.isEmpty() && isFixedType) {
                    extraQueryParam.append(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR);
                    extraQueryParam.append(parameterName);
                    extraQueryParam.append(EQUAL_STRING);
                    extraQueryParam.append(paramVal);
                }
            }
            if (drillType.equalsIgnoreCase(LAUNCH_CHART) || drillType.equalsIgnoreCase(LAUNCH_GRID)) {
                eventBus.fireEvent(new GraphDrillDownLaunchEvent(win.getTabOwnerId(), drillDownData, chartElementClicked, win.getSearchData(), win.getWindowStyle(), new TimeInfoDataType(win.getWindowTimeDate()), queryParameters + extraQueryParam.toString(), win.getSearchFieldUser()));

                return;
            }

            /* keeping same URL but appending parameters for chart drilldowns 
             * (e.g. appending "groupname=") */
            win.setWindowType(MetaMenuItemDataType.Type.GRID); // so returns grid 

            newWindowInfo = new ToolBarURLChangeDataType();

            /* adding functionality supporting specific URL per chart element
             * if not present use the parents URL 
             */
            final String specificURLForElement = drillDownData.getWsURL();

            newWindowInfo.url = specificURLForElement.isEmpty() ? win.getWsURL() : specificURLForElement; // keeps existing URL 
            newWindowInfo.displayType = OUT_BOUND_GRID_DISPLAY_PARAM;
            newWindowInfo.windowType = win.getWindowType().toString();
            newWindowInfo.drillDownWindowType = drillDownData.getDrillTargetDisplayId(); // different grid per element clicked
            newWindowInfo.toolbarType = drillDownData.getDrillToolbarType();
            newWindowInfo.maxRowsParam = drillDownData.getMaxRowsParam();

            /* display not in BaseWindow #getResusableURLParams at this time */
            newWindowInfo.addOutBoundParameter(DISPLAY_TYPE_PARAM, OUT_BOUND_GRID_DISPLAY_PARAM);
            newWindowInfo.addOutBoundParameter(drillDownData.getChartClickedURLParam(), chartElementClicked);

            final MultipleInstanceWinId multiWinId = getMultipleInstanceWinId();

            /* fire event to display grid in same window based on new URL info, 
             * @see com.ericsson.eniq.events.ui.client.events.handlers.ChartGridChangeEventHandler*/
            eventBus.fireEvent(new ChangeChartGridEvent(multiWinId, eventID, newWindowInfo, chartElementClicked));

        } else {
            /* this will not occur if do job right - i.e. disable chart elements from 
             * action listener (see #setEnableEvents) when no drilldown in meta data
             */
            MessageDialog.get().show("Information", "Drill down not supported for this chart element", MessageDialog.DialogType.INFO);
        }
    }

    ChartDrillDownInfoDataType getChartDrillDownInfo(final String drillDownWindowType) {
        return metaReader.getChartDrillDownWindowType(drillDownWindowType);
    }

    /*
     * note keep static ensure just one map being created here
     * inside ToolbarPanelInfoDataType internals
     */
    private static ToolbarPanelInfoDataType getToolbarPanelInfoDataType() {
        if (toolbarPanelInfoDataType == null) {
            toolbarPanelInfoDataType = new ToolbarPanelInfoDataType();
        }
        return toolbarPanelInfoDataType;
    }

    /**
     * Only called by javascript function for external selenium testing.
     */
    @Override
    public void performChartClickManually(final String chartElementClicked, final String drillDownWindowType) {
        drillDownChart(chartElementClicked, drillDownWindowType, EMPTY_STRING);
    }

    /**
     * @return the newWindowInfo
     */
    protected ToolBarURLChangeDataType getNewWindowInfo() {
        return newWindowInfo;
    }
}