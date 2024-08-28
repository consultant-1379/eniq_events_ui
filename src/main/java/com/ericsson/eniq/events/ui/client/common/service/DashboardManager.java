/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.service;

import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.DashBoardDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;

import java.util.Map;

/**
 * DashboardManager for managing portlet states (position, type, etc).
 * Updates data on PortletAddEvent and PortletRemoveEvent.
 * Registered in GInjector as singleton.
 *
 * @author evyagrz
 * @since 09 2011
 */
public interface DashboardManager {

   void registerPortlet(PortletDataType portletInfo);

   void saveDashboardLayout();

   void restoreDashboardLayout(DashBoardDataType dashBoardData);

   PortletDataType getPortletDataTypeById(String portletId);

   boolean isStateRestored();

   Map<String, PortletDataType> getPortletMap();

   void clearDashboardPortlets();

   void setSearchField(SearchFieldDataType searchField);
}