/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.charts.highcharts.config;

import com.ericsson.eniq.events.highcharts.client.ChartEnums;
import com.ericsson.eniq.events.highcharts.client.options.ChartOptions;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.chartconfiguration.CoreNetKpiPortletChartConfiguration;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

/**
 * @author eshusai
 * @since 2012
 *
 */
public class LteRanKpiPortletChartConfiguration extends CoreNetKpiPortletChartConfiguration {

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.charts.highcharts.config.HCBarChartConfiguration#getChartOptions(java.lang.String)
     */
    @Override
    protected ChartOptions getChartOptions(final String containerDiv) {
        final ChartOptions chartOptions = super.getChartOptions(containerDiv);
        chartOptions.setProperty("marginLeft", 153);
        chartOptions.setProperty("spacingTop", 0);
        chartOptions.setProperty("spacingBottom", 0);
        chartOptions.setProperty("marginTop", getChartMetaData().chartTitle.isEmpty() ? 0 : 31);
        chartOptions.setProperty("marginRight", 15);
        chartOptions.setProperty("marginBottom", getChartMetaData().chartTitle.isEmpty() ? 31 : 0);
        chartOptions.setProperty("borderWidth", 0);
        chartOptions.setProperty("reflow", false);
        /** Set the height and width here related to the height/width of the actual container element, which is the parent of the high charts 
         * frame **/
        final Element chartElement = DOM.getElementById(containerDiv);
        if (chartElement != null) {
            chartOptions.setHeight(chartElement.getParentElement().getClientHeight());
        }
        chartOptions.setZoomType(ChartEnums.eZoomType.none);
        return chartOptions;
    }

}
