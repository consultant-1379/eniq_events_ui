/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Class needed just to notify a type change on the paired search 
 * field component - such that group search component holding 
 * same type can be updated also
 * 
 * @author eeicmsy
 * @since May 2010
 *
 */
public class SearchFieldTypeChangeEvent extends GwtEvent<SearchFieldTypeChangeEventHandler> {

    public static final Type<SearchFieldTypeChangeEventHandler> TYPE = new Type<SearchFieldTypeChangeEventHandler>();

    private final String typeSelected;

    private final boolean isGroup;

    private final String tabId;

    private final String typeText;

    /**
     * (paired) Search field type change event (only) 
     *
     * @param tabId         - unique id of tab where search component is contained
     *
     * @param typeSelected  - defined type ID in metadata on a paired search field, e.g.
     *                        APN, SGSN, CELL, etc., which will also be recognisable in
     *                        meta data for group search component
     *@param isGroup      - boolean supporting displaying only the search field or the
     *                       group component at one time on UI. If isGroup is set
     *                       display the group component (and hide search component minus
     * @param typeText    - text for selected type
     */
    public SearchFieldTypeChangeEvent(final String tabId, final String typeSelected, final boolean isGroup,
            String typeText) {
        this.typeSelected = typeSelected;
        this.isGroup = isGroup;
        this.tabId = tabId;
        this.typeText = typeText;
    }

    @Override
    protected void dispatch(final SearchFieldTypeChangeEventHandler handler) {
        handler.handleTypeChanged(tabId, typeSelected, isGroup, typeText);
    }

    @Override
    public Type<SearchFieldTypeChangeEventHandler> getAssociatedType() {
        return TYPE;
    }
}
