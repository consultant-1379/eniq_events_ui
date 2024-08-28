/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events.handlers;

import com.ericsson.eniq.events.common.client.mvp.Display;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.widget.AbstractBaseWindowDisplay;
import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.events.FailedEventHandler;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessageType;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.google.gwt.http.client.RequestTimeoutException;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;
import static com.ericsson.eniq.events.ui.client.common.Constants.TIMEOUT_EXCEPTION;

/**
 * Deal with error response from server for
 * grid or chart population. Window registored
 *
 * This is the main failure response we will use for all windows (grids and charts)
 * receiving communication from the server
 *
 *
 * @see com.ericsson.eniq.events.ui.client.charts.window.ChartWindowPresenter
 * @see com.ericsson.eniq.events.ui.client.common.widget.EventGridPresenter
 *
 * @author eeicmsy
 * @since April 2010
 *
 */
public class ServerFailedResponseHandler implements FailedEventHandler {

    private final static Logger LOGGER = Logger.getLogger(ServerFailedResponseHandler.class.getName());

    /*
     * View reference (MVP pattern)
     */
    private final Display display;

    private final MetaMenuItem windowData;


    /*
     * Id with multi-instance support
     */
    private final MultipleInstanceWinId multiWinID;

    /**
     * Handle failure with server call
     * @param multiWinID - id of window been updated  - can contain multi-instance window information 
     * @param display    - View reference (MVP pattern)
     * @param windowData - pass window data may need to clear on failure
     */
    public ServerFailedResponseHandler(final MultipleInstanceWinId multiWinID, final Display display,
            final MetaMenuItem windowData) {

        this.multiWinID = multiWinID;
        this.display = display;
        this.windowData = windowData;
        // can not trust windowData.isSearchFieldUser()
    }

    @Override
    public void handleFail(final MultipleInstanceWinId multiWinId, final String requestData, final Throwable exception) {

        // guards
        if (!this.multiWinID.isThisWindowGuardCheck(multiWinId)) {
            return;
        }

        final MessageDialog errorDialog = new MessageDialog();
        String exceptionMessage = exception.getMessage();
        String title = "Error";
        if (exception instanceof RequestTimeoutException) {
            // handle a request timeout
            title = TIMEOUT_EXCEPTION;
        } else {
            LOGGER.log(Level.WARNING, "Failed to populate " + multiWinID.getWinId() + " window  with server error", exception);
        }
        if (display instanceof AbstractBaseWindowDisplay) {
            displayErrorInWindow(exceptionMessage, title);
        } else {
            //display is not a window so just show error dialog
            errorDialog.show(title, exceptionMessage, MessageDialog.DialogType.ERROR);
        }
        completeRendering();
        windowData.setWidgetSpecificParams(EMPTY_STRING);

        if (display instanceof IExtendedWidgetDisplay) {
            ((IExtendedWidgetDisplay) display).updateWidgetSpecificURLParams(EMPTY_STRING);
        }

    }

    private void displayErrorInWindow(String exceptionMessage, String title) {
        display.stopProcessing();
        ((AbstractBaseWindowDisplay) display).showErrorMessage(ComponentMessageType.ERROR, title, exceptionMessage);
        int height = ((AbstractBaseWindowDisplay) display).getWindow().getBody().getHeight();
        if (height > 0) ((AbstractBaseWindowDisplay) display).setErrorMessageHeight(height + "px");
    }

    /**
     * Allows for implementation specific rendering to take place.
     */
    protected void completeRendering() {
        // Should be overridden by instantiating class
    }
}
