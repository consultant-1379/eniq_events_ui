/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.buttonenabling;

import com.ericsson.eniq.events.ui.client.datatype.ButtonEnableParametersDataType;

/**
 * Subscriber details button is the "man" icon on the Subscriber OverView charts/grids
 * in Subscriber tab. 
 * 
 * It should be enabled when the window owns an IMSI but should be disabled for groups.
 * It should be initiailiy disabled until window has search data
 * (obviously if button is not present on toolbar - then not an issue).
 * @author eeicmsy
 * @since Nov 2010
 */
public class SubscriberDetailsButtonEnableConditions implements IButtonEnableConditions {

    @Override
    public boolean shouldEnableButton(final ButtonEnableParametersDataType params) {

        if (params.searchData == null || params.searchData.isEmpty()) {
            return false;
        }

        if (params.searchData.isGroupMode()) {
            return false;
        }
        /*  future proof if say only certain types in tabs should enable button
         *  as it happens both PTMSI and IMSI should enable button so this is comment out
         */
        //        final String[] urlParams = params.searchData.urlParams;
        //        for (final String urlParam : urlParams){
        //            if (urlParam != null &&  urlParam.startsWith(SEARCH_FIELD_IMSI_PARAM)){
        //                return true;
        //            }
        //        }
        return true;
    }
}
