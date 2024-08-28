/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.common.widget;

import java.util.HashMap;
import java.util.Map;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.common.comp.MenuTaskBar;
import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.ericsson.eniq.events.ui.client.events.WidgetSpecificParamsChangeEvent;
import com.ericsson.eniq.events.ui.client.main.AbstractWindowLauncher;
import com.ericsson.eniq.events.ui.client.main.GridLauncher;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.web.bindery.event.shared.EventBus;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JMock.class)
public class RecurErrorWindowHelperTest extends TestEniqEventsUI {

    RecurErrorWindowHelperStub objToTest;

    private IEventGridView mockedEventGridView;

    private ModelData mockedRecord;

    private MenuTaskBar mockedMenuTaskBar;

    private IBaseWindowPresenter mockedPresenter;

    private AbstractBaseWindowDisplay mockedRecurErrorWindow;

    private GridLauncher mockedLauncher;

    @Before
    public void setUp() {
        mockedEventGridView = context.mock(EventGridView.class);
        mockedRecord = context.mock(ModelData.class);
        mockedMenuTaskBar = context.mock(MenuTaskBar.class);
        mockedPresenter = context.mock(IBaseWindowPresenter.class);
        mockedRecurErrorWindow = context.mock(AbstractBaseWindowDisplay.class);
        mockedLauncher = context.mock(GridLauncher.class);

        objToTest = getObjectToTest();
    }

    @After
    public void tearDown() {
        objToTest = null;
    }

    private RecurErrorWindowHelperStub getObjectToTest() {
        context.checking(new Expectations() {
            {
                allowing(mockedEventGridView).getWorkspaceController();
                will(returnValue(mockedMenuTaskBar));
                allowing(mockedMenuTaskBar).addSubmitSearchHandler(with(any(RecurErrorWindowHelper.class)));
            }
        });
        return new RecurErrorWindowHelperStub(mockedEventGridView, EventType.RECUR_ERR_SUBSCRIBER, mockedEventBus);
    }

    @Test
    public void fireWidgetChangeEventForExistingWindow() {

        setupGeneralExpectationsRecurButtonPress();

        context.checking(new Expectations() {
            {
                allowing(mockedMenuTaskBar).getTabOwnerId();
                allowing(mockedEventBus).fireEvent(with(any(WidgetSpecificParamsChangeEvent.class)));
                allowing(mockedRecurErrorWindow).toFront();
            }
        });

        objToTest.displayRecurringFailedEventsWindow(mockedRecord, mockedEventGridView);
    }

    @Test
    public void launchNewRecurErrorWindowWithOneWeek() {

        setupGeneralExpectationsRecurTime();

        context.checking(new Expectations() {
            {
                allowing(mockedEventGridView).getParentWindow();
                allowing(mockedEventGridView).getTimeData();
                allowing(mockedEventGridView).getPresenter();
                allowing(mockedPresenter).getSearchData();

                allowing(mockedLauncher).launchWindowWithPresetSearchData(getDummyTimeData(),
                        TimeInfoDataType.DEFAULT_ONE_WEEK_TIME_DATA, true);
            }
        });

        objToTest.isTestNewWindow = true;
        objToTest.displayRecurringFailedEventsWindow(mockedRecord, mockedEventGridView);
    }

    @Test
    public void changeSearchFieldHandlingValueOnly() {

        setupGeneralExpectationsRecurButtonPress();

        context.checking(new Expectations() {
            {
                allowing(mockedMenuTaskBar).getSearchComponentValue("");
                will(returnValue(getDifferentDummySearchData()));

                allowing(mockedRecurErrorWindow).getBaseWindowID();
            }
        });
        objToTest.currentSearchData = this.getDummySearchData();
        objToTest.submitSearchFieldInfo();
    }

    @Test
    public void changeSearchFieldHandlingChangeTypeKillsWindow() {

        setupGeneralExpectationsRecurButtonPress();

        context.checking(new Expectations() {
            {
                allowing(mockedMenuTaskBar).getSearchComponentValue("");
                will(returnValue(getDifferentTypeDummySearchData()));

                allowing(mockedRecurErrorWindow).getBaseWindowID();
                allowing(mockedRecurErrorWindow).hide();
            }
        });
        objToTest.currentSearchData = this.getDummySearchData();
        objToTest.submitSearchFieldInfo();
    }

    @Test
    public void changeSearchFieldHandlingChangeGroupModeKillsWindow() {

        setupGeneralExpectationsRecurButtonPress();

        context.checking(new Expectations() {
            {
                allowing(mockedRecurErrorWindow).getBaseWindowID();

                allowing(mockedMenuTaskBar).getSearchComponentValue();
                will(returnValue(getDifferentGroupDummySearchData()));

                allowing(mockedMenuTaskBar).getSearchComponentValue("");

                allowing(mockedRecurErrorWindow).hide();
            }
        });
        objToTest.currentSearchData = this.getDummySearchData();
        objToTest.submitSearchFieldInfo();
    }

    @Test
    public void changeSearchFieldHandlingNullChangeKillsWindow() {

        setupGeneralExpectationsRecurButtonPress();

        context.checking(new Expectations() {
            {
                allowing(mockedRecurErrorWindow).getBaseWindowID();

                allowing(mockedMenuTaskBar).getSearchComponentValue();
                will(returnValue(getDifferentGroupDummySearchData()));

                allowing(mockedMenuTaskBar).getSearchComponentValue("");

                allowing(mockedRecurErrorWindow).hide();
            }
        });
        objToTest.currentSearchData = this.getNullTypeSearchData();
        objToTest.submitSearchFieldInfo();
    }

    //////////////////////////////////////////////////////////////

    private void setupGeneralExpectationsRecurButtonPress() {
        context.checking(new Expectations() {
            {

                allowing(mockedEventGridView).getPresenter();
                will(returnValue(mockedPresenter));

                allowing(mockedPresenter).getSearchData();
                will(returnValue(getDummySearchData()));

                allowing(mockedEventGridView).getColumns();
                will(returnValue(getDummmyGridInfoDataType()));

                allowing(mockedRecord).get("1");
                allowing(mockedRecord).get("2");

            }
        });

    }

    private void setupGeneralExpectationsRecurTime() {
        context.checking(new Expectations() {
            {

                allowing(mockedEventGridView).getTimeData();
                will(returnValue(TimeInfoDataType.DEFAULT_ONE_WEEK_TIME_DATA));

                allowing(mockedEventGridView).getPresenter();
                will(returnValue(mockedPresenter));

                allowing(mockedPresenter).getSearchData();
                will(returnValue(getDummyTimeData()));

                allowing(mockedEventGridView).getColumns();
                will(returnValue(getDummmyGridInfoDataType()));

                allowing(mockedRecord).get("1");
                allowing(mockedRecord).get("2");

            }
        });

    }

    private GridInfoDataType getDummmyGridInfoDataType() {
        final GridInfoDataType gridInfo = new GridInfoDataType();

        gridInfo.columnInfo = new ColumnInfoDataType[2];

        gridInfo.columnInfo[0] = new ColumnInfoDataType();
        gridInfo.columnInfo[1] = new ColumnInfoDataType();

        gridInfo.columnInfo[0].columnHeader = "Cause Code";
        gridInfo.columnInfo[1].columnHeader = "Sub Cause Code";
        gridInfo.columnInfo[0].columnID = "1";
        gridInfo.columnInfo[1].columnID = "2";

        return gridInfo;
    }


    private SearchFieldDataType getDummyTimeData() {
        final String date = "1 week";
        final String[] urlParams = new String[] { "date=" + date, "type=date" };

        return new SearchFieldDataType(date, urlParams, "date", null, false, "", null, false);

    }

    private SearchFieldDataType getDummySearchData() {
        final String imsi = "12121";
        final String[] urlParams = new String[] { "imsi=" + imsi, "type=IMSI" };

        return new SearchFieldDataType(imsi, urlParams, "IMSI", null, false, "", null, false);

    }

    private SearchFieldDataType getDifferentDummySearchData() {
        final String imsi = "55555";
        final String[] urlParams = new String[] { "imsi=" + imsi, "type=IMSI" };

        return new SearchFieldDataType(imsi, urlParams, "IMSI", null, false, "", null, false);

    }

    private SearchFieldDataType getDifferentTypeDummySearchData() {
        final String ptImsi = "44";
        final String[] urlParams = new String[] { "ptmsi=" + ptImsi, "type=PTMSI" };

        return new SearchFieldDataType(ptImsi, urlParams, "PTMSI", null, false, "", null, false);

    }

    private SearchFieldDataType getDifferentGroupDummySearchData() {
        final String imsiGroup = "whatever";
        final String[] urlParams = new String[] { "imsigroup=" + imsiGroup, "type=IMSI" };

        return new SearchFieldDataType(imsiGroup, urlParams, "PTMSI", null, true, "", null, false);

    }

    private SearchFieldDataType getNullTypeSearchData() {
        final String whatever = "whatever";
        final String[] urlParams = new String[] { "whatever=" + whatever, "type=whatever" };
        // no type passed to args
        return new SearchFieldDataType(whatever, urlParams, null, null, false, "", null, false);

    }

    private class RecurErrorWindowHelperStub extends RecurErrorWindowHelper {

        boolean isTestNewWindow;

        /**
         * @param viewRef
         * @param eventID
         * @param eBus
         */
        public RecurErrorWindowHelperStub(final IEventGridView viewRef, final EventType eventID, final EventBus eBus) {

            super(viewRef, eventID, eBus);

        }

        @Override
        MetaMenuItemDataType createRecurErrMetaMenuItemDataType() {
            final MetaMenuItemDataType metaData = new MetaMenuItemDataType.Builder().build();
            metaData.id = "recurID";
            return metaData;
        }

        @Override
        Map<String, String> getRecurErrHeadersParameters(final boolean includeOptional) {
            final Map<String, String> map = new HashMap<String, String>();
            map.put("Cause Code", "causeCodeHeader");
            map.put("Sub Cause Code", "subCauseCodeHeader");
            return map;
        }

        @Override
        BaseWindow getWindow(final String recurErrWinDataId) {
            if (isTestNewWindow) {
                return null;
            }
            return mockedRecurErrorWindow;
        }

        @Override
        String getRecurringErrSummaryWebServiceURL() {
            return "url";
        }

        @Override
        AbstractWindowLauncher createGridLauncher(final MetaMenuItem item, final ContentPanel containingPanel) {
            return mockedLauncher;
        }

    }

}
