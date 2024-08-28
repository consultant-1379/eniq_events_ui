/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.RequestCallbackImpl;
import com.ericsson.eniq.events.ui.client.common.ServerComms;
import com.ericsson.eniq.events.ui.client.datatype.LiveLoadTypeDataType;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.events.FailedEvent;
import com.ericsson.eniq.events.ui.client.events.FailedEventHandler;
import com.ericsson.eniq.events.ui.client.events.SucessResponseEvent;
import com.ericsson.eniq.events.ui.client.events.SucessResponseEventHandler;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestTimeoutException;
import com.google.gwt.http.client.Response;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

/**
 * Menu items fed from JsonObjectWrapper readup, 
 * support the Live load Type selection via a menuitem. 
 * HOWEVER in this instance we do not have types coded into the meta data 
 * so there is more work to do to fetch the types..
 * 
 * @author eeicmsy
 * @since March 2010
 *
 */
public class LiveLoadTypeUnreadyHelper {

    private final static Logger LOGGER = Logger.getLogger(LiveLoadTypeUnreadyHelper.class.getName());

    private final IMetaReader metaReader = MainEntryPoint.getInjector().getMetaReader();

    private final String valParam;

    private final String submitTip;

    private final String emptyText;

    private final String winMetaSupport;

    private final String style;

    private final String typeURL;

    private final static String QUERY_ID = "LiveLoadTypeUnreadyHelper";

    public PairedTypeSearchComponent searchComponentReference;

    /*
     * Adding ability to switch menu items from CS to PS and back - meaning new search field creation 
     * on task bar. Because this load is slow (and see no reason not to change cache), not refreshing
     * existing types (so making static)
     */
    static final List<LiveLoadTypeMenuItem> typeMenuItems = new ArrayList<LiveLoadTypeMenuItem>();

    private final ServerResponseHandler liveloadTypesResultHandler = new ServerResponseHandler();

    private final String permanentType;

    /*
     * hard-code default type selection (for live-loaded types) into position 0 index
     */
    private final static int DEFAULT_TYPE_INDEX = 0;

    /*
     * tab owner id (needed to respond correctly to 
     * event on event bus) when search component shared across tabs
     * (particularly toggling from group component to single component)
     */
    private final String tabOwnerId;

    /*
     * Though we are not a window, reusing same signature
     * for server calls
     */
    private MultipleInstanceWinId multiWinId = null;

    public final boolean needServerCall = typeMenuItems.isEmpty();// (made static to avoid refreshing on PS/CS switch on assumtion this (Terminal tab) is only user of this)

    /**
     * Utility class for MetaReader.PAIRED_LIVE_TYPE, i.e.  when decide to use meta in such a way that "types" part of 
     * the paired type is not loaded directly into the metadata at GUI launch.
     * 
     * 
     * In this case, the meta data provides a URL only for type which we must 
     * load in order to return something like below:
     * 
     * 
     * { "data" : [
     * {
     *    "id" : "Nokia",
     *    "liveLoadURL" : "http://localhost/dummy/Nokia.php"  
     *    },
     *    {
     *    "id" : "Sony Ericsson",
     *    "liveLoadURL" : "http://localhost/dummy/Sony.php"
     *    },
     *    {
     *    "id" : "Moterolla",
     *    "liveLoadURL" : "http://localhost/dummy/Moterolla.php"
     *    },
     *    {
     *    "id" : "Samseung",
     *    "liveLoadURL" : "http://localhost/dummy/Samseung.php"
     *    }
     *    ]
     * }
     * 
     * We can use that to develop full MenuTaskBarPairedType object at later time 
     * when data ready. 
     * 
     * @param tabOwnerId    Unique identification of where this search component is (typicaly
     *                       tab owner) to support listening to events on bus
    * @param valParam       value passed to URL (along with type), e.g. "node"

     * @param submitTip      Tooltip on submit button
     * @param emptyText      Use as holder for empty text to appear in paired search field
     * @param style          Icon style for menu item
     * @param typeURL        URL containing to fetch types information in required format
     * @param permanentType  (hack for terminal types) when server wants same type to be passed in 
     *                       the URL call no matter what type is picked in the search comopnent 
     * @param winMetaSupport All items in laoding unready types (terminal makes) senariowill have same winMetaSupport
     *                       (e.g. windows support CS and PS when select a type from dtop down)
     */
    public LiveLoadTypeUnreadyHelper(final String tabOwnerId, final String valParam, final String submitTip,
            final String emptyText, final String style, final String typeURL, final String permanentType,
            final String winMetaSupport) {

        this.tabOwnerId = tabOwnerId;
        this.valParam = valParam;

        this.submitTip = submitTip;
        this.emptyText = emptyText;
        this.style = style;
        this.typeURL = typeURL;
        this.permanentType = permanentType; // may not be set. e.g. for type=TAC outbound
        this.winMetaSupport = winMetaSupport;

    }

    /**
     * Return  a "half ready" PairedTypeSearchComponent (and store reference) 
     * for caller to use as a holder to layout the search component on the GUI. 
     * Initiate call to server to fetch the rest. 
     * 
     * @param eventBus           - Default event bus needed to  send server request
     * @param isUsingMenuForType - true if use a Menu component to display the types, 
     *                             else use a combobox component to display the types
     * @param typeEmptyText      - when displaying type as a combobox may need something to put in for empty text 
     * 
     * @return           Enough for MetaReader to be happy to layout but will still need to 
     *                   wait for result of server call to populate with data
     */

    public PairedTypeSearchComponent createSearchFieldPairedType(final EventBus eventBus,
            final boolean isUsingMenuForType, final String typeEmptyText) {

        /* initialise as empty until have result from server call */
        searchComponentReference = new PairedTypeSearchComponent(tabOwnerId, typeMenuItems, DEFAULT_TYPE_INDEX,
                submitTip, isUsingMenuForType, typeEmptyText);

        searchComponentReference.setPermanentValParam(valParam);
        searchComponentReference.setPermanentType(permanentType);
        searchComponentReference.setEmptyTextValue(emptyText);
        eventBus.addHandler(SucessResponseEvent.TYPE, liveloadTypesResultHandler);
        eventBus.addHandler(FailedEvent.TYPE, new FailedResponseHandler());

        return searchComponentReference;
    }

    /* access for junit to avoid method */
    public void makeServerRequestForTypesData(final EventBus eventBus) {
        getServerCommHandler(eventBus).makeServerRequest(getMultipleInstanceWinId(), typeURL, EMPTY_STRING);
    }

    private MultipleInstanceWinId getMultipleInstanceWinId() {
        if (multiWinId == null) {
            multiWinId = new MultipleInstanceWinId(tabOwnerId, QUERY_ID); //not for multi-instance no need for search data
        }
        return multiWinId;
    }

    /*
     * Method called after server success result
     * Now we have the types,  so let search component currently with empty (or dummy) types update.
     * (method access exposed for unit test only) 
     */
    void handleServerResponseByPopulatingTypes(final Response response) {
        typeMenuItems.clear();
        final Collection<LiveLoadTypeDataType> liveLoadTypes = readLiveLoadTypesFromMetaData(response);

        for (final LiveLoadTypeDataType liveLoadType : liveLoadTypes) {
            /* reuse name for id - server has enough to do */
            typeMenuItems.add(new LiveLoadTypeMenuItem(liveLoadType.id, EMPTY_STRING, liveLoadType.id, // NOPMD by eeicmsy on 15/07/10 18:52
                    liveLoadType.url, style, emptyText, winMetaSupport));
        }
        /* update search component "half created" earlier */
        setUpTypeMenuOnSearchComponent();

    }

    /* access for junit to avoid */
    void setUpTypeMenuOnSearchComponent() {
        searchComponentReference.setTypeMenuItems(typeMenuItems);
    }

    /* access for junit to avoid */
    Collection<LiveLoadTypeDataType> readLiveLoadTypesFromMetaData(final Response response) {
        final String jsonMetadata = response.getText();
        return metaReader.getLiveLoadTypes(jsonMetadata);
    }

    /*
     * Addition to ensure all server communication goes though one class
     * 
     * @return ServerComms  server communication helper
     */
    @SuppressWarnings("unchecked")
    ServerComms getServerCommHandler(final EventBus eventBus) {
        return new ServerCommHandler(eventBus);
    }

    /**
     * Handle success response when event bus fired a SucessResponseEvent
     * Indicating server has returned with some success result  
     */
    private final class ServerResponseHandler implements SucessResponseEventHandler {

        @Override
        public void handleResponse(final MultipleInstanceWinId multiWinID, final String requestData,
                final Response response) {

            // guard
            if (!LiveLoadTypeUnreadyHelper.this.getMultipleInstanceWinId().isThisWindowGuardCheck(multiWinID)) {
                return;
            }

            LiveLoadTypeUnreadyHelper.this.handleServerResponseByPopulatingTypes(response);
            searchComponentReference.maskTypesComboBox(false);

        }
    }

    /**
     * Handle success response when event bus fired a SucessResponseEvent
     * Indicating server has returned with some success result  
     */
    private final class FailedResponseHandler implements FailedEventHandler {

        /* (non-Javadoc)
         * @see com.ericsson.eniq.events.ui.client.events.FailedEventHandler#handleFail(java.lang.String, java.lang.String, java.lang.String, java.lang.Throwable)
         */
        @Override
        public void handleFail(final MultipleInstanceWinId multiWinID, final String requestData,
                final Throwable exception) {
            // guard
            if (!LiveLoadTypeUnreadyHelper.this.getMultipleInstanceWinId().isThisWindowGuardCheck(multiWinID)) {
                return;
            }
            searchComponentReference.maskTypesComboBox(false);

            final MessageDialog errorDialog = new MessageDialog();
            errorDialog.setGlassEnabled(true);
            errorDialog.show(LOAD_DATA_MESSAGE, exception.getMessage(),
                    MessageDialog.DialogType.ERROR);

        }
    }

    /**
     * Inner class to reuse ServerComms class code for connecting to the server 
     * Over-ride method supplying the callback object to 
     * suit special case for meta Data retrieval
     */
    @SuppressWarnings("unchecked")
    private class ServerCommHandler extends ServerComms {

        private final EventBus eventBus;

        public ServerCommHandler(final EventBus eventBus) {
            super(eventBus); // super class handles null display
            this.eventBus = eventBus;
        }

        /* Completely over-ridden RequestCallbackImpl with this class to support
         * being able to use ServerComms methods for find host etc..
         * @see com.ericsson.eniq.events.ui.client.common.ServerComms#getRequestCallbackImpl(java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public RequestCallbackImpl getRequestCallbackImpl(final MultipleInstanceWinId multiWinID,
                final String requestData) {

            return new LiveLoadRequestCallbackImpl(getMultipleInstanceWinId(), eventBus, EMPTY_STRING);

        }

    }

    class LiveLoadRequestCallbackImpl extends RequestCallbackImpl {

        public LiveLoadRequestCallbackImpl(final MultipleInstanceWinId multiWinID, final EventBus eventBus,
                final String requestData) {
            super(multiWinID, eventBus, requestData);
        }

        @Override
        public void onError(final Request request, final Throwable exception) {
            if (exception instanceof RequestTimeoutException) {
                // handle a request timeout
                final MessageDialog errorDialog = new MessageDialog();
                errorDialog.setGlassEnabled(true);
                errorDialog.show(TIMEOUT_EXCEPTION, exception.getMessage(), MessageDialog.DialogType.ERROR);
            } else {
                LOGGER.log(Level.WARNING, " Error sending a request to the services for live load: ", exception);
            }
        }

    }
}
