package com.ericsson.eniq.events.ui.client.kpi;

import java.util.HashMap;
import java.util.Map;

import com.ericsson.eniq.events.common.client.json.IJSONArray;
import com.ericsson.eniq.events.common.client.json.IJSONObject;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog.DialogType;
import com.ericsson.eniq.events.ui.client.datatype.KpiPanelDataType;
import com.google.gwt.json.client.JSONValue;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

public abstract class KpiPanelUtils {

 //  private static final String SERVER_ERROR = "KPI Server Error";

 //  private static final String SERVER_RESPONSE_CORRUPT = "KPI error";

   private static int totalBreaches = 0;
   
   private static boolean flag = false;

   private static final String[] severities = {"critical", "major", "minor", "warning"};

   public static Map<String, KpiPanelDataType> getKpiPanelDataType(final JSONValue jsonVal) {

       final Map<String, KpiPanelDataType> result = getSeverityMap();
       
        final JsonObjectWrapper object = new JsonObjectWrapper(jsonVal.isObject());

      final IJSONArray searchTypes = object.getArray("data");

      int total = 0;

      if (searchTypes != null) {
         for (int i = 0; i < searchTypes.size(); i++) {
            final IJSONObject parent = searchTypes.get(i);
            if (parent != null) {
               final String id = parent.getString("1");
               if (id != null) {
                  final String stringBreches = parent.getString("2");
                  if (stringBreches != null && !stringBreches.equals("")) {
                     final int breaches = Integer.parseInt(stringBreches);
                     final KpiPanelDataType kpiPanelDataType = new KpiPanelDataType(id, breaches);
                     total += breaches;
                     result.put(id, kpiPanelDataType);
                  }
               }
            }
         }
      }

      totalBreaches = total;

      return result;
   }

   private static Map<String, KpiPanelDataType> getSeverityMap() {
      final Map<String, KpiPanelDataType> result = new HashMap<String, KpiPanelDataType>();

      for (final String severity : severities) {
         result.put(severity, new KpiPanelDataType(severity, 0));
      }

      return result;
   }

   /**
    * Parse JSON String
    *
    * @param json
    *
    * @return
    */
   public static JSONValue parseJsonString(final String json) {
      JSONValue responseValue = null;      
      try {
         responseValue = JSONUtils.parse(json);
         if (responseValue == null) {
             // flag to display the message box only once in a user's session
             if (!flag) {
                 flag = true;
                 showParseFailureDialog();
             }
         }
      } catch (final Exception e) {
          if (!flag) {
              flag = true;
              showParseFailureDialog();
          }
      }
      return responseValue;
   }

   public static void showParseFailureDialog() {
      final MessageDialog messageDialog = new MessageDialog();
      messageDialog.setGlassEnabled(true);
      messageDialog.show(SERVER_ERROR, SERVER_CORRUPT_RESPONSE, DialogType.ERROR);
   }

   public static int getTotalBreaches() {
      return totalBreaches;
   }
}
