package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.google.gwt.event.shared.GwtEvent;

/**
 * GWT Event that can be added to the EventBus for fail condition
 *
 * @author eeicmsy
 * @since Jan 2010
 *
 */
public class FailedEvent extends GwtEvent<FailedEventHandler> {

    public final static Type<FailedEventHandler> TYPE = new Type<FailedEventHandler>();

    private final Throwable exception;

    private final String requestData;

    private final MultipleInstanceWinId multiWinID;

    /**
     * Event to add to event bus in case of failure
     *
     * @param multiWinID    - id of window been updated  - can contain multi-instance window information 
     * @param requestData   - can be null if could not even create the request
     * @param exception     - exception from server or on create
     */
    public FailedEvent(final MultipleInstanceWinId multiWinID, final String requestData, final Throwable exception) {
        this.exception = exception;
        this.requestData = requestData;
        this.multiWinID = multiWinID;

    }

    @Override
    protected void dispatch(final FailedEventHandler handler) {
        handler.handleFail(multiWinID, requestData, exception);
        
    }

    @Override
    public Type<FailedEventHandler> getAssociatedType() {
        return TYPE;
    }

}
