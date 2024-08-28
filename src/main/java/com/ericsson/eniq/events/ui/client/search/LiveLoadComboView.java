/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

import static com.ericsson.eniq.events.ui.client.common.Constants.LIVE_LOAD_PAGING_SIZE;

/**
 * Live Load combobox (presenter from MVP pattern) which will be used
 * in some cases for the search field component.
 * <p/>
 * <p/>
 * The live load combobox is populated via queries to server - i.e.
 * the #setTypeAhead or selecting items in list is redundant for "autocomplete"
 * - in this case the server will be passing back the best matches
 * so item 0 in list is going to be the target to place in the search field
 *
 * @author eeicmsy
 * @since March 2010
 */
public class LiveLoadComboView implements ILiveLoadComboView {

    private final SearchComboBox combo;

    /**
     * Set up liveload combobox
     * <p/>
     * Uses #setListStyle to account for applying specific style to the dropdown area of the live load combo.
     * <p/>
     * By setting the trigger action to ALL, all the entries present in
     * LiveLoadComboView will be displayed whenever drop down list button
     * is selected.
     */
    public LiveLoadComboView() {

        combo = new SearchComboBox();
        combo.setPageSize(LIVE_LOAD_PAGING_SIZE);
        combo.setWidth(200);
        combo.setListStyle("x-liveload");
        combo.setHideTrigger(false);
        combo.setTypeAhead(true);
        combo.setTriggerAction(TriggerAction.ALL);
        combo.setSelectOnFocus(true);
    }

    public String getDisplayField() {
        return combo.getDisplayField();
    }


    @Override
    public void setToolTipVisible(final boolean toolTipVisible) {
        combo.getToolTip().setVisible(toolTipVisible);
    }

    public String getItemId() {
        return combo.getItemId();
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay#asWidget()
    */
    @Override
    public Widget asWidget() {
        return combo;
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.mvp.Display#startProcessing()
    */
    @Override
    public void startProcessing() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.mvp.Display#stopProcessing()
    */
    @Override
    public void stopProcessing() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setValidator(final Validator validator) {
        combo.setValidator(validator);
    }

    @Override
    public ModelData getValue() {
        return combo.getValue();
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.widget.ILiveLoadComboView#getListView()
    */
    @Override
    public ListView<ModelData> getListView() {
        return combo.getListView();
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.widget.ILiveLoadComboView#getStore()
    */
    @Override
    public ListStore<ModelData> getStore() {
        return combo.getStore();
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.widget.ILiveLoadComboView#setDisplayField(java.lang.String)
    */
    @Override
    public void setDisplayField(final String displayField) {
        combo.setDisplayField(displayField);

    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.widget.ILiveLoadComboView#setStore(com.extjs.gxt.ui.client.store.ListStore)
    */
    @Override
    public void setStore(final ListStore<ModelData> store) {
        combo.setStore(store);
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.widget.ILiveLoadComboView#getRawValue()
    */
    @Override
    public String getRawValue() {
        return combo.getRawValue();
    }

    @Override
    public boolean isEmpty() {
        // TODO this is quite a hit on a key listener (so if can fins a better way)
        return combo.getRawValue().length() == 0;

    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.widget.ILiveLoadComboView#setEmptyText(java.lang.String)
    */
    @Override
    public void setEmptyText(final String emptyText) {
        combo.setEmptyText(emptyText);

    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.widget.ILiveLoadComboView#addSelectionChangedListener(com.extjs.gxt.ui.client.event.SelectionChangedListener)
    */
    @Override
    public void addSelectionChangedListener(final SelectionChangedListener<ModelData> listener) {
        combo.addSelectionChangedListener(listener);

    }

    @Override
    public void addKeyListener(final KeyListener listener) {
        combo.addKeyListener(listener);
    }

    @Override
    public void addMouseListener(final Listener<ComponentEvent> listener) {
        combo.sinkEvents(Event.ONPASTE);
        combo.addListener(Events.OnPaste, listener);
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.widget.ILiveLoadComboView#clearSelections()
    */
    @Override
    public void clearSelections() {
        combo.clearSelections();
        combo.clearLastQuery();
        combo.removeToolTip();
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.widget.ILiveLoadComboView#setToolTip(java.lang.String)
    */
    @Override
    public void setToolTip(final String text) {
        combo.setToolTip(text);
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.widget.ILiveLoadComboView#getPagingToolBar()
    */
    @Override
    public PagingToolBar getPagingToolBar() {
        return combo.getPagingToolBar();
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.search.ILiveLoadComboView#setId(java.lang.String)
    */
    @Override
    public void setId(final String id) {
        combo.setId(id);

    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.search.ILiveLoadComboView#setVisible(boolean)
    */
    @Override
    public void setVisible(final boolean visible) {
        combo.setVisible(visible);

    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.search.ILiveLoadComboView#isVisible()
    */
    @Override
    public boolean isVisible() {
        return combo.isVisible();
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.search.ILiveLoadComboView#setEnable(boolean)
    */
    @Override
    public void setEnable(final boolean enable) {
        combo.setEnabled(enable);
    }

}
