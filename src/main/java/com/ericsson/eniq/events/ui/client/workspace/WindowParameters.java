/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace;

import com.ericsson.eniq.events.common.client.time.TimePeriod;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IDimension;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IWindow;

import java.util.Date;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class WindowParameters {

    private final Date from;

    private final Date to;

    private final String primarySelection;

    private final String pairedSelectionUrl;

    private final TimePeriod timePeriod;
    
    private String extraURLParams; //Additional URL parameters.
    
    private String extraURLType;  //Store the type (CONFIG).
    
    private IWindow window;

    private final String secondarySelection;

    private final IDimension dimension;

    private final SearchFieldDataType searchData;

    private final String category;

    private final String technologyType;
    
    

    /**
     * @param dimension
     * @param timePeriod
     * @param from
     * @param to
     * @param primarySelection
     * @param secondarySelection
     * @param window
     * @param searchData
     * @param technologyType 
     */
    public WindowParameters(IDimension dimension, TimePeriod timePeriod, Date from, Date to, String primarySelection,
            String pairedSelectionUrl, String secondarySelection, IWindow window, SearchFieldDataType searchData,
            String category, String technologyType) {
        this.dimension = dimension;
        this.timePeriod = timePeriod;
        this.from = from;
        this.to = to;
        this.primarySelection = primarySelection;
        this.pairedSelectionUrl = pairedSelectionUrl;
        this.secondarySelection = secondarySelection;
        this.window = window;
        this.searchData = searchData;
        this.category = category;
        this.technologyType = technologyType;
        this.extraURLParams = "";
        this.extraURLType = "";
    }


    public WindowParameters(IDimension dimension, TimePeriod timePeriod, Date from, Date to, String primarySelection,
                            String pairedSelectionUrl, String secondarySelection, IWindow window, SearchFieldDataType searchData,
                            String category, String technologyType, String extraURLParams, String extraURLType) {
        this.dimension = dimension;
        this.timePeriod = timePeriod;
        this.from = from;
        this.to = to;
        this.primarySelection = primarySelection;
        this.pairedSelectionUrl = pairedSelectionUrl;
        this.secondarySelection = secondarySelection;
        this.window = window;
        this.searchData = searchData;
        this.category = category;
        this.technologyType = technologyType;
        this.extraURLParams = extraURLParams;
        this.extraURLType = extraURLType;
    }

    public WindowParameters(WindowParameters windowParameters) {
        this.dimension = windowParameters.getDimension();
        this.timePeriod = windowParameters.getTimePeriod();
        this.from = windowParameters.getFrom();
        this.to = windowParameters.getTo();
        this.primarySelection = windowParameters.getPrimarySelection();
        this.pairedSelectionUrl = windowParameters.getPairedSelectionUrl();
        this.secondarySelection = windowParameters.getSecondarySelection();
        this.window = windowParameters.getWindow();
        this.searchData = windowParameters.getSearchData();
        this.category = windowParameters.getCategory();
        this.technologyType = windowParameters.getTechnology();
        this.extraURLParams = windowParameters.getExtraURLParams();
    }

    /**
     * @return the dimension
     */
    public IDimension getDimension() {
        return dimension;
    }

    /**
     * @return the from
     */
    public Date getFrom() {
        return from;
    }

    /**
     * @return the to
     */
    public Date getTo() {
        return to;
    }

    /**
     * @return the primarySelection
     */
    public String getPrimarySelection() {
        return primarySelection;
    }

    /**
     * @return the pairedSelectionUrl
     */
    public String getPairedSelectionUrl() {
        return pairedSelectionUrl;
    }

    /**
     * @return the timePeriod
     */
    public TimePeriod getTimePeriod() {
        return timePeriod;
    }

    /**
     * @return the window
     */
    public IWindow getWindow() {
        return window;
    }

    /**
     * @return the secondarySelection
     */
    public String getSecondarySelection() {
        return secondarySelection;
    }

    /**
     * @return the searchData
     */
    public SearchFieldDataType getSearchData() {
        return searchData;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @return the technologyType
     */
    public String getTechnology() {
        return technologyType;
    }
    
    public String getExtraURLParams(){
        return this.extraURLParams;
    }
    
    public void setExtraURLParams(final String extraURLParams){
        this.extraURLParams = extraURLParams;
    }

    public String getExtraURLType(){
        return this.extraURLType;
    }

    public void setWindow(final IWindow window){
        this.window = window;
    }
}
