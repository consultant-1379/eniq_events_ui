/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.buttonenabling;

import com.ericsson.eniq.events.ui.client.datatype.ButtonEnableParametersDataType;

/**
 * Implementing class is the provider of conditions that are appropriate 
 * for the button. All buttons being checked use the same input argument
 * 
 * Splitting logic for what each button care about when setting enable/disable status
 * into concrete implementations of this interface
 * 
 * @author eeicmsy
 * @since November 2010
 * 
 * @see {@link com.ericsson.eniq.events.ui.client.buttonenabling.KPIButtonEnableConditions}
 * @see {@link com.ericsson.eniq.events.ui.client.buttonenabling.SACButtonEnableConditions}  
 *
 */
public interface IButtonEnableConditions {

    /**
     * Utility to check if a toolbar button should be enabled
     * based on the inputed parameters (some of which the concrete implementation may not use
     * in determining if the button should be enabled) 
     * 
     * @param params datatype hold all parameters of interest that a subclass might 
     *               use to determine if a particular button should be enabled or not.
     *               Except that the dataType is complete enough for the implementing class to work
     * @return       true if the button should be enabled based on the inputted params 
     */
    boolean shouldEnableButton(ButtonEnableParametersDataType params);

}
