/*
 * -----------------------------------------------------------------------
 *      Copyright (C) ${YEAR} LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;

/**
 * This class manages any notifications application presents to user.
 * This includes error dialogs.
 * 
 * @author edmibuz
 *
 */
public final class NotificationService {

    private final static Logger LOGGER = Logger.getLogger(NotificationService.class.getName());

    public void showErorDialog(final String message, final Throwable e) {
        final MessageDialog messageDialog = new MessageDialog();
        messageDialog.setGlassEnabled(true);
        messageDialog.show(message, e.getMessage(), MessageDialog.DialogType.ERROR);
        LOGGER.log(Level.SEVERE, message, e);
    }

}
