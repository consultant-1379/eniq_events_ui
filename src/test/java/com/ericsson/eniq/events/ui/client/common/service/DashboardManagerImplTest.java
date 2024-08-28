package com.ericsson.eniq.events.ui.client.common.service;

import com.ericsson.eniq.events.common.client.datatype.IThresholdState;
import com.ericsson.eniq.events.common.client.preferences.IUserPreferencesHelper;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.JsonAutoBeanDataFactory;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.DashBoardDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.IDashboardState;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.IPortletState;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.StubbedPortletDataType;
import com.google.web.bindery.autobean.shared.AutoBean;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


//import com.ericsson.eniq.events.ui.client.datatype.dashboard.IDashboardStateWrapper;

/**
 * @author evyagrz
 * @since 10 2011
 */
public class DashboardManagerImplTest {

   private DashboardManagerImpl manager;

   private IUserPreferencesHelper userPreferencesHelper;

   private JsonAutoBeanDataFactory jsonDataFactory;

   @Before
   public void setUp() {
      userPreferencesHelper = mock(IUserPreferencesHelper.class);
      jsonDataFactory = mock(JsonAutoBeanDataFactory.class);
      manager = new DashboardManagerImpl(jsonDataFactory, userPreferencesHelper);
      final String url[] = {"type=SUMMARY_WCDMA"};
      manager.setSearchField(new SearchFieldDataType("", url, "SUMMARY_WCDMA", null, false, "CS,PS", null, false));
   }

   @Test
   public void shouldSavePortletState() throws Exception {
      final AutoBean dashboardStateAutoBean = mock(AutoBean.class);
      final IDashboardState dashboardState = mock(IDashboardState.class);
      when(dashboardStateAutoBean.as()).thenReturn(dashboardState);

      final AutoBean<IPortletState> portletStateAutoBean = mock(AutoBean.class);
      final IPortletState portletState = mock(IPortletState.class);
      when(portletStateAutoBean.as()).thenReturn(portletState);

      final AutoBean thresholdStateAutoBean = mock(AutoBean.class);
      final IThresholdState thresholdState = mock(IThresholdState.class);
      when(thresholdStateAutoBean.as()).thenReturn(thresholdState);

        when(jsonDataFactory.dashboardState()).thenReturn(dashboardStateAutoBean);
        when(jsonDataFactory.portletState()).thenReturn(portletStateAutoBean);
        when(jsonDataFactory.thresholdState()).thenReturn(thresholdStateAutoBean);

      manager.registerPortlet(createPortletDataType());
      manager.saveDashboardLayout();

      verify(userPreferencesHelper).setState(DashboardManagerImpl.STORAGE_KEY, IDashboardState.class, dashboardState);
      verify(dashboardState).setPortletStates(org.mockito.Mockito.any(List.class));
   }

   @Test
   public void shouldRestorePortletState() throws Exception {
      final IDashboardState dashboardStateWrapper = mock(IDashboardState.class);
      final List<IPortletState> portletStates = new ArrayList<IPortletState>();
      final IPortletState portletState = mock(IPortletState.class);
      when(portletState.getPortletId()).thenReturn("portletId");
      when(portletState.getColumnIndex()).thenReturn(2);
      when(portletState.getRowIndex()).thenReturn(2);
      when(portletState.isEmpty()).thenReturn(true);
      portletStates.add(portletState);

      when(dashboardStateWrapper.getPortletStates()).thenReturn(portletStates);
      when(userPreferencesHelper.getStateById(DashboardManagerImpl.STORAGE_KEY, IDashboardState.class)).thenReturn(
              dashboardStateWrapper);

      final DashBoardDataType dashboardData = createDashboardData(createPortletDataType());

      manager.restoreDashboardLayout(dashboardData);

      assertThat(dashboardData.getPortals().size(), equalTo(1));
      final PortletDataType portletDataType = dashboardData.getPortals().get(0);
      assertThat(portletDataType.getColumnIndex(), equalTo(2));
      assertThat(portletDataType.getRowIndex(), equalTo(2));
      assertThat(portletDataType.getType(), equalTo(PortletType.PLACE_HOLDER));
   }

   private DashBoardDataType createDashboardData(final PortletDataType... dataTypes) {
      return new DashBoardDataType("tabOwner", "winId", "title", "1", Arrays.asList(dataTypes));
   }

   private PortletDataType createPortletDataType() {

      return new StubbedPortletDataType.Builder().tabOwnerId("tabOwner").portalId("portletId")
              .type(PortletType.CHART).build();
   }

}
