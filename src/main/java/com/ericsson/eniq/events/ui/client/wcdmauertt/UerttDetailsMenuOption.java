/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 ******************************************************************************* */

package com.ericsson.eniq.events.ui.client.wcdmauertt;

import com.ericsson.eniq.events.common.client.CommonConstants;
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

public class UerttDetailsMenuOption extends Composite implements HasSelectionHandlers<uerttMenuItem> {

    private OptionsMenuPanel<uerttMenuItem> menuPanel;
    private final String SELENIUM_LABEL  = "LABEL";

    public int getIndex() {
        return index;
    }

    private int index;
    private String labelText;

    private static EniqUerttOptionResource resources;

    static {
        resources = GWT.create(EniqUerttOptionResource.class);
        resources.css().ensureInjected();
    }

    public UerttDetailsMenuOption(int index, String labelText) {
        this.index = index;
        this.labelText = labelText;
    }

    //Extracted out to make this class testable
    public UerttDetailsMenuOption()
    {}

    protected Label createLabel(String labelText)
    {
        return new Label(labelText);
    }

    protected OptionsMenuPanel<uerttMenuItem> createOptionsMenuPanel()
    {
        OptionsMenuPanel<uerttMenuItem> menuPanel = new OptionsMenuPanel<uerttMenuItem>(new OptionsMenuItemTranslator<uerttMenuItem>() {
            @Override
            public String getText(uerttMenuItem item) {
                return item.getName();
            }
        }, OptionsMenuAlignment.LEFT);
        return menuPanel;
    }

    public void initMenuOption()
    {
        final Label label = createLabel(labelText);
        setSeleniumTagOnLabel(label);        
        label.setStyleName(resources.css().optionsMenu());
        label.addClickHandler(new LabelClickHandler());
        Window.addResizeHandler(new WindowResizeHandler());
        initialise(label);
    }
    
    protected void setSeleniumTagOnLabel(Label label)
    {
        label.getElement().setId(CommonConstants.SELENIUM_TAG+SELENIUM_LABEL);
    }

    protected void initialise(Label label) {
        initWidget(label);
        initMenuPanel();
    }

    private void initMenuPanel() {
        menuPanel = createOptionsMenuPanel();

        menuPanel.setAutohidePartner(getElement());

        menuPanel.addCloseHandler(new CloseHandler<OptionsMenuPanel<uerttMenuItem>>() {
            @Override
            public void onClose(CloseEvent<OptionsMenuPanel<uerttMenuItem>> optionsMenuPanelCloseEvent) {
                removeStyleName(resources.css().open());
            }
        });

        menuPanel.addSelectionHandler(new SelectionHandler<uerttMenuItem>() {
            @Override
            public void onSelection(SelectionEvent<uerttMenuItem> eniqOptionsMenuItemSelectionEvent) {
                // Delegate event
                fireEvent(eniqOptionsMenuItemSelectionEvent);
            }
        });
    }

    public void setVisible(final uerttMenuItem item, final boolean visible) {
        menuPanel.setVisible(item, visible);
    }

    public void addItem(final uerttMenuItem item) {
        // If item has license, hide it. It will be shown with setVisibleOnLicense
        menuPanel.add(item,true);
    }

    public void addSeparator(final uerttMenuItem items) {
        menuPanel.addSeparator(items);
    }

    public void updateItemLabel(final uerttMenuItem item, final String label) {
        menuPanel.updateLabel(item, label);
    }

    private void show() {
        addStyleName(resources.css().open());
        menuPanel.showRelativeTo(getElement());
        menuPanel.setPopupZIndex(ZIndexHelper.getHighestZIndex());
    }

    private void hide() {
        menuPanel.hide();
        removeStyleName(resources.css().open());
    }

    @Override
    public HandlerRegistration addSelectionHandler(
            final SelectionHandler<uerttMenuItem> dropDownItemSelectionHandler) {
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

    private class WindowResizeHandler implements ResizeHandler {
        @Override
        public void onResize(final ResizeEvent event) {
            if (menuPanel.isShowing()) {
                menuPanel.showRelativeTo(getElement());
            }
        }
    }
}