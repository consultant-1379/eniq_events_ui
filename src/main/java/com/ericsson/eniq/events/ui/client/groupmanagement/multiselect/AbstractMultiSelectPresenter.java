/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.multiselect;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.common.client.service.RestfulRequestBuilder.State;
import com.ericsson.eniq.events.ui.client.common.ServerComms;
import com.ericsson.eniq.events.ui.client.datatype.group.WizardDataType;
import com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementUtils;
import com.ericsson.eniq.events.ui.client.groupmanagement.ISelectionHandler;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author ekurshi
 * @since 2012
 *
 * ecarsea May 2012 - This will currently only handle a 3 panel group editor (i.e. Country, Operator, Selection). Need to extend to handle
 * more panels. There is some code in here to facilitate this (i.e. the wizard indexes - currently always zero
 */
public abstract class AbstractMultiSelectPresenter extends BasePresenter<MultiSelectView> implements
        GroupItemsAggregator {

    private final ServerComms serverComms;

    private final List<SelectionHandler> handlers;

    private String loadRoot;

    private IMultiSelectHandler lastWizardItemSelectHandler;

    /**
     * @param view
     * @param eventBus
     */
    @Inject
    public AbstractMultiSelectPresenter(final MultiSelectView view, final EventBus eventBus) {
        super(view, eventBus);
        serverComms = new ServerComms(eventBus);
        handlers = new ArrayList<AbstractMultiSelectPresenter.SelectionHandler>();
        bind();
    }

    public void init(final WizardDataType dataType, final IMultiSelectHandler clickHandler, final String itemsLoadUrl,
            final String root) {
        this.loadRoot = root;
        this.lastWizardItemSelectHandler = clickHandler;
        this.lastWizardItemSelectHandler.setGroupItemsAggregator(this);
        addWizard(dataType, 0);
        loadInitialSugessions(itemsLoadUrl);
    }

    public void close() {
        unbind();
    }

    /**
     * Load up first panel of items. Selecting one of these will cause second panel to fill with nested items for the current selected item.
     * i.e. operators in a country.
     * @param url
     */
    @SuppressWarnings("deprecation")
    private void loadInitialSugessions(final String url) {
        final String wizardUrl = GroupManagementUtils.getWizardUrl(url, "", null);
        serverComms.requestData(State.GET, wizardUrl, "", new RequestCallback() {

            @Override
            public void onResponseReceived(Request request, Response response) {
                final String result = response.getText();
                final String jsonStr = GroupManagementUtils.removeCallbackParam(result);
                final JSONValue jsonValue = GroupManagementUtils.parseJsonString(jsonStr);
                if (jsonValue == null) {
                    getView().unmask(true);
                    return;
                }
                try {
                    final List<GroupListItem> suggestions = convertToGroupItemList(jsonValue, true, loadRoot);
                    getView().displaySuggestions(0, suggestions);

                    getView().unmask(true);
                } catch (final Exception e) {
                    handleFailure(e, true);
                }
            }

            @Override
            public void onError(Request request, Throwable exception) {
                handleFailure(exception, true);
            }
        });
        getView().mask(0);
    }

    private void addWizard(final WizardDataType wizardDataType, final int index) {
        if (wizardDataType == null) {
            return;
        }
        final boolean multiSelect = index > 0;
        final SelectionHandler handler = createHandler(index, wizardDataType.getItemSelectURL(),
                wizardDataType.getUrlParam(), wizardDataType.getResultRoot());
        getView().addWizard(wizardDataType.getHeader(), handler, multiSelect);
        addWizard(wizardDataType.getWizard(), index + 1);
    }

    public SelectionHandler createHandler(final int index, final String selectUrl, final String param,
            final String resultRoot) {
        final SelectionHandler handler = new SelectionHandler(index, selectUrl, param, resultRoot);
        handlers.add(handler);
        return handler;
    }

    public void maskLiveLoadPanel(final boolean mask) {
        if (mask) {
            lastWizardItemSelectHandler.mask();
        } else {
            lastWizardItemSelectHandler.unMask();
        }
    }

    /**
     * @param jsonValue
     * @param firstItem
     * @param dataRoot
     * @return
     */
    protected abstract List<GroupListItem> convertToGroupItemList(JSONValue jsonValue, boolean firstItem,
            String dataRoot);

    public void displaySuggestions(final List<GroupListItem> suggestions) {
        lastWizardItemSelectHandler.onClear();
        getView().displaySuggestions(0, suggestions);
    }

    protected void handleFailure(final Throwable exception, boolean firstItem) {
        getView().unmask(firstItem);
        getView().displayFailureDialog(exception.getMessage());
    }

    /**
     * ecarsea - Handles selection of an item on the wizard, to load up the corresponding panel i.e, select a country, this will handle loading
     * an operator
     *
     */
    private class SelectionHandler implements ISelectionHandler {

        private final int wizardIndex;

        private final String url;

        private final String param;

        private final String dataRoot;

        private GroupListItem selectedItem;

        private final SelectionHandlerRequestCallback requestCallback = new SelectionHandlerRequestCallback();

        public SelectionHandler(final int wizardIndex, final String selectItemUrl, final String param,
                final String resultRoot) {
            this.wizardIndex = wizardIndex;
            this.url = selectItemUrl;
            this.param = param;
            this.dataRoot = resultRoot;
        }

        /* (non-Javadoc)
         * @see com.ericsson.eniq.events.ui.client.groupmanagement.ISelectionHandler#onItemsSelected(java.util.List)
         */
        @SuppressWarnings("deprecation")
        @Override
        public void onItemsSelected(final List<GroupListItem> selectedItems) {
            //assumes that only one item will be selected at a time
            if (selectedItems.size() > 0) {
                selectedItem = selectedItems.get(0);
                final String wizardUrl = GroupManagementUtils.getWizardUrl(url, param, getUrlParamValue(selectedItem));
                serverComms.requestData(State.GET, wizardUrl, "", requestCallback);
                getView().mask(wizardIndex + 1);
            } else {
                displaySuggestions(new ArrayList<GroupListItem>());
            }
        }

        private class SelectionHandlerRequestCallback implements RequestCallback {

            /* (non-Javadoc)
             * @see com.google.gwt.http.client.RequestCallback#onResponseReceived(com.google.gwt.http.client.Request, com.google.gwt.http.client.Response)
             */
            @Override
            public void onResponseReceived(Request request, Response response) {
                final String result = response.getText();
                final String jsonStr = GroupManagementUtils.removeCallbackParam(result);
                final JSONValue jsonValue = GroupManagementUtils.parseJsonString(jsonStr);
                if (jsonValue == null) {
                    getView().unmask(false);
                    return;
                }
                try {
                    final List<GroupListItem> suggestions = convertToGroupItemList(jsonValue, false, dataRoot);
                    displaySuggestions(suggestions);
                    getView().unmask(false);
                } catch (final Exception e) {
                    handleFailure(e, false);
                }
            }

            /* (non-Javadoc)
             * @see com.google.gwt.http.client.RequestCallback#onError(com.google.gwt.http.client.Request, java.lang.Throwable)
             */
            @Override
            public void onError(Request request, Throwable exception) {
                handleFailure(exception, false);
            }
        }

        public void displaySuggestions(final List<GroupListItem> suggestions) {
            lastWizardItemSelectHandler.onMultiItemSelect(selectedItem, suggestions);
        }
    }

    public abstract String getUrlParamValue(GroupListItem selectedItem);
}
