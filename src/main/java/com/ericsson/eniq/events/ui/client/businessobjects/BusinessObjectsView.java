/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.businessobjects;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.businessobjects.resources.BusinessObjectsResourceBundle;
import com.ericsson.eniq.events.ui.client.common.widget.EniqWindow;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.main.IGenericTabView;
import com.ericsson.eniq.events.ui.client.main.TabViewRegistry;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author ecarsea
 * @since 2011
 */
public class BusinessObjectsView extends BaseView<BusinessObjectsPresenter> {

    private final BusinessObjectsResourceBundle resourceBundle;

    private ContentPanel centerPanel;

    private String tabId;

    private static final int DEFAULT_REPORT_WINDOW_WIDTH = 1000;

    private static final int DEFAULT_REPORT_WINDOW_HEIGHT = 600;

    private Point currentWindowPosition = new Point(0, 0);

    private final EventBus eventBus;

    int count = 0;

    private ContentPanel windowContainer;

    private final Map<String, BusinessObjectsReportWindow> openWindowMap;

    @Inject
    public BusinessObjectsView(final EventBus eventBus, final BusinessObjectsResourceBundle resourceBundle) {
        this.eventBus = eventBus;
        this.resourceBundle = resourceBundle;
        this.openWindowMap = new HashMap<String, BusinessObjectsReportWindow>();
        this.resourceBundle.style().ensureInjected();
    }

    public void init(final String tabId) {
        this.tabId = tabId;
        final IGenericTabView tabView = TabViewRegistry.get().getTabView(tabId);
        centerPanel = tabView.getCenterPanel();
        centerPanel.setLayout(new FlowLayout());
        final ContentPanel filler = new ContentPanel();
        filler.setStyleName(resourceBundle.style().filler());
        filler.setHeaderVisible(false);
        centerPanel.add(filler);
        centerPanel.layout();
        windowContainer = new ContentPanel();
        windowContainer.setHeaderVisible(false);
        windowContainer.setStyleName(resourceBundle.style().windowContainer());
        centerPanel.add(windowContainer);
        centerPanel.layout();
        /** Set the window container in the generic tab view in order that tiling etc will occur using
         * the window container as the constrain area rather than the center panel.
         */
        tabView.setWindowContainer(windowContainer);
    }

    public void addSideBar(final IReportsSideBarView sideBar) {
        centerPanel.add(sideBar.asWidget());
        centerPanel.layout();
    }

    private void configureWindow(final BusinessObjectsReportWindow window) {
        window.setSize(DEFAULT_REPORT_WINDOW_WIDTH, DEFAULT_REPORT_WINDOW_HEIGHT);
        final EniqWindow eniqWindow = window.getWidget();
        eniqWindow.fitIntoContainer();
        window.addLaunchButton();
        window.putWindowToFront();
        currentWindowPosition = eniqWindow.setWindowPosition(currentWindowPosition);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.businessobjects.IBusinessObjectsView#addWindow(java.lang.String, java.lang.String)
     */
    public void addWindow(final String header, final String url) {
        /** use milliseconds for unique window html id **/
        final String winId = header + System.currentTimeMillis();

        final BusinessObjectsReportWindow window = new BusinessObjectsReportWindow(new MultipleInstanceWinId(
                tabId, winId), windowContainer, header, resourceBundle.style().windowIconImage(), eventBus, true);
        count++;
        window.setUrl(url);
        openWindowMap.put(winId, window);
        windowContainer.add(window.getWidget());
        windowContainer.layout();
        configureWindow(window);
    }

    public void closeWindow(final String winId) {
        openWindowMap.remove(winId);
    }

    public void closeAllOpenWindows() {
        if (!openWindowMap.isEmpty()) {
            final Set<Map.Entry<String, BusinessObjectsReportWindow>> entries = openWindowMap.entrySet();
            for (final Map.Entry<String, BusinessObjectsReportWindow> entry : entries) {
                final Object winId = entry.getKey();
                openWindowMap.get(winId).removeLaunchButton();
            }
            openWindowMap.clear();
        }
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onDetach()
     */
    @Override
    protected void onDetach() {
        super.onDetach();
        getPresenter().unbind();
    }
}
