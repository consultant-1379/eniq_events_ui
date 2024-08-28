/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import com.ericsson.eniq.events.ui.client.search.LiveLoadTypeMenuItem;
import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * DataType to retrofit what used be a Menu item for type selection in 
 * the paired search type component (will keep the option in 
 * Metadata to use a menu item still - e.g. for network tab)
 * 
 * This data type will hold information for the "set type" combobox
 * (so thats why extending BaseModelData)
 * 
 * @author eeicmsy
 * @since June 2010
 *
 */
public class PairedSearchTypeComoBoxType extends BaseModelData {

    public final static String DISPLAY_FIELD = "name";

    public final static String VALUE_FIELD = "id";

    private final LiveLoadTypeMenuItem menuItem;

    /**
     * Retrofit menu item to a combobox item to switch menu 
     * item display to a combobox
     * 
     * @param menuItem   menuItem when menu used to display available types 
     *                   for paired search field component
     */
    public PairedSearchTypeComoBoxType(final LiveLoadTypeMenuItem menuItem) {

        setName(menuItem.name);
        setValue(menuItem.id);
        this.menuItem = menuItem;
    }

    /**
     * Retro-fit Menu code into combobox
     * @return   MenuItem for live load type 
     */
    public LiveLoadTypeMenuItem getMenuItem() {
        return menuItem;
    }

    // see base class (want to work in combobox store)
    private final void setName(final String name) {
        set(DISPLAY_FIELD, name);
    }

    // see base class (want to work in combobox store)
    private final void setValue(final String id) {
        set(VALUE_FIELD, id);
    }

}
