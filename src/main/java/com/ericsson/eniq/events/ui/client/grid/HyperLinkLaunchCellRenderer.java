/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.ericsson.eniq.events.ui.client.common.CSSConstants.GRID_CELL_LAUNCHER_LINK_CSS;
import static com.ericsson.eniq.events.ui.client.common.Constants.COMMA;
import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;

/**
 * class to handle the rendering of hyper links to - that will
 * in launch a new window when clicked -
 * grid cells and the events raised by the user.
 * Seperated from hyper links that drill down as the logic
 * is inherently different and has the potential to grow
 *  
 * @author eendmcm
 * @since June 2010
 */
public class HyperLinkLaunchCellRenderer implements GridCellRenderer<ModelData> {

    private String cellUrl;
    
    /* 
     * Map supporting multiple drilldowns on row, 
     * sample meta data:
     * "drillDownWindowType": "*,CS_KPI_BY_BSC_FROM_MSC, callForwarding,NO_LINK, roamingCallForwarding,NO_LINK" 
     * 
     * (Methods needs to be in this class as need value in the cells)  
     */
    private Map<String, String> mulipleURLsMap;

    private boolean isColumnWithNoHyperlink = true;

    /**
     * constructor for cell hyperlink renderer
     * @param url       - meta definition that the cell hyperlink maps too.
     * this is what will be used to define the window that gets launched 
     */
    public HyperLinkLaunchCellRenderer(final String url,final boolean isColumnWithNoHyperlink) {
        cellUrl = url;
        this.isColumnWithNoHyperlink =isColumnWithNoHyperlink;
        setupMultipleRowMap();
    }

    /**
     * custom renderer for hyper launchers within grid cells
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object render(final ModelData model, final String property, final ColumnData config, final int rowIndex,
            final int colIndex, final ListStore store, final Grid grid) {

    	/* get the cell value, if null, replace with empty string */
        final String strCellVal = model.get(property) == null ? EMPTY_STRING : model.get(property).toString();
        
        if (mulipleURLsMap != null) {
            setCellUrlForRow(model.getProperties().values());
        }
        
        /*(split like this for JUNIT)*/
        //return renderHyperLink(strCellVal);
        /* trying to minimise conditionals called */

        if(cellUrl != null && cellUrl.length() > 0 ){
                if( !isColumnWithNoHyperlink && (strCellVal.endsWith("Unknown") || strCellVal.equalsIgnoreCase("0") )){
                    return renderPlainCell(strCellVal);
                } else{
                    return renderHyperLink(strCellVal);
                }
        } else {
            return renderPlainCell(strCellVal) ;
        }
    }
    
    /*
     * Render colors on plain cell (no hyperlink)
     * No business calling this method with null as colorInfo so not checking it
     * 
     * @param sValue   value in celll
     * @return         HTML rendering
     */
    Html renderPlainCell(final String sValue) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("<span id='");
        buffer.append("'class='");
        // No business calling this method with null as colorInfo so not checking it
        buffer.append(sValue);

        buffer.append("'>");
        buffer.append(sValue);
        buffer.append("</span>");

        return new Html(buffer.toString());
    }

    /*
     * wrap cell value in span tags to render as a hyper link 
     * and provide the url as the id
     * (private dropped as need to expose for JUNIT)
     */
    Html renderHyperLink(final String sValue) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("<span id='");
        buffer.append(cellUrl);
        buffer.append("'class='");
        buffer.append(GRID_CELL_LAUNCHER_LINK_CSS);
        buffer.append("'>");
        buffer.append(sValue);
        buffer.append("</span>");
        return new Html(buffer.toString());
    }
    
    /*
     * Handle different rows in column going to different link locations or "no link".
     * (i.e. no link will require turning off hyperlink)
     * 
     * sample meta data:
     *  "drillDownWindowType": "*,CS_KPI_BY_BSC_FROM_MSC, callForwarding,NO_LINK, roamingCallForwarding,NO_LINK" 
     */
    private void setupMultipleRowMap() {
        if (cellUrl != null) {

            final String[] urlsPerRow = cellUrl.split(COMMA);
            if (urlsPerRow.length > 1) {
                mulipleURLsMap = new HashMap<String, String>();
                for (int i = 0; i < urlsPerRow.length; i = i + 2) {
                    mulipleURLsMap.put(urlsPerRow[i].trim(), urlsPerRow[i + 1].trim());
                }

            }
        }
    }
    
    /*
     * Called when mulipleURLsMap is set
     * Meta data has something like "*,CS_KPI_BY_BSC_FROM_MSC, something,''"
     * 
     * Reset the cellUrl besed on value in row (e.g. "something") in found 
     * or take wildcard one. 
     */
    private void setCellUrlForRow(final Collection<Object> rowValues) {
        boolean isFound = false;
        for (final Object rowVal : rowValues) {
            final String urlId = mulipleURLsMap.get(rowVal);
            if (urlId != null) {
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
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HyperLinkLaunchCellRenderer other = (HyperLinkLaunchCellRenderer) obj;
        if (cellUrl == null) {
            if (other.cellUrl != null) {
                return false;
            }
        } else if (!cellUrl.equals(other.cellUrl)) {
            return false;
        }
        return true;
    }

}
