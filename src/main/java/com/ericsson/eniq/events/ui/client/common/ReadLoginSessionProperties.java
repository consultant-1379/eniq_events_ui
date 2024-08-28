/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

/**
 * Class Calls natively to window properties set up from glassfish server
 *
 * @author estepdu
 * @since May 2010
 */
public abstract class ReadLoginSessionProperties {

    /**
     * Fetch revision information (e.g. for about box)
     *
     * @return revision information
     */
    public static native String getEniqEventsUIVersion()
    /*-{
		return $wnd.appVersion;
    }-*/;

    /**
     * Fetch copyright text (e.g. for about box)
     *
     * @return copy right text
     */
    public static native String getEniqEventsUICoypright()
    /*-{
		return $wnd.appCopyright;
    }-*/;

    /**
     * Fetch URI location where we will receive UI Meta Data
     * <p/>
     * Try to set the appServicesURI variable in EniqEventsUI.html instead of modifying this method.
     *
     * @return URI location
     */

    public static native String getEniqEventsServicesURI()
    /*-{
         return $wnd.appServicesURI;
    }-*/;

    /**
     * Fetch the request timeout time. The maximum time allowed before the
     * request timeout.
     *
     * @return Time in milliseconds
     */
    public static native int getRequestTimeoutTime() /*-{
		return $wnd.appRequestTimoutTime;
    }-*/;

    /**
     * @param maxRowsParam A JNDI key
     * @return The value stored in JNDI for the key given as a parameter
     */
    public static native String getMaxRowsValue(String maxRowsParam) /*-{
		return $wnd.maxRowCounts[maxRowsParam];
    }-*/;

    /**
     * TODO - get this from properties or some config
     * @return
     */
    public static boolean getDevConfigOn() {
        return false;
    }

}
