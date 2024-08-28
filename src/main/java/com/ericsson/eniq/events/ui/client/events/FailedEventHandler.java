package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.google.gwt.event.shared.EventHandler;

/**
 * GWT Event Handler that can be added to the EventBus for error condition (of
 * asynch call to server)
 *
 * @author eeicmsy
 * @since Jan 2010
 * @see FailedEvent
 *
 */
public interface FailedEventHandler extends EventHandler {

    // TODO could have a separate interface with less parameters when request is  null

    /**
     * Handle failed request. Call sent to all windows, guard (multiWinId) used 
     * for correct window to take up required action
     * 
     * @param multiWinId    - Contains unique window id (same as menu item id, and launch button id). 
     *                        Includes tab id to guard when 
     *                        same window exists across multiple tabs. 
     *                        Contains search field data for window (or null) to quard for multi instance support.
     *   
     * @param requestData   - requestData in sent in the com.google.gwt.http.client.Request
     *                        can be null if could not build a request for example or no params to pass
     *                        
     * @param exception     - exception arising from request
     */
    void handleFail(final MultipleInstanceWinId multiWinId, final String requestData, final Throwable exception);
}
