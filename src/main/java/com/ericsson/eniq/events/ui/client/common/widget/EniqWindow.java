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
package com.ericsson.eniq.events.ui.client.common.widget;

import com.ericsson.eniq.events.ui.client.datatype.WindowPropertiesDataType;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.ResizeEvent;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

import java.util.logging.Logger;

import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;

/**
 * @author edmibuz
 */
public class EniqWindow extends Window {
    private static final EniqWindowResourceBundle resources;

    static{
        resources = GWT.create(EniqWindowResourceBundle.class);
        resources.style().ensureInjected();
    }

    private static final Logger LOGGER = Logger.getLogger(EniqWindow.class.getName());

    private static final int WINDOW_POSITION_OFFSET = 40;

    /* window height if fail to get screen size */
    private static final int DEFAULT_HEIGHT = 400;

    /* window width if fail to get screen size */
    private static final int DEFAULT_WIDTH = 300;

    private static final int MAXIMISE_MARGIN = 5;
    /**
     * see listener on launchButton (in MenuTaskBarButton)
     */
    private boolean isMinimised = false;

    /*
     * title defined by the widget that is currently been viewed within the
     * window i.e grid or chart
     */
    private String widgetTitle = EMPTY_STRING;

    /**
     * Area window sits in
     */
    private final ContentPanel container;

    /**
     * @see #maximumSize
     */
    private boolean isMaximizing = false;

    /**
     * Not null only during window maximizing
     *
     * @see #isMaximizing
     */
    private Size maximumSize;

    private boolean isHiding;

    private boolean isExtRestoreStateToApply = false;

    private Point restoreWinPos;

    private Size restoreWinSize;

    public EniqWindow(final ContentPanel container, final String title, final String icon) {
        this.container = container;

        super.hidden = false;

        setMaximizable(true);
        setMinimizable(true);

        setSize(getDefaultLaunchWidth(), getDefaultLaunchHeight());

        /* ensure can not drag the window outside center panel */
        getDraggable().setContainer(container);
        setContainer(container.getElement());
        getDraggable().setMoveAfterProxyDrag(false);

        updateTitle(title);
        widgetTitle = title;

        if (icon != null && icon.length() > 0) {
            this.setIconStyle(icon);
        }

        this.setLayout(new FitLayout());
    }

    public final void updateTitle(final String title) {
        setHeading(title);
    }

    /*
     * Fetching a default floating window width based on screen size.
     * NOTE Will NOT use these for cascade function (as too large)
     * (could make this a public static method if useful)
     *
     * @return default width for a new window
     */
    public int getDefaultLaunchWidth() {
        final int defaultWidth = getScreenWidth() / 2;
        return (defaultWidth != 0) ? defaultWidth : DEFAULT_WIDTH;
    }

    /*
     * Fetching a default floating window height based on screen size.
     * NOTE Will NOt use these for cascade function (as too large) (could make
     * this a public static method if useful)
     *
     * @return default width for a new window
     */
    public int getDefaultLaunchHeight() {
        final int defaultHeight = getScreenHeight() / 2;
        return (defaultHeight != 0) ? defaultHeight : DEFAULT_HEIGHT;
    }

    // fetching screen width using native javascript
    private static native int getScreenWidth() /*-{
        return $wnd.screen.width;
    }-*/;

    // fetching screen height using native javascript
    private static native int getScreenHeight() /*-{
        return $wnd.screen.height;
    }-*/;

    @Override
    public void maximize() {
        if (isMaximizable() && !isMaximized()) {
            isMaximizing = true;
            super.maximize();
            isMaximizing = false;

            setSize(maximumSize.width - (MAXIMISE_MARGIN * 2) - (WorkspaceConstants.KPI_PANEL_OFFSET + WorkspaceConstants.LAUNCH_TAB_OFFSET),
                    maximumSize.height - MAXIMISE_MARGIN * 2);
            maximumSize = null;
            setPosition(MAXIMISE_MARGIN + WorkspaceConstants.LAUNCH_TAB_OFFSET, MAXIMISE_MARGIN);
        }
    }

    @Override public void setSize(int width, int height) {
        if (!isMaximizing) {
            super.setSize(width, height);
        } else { // isMaximizing
            maximumSize = new Size(width, height);
        }
    }

    /**
     * Hides the window.
     *
     * @param buttonPressed the button that was pressed or null
     */
    @Override public void hide(Button buttonPressed) {
        isHiding = true;
        super.hide(buttonPressed);
        isHiding = false;
    }


    public ContentPanel getContentPanel(){
        return container;
    }

    @Override public Size getSize() {
        Size size = super.getSize();
        if (isMaximizing) {
            if (isExtRestoreStateToApply) {
                size = restoreWinSize;
            } else if (!isMaximized()){
                restoreWinSize = size; // intercept value and store here
            }
        } else if (isHiding) {
            restoreWinSize = size; // intercept value and store here
        }
        return size;
    }

    @Override public Point getPosition(boolean local) {
        Point position = super.getPosition(local);
        if (isMaximizing) {
            if (isExtRestoreStateToApply) {
                position = restoreWinPos;
            } else if (!isMaximized()) {
                restoreWinPos = position; // intercept value and store here
            }
        } else if (isHiding) {
            restoreWinPos = position; // intercept value and store here
        }
        return position;
    }

    public Point getRestoreWinPos() {
        return restoreWinPos;
    }

    public Size getRestoreWinSize() {
        return restoreWinSize;
    }

    public void maximize(WindowPropertiesDataType winRestoreProps) {
        if (winRestoreProps != null && winRestoreProps.isMaximized) {
            restoreWinPos = winRestoreProps.restoreWinPos;
            restoreWinSize = winRestoreProps.restoreWinSize;
            isExtRestoreStateToApply = true;
            maximize();
            isExtRestoreStateToApply = false;
        }
    }

    @Override
    public void minimize() {
        // el().slideOut(Direction.UP, FxConfig.NONE);
        el().setVisibility(false);
        isMinimised = true;
        super.minimize();
    }

    /*
     * Overriding to handle the minimised state
     */
    @Override
    public void restore() {
        super.restore(); // Handles the maximised state

        if (isMinimised) {
            el().setVisibility(true);
            isMinimised = false;
            /** ecarsea - Fire the show event - Elements such as charts within the window may sometimes need to made
             * invisible as well as the parent window, so need to fire the show event in order that Window elements can
             * restore visibility when restored from a minimized state
             */
            fireEvent(Events.Show);
        }

        /*
         * To ensure on restore, the window fits within the constrain area.
         */
        final Size size = getSize();
        if ((size.width > container.getWidth() - WorkspaceConstants.KPI_PANEL_OFFSET) || (size.height > container.getHeight())) {
            fitContainer();
        }
    }

    @Override
    protected void onWindowResize(final int width1, final int height1) {
        if (isVisible()) {
            if (isMaximized()) {
                fitContainer();
            }
            if (isModal() && getModalPanel() != null) {
                getModalPanel().syncModal();
            }
        }
    }

    /**
     *  This method is fired whenever a BaseWindow is resized.
     *  It is executed after a resize occurs and the size of BaseWindow
     *  is restricted inside it's container.
     */
    // TODO: Find another way how to constrain window resizing, as setSize triggers another onResize event call
    @Override
    protected void onEndResize(final ResizeEvent re) {
        final int constrainHeight = container.getHeight();
        final int constrainWidth = container.getWidth();

        final Element elem = this.getElement();

        if (elem != null) {
            final int height = getHeight();
            final int width = getWidth();

            final int left = elem.getAbsoluteLeft();
            int elementOffset = elem.getOffsetTop();

            if (elementOffset < 0) {
                final int elementHeight = height + elementOffset;
                setPosition(left, 0);
                setSize(getSize().width, elementHeight);
            }
            if (elementOffset < 0) {
                elementOffset = 0;
            }

            final int adjustedHeight = height + elementOffset;

            if (adjustedHeight > constrainHeight) {
                final int finalHeight = constrainHeight - elementOffset; //adjust window height with respect to the bottom boundary of the container
                setPosition(left, elementOffset);
                setSize(getSize().width, finalHeight);

            } else if (width > constrainWidth) {
                final int finalWidth = constrainWidth - left; //adjust window width with respect to the container width
                setPosition(left, elementOffset);
                setSize(finalWidth, height);
            }
        }

        super.onEndResize(re);
    }

    /**
     * Update the window stack (WindowManager.accessList)
     * while launching new window or drilling down on existing window.
     */
    public void putWindowToFront() {
        if (super.manager != null) {
            super.manager.getStack().push(this);
        }
        toFront();
    }

    public String getWindowTitle() {
        return widgetTitle;
    }

    /**
     * Sets static part of the window title (e.g. EventAnalysis)
     * @param widgetTitle title string
     */
    public void setWindowTitle(final String widgetTitle) {
        this.widgetTitle = widgetTitle;
    }

    /**
     * To ensure the new window launched fits within the constrain area.
     * For scenarios where the browser is not in maximised mode.
     */
    public void fitIntoContainer() {
        if (isVisible() && isRendered()) {
            if ((getSize().width > container.getWidth()) || (getSize().height > container.getHeight())) {
                fitContainer();
            }
        }
    }

    /**
     * Method that allows for child controls
     * to be added to this Component
     *
     * @param child child widget
     */
    public void addWidget(final Widget child) {
        add(child);
        layout();
    }

    public Point setWindowPosition(Point lastPosition) {
        /** Check if this is the only window in the panel. If so reset position back to starting position **/
        if (container.getItemCount() == 1) {
            lastPosition = new Point(0, 0);
        }

        final int newX;
        if ((lastPosition.x + WINDOW_POSITION_OFFSET) + getWidth() > container.getInnerWidth()) {
            newX = container.getInnerWidth() - getWidth();
        } else {
            newX = lastPosition.x + WINDOW_POSITION_OFFSET;
        }

        final int newY;
        if ((lastPosition.y + WINDOW_POSITION_OFFSET) + getHeight() > container.getInnerHeight()) {
            newY = container.getInnerHeight() - getHeight();
        } else {
            newY = lastPosition.y + WINDOW_POSITION_OFFSET;
        }

        setPosition(newX, newY);
        return new Point(newX, newY);
    }

    public void setPositionAndSize(final int x, final int y, final int width, final int height) {
        LOGGER.fine("setPositionAndSize");

        disableEvents(true); // see if helps
        restore(); // No maximised check as the restore function provides this

        setPosition(x, y); // Position MUST be set before Size or window positioning is "hosed"

        setSize(width, height);

        toFront(); // this is necessary to last one is in wrong position
        disableEvents(false); // see if helps
    }

    public boolean isMinimised() {
        return isMinimised;
    }

    public void bringToFront() {
        if (isMinimised()) {
            restore();
        }
        toFront();
        focus();
    }

    /*
    * Override for public access
    *
    */
    @Override
    public void fitContainer() {
        super.fitContainer();
    }

    public void noticeMe(boolean isUserNoticeNeeded) {
        if (isUserNoticeNeeded){
            el().addStyleName(resources.style().noticeMe());
            if (isMinimised()){
                el().setVisibility(true);
            }
        } else{
            el().removeStyleName(resources.style().noticeMe());
            if (isMinimised()){
                el().setVisibility(false);
            }
        }
    }

    public void noticeMeEnd(){
        el().addStyleName(resources.style().noticeMeAnimation());
        Timer removeAnimation = new Timer() {
            @Override
            public void run() {
                el().removeStyleName(resources.style().noticeMeAnimation(),
                        resources.style().noticeMe());
            }
        };
        //This setting is the total time taken to animate the fading of the border around the window
        //If the animation time is changed in EniqWindow.css, then it must also be changed here.
        int removeAnimationStyleInMS = 4051;
        removeAnimation.schedule(removeAnimationStyleInMS);
    }
}
