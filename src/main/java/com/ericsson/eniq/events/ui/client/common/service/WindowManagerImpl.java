/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.common.service;

import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.events.window.*;
import com.ericsson.eniq.events.ui.client.gin.EniqEventsUIGinjector;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class should not be instantiated from outside of the package.
 * @author edmibuz
 */
public class WindowManagerImpl implements WindowManager {

    private final EniqEventsUIGinjector injector = MainEntryPoint.getInjector();

    private final EventBus eventBus;

    private final String tabId;

    /*
     * Taskbar (on a tab) keeping track of windows in center panel of tab
     * Maps winIds to Basewindows
     */
    private final Map<String, BaseWindow> ownedBaseWindows = new HashMap<String, BaseWindow>();

    /*
     * Sorting list of baseWindow ids (multiple node versions),same ids as used in owned window naps
     * 
     * key : e.g. NETWORK_EVENT_ANALYSISMULTI_IDENTIFERblackberry.net  
     * Values, e.g. NETWORK_EVENT_ANALYSISMULTI_IDENTIFER121212123
     */
    private final Map<String, Set<String>> previousMultiBaseWindowIds = new HashMap<String, Set<String>>();

    @Inject
    public WindowManagerImpl(final EventBus eventBus, final String tabId) {
        this.eventBus = eventBus;
        this.tabId = tabId;
    }

    int getMaxInstanceCount() {
        return injector.getMetaReader().getMaxInstanceWindowsPerType();
    }

    @Override
    public boolean openWindow(final BaseWindow window, final String title, final String icon) {
        final WindowModel model = new WindowModel(tabId);
        model.setTitle(title);
        model.setIcon(icon);

        final String id = window.getBaseWindowID();
        ownedBaseWindows.put(id, window);

        eventBus.fireEvent(new WindowOpenedEvent(model, window));

        return true;
    }    
    
    @Override
    public boolean updateLaunchButtonTitle (final BaseWindow window, final String title) {
        eventBus.fireEvent(new WindowLaunchButtonTitleUpdateEvent(tabId, window, title));

        return true;
    }          


    @Override
    public void updateWindowTitle(final BaseWindow window, final String title, final String icon) {
        final WindowModel model = new WindowModel(tabId);
        model.setTitle(title);
        model.setIcon(icon);
        eventBus.fireEvent(new WindowTitleUpdateEvent(model, window));
    }

    @Override
    public void closeWindow(final BaseWindow window, final String title, final String icon) {
        final WindowModel model = new WindowModel(tabId);
        model.setTitle(title);
        model.setIcon(icon);

        final String id = window.getBaseWindowID();
        ownedBaseWindows.remove(id);

        eventBus.fireEvent(new WindowClosedEvent(model, window));
    }

    @Override
    public boolean isThereWindows() {
        return !ownedBaseWindows.isEmpty();
    }

    /**
     * Gets the launched window with the given id
     * 
     * ADDITIONAL HERE is to also check previous versions of the window, 
     * i.e if user drilled on window and changed search field in process
     * it could very will be the window we want
     * 
     * @param id instance window id
     * @return The BaseWindow instance associated with the id or null if no window of that id exists
     */
    @Override
    public BaseWindow getWindow(final String id) {

        BaseWindow baseWin = ownedBaseWindows.get(id);

        if (baseWin == null && !previousMultiBaseWindowIds.isEmpty()) {
            if (previousMultiBaseWindowIds.containsKey(id)) {

                final Set<String> existingChildren = previousMultiBaseWindowIds.get(id);
                for (final String baseID : existingChildren) {

                    baseWin = ownedBaseWindows.get(baseID);
                    if (baseWin != null) {
                        break;
                    }
                }

            }
        }
        return baseWin;
    }

    @Override
    public Set<String> getAllOwnedBaseWindowsIds() {
        return ownedBaseWindows.keySet();
    }

    @Override
    public Set<BaseWindow> getAllWindows() {
        return new HashSet<BaseWindow>(ownedBaseWindows.values());
    }

    /**
     * supporting unit test
     * @return
     */
    @Override
    public Map<String, BaseWindow> getOwnedBaseWindowsMap() {
        return ownedBaseWindows;
    }

}
