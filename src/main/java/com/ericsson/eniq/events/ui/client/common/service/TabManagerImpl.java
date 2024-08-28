/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.common.service;

import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TabManagerImpl implements TabManager {

    private final Map<String, WindowManager> windowManagers = new HashMap<String, WindowManager>();
    private final EventBus eventBus;

    @Inject
	public TabManagerImpl(final EventBus eventBus) {
        this.eventBus = eventBus;
    }
	
	@Override
	public WindowManager getWindowManager(final String tabId) {
		WindowManager windowManager = windowManagers.get(tabId);
		if (windowManager == null) {
			windowManager = new WindowManagerImpl(eventBus, tabId);
			windowManagers.put(tabId, windowManager);
		}
		
		return windowManager;
	}
    
	@Override
	public Collection<WindowManager> getWindowManagers() {
		return new HashSet<WindowManager>(windowManagers.values());
	}
	
}
