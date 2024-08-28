/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.common.client.service.DataServiceImpl;
import com.ericsson.eniq.events.common.client.service.IDataService;
import com.ericsson.eniq.events.common.client.service.RestfulRequestBuilder;
import com.ericsson.eniq.events.common.client.service.RestfulRequestBuilder.ContentType;
import com.ericsson.eniq.events.common.client.service.RestfulRequestBuilder.State;
import com.ericsson.eniq.events.ui.client.EniqEventsServiceProperties;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.events.ServerRequestEvent;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.web.bindery.event.shared.EventBus;

/**
 * A base class for ALL communication with server.
 * ALL server calls have to go though here
 * (e.g. to support cancel functionality, but generally
 * to avoid repeated code)
 * <p/>
 * Can use directly or subclass, e.g
 *
 * @author eeicmsy
 * @see com.ericsson.eniq.events.ui.client.common.MetaDataRetriever
 * @see com.ericsson.eniq.events.ui.client.common.comp.BaseWinServerComms
 * @see com.ericsson.eniq.events.ui.client.common.widget.TwoColumnGridDialogPresenter
 * @see com.ericsson.eniq.events.ui.client.search.GroupTypeSearchComponent
 * @see com.ericsson.eniq.events.ui.client.search.LiveLoadTypeUnreadyHelper
 * @since Feb 2011
 */
public class ServerComms {

    private static final Logger LOGGER = Logger.getLogger(ServerComms.class.getName());

    private final Map<String, MultipleInstanceWinId> idMap = new HashMap<String, MultipleInstanceWinId>();

    private final EventBus eventBus;

    private final IDataService dataService;

    /*
    * random number will apply to each request header,
    * such that when send a cancel request
    * (also with this number) the
    * server will know what request to cancel
    */
    private String requestId = EMPTY_STRING;

    /**
     * Server communication class
     * (is intended that can be over-riden,
     * e.g. as MetaDataRetriver, BaseWinServerComms do)
     *
     * @param eventBus - the same old event bus we pass around everywhere
     */
    public ServerComms(final EventBus eventBus) {
        this.eventBus = eventBus;
        dataService = new DataServiceImpl(new EniqEventsServiceProperties());
    }

    /**
     * Backwards Compatible for Eniq Events components that can only get ServerComms at the moment.
     * pass request to new Data Service Interface implementation
     * @param method
     * @param url
     * @param requestData
     * @param callback
     * @return
     * @deprecated See {@link IDataService}     
     */
    @Deprecated
    public String requestData(final RestfulRequestBuilder.State method, final String url, final String requestData,
            final RequestCallback callback) {
        return dataService.performRemoteOperation(method, url, requestData, callback);
    }

    /**
     * Backwards Compatible for Eniq Events components that can only get ServerComms at the moment.
     * pass request to new Data Service Interface implementation
     * @param method
     * @param url
     * @param requestData
     * @param callback
     * @return
     * @deprecated See {@link IDataService}     
     */
    @Deprecated
    public String requestData(final RestfulRequestBuilder.State method, final String url, final String requestData,
            final RequestCallback callback, final ContentType contentType) {
        return dataService.performRemoteOperation(method, url, requestData, callback, contentType);
    }

    /**
         * Make GET Request to server. ALL GET server calls MUST go through this method
         * <p/>
         * This MUST be only place in UI code where
         * deciding to be RPC or HttpRequest, etc.
         *
         * @param multiWinID  - id of window been updated  - can contain multi-instance window information
         * @param wsURL       - web server URL (not parameters), e.g.
         *                    http://atrcxb1020.athtem.eei.ericsson.se:18080/EniqEventsServices/CANCEL
         * @param requestData - Extra parameters for url if applicable (else pass empty string),
         *                    e.g. "?time=30&whatever=somethingElse
         * @deprecated See {@link IDataService}                    
         */
    @Deprecated
    public void makeServerRequest(final MultipleInstanceWinId multiWinID, final String wsURL, final String requestData) {
        doDeferredRequest(State.GET, multiWinID, wsURL, requestData, ContentType.X_WWW_FORM_URLENCODED);
    }

    public void makeSerialServerRequest(final MultipleInstanceWinId multiWinID, final String wsURL,
            final String requestData) {
        doSerialRequest(State.GET, multiWinID, wsURL, requestData, ContentType.X_WWW_FORM_URLENCODED);
    }

    /**
     * Make POST to server. ALL server calls MUST go through this method
     * <p/>
     * This MUST be only place in UI code where
     * deciding to be RPC or HttpRequest, etc.
     *
     * @param multiWinID  - id of window been updated  - can contain multi-instance window information
     * @param wsURL       - web server URL (not parameters), e.g.
     *                    http://atrcxb1020.athtem.eei.ericsson.se:18080/EniqEventsServices/CANCEL
     * @param contentType - Type of the Request Content i.e. content-type/xml, content-type/json
     * @deprecated See {@link IDataService}     
     */
    @Deprecated
    public void makeServerPost(final MultipleInstanceWinId multiWinID, final String wsURL, final String requestData,
            final ContentType contentType) {
        doDeferredRequest(State.POST, multiWinID, wsURL, requestData, contentType);
    }

    /* 
    * Make call to server. 
    *
    * @param multiWinID  - id of window been updated  - can contain multi-instance window information
    * @param wsURL       - web server URL (final not parameters), e.g.
    *                    http://atrcxb1020.athtem.eei.ericsson.se:18080/EniqEventsServices/CANCEL
    * @param requestData - Extra parameters for url if applicable (else pass empty string),
    *                    e.g. "?time=30&whatever=somethingElse
    */
    private void doRequest(final State method, final MultipleInstanceWinId multiWinID, final String wsURL,
            final String requestData, final ContentType contentType) {
        eventBus.fireEvent(new ServerRequestEvent(multiWinID, ServerRequestEvent.State.FIRED));

        final RequestCallbackImpl callback = getRequestCallbackImpl(multiWinID, requestData);
        requestId = dataService.performRemoteOperation(method, wsURL, requestData, callback, contentType);
        idMap.put(requestId, multiWinID);
    }

    /**
     * Want to send a call to the server to cancel the
     * current on going server call (presses the cancel button
     * we supply on the loading message, will now make a
     * request down to the server to cancel the ongoing request)
     * <p/>
     * Assumes will not need to pass requestId (from Request) because
     * only call this serverComms can take is the cancel whilst masked
     */
    public void sendCancelRequestCall() {
        final String cancelURL = getEniqEventsServicesURI() + RestfulRequestBuilder.CANCEL_REQUEST_URI;

        // for server to do whatever it has to do
        dataService.sendCancelRequest(cancelURL, new RequestCallback() {
            @Override
            public void onResponseReceived(final Request request, final Response response) {
            }

            @Override
            public void onError(final Request request, final Throwable exception) {
                LOGGER.log(Level.WARNING, "Cancel request failed.");
            }
        });

        // this should ensure no succeed or failure results come back to grid-chart
        final MultipleInstanceWinId winId = idMap.get(requestId);
        idMap.clear();

        eventBus.fireEvent(new ServerRequestEvent(winId, ServerRequestEvent.State.CANCELLED));
    }

    /**
     * Method exposed to support over-riding the used Callback object
     * used in HTTP request
     *
     * @param multiWinID  -  win id with multiple instance window support
     * @param requestData -  Optional - pass in request parameter data (can be null)
     * @return Callback object for HTTP request
     */
    protected RequestCallbackImpl getRequestCallbackImpl(final MultipleInstanceWinId multiWinID,
            final String requestData) {
        return new RequestCallbackImpl(multiWinID, eventBus, requestData);
    }

    String getEniqEventsServicesURI() {
        return ReadLoginSessionProperties.getEniqEventsServicesURI();
    }

    String encodeData(final State method, final String requestData) {
        if (State.GET.equals(method)) {
            return CommonParamUtil.encode(requestData);
        } else if (State.PUT.equals(method)) {
            return CommonParamUtil.encode(requestData).replaceAll("&amp;", "%26");
        }
        return requestData;
    }

    /**
     * Return scheduler implementation. Used to allow JUnit override unfortunately
     *
     * @return
     */
    protected Scheduler getScheduler() {
        return Scheduler.get();
    }

    /**
     * @param multiWinID
     * @param wsURL
     * @param requestData
     * @deprecated See {@link IDataService}     
     */
    @Deprecated
    public void makePutServerRequest(final MultipleInstanceWinId multiWinID, final String wsURL,
            final String requestData) {
        doDeferredRequest(State.PUT, multiWinID, wsURL, requestData, ContentType.X_WWW_FORM_URLENCODED);
    }

    private void doSerialRequest(final State method, final MultipleInstanceWinId multiWinID, final String wsURL,
            final String requestData, final ContentType contentType) {
        doRequest(method, multiWinID, wsURL, requestData, contentType);
    }

    /**
     * Defer the call to allow any tidying up i.e. removal of event handlers etc, to complete before calling the server
     *
     * @param contentType *
     */
    private void doDeferredRequest(final State method, final MultipleInstanceWinId multiWinID, final String wsURL,
            final String requestData, final ContentType contentType) {
        getScheduler().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                doRequest(method, multiWinID, wsURL, requestData, contentType);
            }
        });
    }
}
