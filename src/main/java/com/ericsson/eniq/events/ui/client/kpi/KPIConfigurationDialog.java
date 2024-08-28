/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.kpi;

import com.ericsson.eniq.events.ui.client.kpi.events.KPIConfigurationDialogHideEvent;
import com.ericsson.eniq.events.ui.client.kpi.events.KPIConfigurationDialogHideEventHandler;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * @author eaajssa
 * @since 2012
 *
 */
public class KPIConfigurationDialog {

    private final Dialog dialog = new Dialog();

    private final HandlerManager handlerManager = new HandlerManager(this);

    public KPIConfigurationDialog() {
        dialog.setHeading("KPI Alarm Configuration");
        dialog.setButtons("");
        dialog.setWidth(345);
        dialog.setAutoHeight(true);
        dialog.addListener(Events.Hide, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(final BaseEvent event) {
                if (Events.Hide.equals(event.getType())) {
                    handlerManager.fireEvent(new KPIConfigurationDialogHideEvent());
                }
            }
        });
    }

    public void show() {
        dialog.show();

    }

    public void hide() {
        dialog.hide();
    }

    public void clear() {
        dialog.removeAll();
    }

    public void setContent(final Widget content) {
        dialog.removeAll();
        dialog.add(content);
    }

    public HandlerRegistration addHideEventHandler(final KPIConfigurationDialogHideEventHandler handler) {
        return handlerManager.addHandler(KPIConfigurationDialogHideEvent.TYPE, handler);
    }
}
