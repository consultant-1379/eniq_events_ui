/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.northpanel.button;

import com.ericsson.eniq.events.ui.client.datatype.LicenseInfoDataType;
import com.ericsson.eniq.events.widgets.client.menu.options.OptionsMenuAlignment;
import com.ericsson.eniq.events.widgets.client.menu.options.OptionsMenuItemTranslator;
import com.ericsson.eniq.events.widgets.client.menu.options.OptionsMenuPanel;
import com.ericsson.eniq.events.widgets.client.utilities.ZIndexHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

public class EniqOptionsMenu extends Composite implements HasSelectionHandlers<OptionMenuItemType> {

    private OptionsMenuPanel<OptionMenuItemType> menuPanel;

    private static EniqOptionsMenuResources resources;

    static {
        resources = GWT.create(EniqOptionsMenuResources.class);
        resources.css().ensureInjected();
    }

    public EniqOptionsMenu(final String name) {
        final Label widget = new Label(name);
        widget.setStyleName(resources.css().optionsMenu());
        widget.addClickHandler(new LabelClickHandler());

        Window.addResizeHandler(new WindowResizeHandler());

        initWidget(widget);

        initMenuPanel();
    }

    private void initMenuPanel() {
        menuPanel = new OptionsMenuPanel<OptionMenuItemType>(new OptionsMenuItemTranslator<OptionMenuItemType>() {
            @Override
            public String getText(OptionMenuItemType item) {
                return item.getName();
            }
        }, OptionsMenuAlignment.RIGHT);

        menuPanel.setAutohidePartner(getElement());

        menuPanel.addCloseHandler(new CloseHandler<OptionsMenuPanel<OptionMenuItemType>>() {
            @Override
            public void onClose(CloseEvent<OptionsMenuPanel<OptionMenuItemType>> optionsMenuPanelCloseEvent) {
                removeStyleName(resources.css().open());
            }
        });

        menuPanel.addSelectionHandler(new SelectionHandler<OptionMenuItemType>() {
            @Override
            public void onSelection(SelectionEvent<OptionMenuItemType> eniqOptionsMenuItemSelectionEvent) {
                // Delegate event
                fireEvent(eniqOptionsMenuItemSelectionEvent);
            }
        });
    }

    /**
     * Set specific menu item (when used as composite, e.g. options menu),
     * as shown or hidden, e.g. want to hide some until meta data is ready etc (or licence based
     * decisions etc)
     *
     * @param item    Menu Option in Options menu
     * @param visible true to set visible, false to hide
     */
    public void setVisible(final OptionMenuItemType item, final boolean visible) {
        menuPanel.setVisible(item, visible);
    }

    /** @param license  */
    public void setVisibleOnLicense(final LicenseInfoDataType license) {
        final OptionMenuItemType[] values = OptionMenuItemType.values();
        for (OptionMenuItemType value : values) {
            final boolean visible = menuPanel.isVisible(value);
            if (!visible) {
                if (value.hasLicense(license.getFeatureName())) {
                    menuPanel.setVisible(value, true);
                }
            }
        }
    }

    public void addItem(final OptionMenuItemType item) {
        // If item has license, hide it. It will be shown with setVisibleOnLicense
        menuPanel.add(item, !item.hasLicenses());
    }

    public void addSeparator(final OptionMenuItemType... items) {
        menuPanel.addSeparator(items);
    }

    public void updateItemLabel(final OptionMenuItemType item, final String label) {
        menuPanel.updateLabel(item, label);
    }

    private void show() {
        addStyleName(resources.css().open());
        menuPanel.showRelativeTo(getElement());
        /**
         * Ensure its on top of all GXT elements plus workspace glass panel, restore dialog etc.
         */
        menuPanel.setPopupZIndex(ZIndexHelper.getHighestZIndex());
    }

    private void hide() {
        menuPanel.hide();
        removeStyleName(resources.css().open());
    }

    @Override
    public HandlerRegistration addSelectionHandler(
            final SelectionHandler<OptionMenuItemType> dropDownItemSelectionHandler) {
        return addHandler(dropDownItemSelectionHandler, SelectionEvent.getType());
    }

    private class LabelClickHandler implements ClickHandler {
        @Override
        public void onClick(final ClickEvent event) {
            event.stopPropagation();

            if (menuPanel.isShowing()) {
                hide();
            } else {
                show();
            }
        }
    }

    /* When window is resized, panel also should change location */
    private class WindowResizeHandler implements ResizeHandler {
        @Override
        public void onResize(final ResizeEvent event) {
            if (menuPanel.isShowing()) {
                menuPanel.showRelativeTo(getElement());
            }
        }
    }
}