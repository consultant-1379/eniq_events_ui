/*
 * -----------------------------------------------------------------------
 *      Copyright (C) ${YEAR} LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.common.service;

import com.ericsson.eniq.events.common.client.datatype.IThresholdState;
import com.ericsson.eniq.events.common.client.datatype.ThresholdDataType;
import com.ericsson.eniq.events.common.client.preferences.IUserPreferencesHelper;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.JsonAutoBeanDataFactory;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.DashBoardDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.IDashboardState;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.IPortletState;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletType;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ericsson.eniq.events.ui.client.common.Constants.SUMMARY_LTE;

/**
 * Implementation of DashboardManager
 *
 * @author evyagrz
 * @since 09 2011
 */
public class DashboardManagerImpl implements DashboardManager {

    static final String TAB_KEY = "DASHBOARD_TAB"; // TODO should be dynamic if multiple dashboards are supported

    public static final String STORAGE_KEY = "DASHBOARD_KEY";

   public static final String LTE_STORAGE_KEY = "LTE_DASHBOARD_KEY";

   private SearchFieldDataType searchField;

   boolean stateRestored = false;

   private Map<String, PortletDataType> dashboardPortlets =
           Collections.unmodifiableMap(Collections.<String, PortletDataType>emptyMap());

    private final JsonAutoBeanDataFactory jsonDataFactory;

    private final IUserPreferencesHelper userPreferencesHelper;

    @Inject
    public DashboardManagerImpl(final JsonAutoBeanDataFactory jsonDataFactory,
            final IUserPreferencesHelper userPreferencesHelper) {
        this.jsonDataFactory = jsonDataFactory;
        this.userPreferencesHelper = userPreferencesHelper;
    }

   @Override
   public void registerPortlet(final PortletDataType portletInfo) {
      final String portletId = portletInfo.getPortletId();
       HashMap<String, PortletDataType> map =
               new HashMap<String, PortletDataType>(dashboardPortlets);
       map.put(portletId, portletInfo);
       dashboardPortlets = Collections.unmodifiableMap(map);
   }

    @Override
    public void saveDashboardLayout() {
        final List<IPortletState> portletStateList = new ArrayList<IPortletState>();
        final Collection<PortletDataType> portlets = dashboardPortlets.values();

        for (final PortletDataType portlet : portlets) {
            // Exclude MAP from serializing, since it's separate portlet within it's own container
            if (!PortletType.MAP.equals(portlet.getType())) {
                final IPortletState portletState = jsonDataFactory.portletState().as();
                createPortletState(portlet, portletState);
                portletStateList.add(portletState);
            }
        }

        final IDashboardState dashboardState = jsonDataFactory.dashboardState().as();
        dashboardState.setPortletStates(portletStateList);

      if (searchField.getType().equals(SUMMARY_LTE)) {
         userPreferencesHelper.setState(LTE_STORAGE_KEY, IDashboardState.class, dashboardState);
      } else {
         userPreferencesHelper.setState(STORAGE_KEY, IDashboardState.class, dashboardState);
      }
   }

    public IPortletState createPortletState(final PortletDataType portletData, final IPortletState portletState) {
        portletState.setPortletId(portletData.getPortletId());
        portletState.setColumnIndex(portletData.getColumnIndex());
        portletState.setRowIndex(portletData.getRowIndex());
        portletState.setEmpty(PortletType.PLACE_HOLDER.equals(portletData.getType()));
        portletState.setThresholds(new ArrayList<IThresholdState>());

        final List<ThresholdDataType> thresholds = portletData.getThresholds();
        for (final ThresholdDataType threshold : thresholds) {
            final IThresholdState thresholdState = jsonDataFactory.thresholdState().as();
            thresholdState.setId(threshold.getId());
            thresholdState.setHighest(threshold.getHighest());
            thresholdState.setLowest(threshold.getLowest());

            portletState.getThresholds().add(thresholdState);
        }

        return portletState;
    }

   @Override
   public void restoreDashboardLayout(final DashBoardDataType dashBoardData) {
      final IDashboardState dashboardState;

      if (searchField.getType().equals(SUMMARY_LTE)) {
         dashboardState = userPreferencesHelper.getStateById(LTE_STORAGE_KEY, IDashboardState.class);
      } else {
         dashboardState = userPreferencesHelper.getStateById(STORAGE_KEY, IDashboardState.class);
      }
      if (dashboardState == null) {
         return; // was not saved yet
      }

        final List<IPortletState> portletStates = dashboardState.getPortletStates();
        final List<PortletDataType> portals = dashBoardData.getPortals();

        for (final IPortletState portletState : portletStates) {
            final String portletId = portletState.getPortletId();

            final PortletDataType portletData = getPortDataById(portletId, portals);
            if (portletData != null) {
                portletData.setColumnIndex(portletState.getColumnIndex());
                portletData.setRowIndex(portletState.getRowIndex());
                if (portletState.isEmpty()) {
                    portletData.setType(PortletType.PLACE_HOLDER);
                }

                final List<IThresholdState> thresholdStates = portletState.getThresholds();
                if (thresholdStates != null) {
                    for (final IThresholdState thresholdState : thresholdStates) {
                        final List<ThresholdDataType> thresholds = portletData.getThresholds();
                        for (final ThresholdDataType threshold : thresholds) {
                            if (threshold.getId().equals(thresholdState.getId())) {
                                threshold.setLowest(thresholdState.getLowest());
                                threshold.setHighest(thresholdState.getHighest());
                            }
                        }
                    }
                }
            }
        }
        stateRestored = true;
    }

    @Override
    public PortletDataType getPortletDataTypeById(final String portletId) {
        final PortletDataType portletInfo = dashboardPortlets.get(portletId);

        // In case we have a placeholder, there is no portlet.
        if (PortletType.PLACE_HOLDER.equals(portletInfo.getType())) {
            return null;
        }

        return portletInfo;
    }

    @Override
    public boolean isStateRestored() {
        return stateRestored;
    }

    private PortletDataType getPortDataById(final String portletId, final List<PortletDataType> dataTypes) {
        for (final PortletDataType dataType : dataTypes) {
            final String id = dataType.getPortletId();
            if (id.equals(portletId)) {
                return dataType;
            }
        }
        return null;
    }

   @Override
   public Map<String, PortletDataType> getPortletMap() {
      return dashboardPortlets;
   }

   @Override
   public void clearDashboardPortlets() {
       if (dashboardPortlets.size() != 0) {
           dashboardPortlets = Collections.unmodifiableMap(Collections.<String, PortletDataType>emptyMap());
       }
   }

    /** @return the searchField */
   public SearchFieldDataType getSearchField() {
      return searchField;
   }

   /** @param searchField the searchField to set */
   @Override
   public void setSearchField(final SearchFieldDataType searchField) {
      this.searchField = searchField;
   }

}