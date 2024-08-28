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

import java.util.ArrayList;

public class EventPojo{
    final private static int Cell_Rnc_Count = 4;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private int millisecond;
    private int transport_channel_id;
    private String measurement_type;
    private String encoded_measurement;
    private String scanner_id;
    private String timestamp;
    private String event_id;
    private String ue_context_id;
    private String rnc_module_id;


    public String getLstCellId(int index) {
        return lstCellId.get(index);
    }

    public String getLstRncId(int index) {
        return lstRncId.get(index);
    }

    public void setLstCellId(ArrayList<String> lstCellIdRncId) {
        this.lstCellId = lstCellIdRncId;
    }

    
    private ArrayList<String> lstRncId;
    private ArrayList<String> lstCellId;
    private String pdu_type;
    private String protocol_id;
    private String direction;
    private String encoded_message;
    private int source_connection_properties;
    private int source_connection_properties_ext;
    private String imsi;
    private String imeisv;

    public EventPojo(String scanner_id, String timestamp, String event_id, String ue_context_id, String rnc_module_id, ArrayList<String> listCellData,ArrayList<String> listRncData,String pdu_type, String protocol_id, String direction, String encoded_message, String imsi) {
        this.scanner_id = scanner_id;
        this.timestamp = timestamp;
        this.event_id = event_id;
        this.ue_context_id = ue_context_id;
        this.rnc_module_id = rnc_module_id;
        this.lstCellId = lstCellId;
        this.pdu_type = pdu_type;
        this.protocol_id = protocol_id;
        this.direction = direction;
        this.encoded_message = encoded_message;
        this.imsi = imsi;
        lstCellId = listCellData;
        lstRncId = listRncData;
    }

    public EventPojo(String timestamp, String event_id, String protocol_id, String direction) {
        this.timestamp = timestamp;
        this.event_id = event_id;
        this.protocol_id = protocol_id;
        this.direction = direction;
    }


    public int getYear(){
        return year;
    }
    public void setYear(int y){
        year = y;
    }
    public int getMonth(){
        return month;
    }
    public void setMonth(int m){
        year = m;
    }
    public int getDay(){
        return day;
    }
    public void setDay(int d){
        day = d;
    }
    public int getHour(){
        return hour;
    }
    public void setHour(int h){
        hour = h;
    }
    public int getMinute(){
        return minute;
    }
    public void setMinute(int min){
        minute = min;
    }
    public int getSecond(){
        return second;
    }
    public void setSecond(int s){
        second = s;
    }
    public int getMillisecond(){
        return millisecond;
    }
    public void setMillisecond(int ms){
        millisecond = ms;
    }
    public String getRnc_module_id(){
        return rnc_module_id;
    }
    public void setRnc_module_id(String rid){
        rnc_module_id = rid;
    }
    public String getUe_context_id(){
        return ue_context_id;
    }
    public void setUe_context_id(String uid){
        ue_context_id = uid;
    }
    public String getScanner_id(){
        return scanner_id;
    }
    public void setScanner_id(String sid){
        scanner_id = sid;
    }
    public String getTimestamp(){
        return timestamp;
    }
    public void setTimestamp(String string){
        timestamp = string;
    }
    public String getEvent_id(){
        return event_id;
    }
    public void setEvent_id(String eid){
        event_id = eid;
    }
    public String getImsi(){
        return imsi;
    }
    public void setImsi(String i){
        imsi = i;
    }

    public String getProtocol_id() {
        return protocol_id;
    }
    public void setProtocol_id(String pid){
        protocol_id = pid;
    }
    public String getDirection() {
        return direction;
    }
    public void setDirection(String dir) {
        direction = dir;
    }
    public String getEncoded_message() {
        return encoded_message;
    }
    public void setEncoded_message(String emsg) {
        encoded_message = emsg;
    }
    public int getSource_connection_properties() {
        return source_connection_properties;
    }
    public void setSource_connection_properties(int scp) {
        source_connection_properties = scp;
    }
    public int getSource_connection_properties_ext() {
        return source_connection_properties_ext;
    }
    public void setSource_connection_properties_ext(int scpe) {
        source_connection_properties_ext = scpe;
    }
    public String getPdu_type() {
        return pdu_type;
    }
    public void setPdu_type(String pt) {
        pdu_type = pt;
    }
    public int getTransport_channel_id() {
        return transport_channel_id;
    }
    public void setTransport_channel_id(int tcid) {
        transport_channel_id = tcid;
    }
    public String getMeasurement_type() {
        return measurement_type;
    }
    public void setMeasurement_type(String mtype) {
        measurement_type = mtype;
    }
    public String getEncoded_measurement() {
        return encoded_measurement;
    }
    public void setEncoded_measurement(String em) {
        encoded_measurement = em;
    }
    public String getImeisv() {
        return imeisv;
    }

    public void setImeisv(String imeisv) {
        this.imeisv = imeisv;
    }
}

