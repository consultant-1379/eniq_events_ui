/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public abstract class WorkspaceFilterUtils {

    /**
     * @param name
     * @return
     */
    public static SafeHtml getFormattedName(String name) {
        return SafeHtmlUtils.fromString(name);
    }

    /**
     * Case-Insensitive test of whether a string is equal or contains a substring
     * @param query
     * @return
     */
    public static boolean containsQuery(String query, String name) {
        if (query != null && query.length() > 0) {
            query = query.toLowerCase();
            name = name.toLowerCase();
            return name.contains(query);
        }
        return true;
    }

//    public static SafeHtml convertToFormattedItem(String query, final String item) {
//
//        query = query.toLowerCase();
//
//        int cursor = 0;
//        int index = 0;
//        final String unformattedItem = item;
//        // Create strong search string.
//        final SafeHtmlBuilder builder = new SafeHtmlBuilder();
//
//        while (true) {
//            final WordBounds wordBounds = findNextWord(unformattedItem.toLowerCase(), query, index);
//            if (wordBounds == null) {
//                break;
//            }
//            if (wordBounds.startIndex != -1) {
//                final String part1 = unformattedItem.substring(cursor, wordBounds.startIndex);
//                final String part2 = unformattedItem.substring(wordBounds.startIndex, wordBounds.endIndex);
//                cursor = wordBounds.endIndex;
//                builder.appendEscaped(part1);
//                builder.appendHtmlConstant("<strong>");
//                builder.appendEscaped(part2);
//                builder.appendHtmlConstant("</strong>");
//            }
//            index = wordBounds.endIndex;
//        }
//        builder.appendEscaped(item.substring(cursor));
//
//        return builder.toSafeHtml();
//    }

    private static class WordBounds implements Comparable<WordBounds> {

        final int startIndex;

        final int endIndex;

        public WordBounds(final int startIndex, final int length) {
            this.startIndex = startIndex;
            this.endIndex = startIndex + length;
        }

        @Override
        public int compareTo(final WordBounds that) {
            int comparison = this.startIndex - that.startIndex;
            if (comparison == 0) {
                comparison = that.endIndex - this.endIndex;
            }
            return comparison;
        }
    }

    static WordBounds findNextWord(final String candidate, final String query, final int indexToStartAt) {
        WordBounds firstWord = null;
        final int index = candidate.indexOf(query, indexToStartAt);
        if (index != -1) {
            firstWord = new WordBounds(index, query.length());
        }
        return firstWord;
    }
}
