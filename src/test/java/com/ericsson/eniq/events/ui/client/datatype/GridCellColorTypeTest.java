package com.ericsson.eniq.events.ui.client.datatype;

import static junit.framework.Assert.*;

import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;

public class GridCellColorTypeTest extends TestEniqEventsUI {

    /**************** 
     * Tests only work when hardcode static Constants in Constants.java
     * (Don't want to delay renderer over test anyway)
     * 
     * public final static float KPI_LESS_THAN_CUT_OFF_AMBER = 97 //getNativeKPIThresholdAmber();
     * public final static float KPI_LESS_THAN_CUT_OFF_RED =   95 // getNativeKPIThresholdRed();
    */

    /// @Test  Test only works when hardcode Constants
    public void kpiHyperLinkColorOk() throws Exception {

        final String expectedRedHyperLink = "gridCellLinkCodeRed";
        final String expectedAmberHyperLink = "gridCellLinkCodeAmber";
        final String expectedNormalHyperLink = "gridCellLink";

        assertEquals("Red HyperLink 95", expectedRedHyperLink, (GridCellColorType.KPI_SUCCESS_RATIO_DRILLDOWN
                .getCellDisplayCSS("95")));
        assertEquals("Red HyperLink 30", expectedRedHyperLink, (GridCellColorType.KPI_SUCCESS_RATIO_DRILLDOWN
                .getCellDisplayCSS("30")));

        assertEquals("Amber HyperLink 97", expectedAmberHyperLink, (GridCellColorType.KPI_SUCCESS_RATIO_DRILLDOWN
                .getCellDisplayCSS("97")));
        assertEquals("Amber HyperLink 96.3", expectedAmberHyperLink, (GridCellColorType.KPI_SUCCESS_RATIO_DRILLDOWN
                .getCellDisplayCSS("96.3")));

        assertEquals("normal HyperLink 99", expectedNormalHyperLink, (GridCellColorType.KPI_SUCCESS_RATIO_DRILLDOWN
                .getCellDisplayCSS("99")));
        assertEquals("normal HyperLink 100.00", expectedNormalHyperLink, (GridCellColorType.KPI_SUCCESS_RATIO_DRILLDOWN
                .getCellDisplayCSS("100.00")));

    }

    /// @Test  Test only works when hardcode Constants
    public void kpiPlainColorOk() throws Exception {

        final String expectedRedHyperLink = "gridCellPlainCodeRed";
        final String expectedAmberHyperLink = "gridCellPlainCodeAmber";
        final String expectedNormalHyperLink = "gridCellPlain";

        assertEquals("Red Plain 95", expectedRedHyperLink, (GridCellColorType.KPI_SUCCESS_RATIO_PLAIN_CELL
                .getCellDisplayCSS("95")));
        assertEquals("Red Plain 30", expectedRedHyperLink, (GridCellColorType.KPI_SUCCESS_RATIO_PLAIN_CELL
                .getCellDisplayCSS("30")));

        assertEquals("Amber Plain 97", expectedAmberHyperLink, (GridCellColorType.KPI_SUCCESS_RATIO_PLAIN_CELL
                .getCellDisplayCSS("97")));
        assertEquals("Amber Plain 96.3", expectedAmberHyperLink, (GridCellColorType.KPI_SUCCESS_RATIO_PLAIN_CELL
                .getCellDisplayCSS("96.3")));

        assertEquals("normal Plain 99", expectedNormalHyperLink, (GridCellColorType.KPI_SUCCESS_RATIO_PLAIN_CELL
                .getCellDisplayCSS("99")));
        assertEquals("normal Plain 100.00", expectedNormalHyperLink, (GridCellColorType.KPI_SUCCESS_RATIO_PLAIN_CELL
                .getCellDisplayCSS("100.00")));

    }

    @Test
    public void emptyStringHAndledOk() throws Exception {
        final String expectedNormalHyperLink = "gridCellPlain";

        final String EMPTY_STRING = "";

        assertEquals("normal Plain for EMPTY_STRING", expectedNormalHyperLink,
                (GridCellColorType.KPI_SUCCESS_RATIO_PLAIN_CELL.getCellDisplayCSS(EMPTY_STRING)));
    }

}
