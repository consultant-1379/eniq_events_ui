/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.charts;

import com.ericsson.eniq.events.common.client.datatype.ChartDisplayType;
import com.ericsson.eniq.events.highcharts.client.config.ChartConfigTemplate;

/**
 * Registry to hold all the different Chart Templates
 * @author ecarsea
 * @since 2011
 *
 */
public interface IChartConfigTemplateRegistry {

    /**
     * Retrieve a Chart Template.
     * @param type - Template Identifier
     * @return
     */
    ChartConfigTemplate createByType(ChartDisplayType type);
}
