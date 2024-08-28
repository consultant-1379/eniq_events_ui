/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.charts;

import static junit.framework.Assert.*;

import java.util.Map;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.common.client.datatype.ChartDrillDownInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.ChartDrillDownParameterInfoDataType;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.charts.window.ChartWindowPresenter;
import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType;
import com.ericsson.eniq.events.ui.client.events.ChangeChartGridEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.dev.util.collect.HashMap;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.EventBus;

public class ChartElementDrillDownListenerTest extends TestEniqEventsUI {

    private static final String CAUSE_CODE = "CAUSE_CODE";

    private static final String DRILLDOWN_ID = "DRILLDOWN_ID";

    private static final String APN = "myApn";

    private static final String SUBSCRIBER_SUBBI_APN = "/SUBSCRIBER/SUBBI/APN";

    JSONValue mockedData;

    ModelData mockedModelData;

    ChartWindowPresenter mockedChartPresenter;

    ChartDrillDownInfoDataType mockedChartDrillDownInfoDataType;

    Map<String, String> drillDownData;

    private ChartElementDrillDownListener objtoTest;

    @Before
    public void setUp() {
        mockedChartPresenter = context.mock(ChartWindowPresenter.class);
        mockedChartDrillDownInfoDataType = context.mock(ChartDrillDownInfoDataType.class);
        setupContext();
        objtoTest = getObjectToTest(SUBSCRIBER_SUBBI_APN, APN, DRILLDOWN_ID, CAUSE_CODE);
    }

    @Test
    public void drillSetsWigitParams() {
        objtoTest.drillDown(drillDownData);
        final String expectedWigitParams = "&drilldown=myApn&display=grid";

        assertEquals("drill sends correct params", expectedWigitParams, objtoTest.newWindowInfo.getWidgetSpecificParams());
    }

    @Test
    public void chartDrillSetsParameters() {
        objtoTest.drillDown(drillDownData);

        assertEquals("Max rows parameter not set correctly", "MAX_ROWS_PARAM", objtoTest.newWindowInfo.maxRowsParam);
        assertEquals("Drilldown window id not set correctly", "GRID_ID", objtoTest.newWindowInfo.drillDownWindowType);
        assertEquals("Toolbar type parameter not set correctly", "DRILL_TOOLBAR_TYPE", objtoTest.newWindowInfo.toolbarType);
    }

    private ChartElementDrillDownListener getObjectToTest(final String wsURL, final String elementName, final String drilldownId,
                                                          final String chartMetaId) {

        drillDownData = new HashMap<String, String>() {
            {
                put("elementKey", elementName);
                put("drilldownKey", drilldownId);
                put("CHART_META_ID", chartMetaId);
            }
        };
        context.checking(new Expectations() {
            {
                one(mockedChartPresenter).setWindowType(MetaMenuItemDataType.Type.GRID);
                one(mockedChartPresenter).getWsURL();
                will(returnValue(wsURL));
                one(mockedChartPresenter).getWindowType();
                will(returnValue(MetaMenuItemDataType.Type.CHART));

                exactly(1).of(mockedEventBus).fireEvent(with(any(ChangeChartGridEvent.class)));
            }
        });

        final ChartElementDrillDownListener returnVal = new StubChartElementDrillDownListener(mockedEventBus, mockedChartPresenter);
        return returnVal;
    }

    private void setupContext() {
        context.checking(new Expectations() {
            {
                final ChartDrillDownParameterInfoDataType[] parameterInfo = new ChartDrillDownParameterInfoDataType[1];
                parameterInfo[0] = new ChartDrillDownParameterInfoDataType("MAX_ROWS_PARAM", "50", true);

                one(mockedChartDrillDownInfoDataType).getParameters();
                will(returnValue(parameterInfo));

                one(mockedChartDrillDownInfoDataType).getDrillTargetDisplayId();
                will(returnValue("GRID_ID"));

                one(mockedChartDrillDownInfoDataType).getDrillToolbarType();
                will(returnValue("DRILL_TOOLBAR_TYPE"));

                one(mockedChartDrillDownInfoDataType).getMaxRowsParam();
                will(returnValue("MAX_ROWS_PARAM"));

                one(mockedChartDrillDownInfoDataType).getChartClickedURLParam();
                will(returnValue("drilldown="));

                one(mockedChartPresenter).getMultipleInstanceWinId();

                one(mockedChartDrillDownInfoDataType).getWsURL();

                one(mockedChartDrillDownInfoDataType).getDrillType();

                one(mockedChartPresenter).getChartConfigInfo(CAUSE_CODE);

            }
        });
    }

    private class StubChartElementDrillDownListener extends ChartElementDrillDownListener {

        public StubChartElementDrillDownListener(final EventBus eventBus, final ChartWindowPresenter win) {
            super(eventBus, win);
        }

        @Override
        ChartDrillDownInfoDataType getChartDrillDownInfo(final String drillDownWindowType) {
            return mockedChartDrillDownInfoDataType;
        }

    }

}