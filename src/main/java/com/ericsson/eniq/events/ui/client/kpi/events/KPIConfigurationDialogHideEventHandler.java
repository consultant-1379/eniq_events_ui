/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.kpi.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author eaajssa
 * @since 2012
 *
 */
public interface KPIConfigurationDialogHideEventHandler extends EventHandler {

    void onHide(KPIConfigurationDialogHideEvent hideEvent);

}
