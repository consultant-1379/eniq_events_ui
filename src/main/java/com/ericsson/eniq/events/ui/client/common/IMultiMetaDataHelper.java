package com.ericsson.eniq.events.ui.client.common;

import com.ericsson.eniq.events.common.client.json.IJSONArray;
import com.ericsson.eniq.events.common.client.json.IJSONObject;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;

import java.util.List;

public interface IMultiMetaDataHelper extends IJSONObject {

    /**
     * Replace current meta data with a previously loaded meta data (Existing
     * windows (from old meta data) must remain on the taskbar) NOT as a result of
     * a server call - just swapping back an existing cache.
     * 
     * @param path
     *          url relative path, e.g. METADATA/UI
     * @return true if meta data is being reset by this call
     */
    boolean resetMetaDataPath(final String path);

    /**
     * Utility to know if certain meta data has been loaded
     * 
     * @param metaDataPath
     *          - URL to meta data, e.g UI_METADATA_PATH
     * @return - true if this meta data (path) has been loaded
     */
    boolean hasLoaded(final String metaDataPath);

    /**
     * Method to call when checking a meta data tag from an alternitive mode e.g.
     * "charts" section is in both meta datas
     * 
     * Looked up current meta data in force and chart array is not blank. final
     * JSONArray arrChart = metaData.getArray(CHARTS_SECTION, null);
     * 
     * so we loop though it. But when nothing is found after looping through that
     * chart section we know then that we are using the wrong meta data at this
     * point (e.g. KPI chart launched from CS window in a PS mode)
     * 
     * So externally call to look in other meta data
     * 
     * @param name
     *          key in JSON file -- e.g. charts
     * @return a json array which may or may not contain what we are looking for
     * 
     */
    IJSONArray alternativeMetaData_getArray(final String name,
                                           final List<String> checkedMetaDatas);

    void setMetaDataFromServer(String metaDataPath, JsonObjectWrapper metaData);

    String getCurrentMetaDataPath();
}