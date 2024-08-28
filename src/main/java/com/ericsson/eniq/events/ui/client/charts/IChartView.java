/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.charts;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public interface IChartView extends IsWidget {

    /**
     * @param overflowX
     */
    void setHorizontalScrollEnabled(boolean overflowX);

    /**
     * @return
     */
    boolean isVisible();

    /**
     * @return
     */
    boolean isRendered();
}
