/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2013 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import com.ericsson.eniq.events.ui.client.workspace.launchwindowconfig.datatype.ConfigLaunchType;

import java.util.ArrayList;

/**
 * @author eeikbe
 * @since 06/2013
 */
public class ExtraURLDataType {
    
    private static final String CALL_SETUP_FAILURE = "Call Setup Failure";
    private static final String CALL_DROP = "Call Drop";
    private static final String EVENTID_CSF = "456";

    public static boolean isExtra(String extraURLType) {
        return !extraURLType.equals("");
    }

    public enum ExtraURLType{
        CONFIG("CONFIG");
        private String type;
        
        private ExtraURLType(String type){
            this.type = type;
        } 
        
        public static ExtraURLType fromString(String type){
            for(ExtraURLType extraURLType: ExtraURLType.values()){
                if(extraURLType.toString().equals(type)){
                    return extraURLType;
                }
            }
            //default to CONFIG.
            return CONFIG;
        }


        @Override
        public String toString() {
            return type;
        }
    }

    private static String extractEventId(String extraURLParams){
        String[] params = extraURLParams.split("&");
        String[] category = params[0].split("=");
        if(category[1].equals(EVENTID_CSF)){
            return CALL_SETUP_FAILURE;
        }
        return CALL_DROP;
    }

    private static String extractCategoryId(String extraURLParams){
        String[] params = extraURLParams.split("&");
        String[] category = params[1].split("=");
        return ConfigLaunchType.fromString(category[1]).getDisplayName();
    }

    public static ArrayList<String> extractExtraURLParams(ExtraURLType extraURLType, String extraURLParams){
        ArrayList<String> list = new ArrayList<String>();
        switch (extraURLType){
            case CONFIG:
                list.add(extractCategoryId(extraURLParams));
                list.add(extractEventId(extraURLParams));
            break;

            default:
                list.add("");
            break;
        }
        return list;
    }
}
