/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.charts;

import com.ericsson.eniq.events.common.client.datatype.ChartDisplayType;
import com.ericsson.eniq.events.highcharts.client.config.AbstractChartConfiguration;
import com.ericsson.eniq.events.highcharts.client.config.WorkSpaceHCBarChartConfiguration;
import com.ericsson.eniq.events.highcharts.client.config.HCBarWithLineChartConfiguration;
import com.ericsson.eniq.events.highcharts.client.config.HCLineChartConfiguration;
import com.ericsson.eniq.events.highcharts.client.config.HCPieChartConfiguration;
import com.ericsson.eniq.events.highcharts.client.config.HorizontalBarChartConfiguration;
import com.ericsson.eniq.events.ui.client.charts.highcharts.config.LteCoreNetKpiPortletChartConfiguration;
import com.ericsson.eniq.events.ui.client.charts.highcharts.config.LteRanKpiPortletChartConfiguration;
import com.ericsson.eniq.events.ui.client.charts.highcharts.config.DvtpChartConfiguration;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.chartconfiguration.AccessAreaPortalChartConfiguration;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.chartconfiguration.CoreNetKpiPortletChartConfiguration;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.chartconfiguration.DataVolumePortletChartConfiguration;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.chartconfiguration.HomerRoamerChartConfiguration;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.chartconfiguration.NetworkKpiChartConfiguration;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.chartconfiguration.TerminalPortalChartConfiguration;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a Registry for Chart Templates
 *
 * @author ecarsea
 * @since 2011
 */
public class ChartConfigTemplateRegistryImpl implements IChartConfigTemplateRegistry {

    private final Map<ChartDisplayType, Provider<? extends AbstractChartConfiguration>> templateMap = new HashMap<ChartDisplayType, Provider<? extends AbstractChartConfiguration>>();

    @Inject
    public ChartConfigTemplateRegistryImpl(final Provider<WorkSpaceHCBarChartConfiguration> barChartProvider,
            final Provider<HCBarWithLineChartConfiguration> barWithLineChartProvider,
            final Provider<HorizontalBarChartConfiguration> horizontalBarChartProvider,
            final Provider<HCPieChartConfiguration> pieChartProvider,
            final Provider<HCLineChartConfiguration> lineChartProvider,
            final Provider<NetworkKpiChartConfiguration> networkKpiProvider,
            final Provider<DataVolumePortletChartConfiguration> dataVolumeProvider,
            final Provider<TerminalPortalChartConfiguration> terminalChartProvider,
            final Provider<AccessAreaPortalChartConfiguration> accessareaChartProvider,
            final Provider<HomerRoamerChartConfiguration> homerRoamerChartProvider,
            final Provider<CoreNetKpiPortletChartConfiguration> coreNetKpiChartProvider,
            final Provider<LteRanKpiPortletChartConfiguration> lteRanKpiChartProvider,
            final Provider<LteCoreNetKpiPortletChartConfiguration> lteCoreNetKpiChartProvider,
            final Provider<DvtpChartConfiguration> dvtpChartProvider) {
        templateMap.put(ChartDisplayType.STANDARD_BAR, barChartProvider);
        templateMap.put(ChartDisplayType.STANDARD_HORIZONTAL_BAR, horizontalBarChartProvider);
        templateMap.put(ChartDisplayType.STANDARD_BAR_WITH_LINE, barWithLineChartProvider);
        templateMap.put(ChartDisplayType.STANDARD_PIE, pieChartProvider);
        templateMap.put(ChartDisplayType.STANDARD_LINE, lineChartProvider);
        templateMap.put(ChartDisplayType.NETWORK_KPI, networkKpiProvider);
        templateMap.put(ChartDisplayType.DATA_VOLUME, dataVolumeProvider);
        templateMap.put(ChartDisplayType.TERMINAL_PORTLET, terminalChartProvider);
        templateMap.put(ChartDisplayType.ACCESSAREA_PORTLET, accessareaChartProvider);
        templateMap.put(ChartDisplayType.HOMER_ROAMER, homerRoamerChartProvider);
        templateMap.put(ChartDisplayType.CORE_NETWORK_KPIS, coreNetKpiChartProvider);
        templateMap.put(ChartDisplayType.LTE_NETWORK_KPI, lteRanKpiChartProvider);
        templateMap.put(ChartDisplayType.LTE_CORE_NETWORK_KPIS, lteCoreNetKpiChartProvider);
        templateMap.put(ChartDisplayType.DVTP_CHART, dvtpChartProvider);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.config.IChartConfigTemplateRegistry#createByType(com.ericsson.eniq.events.ui.client.datatype.chart.ChartDisplayType)
     */
    @Override
    public AbstractChartConfiguration createByType(final ChartDisplayType name) {
        final Provider<? extends AbstractChartConfiguration> provider = templateMap.get(name);
        if (provider == null) {
            throw new IllegalArgumentException("Chart Config template for name " + name + " was not found.");
        }
        return provider.get();
    }
}
