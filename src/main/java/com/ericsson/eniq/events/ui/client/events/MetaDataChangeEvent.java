/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.MetaDataChangeDataType;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event fired to event bus when UI meta data has been CHANGED
 * (supporting swapping meta data menus, e.g. from circuit switched menu items to
 * packet switched menu items and back again)
 * 
 * @author eeicmsy
 * @since April 2011
 */
public class MetaDataChangeEvent extends GwtEvent<MetaDataChangeEventHandler> {

    public static final Type<MetaDataChangeEventHandler> TYPE = new Type<MetaDataChangeEventHandler>();

    private final MetaDataChangeDataType menuSelected;

    public MetaDataChangeEvent(final MetaDataChangeDataType menuSelected) {
        this.menuSelected = menuSelected;
    }

    @Override
    protected void dispatch(final MetaDataChangeEventHandler handler) {
        handler.handleMetaDataChangeEvent(menuSelected);

    }

    @Override
    public Type<MetaDataChangeEventHandler> getAssociatedType() {
        return TYPE;
    }
}
