/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ericsson.eniq.events.common.client.json.IJSONArray;
import com.ericsson.eniq.events.common.client.json.IJSONObject;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.common.client.service.DataServiceHelper;
import com.ericsson.eniq.events.common.client.service.IDataRequestCallback;
import com.ericsson.eniq.events.ui.client.datatype.SearchGroupModelData;
import com.ericsson.eniq.events.ui.client.workspace.config.WorkspaceConfigService;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IDimension;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog.DialogType;
import com.ericsson.eniq.events.widgets.client.dropdown.IDropDownItem;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.ID;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.JSON_ROOTNODE;

/**
 * @author ecarsea
 * @since 2012
 *
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractWindowMenuPresenter<T extends BaseView> extends BasePresenter<T> {

    private final DataServiceHelper dataServiceHelper;

    protected final WorkspaceConfigService configService;

    protected final WorkspaceLaunchMenuResourceBundle resourceBundle;

    /**
     * @param view
     * @param eventBus
     * @param resourceBundle
     * @param configService
     * @param dataServiceHelper
     */
    public AbstractWindowMenuPresenter(T view, EventBus eventBus, WorkspaceLaunchMenuResourceBundle resourceBundle,
            WorkspaceConfigService configService, DataServiceHelper dataServiceHelper) {
        super(view, eventBus);
        this.dataServiceHelper = dataServiceHelper;
        this.configService = configService;
        this.resourceBundle = resourceBundle;
        bind();
    }

    /**
     * Get the view associated with the derived presenter
     *
     * @return
     */
    protected abstract IWindowMenuView getWindowMenuView();

    public void getGroups(IDimension dimension) {
        dataServiceHelper.doGetData(dimension.getLiveloadUrl(), "", new IDataRequestCallback() {

            @Override
            public void onSuccess(JSONValue value) {
                final JsonObjectWrapper metaData = new JsonObjectWrapper(value.isObject());

                final IJSONArray data = metaData.getArray(JSON_ROOTNODE);

                final List<DimensionGroup> groups = new ArrayList<DimensionGroup>();
                for (int i = 0; i < data.size(); i++) {
                    final IJSONObject parent = data.get(i);
                    final String name = parent.getString(SearchGroupModelData.DISPLAY_FIELD);
                    final IJSONArray values = parent.getArray(SearchGroupModelData.VALUES);

                    final List<String> elements = extractGroupContents(values);
                    groups.add(new DimensionGroup(name, elements));
                }
                getWindowMenuView().setGroups(groups);

            }

            @Override
            public void onFailure(String message) {
                new MessageDialog().show("Group Load Error", message, DialogType.ERROR);
                getWindowMenuView().setGroups(Collections.<DimensionGroup> emptyList());
            }
        });
    }

    /**
     * 
     */
    public void getInitialSuggestions(IDimension dimension) {
        dataServiceHelper.doGetData(dimension.getLiveloadUrl(), "", new IDataRequestCallback() {

            @Override
            public void onSuccess(JSONValue value) {
                final JsonObjectWrapper metaData = new JsonObjectWrapper(value.isObject());

                final IJSONArray data = metaData.getArray(JSON_ROOTNODE);

                final List<PairedSuggestion> suggestions = new ArrayList<PairedSuggestion>();
                for (int i = 0; i < data.size(); i++) {
                    final IJSONObject parent = data.get(i);
                    final String id = parent.getString("id");
                    final String url = parent.getString("liveLoadURL");

                    suggestions.add(new PairedSuggestion(id, url));
                }
                getWindowMenuView().setInitalSuggestions(suggestions);

            }

            @Override
            public void onFailure(String message) {
                new MessageDialog().show("Load Error", message, DialogType.ERROR);
                getWindowMenuView().setInitalSuggestions(Collections.<PairedSuggestion> emptyList());
            }
        });

    }

    protected List<String> extractGroupContents(final IJSONArray values) {
        final List<String> groups = new ArrayList<String>();

        for (int v = 0; v < values.size(); v++) {
            final IJSONObject vparent = values.get(v);
            groups.add(vparent.getString(ID));
        }
        return groups;
    }

    public static class DimensionGroup implements IDropDownItem {
        private final String name;

        private final List<String> elements;

        /**
         * @param name
         * @param elements
         */
        public DimensionGroup(String name, List<String> elements) {
            this.name = name;
            this.elements = elements;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the elements
         */
        public List<String> getElements() {
            return elements;
        }

        /* (non-Javadoc)
         * @see com.ericsson.eniq.events.widgets.client.dropdown.IDropDownItem#isSeparator()
         */
        @Override
        public boolean isSeparator() {
            return false;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return name;
        }
    }

    public static class PairedSuggestion implements IDropDownItem {
        private final String id;

        private final String loadUrl;

        /**
         * @param id
         * @param loadUrl
         */
        public PairedSuggestion(String id, String loadUrl) {
            this.id = id;
            this.loadUrl = loadUrl;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @return the loadUrl
         */
        public String getLoadUrl() {
            return loadUrl;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return id;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            result = prime * result + ((loadUrl == null) ? 0 : loadUrl.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            PairedSuggestion other = (PairedSuggestion) obj;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            if (loadUrl == null) {
                if (other.loadUrl != null)
                    return false;
            } else if (!loadUrl.equals(other.loadUrl))
                return false;
            return true;
        }

        /* (non-Javadoc)
         * @see com.ericsson.eniq.events.widgets.client.dropdown.IDropDownItem#isSeparator()
         */
        @Override
        public boolean isSeparator() {
            return false;
        }
    }

}
