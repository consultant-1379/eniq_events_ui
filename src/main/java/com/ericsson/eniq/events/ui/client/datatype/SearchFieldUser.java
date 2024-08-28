/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

/**
 * Classification of search field 
 * user for window (or porlet)
 * 
 * @author eeicmsy
 * @since October 2011
 *
 */
public enum SearchFieldUser {

    /*
     * TRUE is search field user (window reacts to search field changes)
     * PATH is search field user who does not use "type=APN", rather appends type to base URL
     * FALSE is not a search field user
     */

    TRUE, PATH, FALSE

}
