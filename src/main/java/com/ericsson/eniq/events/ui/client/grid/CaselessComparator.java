/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import java.util.Comparator;

/**
 * CaselessComparator defines the case-insensitive comparator
 * required to fix TR HN14512. Referenced in CaselessListStore
 * and CacheStore
 * @author eriwals
 * @since Nov 2010
 *
 */
public class CaselessComparator<X extends Object> implements Comparator<X> {

    /**
     * implements the case-insensitive comparator
     * @param o1 - first object in comparison
     * @param o2 - second object in comparison
     */
    @Override
    public int compare(final X o1, final X o2) {

        if (o1 == null || o2 == null) {
            if (o1 == null && o2 == null) {
                return 0;
            }
            return (o1 == null) ? -1 : 1;

        }

        if (o1 instanceof Number) {
            return ((Comparable) o1).compareTo(o2);
        }

        // i.e. if instanceof String:
        return String.CASE_INSENSITIVE_ORDER.compare((String) o1, (String) o2);
    }
}
