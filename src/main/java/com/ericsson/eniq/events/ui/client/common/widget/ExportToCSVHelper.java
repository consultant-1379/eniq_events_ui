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
package com.ericsson.eniq.events.ui.client.common.widget;

import static com.ericsson.eniq.events.common.client.CommonConstants.*;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.Date;

import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.common.client.service.RestfulRequestBuilder;
import com.ericsson.eniq.events.common.client.url.Url;
import com.ericsson.eniq.events.common.client.url.UrlUtils;
import com.ericsson.eniq.events.ui.client.common.ReadLoginSessionProperties;
import com.ericsson.eniq.events.ui.client.common.comp.BreadCrumbMenuItem;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 
 * Helper handling export to CSV from export grid button press, with new view ref every time
 * 
 * 
 * Passes EniqEventsCSV.jsp?url= call with existing widget parameters and changes the time parameter to always be in "dataTimeFrom=" and "dataTimeTo="
 * format rather than the relative "time=" format.
 * 
 * (moving code that was in NavigationHelper)
 * 
 */
public class ExportToCSVHelper {

    /**
     * handle the selection of the export button from the GUI toolbar navigation to export data in grid to csv file
     * 
     * Convert to format like :
     * http://atrcxb1020.athtem.eei.ericsson.se:18080/EventEventsUI/EniqEventsCSV.jsp?url=NETWORK/RANKING_ANALYSIS&display=grid
     * &type=RNC&tzOffset=+0000&maxRows=50&dataTimeFrom=1297956840000&dataTimeTo=1297958640000&maxRows=0&tzOffset=+0000
     * 
     * @param viewRef - grid view reference
     */
    public void exportDataToCSV(final IExtendedWidgetDisplay viewRef) {
        final Frame csvFrame = getCSVFrame();
        csvFrame.setVisible(false);

        // TODO ideally view references should not have had state information at all
        //      Presenter is for business logic not view

        String widgetURLParams;
        TimeInfoDataType time;
        String wsURL;
        final StringBuffer urlParams = new StringBuffer();

        final BreadCrumbMenuItem breadCrumb = viewRef.getPresenter().getCurrentBreadCrumbMenuItem();
        if (breadCrumb != null) {
            widgetURLParams = breadCrumb.getWidgetURLParameters();
            if (widgetURLParams.equals(EMPTY_STRING)) {
                widgetURLParams = viewRef.getWidgetSpecificURLParamsForCSV();
                if (widgetURLParams.equals(EMPTY_STRING)) {
                    widgetURLParams = viewRef.getPresenter().getSearchURLParameters();
                }
            }
            time = breadCrumb.getTimeData();
            wsURL = breadCrumb.getURL();
            if (!(widgetURLParams.contains(DISPLAY_TYPE_PARAM))) {
                widgetURLParams = appendDisplayParameterToWidgetURLParams(widgetURLParams, urlParams);
            }
        } else {
            // this must not be empty (cleared) at time of calling
            widgetURLParams = viewRef.getWidgetSpecificURLParamsForCSV();
            if (widgetURLParams.equals(EMPTY_STRING)) {
                widgetURLParams = viewRef.getPresenter().getSearchURLParameters();
            }
            time = viewRef.getPresenter().getWindowTimeDate();
            wsURL = viewRef.getViewSettings().getWsURL();
            if (!(widgetURLParams.contains(DISPLAY_TYPE_PARAM))) {
                widgetURLParams = appendDisplayParameterToWidgetURLParams(widgetURLParams, urlParams);
            }
        }
        widgetURLParams = removeRedundantParamsFromURL(widgetURLParams);
        final String localUrl = wsURL.replace(getEniqEventsServicesURI(), EMPTY_STRING);

        final Url parameters = UrlUtils.parse(widgetURLParams);
        parameters.setParameter(DATA_TIME_FROM_QUERY_PARAM_WITHOUT_EQUALS, time.dataTimeFrom);
        parameters.setParameter(DATA_TIME_TO_QUERY_PARAM_WITHOUT_EQUALS, time.dataTimeTo);
        parameters.setParameter(TIME_ZONE_PARAM_WITHOUT_EQUALS, DateTimeFormat.getFormat(CommonParamUtil.TIME_ZONE_DATE_FORMAT).format(new Date()));
        parameters.setParameter(RestfulRequestBuilder.USER_NAME_PARAM_NO_EQUALS, getLoginUserName());
        parameters.setParameter(URL_QUERY_PARAM_WITHOUT_EQUALS, localUrl);
        // Since we don't need maxRows for CSV export, we just remove it
        parameters.removeParameter(MAX_ROWS_URL_PARAM_WITHOUT_EQUALS);

        //...we do however require maxRows=0 if exporting CSV, otherwise Services panics and limits the rows to 5000.
        parameters.setParameter(MAX_ROWS_URL_PARAM_WITHOUT_EQUALS, CSV_MAX_ROWS_VALUE);

        csvFrame.setUrl(getHostPageBaseURL() + encode("EniqEventsCSV.jsp" + UrlUtils.build(parameters)));
        getRootPanel().add(csvFrame);
    }

    private String appendDisplayParameterToWidgetURLParams(String widgetURLParams, final StringBuffer urlParams) {
        urlParams.append(FIRST_URL_PARAM_DELIMITOR);
        urlParams.append(DISPLAY_TYPE_PARAM);
        urlParams.append(OUT_BOUND_GRID_DISPLAY_PARAM);
        urlParams.append(widgetURLParams);
        widgetURLParams = urlParams.toString();
        return widgetURLParams;
    }

    /*
     * Removes the dateFrom and dateTo that were appended to the original url for requesting the json data for the grid
     * 
     * Also any extra tzOffset and maxRows parameters if present, that will be replacing with CSV ones
     * 
     * @param widgetURLParams sample : ?time=30&key=SUM&whaterever=somethingelse
     * 
     * @return formated string ?key=SUM&whaterever=somethingelse
     */
    String removeRedundantParamsFromURL(String widgetURLParams) {

        for (final String timeTypeParameter : ALL_TIME_PARAMS) {
            // recursively change widgetURLParams
            widgetURLParams = removeParamFromURL(widgetURLParams, timeTypeParameter);
        }
        return widgetURLParams;
    }

    private final String removeParamFromURL(String widgetURLParams, final String paramEqualsToRemove) {

        if (widgetURLParams.contains(paramEqualsToRemove)) {
            final int startPt = widgetURLParams.indexOf(paramEqualsToRemove);

            int endPt = widgetURLParams.indexOf(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR, startPt) + 1;
            if (endPt == 0) { // last item
                endPt = widgetURLParams.length();
            }

            widgetURLParams = (startPt > 0) ? widgetURLParams.replace(widgetURLParams.substring(startPt, endPt), EMPTY_STRING) : widgetURLParams;

            if (widgetURLParams.endsWith(EMPTY_STRING + CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR)) { // last one
                widgetURLParams = widgetURLParams.substring(0, widgetURLParams.length() - 1);
            }
        }
        return widgetURLParams;
    }

    ////////////////////    expose for junit

    Frame getCSVFrame() {
        return new Frame();
    }

    String getEniqEventsServicesURI() {
        return ReadLoginSessionProperties.getEniqEventsServicesURI();
    }

    String getHostPageBaseURL() {
        return GWT.getHostPageBaseURL();
    }

    String encode(final String s) {
        return CommonParamUtil.encode(s);
    }

    RootPanel getRootPanel() {
        return RootPanel.get();
    }

    String getLoginUserName() {
        return CommonParamUtil.getLoginUserName();
    }
}
