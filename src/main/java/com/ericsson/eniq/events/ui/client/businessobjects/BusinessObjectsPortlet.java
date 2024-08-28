/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.businessobjects;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.ui.client.businessobjects.resources.BusinessObjectsResourceBundle;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessageType;
import com.ericsson.eniq.events.ui.client.dashboard.PortletTemplate;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.events.component.ComponentMessageEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletRemoveEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletRemoveEventHandler;
import com.ericsson.eniq.events.ui.client.events.tab.TabAddEvent;
import com.ericsson.eniq.events.ui.client.events.tab.TabRemoveEvent;
import com.ericsson.eniq.events.ui.client.main.TabViewRegistry;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import javax.inject.Inject;

import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;

/**
 * Portlet for launching a Business Objects Report Viewer
 *
 * @author ecarsea
 * @since 2011
 */
public class BusinessObjectsPortlet implements PortletTemplate {

    private static final String TAB_ID_PARAM = "boTabId";

    private static final String BIS_SERVICE_SUCCESS = "bisServiceSuccess";

    private final EventBus eventBus;

    private final BusinessObjectsPresenter businessObjectsPresenter;

    private final BusinessObjectsResourceBundle resourceBundle;

    private String boTabId;

    private String portletId;

    private Image portletImage;

    private final FlowPanel portletImagePanel;

    private SearchFieldDataType windowSearchData;

    private TimeInfoDataType windowTimeData;

    private HandlerRegistration portletRemoveHandler;

    @Inject
    public BusinessObjectsPortlet(final EventBus eventBus, final BusinessObjectsResourceBundle resourceBundle,
            final BusinessObjectsPresenter businessObjectsPresenter) {
        this.eventBus = eventBus;
        this.resourceBundle = resourceBundle;
        this.businessObjectsPresenter = businessObjectsPresenter;

        injectResources(resourceBundle); // NOPMD by eeicmsy on 09/11/11 17:18
        this.portletImagePanel = createImagePanel(); // NOPMD by eeicmsy on 09/11/11 17:19
    }

    /**
     * For junit test
     * @param resourceBundle Business objects resource bundle
     */
    @SuppressWarnings("hiding")
    void injectResources(final BusinessObjectsResourceBundle resourceBundle) {
        resourceBundle.style().ensureInjected();
    }

    FlowPanel createImagePanel() {
        final FlowPanel panel = new FlowPanel();
        panel.setSize("100%", "100%");
        return panel;
    }

    @Override
    public Widget asWidget() {
        return portletImagePanel;
    }

    @Override
    public void init(final PortletDataType descriptor) {

        this.boTabId = descriptor.getParameters().getParameter(TAB_ID_PARAM);
        this.portletId = descriptor.getPortletId();

        portletRemoveHandler = eventBus.addHandler(PortletRemoveEvent.TYPE, new PortletRemoveEventHandler() {

            @Override
            public void onRemove(final PortletRemoveEvent event) {
                if (event.getComponentId().equals(portletId)) {
                    eventBus.fireEvent(new TabRemoveEvent(boTabId));
                    businessObjectsPresenter.closeAllOpenWindows();
                    if (portletRemoveHandler != null) {
                        portletRemoveHandler.removeHandler();
                    }
                }
            }
        });
    }

    @Override
    public void update(final JSONValue data, final SearchFieldDataType searchData, final TimeInfoDataType timeData) {

        this.windowSearchData = searchData;
        this.windowTimeData = timeData;

        final JsonObjectWrapper metaData = createMetaData(data);
        final String success = metaData.getString(BIS_SERVICE_SUCCESS);

        portletImagePanel.clear();
        if (CommonConstants.TRUE.equalsIgnoreCase(success)) {
            portletImagePanel.add(getPortletImage());
        } else {
            String error = metaData.getString(CommonConstants.ERROR_DESCRIPTION);
            if (error == null || error.isEmpty()) {
                error = "Connection Unavailable"; //default message
            }

            eventBus.fireEvent(new ComponentMessageEvent(portletId, ComponentMessageType.WARN, error));
        }
    }

    @Override
    public SearchFieldDataType getSearchFieldData() {
        return windowSearchData;
    }

    @Override
    public TimeInfoDataType getTimeFieldData() {
        return windowTimeData;
    }

    /**
     * Create metadata. For junit test
     *
     * @param data metadata
     * @return JsonObjectWrapper object from provided data
     */
    JsonObjectWrapper createMetaData(final JSONValue data) {
        return new JsonObjectWrapper(data.isObject());
    }

    Image getPortletImage() {
        if (portletImage == null) {
            portletImage = new Image();
            portletImage.setResource(resourceBundle.businessObjectsImage());
            portletImage.addClickHandler(new ImageClickHandler());
            portletImage.setStyleName(resourceBundle.style().boPortletImage());
            portletImage.setSize("100%", "100%");
        }

        return portletImage;
    }

    private class ImageClickHandler implements ClickHandler {

        @Override
        public void onClick(final ClickEvent event) {
            if (TabViewRegistry.get().containsTabView(boTabId)) {
                final TabItem tabItem = TabViewRegistry.get().getTabView(boTabId).getTabItem();
                tabItem.getTabPanel().setSelection(tabItem);
            } else {
                eventBus.fireEvent(new TabAddEvent(boTabId));
            }
        }
    }
}
