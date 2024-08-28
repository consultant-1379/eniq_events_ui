/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2013 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.GridViewConfig;
import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Collection;
import java.util.Date;

import static com.ericsson.eniq.events.ui.client.common.CSSConstants.*;
import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;

/**
 * @author eeikbe
 * @since 10/2013
 */
public class AllGridViewConfig extends GridViewConfig {

    private final static long MINUTE1 = 60000;
    private final static long MINUTE3 = 180000;
    private final static long MINUTE6 = 360000;

    private boolean isTimeGap = false;
    private boolean isColorRow = false;
    private boolean isRankingRow = false;

    private void setColorRow() {
        isColorRow = shouldTurnOnRowColors(this.gridMetaData);
    }

    public void setRankingRow() {
        if(windowType.equals(MetaMenuItemDataType.Type.RANKING)){
            isRankingRow = true;
        }else{
            isRankingRow = false;
        }
    }

    public void setTimeGap() {
        //only show the time gap if the metadata has it set AND the Success RAW tables are not being used.
        //if getSuccessRAWToggle returns true, don't show time dots.
        if(isSuccessRAWActivated()){
            isTimeGap = false;
        }else{
            if(isTimeGapColumnSet()){
                isTimeGap = true;
            } else{
                isTimeGap = false;
            }
        }
    }

    private final GridInfoDataType gridMetaData;
    private final MetaMenuItemDataType.Type windowType;
    
    public AllGridViewConfig(MetaMenuItemDataType.Type windowType, GridInfoDataType gridMetaData) {
        this.gridMetaData = gridMetaData;
        this.windowType = windowType;
        setColorRow();
        setTimeGap();
        setRankingRow();
    }

    @Override
    public String getRowStyle(ModelData model, int rowIndex, ListStore<ModelData> ds) {
        StringBuilder sb = new StringBuilder();
        if(isTimeGap){
            //Only show the time dots if the sort field == the time gap column.
            if(ds.getSortField() == null || ds.getSortField().equals(this.gridMetaData.timeGapWithColumn)){
                sb.append(this.getRowStyleForTimeGap(model, rowIndex, ds));
            }
        }
        if(isColorRow){
            sb.append(" ");
            sb.append(this.getRowStyleForEventResult(model));
        }
        if(isRankingRow){
            sb.append(" ");
            sb.append(getrowStyleForRanking(model));
        }
        return sb.toString();
    }

    /**
     * Get the style to append a TimeGap of 1, 2 or 3 to the row.
     * @param model
     * @param rowIndex
     * @param ds
     * @return
     */
    private String getRowStyleForTimeGap(ModelData model, int rowIndex, ListStore<ModelData> ds){
        String style = EMPTY_STRING;
        if(rowIndex != 0){
            String gridKey = gridMetaData.timeGapWithColumn;
            String currentRowDateTimeString = model.get(gridKey);
            String previousRowDateTimeString = ds.getAt(rowIndex-1).get(gridKey);
            DateTimeFormat labelDateFormat = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
            try{
                Date rowDate = labelDateFormat.parse(currentRowDateTimeString);
                Date previousRowDate = labelDateFormat.parse(previousRowDateTimeString);
                long previousRowTime = previousRowDate.getTime();
                long currentRowTime = rowDate.getTime();
                long timeDifference = 0;
                if(previousRowDate.before(rowDate)){
                    timeDifference = currentRowTime - previousRowTime;
                }else if(previousRowDate.after(rowDate)){
                    timeDifference = previousRowTime - currentRowTime;
                }
                if(timeDifference >= MINUTE1 && timeDifference <= MINUTE3){
                    style = TIME_GAP_ONE_CSS;
                }else if(timeDifference > MINUTE3 && timeDifference <= MINUTE6){
                    style = TIME_GAP_TWO_CSS;
                }else if(timeDifference > MINUTE6){
                    style = TIME_GAP_THREE_CSS;
                }
            }catch(IllegalArgumentException e){
                e.printStackTrace();
            }
        }
        return style;    
    }

    /**
     * Get the style to apply to a ranking grid. Only applies the style to 1st,
     * 2nd, 3rd, 4th and 5th place
     * @param model
     * @return
     */
    private String getrowStyleForRanking(final ModelData model){
        StringBuilder style = new StringBuilder();

        ColumnInfoDataType type = gridMetaData.columnInfo[Integer.parseInt(gridMetaData.sortColumn)-1];
        if(type.columnHeader.equalsIgnoreCase("rank")){
            if (model.get(gridMetaData.sortColumn) instanceof Long) {
            style.append(RANKING_ROW_CSS);
            style.append(" ");
            Long rank = model.get(gridMetaData.sortColumn);
            if(rank.longValue() == 1){
                style.append(RANKING_ONE_CSS);
            }else if(rank.longValue() == 2){
                style.append(RANKING_TWO_CSS);
            }else if(rank.longValue() == 3){
                style.append(RANKING_THREE_CSS);
            }else if(rank.longValue() == 4){
                style.append(RANKING_FOUR_CSS);
            }else if(rank.longValue() == 5){
                style.append(RANKING_FIVE_CSS);
                }
            }
        }
        return style.toString();
    }

    /**
     * Get the style for the row based on the value of a cell on the row.
     * This method sets the color of the row.
     * @param model
     * @return
     */
    public String getRowStyleForEventResult(final ModelData model) {
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


    
    private boolean shouldTurnOnRowColors(final GridInfoDataType gridMetaData) {
        //No grids are using coloured rows at the moment but they might in the future, so if they do then you'll
        //need a check like the one commented out below.
        //return headersContains(EVENT_RESULT_HEADER, gridMetaData);
        return false;


    }

    private boolean headersContains(final String headerName, final GridInfoDataType gridMetaData) {
        final ColumnInfoDataType[] columnInfo = gridMetaData.columnInfo;
        for (final ColumnInfoDataType column : columnInfo) {
            if (column.columnHeader.equals(headerName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTimeGapColumnSet(){
        boolean isTimeGapColumnSet = true;
        if(gridMetaData.timeGapWithColumn.equals("0")){
            isTimeGapColumnSet = false;
        }
        return isTimeGapColumnSet;
    }


    /**
     * Read the JNDI parameter from Glassfish. The parameter is called ENIQ_EVENTS_SUC_RAW. If this
     * parameter is not present in JNDI, it is presumed that the SUCCESS_RAW table is being used.
     * ENIQ_EVENTS_SUC_RAW set to true indicates the SUCCESS_RAW table is used, so do not show time gap.
     * ENIQ_EVENTS_SUC_RAW set to false indicates the SUCCESS_RAW table is not used, so do show time gap.
     * @return
     */
    public static native boolean isSuccessRAWActivated()
    /*-{
            if(($wnd.successRAWToggle === "false") || ($wnd.successRAWToggle === "FALSE")){
                return false;
            }

            return true; //return the default
    }-*/;

}
