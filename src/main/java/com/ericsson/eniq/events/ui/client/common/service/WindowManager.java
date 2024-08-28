/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.common.service;

import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import java.util.Map;
import java.util.Set;

/**
 * @author edmibuz
 */
public interface WindowManager {

    boolean openWindow(final BaseWindow window, final String title, final String icon);

    void closeWindow(final BaseWindow window, final String title, final String icon);

    boolean isThereWindows();

    Set<BaseWindow> getAllWindows();

    BaseWindow getWindow(final String id);

    Set<String> getAllOwnedBaseWindowsIds();

    Map<String, BaseWindow> getOwnedBaseWindowsMap();

    /**
     * When a launched window title is updated by drilldown or search field update or navigation,
     * the same should reflect in its menuTaskBar launch button.
     * 
     * @param window
     * @param title
     * @return
     */
    boolean updateLaunchButtonTitle(BaseWindow window, String title);

    /**
     * @param window
     * @param title
     * @param icon
     */
    void updateWindowTitle(BaseWindow window, String title, String icon);

}
