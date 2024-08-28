/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import com.ericsson.eniq.events.ui.client.common.widget.EniqWindow;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.user.client.ui.Widget;

/**
 * Data type storing a window's current position settings at time
 * of request.
 *
 * @author eeicmsy
 * @since May 2010
 */
public class WindowPropertiesDataType {

    /**
     * Window absolute left position (from #display.asWidget().getAbsoluteLeft())
     */
    public final int absoluteLeft;

    /**
     * Window absolute top position (from #display.asWidget().getAbsoluteTop())
     */
    public final int absoluteTop;

    /**
     * Window "offsetWidth" property (from #display.asWidget().getOffsetWidth())
     */
    public final int offsetWidth;

    /**
     * Window "offsetHeight" property (from #display.asWidget().getOffsetHeight())
     */
    public final int offsetHeight;

    /**
     * Window restore position. Can be <tt>null</tt>.
     */
    public final Point restoreWinPos;

    /**
     * Window restore size. Can be <tt>null</tt>.
     */
    public final Size restoreWinSize;

    /**
     * For restoring window maximised/normal state. It is <tt>false</tt> if it is not <tt>EniqWindow</tt>.
     */
    public final boolean isMaximized;

    /**
     * Store current window properties for size, position, and normal/maximized state
     *
     * @param win window widget
     */
    public WindowPropertiesDataType(Widget win) {
        this.absoluteLeft = win.getAbsoluteLeft();
        this.absoluteTop = win.getAbsoluteTop();
        this.offsetWidth = win.getOffsetWidth();
        this.offsetHeight = win.getOffsetHeight();

        if (win instanceof EniqWindow) {
            EniqWindow eniqWindow = (EniqWindow) win;
            this.isMaximized = eniqWindow.isMaximized();
            this.restoreWinSize = eniqWindow.getRestoreWinSize();
            this.restoreWinPos = eniqWindow.getRestoreWinPos();
        } else {
            this.isMaximized = false;
            this.restoreWinPos = null;
            this.restoreWinSize = null;
        }
    }
}

