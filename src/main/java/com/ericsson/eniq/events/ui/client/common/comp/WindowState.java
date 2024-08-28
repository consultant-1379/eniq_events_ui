/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

/**
 * TODO: it is supposed to move all transferable state of a window here:
 * Candidates to move here (into this class):
 * isToggling from BaseWinChangeGridViewHandler.handleChangeGridView(...) (and parameters SearchFieldDataType
 * searchData, TimeInfoDataType timeInfo)
 * fields from BaseWindowPresenter: e.g. drillDepth, drillDownWidgetID, searchData, timeData, etc.
 * store window title and its components (prefixes) there
 * from EventGridPresenter: isDrillDownTitleParamsSet
 * TODO: All AbstractWindowLauncher.launchWindow(...) methods should be invoked with proper WindowState were necessary.
 *
 * @author ealeerm
 * @since 05/2012
 */
public class WindowState {

    private int drillDepth;

    public WindowState() {
    }

    public boolean isDrillDown() {
        return drillDepth > 0;
    }

    public int getDrillDepth() {
        return drillDepth;
    }

    public void incrementDrillDepth() {
        this.drillDepth++;
    }

    public void decrementDrillDepth() {
        this.drillDepth--;
        if (drillDepth < 0) {
            resetDrillDepth();
        }
    }

    public void resetDrillDepth() {
        this.drillDepth = 0;
    }

    @Override
    public String toString() {
        return "WindowState{drillDepth=" + drillDepth + '}';
    }
}
