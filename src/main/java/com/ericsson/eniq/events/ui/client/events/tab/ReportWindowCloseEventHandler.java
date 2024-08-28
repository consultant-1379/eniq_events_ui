/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events.tab;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author ecarsea
 * @since 2011
 */
public interface ReportWindowCloseEventHandler extends EventHandler {
    void onReportClose(String tabId, String winId);
}
