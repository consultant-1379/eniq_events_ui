/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * @author ekurshi
 * @since 2012
 */
public abstract class GroupOracle extends SuggestOracle {

    protected Response defaultResponse;

    private Request request;

    protected static final String WHITESPACE_STRING = " ";

    class GroupSuggestion implements Suggestion {

        private final GroupListItem groupElement;

        private final String displayString;

        public GroupSuggestion(final GroupListItem groupElement, final String displayString) {
            this.groupElement = groupElement;
            this.displayString = displayString;
        }

        @Override
        public String getDisplayString() {
            return displayString;
        }

        @Override
        public String getReplacementString() {
            return groupElement.getStringValue();
        }

        /**
         * @return the groupElement
         */
        public GroupListItem getGroupElement() {
            return groupElement;
        }
    }

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

    public final void setDefaultSuggestionsFromText(final Collection<GroupListItem> suggestionList) {
        final Collection<GroupSuggestion> accum = new ArrayList<GroupSuggestion>();
        for (final GroupListItem candidate : suggestionList) {
            accum.add(createSuggestion(candidate, candidate.getStringValue()));
        }
        this.defaultResponse = new Response(accum);
    }

    public void addAll(final Collection<GroupListItem> groupElements) {
        clear();

        if (groupElements != null && !groupElements.isEmpty()) {
            final Collection<Suggestion> defaultSuggestions = new ArrayList<Suggestion>();
            for (final GroupListItem groupElement : groupElements) {
                defaultSuggestions.add(createSuggestion(groupElement, groupElement.getStringValue()));
                add(groupElement);
            }
            defaultResponse = new Response(defaultSuggestions);
        }
    }

    public abstract void add(final GroupListItem item);

    @Override
    public boolean isDisplayStringHTML() {
        return true;
    }

    public abstract void clear();

    protected String normalizeSearch(final String search) {
        return normalizeSuggestion(search);
    }

    protected String normalizeSuggestion(final String toNormalize) {
        return toNormalize.toLowerCase();
    }

    protected GroupSuggestion createSuggestion(final GroupListItem groupElement, final String displayString) {
        return new GroupSuggestion(groupElement, displayString);
    }

    protected List<GroupSuggestion> convertToFormattedSuggestion(String query, final List<GroupListItem> items) {

        query = normalizeSearch(query);

        final List<GroupSuggestion> suggestions = new ArrayList<GroupSuggestion>();
        for (final GroupListItem candidate : items) {
            int cursor = 0;
            int index = 0;
            final String unformattedItem = candidate.getStringValue();
            // Create strong search string.
            final SafeHtmlBuilder accum = new SafeHtmlBuilder();

            while (true) {
                final WordBounds wordBounds = findNextWord(unformattedItem.toLowerCase(), query, index);
                if (wordBounds == null) {
                    break;
                }
                if (wordBounds.startIndex != -1) {
                    final String part1 = unformattedItem.substring(cursor, wordBounds.startIndex);
                    final String part2 = unformattedItem.substring(wordBounds.startIndex, wordBounds.endIndex);
                    cursor = wordBounds.endIndex;
                    accum.appendEscaped(part1);
                    accum.appendHtmlConstant("<strong>");
                    accum.appendEscaped(part2);
                    accum.appendHtmlConstant("</strong>");
                }
                index = wordBounds.endIndex;
            }

            // Check to make sure the search was found in the string.
            if (cursor == 0) {
                continue;
            }
            accum.appendEscaped(candidate.getStringValue().substring(cursor));

            suggestions.add(createSuggestion(candidate, accum.toSafeHtml().asString()));
        }
        return suggestions;
    }

    private WordBounds findNextWord(final String candidate, final String query, final int indexToStartAt) {
        WordBounds firstWord = null;
        final int index = candidate.indexOf(query, indexToStartAt);
        if (index != -1) {
            firstWord = new WordBounds(index, query.length());
        }
        return firstWord;
    }

    public boolean isFormattingRequired() {
        final String query = request.getQuery();
        return isDisplayStringHTML() && (query != null && query.length() > 0);
    }

    public List<GroupSuggestion> getSuggestionsFromStringItems(final List<GroupListItem> items) {
        final List<GroupSuggestion> suggestions = new ArrayList<GroupSuggestion>();
        for (final GroupListItem candidate : items) {
            suggestions.add(createSuggestion(candidate, candidate.getStringValue()));
        }
        return suggestions;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(final Request request) {
        this.request = request;
    }

}
