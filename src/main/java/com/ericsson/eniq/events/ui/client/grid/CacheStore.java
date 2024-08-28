/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.MemoryProxy;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

/**
 * CacheStore reads in JSON data using the ModelType definition to create
 * a client side cache of ModelData
 *
 * @author esuslyn
 * @since March 2010
 */
public class CacheStore {

   final BaseListLoader<ListLoadResult<ModelData>> cacheloader;

   final GridFilters gridFilters;

   /** @return the cacheloader */
   public BaseListLoader<ListLoadResult<ModelData>> getCacheloader() {
      return cacheloader;
   }

   ListStore<ModelData> cacheStore;

   /**
    * JsonReader implementation that reads JSON data using a ModelType
    * definition and creates a store to encapsulates a client side cache
    * of ModelData objects which provide input data for the grid
    *
    * @param data        - JSON data to be read in
    * @param type        - model used to define JSON data being read in
    * @param gridFilters
    */
   public CacheStore(final JSONValue data, final ModelType type, final GridFilters gridFilters) {

      this.gridFilters = gridFilters;

       final Object dataObj;
       if (data instanceof JSONObject) { // for performance
           dataObj = ((JSONObject) data).getJavaScriptObject();
       } else if (data instanceof JSONArray) { // for performance
           dataObj = ((JSONArray) data).getJavaScriptObject();
       } else {
           dataObj = data.toString();
       }

       final MemoryProxy<ListLoadResult<ModelData>> proxy = new MemoryProxy<ListLoadResult<ModelData>>(dataObj);

      final JSONGridLoadResultReader<ListLoadResult<ModelData>> reader = new JSONGridLoadResultReader<ListLoadResult<ModelData>>(
              type);
      cacheloader = createBaseListLoader(proxy, reader); // NOPMD by eeicmsy on 31/03/10 20:04
      this.cacheStore = new ListStore<ModelData>(cacheloader);
      cacheloader.load();
   }

   /** @return the cache store */
   public ListStore<ModelData> getStore() {
      return cacheStore;
   }

   /* Method added for unit testing */
   void setSuperStore(final ListStore<ModelData> store) {
      this.cacheStore = store;
   }

   /**
    * Creates a DataProxy that supports paging when the entire data set in its memory
    *
    * @return PagingLoader interface which loads page able set of data
    */
   public BasePagingLoader<PagingLoadResult<ModelData>> getPagingLoader() {

      final FilteredPagingModelMemoryProxy pagingProxy = new FilteredPagingModelMemoryProxy(cacheStore, gridFilters);
      pagingProxy.setComparator(new CaselessComparator<Object>());

      return new BasePagingLoader<PagingLoadResult<ModelData>>(pagingProxy);
   }

   /* extract for junit to override*/
   BaseListLoader<ListLoadResult<ModelData>> createBaseListLoader(final MemoryProxy<ListLoadResult<ModelData>> proxy,
                                                                  final JSONGridLoadResultReader<ListLoadResult<ModelData>> reader) {
      return new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);
   }

}
