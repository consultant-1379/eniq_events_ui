/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import static com.ericsson.eniq.events.ui.client.common.CSSConstants.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Enum Holder for all information relevant to 
 * colour rendering certain table cells.
 * 
 * Small functionality added to temp turn off htperlinks to
 * support multiple row rendering on same column
 *
 * @author eeicmsy
 * @since June 2011
 */
public enum GridCellColorType {
    
    /**
     * render info for KPI cell
     */
    KPI_SUCCESS_RATIO_DRILLDOWN  (new KPIConditions(true)), 
    KPI_SUCCESS_RATIO_PLAIN_CELL (new KPIConditions(false));;

    private final IColorValueDependant conditions;
  
    /*
     * References this class might build up as more colors 
     * go one hyperlinks (to support hack where hyperlink click listener use of CSS strings)
     */
    public final static List<String> drilldownCSSStrings = new ArrayList<String>();

    static {
        drilldownCSSStrings.add(GRID_CELL_LINK_CSS);
        drilldownCSSStrings.add(GRID_CELL_LINK_CODE_RED_CSS);
        drilldownCSSStrings.add(GRID_CELL_LINK_CODE_AMBER_CSS);
    }
    
    


    /**
     * Get colour to display cell ot hyperlink 
     * (which may have colour too)
     * 
     * @param cellValue    current value in cell 
     * 
     * @return             hyperlink CSS with a colour (or a colour if this cell is not 
     *                     for hyperlinking)
     */
    public String getCellDisplayCSS(final String cellValue) {
        return conditions.getCellDisplayCSS(cellValue);
    }
    
    /**
     * Utility for situations where don't want certain rows to take
     * hyperlink. This TEMPORALLIY resets the 
     * requiring hyperlink flag - (ie not "damaging" our enum) 
      
     * (support for to turn off hyperlink on 
     * some rows but not on other rows in the same column)
     * @param hyperlinkOn  - false to turn off a hyperlink (and vice versa)
     */
    public void turnOnHyperLink(final boolean hyperlinkOn){
        conditions.turnOnHyperLink(hyperlinkOn);
    }

    
    
  /////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////   private methods and classes
  /////////////////////////////////////////////////////////////////////////////////////////////  
    
    /*
     * Enum construct      
     * @param conditions  conditions for this enum  
     */
    private GridCellColorType(final IColorValueDependant conditions) {
        this.conditions = conditions;

    }


    /* 
     * Conditions for rendering waht is known to be KPI cell, 
     * i.e. a float and hyperlinking 
     * */
    private final static class KPIConditions implements IColorValueDependant {

        private boolean isHyperLink;
        
        /*
         * Boolean which has to be reset if
         * change the enum property at run time
         * (support for request to turn off hyperlink on 
         * some rows but not on other rows in the coloum
         * (ref - #turnOnHyperLink) 
         */
        private final boolean origonalIsHyperLink;
        
        public KPIConditions(final boolean isHyperLink){
            this.isHyperLink = isHyperLink;
            this.origonalIsHyperLink = isHyperLink;
        }

        @Override
        public String getCellDisplayCSS(final String cellValue) {
            // empty check bit of a hit which could consider loosing
            String returnVal = null;
           
            if (! cellValue.isEmpty()){ 
                
                // fall over if NumberFormatException (we have to trust its a float)
                final float val = Float.parseFloat(cellValue);     
            
                // can not exit - may need to reset hyperlink boolean
 
                if (val <= getCutOffRed()){
                    returnVal = (isHyperLink) ? GRID_CELL_LINK_CODE_RED_CSS : GRID_CELL_PLAIN_CODE_RED_CSS;
                                     
                } else if (val <= getCutOffAmber()) {
                    returnVal = (isHyperLink) ? GRID_CELL_LINK_CODE_AMBER_CSS : GRID_CELL_PLAIN_CODE_AMBER_CSS;
                   
                }
            }
            
            if (returnVal == null){
                returnVal = (isHyperLink) ? GRID_CELL_LINK_CSS : GRID_CELL_PLAIN_CSS;
            }
           // reset the enum changed by the (hack) required to temp change it
           this.isHyperLink = origonalIsHyperLink;
           return returnVal;
        }
        
        @Override
        public void turnOnHyperLink(final boolean hyperlinkOn){
            isHyperLink = hyperlinkOn;
            
        }
    }
    

 
    private interface IColorValueDependant {

        String getCellDisplayCSS(final String cellValue);
        
        void turnOnHyperLink (boolean hyperlinkOn);
    }
    
    
    ///////////////////////////////////// Tried these in Constants class proved problematic
    
    
    private static float cutOffRed = -1;
    private static float cutOffAmber = -1;
    
    private static float getCutOffRed(){
        if (cutOffRed == -1){
            cutOffRed = getNativeKPIThresholdRed();
        }
        return cutOffRed;
    }
    private static float getCutOffAmber(){
        if (cutOffAmber == -1){
            cutOffAmber = getNativeKPIThresholdAmber();
        }
        return cutOffAmber;
    }
     

    // read amber KPI threshold in via glassfish (usaully 97)
    private static native float getNativeKPIThresholdAmber() /*-{
		return $wnd.kpiAmberThreshold;
    }-*/;

    // read red KPI threshold in via glassfish (usaully 95)
    private static native float getNativeKPIThresholdRed() /*-{
		return $wnd.kpiRedThreshold;
    }-*/;

    
 
}
