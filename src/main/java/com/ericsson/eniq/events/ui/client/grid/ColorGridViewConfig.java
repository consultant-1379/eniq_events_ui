/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import static com.ericsson.eniq.events.ui.client.common.CSSConstants.*;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;

import java.util.Collection;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.GridViewConfig;

/**
 * Apply a color to entire grid row depending on 
 * finding content in row (e.g. Event Result ABORT)
 * 
 * @author eeicmsy
 * @since June 2011
 */
public class ColorGridViewConfig extends GridViewConfig {

    @Override
    public String getRowStyle(final ModelData model, final int rowIndex, final ListStore<ModelData> ds) {
        if (model != null) {
            final Collection<Object> rowVals = model.getProperties().values();

            for (final Object rowVal : rowVals) {
                if (colorCodeRowMap.containsKey(rowVal)) {
                    return colorCodeRowMap.get(rowVal);
                }
            }
        }
        return EMPTY_STRING;
    }

    /**
     * External utility to check if even want to apply this
     * ColorGridViewConfig to our grid view.
     * 
     * @param gridMetaData metadata for grid (columns)
     * @return             true if need to apply a ColorGridViewConfig to this grid
     */
    public static boolean shouldTurnOnRowColors(final GridInfoDataType gridMetaData) {
        return headersContains(EVENT_RESULT_HEADER, gridMetaData);
    }

    private static boolean headersContains(final String headerName, final GridInfoDataType gridMetaData) {
        final ColumnInfoDataType[] columnInfo = gridMetaData.columnInfo;
        for (final ColumnInfoDataType column : columnInfo) {
            if (column.columnHeader.equals(headerName)) {
                return true;
            }
        }
        return false;
    }
}
