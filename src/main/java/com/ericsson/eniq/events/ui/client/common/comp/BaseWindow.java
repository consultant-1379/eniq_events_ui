/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.ui.client.common.Constants;
import com.ericsson.eniq.events.ui.client.common.service.TabManager;
import com.ericsson.eniq.events.ui.client.common.service.WindowManager;
import com.ericsson.eniq.events.ui.client.common.widget.AbstractBaseWindowDisplay;
import com.ericsson.eniq.events.ui.client.common.widget.EniqWindow;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.gin.EniqEventsUIGinjector;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.comp.TitleUtils.adjustTitleByPrefixes;
import static com.extjs.gxt.ui.client.event.Events.*;

/**
 *
 * BaseWindow that table and chart views etc should over-ride.
 *
 * @author eendmcm
 * @author eeicmsy
 * @since Feb 2010
 */
public abstract class BaseWindow implements IBaseWindowView, Listener<WindowEvent> {

    /**
     * Can be <tt>null</tt>
     */
    String titlePrePrefix;

    /**
     * Can be <tt>null</tt>
     */
    String titlePrefix;
    
    String titlePostfix;

    String titleBase;

    String fullTitle; // equals "titlePrePrefix - titlePrefix - titleBase > titlePostfix"

    private final String icon;

    /**
     * Area window sits in
     */
    protected final ContentPanel constraintArea;

    /*
     * Multi-instance support Id. Contains Id (winID) shared by Menu menu item,
     * window and launch button to facilitate enabling-disabling etc
     */
    private final MultipleInstanceWinId multiWinId;

    protected BaseToolBar winToolBar = null;

    private final EniqWindow window;

    private final String tabId;

    private final boolean hideToolBar;

    private final WindowManager windowManager;

    private final WindowState windowState;

    private String windowCategoryId = "";

    /**
     * Construct base window view
     *
     *            - null in most cases (use other constructor). Enum used when
     *            window belongs to a multiple instance type (e.g. KPI windows)
     *
     * @param multiWinId
     *            Multi-instance support id (contains tabId). Contains id we put on base window
     *            (which is same as what will use for menu item, launch button
     *            and query

     * @param titleBase
     *            - Window title (which will also be used by launch buttons)
     * @param icon
     *            - Icon for window title (same icon as will be on the menu item
     *            that launched this window)
     *
     * @param eventBus - global application event bus
     *
     * @param hideToolBar - hide the window toolbar
     * @param windowState state of window
     */
    protected BaseWindow(final MultipleInstanceWinId multiWinId,
            final ContentPanel constrainArea, final String titleBase, final String icon, final EventBus eventBus,
            final boolean hideToolBar, WindowState windowState) {
        this.windowState = windowState != null ? windowState : new WindowState();
        this.tabId = multiWinId.getTabId();

        windowManager = createWindowManagerForTab();

        this.constraintArea = constrainArea;

        this.titleBase = adjustTitleByPrefixes(titleBase, titlePrePrefix, titlePrefix);
        this.fullTitle = this.titleBase;

        this.multiWinId = multiWinId;
        updateSearchFieldDataType(multiWinId.getSearchInfo());

        this.icon = icon;
        this.hideToolBar = hideToolBar;

        window = createEniqWindow(constrainArea, icon);

        window.addListener(Minimize, this);
        window.addListener(Restore, this);
        window.addListener(BeforeHide, this);
        window.addListener(Hide, this);
        window.addListener(Show, this);
        window.setId(SELENIUM_TAG + "baseWindow");
        window.setShadow(false);

        // TODO Review usage of
        if (!hideToolBar) {
            winToolBar = new BaseToolBar(multiWinId, eventBus);
            /*
             * set the place holder for the Window toolBar (never null so not
             * checking)
             */
            window.setTopComponent(winToolBar);
        }
    }

    @Override
    public boolean updateSearchFieldDataType(SearchFieldDataType searchInfo) {
        // [should be] fullTitle: <Cell Group name> - Access Area - GSM Call Failure: Event Analysis Summary

        boolean isChanged = false;
        if (searchInfo == null || searchInfo.isEmpty()) {
            return isChanged;
        }

        if (multiWinId.getSearchInfo() != searchInfo) {
            multiWinId.setSearchInfo(searchInfo);
        }

        if (!getWindowState().isDrillDown()) { // For drilled down windows it can be index what is useless for user
            // e.g. 001,,ONRM_RootMo_R:RNC01:RNC01,Ericsson,3G
            titlePrePrefix = searchInfo.getSearchFieldVal();
            if (titlePrePrefix != null) {
                titlePrePrefix = titlePrePrefix.trim();
                if (titlePrePrefix.isEmpty() || titlePrePrefix.equalsIgnoreCase(Constants.NOT_FOR_DISPLAY)) {
                    titlePrePrefix = null;
                }
            }

            // e.g. Access Area
            titlePrefix = searchInfo.getTitlePrefix();
            if (titlePrefix != null) {
                titlePrefix = titlePrefix.trim();
                if (titlePrefix.isEmpty() || titlePrefix.equalsIgnoreCase(Constants.NOT_FOR_DISPLAY)) {
                    titlePrefix = null;
                }
            }
            
            titlePostfix = searchInfo.getTitlePostfix();
            if (titlePostfix != null){
                titlePostfix = titlePostfix.trim();
                if (titlePostfix.isEmpty()){
                    titlePostfix = null;
                }
            }
        }

        return applyPrefixesToFullTitle();
    }

    protected BaseWindow(final MultipleInstanceWinId multiWinId,
            final ContentPanel constrainArea, final String title, final String icon, final EventBus eventBus,
            WindowState windowState) {
        this(multiWinId, constrainArea, title, icon, eventBus, false, windowState);
    }

    @Override
    public WindowState getWindowState() {
        return windowState;
    }

    @Override
    public void resetSearchData(final SearchFieldDataType data) {

        multiWinId.setSearchInfo(data);

        if (!hideToolBar) {
            winToolBar.setResetMultiWinId(multiWinId);
        }
        //  updateSearchFieldDataType(data); As solution for TR HR83479 Issue 2 this line is commented
    }

    /**
     * Update the window stack (WindowManager.accessList)
     * while launching new window or drilling down on existing window.
     */
    public void putWindowToFront() {
        window.putWindowToFront();
    }

    /**
     * Introduced abstract to call method in subclass AbstractBaseWindowDisplay
     */
    @Override
    public abstract void stopProcessing();

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowView#
     * addLaunchButton()
     */
    @Override
    public boolean addLaunchButton() {
        return windowManager.openWindow(this, fullTitle, icon);

    }

    /**
     * Remove task button from menu bar (when remove window) Method extract to
     * support over-ride when have instance buttons (KPI), in which case may be
     * removing menu items
     */
    protected void removeLaunchButton() {
        windowManager.closeWindow(this, fullTitle, icon);
    }

    @Override
    public ContentPanel getConstraintArea() {
        return constraintArea;
    }

    @Override
    public String getBaseWindowID() {
        return multiWinId.getWinId();
    }

    // TODO may remove (used for multiple cancel buttons)
    protected final String generateCompositeId() {
        return multiWinId.generateCompositeId();
    }

    @Override
    public final void updateTitle(final String title) {
        fullTitle = title;
        if (window != null) {
            window.updateTitle(fullTitle);
            windowManager.updateLaunchButtonTitle(this, fullTitle);
        }
        windowManager.updateWindowTitle(this, fullTitle, icon);
    }

    @Override
    public String applyTitle(String newTitle) {
        this.titleBase = adjustTitleByPrefixes(newTitle, titlePrePrefix, titlePrefix);
        applyPrefixesToFullTitle();
        return fullTitle;
    }

    @Override
    public void appendTitle(final String elementClickedForTitleBar) {
        if (window != null && elementClickedForTitleBar != null && !elementClickedForTitleBar.isEmpty()) {
            String title = getBaseWindowTitle() + " (" + elementClickedForTitleBar + ")";
            window.updateTitle(title);
            windowManager.updateWindowTitle(this, title, icon);
        }

    }

    @Override
    public String getBaseWindowTitle() {
        return window.getHeading();
    }

    /**
     * Closes down (presenter without hiding the window (call when know window
     * hidden already)
     */
    private void doNotHideShutDownAndCancelOnGoingCalls() {

        /*
         * hack to cancel on going call (added for when switching from multiple
         * to non-multiple // If a searchable window is open and making a call I
         * want to cancel it
         */
        if (this instanceof AbstractBaseWindowDisplay) {
            final IBaseWindowPresenter presenter = ((AbstractBaseWindowDisplay) this).getPresenter();

            if (presenter != null) {
                presenter.handleShutDown();
            }
        }
    }

    protected void onRestore() {
        // For overriding
    }

    protected void onShow() {
        // For overriding
    }

    protected void onMinimize() {
        // For overriding
    }

    protected void onBeforeHide() {
        // notice the add-removes are over-loaded
        // subclass carries this method want to force mask off
        // on window hide
        stopProcessing();
    }

    /**
     * Please note this is a hack because the close (remove) was
     * not working - suspect super field "hidden" and reversing it (see super
     * class method)
     *
     * (note with GXT 2.2. removed need to call getConstraintArea().remove(this) at end)
     */
    protected void onHide() {
        /*
        * note we are over-loading #remove when launchButton in multiple
        * instance scenario
        */
        removeLaunchButton();
        doNotHideShutDownAndCancelOnGoingCalls();
        if (!hideToolBar) {
            winToolBar.cleanUpOnClose();
        }
    }

    @Override
    public String getBaseWindowTitleWithoutParams() {
        return window.getWindowTitle();
    }

    public EniqWindow getWidget() {
        return window;
    }

    @Override
    public void handleEvent(final WindowEvent event) {
        EventType eventType = event.getType();
        if (Minimize == eventType) {
            onMinimize();
        } else if (Restore == eventType) {
            onRestore();
        } else if (BeforeHide == eventType) {
            onBeforeHide();
        } else if (Hide == eventType) {
            onHide();
        } else if (Show == eventType) {
            onShow();
        }
    }

    public void hide() {
        window.hide();
    }

    public void toFront() {
        window.toFront();
    }

    public void toBack() {
        window.toBack();
    }

    public boolean isMinimised() {
        return window.isMinimised();
    }

    @Override
    public void bringToFront() {
        window.bringToFront();
    }

    public void focus() {
        window.focus();
    }

    public void setPositionAndSize(final int x, final int y, final int width, final int height) {
        window.setPositionAndSize(x, y, width, height);
    }

    public void setUrl(final String url) {
        window.setUrl(url);
    }

    public void setSize(final int width, final int height) {
        window.setSize(width, height);
    }

    private boolean applyPrefixesToFullTitle() {
        boolean isChanged;
        String title = this.titleBase;
        if (titlePrefix != null) {
            title = titlePrefix + DASH + title;
        }

        if (titlePrePrefix != null) {
            title = titlePrePrefix + DASH + title;
        }

        if (titlePostfix != null){
            title = title + ARROW + titlePostfix;
        }

        isChanged = fullTitle != null && !fullTitle.equals(title) || fullTitle == null && title != null;
        if (isChanged) {
            fullTitle = title;
            updateTitle(fullTitle);
        }
        return isChanged;
    }
    
    public EniqWindow getWindow() {
        return window;
    }

    WindowManager createWindowManagerForTab() {
        final EniqEventsUIGinjector injector = MainEntryPoint.getInjector();
        final TabManager tabManager = injector.getTabManager();
        return tabManager.getWindowManager(tabId);
    }

    EniqWindow createEniqWindow(final ContentPanel constrainArea, final String icon) {
        return new EniqWindow(constrainArea, this.fullTitle, icon);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowView#setWindowCategoryId()
     */
    @Override
    public void setWindowCategoryId(String categoryId) {
        this.windowCategoryId = categoryId;

    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowView#getWindowCategoryId()
     */
    @Override
    public String getWindowCategoryId() {
        return windowCategoryId;
    }
    public String getIcon() {
        return this.icon;
    }
    
    public String getTitleBase(){
        return titleBase.trim();
    }
    
    public String getTitlePrePrefix(){
        return titlePrePrefix.trim();
    }
    
    public String getTitlePrefix(){
        return titlePrefix.trim();
    }

    public void noticeMe(boolean isUserNoticeNeeded){
        window.noticeMe(isUserNoticeNeeded);
    }

    public void noticeMeEnd(){
        window.noticeMeEnd();
    }
}
