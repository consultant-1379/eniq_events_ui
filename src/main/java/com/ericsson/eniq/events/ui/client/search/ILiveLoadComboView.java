/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;

/**
 * Complete attempt to use interfaces to avoid GXT 2.1 
 * problem mocking a combobox.
 *
 * @author eeicmsy
 * @since March 2010
 *
 */

/**
 * @author emohasu
 * @since 2011
 */
public interface ILiveLoadComboView extends WidgetDisplay {

    /* it seems can not pass back whole ComboBox as can not mock or stub it
    * without getting errors in GXT
    */

    /**
     * The underlying data field name to bind to this ComboBox (defaults to
     * 'text').
     *
     * @param displayField the display field
     */
    void setDisplayField(final String displayField);

    /**
     * Returns the combo's store.
     *
     * @return the store
     */
    ListStore<ModelData> getStore();

    /**
     * Returns the combo's list view.
     *
     * @return the view
     */
    ListView<ModelData> getListView();

    /**
     * Sets the combo's store.
     *
     * @param store the store
     */
    void setStore(ListStore<ModelData> store);

    /**
     * Returns the raw data value which may or may not be a valid, defined value.
     * To return a normalized value see {@link #getValue}.
     *
     * @return the raw value
     */
    String getRawValue();

    /**
     * Utility to avoid repeat getRawValue checks
     *
     * @return the dirty state
     */
    boolean isEmpty();


    String getDisplayField();

    /**
     * Convenience function to hide or show this component by boolean.
     *
     * @param visible the visible state
     */
    void setVisible(boolean visible);

    void setValidator(Validator validator);

    /**
     * Check if group or single search field in use
     * (if liveload combobox visible then single mode is in use)
     *
     * @return <code>true</code> if the component is visible.
     */
    boolean isVisible();

    /**
     * Sets the default text to display in an empty field.
     *
     * @param emptyText the empty text
     */
    void setEmptyText(String emptyText);

    /**
     * Sets the component's id.
     *
     * @param id the new id
     */
    void setId(String id);

    /**
     * Add selection listener to comobobox input
     *
     * @param listener the listener
     */
    void addSelectionChangedListener(SelectionChangedListener<ModelData> listener);

    /**
     * Adds a key listener.
     *
     * @param listener the key listener
     */
    void addKeyListener(KeyListener listener);

    void setToolTipVisible(boolean toolTipVisible);

    /**
     * Adds a mouse OnPaste event listener.
     *
     * @param listener the mouse listener
     */
    void addMouseListener(Listener<ComponentEvent> listener);

    /**
     * Clears any text/value currently set in the field.
     */
    void clearSelections();

    ModelData getValue();

    /**
     * Sets the component's tool tip.
     *
     * @param text the text
     */
    void setToolTip(String text);

    /**
     * Returns the combo's paging tool bar.
     *
     * @return the tool bar
     */
    PagingToolBar getPagingToolBar();

    /**
     * @param enable
     */
    void setEnable(boolean enable);

}
