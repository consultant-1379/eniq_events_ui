/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 ******************************************************************************* */
package com.ericsson.eniq.events.ui.client.wcdmauertt;

import com.google.gwt.json.client.JSONValue;

import java.util.ArrayList;

public class EventCacheUertt {
    private ArrayList<EventPojo> eventList = new ArrayList<EventPojo>();
    private final int pagingOffSet = 50;
    private JSONValue responseEventRawData;

    public ArrayList<EventPojo> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<EventPojo> eventList) {
        this.eventList = eventList;
    }

    public void addToEventList(EventPojo eventPojo) {
        eventList.add(eventPojo);
    }

    public void clearEventList() {
        this.eventList.clear();
    }

    public ArrayList<EventPojo> getSubSetElement(int pageIndex) {
        int startIndex = ((pageIndex - 1) * pagingOffSet);
        int endIndex = (pagingOffSet * pageIndex);
        if (eventList.size() > endIndex) {
            ArrayList<EventPojo> subArrayList = new ArrayList<EventPojo>(eventList.subList(startIndex, endIndex));
            return subArrayList;
        } else {
            ArrayList<EventPojo> subArrayList = new ArrayList<EventPojo>(eventList.subList(startIndex, eventList.size()));
            return subArrayList;
        }

    }

    public void setResponseEventRawData(JSONValue responseEventRawData) {
        this.responseEventRawData = responseEventRawData;
    }

    public JSONValue getResponseEventRawData() {
        return responseEventRawData;
    }
}
