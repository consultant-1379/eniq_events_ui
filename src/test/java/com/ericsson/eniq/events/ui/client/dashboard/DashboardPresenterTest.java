package com.ericsson.eniq.events.ui.client.dashboard;

import com.ericsson.eniq.events.ui.client.common.service.DashboardManager;
import com.ericsson.eniq.events.ui.client.dashboard.threshold.ThresholdsPresenter;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldUser;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.StubbedPortletDataType;
import com.ericsson.eniq.events.ui.client.resources.EniqResourceBundle;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.web.bindery.event.shared.EventBus;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author evyagrz
 * @since 10 2011
 */
public class DashboardPresenterTest {

    private StubbedDashboardPresenter dashboardPresenter;

    private DashboardManager dashboardManager;
    private EventBus eventBus;

    @BeforeClass
    public static void init() {
        GWTMockUtilities.disarm();
    }

    @Before
    public void setUp() throws Exception {
        dashboardManager = mock(DashboardManager.class);
        IDashboardTaskbarHelper taskbarHelper = mock(IDashboardTaskbarHelper.class);
        eventBus = mock(EventBus.class);
        dashboardPresenter = new StubbedDashboardPresenter(eventBus, mock(EniqResourceBundle.class),
                mock(DashboardView.class), dashboardManager, mock(PortletTemplateRegistry.class),
                mock(ThresholdsPresenter.class));
        dashboardPresenter.setTaskBarHelper(taskbarHelper);
    }

    @Test
    public void testPortletPositionOnLayout() throws Exception {
        final ArrayList<PortletDataType> portalInfos = new ArrayList<PortletDataType>();

        final PortletDataType one = createPortletDataType();
        final PortletDataType two = createPortletDataType();
        final PortletDataType three = createPortletDataType();
        final PortletDataType four = createPortletDataType();

        portalInfos.add(one);
        portalInfos.add(two);
        portalInfos.add(three);
        portalInfos.add(four);

        dashboardPresenter.buildDefaultPortletLayout(portalInfos);

        verify(dashboardManager, times(4)).registerPortlet(Matchers.any(PortletDataType.class));

        assertThat(one.getRowIndex(), equalTo(0));
        assertThat(one.getColumnIndex(), equalTo(0));
        assertThat(two.getRowIndex(), equalTo(0));
        assertThat(two.getColumnIndex(), equalTo(1));
        assertThat(three.getRowIndex(), equalTo(0));
        assertThat(three.getColumnIndex(), equalTo(2));
        assertThat(four.getRowIndex(), equalTo(1));
        assertThat(four.getColumnIndex(), equalTo(0));
    }

    @Test
    public void shouldHandleFail() {
        dashboardPresenter.setEventHandlingIds("tabId", "winId");

        final MultipleInstanceWinId multiWinId = mock(MultipleInstanceWinId.class);
        when(multiWinId.getTabId()).thenReturn("tabId");

        dashboardPresenter.handleFail(multiWinId, "data", new NullPointerException());

        verify(eventBus, times(3)).fireEvent(any(GwtEvent.class));
    }

    private PortletDataType createPortletDataType() {
        return new StubbedPortletDataType.Builder().type(PortletType.CHART).isSearchFieldUser(SearchFieldUser.PATH)
                .build();
    }

    private class StubbedDashboardPresenter extends DashboardPresenter {

        public StubbedDashboardPresenter(final EventBus eventBus, final EniqResourceBundle eniqResourceBundle,
                final DashboardView view, final DashboardManager dashboardManager,
                final PortletTemplateRegistry portletTemplateRegistry, final ThresholdsPresenter thresholdPresenter) {
            super(eventBus, eniqResourceBundle, view, dashboardManager, portletTemplateRegistry, thresholdPresenter);
            // TODO Auto-generated constructor stub
        }

    }

}
