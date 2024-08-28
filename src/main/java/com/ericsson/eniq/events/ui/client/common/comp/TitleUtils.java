package com.ericsson.eniq.events.ui.client.common.comp;

import static com.ericsson.eniq.events.ui.client.common.Constants.DASH;
import static com.ericsson.eniq.events.ui.client.common.Constants.COMMA;

/**
 * Utility class. Not for instantiation.
 *
 * @author ealeerm - Alexey Ermykin
 * @since 05 2012
 */
abstract class TitleUtils {

    private TitleUtils() {
    }

    /**
     * Example of work 1: <br>
     * duplicateWordsPhrase: <tt><b>Access Area Group</b></tt><br>
     * content: <tt><b>Access Area : Event Analysis Summary</b></tt><br>
     * result: <tt><b>Event Analysis Summary</b></tt><br>
     * <p/>
     * * Example of work 2: <br>
     * duplicateWordsPhrase: <tt><b>Access Area Group</b></tt><br>
     * content: <tt><b>Access Area    :  ::::::::::::::: Unknown :: </b></tt><br>
     * result: <tt><b>Unknown ::</b></tt><br>
     *
     * @param duplicateWordsPhrase phrase that should not be duplicated at the beginning of content
     * @param content              whole phrase that can have duplicate words from duplicateWordsPhrase
     *
     * @return new content without duplicates
     */
    static String removeDuplicateWords(final String duplicateWordsPhrase, String content) {
        String result = removeDashesFromStartAndEnd(content);
        result = removeColonsAndSpacesFromStart(result);
        result = result.trim();
        if (duplicateWordsPhrase != null) {
            String duplicateWords = duplicateWordsPhrase.trim();
            if (!duplicateWords.isEmpty()) {
                final String[] words = duplicateWords.split(" ");
                for (String word : words) {
                    word = word.trim(); // it should be whole word
                    if (result.startsWith(word)) {
                        result = result.replaceFirst(word, "");
                        result = removeDashesFromStartAndEnd(result);
                        result = result.trim();
                        result = removeColonsAndSpacesFromStart(result);
                    } else {
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * TR HR83479 Issue 1 :
     * Example of work 1:
     * duplicateWordsPhrase:  Unknown -
     * content: Unknown - KPI Analysis By CauseCode
     * result:  KPI Analysis By CauseCode
     * @param duplicateWordsPhrase    DrillingParameters needs to add in Window Title
     * @param windowTitle  Existing Window Title which may have drilling parameters
     * @return new content without duplicates
     */
    static String removeDuplicateDrillWordsFromTitle(final String duplicateWordsPhrase, String windowTitle) {
        String result = removeDashesFromStartAndEnd(windowTitle);
        result = removeColonsAndSpacesFromStart(result);
        result = result.trim();

        if (duplicateWordsPhrase != null) {
            String duplicateWords = duplicateWordsPhrase.trim();
            if (!duplicateWords.isEmpty()) {
                final String[] words = duplicateWords.split(" ");
                for (int i =0; i<words.length;i++) {
                    String word = words[i].trim(); // it should be whole word
                    result = removeDuplicateWords_DashCommaColonsAndSpaces(result,word);
                }
                for(String word: words){
                    word = word.trim(); // it should be whole word
                    if(result.contains(word) && !word.equals("-")){
                        result = removeDuplicateWords_DashCommaColonsAndSpaces(result,word);
                    }
                }
            }
        }
        return result;
    }


    /**
     * Example of work: <br>
     * title: <tt><b>Event Analysis (GSM Call : Successes)</b></tt><br>
     * result: <tt><b>GSM Call Successes Event Analysis</b></tt><br>
     *
     * @param title title to transform and remove colons and replace double spaces by single ones
     *
     * @return changed (if necessary) title
     */
    static String adjustTitle(String title) {
        String result = makeContentInBracketsFirst(title);
        result = result.replace(":", "");
        result = removeMultiSpaces(result);
        result = result.trim();
        return result;
    }

    /**
     * Example of work: <br>
     * title: <tt><b>Event Analysis (GSM Call Failure)</b></tt><br>
     * result: <tt><b>GSM Call Failure Event Analysis</b></tt><br>
     *
     * @param title title to transform
     *
     * @return changed (if necessary) title
     */
    static String makeContentInBracketsFirst(String title) {
        String result = title;
        if (result != null) {
            result = result.trim();
            if (result.length() > 0) {
                int openBracketInd = result.lastIndexOf('(');
                if (openBracketInd != -1) {
                    int closeBracketInd = result.lastIndexOf(')');
                    if (closeBracketInd != -1 && closeBracketInd > openBracketInd) {
                        String lastPart = result.substring(0, openBracketInd).trim();
                        String firstPart = result.substring(openBracketInd + 1, closeBracketInd).trim();
                        result = firstPart + " " + lastPart;
                    }
                }
            } else {
                result = "";
            }
        } else {
            result = "";
        }
        return result;
    }

    /**
     * Example of work 1: <br>
     * title: <tt><b>Event Analysis Summary (GSM Call Failure)</b></tt><br>
     * prefixes: <tt><b>DG_GroupXXX</b></tt> and <tt><b>GSM Call Failure</b></tt><br>
     * result: <tt><b>Event Analysis Summary</b></tt><br>
     * <p/>
     * Example of work 2: <br>
     * title: <tt><b>Access Area : Event Analysis Summary</b></tt><br>
     * prefixes: <tt><b>DG_GroupXXX</b></tt> and <tt><b>Access Area Group</b></tt><br>
     * result: <tt><b>Event Analysis Summary</b></tt><br>
     *
     * @param title    title to be adjusted
     * @param prefixes prefixes that should not be in the given title
     *
     * @return updated title if needed
     */
    static String adjustTitleByPrefixes(String title, String... prefixes) {
        String newTitleBase = title.trim();

        for (String prefix : prefixes) { // we don't want duplicates in the title
            newTitleBase = removeDuplicateWords(prefix, newTitleBase);
        }

        newTitleBase = adjustTitle(newTitleBase);

        for (String prefix : prefixes) { // in the new title we don't want duplicates as well
            newTitleBase = removeDuplicateWords(prefix, newTitleBase);
        }

        return newTitleBase;
    }

    private static String removeDashesFromStartAndEnd(String str) {
        String s = str;
        while (s.startsWith(DASH)) {
            s = s.replaceFirst(DASH, "");
        }
        while (s.endsWith(DASH)) {
            int dashInd = s.lastIndexOf(DASH);
            s = s.substring(0, dashInd);
        }
        return s;
    }

    private static String removeMultiSpaces(String result) {
        String temp;
        while (result.length() > 0) {
            temp = result.replace("  ", " ");
            if (result.equals(temp)) {
                break;
            } else {
                result = temp;
            }
        }
        return result;
    }

    private static String removeColonsAndSpacesFromStart(String result) {
        if (result.length() > 0) {
            char firstCharacter = result.charAt(0);
            while (firstCharacter == ' ' || firstCharacter == ':') {
                result = result.substring(1);
                if (result.length() > 0) {
                    firstCharacter = result.charAt(0);
                } else {
                    break;
                }
            }
        }
        return result;
    }

    private static String removeCommaFromStartAndEnd(String str) {
        String s = str;
        while (s.startsWith(COMMA)) {
            s = s.replaceFirst(COMMA, "");
        }
        while (s.endsWith(COMMA)) {
            int dashInd = s.lastIndexOf(COMMA);
            s = s.substring(0, dashInd);
        }
        return s;
    }

    private static String removeDuplicateWords_DashCommaColonsAndSpaces(String str,String word) {
        String s = str;
        if(s.startsWith(word)&& !word.equals("-")) {
            s = s.replaceFirst(word, "");
            s = removeCommaFromStartAndEnd(s);
            s = removeDashesFromStartAndEnd(s);
            s = removeColonsAndSpacesFromStart(s);
            s = removeMultiSpaces(s);
            s = s.trim();
        }
        return s;
    }
}
