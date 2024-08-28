/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.businessobjects;

import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.events.tab.ReportWindowCloseEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Window containing a business objects report
 * @author ecarsea
 * @since October 2011
 *
 */
public class BusinessObjectsReportWindow extends BaseWindow {

    private final EventBus eventBus;
    private final String tabId;

    /**
     *
     */
    public BusinessObjectsReportWindow(final MultipleInstanceWinId multiWinId,
            final ContentPanel constrainArea, final String title, final String icon, final EventBus eventBus,
            final boolean hideToolBar) {
        super(multiWinId, constrainArea, title, icon, eventBus, hideToolBar, null);
        this.tabId = multiWinId.getTabId();
        this.eventBus = eventBus;
    }

    @Override
    public void startProcessing() {
    }


    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.comp.BaseWindow#stopProcessing()
    */
    @Override
    public void stopProcessing() {
        // TODO Auto-generated method stub
    }

    @Override
    public Widget asWidget() {
        return getWidget();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.common.comp.BaseWindow#onClose()
     */
    @Override
    protected void onHide() {
        super.onHide();
        eventBus.fireEvent(new ReportWindowCloseEvent(tabId, getBaseWindowID()));
    }

    @Override
    protected void removeLaunchButton() {
        super.removeLaunchButton();
    }
}
