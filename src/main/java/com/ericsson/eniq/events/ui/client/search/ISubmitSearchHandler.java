/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

/**
 * Interface to decouple the search fields from the MenuTaskBar
 * by passing MenuTaskBar algorithm into search field
 * (kind of a strategy pattern)
 * 
 * @author eeicmsy
 * @since March 2010
 * 
 * @see {@link com.ericsson.eniq.events.ui.client.common.comp.ISearchFieldComponent.#setSubmitSearchHandler(ISubmitSearchHandler) 
 *
 */
public interface ISubmitSearchHandler {

    /**
     * Perform action when search field informatino
     * is submitted (i.e. send current value to server)
     * 
     * For situation where there is both a group search and a 
     * regular search field available we are not displaying both a the tame 
     * time. This means the component which is currently visible is the one 
     * which is going to take precedence (even if "play" button not selected)
     * 
     */
    void submitSearchFieldInfo();
}
