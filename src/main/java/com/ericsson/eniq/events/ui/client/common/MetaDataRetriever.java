/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.ericsson.eniq.events.common.client.PerformanceUtil;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.events.MetaDataReadyEvent;
import com.ericsson.eniq.events.ui.client.gin.EniqEventsUIClientModule;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.user.client.Timer;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Class that contacts the services layer to retrieve the JsonObjectWrapper information
 * Once it receives the meta data from the services layer, it sets it in the
 * MetaReader singleton
 * 
 * @author eemecoy
 * @author eeicmsy
 * @since Sept 2010
 * 
 */
/**
 * @author xkancha
 * @since 2012
 * 
 */
public class MetaDataRetriever extends RequestCallbackImpl {

    private static final int WAITING_PERIOD = 100;

    private static final Logger LOGGER = Logger.getLogger(MetaDataRetriever.class.getName());

    private final IMultiMetaDataHelper multiMetaDataHelper = MainEntryPoint.getInjector().getMultiMetaDataHelper();

    /**
     * Defines the relative path on the services tier where the meta data is located
     */
    @Inject
    @Named(EniqEventsUIClientModule.UI_METADATA_PATH)
    public static String uiMetadataPath;

    private final String metaDataPath;

    /**
     * Class to Retrieve meta data (default).
     * 
     * Determines location of the web service that will return the meta data that defines the GUI and initiates a call to this url for the meta data
     * 
     * When successful (and data returned) fire meta data ready event onto event bus to be picked up by MainPresenter to populate the UI
     * 
     * All server calls go though ServerComms (server communication mechanism)
     * 
     * 
     * @param eventBus - standard event bus used thoughout UI
     * 
     * @param metaDataPath - URL path to mata data to support multiple paths If empty or null using default UI_METADATA_PATH
     */

    MetaDataRetriever(final EventBus eventBus, String metaDataPath) {

        super(EMPTY_WIN_ID, eventBus, null);

        if (metaDataPath == null || metaDataPath.isEmpty()) {
            metaDataPath = uiMetadataPath;
        }

        this.metaDataPath = metaDataPath;
        final String metaURL = getEniqEventsServicesURIFromReadLoginSessionProperties() + metaDataPath; // NOPMD
                                                                                                        // by
                                                                                                        // eeicmsy
                                                                                                        // on
                                                                                                        // 25/02/11
                                                                                                        // 16:01
        /*
         * To redirect user to logout page when UI session has timed out and user refreshed the browser instead of mouse click on UI page
         */
        if (!(metaURL.contains(NULL) || metaURL.contains(ENIQ_EVENTS_UI))) {
            getServerCommHandler().makeSerialServerRequest(EMPTY_WIN_ID, metaURL, EMPTY_STRING);
        } else {
            logout();
        }
    }

    /*
     * redirect UI to logout page
     */
    public static native int logout()
    /*-{
    	$wnd.onLogout();
    }-*/;

    //
    //  Override RequestCallbackImpl for use with ServerComms
    //
    /*
     * HTTP RequestError reading meta data on load
     * 
     * @see com.google.gwt.http.client.RequestCallback#onError(com.google.gwt.http. client.Request, java.lang.Throwable)
     */
    @Override
    public void onError(final Request request, final Throwable ex) {
        LOGGER.log(Level.WARNING, "Failed to load", ex);
        displayMetaServerError(ex, null);
    }

    /*
     * HTTP Request - Handle reading meta data on load
     * 
     * @see com.google.gwt.http.client.RequestCallback#onResponseReceived(com.google .gwt.http.client.Request, com.google.gwt.http.client.Response)
     */
    @Override
    public void onResponseReceived(final Request request, final Response response) {
        PerformanceUtil.getSharedInstance().clear("onResponseReceived");
        if (STATUS_CODE_OK == response.getStatusCode()) {
            final String jsonMetadata = response.getText();
            final JsonObjectWrapper metaData = createMetaDataObject(jsonMetadata);
            multiMetaDataHelper.setMetaDataFromServer(metaDataPath, metaData);
            initiateRendering();
            // @see MainView
            //eventBus.fireEvent(new MetaDataRetrievedEvent()); // there may be more types
            // of meta data

        } else {
            LOGGER.warning("HTTP Error Code " + response.getStatusCode() + ": receiving layout metadata!");
            displayMetaServerError(null, response);
        }
        PerformanceUtil.getSharedInstance().logTimeTaken("Time taken in parsing metadata : ", "onResponseReceived");
    }

    private int counter = 1;

    private void initiateRendering() {
        final Timer t = new Timer() {

            @Override
            public void run() {
                initiateRendering();
            }
        };
        if (userPreferencesAreReady()) {
            PerformanceUtil.getSharedInstance().logCurrentTime("user setting loaded..");
            eventBus.fireEvent(new MetaDataReadyEvent());
        } else {
            PerformanceUtil.getSharedInstance().logCurrentTime("user setting not loaded..");
            t.schedule(counter * WAITING_PERIOD);
            counter++;
        }
    }

    /*
     * Extracted the help with testing of thise class.
     */
    protected boolean userPreferencesAreReady() {
        return UserPreferencesRetriever.isUserPreferenceReady();
    }

    /* show meta read exception on screen */
    private void displayMetaServerError(final Throwable ex, final Response response) {
        final Map<String, String> result = getTitleAndMessage(ex, response);
        MessageDialog.get().show(result.get("title"), result.get("message"), MessageDialog.DialogType.ERROR);
    }

    protected Map<String, String> getTitleAndMessage(final Throwable ex, final Response response) {
        final Map<String, String> messageDialogContent = new HashMap<String, String>();

        if (response != null) {
            //Get the title and the message from the response.
            final String text = response.getText();
            final String[] responseText = text.split(":");

            //Set the title...
            if (responseText.length == 1) {
                messageDialogContent.put("title", "HTTP CODE " + response.getStatusCode());
            } else {
                messageDialogContent.put("title", responseText[0]);
            }

            //Set the message...
            if (responseText.length == 1) {
                messageDialogContent.put("message", responseText[0]);
            } else {
                messageDialogContent.put("message", responseText[1]);
            }
        }
        if (ex != null) {
            messageDialogContent.put("title", "Failure receiving initial meta data from server!");
            messageDialogContent.put("message", ex.getMessage());
        }
        return messageDialogContent;
    }

    /*
     * extracted out to get under unit test
     * 
     * @param jsonMetadata
     * 
     * @return
     */
    JsonObjectWrapper createMetaDataObject(final String jsonMetadata) {
        try {
            return new JsonObjectWrapper(JSONUtils.parse(jsonMetadata).isObject());
        } catch (final JSONException e) {
            LOGGER.log(Level.INFO, "Parsing text: " + jsonMetadata);
            throw e;
        }
    }

    /*
     * Important all server communication goes through the same methods
     * 
     * @return ServerComm handler to hit code that will be used for all server communication
     */
    @SuppressWarnings("unchecked")
    ServerComms getServerCommHandler() {
        return new ServerCommHandler();
    }

    /*
     * extracted to get under test
     * 
     * @return
     */
    String getEniqEventsServicesURIFromReadLoginSessionProperties() {
        return ReadLoginSessionProperties.getEniqEventsServicesURI();
    }

    /**
     * Inner class to reuse ServerComms class code for connecting to the server Over-ride method supplying the callback object to suit special case
     * for meta Data retrieval
     */
    private class ServerCommHandler extends ServerComms {

        public ServerCommHandler() {
            super(eventBus);
        }

        /*
         * Completely over-ridden RequestCallbackImpl with this class to support being able to use ServerComms methods for find host etc..
         * 
         * @see com.ericsson.eniq.events.ui.client.common.ServerComms#getRequestCallbackImpl (java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public RequestCallbackImpl getRequestCallbackImpl(final MultipleInstanceWinId multiWinID, final String requestData) {

            return MetaDataRetriever.this;
        }
    }
}
