/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid.listeners;

import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.datatype.GridCellColorType;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.drill.DrillManager;
import com.ericsson.eniq.events.ui.client.events.HyperLinkCellClickEvent;
import com.ericsson.eniq.events.ui.client.events.HyperLinkCellWinLauncherEvent;
import com.ericsson.eniq.events.widgets.client.drill.IDrillCallback;
import com.ericsson.eniq.events.widgets.client.drill.Point;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.google.web.bindery.event.shared.EventBus;

import java.util.Map;

import static com.ericsson.eniq.events.ui.client.common.CSSConstants.GRID_CELL_LAUNCHER_LINK_CSS;

/**
 * listens for the end user clicking on a 
 * hyper link within a grid cell
 * 
 * @author eendmcm
 * @since Mar 2010
 */
public class HyperLinkCellClickListener implements Listener<BaseEvent> {

    private final EventBus eventBus;

    /*
    * id of window been updated containing
    * multi-instance window information 
    */
    private final MultipleInstanceWinId multiWinID;

    private final DrillManager drillManager;

    /**
     * Construct listener for click event on a cell hyperlink
     * Which may result in a drilldown or a new window luanch event being fired.
     * 
     * @param multiWinId  id of window been updated  - containing multi-instance window information
     * @param bus   - the EventBus
     */
    public HyperLinkCellClickListener(final MultipleInstanceWinId multiWinId, final EventBus bus,
            final IMetaReader metaReader) {
        this.multiWinID = multiWinId;
        this.eventBus = bus;
        drillManager = new DrillManager(metaReader.getDrillManagerData());
    }

    /**
     * determines if the click event on 
     * the grid cell is a hyper link and 
     * handles appropriately 
     */
    @SuppressWarnings("unchecked")
    @Override
    public void handleEvent(final BaseEvent be) {
        final GridEvent<ModelData> ge = (GridEvent<ModelData>) be;
        Point point = new Point(ge.getClientX(), ge.getClientY());
        final int rowIndex = ge.getRowIndex();
        /* Is this a drill down hyper link */
        El domElement = null;
        final boolean drillDown;
        if ((domElement = getDrillDownElement(ge)) != null) {
            drillDown = true;
        } else if ((domElement = ge.getTarget("." + GRID_CELL_LAUNCHER_LINK_CSS, 1)) != null) {
            drillDown = false;
        } else {
            return;
        }
        final String drillDownKey = domElement.getId();
        final String sValue = domElement.getInnerHtml();

        Map<String, Object> row = DrillManager.getRowMap(ge.getGrid(), rowIndex);
        drillManager.getDrillDownInfo(row, drillDownKey, point, new IDrillCallback() {

            @Override
            public void onDrillDownSelected(String drillDownTargetId) {
                if (drillDown) {
                    eventBus.fireEvent(new HyperLinkCellClickEvent(multiWinID, sValue, drillDownTargetId, rowIndex));
                } else {
                    eventBus.fireEvent(new HyperLinkCellWinLauncherEvent(multiWinID, sValue, drillDownTargetId,
                            rowIndex));
                }
            }
        });
    }

    /* multiple colored drilldowns */
    private El getDrillDownElement(final GridEvent<ModelData> be) {
        El domElement = null;
        for (final String drillDownCSS : GridCellColorType.drilldownCSSStrings) {
            domElement = be.getTarget("." + drillDownCSS, 1);
            if (domElement != null) {
                break;
            }
        }
        return domElement;
    }

}
