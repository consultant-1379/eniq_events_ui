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
import com.extjs.gxt.ui.client.util.Point;
import org.apache.fop.fo.pagination.Title;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class EniqWindowUertt extends Window {
    private ContentPanel windowContainer;
    private String windowHeader;
    private static Point currentWindowPosition = new Point(60, 70);
    private static int WINDOW_POSITION_OFFSET = 40;
    // Offset increments if the window start touching container's boundary
    private static int countXOffset = 0;
    private static int countYOffset = 0;
    private static int windowsAwayFromContainerBounds = 7;
    private final String SELENIUM_UERTT_WINDOW = "UERTT WINDOW";

    public EniqWindowUertt(final ContentPanel windowContainer)
    {
        this.windowContainer = windowContainer;
        getDraggable().setContainer(windowContainer);
        setContainer(windowContainer.getElement());
        getDraggable().setMoveAfterProxyDrag(false);
        this.setLayout(new FitLayout());
    }

    public void fitIntoContainer() {
        if (isVisible() && isRendered()) {
            if ((getSize().width > windowContainer.getWidth()) || (getSize().height > windowContainer.getHeight())) {
                fitContainer();
            }
        }
    }

    @Override
    public void fitContainer() {
        super.fitContainer();
    }

    @Override
    public void onHide() {

        // Reset the current window position if any of the window is closed
        if(countXOffset > 0)
        {}
        else
        {
            currentWindowPosition.x -= WINDOW_POSITION_OFFSET;
        }
        
        if (countYOffset > 0)
        {}
        else
        {
            currentWindowPosition.y -= WINDOW_POSITION_OFFSET;
        }
        super.onHide();        
    }

    public void updateTitle(final String title) {
        setHeading(title);
    }

    private Point setWindowPosition(Point lastPosition, ContentPanel windowContainer) {
        /** Check if this is the only window other than launch window in the panel. If so reset position back to starting position **/
        if (windowContainer.getItemCount() == 2) {
            lastPosition = new Point(60, 70);
            countXOffset = 0;
            countYOffset = 0;
        }
        else if (windowContainer.getItemCount() > 2 && windowContainer.getItemCount() <= windowsAwayFromContainerBounds)
        {
            countXOffset = 0;
            countYOffset = 0;
        }

        final int newX;
        if ((lastPosition.x + WINDOW_POSITION_OFFSET) + getWidth() > windowContainer.getInnerWidth()) {
            newX = windowContainer.getInnerWidth() - getWidth();
            countXOffset++;
        } else {
            newX = lastPosition.x + WINDOW_POSITION_OFFSET;
        }

        final int newY;
        if ((lastPosition.y + WINDOW_POSITION_OFFSET) + getHeight() > windowContainer.getInnerHeight()) {
            newY = windowContainer.getInnerHeight() - getHeight();
            countYOffset++;
        } else {
            newY = lastPosition.y + WINDOW_POSITION_OFFSET;
        }

        setPosition(newX, newY);

        return new Point(newX, newY);
    }

    public void setEventDetailWindowPosition(EniqWindowUertt window, ContentPanel container)
    {
        currentWindowPosition = window.setWindowPosition(currentWindowPosition, container);
    }


    private void setSeleniumTags() {
        //set selenium tag on window
        getElement().setId(CommonConstants.SELENIUM_TAG+SELENIUM_UERTT_WINDOW);
    }

}


