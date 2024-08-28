package com.ericsson.eniq.events.ui.client.common.listeners;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.json.IJSONArray;
import com.ericsson.eniq.events.common.client.json.IJSONObject;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.ui.client.common.*;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.SearchGroupModelData;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.widgets.client.button.ImageButton;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.ID;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.JSON_ROOTNODE;

/**
 * Listener class to EXC_TAC button on Rankings Tab. To list all the TAC groups
 * within the EXCLUSIVE_TAC group. If group does not exist, display appropriate
 * error message.
 * 
 * @author ebelcha
 * @since May 2011
 */
public class ExclusiveTacButtonListener extends SelectionListener<MenuEvent> implements ClickHandler {

    private static final Logger LOGGER = Logger.getLogger(ExclusiveTacButtonListener.class.getName());

    private final EventBus eventBus;

    private final String tabOwnerId;

    private final IMetaReader metaReader = MainEntryPoint.getInjector().getMetaReader();

    private final String id;

    private MessageDialog message;

    private ImageButton eventSource;

    /**
     * @param bus
     *          - eventBus singleton required for presenters
     * @param tabId
     *          - tab on which to launch dialog
     * @param itemId
     *          - id of the button clicked
     */
    public ExclusiveTacButtonListener(final EventBus bus, final String tabId, final String itemId) {
        super();
        eventBus = bus;
        tabOwnerId = tabId;
        id = itemId;
    }

    /* adapting to take component off button and onto a menu item */
    @Override
    public void componentSelected(final MenuEvent ce) {
        fetchExclusiveTACs();
    }

    /*
     * giving benifit of doubt that using tabOwnerId to preserve existing code
     * (but don't see it being used)
     */
    private MultipleInstanceWinId getMultipleInstanceWinId() {
        return new MultipleInstanceWinId(tabOwnerId, EMPTY_STRING);
    }

    /*
     * Addition to ensure all server communication goes though one class
     * 
     * @return ServerComms server communication helper
     */
    @SuppressWarnings("unchecked")
    ServerComms getServerCommHandler() {
        return new ServerCommsHandler(eventBus);
    }

    /* when (if) used as a buton */
    @Override
    public void onClick(final ClickEvent event) {
        // get button reference to enable it back after dialog display.
        // this is to avoid multiple clicks on the exc_tac button

        eventSource = (ImageButton) event.getSource();
        eventSource.setEnabled(false);

        fetchExclusiveTACs();
    }

    private void fetchExclusiveTACs() {
        // fetch url & params info for the button via metaMenuItem
        final MetaMenuItem metaMenuItem = metaReader.getMetaMenuItemFromID(id);
        // Make server request to fetch the EXCLUSIVE_TAC group info
        getServerCommHandler().makeServerRequest(getMultipleInstanceWinId(), metaMenuItem.getWsURL(),
                metaMenuItem.getWidgetSpecificParams());

    }

    @SuppressWarnings("unchecked")
    private class ServerCommsHandler extends ServerComms {

        public ServerCommsHandler(final EventBus eventBus) {
            super(eventBus);
        }

        @Override
        public RequestCallbackImpl getRequestCallbackImpl(final MultipleInstanceWinId multiWinID,
                final String requestData) {
            return new ExcTacInfoRequestCallbackImpl(multiWinID, eventBus, requestData);
        }

    }

    /**
     * Class included to support all server communication to pass through
     * ServerComms method
     * 
     */
    private class ExcTacInfoRequestCallbackImpl extends RequestCallbackImpl {

        public ExcTacInfoRequestCallbackImpl(final MultipleInstanceWinId multiWinID, final EventBus eventBus,
                final String requestData) {
            super(multiWinID, eventBus, requestData);

        }

        /*
         * If error, display 'failure to get exclusive tac group info from server'
         * message
         */
        @Override
        public void onError(final Request request, final Throwable exception) {
            displayExcTacLoadServerError(exception);
            eventSource.setEnabled(true);
        }

        /*
         * If EXC_TAC group info found, display on MessageDialog. If no EXC_TAC
         * group exists, pop a error message
         */
        @Override
        public void onResponseReceived(final Request request, final Response response) {

            if (STATUS_CODE_OK == response.getStatusCode()) {

                final JSONValue responseValue = parseText(response.getText());

                if (responseValue != null && JSONUtils.checkData(responseValue)) {

                    final JsonObjectWrapper metaData = new JsonObjectWrapper(responseValue.isObject());

                    final IJSONArray data = metaData.getArray(JSON_ROOTNODE);

                    SearchGroupModelData smd;
                    if (data.size() == 0) {
                        getMessageDialog().hide();
                        getMessageDialog().show("Error", "There are no TACs excluded in the system!",
                                MessageDialog.DialogType.ERROR);
                    } else if (data.size() == 1) {
                        final IJSONObject parent = data.get(0);
                        final String name = parent.getString(SearchGroupModelData.DISPLAY_FIELD);
                        final boolean isVIP = CommonConstants.TRUE.equals(parent.getString(SearchGroupModelData.VIP));

                        final IJSONArray values = parent.getArray(SearchGroupModelData.VALUES);

                        final Collection<String> stringVals = new ArrayList<String>();

                        for (int v = 0; v < values.size(); v++) {
                            final IJSONObject vparent = values.get(v);
                            stringVals.add(vparent.getString(ID));
                        }
                        smd = new SearchGroupModelData(name, isVIP, stringVals);

                        getMessageDialog().hide();

                        final StringBuilder sb = new StringBuilder();
                        for (String groupValue : smd.getGroupValues()) {
                            sb.append(groupValue);
                            sb.append("\n");
                        }
                        getMessageDialog().show(EXCLUSIVE_TAC, sb.toString(), MessageDialog.DialogType.INFO);
                    }
                }

            } else {
                LOGGER.log(Level.WARNING, "Bad response receiving excluded TACs data!");
                displayExcTacLoadServerError(null);
            }
            if (eventSource != null) {
                eventSource.setEnabled(true);
            }

        }

        JSONValue parseText(final String s) {
            if (s != null && s.length() > 0) {
                return JSONUtils.parse(s);
            }
            return null;
        }

        /* display server error message */
        private void displayExcTacLoadServerError(final Throwable exception) {
            getMessageDialog().hide();
            getMessageDialog().show("Error", "Failed to receive excluded TACs data from server!",
                    MessageDialog.DialogType.ERROR);

            LOGGER.log(Level.WARNING, "Failed returning excluded TACs data from server", exception);
        }
    }

    public MessageDialog getMessageDialog() {
        if (message == null) {
            message = new MessageDialog();
            message.setWidth("250px");
        }

        return message;
    }
}
