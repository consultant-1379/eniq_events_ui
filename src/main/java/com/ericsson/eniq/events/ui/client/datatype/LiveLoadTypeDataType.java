/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

/**
 * Holder for live load id and its URL
 * (a Map would be used but may well wish to expand)
 *   
 * @author eeicmsy
 * @since March 2010
 *
 */
public final class LiveLoadTypeDataType {

    public String id, url;

    /**
     * Holder for live load id and its URL
     * (a Map would be used but may well wish to expand)
     * 
     * @param id     type id defined in metadata (e.g. a hand set make)
     * @param url    livel load url where information for this handset make may be found
     */
    public LiveLoadTypeDataType(final String id, final String url) {
        this.id = id;
        this.url = url;
    }

}
