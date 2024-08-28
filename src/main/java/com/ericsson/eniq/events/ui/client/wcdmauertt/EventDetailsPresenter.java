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

import com.ericsson.eniq.events.common.client.export.CSVBuilder;
import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.EventBus;
import java.util.*;

public class EventDetailsPresenter extends BasePresenter<EventDetailsView>{
    private static final String CONS_SUBSCRIBER = "Subscriber Information";
    private static final String CONS_IMSI = "IMSI";
    private static final String CONS_IMEISV = "IMEISV";
    private static final String CONS_EVENT = "Event Information";
    private static final String CONS_TIMESTAMP = "TimeStamp";
    private static final String CONS_EVENTID = "Message Id";
    private static final String CONS_PROTOCOL = "Protocol Id";
    private static final String CONS_DIRECTION = "Message-Direction";
    private static final String CONS_UECONTEXT = "UE-Context Id";
    private static final String CONS_MODULE = "Module Id";
    private static final String CONS_PDU = "Pdu Type";
    private static final String CONS_CELL = "CELL/RNC Information";
    private static final String CONS_MISC = "Miscellaneous Information";
    private static final String CONS_MESSAGE_LENGTH = "Message-Length";
    private static final String CONS_MESSAGE_CONTENT = "Message-Content";
    private static final String TIMESTAMP = "TIMESTAMP";
    private static final String DIRECTION = "DIRECTION";
    private static final String EVENT_ID = "EVENT_NAME";
    private static final String PROTOCOL_ID = "PROTOCOL_NAME";
    private static final String UERTT_TAG = "WCDMA-UERTT";
    private static final String IMSI = "IMSI";
    private static final String SCANNER_ID = "SCANNER_ID";
    private static final String UE_CONTEXT = "UE_CONTEXT";
    private static final String RNC_MODULE_ID = "RNC_MODULE_ID";
    private static final String MESSAGE_LENGTH = "MESSAGE_LENGTH";
    private static final String MESSAGE_CONTENTS = "MESSAGE_CONTENTS";
    private static final String PDU_TYPE = "PDU_TYPE";
    ArrayList<String> lstCellIdToRetrieveData = new ArrayList<String>();
    ArrayList<String> lstRncIdToRetrieveData = new ArrayList<String>();
    ArrayList<String> lstCellIdToDisplayData = new ArrayList<String>();
    ArrayList<String> lstRncIdToDisplayData = new ArrayList<String>();
    private static final String DATA_TAG = "data";
    private JSONValue responseValue;
    List<CollapsibleSection> sections = new ArrayList<CollapsibleSection>();
    public EventDetailsPresenter(EventDetailsView view, EventBus eventBus, JSONValue responseValue) {
        super(view, eventBus);
        this.responseValue = responseValue;
    }

    private String getSpecifiedTagFromJson(final int jsonIndex, final String field) {
        final String data = responseValue.isObject().get(DATA_TAG).isArray().get(jsonIndex).isObject().get(field).toString();
        return removeDoubleQuotes(data);
    }
    protected String removeDoubleQuotes(final String string) {
        return string.substring(1, string.length() - 1);
    }
    public List<CollapsibleSection> go(int index,EventCacheUertt eventCacheUertt){
        initialiseListCellIdRncId();
        CollapsibleSectionState subscriberInfo = new CollapsibleSectionState();
        subscriberInfo.setId(CONS_SUBSCRIBER);
        subscriberInfo.setCollapsed(false);
        Map<String,String> data1 = new HashMap<String, String>();
        data1.put(CONS_IMSI,getData(index,eventCacheUertt).getImsi());
        CollapsibleSection section = new CollapsibleSection(subscriberInfo,data1);
        sections.add(section);
        CollapsibleSectionState eventInfo = new CollapsibleSectionState();
        eventInfo.setId(CONS_EVENT);
        eventInfo.setCollapsed(false);
        Map<String,String> data2 = new HashMap<String, String>();
        data2.put(CONS_TIMESTAMP,getData(index,eventCacheUertt).getTimestamp());
        data2.put(CONS_EVENTID,getData(index,eventCacheUertt).getEvent_id());
        data2.put(CONS_PROTOCOL,getData(index,eventCacheUertt).getProtocol_id());
        data2.put(CONS_UECONTEXT,getData(index,eventCacheUertt).getUe_context_id());
        data2.put(CONS_MODULE,getData(index,eventCacheUertt).getRnc_module_id());
        data2.put(CONS_PDU,getData(index,eventCacheUertt).getPdu_type());
        CollapsibleSection section2 = new CollapsibleSection(eventInfo,data2);
        sections.add(section2);
        CollapsibleSectionState cellInfo = new CollapsibleSectionState();
        cellInfo.setId(CONS_CELL);
        cellInfo.setCollapsed(false);
        LinkedHashMap<String,String> data3 = new LinkedHashMap<String, String>();
        for (int i = 0 ; i < 4; i++)
        {
            data3.put(lstCellIdToDisplayData.get(i),getData(index,eventCacheUertt).getLstCellId(i));
        }

        for (int i = 0 ; i < 4; i++)
        {
            data3.put(lstRncIdToDisplayData.get(i),getData(index,eventCacheUertt).getLstRncId(i));
        }

        CollapsibleSection section3 = new CollapsibleSection(cellInfo,data3);
        sections.add(section3);
        CollapsibleSectionState miscInfo = new CollapsibleSectionState();
        miscInfo.setId(CONS_MISC);
        miscInfo.setCollapsed(false);
        Map<String,String> data4 = new HashMap<String, String>();
        data4.put(CONS_MESSAGE_LENGTH,intToString(getData(index,eventCacheUertt).getEncoded_message().length()));
        data4.put(CONS_MESSAGE_CONTENT,getData(index,eventCacheUertt).getEncoded_message());
        CollapsibleSection section4 = new CollapsibleSection(miscInfo,data4);
        sections.add(section4);
        return sections;
    }

    private void initialiseListCellIdRncId()
    {
        for(int i = 0; i < 4; i++)
        {
            int index = i+1;
            lstCellIdToDisplayData.add("CELL-ID-" +index);
            lstRncIdToDisplayData.add("RNC-ID-" +index);
        }

        for(int i = 0; i < 4; i++)
        {
            int index = i+1;
            lstCellIdToRetrieveData.add("C_ID_" +index);
            lstRncIdToRetrieveData.add("RNC_ID_"+index);
        }
    }
    private String intToString(int i){
        return Integer.toString(i);
    }
    private EventPojo getData(int jsonIndex,EventCacheUertt eventCacheUertt){

        ArrayList<String> listCellData = new ArrayList<String>();
        ArrayList<String> listRncData = new ArrayList<String>();
        final String protocolId = formatProtocolId(getSpecifiedTagFromJson(jsonIndex, PROTOCOL_ID));
        final String eventId = protocolId + " " + formatEventId(getSpecifiedTagFromJson(jsonIndex, EVENT_ID));
        final String direction = getSpecifiedTagFromJson(jsonIndex, DIRECTION);
        final String timestamp = getSpecifiedTagFromJson(jsonIndex, TIMESTAMP);
        final String scannerId = getSpecifiedTagFromJson(jsonIndex, SCANNER_ID);
        final String ueContextId = getSpecifiedTagFromJson(jsonIndex, UE_CONTEXT);
        final String rncModuleId = getSpecifiedTagFromJson(jsonIndex, RNC_MODULE_ID);
        for(int i=0;i<4;i++){
            listCellData.add(getSpecifiedTagFromJson(jsonIndex, lstCellIdToRetrieveData.get(i)));
        }
        for(int i=0;i<4;i++){
            listRncData.add(getSpecifiedTagFromJson(jsonIndex, lstRncIdToRetrieveData.get(i)));
        }
        final String pdu_type = getSpecifiedTagFromJson(jsonIndex, PDU_TYPE);
        final String encodedMessage = getSpecifiedTagFromJson(jsonIndex, MESSAGE_CONTENTS);
        final String imsi = getSpecifiedTagFromJson(jsonIndex, IMSI);
        return new EventPojo(scannerId,timestamp,eventId,ueContextId,rncModuleId,listCellData,listRncData,pdu_type,protocolId,direction,encodedMessage,imsi);
    }

    protected String formatProtocolId(String protocol_Id)
    {
        String[] subStringProtocol = protocol_Id.split("_", 3);
        return subStringProtocol[2];
    }

    protected String formatEventId(String event_Id)
    {
        String eventNameModified = "";
        final String[] subStringeventId = event_Id.split("_", 4);
        eventNameModified += subStringeventId[3].charAt(0);
        for (int i = 1; i < subStringeventId[3].length(); i++) {
            if (subStringeventId[3].charAt(i) == '_') {
                eventNameModified += " " + subStringeventId[3].charAt(i + 1);
                i++;
            } else {
                eventNameModified += subStringeventId[3].charAt(i);
            }
        }
        return eventNameModified;
    }

    public CSVBuilder getDataAsCSV() {
        CSVBuilder csvBuilder = new CSVBuilder();
        csvBuilder.addColumn(UERTT_TAG);
        csvBuilder.nextLine();
        for (CollapsibleSection cp : sections) {
            csvBuilder.addColumn(cp.getSectionState().getId());
            csvBuilder.nextLine();
            for (Map.Entry<String,String> entry:cp.getDetails().entrySet())
            {
                csvBuilder.addBlankColumn();
                csvBuilder.addColumn(entry.getKey());
                csvBuilder.addBlankColumn();
                csvBuilder.addColumn(entry.getValue());
                csvBuilder.nextLine();
            }
        }
        return csvBuilder;
    }
}
