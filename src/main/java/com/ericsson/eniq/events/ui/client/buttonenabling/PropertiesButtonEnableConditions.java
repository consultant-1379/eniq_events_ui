/********************************************************************************
* COPYRIGHT Ericsson 2014
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
******************************************************************************* */


package com.ericsson.eniq.events.ui.client.buttonenabling;

import com.ericsson.eniq.events.ui.client.datatype.ButtonEnableParametersDataType;

/**
 * Concrete implementation of enabling conditions suitable for property buttons on window toolbar.
 *
 * @author eeikbe
 * @since Jan 2014
 */
public class PropertiesButtonEnableConditions implements IButtonEnableConditions {

    @Override
    public boolean shouldEnableButton(final ButtonEnableParametersDataType params) {
        return params.isRowSelected;
    }

}
