/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.ui.client.main.GenericTabView;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Handle window cascade and tiling on behalf 
 * of the Menu task bar for the tab 
 * (the window owner)
 *
 * @author eeicmsy
 * @since May 2011
 *
 */
public class CascadeTileHelper {

    /**
     * Space required to be added for all browsers
     */
    public static final int WINDOW_SPACE_HACK = 50;

    private static final Logger LOGGER = Logger.getLogger(CascadeTileHelper.class.getName());

    /*
     * how much will be pushing windows out by in the cascade 
     * (note selenium testing may check if windows are cascaded based on this)
     */
    private static final int CASCADE_WINDOW_OFFSET = 40;

    private final GenericTabView genericTabViewParent;

    public CascadeTileHelper(final GenericTabView genericTabViewParent) {
        this.genericTabViewParent = genericTabViewParent;
    }

    /**
     * Cascade all windows. 
     * This  method can be called as part of
     * listener on button or listener on a menu item.
     */
    public void cascade(final Collection<BaseWindow> ownedBaseWindows, final List<Dialog> dialogsOnFront) {
        LOGGER.fine("cascade");

        int i = 0;
        final int defaultWidth = getDefaultCascadeWidth();
        final int defaultHeight = getDefaultCascadeHeight();
        // owned windows in in the order they were created

        final List<BaseWindow> visibleWindows = getOpenWindows(ownedBaseWindows);
        for (final BaseWindow win : visibleWindows) {
            final int x = 100 + (i * CASCADE_WINDOW_OFFSET);
            final int y = 120 + (i * CASCADE_WINDOW_OFFSET);

            win.setPositionAndSize(x, y, defaultWidth, defaultHeight);

            i++;
        }
        resetDialogsWhichHaveToBeToFront(visibleWindows, dialogsOnFront);
        genericTabViewParent.layout(true);
    }

    /**
     * Tile all visible windows into the center panel
     * (which excludes the taskbar). Put any property windows 
     * which are in tab to the front
     * . 
     */
    public void tile(final Collection<BaseWindow> ownedBaseWindows, final List<Dialog> dialogsOnFront) {

        final List<BaseWindow> visibleWindows = getOpenWindows(ownedBaseWindows);
        tile(visibleWindows);
        resetDialogsWhichHaveToBeToFront(visibleWindows, dialogsOnFront);
        genericTabViewParent.layout(true);
    }

    /*
     * Because of multi-mode (and just generally as there are 
     * a lot of windows) reduce the tile and cascade to operate on open windows
     * - not pulling in all minimised windows
     * 
     * @param ownedBaseWindows   current set of windows owned by the menu taskbar (tab)
     * @return                   open windows
     */
    private List<BaseWindow> getOpenWindows(final Collection<BaseWindow> ownedBaseWindows) {
        final List<BaseWindow> allWindows = new ArrayList<BaseWindow>(ownedBaseWindows);

        final List<BaseWindow> visibleWindows = new ArrayList<BaseWindow>();
        for (final BaseWindow winRef : allWindows) {
            if (!winRef.isMinimised()) {
                visibleWindows.add(winRef);
            }
        }
        return visibleWindows;
    }

    /*
     * Reuse tile functionality to get around IE not maximising correctly
     */
    private void tile(final List<BaseWindow> windows) {
        LOGGER.fine("tile");
        final int numWindows = windows.size();
        final ContentPanel centerPanel = genericTabViewParent.getWindowContainer(); // Rear panel with product logo

        int nbRows = (int) Math.ceil(Math.sqrt(numWindows));
        final int nbCols = nbRows;
        int nbColslInLastRow = nbCols - (nbRows * nbCols - numWindows);
        if (nbColslInLastRow <= 0) {
            nbRows--;
            nbColslInLastRow = nbCols - (nbRows * nbCols - numWindows);
        }

        // Hack - need to remove the height of the browser status bar somehow.
        final int areaHeight = centerPanel.getOffsetHeight() - WINDOW_SPACE_HACK;
        final int areaWidth = centerPanel.getOffsetWidth();

        int currentRow = 0, currentCol = 0;
        final int winHeight = areaHeight / nbRows;

        final int margin = 5;

        LOGGER.finer("Tiling " + windows.size() + " windows in Center Panel");
        LOGGER.finer("centerPanel:h=" + areaHeight + " w=" + areaWidth);
        for (final BaseWindow win : windows) {
            int winWidth = areaWidth / (currentRow == nbRows - 1 ? nbColslInLastRow : nbCols);

            int x = currentCol * winWidth;
            int y = currentRow * winHeight;

            LOGGER.finer("window:x=" + x + " y=" + y + " w=" + winWidth + " h=" + winHeight);

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
        // Repaint to tidy up any painting issues
        //        genericTabViewParent.layout(true);
    }

    /* hack - cascade above could be putting our floating dialog to back  */
    private void resetDialogsWhichHaveToBeToFront(final List<BaseWindow> visibleWindows,
                                                  final List<Dialog> dialogsOnFront) {

        /* for some insane reason calling dialog.toFront() on dialog(s) in 
         * dialogsOnFront is having no affect so putting the rest to the back instead
         * whenever a "always on top" dialog is present
         */
        if (!dialogsOnFront.isEmpty()) {
            for (final BaseWindow win : visibleWindows) {
                win.toBack();
            }
        }
    }

    /* 
     * Width of window when appearing as cascade
     * Was going to use Base window launch size (but its better if they are smaller)
     */
    int getDefaultCascadeWidth() {
        return (genericTabViewParent.getOffsetWidth()) / 2;
    }

    int getDefaultCascadeHeight() {
        return (genericTabViewParent.getOffsetHeight()) / 2;
    }

}
