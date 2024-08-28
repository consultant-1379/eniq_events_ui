/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events.tab;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author ecarsea
 * @since 2011
 */
public class ReportWindowCloseEvent extends GwtEvent<ReportWindowCloseEventHandler> {

    public final static Type<ReportWindowCloseEventHandler> TYPE = new Type<ReportWindowCloseEventHandler>();

    private final String winId;

    private final String tabId;

    public ReportWindowCloseEvent(final String tabId, final String winId) {
        this.winId = winId;
        this.tabId = tabId;
    }

    @Override
    public Type<ReportWindowCloseEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final ReportWindowCloseEventHandler handler) {
        handler.onReportClose(tabId, winId);
    }
}
