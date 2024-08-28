/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid.filters;

import static com.ericsson.eniq.events.common.client.CommonConstants.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.FilterConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.i18n.client.DateTimeFormat;

/**

/**
 * @author ekurshi
 * @since 2011
 *
 */
public class DateTimeFilterTest extends TestEniqEventsUI {

    private DateTimeFilter objToTest;

    DateTimeField mockedDateTimeField;

    DateTimeField mockedOnDateTimeField;

    DateTimeField mockedBeforeDateTimeField;

    DateTimeField mockedAfterDateTimeField;

    @Before
    public void setUp() {
        mockedDateTimeField = context.mock(DateTimeField.class, "dummyField");
        mockedOnDateTimeField = context.mock(DateTimeField.class, "onField");
        mockedBeforeDateTimeField = context.mock(DateTimeField.class, "beforeField");
        mockedAfterDateTimeField = context.mock(DateTimeField.class, "afterField");
        objToTest = new StubDateTimeFilter("colID");

    }

    @Test
    public void testOnFilter() {
        context.checking(new Expectations() {
            {
                final Date someDate = convertStringToDate("2011-02-02 10:30:00");
                exactly(4).of(mockedOnDateTimeField).getDate();
                will(returnValue(someDate));
                exactly(4).of(mockedBeforeDateTimeField).getDate();
                will(returnValue(null));
                exactly(4).of(mockedAfterDateTimeField).getDate();
                will(returnValue(null));
            }
        });
        final List<FilterConfig> filterConfigList = objToTest.getSerialArgs();
        assertEquals("Expected list size is 1 ", 1, filterConfigList.size());
        final ModelData dataModel = createModelDataInstance();
        //testing same date
        final String sameDate = "2011-02-02 10:30:00";
        dataModel.set("colID", sameDate);
        assertTrue("Expected same date must not be filtered.", objToTest.validateModel(dataModel));
        //testing after date
        final String afterDate = "2011-02-03 10:30:00";
        dataModel.set("colID", afterDate);
        assertFalse("Expected after date must be filtered.", objToTest.validateModel(dataModel));
        //testing before date
        final String beforeDate = "2011-02-01 10:30:00";
        dataModel.set("colID", beforeDate);
        assertFalse("Expected before date must be filtered.", objToTest.validateModel(dataModel));
    }

    @Test
    public void testAfterFilter() {
        context.checking(new Expectations() {
            {
                final Date someDate = convertStringToDate("2011-02-02 10:30:00");
                exactly(2).of(mockedOnDateTimeField).getDate();
                will(returnValue(null));
                exactly(4).of(mockedBeforeDateTimeField).getDate();
                will(returnValue(null));
                exactly(4).of(mockedAfterDateTimeField).getDate();
                will(returnValue(someDate));
            }
        });
        final List<FilterConfig> filterConfigList = objToTest.getSerialArgs();
        assertEquals("Expected list size is 1 ", 1, filterConfigList.size());
        final ModelData dataModel = createModelDataInstance();
        //testing same date
        final String sameDate = "2011-02-02 10:30:00";
        dataModel.set("colID", sameDate);
        assertFalse("Expected same date must be filtered.", objToTest.validateModel(dataModel));
        //testing after date
        final String afterDate = "2011-02-03 10:30:00";
        dataModel.set("colID", afterDate);
        assertTrue("Expected after date must not be filtered.", objToTest.validateModel(dataModel));
        //testing before date
        final String beforeDate = "2011-02-01 10:30:00";
        dataModel.set("colID", beforeDate);
        assertFalse("Expected before date must filtered.", objToTest.validateModel(dataModel));
    }

    @Test
    public void testBeforeFilter() {
        context.checking(new Expectations() {
            {
                final Date someDate = convertStringToDate("2011-02-02 10:30:00");
                exactly(2).of(mockedOnDateTimeField).getDate();
                will(returnValue(null));
                exactly(4).of(mockedBeforeDateTimeField).getDate();
                will(returnValue(someDate));
                exactly(2).of(mockedAfterDateTimeField).getDate();
                will(returnValue(null));
            }
        });
        final List<FilterConfig> filterConfigList = objToTest.getSerialArgs();
        assertEquals("Expected list size is 1 ", 1, filterConfigList.size());
        final ModelData dataModel = createModelDataInstance();
        //testing same date
        final String sameDate = "2011-02-02 10:30:00";
        dataModel.set("colID", sameDate);
        assertFalse("Expected same date must be filtered.", objToTest.validateModel(dataModel));
        //testing after date
        final String afterDate = "2011-02-03 10:30:00";
        dataModel.set("colID", afterDate);
        assertFalse("Expected after date must be filtered.", objToTest.validateModel(dataModel));
        //testing before date
        final String beforeDate = "2011-02-01 10:30:00";
        dataModel.set("colID", beforeDate);
        assertTrue("Expected before date must not be filtered.", objToTest.validateModel(dataModel));
    }

    @Test
    public void testAfterAndBeforeFilter() {
        context.checking(new Expectations() {
            {
                final Date afterDate = convertStringToDate("2011-02-02 00:00:00");
                final Date beforeDate = convertStringToDate("2011-02-06 00:00:00");
                exactly(2).of(mockedOnDateTimeField).getDate();
                will(returnValue(null));
                atMost(5).of(mockedBeforeDateTimeField).getDate();
                will(returnValue(beforeDate));
                atMost(4).of(mockedAfterDateTimeField).getDate();
                will(returnValue(afterDate));
            }
        });
        final List<FilterConfig> filterConfigList = objToTest.getSerialArgs();
        assertEquals("Expected list size is 2 ", 2, filterConfigList.size());
        final ModelData dataModel = createModelDataInstance();
        //testing before range
        final String dateBeforeRange = "2011-02-01 10:30:00";
        dataModel.set("colID", dateBeforeRange);
        assertFalse("Expected date before range must be filtered.", objToTest.validateModel(dataModel));
        //testing after range
        final String dateAfterRange = "2011-02-07 10:30:00";
        dataModel.set("colID", dateAfterRange);
        assertFalse("Expected date before range must be filtered.", objToTest.validateModel(dataModel));
        //testing in between range
        final String dateBetweenRange = "2011-02-03 10:30:00";
        dataModel.set("colID", dateBetweenRange);
        assertTrue("Expected date falls in range must not be filtered.", objToTest.validateModel(dataModel));
        //testing on range
        final String dateOnRange = "2011-02-01 10:30:00";
        dataModel.set("colID", dateOnRange);
        assertFalse("Expected date on range must be filtered.", objToTest.validateModel(dataModel));

    }

    private class StubDateTimeFilter extends DateTimeFilter {

        /**
         * @param dataIndex
         */
        public StubDateTimeFilter(final String dataIndex) {
            super(dataIndex);

        }

        @Override
        DateTimeField createDateTimeField() {
            return mockedDateTimeField;
        }

        @Override
        DateTimeField getBeforeDateField() {
            return mockedBeforeDateTimeField;
        }

        @Override
        DateTimeField getAfterDateField() {
            return mockedAfterDateTimeField;
        }

        @Override
        DateTimeField getOnDateField() {
            return mockedOnDateTimeField;
        }

        @Override
        void configureMenu() {

        }
    }

    /**
     * @return new BaseModelData instance
     */
    private ModelData createModelDataInstance() {
        return new BaseModelData();
    }

    /**
     * dateStr must be in format "yyyy-MM-dd HH:mm:ss"

     */
    private Date convertStringToDate(final String dateStr) {
        return DateTimeFormat.getFormat(Displayed_Date_Format).parse(dateStr);
    }
}
