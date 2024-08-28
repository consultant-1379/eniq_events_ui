/**
 /*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.events;
import com.ericsson.eniq.events.common.client.datatype.ChartDrillDownInfoDataType;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldUser;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
public class PieChartHelperTest extends TestEniqEventsUI
{
    GraphDrillDownLaunchEvent objecttotest;
    GraphDrillDownLaunchEventHandler graphDrillDownLaunchEventHandler;
    ChartDrillDownInfoDataType chartDrillDownInfoDataType;
    SearchFieldDataType searchFieldDataType;
    TimeInfoDataType timeInfoDataType;
    SearchFieldUser searchFieldUser;
    @Before
    public void setUp() {

        graphDrillDownLaunchEventHandler =context.mock(GraphDrillDownLaunchEventHandler.class);
        objecttotest=new GraphDrillDownLaunchEvent("tabid",chartDrillDownInfoDataType,"selectedelement",searchFieldDataType,"windowstyle",timeInfoDataType,"queryparams",searchFieldUser);

    }

    @Test
    public void hideShowNotificationOccursOnDispatch() throws Exception {

        context.checking(new Expectations() {
            {
                one(graphDrillDownLaunchEventHandler).handleLaunchFromGraphDrillDown(objecttotest);
            }

        });
        objecttotest.dispatch(graphDrillDownLaunchEventHandler);
    }

}