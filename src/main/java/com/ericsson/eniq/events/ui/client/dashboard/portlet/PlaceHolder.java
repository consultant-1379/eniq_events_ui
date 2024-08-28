/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import com.ericsson.eniq.events.ui.client.events.dashboard.PortletAddEvent;
import com.ericsson.eniq.events.widgets.client.window.title.TitleWindowResourceBundle;
import com.ericsson.eniq.events.widgets.client.window.title.TitleWindowResourceBundleHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * PlaceHolder class which can fire event to add new portlet
 *
 * @author evyagrz
 * @author edavboj
 * @since September 2011
 */
public class PlaceHolder extends Composite implements HasPortletId, HasRowIndex {

    private static PlaceHolderUiBinder uiBinder = GWT.create(PlaceHolderUiBinder.class);

    @UiField
    SimplePanel bodyContainer;

    @UiField(provided = true)
    TitleWindowResourceBundle resourceBundle = TitleWindowResourceBundleHelper.getBundle();

    private final String portletId;

    private final int rowIndex;

    public PlaceHolder(final String portletId, final int height, final int rowIndex, final EventBus eventBus) {
        this.portletId = portletId;
        this.rowIndex = rowIndex;

        initWidget(uiBinder.createAndBindUi(this));

        final int bodyContainerHeight = height - PortletWindow.BODY_CONTAINER_TOP_OFFSET;
        getBodyContainer().setHeight(bodyContainerHeight + "px");

        addStyleName(resourceBundle.style().placeHolder());
        createAddButton(portletId, height, eventBus);

        setId(portletId);
    }

    @Override
    public String getPortletId() {
        return portletId;
    }

    @Override
    public int getRowIndex() {
        return rowIndex;
    }

    private void createAddButton(final String portletId, final int portletHeight, final EventBus eventBus) {

        final HTML addButton = new HTML();
        addButton.setStyleName(resourceBundle.style().addButton());
        addButton.addStyleName("add"); // Used for selenium testing, please don't remove.

        final int addButtonTopMargin = ((portletHeight - resourceBundle.add().getHeight()) / 2 - PortletWindow.BODY_CONTAINER_TOP_OFFSET);

        addButton.getElement().getStyle().setPropertyPx("marginTop", addButtonTopMargin);
        addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                eventBus.fireEvent(new PortletAddEvent(portletId, PlaceHolder.this));
            }
        });

        getBodyContainer().setWidget(addButton);
    }

    public SimplePanel getBodyContainer() {
        return bodyContainer;
    }

    void setId(final String portletId) {
        getElement().setId(portletId);
    }

    @UiTemplate("PortletWindow.ui.xml")
    interface PlaceHolderUiBinder extends UiBinder<Widget, PlaceHolder> {
    }
}