/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.component;

import com.ericsson.eniq.events.ui.login.client.Login;
import com.ericsson.eniq.events.ui.login.client.window.EWindow;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.KeyboardListenerCollection;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author egallou
 * @since 2011
 *
 */

@SuppressWarnings("deprecation")
public class ETeardropPopup extends PopupPanel { // NOPMD - cyclomatic complexity

    //instantiate styling resources 
    private static ETeardropPopupResources resources;

    static {
        resources = GWT.create(ETeardropPopupResources.class);
        resources.css().ensureInjected();
    }

    //flexTable for popup
    final FlexTable popUpHolder = new FlexTable();

    final FlexTable topBorder = new FlexTable();

    final FlexTable middle = new FlexTable();

    final FlexTable bottomBorder = new FlexTable();

    //top border components
    final Label lhc = new Label();

    final Label middle_top = new Label();

    final Label tear_drop = new Label();

    final Label rhc = new Label();

    //middle content with styles
    final Label l = new Label();

    private HTML mid;

    final Label r = new Label();

    //bottom border components
    final Label bl = new Label();

    final Label bmid = new Label();

    final Label br = new Label();

    private HandlerRegistration nativePreviewHandlerRegistration;

    private HandlerRegistration windowResizeHandlerRegistration;

    /** Offsets to line the tear drop up with the question icon ecarsea**/
    public static final int LEFT_OFFSET = 52;

    public static final int TOP_OFFSET = 11;

    /**
     * @param conent - They content to be displayed in the popUp 
     * 
     */
    public ETeardropPopup(final HTML content) {
        super(true);

        setContent(content); //NOPMD

        initPopup();

        setWidget(popUpHolder);
    }

    /*Set up the layout and styling for the popup*/
    private void initPopup() {

        initUI();

        initStyles();

        /** Retrieve Theme from central location as it will be personalisable **/
        applyStyle(Login.CSS_THEME_LIGHT);

        /* Add 2 to show it above glass panel */
        this.getElement().getStyle().setZIndex(EWindow.getLayerOfTheTopWindow() + 2);

        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(final ResizeEvent event) {
                hide();
            }
        });
    }

    public void setContent(final HTML content) {
        mid = content;
    }

    private void initUI() {
        topBorder.setWidget(0, 0, lhc);
        topBorder.setWidget(0, 1, tear_drop);
        topBorder.setWidget(0, 2, middle_top);
        topBorder.setWidget(0, 3, rhc);

        topBorder.setCellPadding(0);
        topBorder.setCellSpacing(0);
        topBorder.setHeight("100%");
        topBorder.setWidth("100%");
        topBorder.getCellFormatter().setWidth(0, 2, "100%");

        middle_top.setWidth("100%");
        mid.setHeight("100%");

        middle.setWidget(0, 0, l);
        middle.setWidget(0, 1, mid);
        middle.setWidget(0, 2, r);

        //this ensures it will resize with syling
        l.setHeight("100%");
        r.setHeight("100%");
        bmid.setWidth("100%");

        middle.setCellPadding(0);
        middle.setCellSpacing(0);
        middle.setWidth("100%");
        middle.setHeight("100%");
        middle.getCellFormatter().setHeight(0, 0, "100%");
        middle.getCellFormatter().setHeight(0, 2, "100%");
        middle.getFlexCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);

        bottomBorder.setWidget(0, 0, bl);
        bottomBorder.setWidget(0, 1, bmid);
        bottomBorder.setWidget(0, 2, br);

        bottomBorder.setCellPadding(0);
        bottomBorder.setCellSpacing(0);
        bottomBorder.setHeight("100%");
        bottomBorder.setWidth("100%");
        bottomBorder.getCellFormatter().setWidth(0, 1, "100%");

        popUpHolder.setCellPadding(0);
        popUpHolder.setCellSpacing(0);

        popUpHolder.setWidget(1, 0, topBorder);
        popUpHolder.setWidget(2, 0, middle);
        popUpHolder.setWidget(3, 0, bottomBorder);

    }

    private void initStyles() {
        lhc.setStylePrimaryName("ETeardropPopup-Border-tl");
        tear_drop.setStylePrimaryName("ETeardropPopup-Border-teardrop");
        middle_top.setStylePrimaryName("ETeardropPopup-Border-t");
        rhc.setStylePrimaryName("ETeardropPopup-Border-tr");

        l.setStylePrimaryName("ETeardropPopup-Border-l");
        r.setStylePrimaryName("ETeardropPopup-Border-r");
        mid.setStylePrimaryName("ETeardropPopup-Text");

        bl.setStylePrimaryName("ETeardropPopup-Border-bl");
        bmid.setStylePrimaryName("ETeardropPopup-Border-b");

        br.setStylePrimaryName("ETeardropPopup-Border-br");
    }

    /**
     * ecarsea - Added to get the mouse click event and to hide the popup panel if the click is anywhere outside the panel
     * content, rather than the entire panel, as the panel borders are large and in places they extend transparently well beyond the visible
     * border.
     * Preview the {@link NativePreviewEvent}.
     *
     * @param event the {@link NativePreviewEvent}
     */
    private void previewNativeEvent(final NativePreviewEvent event) { // NOPMD Cyclomatic Complexity
        // If the event has been canceled or consumed, ignore it
        if (event.isCanceled() || (!isPreviewingAllNativeEvents() && event.isConsumed())) {
            // We need to ensure that we cancel the event even if its been consumed so
            // that popups lower on the stack do not auto hide
            return;
        }

        // Fire the event hook and return if the event is canceled
        onPreviewNativeEvent(event);
        if (event.isCanceled()) {
            return;
        }

        // If the event targets the popup or the partner, consume it
        final Event nativeEvent = Event.as(event.getNativeEvent());
        final boolean eventTargetsPopup = eventTargetsPopup(nativeEvent);
        if (eventTargetsPopup) {
            event.consume();
        }

        // Switch on the event type
        final int type = nativeEvent.getTypeInt();
        switch (type) {
        case Event.ONKEYDOWN: {
            if (!onKeyDownPreview((char) nativeEvent.getKeyCode(),
                    KeyboardListenerCollection.getKeyboardModifiers(nativeEvent))) {
                event.cancel();
            }
            return;
        }
        case Event.ONKEYUP: {
            if (!onKeyUpPreview((char) nativeEvent.getKeyCode(),
                    KeyboardListenerCollection.getKeyboardModifiers(nativeEvent))) {
                event.cancel();
            }
            return;
        }
        case Event.ONKEYPRESS: {
            if (!onKeyPressPreview((char) nativeEvent.getKeyCode(),
                    KeyboardListenerCollection.getKeyboardModifiers(nativeEvent))) {
                event.cancel();
            }
            return;
        }

        case Event.ONMOUSEDOWN:
            // Don't eat events if event capture is enabled, as this can
            // interfere with dialog dragging, for example.
            if (DOM.getCaptureElement() != null) {
                event.consume();
                return;
            }

            if (!eventTargetsPopup) {
                hide(true);
                updateHandlers();
                return;
            }
            break;
        case Event.ONMOUSEUP:
        case Event.ONMOUSEMOVE:
        case Event.ONCLICK:
        case Event.ONDBLCLICK: {
            // Don't eat events if event capture is enabled, as this can
            // interfere with dialog dragging, for example.
            if (DOM.getCaptureElement() != null) {
                event.consume();
                return;
            }
            break;
        }
        }
    }

    /*
     * Does the event target this pop up?
     *
     * @param event the native event
     * @return true if the event targets the pop up
     */
    private boolean eventTargetsPopup(final NativeEvent event) {
        final EventTarget target = event.getEventTarget();
        if (Element.is(target)) {
            return this.mid.getElement().isOrHasChild(Element.as(target));
        }
        return false;
    }

    /**
     * Register or unregister the handlers used by {@link PopupPanel}.
     */
    private void updateHandlers() {
        // Remove any existing handlers.
        if (nativePreviewHandlerRegistration != null) {
            nativePreviewHandlerRegistration.removeHandler();
            nativePreviewHandlerRegistration = null;
        }

        if (windowResizeHandlerRegistration != null) {
            windowResizeHandlerRegistration.removeHandler();
            windowResizeHandlerRegistration = null;
        }

        // Create handlers if showing.
        if (this.isShowing()) {
            nativePreviewHandlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
                @Override
                public void onPreviewNativeEvent(final NativePreviewEvent event) {
                    previewNativeEvent(event);
                }
            });

            windowResizeHandlerRegistration = Window.addResizeHandler(new ResizeHandler() {

                @Override
                public void onResize(final ResizeEvent event) {
                    hide();
                }
            });
        }
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.PopupPanel#setPopupPositionAndShow(com.google.gwt.user.client.ui.PopupPanel.PositionCallback)
     */
    @Override
    public void setPopupPositionAndShow(final PositionCallback callback) {
        super.setPopupPositionAndShow(callback);
        updateHandlers();
    }

    /**
     * @param style
     * Adds style dependent name to all components making up the pop up
     */
    private final void applyStyle(final String style) {

        lhc.addStyleDependentName(style);
        tear_drop.addStyleDependentName(style);
        middle_top.addStyleDependentName(style);
        rhc.addStyleDependentName(style);

        l.addStyleDependentName(style);
        mid.addStyleDependentName(style);
        r.addStyleDependentName(style);

        bl.addStyleDependentName(style);
        bmid.addStyleDependentName(style);
        br.addStyleDependentName(style);

    }
}
