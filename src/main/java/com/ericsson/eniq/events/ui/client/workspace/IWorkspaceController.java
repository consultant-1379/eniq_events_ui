/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace;

import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.events.window.WindowClosedEventHandler;
import com.ericsson.eniq.events.ui.client.events.window.WindowOpenedEventHandler;
import com.ericsson.eniq.events.ui.client.search.ISubmitSearchHandler;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface IWorkspaceController extends WindowOpenedEventHandler, WindowClosedEventHandler {

    /**
     * @param id 
     * @return
     */
    SearchFieldDataType getSearchComponentValue(String id);

    /**
     * @return
     */
    EventBus getEventBus();

    /**
     * @return
     */
    String getTabOwnerId();

    /**
     * @param winId
     * @return
     */
    BaseWindow getWindow(String winId);

    /**
     * @return
     */
    ContentPanel getCenterPanel();

    void addSubmitSearchHandler(ISubmitSearchHandler handler);

    void removeSubmitSearchHandler(ISubmitSearchHandler handler);

    /**
     * @return
     */
    Point getLastOpenedWindowPosition();

    /**
     * @param newPosition
     */
    void setLastOpenedWindowPosition(Point newPosition);

    /**
     * @return
     */
    boolean justSaysInputInSearchFieldNoSelection();

    /**
     * @return
     */
    Component getSearchComponent();
}
