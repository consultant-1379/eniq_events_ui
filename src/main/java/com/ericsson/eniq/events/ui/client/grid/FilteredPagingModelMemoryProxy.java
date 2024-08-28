package com.ericsson.eniq.events.ui.client.grid;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.DataReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingModelMemoryProxy;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.util.DefaultComparator;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Client-side filtering-aware paging model. Filtering is performed on the whole result set(previously sorted if needed) and NOT on the first page only.
 *
 * @author ejedmar
 * @since 2012
 */
public class FilteredPagingModelMemoryProxy extends PagingModelMemoryProxy {

   private final Store<ModelData> store;

   private final GridFilters gridFilters;

   public FilteredPagingModelMemoryProxy(final Store<ModelData> store, final GridFilters gridFilters) {
      super(store.getModels());
      this.store = store;
      this.gridFilters = gridFilters;
   }

   @Override
   public void load(final DataReader<PagingLoadResult<? extends ModelData>> reader, final Object loadConfig,
                    final AsyncCallback<PagingLoadResult<? extends ModelData>> callback) {
      try {

         final List<ModelData> models = store.getModels();

         final PagingLoadConfig config = (PagingLoadConfig) loadConfig;

         if (isSortedEnabled(config)) {
            sort(models, config);
         }

         final List<ModelData> filteredList = filter(models);

         callback.onSuccess(createPagingResultSet(config, filteredList));
      } catch (final Exception e) {
         callback.onFailure(e);
      }
   }

   private BasePagingLoadResult<ModelData> createPagingResultSet(final PagingLoadConfig config,
                                                                 final List<ModelData> filteredList) {
      return new BasePagingLoadResult<ModelData>(limitToPage(config, filteredList), config.getOffset(),
              filteredList.size());
   }

   private List<ModelData> filter(final List<ModelData> models) {
      final StoreFilter<ModelData> storeFilter = getStoreFilter();

      final List<ModelData> filteredList = new ArrayList<ModelData>();

      for (final ModelData record : models) {
         if (!filter(storeFilter, record)) {
            filteredList.add(record);
         }
      }
      return filteredList;
   }

   private List<ModelData> limitToPage(final PagingLoadConfig config, final List<ModelData> filtered) {
      final List<ModelData> sublist = new ArrayList<ModelData>();
      final int start = config.getOffset();
      int limit = filtered.size();
      if (config.getLimit() > 0) {
         limit = Math.min(start + config.getLimit(), limit);
      }
      for (int i = config.getOffset(); i < limit; i++) {
         sublist.add(filtered.get(i));
      }
      return sublist;
   }

   private boolean isSortedEnabled(final PagingLoadConfig config) {
      return config.getSortInfo().getSortField() != null;
   }

   private void sort(final List<ModelData> models, final PagingLoadConfig config) {
      final String srtField = config.getSortInfo().getSortField();
      if (srtField != null) {
         Collections.sort(models, config.getSortInfo().getSortDir().comparator(buildComparator(srtField)));
      }
   }

   private Comparator<ModelData> buildComparator(final String srtField) {
      return new Comparator<ModelData>() {

         @Override
         public int compare(final ModelData o1, final ModelData o2) {
            final Object v1 = o1.get(srtField);
            final Object v2 = o2.get(srtField);

            if (getComparator() != null) {
               return getComparator().compare(v1, v2);
            }
            return DefaultComparator.INSTANCE.compare(v1, v2);
         }
      };
   }

   private StoreFilter<ModelData> getStoreFilter() {
      final StoreFilter<ModelData> storeFilter = new StoreFilter<ModelData>() {

         @Override
         public boolean select(final Store<ModelData> storeData, final ModelData parent, final ModelData item,
                               final String property) {
            for (final Filter filter : gridFilters.getFilterData()) {
               if (filter.isActivatable() && filter.isActive() && !filter.validateModel(item)) {
                  return false;
               }
            }
            return true;
         }
      };
      return storeFilter;
   }

   private boolean filter(final StoreFilter<ModelData> storeFilter, final ModelData item) {
      return !storeFilter.select(store, item, item, "");
   }

}