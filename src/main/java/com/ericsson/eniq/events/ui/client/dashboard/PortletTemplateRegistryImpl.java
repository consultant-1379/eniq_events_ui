/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard;

import com.ericsson.eniq.events.ui.client.businessobjects.BusinessObjectsPortlet;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.ChartPortlet;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.CoreNetworkKPIsPortlet;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.DataVolumePortlet;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.EmptyPortlet;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.GridPortlet;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.HomerRoamerPortlet;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.LTENetworkKPIPortlet;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.LteCoreNetworkKPIsPortlet;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.MapPresenter;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.NetworkKPIPortlet;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.TopAccessAreaPortlet;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.TopTerminalsPortlet;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletType;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

public class PortletTemplateRegistryImpl implements PortletTemplateRegistry {

   private final Map<PortletType, Provider<? extends PortletTemplate>> templateMap = new HashMap<PortletType, Provider<? extends PortletTemplate>>();

   @Inject
   public PortletTemplateRegistryImpl(final Provider<ChartPortlet> chartPortletProvider,
                                      final Provider<DataVolumePortlet> dataVolumePortletProvider,
                                      final Provider<GridPortlet> gridPortletProvider,
                                      final Provider<BusinessObjectsPortlet> businessObjectsPortletProvider,
                                      final Provider<NetworkKPIPortlet> networkKpiPortlet, final Provider<EmptyPortlet> emptyPortletProvider,
                                      final Provider<HomerRoamerPortlet> homerRoamerPortletProvider,
                                      final Provider<TopTerminalsPortlet> topTerminalsPortletProvider,
                                      final Provider<TopAccessAreaPortlet> topAccessAreaPortletProvider,
                                      final Provider<MapPresenter> mapPortletProvider,
                                      final Provider<CoreNetworkKPIsPortlet> coreNetworkKPIsProvider,
                                      final Provider<LteCoreNetworkKPIsPortlet> lteCoreNetworkKPIsProvider,
                                      final Provider<LTENetworkKPIPortlet> lteNetworkKPIsProvider) {

      templateMap.put(PortletType.CHART, chartPortletProvider);
      templateMap.put(PortletType.DATA_VOLUME, dataVolumePortletProvider);
      templateMap.put(PortletType.GRID, gridPortletProvider);
      templateMap.put(PortletType.BUSINESS_OBJECTS, businessObjectsPortletProvider);
      templateMap.put(PortletType.NETWORK_KPI, networkKpiPortlet);
      templateMap.put(PortletType.UNKNOWN, emptyPortletProvider);
      templateMap.put(PortletType.PLACE_HOLDER, emptyPortletProvider);
      templateMap.put(PortletType.MAP, mapPortletProvider);
      templateMap.put(PortletType.HOMER_ROAMER, homerRoamerPortletProvider);
      templateMap.put(PortletType.TOP_TERMINALS, topTerminalsPortletProvider);
      templateMap.put(PortletType.TOP_ACCESSAREA, topAccessAreaPortletProvider);
      templateMap.put(PortletType.CORE_NETWORK_KPIS, coreNetworkKPIsProvider);
      templateMap.put(PortletType.LTE_NETWORK_KPI, lteNetworkKPIsProvider);
      templateMap.put(PortletType.LTE_CORE_NETWORK_KPIS, lteCoreNetworkKPIsProvider);
   }

   @Override
   public PortletTemplate createByName(final PortletType name) {
      final Provider<? extends PortletTemplate> provider = templateMap.get(name);
      if (provider == null) {
         throw new IllegalArgumentException("Portlet template for name " + name + " was not found.");
      }
      return provider.get();
   }
}