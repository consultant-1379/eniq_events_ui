/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.kpi;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.common.client.datatype.IPropertiesState;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.kpi.KPIConfigurationPanelDataType;
import com.ericsson.eniq.events.ui.client.datatype.kpi.KPIConfigurationPanelElement;

/**
 * @author eemecoy
 *
 */
public class KPIConfigurationPropertiesTest extends TestEniqEventsUI {

    private KPIConfigurationProperties kpiConfigurationProperties;

    IMetaReader metaReader;

    @Before
    public void setupTest() {
        kpiConfigurationProperties = new KPIConfigurationProperties();
        metaReader = context.mock(IMetaReader.class);
    }

    @Test
    public void testValidateSelectionWithEmptyListReturnsNoErrors() {
        final KPIConfigurationPanelDataType metaData = null;
        context.checking(new Expectations() {
            {
                allowing(metaReader).getKPIConfigurationPanelMetaData();
                will(returnValue(metaData));
            }
        });
        assertThat(kpiConfigurationProperties.validateSelection(metaReader, new HashMap<String, TimeInfoDataType>())
                .isEmpty(), is(true));
    }

    @Test
    public void testValidateEntry_RefreshTime_ValueValid() {
        setUpMetaDataForMinMaxValuesForRefreshTime(0, 90);
        final TimeInfoDataType userSelection = createTimeInfoDataType("10");
        assertEquals(null, kpiConfigurationProperties.validateEntry(metaReader, KPIConfigurationConstants.REFRESH_TIME,
                userSelection));
    }

    @Test
    public void testValidateEntry_RefreshTime_ValueTooLow() {
        setUpMetaDataForMinMaxValuesForRefreshTime(2, 60);
        final TimeInfoDataType userSelection = createTimeInfoDataType("1");
        assertEquals("Value should be between 2 minutes and 1 hour", kpiConfigurationProperties.validateEntry(
                metaReader, KPIConfigurationConstants.REFRESH_TIME, userSelection));
    }

    @Test
    public void testValidateEntry_RefreshTime_ValueTooHigh() {
        setUpMetaDataForMinMaxValuesForRefreshTime(5, 1440);
        final TimeInfoDataType userSelection = createTimeInfoDataType("1441");
        assertEquals("Value should be between 5 minutes and 1 day", kpiConfigurationProperties.validateEntry(
                metaReader, KPIConfigurationConstants.REFRESH_TIME, userSelection));
    }

    private void setUpMetaDataForMinMaxValuesForRefreshTime(final int minValueForRefreshTime,
            final int maxValueForRefreshTime) {
        final KPIConfigurationPanelDataType metaData = new KPIConfigurationPanelDataType();
        final KPIConfigurationPanelElement windowTimePeriod = new KPIConfigurationPanelElement();
        windowTimePeriod.setMinValue(minValueForRefreshTime);
        windowTimePeriod.setMaxValue(maxValueForRefreshTime);
        metaData.setRefreshTime(windowTimePeriod);
        context.checking(new Expectations() {
            {
                allowing(metaReader).getKPIConfigurationPanelMetaData();
                will(returnValue(metaData));
            }
        });

    }

    @Test
    public void testValidateEntry_RefreshRate_ValueTooLow() {
        setUpMetaDataForMinMaxValuesForRefreshRate(5, 60);
        final TimeInfoDataType userSelection = createTimeInfoDataType("1");
        assertEquals("Value should be between 5 minutes and 1 hour", kpiConfigurationProperties.validateEntry(
                metaReader, KPIConfigurationConstants.REFRESH_RATE, userSelection));
    }

    @Test
    public void testValidateEntry_RefreshRate_ValueTooHigh() {
        setUpMetaDataForMinMaxValuesForRefreshRate(5, 1440 * 2);
        final TimeInfoDataType userSelection = createTimeInfoDataType("10080");
        assertEquals("Value should be between 5 minutes and 2 days", kpiConfigurationProperties.validateEntry(
                metaReader, KPIConfigurationConstants.REFRESH_RATE, userSelection));
    }

    @Test
    public void testValidateEntry_RefreshRate_ValueValid() {
        setUpMetaDataForMinMaxValuesForRefreshRate(0, 90);
        final TimeInfoDataType userSelection = createTimeInfoDataType("30");
        assertEquals(null, kpiConfigurationProperties.validateEntry(metaReader, KPIConfigurationConstants.REFRESH_RATE,
                userSelection));
    }

    private void setUpMetaDataForMinMaxValuesForRefreshRate(final int refreshRateMinValue, final int refreshRateMaxValue) {
        final KPIConfigurationPanelDataType metaData = new KPIConfigurationPanelDataType();
        final KPIConfigurationPanelElement refreshRateDetails = new KPIConfigurationPanelElement();
        refreshRateDetails.setMinValue(refreshRateMinValue);
        refreshRateDetails.setMaxValue(refreshRateMaxValue);
        metaData.setRefreshRate(refreshRateDetails);
        context.checking(new Expectations() {
            {
                allowing(metaReader).getKPIConfigurationPanelMetaData();
                will(returnValue(metaData));
            }
        });

    }

    @Test
    public void testValidatingProperties_AllPropertiesOK() {
        setUpMetaDataForMinMaxValues(0, 90, 0, 1440);
        final Map<String, TimeInfoDataType> userTimeDetails = new HashMap<String, TimeInfoDataType>();
        final TimeInfoDataType refreshRate = createTimeInfoDataType("30");
        userTimeDetails.put(KPIConfigurationConstants.REFRESH_RATE, refreshRate);
        final TimeInfoDataType refreshTime = createTimeInfoDataType("90");
        userTimeDetails.put(KPIConfigurationConstants.REFRESH_TIME, refreshTime);
        final Map<String, String> result = kpiConfigurationProperties.validateSelection(metaReader, userTimeDetails);
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void testValidatingProperties_RefreshRateTooLow() {
        final int minValue = 5;
        final int maxValue = 60 * 24; //1 day
        setUpMetaDataForMinMaxValues(minValue, maxValue, 0, 1440);
        final Map<String, TimeInfoDataType> userTimeDetails = new HashMap<String, TimeInfoDataType>();
        final TimeInfoDataType refreshRate = createTimeInfoDataType("3");
        userTimeDetails.put(KPIConfigurationConstants.REFRESH_RATE, refreshRate);
        final TimeInfoDataType refreshTime = createTimeInfoDataType("90");
        userTimeDetails.put(KPIConfigurationConstants.REFRESH_TIME, refreshTime);
        final Map<String, String> result = kpiConfigurationProperties.validateSelection(metaReader, userTimeDetails);
        assertThat(result.size(), is(1));
        final String validationError = result.get(KPIConfigurationConstants.REFRESH_RATE);
        assertEquals("Value should be between " + minValue + " minutes and 1 day", validationError);
    }

    @Test
    public void testValidatingProperties_RefreshTimeTooHigh() {
        final int minValueForRefreshRate = 5;
        final int maxValueForRefreshRate = 60 * 24; //1 day
        final int minValueForRefreshTime = 60;
        final int maxValueForRefreshTime = 1440;
        setUpMetaDataForMinMaxValues(minValueForRefreshRate, maxValueForRefreshRate, minValueForRefreshTime,
                maxValueForRefreshTime);
        final Map<String, TimeInfoDataType> userTimeDetails = new HashMap<String, TimeInfoDataType>();
        final TimeInfoDataType refreshRate = createTimeInfoDataType("6");
        userTimeDetails.put(KPIConfigurationConstants.REFRESH_RATE, refreshRate);
        final TimeInfoDataType refreshTime = createTimeInfoDataType("10080");
        userTimeDetails.put(KPIConfigurationConstants.REFRESH_TIME, refreshTime);
        final Map<String, String> result = kpiConfigurationProperties.validateSelection(metaReader, userTimeDetails);
        assertThat(result.size(), is(1));
        final String validationError = result.get(KPIConfigurationConstants.REFRESH_TIME);
        assertEquals("Value should be between 1 hour and 1 day", validationError);
    }

    @Test
    public void testValidatingProperties_BothValuesTooHigh() {
        final int minValueForRefreshRate = 5;
        final int maxValueForRefreshRate = 60 * 24; //1 day
        final int minValueForRefreshTime = 60;
        final int maxValueForRefreshTime = 1440;
        setUpMetaDataForMinMaxValues(minValueForRefreshRate, maxValueForRefreshRate, minValueForRefreshTime,
                maxValueForRefreshTime);
        final Map<String, TimeInfoDataType> userTimeDetails = new HashMap<String, TimeInfoDataType>();
        final TimeInfoDataType refreshRate = createTimeInfoDataType("10080");
        userTimeDetails.put(KPIConfigurationConstants.REFRESH_RATE, refreshRate);
        final TimeInfoDataType refreshTime = createTimeInfoDataType("10080");
        userTimeDetails.put(KPIConfigurationConstants.REFRESH_TIME, refreshTime);
        final Map<String, String> result = kpiConfigurationProperties.validateSelection(metaReader, userTimeDetails);
        assertThat(result.size(), is(2));
        final String validationErrorForRefreshTime = result.get(KPIConfigurationConstants.REFRESH_TIME);
        assertEquals("Value should be between 1 hour and 1 day", validationErrorForRefreshTime);
        final String validationErrorForRefreshRate = result.get(KPIConfigurationConstants.REFRESH_RATE);
        assertEquals("Value should be between 5 minutes and 1 day", validationErrorForRefreshRate);
    }

    private void setUpMetaDataForMinMaxValues(final int refreshRateMinValue, final int refreshRateMaxValue,
            final int minValueForRefreshTime, final int maxValueForRefreshTime) {
        final KPIConfigurationPanelDataType metaData = new KPIConfigurationPanelDataType();
        final KPIConfigurationPanelElement refreshRateDetails = new KPIConfigurationPanelElement();
        refreshRateDetails.setMinValue(refreshRateMinValue);
        refreshRateDetails.setMaxValue(refreshRateMaxValue);
        metaData.setRefreshRate(refreshRateDetails);
        final KPIConfigurationPanelElement windowTimePeriod = new KPIConfigurationPanelElement();
        windowTimePeriod.setMinValue(minValueForRefreshTime);
        windowTimePeriod.setMaxValue(maxValueForRefreshTime);
        metaData.setRefreshTime(windowTimePeriod);
        context.checking(new Expectations() {
            {
                allowing(metaReader).getKPIConfigurationPanelMetaData();
                will(returnValue(metaData));
            }
        });

    }

    private TimeInfoDataType createTimeInfoDataType(final String userSelectedValueInMinutes) {
        final TimeInfoDataType timeInfoDataType = new TimeInfoDataType();
        timeInfoDataType.timeRange = userSelectedValueInMinutes;
        return timeInfoDataType;
    }

    private void setUpRefreshTimeDefaultValue(final String defaultValue) {
        final KPIConfigurationPanelDataType metaData = new KPIConfigurationPanelDataType();
        final KPIConfigurationPanelElement windowTimePeriod = new KPIConfigurationPanelElement();
        windowTimePeriod.setDefaultValue(defaultValue);
        metaData.setRefreshTime(windowTimePeriod);
        context.checking(new Expectations() {
            {
                allowing(metaReader).getKPIConfigurationPanelMetaData();
                will(returnValue(metaData));
            }
        });
    }

    @Test
    public void testgetDefaultValueForRefreshTimeInDisplayFormat_4Minutes() {
        setUpRefreshTimeDefaultValue("4");
        assertThat(kpiConfigurationProperties.getDefaultValueForRefreshTimeInDisplayFormat(metaReader), is("4 minutes"));
    }

    @Test
    public void testgetDefaultValueForRefreshTimeInDisplayFormat_4Hours() {
        setUpRefreshTimeDefaultValue("240");
        assertThat(kpiConfigurationProperties.getDefaultValueForRefreshTimeInDisplayFormat(metaReader), is("4 hours"));
    }

    @Test
    public void testgetDefaultValueForRefreshTimeInDisplayFormat_4Days() {
        setUpRefreshTimeDefaultValue(Integer.toString(4 * 24 * 60));
        assertThat(kpiConfigurationProperties.getDefaultValueForRefreshTimeInDisplayFormat(metaReader), is("4 days"));
    }

    @Test
    public void testgetDefaultValueForRefreshTimeInDisplayFormat_4Weeks() {
        setUpRefreshTimeDefaultValue(Integer.toString(4 * 7 * 24 * 60));
        assertThat(kpiConfigurationProperties.getDefaultValueForRefreshTimeInDisplayFormat(metaReader), is("4 weeks"));
    }

    @Test
    public void testgetDefaultValueForRefreshRateInDisplayFormat_1Minute() {
        setUpRefreshRateDefaultValue(1);
        assertThat(kpiConfigurationProperties.getDefaultValueForRefreshRateInDisplayFormat(metaReader), is("1 minute"));
    }

    @Test
    public void testgetDefaultValueForRefreshRateInDisplayFormat_5Minutes() {
        setUpRefreshRateDefaultValue(5);
        assertThat(kpiConfigurationProperties.getDefaultValueForRefreshRateInDisplayFormat(metaReader), is("5 minutes"));
    }

    @Test
    public void testgetDefaultValueForRefreshRateInDisplayFormat_1Hour() {
        setUpRefreshRateDefaultValue(60);
        assertThat(kpiConfigurationProperties.getDefaultValueForRefreshRateInDisplayFormat(metaReader), is("1 hour"));
    }

    @Test
    public void testgetDefaultValueForRefreshRateInDisplayFormat_23Hours() {
        setUpRefreshRateDefaultValue(60 * 23);
        assertThat(kpiConfigurationProperties.getDefaultValueForRefreshRateInDisplayFormat(metaReader), is("23 hours"));
    }

    @Test
    public void testgetDefaultValueForRefreshRateInDisplayFormat_1Day() {
        setUpRefreshRateDefaultValue(60 * 24);
        assertThat(kpiConfigurationProperties.getDefaultValueForRefreshRateInDisplayFormat(metaReader), is("1 day"));
    }

    @Test
    public void testgetDefaultValueForRefreshRateInDisplayFormat_6Days() {
        setUpRefreshRateDefaultValue(60 * 24 * 6);
        assertThat(kpiConfigurationProperties.getDefaultValueForRefreshRateInDisplayFormat(metaReader), is("6 days"));
    }

    @Test
    public void testgetDefaultValueForRefreshRateInDisplayFormat_1Week() {
        setUpRefreshRateDefaultValue(60 * 24 * 7);
        assertThat(kpiConfigurationProperties.getDefaultValueForRefreshRateInDisplayFormat(metaReader), is("1 week"));
    }

    @Test
    public void testConvertingPropertiesToRefreshRate_NullPropertiesState_ReadDefaultValuesFromMetaData() {
        final int refreshRateDefaultValue = 5;
        setUpRefreshRateDefaultValue(refreshRateDefaultValue);
        assertThat(kpiConfigurationProperties.getRefreshRateParameterInMinutes(null, metaReader),
                is(refreshRateDefaultValue));
    }

    private void setUpRefreshRateDefaultValue(final int refreshRateDefaultValue) {
        final KPIConfigurationPanelDataType metaData = new KPIConfigurationPanelDataType();
        final KPIConfigurationPanelElement refreshRateDetails = new KPIConfigurationPanelElement();
        refreshRateDetails.setDefaultValue(Integer.toString(refreshRateDefaultValue));
        metaData.setRefreshRate(refreshRateDetails);
        context.checking(new Expectations() {
            {
                allowing(metaReader).getKPIConfigurationPanelMetaData();
                will(returnValue(metaData));
            }
        });

    }

    @Test
    public void testConvertingPropertiesToRefreshRate_EmptyPropertiesState_ReadDefaultValuesFromMetaData() {
        final IPropertiesState propertiesState = context.mock(IPropertiesState.class);
        final Map<String, String> propertiesMap = new HashMap<String, String>();
        expectCallOnPropertiesState(propertiesState, propertiesMap);
        final int refreshRateDefaultValue = 6;
        setUpRefreshRateDefaultValue(refreshRateDefaultValue);
        assertThat(kpiConfigurationProperties.getRefreshRateParameterInMinutes(propertiesState, metaReader),
                is(refreshRateDefaultValue));
    }

    @Test
    public void testConvertingPropertiesToRefreshRate_5minutes() {
        testConvertingPropertiesToRefreshRate("5 minutes", 5);
    }

    @Test
    public void testConvertingPropertiesToRefreshRate_1hour() {
        testConvertingPropertiesToRefreshRate("1 hour", 60);
    }

    @Test
    public void testConvertingPropertiesToRefreshRate_12hours() {
        testConvertingPropertiesToRefreshRate("12 hours", 60 * 12);
    }

    @Test
    public void testConvertingPropertiesToRefreshRate_1day() {
        testConvertingPropertiesToRefreshRate("1 day", 60 * 24);
    }

    @Test
    public void testConvertingPropertiesToRefreshRate_1week() {
        testConvertingPropertiesToRefreshRate("1 week", 60 * 24 * 7);
    }

    private void testConvertingPropertiesToRefreshRate(final String displayString, final int expectedValue) {
        setUpRefreshRateDefaultValue(0);
        final IPropertiesState propertiesState = context.mock(IPropertiesState.class);
        final Map<String, String> propertiesMap = new HashMap<String, String>();
        propertiesMap.put(KPIConfigurationConstants.REFRESH_RATE, displayString);
        expectCallOnPropertiesState(propertiesState, propertiesMap);
        assertThat(kpiConfigurationProperties.getRefreshRateParameterInMinutes(propertiesState, metaReader),
                is(expectedValue));

    }

    @Test
    public void testConvertingPropertiesToTimeQueryParameter_NullPropertiesState_ReadDefaultValuesFromMetaData() {
        final String timeQueryDefaultValue = "5";
        setUpRefreshTimeDefaultValue(timeQueryDefaultValue);
        assertThat(kpiConfigurationProperties.convertStoredPropertiesToTimeQueryParameter(null, metaReader),
                is(timeQueryDefaultValue));
    }

    @Test
    public void testConvertingPropertiesToTimeQueryParameter_EmptyPropertiesState_ReadDefaultValuesFromMetaData() {
        final IPropertiesState propertiesState = context.mock(IPropertiesState.class);
        final Map<String, String> propertiesMap = new HashMap<String, String>();
        expectCallOnPropertiesState(propertiesState, propertiesMap);
        final String timeQueryDefaultValue = "6";
        setUpRefreshTimeDefaultValue(timeQueryDefaultValue);
        assertThat(kpiConfigurationProperties.convertStoredPropertiesToTimeQueryParameter(propertiesState, metaReader),
                is(timeQueryDefaultValue));
    }

    @Test
    public void testConvertingPropertiesToTimeQueryParameter_4minutes() {
        testConvertingPropertiesToTimeQueryParameter("4 minutes", "4");
    }

    @Test
    public void testConvertingPropertiesToTimeQueryParameter_4hours() {
        testConvertingPropertiesToTimeQueryParameter("4 hours", Integer.toString(4 * 60));
    }

    @Test
    public void testConvertingPropertiesToTimeQueryParameter_4days() {
        testConvertingPropertiesToTimeQueryParameter("4 days", Integer.toString(4 * 60 * 24));
    }

    @Test
    public void testConvertingPropertiesToTimeQueryParameter_4weeks() {
        testConvertingPropertiesToTimeQueryParameter("4 weeks", Integer.toString(4 * 60 * 24 * 7));
    }

    private void testConvertingPropertiesToTimeQueryParameter(final String displayValue, final String expectedValue) {
        setUpRefreshTimeDefaultValue("0");
        final IPropertiesState propertiesState = context.mock(IPropertiesState.class);
        final Map<String, String> propertiesMap = new HashMap<String, String>();
        propertiesMap.put(KPIConfigurationConstants.REFRESH_TIME, displayValue);

        expectCallOnPropertiesState(propertiesState, propertiesMap);

        assertThat(kpiConfigurationProperties.convertStoredPropertiesToTimeQueryParameter(propertiesState, metaReader),
                is(expectedValue));
    }

    private void expectCallOnPropertiesState(final IPropertiesState propertiesState,
            final Map<String, String> propertiesMap) {
        context.checking(new Expectations() {
            {
                one(propertiesState).getProperties();
                will(returnValue(propertiesMap));
            }
        });

    }

}
