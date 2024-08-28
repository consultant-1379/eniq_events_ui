/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid.listeners;

import com.ericsson.eniq.events.ui.client.grid.filters.DateTimeFilter.FilterType;

/**
 * @author ekurshi
 * @since 2011
 *
 *Handles the enter press on DateTimeField
 */
public interface DateTimeFieldListener {
    /**
     * Check the updates and validate that on and before filter not apply at same time and then apply the filters.
     * @param filterType it can be on(for equal),before(for less than) or after(for greater than)
     */
    void fireUpdate(FilterType filterType);

}
