/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.charts;

import java.util.Map;

/**
 * Interface for classes handling drill down on Chart Elements
 * @author ecarsea
 * @since 2011
 */
public interface IChartElementDrillDownListener {

    /**
     * Drill down from a chart element
     * @param drillDownDataMap - Map of name/values containing the drill down data of the drilled element.
     */
    void drillDown(Map<String, String> drillDownDataMap);

    /**
     * Method to call when chart view is changed so can 
     * reset the listener without creating new ones. 
     * 
     * Assumes using EventID to identify 
     * windows created from view menu in toolbar.
     * 
     * @param id The new id for this listener 
     */
    void setEventId(String id);

    /**
     * Only called by javascript function for external selenium testing.  
     */
    void performChartClickManually(String chartElementClicked, String drillDownWindowType);
}
