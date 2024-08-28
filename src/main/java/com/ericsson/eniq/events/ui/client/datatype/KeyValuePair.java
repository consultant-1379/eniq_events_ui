/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import com.extjs.gxt.ui.client.data.BaseModel;

/** @author esuslyn */
public class KeyValuePair extends BaseModel {

   public KeyValuePair() {
   }

   public KeyValuePair(final String key, final String value) {
      set("1", key.replaceAll("\"", ""));
      set("2", value.replaceAll("\"", ""));
   }

   public String getKey() {
      return get("1");
   }

   public String getValue() {
      return get("2");
   }

   public void setKey(final String key) {
      set("1", key.replaceAll("\"", ""));
   }

   public void setValue(final String value) {
      set("2", value.replaceAll("\"", ""));
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder(64);
      sb.append("KeyValuePair");
      sb.append("{key='").append(getKey()).append('\'');
      sb.append(", value='").append(getValue()).append('\'');
      sb.append('}');
      return sb.toString();
   }
}
