/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

/**
 * Class to support GridColumnView - 
 * Mainly to store mapping between licence & sublicences information from Metadata
 * 
 * @author edivkir
 * @since 2011
 *
 */
public class GridColumnsData {

    String[] licenceType;

    ColumnModel cm;

    GridInfoDataType gridInfo;

    Map<String, Set<String>> licenceGroupingMap = new HashMap<String, Set<String>>();

    public GridColumnsData(final GridInfoDataType columnInfo, final ColumnModel cm) {
        this.gridInfo = columnInfo;
        licenceType = columnInfo.licenceTypes.split(",");
        this.cm = cm;
    }

    /**
     * Method returns a map with "keys = licences"
     * values = set of subgroups for respective licence
     * 
     * @return licenceGroupingMap
     */
    public Map<String, Set<String>> getlicenceGroupingMap() {
        for (final String type : licenceType) {
            licenceGroupingMap.put(type, new HashSet<String>()); // NOPMD by edivkir on 29/07/11 15:20
        }
        final int cols = cm.getColumnCount();
        for (int i = 0; i < cols; i++) {
            final String columnLicence = gridInfo.columnInfo[i].columnLicence;
            if (columnLicence == null || columnLicence.isEmpty()) {
                continue;
            }
            final String[] multipleColLicences = columnLicence.split(",");

            for (final String mulLicence : multipleColLicences) {
                final String[] subGroupColumnLicences = mulLicence.split("-");

                final int subColLength = subGroupColumnLicences.length;

                if (subColLength > 0) {
                    final String licence = subGroupColumnLicences[0];
                    final String subLicence = (subColLength > 1) ? subGroupColumnLicences[1] : OTHER_COLUMN_GROUP;

                    if (licenceGroupingMap.containsKey(licence)) {
                        licenceGroupingMap.get(licence).add(subLicence);
                    }
                }
            }
        }
        return updateOtherColumns(licenceGroupingMap);
    }

    /*
     * We want to differentiate between licence which has only default columns and 
     * the one which has other sub-groups and default-columns both.
     * 
     * @param groupingMap
     */
    private Map<String, Set<String>> updateOtherColumns(final Map<String, Set<String>> groupingMap) {
        final Set<String> otherColumn = new HashSet<String>();
        otherColumn.add(OTHER_COLUMN_GROUP);

        for (final String type : licenceType) {
            final Set<String> licenceKey = groupingMap.get(type);

            if (licenceKey.equals(otherColumn)) {
                groupingMap.put(type, null);
            }
        }
        return groupingMap;
    }

    public static boolean isColumnEventTime(final String columnName) {
        return EVENT_TIME.equals(columnName);
    }

    /**
     * RESET the state of all "select All" checkboxes; when "select All" of "ACTIVE_COLUMN_GROUP" is unchecked. 
     * @param state
     * @param selectAllStateMap
     */
    public void reset(final Map<String, Object> state, final Map<String, Boolean> selectAllStateMap) {
        for (final Entry<String, Object> entry : state.entrySet()) {
            if (selectAllStateMap.containsKey((entry.getKey()))) {
                state.put(entry.getKey(), false);
            }
        }
    }
}
