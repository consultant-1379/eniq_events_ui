/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.kpi;

import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.language.Grammar;
import com.ericsson.eniq.events.common.client.datatype.IPropertiesState;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.kpi.KPIConfigurationPanelDataType;
import com.ericsson.eniq.events.ui.client.datatype.kpi.KPIConfigurationPanelElement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author eemecoy
 *
 */
public class KPIConfigurationProperties {

    private static final int INVALID_VALUE = -1;

    private static final int NUMBER_MINUTES_IN_HOUR = 60;

    private static final int NUMBER_HOURS_IN_DAY = 24;

    private static final int NUMBER_MINUTES_IN_DAY = NUMBER_MINUTES_IN_HOUR * NUMBER_HOURS_IN_DAY;

    private static final int NUMBER_DAYS_IN_WEEK = 7;

    private static final int NUMBER_MINUTES_IN_WEEK = NUMBER_MINUTES_IN_DAY * NUMBER_DAYS_IN_WEEK;

    public int getRefreshRateParameterInMinutes(final IPropertiesState propertiesState, final IMetaReader metaReader) {
        final String defaultValue = getDefaultValueForRefreshRate(metaReader);
        return convertStoredProperties(propertiesState, KPIConfigurationConstants.REFRESH_RATE, defaultValue);
    }

    /**
     * Will return the default value defined in the meta data for the refresh rate
     * Will be in the format of : "60"
     * Unit of time is minutes
     * 
     * 
     * @param metaReader                for reading meta data file
     * @return see above
     */
    public String getDefaultValueForRefreshRate(final IMetaReader metaReader) {
        final KPIConfigurationPanelDataType metaData = metaReader.getKPIConfigurationPanelMetaData();
        final KPIConfigurationPanelElement refreshRateMetaData = metaData.getRefreshRate();
        return refreshRateMetaData.getDefaultValue();
    }

    /**
     * Will return the default value defined in the meta data for the refresh rate
     * Will be in the format of : "1 hour" or "5 minutes"     
     * 
     * 
     * @param metaReader                for reading meta data file
     * @return see above
     */
    public String getDefaultValueForRefreshRateInDisplayFormat(final IMetaReader metaReader) {
        final String defaultValueInMinutesAsString = getDefaultValueForRefreshRate(metaReader);
        return convertToDisplayValue(defaultValueInMinutesAsString);
    }

    private String convertToDisplayValue(final String valueInMinutesAsString) {
        final int valueAsInteger = Integer.parseInt(valueInMinutesAsString);
        return convertToDisplayValue(valueAsInteger);

    }

    private String convertToDisplayValue(final int valueInMinutes) {
        final Grammar grammar = new Grammar();
        if (shouldValueBeDisplayedAsHours(valueInMinutes)) {
            final int numberHours = valueInMinutes / NUMBER_MINUTES_IN_HOUR;
            return numberHours + " hour" + grammar.getNounEnding(numberHours);
        }
        if (shouldValueBeDisplayedAsDays(valueInMinutes)) {
            final int numberDays = valueInMinutes / NUMBER_MINUTES_IN_DAY;
            return numberDays + " day" + grammar.getNounEnding(numberDays);
        }
        if (shoudlValueBeDisplayedAsWeeks(valueInMinutes)) {
            final int numberWeeks = valueInMinutes / NUMBER_MINUTES_IN_WEEK;
            return numberWeeks + " week" + grammar.getNounEnding(numberWeeks);
        }
        return valueInMinutes + " minute" + grammar.getNounEnding(valueInMinutes);
    }

    private boolean shoudlValueBeDisplayedAsWeeks(final int defaultValueInMinutes) {
        return defaultValueInMinutes >= NUMBER_MINUTES_IN_WEEK;
    }

    private boolean shouldValueBeDisplayedAsDays(final int defaultValueInMinutes) {
        return defaultValueInMinutes >= NUMBER_MINUTES_IN_DAY && defaultValueInMinutes < NUMBER_MINUTES_IN_WEEK;
    }

    private boolean shouldValueBeDisplayedAsHours(final int defaultValueInMinutes) {
        return defaultValueInMinutes >= NUMBER_MINUTES_IN_HOUR && defaultValueInMinutes < NUMBER_MINUTES_IN_DAY;
    }

    /**
     * Get the user's configured value for the number of minutes to query for data in kpi panel
     * @param propertiesState 
     * @param metaReader 
     * 
     * @return                          number of minutes of data to query for
     */
    public String convertStoredPropertiesToTimeQueryParameter(final IPropertiesState propertiesState,
            final IMetaReader metaReader) {
        final String defaultValue = getDefaultValueForRefreshTime(metaReader);
        final int timeQueryParameter = convertStoredProperties(propertiesState, KPIConfigurationConstants.REFRESH_TIME,
                defaultValue);
        return Integer.toString(timeQueryParameter);

    }

    public String getDefaultValueForRefreshTime(final IMetaReader metaReader) {
        final KPIConfigurationPanelDataType metadata = metaReader.getKPIConfigurationPanelMetaData();
        return metadata.getRefreshTime().getDefaultValue();
    }

    private int convertStoredProperties(final IPropertiesState propertiesState, final String key,
            final String defaultValue) {
        if (propertiesState != null) {
            final Map<String, String> properties = propertiesState.getProperties();
            final int result = checkPropertiesAndConvert(properties, key);
            if (result != INVALID_VALUE) {
                return result;
            }
        }
        return Integer.parseInt(defaultValue);
    }

    private int checkPropertiesAndConvert(final Map<String, String> properties, final String key) {
        if (propertyExistsAndHasValidValue(properties, key)) {
            final String value = properties.get(key);
            return convertConfiguredPropertiesToTimeUnit(value);
        }
        return INVALID_VALUE;
    }

    private boolean propertyExistsAndHasValidValue(final Map<String, String> properties, final String key) {
        return properties.containsKey(key) && hasValidValue(properties.get(key));
    }

    boolean hasValidValue(final String value) {
        return value != null && value.length() > 0;
    }

    private int convertConfiguredPropertiesToTimeUnit(final String valueAsDisplayed) {
        final String strippedStoredParameter = valueAsDisplayed.substring(0, valueAsDisplayed.indexOf(" "));
        final int selectedValue = Integer.parseInt(strippedStoredParameter);
        if (valueInHours(valueAsDisplayed)) {
            return selectedValue * NUMBER_MINUTES_IN_HOUR;
        }
        if (valueInDays(valueAsDisplayed)) {
            return selectedValue * NUMBER_MINUTES_IN_DAY;
        }
        if (valueInWeeks(valueAsDisplayed)) {
            return selectedValue * NUMBER_MINUTES_IN_WEEK;
        }
        return selectedValue;
    }

    private boolean valueInWeeks(final String valueAsDisplayed) {
        if (valueAsDisplayed.contains("week")) {
            return true;
        }
        return false;
    }

    private boolean valueInDays(final String valueAsDisplayed) {
        if (valueAsDisplayed.contains("day")) {
            return true;
        }
        return false;
    }

    private boolean valueInHours(final String valueAsDisplayed) {
        if (valueAsDisplayed.contains("hour")) {
            return true;
        }
        return false;
    }

    public String getDefaultValueForRefreshTimeInDisplayFormat(final IMetaReader metaReader) {
        final String defaultValueForRefreshTimeInMinutes = getDefaultValueForRefreshTime(metaReader);
        return convertToDisplayValue(defaultValueForRefreshTimeInMinutes);
    }

    public Map<String, String> validateSelection(final IMetaReader metaReader,
            final Map<String, TimeInfoDataType> userTimeDetails) {
        final Map<String, String> validationErrors = new HashMap<String, String>();
        final KPIConfigurationPanelDataType metaData = metaReader.getKPIConfigurationPanelMetaData();
        if (userTimeDetails.containsKey(KPIConfigurationConstants.REFRESH_RATE)) {
            validateSelection(validationErrors, userTimeDetails, KPIConfigurationConstants.REFRESH_RATE,
                    metaData.getRefreshRate());
        }
        if (userTimeDetails.containsKey(KPIConfigurationConstants.REFRESH_TIME)) {
            validateSelection(validationErrors, userTimeDetails, KPIConfigurationConstants.REFRESH_TIME,
                    metaData.getRefreshTime());
        }

        return validationErrors;
    }

    private void validateSelection(final Map<String, String> validationErrors,
            final Map<String, TimeInfoDataType> userTimeDetails, final String key,
            final KPIConfigurationPanelElement metaData) {
        final TimeInfoDataType timeInfoDataType = userTimeDetails.get(key);
        final int selectionInMinutes = Integer.parseInt(timeInfoDataType.timeRange);
        final int minValue = metaData.getMinValue();
        final int maxValue = metaData.getMaxValue();
        if (selectionInMinutes < minValue || selectionInMinutes > maxValue) {
            validationErrors.put(key, "Value should be between " + convertToDisplayValue(minValue) + " and "
                    + convertToDisplayValue(maxValue));
        }

    }

    private String validate(final KPIConfigurationPanelElement metaData, final TimeInfoDataType userSelection) {
        final int selectionInMinutes = Integer.parseInt(userSelection.timeRange);
        final int minValue = metaData.getMinValue();
        final int maxValue = metaData.getMaxValue();
        if (selectionInMinutes < minValue || selectionInMinutes > maxValue) {
            return generateErrorMessage(minValue, maxValue);
        }
        return null;
    }

    private String generateErrorMessage(final int minValue, final int maxValue) {
        return "Value should be between " + convertToDisplayValue(minValue) + " and " + convertToDisplayValue(maxValue);
    }

    public String validateEntry(final IMetaReader metaReader, final String key, final TimeInfoDataType userSelection) {
        final KPIConfigurationPanelElement metaData = getMetaDataForKey(metaReader, key);
        return validate(metaData, userSelection);
    }

    private KPIConfigurationPanelElement getMetaDataForKey(final IMetaReader metaReader, final String key) {
        final KPIConfigurationPanelDataType parentMetaData = metaReader.getKPIConfigurationPanelMetaData();
        if (KPIConfigurationConstants.REFRESH_RATE.equals(key)) {
            return parentMetaData.getRefreshRate();
        }
        return parentMetaData.getRefreshTime();
    }

}
