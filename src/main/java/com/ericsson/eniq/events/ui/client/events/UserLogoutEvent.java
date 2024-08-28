package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.LicenseInfoDataType;
import com.google.gwt.event.shared.GwtEvent;

import java.util.List;

public class UserLogoutEvent extends GwtEvent<UserLogoutEventHandler> {

    public final static Type<UserLogoutEventHandler> TYPE = new Type<UserLogoutEventHandler>();

    @Override
    protected void dispatch(final UserLogoutEventHandler handler) {
        handler.onUserLogoutEvent();
    }

    @Override
    public Type<UserLogoutEventHandler> getAssociatedType() {
        return TYPE;
    }
}
