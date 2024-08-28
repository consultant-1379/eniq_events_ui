/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

import com.ericsson.eniq.events.common.client.json.IJSONArray;
import com.ericsson.eniq.events.common.client.json.IJSONObject;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.ui.client.events.MetaDataReadyEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Adaptor class to support having possibility for more than one meta data.
 * (Intended to be directly swapped with existing use of JsonObjectWrapper inside
 * MetaReader class)
 * 
 * The modus operandi for this class is to search the current metadata first for
 * ids, etc, if nothing found (empty string) suffer the cost of searching though
 * cached metadatas (may still give empty string, etc).
 * 
 * i.e. Supporting case for having windows from both meta datas active at the
 * same time, e.g if have meta data for PS and Circuit Switched, can still be
 * drilling down on a window from Packet Switched when have Circuit Switched
 * menu items in place.
 * 
 * This also means if "master" meta data is always delivered - the "piggy back"
 * meta data does not have to carry all information already in master.
 * 
 * @author eeicmsy
 * @since April 2011
 */
public class MultiMetaDataHelper implements IMultiMetaDataHelper {

    private final static Logger LOGGER = Logger.getLogger(MultiMetaDataHelper.class.getName());

    private final EventBus eventBus;

    private JsonObjectWrapper metaData = null;

    private String currentMetaDataPath = null;

    /**
     * Map supporting multiple meta datas in force
     */
    private final Map<String, JsonObjectWrapper> metaDataMap = new HashMap<String, JsonObjectWrapper>();

    private boolean supportMultiple = false;

    @Inject
    public MultiMetaDataHelper(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Load a newly received meta data from response of a server call
     * 
     * @param metaDataPath
     *          - url relative path, e.g. METADATA/UI
     * @param receivedMetaData
     *          - Class containing JSON for setting up UI from meta data retrieved
     *          on server using the call to metaDataPath
     */
    @Override
    public void setMetaDataFromServer(final String metaDataPath, final JsonObjectWrapper receivedMetaData) {

        metaData = receivedMetaData; // setting default to use too
        currentMetaDataPath = metaDataPath;

        metaDataMap.put(metaDataPath, receivedMetaData); // should be called twice after two meta data retreived
        supportMultiple = metaDataMap.size() > 1;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.common.IMultiMetaDataHelper#resetMetaDataPath(java.lang.String)
     */
    @Override
    public boolean resetMetaDataPath(final String path) {
        if (!path.equals(currentMetaDataPath)) {

            final JsonObjectWrapper meta = metaDataMap.get(path);
            if (meta != null) {
                metaData = meta;
                currentMetaDataPath = path;

                LOGGER.info("Resetting to: " + path);
                eventBus.fireEvent(new MetaDataReadyEvent());
                return true;

            }
            LOGGER.info("Not yet loaded: " + path);

        } else {
            LOGGER.info("Not resetting meta data when its current path is: " + path);
        }

        return false;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.common.IMultiMetaDataHelper#hasLoaded(java.lang.String)
     */
    @Override
    public boolean hasLoaded(final String metaDataPath) {
        return metaDataMap.containsKey(metaDataPath);
    }

    /**
     * Utility when load with new meta data to know which selection state to
     * present to user (i.e. so MetaMenu item change combobox is presented in same
     * state as user last put it to (when it was created from other meta data)
     * 
     * @return current meta data path (current default in force)
     */
    @Override
    public String getCurrentMetaDataPath() {
        return currentMetaDataPath;
    }

    // //////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////
    // /
    // / Supporting JsonObjectWrapper Methods but catering for multiple MetaDatas
    // /
    // //////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.common.IMultiMetaDataHelper#getString(java.lang.String, com.google.gwt.json.client.JSONObject)
     */
    @Override
    public String getString(final String name) {

        if (!supportMultiple) {
            // plain old way of working - can return empty
            return metaData.getString(name);
        }

        /*
         * we hope you are on the right meta data - you probably are unless this is
         * an old floating window from old meta data (this one better be
         * currentMetaDataPath)
         */
        String foundVal = metaData.getString(name);

        if (foundVal.isEmpty()) { // unfortunate that may actually want it to be
            // empty

            for (final String path : metaDataMap.keySet()) {

                if (!currentMetaDataPath.equals(path)) { // done that one already (so if
                    // two meta data's only one
                    // more to check)

                    final JsonObjectWrapper anotherMetaData = metaDataMap.get(path);
                    foundVal = anotherMetaData.getString(name);
                    if (!foundVal.isEmpty()) {
                        break;
                    }
                }
            }
        }
        return foundVal; // can still be empty
    }


    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.common.IMultiMetaDataHelper#getNumberAsString(java.lang.String, com.google.gwt.json.client.JSONObject)
     */
    @Override
    public String getNumberAsString(final String name) {

        if (!supportMultiple) {
            return metaData.getNumberAsString(name);
        }
        String foundVal = metaData.getNumberAsString(name);

        if (foundVal.isEmpty()) {

            for (final String path : metaDataMap.keySet()) {

                if (!currentMetaDataPath.equals(path)) { // done that one already

                    final JsonObjectWrapper anotherMetaData = metaDataMap.get(path);
                    foundVal = anotherMetaData.getNumberAsString(name);
                    if (!foundVal.isEmpty()) {
                        break;
                    }
                }
            }
        }
        return foundVal; // can still be empty
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.common.IMultiMetaDataHelper#getArray(java.lang.String, com.google.gwt.json.client.JSONObject)
     */
    @Override
    public IJSONArray getArray(final String name) {
        if (!supportMultiple) {
            return metaData.getArray(name);
        }
        IJSONArray foundVal = metaData.getArray(name);

        if (foundVal.size() == 0) {

            for (final String path : metaDataMap.keySet()) {

                if (!currentMetaDataPath.equals(path)) { // done that one already

                    final JsonObjectWrapper anotherMetaData = metaDataMap.get(path);
                    foundVal = anotherMetaData.getArray(name);
                    if (foundVal.size() != 0) {
                        break;
                    }
                }
            }
        }
        return foundVal;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.common.IMultiMetaDataHelper#alternativeMetaData_getArray(java.lang.String, com.google.gwt.json.client.JSONObject, java.util.List)
     */
    /* the fact that got here at all means looking in alternative meta data */
    @Override
    public IJSONArray alternativeMetaData_getArray(final String name, final List<String> checkedMetaDatas) {
        if (!supportMultiple) {
            // If got here (may support both has not selected voice yet (so means effectively looking for PS data anyway),
            // in this method (means he will NEVER find what looking for
            // (could show exception as this point)

            return metaData.getArray(name);
        }

        IJSONArray foundVal = null; // known to be useless when used with current
        // metaData in force

        checkedMetaDatas.add(currentMetaDataPath);
        for (final String path : metaDataMap.keySet()) {

            if (!checkedMetaDatas.contains(path)) { // done that one already

                final JsonObjectWrapper anotherMetaData = metaDataMap.get(path);
                foundVal = anotherMetaData.getArray(name);
                if (foundVal.size() != 0) {
                    checkedMetaDatas.add(path);
                    break;
                }
            }
        }
        return foundVal;
    }

    @Override
    public IJSONObject getObject(final String name) {
        return metaData.getObject(name);
    }

    @Override
    public JSONObject getNativeObject() {
        return metaData.getNativeObject();
    }

    @Override
    public Double getNumber(final String name) {
        return metaData.getNumber(name);
    }

    @Override
    public int size() {
        return metaData.size();
    }

    @Override
    public boolean containsKey(final String key) {
        return metaData.containsKey(key);
    }

    @Override
    public Set<String> keySet() {
        return metaData.keySet();
    }
}
