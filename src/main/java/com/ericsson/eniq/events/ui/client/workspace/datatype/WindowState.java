/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.datatype;

import java.util.Date;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface WindowState {
    String getTimePeriod();

    void setTimePeriod(String timePeriod);

    Date getFrom();

    void setFrom(Date from);

    Date getTo();

    void setTo(Date to);

    String getDimensionId();

    void setDimensionId(String dimensionId);

    String getTechnology();

    void setTechnology(String technology);

    String getWindowId();

    void setWindowId(String windowId);

    String getPrimarySelection();

    void setPrimarySelection(String primarySelection);

    String getPairedSelectionUrl();

    void setPairedSelectionUrl(String url);

    String getSecondarySelection();

    void setSecondarySelection(String secondarySelection);

    double getWidthRatio();

    void setWidthRatio(double widthRatio);

    double getHeightRatio();

    void setHeightRatio(double heightRatio);

    double getTopRatio();

    void setTopRatio(double topRatio);

    double getLeftRatio();

    void setLeftRatio(double leftRatio);

    /**
     * Set the relaunch status of the window.
     * @param enabled  true = show window on relaunch of WorkSpace,
     *                 false = do not show window on relaunch of WorkSpace.
     */
    void setEnabled(boolean enabled);

    boolean isEnabled();

    /**
     * Set the extra URL Params in the window state.
     * @param extraURLParams
     */
    void setExtraURLParams(String extraURLParams);
    
    String getExtraURLParams();

    /**
     * Type of ExtraURL (CONFIG).
     * @param type
     */
    void setExtraURLType(String type);

    String getExtraURLType();
}
