/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype.dashboard;

public enum PortletType {
   GRID("grid"), CHART("chart"), PLACE_HOLDER("placeHolder"), BUSINESS_OBJECTS("businessObjects"), DATA_VOLUME(
           "dataVolume"), HOMER_ROAMER("homerRoamer"), TOP_TERMINALS("terminalPortlet"), TOP_ACCESSAREA(
           "accessareaPortlet"), NETWORK_KPI("networkkpi"), MAP("map"), CORE_NETWORK_KPIS("corenetworkkpis"), LTE_NETWORK_KPI(
           "ltenetworkkpi"), LTE_CORE_NETWORK_KPIS("ltecorenetworkkpis"), UNKNOWN("unknown");

   private final String type;

   PortletType(final String type) {
      this.type = type;
   }

   public String getType() {
      return type;
   }

   public static PortletType fromString(final String type) {
      final PortletType[] values = PortletType.values();
      for (final PortletType value : values) {
         if (value.getType().equals(type)) {
            return value;
         }
      }
      return UNKNOWN;
   }
}