/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.json.IJSONArray;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.events.FailedEvent;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.JSON_ROOTNODE;

/**
 * @author ericker
 * @since 2010
 */
public abstract class JSONUtils {

    /**
     * Central Utlity for parsing JSON
     * (In case ever want to change say from
     * Lenient to Strict)
     * <p/>
     * GXT 2.2. deprecated is parse method ans
     * introduces other options
     *
     * @param s string to parse
     *
     * @return The superclass of all JSON value types
     */
    public static JSONValue parse(String s) {
        return JSONParser.parseLenient(replaceApostrophe(s));
    }

    /**
     * Central Utlity for parsing JSON - specifically set to Lenient
     * (if fails using strict)
     *
     * @param s string to parse
     *
     * @return The superclass of all JSON value types
     */
    public static JSONValue parseLenient(final String s) {
        return JSONParser.parseLenient(s);
    }

    /**
     * Specifically for toggle button press from updated grid back to chart
     * <p/>
     * Check response is suitable for chart display.
     * We can toggle a chart to a grid and toggle back. However if changed say the time when
     * was a chart and server passes back grid data and then you toggle back to chart with this
     * new response object then the axis format on the chart will be wrong.
     * <p/>
     * This method is just checking for presence of at least one axis statement
     * e.g.
     * {"success":"true","errorDescription":"","yaxis_min":"0","yaxis_max":"73800","data":[
     * <p/>
     * -
     * (WE ARE TOLD SERVICES WILL PASS CHART DATA FOR GRID SOMETIMES (kpi, event volume)
     * but not always unfortunately
     * TODO in future maybe services should for all)
     *
     * @param responseObjText inside com.google.gwt.http.client.Response
     *
     * @return true if JSON response looks like chart data, else false
     */
    public static boolean isChartData(final String responseObjText) {
        if (responseObjText == null) {
            return false;
        }
        // hope fastest performance way to exlude checking body
        final int endIndex = responseObjText.indexOf(JSON_ROOTNODE); // "data"
        if (endIndex != -1) {
            final String headerSection = responseObjText.substring(0, endIndex);
            return (headerSection.indexOf(Y_AXIS_MIN_VAL) != -1);

        }
        return false;

    }

    /**
     * Utility (static) to check data array from server Response object based on the assumption that
     * return JSON in return  is similar to as follows:
     * <pre>
     * {"success":"true","errorDescription":"","data":[{"1":"ACTIVATE","2":"2009-11-14 16:44:13.0"}]}
     *
     * This method will show dialog boxes to user and return false if
     * error description is present in the data
     *
     * @return false if error (no table rendered)
     */
    public static boolean checkData(final JSONValue parsedResult, final EventBus eventBus,
            final MultipleInstanceWinId multiWinID) {
        final JsonObjectWrapper metaData = new JsonObjectWrapper(parsedResult.isObject());

        final String success = metaData.getString(SUCCESS);
        if (!CommonConstants.TRUE.equalsIgnoreCase(success)) {

            String error = metaData.getString(ERROR_DESCRIPTION);
            // server has been known to pass success false with no error message
            if (error.length() == 0) {
                error = UNDEFINED_ERROR_FROM_SERVER_MESSAGE; // success flag failed
            } else if (error.contains(LICENSE_ERROR_DESCRIPTION_SUBSTRING)) {
                error = LICENSE_ERROR; // feature not licensed
            }
            //raise the server failed response
            eventBus.fireEvent(new FailedEvent(multiWinID, EMPTY_STRING, new Exception(error))); // NOPMD by eeicmsy on 19/11/10 17:34
            return false;
        }
        return true;
    }

    /**
     * Utility (static) to check data array from server Response object based on the assumption that
     * return JSON in return  is similar to as follows:
     * <pre>
     * {"success":"true","errorDescription":"","data":[{"1":"ACTIVATE","2":"2009-11-14 16:44:13.0"}]}
     *
     * This method will show dialog boxes to user and return false if
     * error description is present in the data
     *
     * @return false if error (no table rendered)
     */
    public static boolean checkData(final JSONValue parsedResult) {

        final JsonObjectWrapper metaData = new JsonObjectWrapper(parsedResult.isObject());

        final String success = metaData.getString(SUCCESS);
        if (!CommonConstants.TRUE.equalsIgnoreCase(success)) {

            String error = metaData.getString(ERROR_DESCRIPTION);
            // server has been known to pass success false with no error message
            if (error.length() == 0) {
                error = UNDEFINED_ERROR_FROM_SERVER_MESSAGE; // success flag failed
            }
            displayErrorDialog(error);
            return false;

        }
        return true;
    }

    private static void displayErrorDialog(final String error) {
        final MessageDialog messageDialog = MessageDialog.get();
        messageDialog.show("Error", error, MessageDialog.DialogType.ERROR);
    }

    /**
     * Utility (static) to check data array from server Response object based on the assumption that
     * return JSON in return  is similar to as follows:
     * <pre>
     * {"success":"true","errorDescription":"","data":[{"1":"ACTIVATE","2":"2009-11-14 16:44:13.0"}]}
     *
     * This method will show dialog boxes to user and return false if
     * error description is present in the data
     *
     * @return false if error (no table rendered)
     */
    public static boolean checkData(final JSONValue parsedResult, final boolean checkExTac) {

        final JsonObjectWrapper metaData = new JsonObjectWrapper(parsedResult.isObject());

        final String success = metaData.getString(SUCCESS);
        if (!CommonConstants.TRUE.equalsIgnoreCase(success)) {

            String error = metaData.getString(ERROR_DESCRIPTION);
            // server has been known to pass success false with no error message
            if (error == null || error.length() == 0) {
                error = UNDEFINED_ERROR_FROM_SERVER_MESSAGE; // success flag failed
            }
            if (checkExTac && error.equalsIgnoreCase(UNKNOWN_ERROR)) {
                error = TAC_NOT_SAVED;
            }
            displayErrorDialog(error);

            return false;
        }
        return true;
    }

    public static boolean isDataEmpty(final JSONValue parsedResult) {
        final JsonObjectWrapper metaData = new JsonObjectWrapper(parsedResult.isObject());
        final IJSONArray data = metaData.getArray(JSON_ROOTNODE);

        return data == null || data.size() == 0;
    }

    /**
     * Replace the ' (Apostrophe) with ?(Modifier letter prime). This is to handle the cases where input
     * text from Services contain '. the ' in the input text breaks the Javascript String handling.
     * @param inputString
     * @return
     */
    public static String replaceApostrophe(final String inputString){
        return inputString.replaceAll("'", "\u02B9");
    }
}