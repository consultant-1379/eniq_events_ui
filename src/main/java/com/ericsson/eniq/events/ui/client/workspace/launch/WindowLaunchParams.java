/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch;

import com.ericsson.eniq.events.common.client.time.TimePeriod;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.TechnologyType;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IDimension;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IWindow;
import com.google.gwt.user.client.ui.Widget;

import java.util.Date;
import java.util.List;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class WindowLaunchParams {
    private final TimePeriod timePeriod;

    private final Date from;

    private final Date to;

    private final IDimension dimension;

    private final TechnologyType technologyType;

    private final List<IWindow> windows;

    private final String primarySelection;

    private final String secondarySelection;

    private final Widget source;

    private final String pairedSelectionUrl;
    
    private final String extraURLParms;

    /**
     * @param timePeriod
     * @param from
     * @param to
     * @param dimension
     * @param technologyType
     * @param source
     */
    public WindowLaunchParams(TimePeriod timePeriod, Date from, Date to, IDimension dimension,
            TechnologyType technologyType, List<IWindow> windows, Widget source) {
        this(timePeriod, from, to, dimension, technologyType, windows, "", "", "", source);
    }

    /**
     * @param timePeriod
     * @param from
     * @param to
     * @param dimension
     * @param technologyType
     * @param primarySelection
     * @param source
     */
    public WindowLaunchParams(TimePeriod timePeriod, Date from, Date to, IDimension dimension,
            TechnologyType technologyType, List<IWindow> windows, String primarySelection, Widget source) {
        this(timePeriod, from, to, dimension, technologyType, windows, primarySelection, "", "", source);
    }

    /**
     * @param timePeriod
     * @param from
     * @param to
     * @param dimension
     * @param technologyType
     * @param windows
     * @param primarySelection
     * @param pairedSelectionUrl
     * @param secondarySelection
     * @param source
     */
    public WindowLaunchParams(TimePeriod timePeriod, Date from, Date to, IDimension dimension,
            TechnologyType technologyType, List<IWindow> windows, String primarySelection,
            String pairedSelectionUrl, String secondarySelection, Widget source) {
        this.timePeriod = timePeriod;
        this.from = from;
        this.to = to;
        this.dimension = dimension;
        this.technologyType = technologyType;
        this.windows = windows;
        this.primarySelection = primarySelection;
        this.pairedSelectionUrl = pairedSelectionUrl;
        this.secondarySelection = secondarySelection;
        this.source = source;
        this.extraURLParms = "";
    }


    public WindowLaunchParams(TimePeriod timePeriod, Date from, Date to, IDimension dimension,
                              TechnologyType technologyType, List<IWindow> windows, String primarySelection,
                              String pairedSelectionUrl, String secondarySelection, Widget source, String extraURLParams) {
        this.timePeriod = timePeriod;
        this.from = from;
        this.to = to;
        this.dimension = dimension;
        this.technologyType = technologyType;
        this.windows = windows;
        this.primarySelection = primarySelection;
        this.pairedSelectionUrl = pairedSelectionUrl;
        this.secondarySelection = secondarySelection;
        this.source = source;
        this.extraURLParms = extraURLParams;
    }
    /**
     * @return the timePeriod
     */
    public TimePeriod getTimePeriod() {
        return timePeriod;
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
     * @return the dimension
     */
    public IDimension getDimension() {
        return dimension;
    }

    /**
     * @return the technologyType
     */
    public TechnologyType getTechnologyType() {
        return technologyType;
    }

    /**
     * @return the window
     */
    public List<IWindow> getWindows() {
        return windows;
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
     * @return the secondarySelection
     */
    public String getSecondarySelection() {
        return secondarySelection;
    }

    /**
     * @return the source
     */
    public Widget getSource() {
        return source;
    }

    public String getExtraURLParams() {
        return extraURLParms;
    }
}
