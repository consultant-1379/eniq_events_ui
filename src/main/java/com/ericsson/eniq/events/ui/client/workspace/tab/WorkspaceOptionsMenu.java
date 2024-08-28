/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.tab;

import static com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.*;

import com.ericsson.eniq.events.ui.client.common.Constants;
import com.ericsson.eniq.events.widgets.client.menu.options.OptionsMenuAlignment;
import com.ericsson.eniq.events.widgets.client.menu.options.OptionsMenuItemTranslator;
import com.ericsson.eniq.events.widgets.client.menu.options.OptionsMenuPanel;
import com.extjs.gxt.ui.client.core.XDOM;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

/**
 * @author ealeerm - Alexey Ermykin
 * @since 06/2012
 */
public class WorkspaceOptionsMenu extends Composite implements HasSelectionHandlers<WorkspaceOptionMenuItemType> {

    private OptionsMenuPanel<WorkspaceOptionMenuItemType> menuPanel;

    private static WorkspaceOptionsMenuResourceBundle resources;

    static {
        resources = GWT.create(WorkspaceOptionsMenuResourceBundle.class);
        resources.css().ensureInjected();
    }

    public WorkspaceOptionsMenu() {
        final Label widget = new Label("");
        widget.setStyleName(resources.css().optionsMenu());
        widget.addClickHandler(new LabelClickHandler());
        widget.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
            }
        });

        Window.addResizeHandler(new WindowResizeHandler());

        initWidget(widget);
        initMenuPanel();
    }

    private void initMenuPanel() {
        menuPanel = new OptionsMenuPanel<WorkspaceOptionMenuItemType>(
                new OptionsMenuItemTranslator<WorkspaceOptionMenuItemType>() {
                    @Override
                    public String getText(WorkspaceOptionMenuItemType item) {
                        return item.getName();
                    }
                }, OptionsMenuAlignment.LEFT);

        menuPanel.setAutohidePartner(getElement());

        menuPanel.addCloseHandler(new CloseHandler<OptionsMenuPanel<WorkspaceOptionMenuItemType>>() {
            @Override
            public void onClose(CloseEvent<OptionsMenuPanel<WorkspaceOptionMenuItemType>> optionsMenuPanelCloseEvent) {
                processClose();
            }
        });

        menuPanel.addSelectionHandler(new SelectionHandler<WorkspaceOptionMenuItemType>() {
            @Override
            public void onSelection(SelectionEvent<WorkspaceOptionMenuItemType> event) {
                fireEvent(event); // Delegate event
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
    public void setVisible(final WorkspaceOptionMenuItemType item, final boolean visible) {
        menuPanel.setVisible(item, visible);
    }

    public void addItem(final WorkspaceOptionMenuItemType item) {
        menuPanel.add(item);
    }

    public void addSeparator(final WorkspaceOptionMenuItemType... items) {
        menuPanel.addSeparator(items);
    }

    private void show(Element e) {
        addStyleName(resources.css().open());
        menuPanel.showRelativeTo(e);
        /**
         * Ensure its on top of all GXT elements plus workspace glass panel, restore dialog etc.
         */
        menuPanel.setPopupZIndex(Math.max(Constants.ENIQ_EVENTS_BASE_ZINDEX + 3, XDOM.getTopZIndex()));
    }

    private void hide() {
        menuPanel.hide();
        processClose();
    }

    @Override
    public HandlerRegistration addSelectionHandler(
            final SelectionHandler<WorkspaceOptionMenuItemType> dropDownItemSelectionHandler) {
        return addHandler(dropDownItemSelectionHandler, SelectionEvent.getType());
    }

    public void processClick(com.extjs.gxt.ui.client.event.DomEvent ce) {
        ce.stopEvent();

        if (menuPanel.isShowing()) {
            hide();
        } else {
            show(ce.getTarget());
        }
    }

    public void processClick(@SuppressWarnings("rawtypes") com.google.gwt.event.dom.client.DomEvent ce) {
        ce.stopPropagation();

        if (menuPanel.isShowing()) {
            hide();
        } else {
            show(ce.getRelativeElement());
        }
    }

    /* When window is resized, panel also should change location */
    public void processClose() {
        removeStyleName(resources.css().open());
    }

    private class LabelClickHandler implements ClickHandler {
        @Override
        public void onClick(final ClickEvent event) {
            processClick(event);
        }
    }

    private class WindowResizeHandler implements ResizeHandler {
        @Override
        public void onResize(final ResizeEvent event) {
            if (menuPanel.isShowing()) {
                menuPanel.hide();
            }
        }
    }

    public WorkspaceOptionsMenuResourceBundle getResources() {
        return resources;
    }

    public void setAutohidePartner(final Element partner) {
        menuPanel.setAutohidePartner(partner);
    }
}
