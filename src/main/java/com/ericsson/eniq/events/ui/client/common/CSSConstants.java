/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

import java.util.HashMap;
import java.util.Map;

/**
 * holds (important) CSS Class names used 
 * throughout the application
 *  
 * @author eendmcm
 * @since Mar 2010
 */
public abstract class CSSConstants {

    /**
     * CSS rendering of whole row 
     * @key Row value to trigger changing row color
     * @value CSS string to apply to the row
     */
    public static Map<String, String> colorCodeRowMap = new HashMap<String, String>();

    static {

        colorCodeRowMap.put("SUCCESS", "row-SUCCESS");
        colorCodeRowMap.put("REJECT", "row-REJECT");
        colorCodeRowMap.put("ABORT", "row-ABORT");
        colorCodeRowMap.put("IGNORE", "row-IGNORE");
        colorCodeRowMap.put("ERROR", "row-ERROR");
        colorCodeRowMap.put("BLOCKED", "row-BLOCKED");
        colorCodeRowMap.put("DROPPED", "row-DROPPED ");

    }

    /*css classes used for the ranking grids*/
    public static final String RANKING_ONE_CSS = "rankingOne";
    public static final String RANKING_TWO_CSS = "rankingTwo";
    public static final String RANKING_THREE_CSS = "rankingThree";
    public static final String RANKING_FOUR_CSS = "rankingFour";
    public static final String RANKING_FIVE_CSS = "rankingFive";

    public static final String RANKING_ROW_CSS = "rankingRow";

    /*css classes used for the time gap grids*/
    public static final String TIME_GAP_ONE_CSS = "timeGapOne";
    public static final String TIME_GAP_TWO_CSS = "timeGapTwo";
    public static final String TIME_GAP_THREE_CSS = "timeGapThree";

    public static final String TIME_GAP_GRID_CSS = "timeGapGrid";
    public static final String FIX_HEADER = "fixHeaderWidth";

    /*css class used on cell hyperlinks */
    public static final String GRID_CELL_LINK_CSS = "gridCellLink";

    public static final String GRID_CELL_LINK_CODE_RED_CSS = "gridCellLinkCodeRed";

    public static final String GRID_CELL_LINK_CODE_AMBER_CSS = "gridCellLinkCodeAmber";

    public static final String GRID_CELL_PLAIN_CODE_RED_CSS = "gridCellPlainCodeRed";

    public static final String GRID_CELL_PLAIN_CODE_AMBER_CSS = "gridCellPlainCodeAmber";

    // if just did not exist in CSS that would work too
    public static final String GRID_CELL_PLAIN_CSS = "gridCellPlain";

    /* sublibimal message style */
    public final static String INFO_DISPLAY_STYLE_CSS = "background-color: #E5E5E5; text-align: center; border-style:solid; border-width:1px; padding: 10px; font-size: 11px; font-weight: bold;";

    /*
     *css is used to detemine if this hyperlink open another window as opposed to drills
     *down within same window - not ideal i know - so extra care if changing !! 
     */
    public static final String GRID_CELL_LAUNCHER_LINK_CSS = "gridCellLauncherLink";

    /*css used for displaying expand/collapse images in a grouping grid*/
    public static final String GRID_GROUPING_CELL_CSS = "x-grid-group-div";

    public static final String GRID_TOOLBAR_CSS = "gridToolbar";

    /* css after call #setPlain on tab panel */

    public static final String NO_LICENCE_STYLE = "locked_no_license";

    public static final String START_TASK_CSS = "startTask";

    public static final String LOADING_STYLE_NAME = "loading-indicator"; // GXT css

    public static final String CHART_TITLE_CSS = "chartTitle";

    public static final String BREADCRUMB_NOT_SELECTED = "";

    public static final String BREADCRUMB_SELECTED = "breadCrumb-selected";

    public static final String KPI_BUTTON_STYLE = "kpiButton";

    public static final String INFO_BUTTON_STYLE = "toolbar_infobtn";

    public static final String CASCADE_BUTTON_STYLE = "smallCascadeIcon";

    public static final String TILE_BUTTON_STYLE = "smallTileIcon";

    public static final String PAIRED_SEARCH_COMBO_GAP = "pairsearchTypeCombo";

    public static final String CHART_AXIS_LEGEND_LABEL = "chartAxisLegendLabel";

    public static final String EXC_TAC_BUTTON_STYLE = "excTacBtnIcon";

    //XXXpublic static final String CHART_AXIS_LEGEND_LABEL = "font-size:x-large;font-family:tahoma,arial,helvetica,sans-serif; font-weight:bold; color: #0000FF;";

    /**
     * css used by GXT on the "Displaying 1-10 of 10" in grids, reused by the label for 
     * displaying the maximum number of rows
     */
    public static final String GRID_PAGING_TOOLBAR_CSS = ".x-toolbar div";

    /**
     * CSS for the last refresh label in grids
     */
    public static final String LAST_REFRESH_LABEL_CSS = "lastRefreshLabel";

}
