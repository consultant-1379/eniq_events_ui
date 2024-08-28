/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.events;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to fire (e.g from MultiWinSelectComponent) to check if 
 * can user ok to close all windows
 *   
 * @author eeicmsy
 * @since June 2011
 *
 */
public class CheckAndCloseAllWindowsEvent extends GwtEvent<CheckAndCloseAllWindowsEventHandler> {

    public static final Type<CheckAndCloseAllWindowsEventHandler> TYPE = new Type<CheckAndCloseAllWindowsEventHandler>();

    private final ComponentEvent ce;

    private final String tabId;

    /**
     * Event to fire (e.g from MultiWinSelectComponent) to check if 
     * can user ok to close all windows
     * 
     * @see com.ericsson.eniq.events.ui.client.main.MainPresenter
     *  
     * @param ce           - component event, 
     *                      @see #com.extjs.gxt.ui.client.widget.form.CheckBox.onClick
     * @param tabId        - tabId owner of checkbox (whose manual setting we may need to undo) 
     */
    public CheckAndCloseAllWindowsEvent(final ComponentEvent ce, final String tabId) {
        this.ce = ce;
        this.tabId = tabId;
    }

    @Override
    protected void dispatch(final CheckAndCloseAllWindowsEventHandler handler) {

        handler.handleCheckForMultiWinSelectComp(ce, tabId);
    }

    @Override
    public Type<CheckAndCloseAllWindowsEventHandler> getAssociatedType() {
        return TYPE;
    }
}
