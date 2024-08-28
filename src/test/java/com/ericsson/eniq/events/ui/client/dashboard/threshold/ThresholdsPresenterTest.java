package com.ericsson.eniq.events.ui.client.dashboard.threshold;

import com.ericsson.eniq.events.common.client.datatype.ThresholdDataType;
import com.ericsson.eniq.events.common.client.threshold.ThresholdsView;
import com.ericsson.eniq.events.common.client.threshold.UpdateCommand;
import com.ericsson.eniq.events.ui.client.common.service.DashboardManager;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.StubbedPortletDataType;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.Button;
import com.google.web.bindery.event.shared.EventBus;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class ThresholdsPresenterTest {

    private EventBus eventBus;

    private ThresholdsPresenter presenter;

    private ThresholdsView view;

	private DashboardManager dashboardManager;

    private Button button;

    @BeforeClass
    public static void init() {
        GWTMockUtilities.disarm();
    }

    @Before
    public void setUp() {
        this.eventBus = Mockito.mock(EventBus.class);
        this.view = Mockito.mock(ThresholdsView.class);
        this.button = Mockito.mock(Button.class);
        this.dashboardManager = Mockito.mock(DashboardManager.class);

        this.presenter = new ThresholdsPresenter(eventBus, view, dashboardManager);
    }

//    @Test
//    public void shouldBind() {
//        final ThresholdDataType threshold = createThreshold();
//        final PortletDataType portletData = createPortlet();
//        portletData.getThresholds().add(threshold);
//
//        presenter.addThresholdsSection(portletData);
//
//        presenter.bind();
//
//        Mockito.verify(view).addSection("id", "name", portletData.getThresholds());
//    }

//    @Test
//    public void shouldUnbind() {
//        presenter.bind();
//        presenter.unbind();
//    }

    @Test
    public void shouldUpdateValaue() {
    	final ThresholdDataType threshold = createThreshold();
    	presenter.changeValue("A", new UpdateCommand(threshold, true, 1.0));
    	presenter.changeValue("B", new UpdateCommand(threshold, false, 2.0));

    	presenter.update();
    	
    	Assert.assertThat(threshold.getHighest(), CoreMatchers.equalTo(1.0));
    	Assert.assertThat(threshold.getLowest(), CoreMatchers.equalTo(2.0));
    	Mockito.verify(eventBus, Mockito.times(2)).fireEvent(Matchers.any(GwtEvent.class));
    	Mockito.verify(dashboardManager).saveDashboardLayout();
    }
    
    private PortletDataType createPortlet() {
        return new StubbedPortletDataType.Builder().portalId("id").portalName("name").height("1").build();
    }

    private ThresholdDataType createThreshold() {
        return new ThresholdDataType("1", ThresholdDataType.Format.NUMBER, "name", 0.0, 1.0);
    }

}
