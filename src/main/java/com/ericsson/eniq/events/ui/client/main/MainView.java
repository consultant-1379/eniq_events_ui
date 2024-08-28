/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.main;

import com.ericsson.eniq.events.ui.client.events.dashboard.PortletResizeEvent;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;
import java.util.Date;

/**
 * Main view class
 * North panel (banner, userLogout, etc) and Tabbed Panel
 * 
 * @author eeicmsy
 * @since Feb 2010
 *
 */
public class MainView extends Viewport implements IMainView {

    /* because using runtime metadata for content - 
       tabItems are defined in the presenter (as needed eventBus to 
       ensure metadata loaded  */

    private final TabPanel tabbedPanel = new TabPanel();

    @Inject
    public MainView(final EventBus eventBus) {

        setLayout(new BorderLayout());

        final BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);

        // set plain so loose tab strip
        tabbedPanel.setPlain(true);
        tabbedPanel.setTabScroll(true);
        //Done like this to allow the header buttons float over the tabPanel bar
        add(tabbedPanel, centerData);

        tabbedPanel.addListener(Events.Resize, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(final BaseEvent baseEvent) {
                eventBus.fireEvent(new PortletResizeEvent());
            }
        });
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void startProcessing() {

    }

    @Override
    public void stopProcessing() {
        DOM.getElementById("loading").removeClassName("loadingDisplay");
    }

    @Override
    public TabPanel getContainerTab() {
        return tabbedPanel;
    }
}
