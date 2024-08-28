/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;
import static com.ericsson.eniq.events.common.client.CommonConstants.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * extend the GXT class JsonLoadResultReader to ensure Integer
 * and Date Values can be stored in a JSON object within single quotes
 * (String) and yet handled by the Grid as 
 * the Datatype specified by the Model 
 * 
 * @author eendmcm
 * @since May 2010
 */
public class JSONGridLoadResultReader<D> extends JsonLoadResultReader<D> { // NOPMD by eeicmsy on 21/05/10 18:35

    private final ModelType modelType;

    /**
     * @param modelType
     */
    public JSONGridLoadResultReader(final ModelType modelType) {
        super(modelType);
        this.modelType = modelType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public D read(Object loadConfig, Object data) { // NOPMD by eeicmsy on 21/05/10 18:35
        final JSONObject jsonRoot;
        if (data instanceof JavaScriptObject) {
            jsonRoot = new JSONObject((JavaScriptObject) data);
        } else {
            jsonRoot = (JSONObject) JSONUtils.parse((String) data);
        }

        JSONArray root = (JSONArray) jsonRoot.get(modelType.getRoot());

        int size = root.size();
        List<ModelData> models = new ArrayList<ModelData>(size);
        for (int i = 0; i < size; i++) {
            JSONObject obj = (JSONObject) root.get(i);
            models.add(retrieveModelData(obj));
        }

        int totalCount = getTotalCount(jsonRoot);
        if (totalCount < 0) {
            totalCount = models.size();
        }
        return (D) createReturnData(loadConfig, models, totalCount);
    }

    private ModelData retrieveModelData(final JSONObject obj) {
        ModelData model = newModelInstance();

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, fieldsNumber = modelType.getFieldCount(); i < fieldsNumber; i++) {
            DataField field = modelType.getField(i);
            String name = field.getName();
            Class type = field.getType();
            String map = field.getMap();
            if (map == null) {
                map = name;
            }

            JSONValue value = obj.get(map);
            if (value == null || value.isArray() != null) {
                continue;
            }

            final JSONBoolean boolVal = value.isBoolean();
            if (boolVal != null) {
                model.set(name, boolVal.booleanValue());
            } else {
                final JSONNumber numVal = value.isNumber();
                if (numVal != null) {
                    final Double d = numVal.doubleValue();
                    if (type == null) {
                        model.set(name, d); // takes as double
                    } else {
                        if (Long.class == type) {
                            model.set(name, d.longValue());
                        } else if (Integer.class == type) {
                            model.set(name, d.intValue());
                        } else if (Float.class == type) {
                            model.set(name, d.floatValue());
                        } else {
                            model.set(name, d);
                        }
                    }
                } else if (value.isObject() == null) {
                    final JSONString strVal = value.isString();
                    if (strVal != null) {
                        final String s = strVal.stringValue();
                        if (type == null) { // type == null
                            model.set(name, s);
                        } else { // type != null
                            if (Long.class == type) {
                                Long longVal = s.length() == 0 ? null : Long.valueOf(s);
                                model.set(name, longVal);
                            } else if (Integer.class == type) {
                                Integer intVal = s.length() == 0 ? null : Integer.valueOf(s);
                                model.set(name, intVal);
                            } else if (Float.class == type) {
                                Float flVal = s.length() == 0 ? null : Float.valueOf(s);
                                model.set(name, flVal);
                            } else if (Date.class == type) {
                                final String fFormat = field.getFormat();
                                if ("timestamp".equals(fFormat)) {
                                    Date d = new Date(Long.parseLong(s) * 1000); // NOPMD by eeicmsy on 21/05/10 18:35
                                    model.set(name, d);
                                } else {
                                    DateTimeFormat format = DateTimeFormat.getFormat(fFormat);
                                    Date d = format.parse(s);
                                    /* eendmcm - extended GXT version to account for specific output date format*/
                                    model.set(name, DateTimeFormat.getFormat(Displayed_Date_Format).format(d));
                                }
                                /* eendmcm - extended GXT version to account for Int and Long*/
                            }
                        }
                    } else if (value.isNull() != null) {
                        model.set(name, null);
                    }
                }
            }
        }
        return model;
    }
}
