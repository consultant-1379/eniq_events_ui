package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.http.client.Response;

/**
 * Interface for class to handle SucessResponseEvent dispatched
 * using EventBus
 *
 * @author eeicmsy
 * @since Jan 2010
 * @see SucessResponseEventHandler
 */
public interface SucessResponseEventHandler extends EventHandler {

    /**
     * Handle server Response. The Response will do to all open windows, i.e. 
     * all are listeners to event bus firing response event.
     * The multiWinId is used to ensure the correct window acts on the response.
     * 
     * @param multiWinId    - Contains unique window id (same as menu item id, and launch button id). 
     *                        Includes tab id to guard when 
     *                        same window exists across multiple tabs. 
     *                        Contains search field data for window (or null) to quard for multi instance support.
     *                        
     * @param requestData   - can be null - request data sent to server (in the gwt.http.client.Request                                               
     *                        
     * @param response      - server Response from HTTP request (e.g. contains JSON Text)
     */
    void handleResponse(final MultipleInstanceWinId multiWinId, final String requestData, final Response response);
}
