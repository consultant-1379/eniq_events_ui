package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.http.client.Response;

/**
 * GWT Event that can be added to the EventBus for success condition.
 * General success event for windows making server calls, e.g
 * we will use for successful grid data, fetch, etc.
 *
 * @author eeicmsy
 * @since Feb 2010
 * @see SucessResponseEventHandler
 *
 */
// TODO: Sucess -> Success
public class SucessResponseEvent extends GwtEvent<SucessResponseEventHandler> {

    public static final Type<SucessResponseEventHandler> TYPE = new Type<SucessResponseEventHandler>();

    private final String requestData;

    private final Response response;

    private final MultipleInstanceWinId multiWinID;

    /**
     * We are trying to avoid any google impacts server side so sending plain
     * requests with JSON
     *
     * @param multiWinID    - id of window been updated  - can contain multi-instance window information
     * @param requestData       - can be null - request data sent to server (in the gwt.http.client.Request)
     * @param response      - HTTP response
     */
    public SucessResponseEvent(final MultipleInstanceWinId multiWinID, final String requestData, final Response response) {

        this.multiWinID = multiWinID;
        this.requestData = requestData;
        this.response = response;

    }

    @Override
    protected void dispatch(final SucessResponseEventHandler handler) {
        handler.handleResponse(multiWinID, requestData, response);

    }

    @Override
    public Type<SucessResponseEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder(256);
        sb.append("SuccessResponseEvent{");
        sb.append("multiWinID=").append(multiWinID);
        sb.append(", requestData='").append(requestData).append('\'');
        sb.append('}');
        return sb.toString();
    }
}