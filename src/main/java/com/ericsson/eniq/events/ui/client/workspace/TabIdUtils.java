/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace;

/**
 * Note: utils, not for instantiation
 *
 * @author ealeerm
 * @since 06/2012
 */
abstract class TabIdUtils {

    private TabIdUtils() {
        // not for instantiation
    }

    static String generateTabIdPrefix(String tabName) {
        if (tabName == null) {
            return "";
        }
        String s = tabName.trim();
        s = s.replaceAll("[\\s]+", "_");
        s = s.replaceAll("[\\W]+", "");

        while (!s.isEmpty() && s.charAt(0) == '_') {
            s = s.substring(1);
        }

        while (!s.isEmpty() && s.charAt(s.length() - 1) == '_') {
            s = s.substring(0, s.length() - 1);
        }

        while (s.contains("__")) {
            s = s.replace("__", "_");
        }

        return s.toUpperCase();
    }

    static String generateTabId(String tabIdPrefix, int index) {
        String tabId;
        if (tabIdPrefix == null) {
            tabId = "";
        } else {
            tabId = tabIdPrefix.trim();
        }

        if (!tabId.isEmpty()) {
            tabId += "_";
        }

        if (index != 0) {
            tabId += index + "_";
        }

        tabId += "TAB";
        return tabId;
    }
}
