package com.ericsson.eniq.events.ui.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created with IntelliJ IDEA.
 * User: emauoco
 * Date: 06/11/12
 */
public interface SetupLicensesEventHandler extends EventHandler {

    public void onLicensesEvent(SetupLicensesEvent licensesEvent);

}
