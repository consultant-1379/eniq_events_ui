/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.MemoryProxy;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.google.gwt.json.client.JSONValue;
import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** @author esuslyn */
public class CacheStoreTest extends TestEniqEventsUI {

   private CacheStore objToTest;

   private JSONValue mockedJSONValue;

   private ModelType mockedModelType;

   private GridFilters mockedGridFilters;

   BaseListLoader<ListLoadResult<ModelData>> mockedBaseListLoader;
   ;

   @Before
   public void setUp() {
      mockedJSONValue = context.mock(JSONValue.class);
      mockedModelType = context.mock(ModelType.class);
      mockedGridFilters = context.mock(GridFilters.class);
      context.mock(MemoryProxy.class);
      context.mock(JsonLoadResultReader.class);
      mockedBaseListLoader = context.mock(BaseListLoader.class);

      createObjectToTest();
   }

   @After
   public void tearDown() {
      objToTest = null;
   }

   void createObjectToTest() {
      context.checking(new Expectations() {
         {
            one(mockedBaseListLoader).addLoadListener(with(any(LoadListener.class)));
            one(mockedBaseListLoader).load();
         }
      });
      objToTest = new StubbedCacheStore(mockedJSONValue, mockedModelType, mockedGridFilters);
   }

   @Test
   public void getCacheStore() {
      objToTest.getStore();
   }

   @Test
   public void getPagingLoader() {
      objToTest.getPagingLoader();
   }

   class StubbedCacheStore extends CacheStore {

      /**
       * @param data
       * @param type
       */
      public StubbedCacheStore(final JSONValue data, final ModelType type, final GridFilters gridFilters) {
         super(data, type, gridFilters);
      }

      @Override
      BaseListLoader<ListLoadResult<ModelData>> createBaseListLoader(
              final MemoryProxy<ListLoadResult<ModelData>> proxy,
              final JSONGridLoadResultReader<ListLoadResult<ModelData>> reader) {
         return mockedBaseListLoader;
      }

   }

}
