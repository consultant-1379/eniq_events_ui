/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.eniq.events.ui.client.groupmanagement.component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.json.IJSONArray;
import com.ericsson.eniq.events.common.client.json.IJSONObject;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.common.client.service.RestfulRequestBuilder.State;
import com.ericsson.eniq.events.ui.client.common.Constants;
import com.ericsson.eniq.events.ui.client.common.ReadLoginSessionProperties;
import com.ericsson.eniq.events.ui.client.common.ServerComms;
import com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementUtils;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListTextItem;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog.DialogType;
import com.ericsson.eniq.events.widgets.client.mask.MaskHelper;
import com.extjs.gxt.ui.client.core.XDOM;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.common.client.CommonConstants.CELL_LIST_OFFSET_LIMIT;
import static com.ericsson.eniq.events.common.client.CommonConstants.ERROR_DESCRIPTION;
import static com.ericsson.eniq.events.common.client.CommonConstants.SUCCESS;
import static com.ericsson.eniq.events.common.client.CommonConstants.TRUE;
import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.CALLBACK_PARAM;
import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.LOADING;
import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.LOADING_MSG_ENDING_DOTS;


/**
 * @author ekurshi
 * @since 2012
 */

public class RemoteSuggestOracle extends GroupOracle {

    private static final int SUGGESTION_TIMER_DELAY = 500;

    private final String url;

    private final ServerComms serverComms;

    private Timer t;

    private final String liveloadRoot;

    private Callback callback;

    private final Element maskElement;

    private final MaskHelper maskHelper;

    private final String displayName;

    private final RemoteSuggestOracleRequestCallback requestCallback = new RemoteSuggestOracleRequestCallback();

    /**
     * @param eventBus
     * @param url
     * @param liveloadRoot
     * @param displayName
     * @param maskElement
     */
    public RemoteSuggestOracle(final EventBus eventBus, final String url, final String liveloadRoot,
            final String displayName, final Element maskElement) {
        super();
        this.url = url;
        this.liveloadRoot = liveloadRoot;
        this.displayName = displayName;
        this.maskElement = maskElement;
        maskHelper = new MaskHelper();
        serverComms = new ServerComms(eventBus);
    }

    private class RemoteSuggestOracleRequestCallback implements RequestCallback {

        /* (non-Javadoc)
         * @see com.google.gwt.http.client.RequestCallback#onResponseReceived(com.google.gwt.http.client.Request, com.google.gwt.http.client.Response)
         */
        @Override
        public void onResponseReceived(com.google.gwt.http.client.Request req,
                                       com.google.gwt.http.client.Response response) {
            maskHelper.unmask();
            final JSONValue jsonValue;
            final String data = response.getText();
            if (data.contains("transId0"))  {
                final String jsonStr = GroupManagementUtils.removeCallbackParam(data);
                jsonValue = GroupManagementUtils.parseJsonString(jsonStr);
            }
            else{
                jsonValue = GroupManagementUtils.parseJsonString(data);
            }
            if (jsonValue == null) {
                MessageDialog messageDialog = new MessageDialog();
                messageDialog.getElement().getStyle().setZIndex(Math.max(Constants.ENIQ_EVENTS_BASE_ZINDEX + 3,
                        XDOM.getTopZIndex()));
                messageDialog.show(ERROR, EMPTY_RESPONSE, MessageDialog.DialogType.ERROR);
                return;
            }
            if (checkData(jsonValue)) {
                final JsonObjectWrapper json = new JsonObjectWrapper(jsonValue.isObject());
                final IJSONArray searchObjectArray = json.getArray(liveloadRoot);

                if (jsonValue != null) {
                    final List<GroupListItem> candidates = new ArrayList<GroupListItem>();
                    for (int i = 0; i < searchObjectArray.size(); i++) {
                        final IJSONObject obj = searchObjectArray.get(i);
                        final String candidate = obj.getString("id");
                        if (candidate.length() == 0 || candidate.matches(WHITESPACE_STRING)) {//TODO why matches with whitespace
                            continue;
                        }
                        candidates.add(new GroupListTextItem(candidate));
                    }

                    final Request request = getRequest();
                    // Respect limit for number of choices.
                    final int numberTruncated = Math.max(0, candidates.size() - request.getLimit());
                    for (int i = candidates.size() - 1; i > request.getLimit(); i--) {
                        candidates.remove(i);
                    }

                    // Convert candidates to suggestions if required.
                    final List<GroupSuggestion> suggestions;
                    if (isFormattingRequired()) {
                        suggestions = convertToFormattedSuggestion(request.getQuery(), candidates);
                    } else {
                        suggestions = getSuggestionsFromStringItems(candidates);
                    }

                    final Response suggestionsResponse = new Response(suggestions);
                    suggestionsResponse.setMoreSuggestionsCount(numberTruncated);

                    callback.onSuggestionsReady(request, suggestionsResponse);
                }
            }
        }

        /**
         * Copied from JSON utils with changes due to the fact that the expected message format is only when there is an error for this
         * liveload, otherwise the successful result JSON is in GXT format
         *
         * @param jsonValue
         *
         * @return
         */
        private boolean checkData(final JSONValue jsonValue) {
            final JsonObjectWrapper metaData = new JsonObjectWrapper(jsonValue.isObject());

            final String success = metaData.getString(SUCCESS);
            /** No success parameter in response, assume response is correct and in the GXT expected format **/
            if (success == null || success.isEmpty()) {
                return true;
            }
            if (!TRUE.equalsIgnoreCase(success)) {

                String error = metaData.getString(ERROR_DESCRIPTION);
                // server has been known to pass success false with no error message
                if (error.length() == 0) {
                    error = UNDEFINED_ERROR_FROM_SERVER_MESSAGE; // success flag failed
                }
                MessageDialog messageDialog = new MessageDialog();
                messageDialog.getElement().getStyle().setZIndex(Math.max(Constants.ENIQ_EVENTS_BASE_ZINDEX + 3,
                        XDOM.getTopZIndex()));
                messageDialog.show("Error", error, MessageDialog.DialogType.ERROR);
                return false;

            }
            return true;
        }

        /* (non-Javadoc)
         * @see com.google.gwt.http.client.RequestCallback#onError(com.google.gwt.http.client.Request, java.lang.Throwable)
         */
        @Override
        public void onError(com.google.gwt.http.client.Request request, Throwable exception) {
            maskHelper.unmask();
            final MessageDialog messageDialog = new MessageDialog();
            messageDialog.setGlassEnabled(true);
            messageDialog.getElement().getStyle().setZIndex(Math.max(Constants.ENIQ_EVENTS_BASE_ZINDEX + 3,
                    XDOM.getTopZIndex()));
            messageDialog.show(displayName + " Liveload Failure", exception.getMessage(), DialogType.ERROR);
        }

    }

    /* (non-Javadoc)
    * @see com.google.gwt.user.client.ui.MultiWordSuggestOracle#requestSuggestions(com.google.gwt.user.client.ui.SuggestOracle.Request, com.google.gwt.user.client.ui.SuggestOracle.Callback)
    */
    @Override
    public void requestSuggestions(final Request request, final Callback callback) {
        setRequest(request);
        final String query = normalizeSearch(request.getQuery());
        request.setLimit(CELL_LIST_OFFSET_LIMIT);
        this.callback = callback;
        if (t != null) {
            t.cancel();
        }
        t = new Timer() {

            @Override
            public void run() {
                maskHelper.mask(maskElement, LOADING + displayName + "s" + LOADING_MSG_ENDING_DOTS,
                        maskElement.getOffsetHeight());
                serverComms.requestData(State.GET, url, getRequestData(query), requestCallback);
            }
        };
        t.schedule(SUGGESTION_TIMER_DELAY);

    }

    /* (non-Javadoc)
    * @see com.google.gwt.user.client.ui.MultiWordSuggestOracle#requestDefaultSuggestions(com.google.gwt.user.client.ui.SuggestOracle.Request, com.google.gwt.user.client.ui.SuggestOracle.Callback)
    */
    @Override
    public void requestDefaultSuggestions(final Request request, final Callback callback) {
        setRequest(request);
        request.setLimit(CELL_LIST_OFFSET_LIMIT);
        this.callback = callback;

        /** Execute in deferred command due to height calculation incorrect in current event loop **/
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                maskHelper.mask(maskElement, LOADING + displayName + "s" + LOADING_MSG_ENDING_DOTS,
                        maskElement.getOffsetHeight());
                serverComms.requestData(State.GET, url, getRequestData(""), requestCallback);
            }
        });

    }

    /**
     * @param query
     *
     * @return
     */
    private String getRequestData(final String query) {
        final StringBuilder sb = new StringBuilder();
        sb.append("?callback=" + CALLBACK_PARAM);
        if (query != null && !query.isEmpty()) {
            sb.append("&query=" + query);
        }
        sb.append("&maxRows=" + getMaxRowsProperty(CommonConstants.ENIQ_EVENTS_LIVE_LOAD_COUNT));
        sb.append("&sortField=null");
        sb.append("&sortDir=NONE");
        sb.append("&offset=0");
        sb.append("&start=0");
        sb.append("&limit=" + CELL_LIST_OFFSET_LIMIT);
        sb.append("&tzOffset=" + DateTimeFormat.getFormat(CommonParamUtil.TIME_ZONE_DATE_FORMAT).format(new Date()));
        return sb.toString();
    }

    String getMaxRowsProperty(final String propertyKey) {
        return ReadLoginSessionProperties.getMaxRowsValue(propertyKey);
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }

    @Override
    public void add(final GroupListItem item) {
        // TODO Auto-generated method stub
    }

}
