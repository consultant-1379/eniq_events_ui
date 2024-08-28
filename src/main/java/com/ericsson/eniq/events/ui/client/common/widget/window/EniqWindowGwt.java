/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget.window;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.ericsson.eniq.events.widgets.client.utilities.ZIndexHelper;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Provide the Dialog Windows for the Group Management Component
 * @author ecarsea
 * @since 2011
 *
 */
public class EniqWindowGwt extends Composite {

    private static EniqWindowGwtUiBinder uiBinder = GWT.create(EniqWindowGwtUiBinder.class);

    private static final String glassStyleName = "gwt-PopupPanelGlass";

    private final int currentZIndex;

    private final List<EniqWindowListener> windowListeners;

    interface EniqWindowGwtUiBinder extends UiBinder<Widget, EniqWindowGwt> {
    }

    @UiField
    HTMLPanel window;

    @UiField
    SimplePanel windowContent;

    @UiField
    HTMLPanel windowTitle;

    @UiField
    Image closeButton;

    @UiField
    FocusPanel dragHandle;

    private boolean isGlassEnabled;

    private DivElement glass;

    private HandlerRegistration resizeRegistration;

    private boolean centered;

    private PickupDragController windowDragController;

    protected boolean dragging;

    private LayoutContainer windowContainer;

    @UiHandler("closeButton")
    public void onClose(@SuppressWarnings("unused") final ClickEvent event) {
        close();
    }

    public EniqWindowGwt() {
        initWidget(uiBinder.createAndBindUi(this));
        /** Sit on top of the GXT stuff and any launch panels. Ensure glass panel is also on top of everything but the window.
         * It has to be huge as tiling and cascading windows has an exponential effect on XDOM ZIndex (Due to the GXT algorithm).
         * Add 2 in order to have it above the glasspanel which will be 1 less than this, but greater than other elements **/
        this.currentZIndex = ZIndexHelper.getHighestZIndex()+1;
        this.getElement().getStyle().setZIndex(currentZIndex);
        configureDraggable();
        windowListeners = new ArrayList<EniqWindowListener>();
    }

    public Image getCloseButton() {
        return closeButton;
    }

    public void close() {
        unbind();
        if (this.getParent() instanceof HasWidgets) {
            this.removeFromParent();
        } else if (this.getParent() instanceof WidgetComponent) {
            /** Parent is GXT **/
            ((WidgetComponent) this.getParent()).removeFromParent();
        }
        fireWindowClosed();
    }

    /**
     * Configure windows to be draggable by default
     */
    public void configureDraggable() {
        configureDraggable(RootPanel.get());
    }

    public void setBoundaryPanel(LayoutContainer boundaryPanel) {
        this.windowContainer = boundaryPanel;
    }

    public void configureDraggable(final AbsolutePanel boundaryPanel) {
        /** Need to capture dragging for attach and detach methods **/
        windowDragController = new PickupDragController(boundaryPanel, true) {
            /* (non-Javadoc)
             * @see com.allen_sauer.gwt.dnd.client.PickupDragController#dragStart()
             */
            @Override
            public void dragStart() {
                dragging = true;
                super.dragStart();
            }

            /* (non-Javadoc)
             * @see com.allen_sauer.gwt.dnd.client.PickupDragController#dragEnd()
             */
            @Override
            public void dragEnd() {
                super.dragEnd();
                dragging = false;
            }
        };

        windowDragController.setBehaviorMultipleSelection(false);
        windowDragController.registerDropController(new AbsolutePositionDropController(RootPanel.get()));
        windowDragController.makeDraggable(this, dragHandle);
    }

    public void setDraggable(final boolean draggable) {
        if (!draggable) {
            windowDragController.makeNotDraggable(this);
            /**Sets the cursor to Default on draggable area. Stops the User thinking they can drag the window (Used in Call Failure Analysis Window)**/
            dragHandle.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        } else {
            windowDragController.makeDraggable(this, dragHandle);
            dragHandle.getElement().getStyle().setCursor(Style.Cursor.MOVE);         //Changes the Cursor to the Move icon when the Window is draggable.
        }
    }

    /**
     * @param widget
     */
    public void setContent(final Widget widget) {
        windowContent.setWidget(widget);
    }

    @Override
    public void setTitle(final String title) {
        windowTitle.getElement().setInnerText(title);
    }

    public void center() {
        centered = true;
        final int left = (getParentWidth() - getOffsetWidth()) >> 1;
        final int top = (getParentHeight() - getOffsetHeight()) >> 1;
        setPosition(Math.max(getScrollLeft() + left, 0), Math.max(getScrollTop() + top, 0));
    }

    protected int getParentWidth() {
        if (windowContainer != null) {
            return windowContainer.getOffsetWidth();
        }
        return Window.getClientWidth();
    }

    protected int getParentHeight() {
        if (windowContainer != null) {
            return windowContainer.getOffsetHeight();
        }
        return Window.getClientHeight();
    }

    protected Element getParentContainer() {
        return windowContainer == null ? Document.get().getBody() : windowContainer.getElement();
    }

    protected int getScrollLeft() {
        if (windowContainer != null) {
            return 0;
        }
        return Window.getScrollLeft();
    }

    protected int getScrollTop() {
        if (windowContainer != null) {
            return 0;
        }
        return Window.getScrollTop();
    }

    /**
     * @param left
     * @param top
     */
    private void setPosition(final int left, final int top) {
        final Element elem = getElement();
        elem.getStyle().setPropertyPx("left", left);
        elem.getStyle().setPropertyPx("top", top);

    }

    protected void unbind() {

    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onAttach() {
        /** Dragging attachs and detachs this element, so ignore attaching glass and handlers when dragging **/
        if (!dragging) {
            if (isGlassEnabled) {
                getParentContainer().appendChild(glass);
                resizeRegistration = Window.addResizeHandler(resizeHandler);
                resizeHandler.onResize(null);
            }
        }
        super.onAttach();
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onDetach()
     */
    @Override
    protected void onDetach() {
        super.onDetach();
        /** Dragging attachs and detachs this element, so ignore removing glass and handlers when dragging **/
        if (!dragging) {
            if (glass != null) {
                getParentContainer().removeChild(glass);
                glass = null;
            }
            if (resizeRegistration != null) {
                resizeRegistration.removeHandler();
            }
        }
    }

    /**
     * Fires the closed event of this window to its listeners.
     */
    private void fireWindowClosed() {
        for (final EniqWindowListener listener : windowListeners) {
            listener.windowClosed(new EniqWindowEvent(this));
        }
    }

    public void setGlassEnabled(final boolean isEnabled) {
        this.isGlassEnabled = isEnabled;
        if (isEnabled && glass == null) {
            glass = Document.get().createDivElement();
            glass.setClassName(glassStyleName);

            glass.getStyle().setPosition(Position.ABSOLUTE);
            glass.getStyle().setLeft(0, Unit.PX);
            glass.getStyle().setTop(0, Unit.PX);
            glass.getStyle().setZIndex(currentZIndex - 1);
        }
    }

    /**
     * @param listener
     */
    public void addWindowListener(final EniqWindowListener listener) {
        windowListeners.add(listener);
    }

    /**
     * @param listener
     */
    public void removeWindowListener(final EniqWindowListener listener) {
        windowListeners.remove(listener);
    }

    public SimplePanel getWindowContentPanel() {
        return windowContent;
    }

    /**
     * Window resize handler used to keep the glass the proper size.
     */
    private final ResizeHandler resizeHandler = new ResizeHandler() {
        @Override
        public void onResize(final ResizeEvent event) {
            final Style style = glass.getStyle();

            final int winWidth = getParentWidth();
            final int winHeight = getParentHeight();

            // Hide the glass while checking the document size. Otherwise it would
            // interfere with the measurement.
            style.setDisplay(Display.NONE);
            style.setWidth(0, Unit.PX);
            style.setHeight(0, Unit.PX);

            final int width = Document.get().getScrollWidth();
            final int height = 0;//Document.get().getScrollHeight();

            // Set the glass size to the larger of the window's client size or the
            // document's scroll size.
            style.setWidth(Math.max(width, winWidth), Unit.PX);
            style.setHeight(Math.max(height, winHeight), Unit.PX);

            // The size is set. Show the glass again.
            style.setDisplay(Display.BLOCK);
            if (centered) {
                center();
            }
        }
    };
}
