/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.buttonenabling;

import com.ericsson.eniq.events.ui.client.datatype.ButtonEnableParametersDataType;

/**
 * Concrete implementation of enabling conditions suitable for generic buttons on window toolbar.
 * 
 * @author eeicmsy
 * @since Nov 2010
 */
public class GenericButtonEnableConditions implements IButtonEnableConditions {

    @Override
    public boolean shouldEnableButton(final ButtonEnableParametersDataType params) { 
        return params.rowCount > 0;
    }

}
