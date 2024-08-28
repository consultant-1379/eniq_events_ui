/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;

/**
 * @author ekurshi
 * @since 2012
 */

public class LocalSuggestOracle extends GroupOracle {

    private final SuggestionTree tree = new SuggestionTree();

    @Override
    public void clear() {
        defaultResponse = null;
        tree.clear();
    }

    @Override
    public void add(final GroupListItem item) {
        tree.add(item);
    }

    @Override
    public void requestSuggestions(final Request req, final Callback callback) {
        setRequest(req);
        final String query = normalizeSearch(req.getQuery());
        final int limit = req.getLimit();

        // Get candidates from search words.
        final List<GroupListItem> candidates = createCandidatesFromSearch(query);

        // Respect limit for number of choices.
        final int numberTruncated = Math.max(0, candidates.size() - limit);
        for (int i = candidates.size() - 1; i > limit; i--) {
            candidates.remove(i);
        }

        // Convert candidates to suggestions if required.
        final List<GroupSuggestion> suggestions;
        if (isFormattingRequired()) {
            suggestions = convertToFormattedSuggestion(req.getQuery(), candidates);
        } else {
            suggestions = getSuggestionsFromStringItems(candidates);
        }

        final Response response = new Response(suggestions);
        response.setMoreSuggestionsCount(numberTruncated);
        callback.onSuggestionsReady(req, response);
    }

    @Override
    public void requestDefaultSuggestions(final Request req, final Callback callback) {
        if (defaultResponse != null) {
            callback.onSuggestionsReady(req, defaultResponse);
        } else {
            super.requestDefaultSuggestions(req, callback);
        }
    }

    private List<GroupListItem> createCandidatesFromSearch(final String query) {
        final ArrayList<GroupListItem> candidates = new ArrayList<GroupListItem>();

        if (query.length() == 0) {
            return candidates;
        }

        // Find all words to search for.
        final HashSet<GroupListItem> candidateSet = new HashSet<GroupListItem>();
        final List<GroupListItem> words = tree.getSuggestions(query, Integer.MAX_VALUE);
        if (words != null) {
            candidateSet.addAll(words);
        }
        candidates.addAll(candidateSet);
        Collections.sort(candidates, new Comparator<GroupListItem>() {

            @Override
            public int compare(GroupListItem ge1, GroupListItem ge2) {
                return ge1.getStringValue().compareTo(ge2.getStringValue());
            }
        });
        return candidates;
    }

    // TODO: Implement correctly, as this solution is super slow
    // Possibly use http://en.wikipedia.org/wiki/Trie to speed things up
    public class SuggestionTree {

        private final Set<GroupListItem> data = new HashSet<GroupListItem>();

        List<GroupListItem> getSuggestions(final String query, @SuppressWarnings("unused") final int limit) {
            final ArrayList<GroupListItem> strings = new ArrayList<GroupListItem>();

            for (final GroupListItem ge : data) {
                String suggestion = normalizeSuggestion(ge.getStringValue());
                if (suggestion.equals(query) || suggestion.contains(query)) {
                    strings.add(ge);
                }
            }

            return strings;
        }

        public void add(final GroupListItem ge) {
            data.add(ge);
        }

        public void clear() {
            data.clear();
        }
    }

}
