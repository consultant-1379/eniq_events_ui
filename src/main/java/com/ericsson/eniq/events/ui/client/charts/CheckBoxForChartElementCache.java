/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.charts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;

/**
 * Static Cache (toggle chart element) checkbox settings
 * to be used when user:
 * 
 * This is static, i.e. independent (of window closing) needed when user
 * 
 * - selects or unselects (toggle chart element) checkbox 
 * - toggles graph to grid and back
 * - or refreshes, changes time, changes legend on and off, etc.
 * 
 * We want to preserve setting for chart elements shown and hidden (toggle chart element 
 * checkboxes selected)
 * 
 * @author eeicmsy
 * @since March 2011
 *
 */
public abstract class CheckBoxForChartElementCache {

    private final static Set<CheckBoxForChartElement> EMPTY_RESULT = new HashSet<CheckBoxForChartElement>();

    private static Map<String, Set<CheckBoxForChartElement>> winIdCheckBoxesTickedMap = new HashMap<String, Set<CheckBoxForChartElement>>();

    /**
     * Checkbox used to show and hide elements (lines) in graph.
     * Get collection of checkboxes selected for this window.
     * 
     * @param multiWinId   unique id for window - will multi-instance support
     * @return        - Empty set or collection of selected checkboxes (ticked - implying show chart element)
     * 
     */
    public static Set<CheckBoxForChartElement> getCheckBoxesTicked(final MultipleInstanceWinId multiWinId) {
        final String key = multiWinId.generateCompositeId();

        final Set<CheckBoxForChartElement> returnVal = winIdCheckBoxesTickedMap.get(key);
        return (returnVal == null) ? EMPTY_RESULT : returnVal;
    }

    /**
     * Checkbox used to show and hide elements (lines) in graph.
     * 
     * Cache every checkbox selection and disselection for current window.
     * Map created will be a direct map of checkbox items ticked for the window
     * 
     * @param multiWinId   unique id for window - will multi-instance support
     * @param checkBoxSelected - checkbox checked or unchecked
     */
    public static void cacheCheckBoxTicked(final MultipleInstanceWinId multiWinId,
            final CheckBoxForChartElement checkBoxSelected) {

        // assume all starts empty
        final String key = multiWinId.generateCompositeId();

        Set<CheckBoxForChartElement> totalItemsTicked = winIdCheckBoxesTickedMap.get(key);
        if (totalItemsTicked == null) { //semi redundant considering #setupWindowToContainToggleCheckBoxMenu
            totalItemsTicked = new HashSet<CheckBoxForChartElement>();
            totalItemsTicked.add(checkBoxSelected);

            winIdCheckBoxesTickedMap.put(key, totalItemsTicked);
            return;

        }
        if (totalItemsTicked.contains(checkBoxSelected)) {
            totalItemsTicked.remove(checkBoxSelected);

        } else {
            totalItemsTicked.add(checkBoxSelected);

        }
        winIdCheckBoxesTickedMap.put(key, totalItemsTicked);
    }

    /**
     * Checkbox used to show and hide elements (lines) in graph.
     * 
     * Cache every checkbox selection and disselection for current window.
     * Map created will be a direct map of checkbox items ticked for the window
     * 
     * @param multiWinId         unique id for window - will multi-instance support 
     * @param checkBoxSelected   (CheckBoxForChartElement - which is a menu item (without any state set on it)
     * @param isChecked           true if checkbox is checked, else false
     */
    public static void cacheCheckBoxTicked(final MultipleInstanceWinId multiWinId,
            final CheckBoxForChartElement checkBoxSelected, final boolean isChecked) {

        // assume all starts empty
        final String key = multiWinId.generateCompositeId();

        Set<CheckBoxForChartElement> totalItemsTicked = winIdCheckBoxesTickedMap.get(key);
        if (isChecked && totalItemsTicked == null) {
            totalItemsTicked = new HashSet<CheckBoxForChartElement>();
            totalItemsTicked.add(checkBoxSelected);

            winIdCheckBoxesTickedMap.put(key, totalItemsTicked);
            return;

        }
        if (isChecked) {
            totalItemsTicked.add(checkBoxSelected);
        } else {
            totalItemsTicked.remove(checkBoxSelected);

        }
        winIdCheckBoxesTickedMap.put(key, totalItemsTicked);
    }

    /**
     * Utlilty for "Select all" functionaliy 
     * (false to remove all cached checkbox items associated with win id and vice versa)
     * @param multiWinId   window id
     * @param isChecked    true for "select all", false for remove all)
     * @param totalItemsTicked  - can be null for remove (isChecked false)
     */
    public static void checkAllCheckBoxTicked(final MultipleInstanceWinId multiWinId, final boolean isChecked,
            final Set<CheckBoxForChartElement> totalItemsTicked) {
        final String key = multiWinId.generateCompositeId();

        if (isChecked) {
            winIdCheckBoxesTickedMap.put(key, totalItemsTicked);
        } else {
            winIdCheckBoxesTickedMap.remove(key);
        }
    }

    /**
     * Initiate window as one that contains a drop down list of checkboxes used to toggle 
     * chart elements 
     * @param multiWinId   unique id for window - will multi-instance support
     */
    public static void setupWindowToContainToggleCheckBoxMenu(final MultipleInstanceWinId multiWinId) {
        final String key = multiWinId.generateCompositeId();

        if (!winIdCheckBoxesTickedMap.containsKey(key)) { // don't wipe if exists
            winIdCheckBoxesTickedMap.put(key, new HashSet<CheckBoxForChartElement>());
        }
    }

    /**
     * Utility to tell if this window (chart) contains a checkbox menu item
     * for toggling on and off elements
     * 
     * @param multiWinId   unique id for window - will multi-instance support
     * @return       true if we have ever created a toggle checkbox menu item for this window
     */
    public static boolean containsToggleCheckBoxMenu(final MultipleInstanceWinId multiWinId) {
        final String key = multiWinId.generateCompositeId();
        return winIdCheckBoxesTickedMap.containsKey(key);
    }

}
