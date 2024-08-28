/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;

/**
 * @author esuslyn
 *
 */
public class GridGroupingCellRenderer implements GridGroupRenderer {

    private final JSONGrid grid;

    /**
     * @param html
     */
    public GridGroupingCellRenderer(final JSONGrid grid) {
        this.grid = grid;
    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer#render(com.extjs.gxt.ui.client.widget.grid.GroupColumnData)
     */
    @Override
    public String render(final GroupColumnData data) {
        final String groupedColumnTitle = getColumnModelFromGrid().getColumnById(data.field).getHeader();
        final ColumnModel columnModelFromGrid = getColumnModelFromGrid();
        String occurrencesId = null;
        String causeCodeIdColumnId = null;
        long occurrences = 0;
        String causeCodeId = EMPTY_STRING;

        for (int k = 0; k < columnModelFromGrid.getColumnCount(); k++) {

            final String header = columnModelFromGrid.getColumnHeader(k);

            if (header.equalsIgnoreCase(OCCURRENCES)) {
                occurrencesId = columnModelFromGrid.getColumnId(k);
            }
            // changing to endsWith for MSS ("Internal Cause Code ID")
            if (header.endsWith(CAUSE_CODE_ID) || header.endsWith(CAUSE_CODE_ID_LOWER)) {
                causeCodeIdColumnId = columnModelFromGrid.getColumnId(k);
            }
        }
        /*check to ensure occurrencesId is not null as will cause grid to fail otherwise */
        if (occurrencesId != null) {
            for (int i = 0; i < data.models.size(); i++) {
                occurrences = occurrences + ((Long) data.models.get(i).get(occurrencesId));
            }
        }

        /*check to ensure causeCodeIdColumnId is not null as will cause grid to fail otherwise */
        if (causeCodeIdColumnId != null) {
            causeCodeId = (data.models.size() > 0 ? "(ID: " + data.models.get(0).get(causeCodeIdColumnId) + ")" : "");
        }
        return groupedColumnTitle + ": " + data.group + " " + causeCodeId + "(" + occurrences + " " + OCCURRENCES + ")";

    }

    /**
     * extracted out to help get under unit test
     * @return
     */
    ColumnModel getColumnModelFromGrid() {
        return grid.getColumnModel();
    }
}
