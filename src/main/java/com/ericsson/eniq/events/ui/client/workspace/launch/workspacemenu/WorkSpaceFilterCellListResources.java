/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellList.Resources;
import com.google.gwt.user.cellview.client.CellList.Style;

/**
 * @author egallou
 * @since 2012
 *
 */
public interface WorkSpaceFilterCellListResources extends CellList.Resources {

    @Source("images/background.png")
    @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.Horizontal, flipRtl = true)
    ImageResource cellListSelectedBackground();

    interface CellResource extends Resources {
        @Override
        @Source("css/WorkSpaceCellListStyle.css")
        public Style cellListStyle();
    }
}
