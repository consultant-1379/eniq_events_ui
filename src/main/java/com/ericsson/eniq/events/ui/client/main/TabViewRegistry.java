/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.main;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton for holding Tab View references.
 * 
 * @author ecarsea
 * @since 2011
 *
 */
public class TabViewRegistry {
    private static final TabViewRegistry _instance = new TabViewRegistry();

    private final Map<String, IGenericTabView> tabItemMap = new HashMap<String, IGenericTabView>();

    private TabViewRegistry() {
    }

    public static TabViewRegistry get() {
        return _instance;
    }

    public boolean containsTabView(final String tabId) {
        return tabItemMap.containsKey(tabId);
    }

    public IGenericTabView addTabView(final String tabId, final IGenericTabView tabItem) {
        return tabItemMap.put(tabId, tabItem);
    }

    public IGenericTabView getTabView(final String tabId) {
        return tabItemMap.get(tabId);
    }

    public IGenericTabView removeTabView(final String tabId) {
        return tabItemMap.remove(tabId);
    }

    public boolean isEmpty() {
        return tabItemMap.size() == 0;
    }

    public Collection<IGenericTabView> getAllTabViews() {
        return tabItemMap.values();
    }
}
