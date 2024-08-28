package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired when new HTTP request is about to be sent.
 *
 * @author edmibuz
 */
public class ServerRequestEvent extends GwtEvent<ServerRequestEventHandler> {

    public static final Type<ServerRequestEventHandler> TYPE = new Type<ServerRequestEventHandler>();

    private final MultipleInstanceWinId multiWinID;
    private final State state;

    public static enum State {
        FIRED, CANCELLED
    }

    public ServerRequestEvent(final MultipleInstanceWinId multiWinID, final State state) {
        this.multiWinID = multiWinID;
        this.state = state;
    }

    public MultipleInstanceWinId getMultiWinID() {
        return multiWinID;
    }

    @Override
    public Type<ServerRequestEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final ServerRequestEventHandler handler) {
        if (State.FIRED.equals(state)) {
            handler.onRequestFired(this);
        } else if (State.CANCELLED.equals(state)) {
            handler.onRequestCancelled(this);
        }
    }

}
