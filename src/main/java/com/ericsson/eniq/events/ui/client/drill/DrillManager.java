/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.drill;


import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog.DialogType;
import com.ericsson.eniq.events.widgets.client.drill.DrillCategoryType;
import com.ericsson.eniq.events.widgets.client.drill.DrillDialog;
import com.ericsson.eniq.events.widgets.client.drill.IDrillCallback;
import com.ericsson.eniq.events.widgets.client.drill.Point;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.dom.client.Element;

import java.util.*;

import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class DrillManager {
    private final Map<String, List<DrillCategoryType>> drillCategoryMap;

    /**
     * @param drillCategoryMap  drill down category map
     */
    public DrillManager(Map<String, List<DrillCategoryType>> drillCategoryMap) {
        super();
        this.drillCategoryMap = drillCategoryMap;
    }

    /**
     * @param drillDownKey - drill down key
     * @param seriesMap - the row
     * @return return value
     */
    private List<DrillCategoryType> getDrillCategories(Map<String, Object> seriesMap, String drillDownKey) {
        if (!drillCategoryMap.containsKey(drillDownKey)) {
            return getDrillDownTargetId(drillDownKey);
        }
        return getApplicableDrillCategories(drillDownKey, seriesMap);
    }

    /**
     * Decide what to do when drilling from a certain screen where its key was found in drillManager.json.
     * If we have
     * 1. One category in the drillManager.json then just drill to that key
     * 2. If we have multiple categories and these contain no names then we must decide the next key to drill to based on the current screen
     * 3. If we have multiple categories with multiple names then give the user the popups so they can decide which key they would like to drill to next
     *
     * Note:  Replacing what was previously done in commons when using a certain string pattern to decide the next key to be chosen
     *
     * @param seriesMap map containing the parameters from the current view
     * @param drillDownKey the key for the next drill down
     * @param point x y co-ordinates where the user clicked
     * @param callback method to callback
     */
    public void getDrillDownInfo(Map<String, Object> seriesMap, String drillDownKey, Point point,
            final IDrillCallback callback) {
        List<DrillCategoryType> categories = getDrillCategories(seriesMap, drillDownKey);
        /**  Error - no meta data for this drill key **/
        if (categories.isEmpty()) {
            MessageDialog.get().show("Drill Error", "Drill Down Target Not Found : " + drillDownKey, DialogType.ERROR);
            return;
        }

        /** 1. Just the one drill category for this key, so no need for selection panel, just go to normal drill through **/
        if (categories.size() == 1) {
            callback.onDrillDownSelected(categories.get(0).getId());
            return;
        }

        /** 2. if we have more than 2 categories but have no names then we will decide the next screen based on the handover type from the current screen **/
        boolean hasCategoryNames = false;
        for (DrillCategoryType category : categories)
        {
            if(category.getName()!= null && !category.getName().equals(""))
            {
                hasCategoryNames = true;
            }
        }

        if (!hasCategoryNames)
        {
            //Find if the series value defined by the series id (i.e. it's position) is contained in the include list, if so we will drill with the id of this category match
            /** Have a look at drillManager.json on the services side as that's where we are drilling here.  **/
            for (DrillCategoryType category : categories)
            {
                for (DrillCategoryType.SeriesMatcherType seriesValues : category.getCriteria().getSeriesMatchers())
                {
                    if(seriesValues.getValues().contains(seriesMap.get(seriesValues.getSeriesId())))
                    {
                        callback.onDrillDownSelected(category.getId());
                        return;
                    }

                }
            }

        }

        /** 3. More than one category, need selection panel for user to select drill target **/
        final DrillDialog drillByDialog = DrillDialog.get();
        for (DrillCategoryType category : categories) {
            drillByDialog.addDrillOption(category, callback);
        }
        drillByDialog.setGlassEnabled(false);
        drillByDialog.show(point);
    }

    /**
     * @param drillDownKey drill down key
     * @return
     */
    private List<DrillCategoryType> getDrillDownTargetId(final String drillDownKey) {
        if (drillDownKey == null || drillDownKey.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<DrillCategoryType>() {
            {
                add(new DrillCategoryType(drillDownKey));
            }
        };
    }

    private List<DrillCategoryType> getApplicableDrillCategories(String key, Map<String, Object> seriesMap) {
        List<DrillCategoryType> allCategories = drillCategoryMap.get(key);
        List<DrillCategoryType> applicableCategories = new ArrayList<DrillCategoryType>();
        for (DrillCategoryType category : allCategories) {
            DrillCategoryType.DrillCriteria criteria = null;
            if ((criteria = category.getCriteria()) == null) {
                applicableCategories.add(category);
            } else {
                List<DrillCategoryType.SeriesMatcherType> seriesMatchers = criteria.getSeriesMatchers();
                boolean matched = true;
                /** Check if the values for the particular rows in the series match the criteria **/
                for (DrillCategoryType.SeriesMatcherType seriesMatcher : seriesMatchers) {
                    if (!seriesMatcher.getValues().contains(seriesMap.get(seriesMatcher.getSeriesId()))) {
                        matched = false;
                    }
                }
                if (matched) {
                    /** Criteria matched, are we excluding or including matchers? **/
                    if (criteria.getCriteriaType().equals(DrillCategoryType.DrillCriteriaType.INCLUDE)) {
                        applicableCategories.add(category);
                    }
                } else {
                    /** Criteria not matched, if we are excluding, then we can include this category **/
                    if (criteria.getCriteriaType().equals(DrillCategoryType.DrillCriteriaType.EXCLUDE)) {
                        applicableCategories.add(category);
                    }
                }
            }
        }
        return applicableCategories;
    }

    /**
     * get row for a chart
     * @param seriesMap
     * @param rowIndex 
     * @return
     */
    public static Map<String, Object> getRowMap(Map<String, Object[]> seriesMap, int rowIndex) {
        Map<String, Object> row = new HashMap<String, Object>();
        for (String seriesId : seriesMap.keySet()) {
            row.put(seriesId, seriesMap.get(seriesId)[rowIndex]);
        }
        return row;
    }

    /**
     * Get row for a grid.
     * @param grid
     * @param rowIndex 
     * @return
     */
    public static Map<String, Object> getRowMap(Grid<ModelData> grid, int rowIndex) {
        Map<String, Object> row = new HashMap<String, Object>();
        for (int i = 0; i < grid.getColumnModel().getColumnCount(); i++) {
            String id = grid.getColumnModel().getColumnId(i);
            final Element ele = grid.getView().getCell(rowIndex, i);
            row.put(id, (ele == null ? EMPTY_STRING : ele.getInnerText()));
        }
        return row;
    }
}
