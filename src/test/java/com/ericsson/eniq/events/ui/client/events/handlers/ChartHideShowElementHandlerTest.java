package com.ericsson.eniq.events.ui.client.events.handlers;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.charts.ChartElementDetails;
import com.ericsson.eniq.events.ui.client.charts.IChartPresenter;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class ChartHideShowElementHandlerTest extends TestEniqEventsUI {

    private ChartHideShowElementHandler objUnderTest;

    private IChartPresenter mockedCurrentChart;

    @Before
    public void setUp() {

        mockedCurrentChart = context.mock(IChartPresenter.class);
        objUnderTest = new ChartHideShowElementHandler(createMultipleWinId("tabId", "winId"), mockedCurrentChart);
    }

    @After
    public void tearDown() {
        objUnderTest = null;
    }

    @Test
    public void sameWindowInAnotherTabNotAffected() throws Exception {
        // no action for mocked chart     
        objUnderTest.handleShowChartElementsEvent(createMultipleWinId("SomeOtherTab", "winId"), null);
    }

    @Test
    public void differentWindowNotAffected() throws Exception {
        // no action for mocked chart     
        objUnderTest.handleShowChartElementsEvent(createMultipleWinId("tabId", "someOtherWinId"), null);

    }

    @Test
    public void sameWindowSameTabAffected() throws Exception {

        context.checking(new Expectations() {
            {
                one(mockedCurrentChart).showChartElements(with(any(Set.class)));

            }
        });
        objUnderTest.handleShowChartElementsEvent(createMultipleWinId("tabId", "winId"),
                new HashSet<ChartElementDetails>());

    }

    @Test
    public void legendChangeCallAffected() throws Exception {

        context.checking(new Expectations() {
            {
                one(mockedCurrentChart).hideShowChartLegend();

            }
        });
        objUnderTest.handleHideShowChartLegend(createMultipleWinId("tabId", "winId"));

    }

    @Test
    public void legendChangeCallNotAffectedDifferentTab() throws Exception {
        objUnderTest.handleHideShowChartLegend(createMultipleWinId("differentTabId", "winId"));
    }

    @Test
    public void legendChangeCallNotAffectedDifferentWindow() throws Exception {
        objUnderTest.handleHideShowChartLegend(createMultipleWinId("tabId", "diffentWinId"));
    }

    private MultipleInstanceWinId createMultipleWinId(final String tabId, final String winId) {
        return new MultipleInstanceWinId(tabId, winId/*, null*/);
    }

}
