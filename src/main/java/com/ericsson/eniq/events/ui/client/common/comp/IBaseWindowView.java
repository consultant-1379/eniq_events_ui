/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.ui.client.common.widget.EniqWindow;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay;
import com.extjs.gxt.ui.client.widget.ContentPanel;

/**
 * Base window interface
 * 
 * @author eeicmsy
 * @since Feb 2010
 * 
 *
 */
public interface IBaseWindowView extends WidgetDisplay {

    /**
     * Utility to pass our own assign window id (effectively from meta data id)
     * @return  id we put on base window (which is same as what will use for menu item, 
     *          launch button and query)
     */
    String getBaseWindowID();

    /**
     * Get Centre panel this window has to sit in
     * (for current tab)
     * @return constraint area (outside menu taskbar area)
     */
    ContentPanel getConstraintArea();

    /**
     * Set the title variable on window.
     * use to add any additions to title 
     * (e.g. search field parameter input)
     * 
     * @param title - title on the window
     */
    void updateTitle(final String title);

    /**
     * Updates the title variable on window.
     *
     * @param newTitle - new title on the window that will be adjusted before applying
     * @return new full title
     */
    String applyTitle(String newTitle);

    boolean updateSearchFieldDataType(SearchFieldDataType searchInfo);

    /**
     * gets the String title of the window
     * @return Current Title on the Base Window 
     */
    String getBaseWindowTitle();

    /**
     * Append information to title - at end of title (e.g. title (something))
     * No action if string empty or null
     * @param elementClickedForTitleBar  String to append to title
     */
    void appendTitle(final String elementClickedForTitleBar);

    /**
     * gets the window title without
     * any concatenated strings as parameters
     * @return title as String value
     */
    String getBaseWindowTitleWithoutParams();

    /**
     * Add  task button from menu bar
     * Method not called from View construct because 
     * over-riding methods (MultiInstance windows) will need 
     * search data to be ready
     * 
     * @return true if button is added (it won't always be
     *         if maximum window limited are exceeded
     */
    boolean addLaunchButton();

    /**
     * Reset search field info on view at same time as set on 
     * Presenter (for as long as we have two copies).
     * the search field data for a window changes with drilldowns
     *  
     * @param data  data to be in synch with presenter (for multiple instance 
     *              windows where view is used for business logic - e.g. time updates)
     */
    void resetSearchData(final SearchFieldDataType data);

    WindowState getWindowState();

    /** Restore window from minimize if minimized. Bring window to front **/
    void bringToFront();

    void setWindowCategoryId(String categoryId);

    String getWindowCategoryId();

    /**
     *  Puts a highlight around the window. This is used to attract the users
     *  attention to the window.
     * @param isUserNoticeNeeded
     */
    void noticeMe(boolean isUserNoticeNeeded);

    /**
     * This puts a highlight around the window and after a number of seconds
     * fades away.
     */
    public void noticeMeEnd();
    public EniqWindow getWindow();
}
