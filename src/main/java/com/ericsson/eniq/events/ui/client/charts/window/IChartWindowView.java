/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.charts.window;

import com.ericsson.eniq.events.ui.client.charts.IChartPresenter;
import com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowView;
import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.google.gwt.http.client.Response;

/**
 * Interface for chart view
 *
 * @author eeicmsy
 * @since April 2010
 */
public interface IChartWindowView extends IBaseWindowView, IExtendedWidgetDisplay {

    /**
     * @return embedded chart widget for this view
     */
    IChartPresenter getChartControl();

    /**
     * Update the last time the data was refreshed on a toolbar label
     *
     * @param response response from server call
     */
    void upDateLastRefreshedLabel(Response response);

}
