/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.user.cellview.client.CellList.Resources;
import com.google.gwt.user.cellview.client.CellList.Style;

/**
 * @author ekurshi
 * @since 2012
 *
 */
public interface GroupMgmtCellListResources extends ClientBundle {

    CellResource cellStyle();

    interface CellResource extends Resources {
        @Override
        @Source("css/CellListStyle.css")
        Style cellListStyle();
    }
}
