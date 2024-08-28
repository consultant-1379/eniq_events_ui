/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.eniq.events.ui.client.grid;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.ui.client.datatype.CellType;
import com.ericsson.eniq.events.ui.client.datatype.GridCellColorBlockType;
import com.ericsson.eniq.events.ui.client.datatype.GridCellColorType;
import com.ericsson.eniq.events.ui.client.datatype.GridCellTrendType;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import static com.ericsson.eniq.events.ui.client.common.CSSConstants.GRID_CELL_LINK_CSS;
import static com.ericsson.eniq.events.ui.client.common.CSSConstants.GRID_CELL_PLAIN_CSS;
import static com.ericsson.eniq.events.ui.client.common.Constants.COMMA;
import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.NO_URL_LINK;

/**
 * Class to handle rendering grid cells: Note cells more than cell backgrounds
 * 
 * 
 * 1) Color coding cells (which may also be hyperlinked)
 * 
 * 2) The rendering of hyper links to grid cells and the events raised by the
 * user on these hyper links
 * 
 * 3) Handle multiple drilldown locations for the same column
 * 
 */
public class ColorGridCellRenderer implements GridCellRenderer<ModelData> { // NOPMD (generated equals is complex - not used in performance code)

    private static final String SPAN_CLOSE = "</span>";

    private static final String HTML_CLOSE = "'>";

    private static final String CLASS_EQUALS = "'class='";

    private static final String SPAN_ID_EQUALS = "<span id='";

    private String cellUrl;

    private final GridCellColorType colorInfo;

    private CellType cellType = CellType.REGULAR_CELL; // default

    private boolean hyperlinkIfZero = true;
    
    private String zero = "0";
    /*
     * Map supporting multiple drilldowns on row, sample meta data:
     * "drillDownWindowType":
     * "*,CS_KPI_BY_BSC_FROM_MSC, callForwarding,NO_LINK, roamingCallForwarding,NO_LINK"
     * 
     * (Methods needs to be in this class as need value in the cells)
     */
    protected Map<String, String> mulipleURLsMap;

    /**
     * Constructor to call when want to color code specific columns with out
     * applying a hyperlink
     * @param colorInfo
     *            - color information (conditions)
     */
    public ColorGridCellRenderer(final GridCellColorType colorInfo) {
        this(null, colorInfo);
    }

    /**
     * Constructor for cell hyperlink renderer without setting particular color
     * contraints
     * @param url
     *            - the url that a cell hyperlink maps too.
     */
    public ColorGridCellRenderer(final String url) {
        this(url, null);
    }

    public ColorGridCellRenderer(final String url, final boolean hyperlinkIfZero) {
        this(url, null);
        this.hyperlinkIfZero = hyperlinkIfZero;
    }

    public ColorGridCellRenderer(final CellType cellType) {
        this.cellUrl = null;
        this.colorInfo = null;
        this.cellType = cellType;
    }

    /**
     * Constructor to call when want to color code and hyperlink specific cells
     * in grid
     * @param url
     *            - the url that a cell hyperlink maps too.
     * @param colorInfo
     *            - color information (conditions)
     */
    public ColorGridCellRenderer(final String url,
            final GridCellColorType colorInfo) {
        this.cellUrl = url;
        this.colorInfo = colorInfo;

        setupMultipleRowMap();
    }

    /**
     * custom renderer for hyperlinks within grid cells
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object render(final ModelData model, final String property,
            final ColumnData config, final int rowIndex, final int colIndex,
            final ListStore store, final Grid grid) {

        /* get the cell value, if null, replace with empty string */
        final String strCellVal = model.get(property) == null ? EMPTY_STRING
                : model.get(property).toString();

        if (mulipleURLsMap != null) {
            setCellUrlForRow(model.getProperties().values());
            if (colorInfo != null) {
                this.colorInfo.turnOnHyperLink(!cellUrl.equals(NO_URL_LINK));
            }
        }

        if (!hyperlinkIfZero && strCellVal.equalsIgnoreCase(zero)) {
            if (colorInfo == null)
                return renderPlainCellNoColor(strCellVal);
            else
                this.colorInfo.turnOnHyperLink(false);
        }

        if (this.cellType.equals(CellType.EVENT_CELL)) {
            return renderColorBlockCell(strCellVal);
        }

        if (this.cellType.equals(CellType.TREND_CELL)) {
            return renderTrendCell(strCellVal);
        }

        if (cellUrl != null && cellUrl.equals(NO_URL_LINK)) {
            return renderPlainCellNoColor(strCellVal);
        }

        /* trying to minimise conditionals called */
        return (cellUrl != null && cellUrl.length() > 0) ? renderHyperLink(strCellVal)
                : renderPlainCell(strCellVal);
    }

    /*
     * wrap cell value in span tags to render as a hyper link and provide the
     * url as the id (private dropped as need to expose for JU NIT)
     */
    Html renderHyperLink(final String sValue) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(SPAN_ID_EQUALS);
        buffer.append(cellUrl);
        buffer.append(CLASS_EQUALS);

        if (colorInfo == null) {
            buffer.append(GRID_CELL_LINK_CSS);
        } else {
            buffer.append(colorInfo.getCellDisplayCSS(sValue));
        }

        buffer.append(HTML_CLOSE);
        buffer.append(sValue);
        buffer.append(SPAN_CLOSE);

        return new Html(buffer.toString());
    }

    /*
     * Render colors on plain cell (no hyperlink) No business calling this
     * method with null as colorInfo so not checking it
     * @param sValue value in celll
     * @return HTML rendering
     */
    Html renderPlainCell(final String sValue) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(SPAN_ID_EQUALS);
        buffer.append(CLASS_EQUALS);
        buffer.append(colorInfo.getCellDisplayCSS(sValue));
        buffer.append(HTML_CLOSE);
        buffer.append(sValue);
        buffer.append(SPAN_CLOSE);

        return new Html(buffer.toString());
    }

    Html renderColorBlockCell(final String sValue) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<div class=\" colorBlock ");
        stringBuilder.append(GridCellColorBlockType.getColorBlockFromCell(
                sValue).toString());
        stringBuilder.append("\"></div>");
        stringBuilder.append("<div style=\"padding-left:6px\">");
        stringBuilder.append(sValue);
        stringBuilder.append("</div>");
        return new Html(stringBuilder.toString());
    }

    /* for ranking trend cell - just Icon no text */
    Html renderTrendCell(final String sValue) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<div class=\" trendCell ");
        stringBuilder.append(GridCellTrendType.getTrendFromCell(sValue)
                .toString());
        stringBuilder.append("\" title=\" "
                + GridCellTrendType.getTrendFromCell(sValue).getToolTip()
                + "\"></div>");
        return new Html(stringBuilder.toString());
    }

    /*
     * Render plain cell (no hyperlink)
     * @param sValue value in celll
     * @return HTML rendering
     */
    Html renderPlainCellNoColor(final String sValue) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(SPAN_ID_EQUALS);
        buffer.append(CLASS_EQUALS);
        if (colorInfo == null) {
            buffer.append(GRID_CELL_PLAIN_CSS);
        } else {
            buffer.append(colorInfo.getCellDisplayCSS(sValue));
        }

        buffer.append(HTML_CLOSE);
        buffer.append(sValue);
        buffer.append(SPAN_CLOSE);

        return new Html(buffer.toString());
    }

    /*
     * Handle different rows in column going to different link locations or
     * "no link". (i.e. no link will require turning off hyperlink)
     * sample meta data: "drillDownWindowType":
     * "*,CS_KPI_BY_BSC_FROM_MSC, callForwarding,NO_LINK, roamingCallForwarding,NO_LINK"
     */
    protected void setupMultipleRowMap() {
        if (cellUrl != null) {
            final String[] urlsPerRow = cellUrl.split(COMMA);
            if (urlsPerRow.length > 1) {
                mulipleURLsMap = new HashMap<String, String>();
                for (int i = 0; i < urlsPerRow.length; i = i + 2) {
                    mulipleURLsMap.put(urlsPerRow[i].trim(),
                            urlsPerRow[i + 1].trim());
                }

            }
        }
    }

    /*
     * Called when mulipleURLsMap is set and Meta data has something like
     * "*,CS_KPI_BY_BSC_FROM_MSC, something,''"  or
     *  "*,CS_KPI_BY_BSC_FROM_MSC, something,NO_LINK"
     * Reset the cellUrl based on value in row (e.g. "something") is found or
     * take wildcard one.
     */
    protected void setCellUrlForRow(final Collection<Object> rowValues) {
        boolean isFound = false;
        for (final Object rowVal : rowValues) {
            final String urlId = mulipleURLsMap.get(rowVal);
            if (urlId != null)
            {
                isFound = true;

                this.cellUrl = urlId.trim(); // can be empty string (no hyperlink required)
                break;
            }
        }
        if (!isFound) {
            this.cellUrl = mulipleURLsMap.get(CommonConstants.WILDCARD);
        }

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cellUrl == null) ? 0 : cellUrl.hashCode());
        result = prime * result
                + ((colorInfo == null) ? 0 : colorInfo.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) { // NOPMD by eeicmsy on 24/05/11 10:15
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ColorGridCellRenderer other = (ColorGridCellRenderer) obj;
        if (cellUrl == null) {
            if (other.cellUrl != null) {
                return false;
            }
        } else if (!cellUrl.equals(other.cellUrl)) {
            return false;
        }
        if (colorInfo == null) {
            if (other.colorInfo != null) {
                return false;
            }
        } else if (!colorInfo.equals(other.colorInfo)) {
            return false;
        }

        return this.hyperlinkIfZero == other.hyperlinkIfZero;
    }

}
