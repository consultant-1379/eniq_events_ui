/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.workspace;

import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.extjs.gxt.ui.client.util.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.*;

public class WindowPositionHelper {

    private final IWindowContainer windowContainer;

    private final Map<String, BaseWindow> windowsMap;

    private static final int CASCADE_WINDOW_OFFSET = 40;

    private static final int SPACE_BETWEEN_CONFIG_PANELS_ROW = 60;

    private static final int SPACE_BETWEEN_CONFIG_PANELS_COLUMN = 10;

    private static final int CONFIG_WIN_WIDTH = 327;

    private static final int CONFIG_WIN_HEIGHT = 140;

    private Point lastOpenedWindowPosition;

    /**
     * @param windowContainer
     * @param windowsMap
     */
    public WindowPositionHelper(IWindowContainer windowContainer, Map<String, BaseWindow> windowsMap) {
        this.windowContainer = windowContainer;
        this.windowsMap = windowsMap;
    }

    public WindowPositionInfo[] getWindowPositionInfoForTiling(int windowCount, boolean pinned) {
        int launchMenuOffSet = getLaunchMenuOffset(pinned);
        int containerWidth = windowContainer.getWindowContainerPanel().getInnerWidth() - (launchMenuOffSet + KPI_PANEL_OFFSET);
        int containerHeight = windowContainer.getWindowContainerPanel().getInnerHeight();

        WindowPositionInfo[] positionInfoArr = new WindowPositionInfo[windowCount];

        int nbRows = (int) Math.ceil(Math.sqrt(windowCount));
        final int nbCols = nbRows;
        int nbColslInLastRow = nbCols - (nbRows * nbCols - windowCount);
        if (nbColslInLastRow <= 0) {
            nbRows--;
            nbColslInLastRow = nbCols - (nbRows * nbCols - windowCount);
        }

        int currentRow = 0, currentCol = 0;
        final int winHeight = containerHeight / nbRows;

        final int margin = 5;

        for (int i = 0; i < windowCount; i++) {
            int winWidth = containerWidth / (currentRow == nbRows - 1 ? nbColslInLastRow : nbCols);

            /** Offset windows in the first column to account for the launch tab **/
            int x = currentCol * winWidth + launchMenuOffSet;
            int y = currentRow * winHeight;

            winWidth -= margin * 2;

            x += margin;
            y += margin;

            currentCol++;
            if (currentCol >= nbCols) {
                currentCol = 0;
                currentRow++;
            }
            positionInfoArr[i] = new WindowPositionInfo(winWidth, winHeight - margin * 2, x, y);
            lastOpenedWindowPosition = new Point(30, 10);
        }
        return positionInfoArr;
    }

    public WindowPositionInfo[] getConfigWindowPositionInfoForTiling(int windowCount, boolean pinned) {
        int launchMenuOffSet = getLaunchMenuOffset(pinned);
        int containerWidth = windowContainer.getWindowContainerPanel().getInnerWidth() - (launchMenuOffSet + KPI_PANEL_OFFSET);
        int containerHeight = windowContainer.getWindowContainerPanel().getInnerHeight();
        WindowPositionInfo[] positionInfoArr = new WindowPositionInfo[windowCount];

        int currentRow = 0;
        int currentCol = 0;
        final int margin = 5;

        if (windowCount == 1 && windowContainer.getWindowContainerPanel().getItemCount() == 0) {
            currentCol = 0;
        } else if (windowCount == 1 && windowContainer.getWindowContainerPanel().getItemCount() >= 1) {
            currentCol++;
        }

        else if (windowCount == 2) {
            currentCol = 0;
        }

        for (int i = 0; i < windowCount; i++) {

            if (windowContainer.getWindowContainerPanel().getItemCount() == 0 || lastOpenedWindowPosition == null) {
                lastOpenedWindowPosition = new Point(30, 10);
            }

            int x = pinned && (windowContainer.getWindowContainerPanel().getItemCount() == 0) ? (lastOpenedWindowPosition.x + currentCol
                    * (CONFIG_WIN_WIDTH + margin + SPACE_BETWEEN_CONFIG_PANELS_COLUMN) + LAUNCH_MENU_OFFSET)
                    : (lastOpenedWindowPosition.x + currentCol * (CONFIG_WIN_WIDTH + margin + SPACE_BETWEEN_CONFIG_PANELS_COLUMN));
            int y = lastOpenedWindowPosition.y + currentRow * (CONFIG_WIN_HEIGHT + margin);
            int remainingWidth = containerWidth - (lastOpenedWindowPosition.x + currentCol * (CONFIG_WIN_WIDTH + margin));
            int remainingHeight;

            if (remainingWidth >= CONFIG_WIN_WIDTH) {
                currentCol++;
                lastOpenedWindowPosition = new Point(x, y);
            }

            else if (remainingWidth < CONFIG_WIN_WIDTH) {
                currentCol = 1;
                currentRow++;
                x = pinned ? (30 + LAUNCH_MENU_OFFSET) : 30;
                y = lastOpenedWindowPosition.y + currentRow * (CONFIG_WIN_HEIGHT + margin) + SPACE_BETWEEN_CONFIG_PANELS_ROW;
                remainingHeight = containerHeight - y;
                if (remainingHeight < CONFIG_WIN_HEIGHT) {
                    x = x + 20;
                    y = lastOpenedWindowPosition.y + 20;
                }
                lastOpenedWindowPosition = new Point(x, y);
            }
            positionInfoArr[i] = new WindowPositionInfo(CONFIG_WIN_WIDTH, CONFIG_WIN_HEIGHT, x, y);
        }
        return positionInfoArr;
    }

    public WindowPositionInfo[] getWindowPositionInfoForCascade(int windowCount, boolean pinned) {
        int launchMenuOffset = getLaunchMenuOffset(pinned);
        int containerWidth = windowContainer.getWindowContainerPanel().getInnerWidth() - KPI_PANEL_OFFSET;
        int containerHeight = windowContainer.getWindowContainerPanel().getInnerHeight();
        final int defaultWidth = getDefaultCascadeWidth(launchMenuOffset);
        final int defaultHeight = getDefaultCascadeHeight();

        WindowPositionInfo[] positionInfoArr = new WindowPositionInfo[windowCount];

        for (int i = 0; i < windowCount; i++) {
            final int x = 20 + (i * CASCADE_WINDOW_OFFSET) + launchMenuOffset;
            final int y = 25 + (i * CASCADE_WINDOW_OFFSET);

            final int newX;
            if (x + defaultWidth > containerWidth) {
                newX = containerWidth - defaultWidth;
            } else {
                newX = x;
            }

            final int newY;
            if (y + +defaultHeight > containerHeight) {
                newY = containerHeight - defaultHeight;
            } else {
                newY = y;
            }

            positionInfoArr[i] = new WindowPositionInfo(defaultWidth, defaultHeight, newX, newY);
            i++;
        }
        return positionInfoArr;
    }

    /**
     * @param pinned
     * @return
     */
    private int getLaunchMenuOffset(boolean pinned) {
        return pinned ? LAUNCH_MENU_OFFSET : LAUNCH_TAB_OFFSET;
    }

    public static class WindowPositionInfo {
        private final int width;

        private final int height;

        private final int x;

        private final int y;

        /**
         * @param width
         * @param height
         * @param x
         * @param y
         */
        public WindowPositionInfo(int width, int height, int x, int y) {
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
        }

        /**
         * @return the width
         */
        public int getWidth() {
            return width;
        }

        /**
         * @return the height
         */
        public int getHeight() {
            return height;
        }

        /**
         * @return the x
         */
        public int getX() {
            return x;
        }

        /**
         * @return the y
         */
        public int getY() {
            return y;
        }
    }

    public void cascade(boolean pinned) {
        int i = 0;
        int launchMenuOffset = getLaunchMenuOffset(pinned);
        final int defaultWidth = getDefaultCascadeWidth(launchMenuOffset);
        final int defaultHeight = getDefaultCascadeHeight();

        /** not cascading minimized windows **/
        for (final BaseWindow win : getOpenWindows()) {
            final int x = 20 + (i * CASCADE_WINDOW_OFFSET) + launchMenuOffset;
            final int y = 25 + (i * CASCADE_WINDOW_OFFSET);

            final int newX;
            if (x + defaultWidth > windowContainer.getWindowContainerPanel().getInnerWidth()) {
                newX = windowContainer.getWindowContainerPanel().getInnerWidth() - defaultWidth;
            } else {
                newX = x;
            }

            final int newY;
            if (y + +defaultHeight > windowContainer.getWindowContainerPanel().getInnerHeight()) {
                newY = windowContainer.getWindowContainerPanel().getInnerHeight() - defaultHeight;
            } else {
                newY = y;
            }

            win.setPositionAndSize(newX, newY, defaultWidth, defaultHeight);

            i++;
        }
        windowContainer.getWindowContainerPanel().layout(true);
    }

    private List<BaseWindow> getOpenWindows() {
        final List<BaseWindow> visibleWindows = new ArrayList<BaseWindow>();
        for (final BaseWindow winRef : windowsMap.values()) {
            if (!winRef.isMinimised()) {
                visibleWindows.add(winRef);
            }
        }
        return visibleWindows;
    }

    /**
     * @return
     */
    private int getDefaultCascadeHeight() {
        return windowContainer.getWindowContainerPanel().getOffsetHeight() / 2;
    }

    /**
     * @return
     */
    private int getDefaultCascadeWidth(int offset) {
        return (windowContainer.getWindowContainerPanel().getOffsetWidth() - offset) / 2;
    }

    public void tile(boolean pinned) {
        List<BaseWindow> openWindows = getOpenWindows();
        int launchMenuOffset = getLaunchMenuOffset(pinned);
        final int numWindows = openWindows.size();

        int nbRows = (int) Math.ceil(Math.sqrt(numWindows));
        final int nbCols = nbRows;
        int nbColslInLastRow = nbCols - (nbRows * nbCols - numWindows);
        if (nbColslInLastRow <= 0) {
            nbRows--;
            nbColslInLastRow = nbCols - (nbRows * nbCols - numWindows);
        }

        // Hack - need to remove the height of the browser status bar somehow.
        final int areaHeight = windowContainer.getWindowContainerPanel().getOffsetHeight();
        final int areaWidth = windowContainer.getWindowContainerPanel().getOffsetWidth() - (launchMenuOffset + KPI_PANEL_OFFSET);

        int currentRow = 0, currentCol = 0;
        final int winHeight = areaHeight / nbRows;

        final int margin = 5;

        for (final BaseWindow win : openWindows) {
            int winWidth = areaWidth / (currentRow == nbRows - 1 ? nbColslInLastRow : nbCols);

            int x = currentCol * winWidth + launchMenuOffset;
            int y = currentRow * winHeight;

            winWidth -= margin * 2;

            x += margin;
            y += margin;

            win.setPositionAndSize(x, y, winWidth, winHeight - margin * 2);

            currentCol++;
            if (currentCol >= nbCols) {
                currentCol = 0;
                currentRow++;
            }
        }
        windowContainer.getWindowContainerPanel().layout(true);
    }
}
