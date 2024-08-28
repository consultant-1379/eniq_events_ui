/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import java.util.HashMap;
import java.util.Map;

import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;

/**
 * Data type used to construct a group selection component
 * Contains Map when used in conjunction with type selection from
 * paired selection component.
 * 
 * @author eeicmsy
 * @since May 2010
 *
 */
public final class GroupSelectInfoDataType {

    // TODO this class was refactored with setters and getters. In general (for code volume reasons) we
    // will NOT be encouraging this for these client.datatype classes (data type classes only)

    /*
     * Remains null if no separate type consideration
     * Hold a reference of types to groups
     * 
     * key - id of type from metadata matched to type combobox in a paired search scenario (e.g. APN),
     * value -  TypeInfo information
     */
    private final Map<String, TypeInfo> typeInfoMap = new HashMap<String, TypeInfo>();

    /*
     * Tool tip on group - e.g. Click to view node group information
     */
    private String tip;

    /*
     * Tool tip on group info button
     */
    private String infoTip;

    /*
     * Web service location of group information 
     */
    private String loadGroupURL;

    /*
     * String to display for empty text, e.g. Enter BSC group...
     */
    private String emptyText;

    /*
     * When have node type group selection, set the default for window launch, e.g APN
     */
    private String defaultIDType;

    /*
     * When not using node type selection, 
     * e.g. IMSI, TAC 
     * Normally will be using existing type 
     * on node
     */
    private String groupType;

    /**
     * Defined when required. The unique text for selected type (e.g. IMSI, IMSI Group, etc.)
     * <p/>
     * It is used as a part of the window title.
     */
    private String groupTypeText;

    /*
     * Support CS and PS (Circuit switched and Packet Switched). Empty if
     * only supporting one (handled a search component creation), e.g set for 
     * controller to indicate controller updates must update both PS and CS windows.
    */
    private String winMetaSupport;

    /**
     * @param tip the tip to set
     */
    public void setTip(final String tip) {
        this.tip = tip;
    }

    /**
     * @param infoTip the infoTip to set
     */
    public void setInfoTip(final String infoTip) {
        this.infoTip = infoTip;
    }

    /**
     * @param loadGroupURL the loadGroupURL to set
     */
    public void setLoadGroupURL(final String loadGroupURL) {
        this.loadGroupURL = loadGroupURL;
    }

    /**
     * @param emptyText the emptyText to set
     */
    public void setEmptyText(final String emptyText) {
        this.emptyText = emptyText;
    }

    /**
     * @param defaultIDType the defaultIDType to set
     */
    public void setDefaultIDType(final String defaultIDType) {
        this.defaultIDType = defaultIDType;
    }

    /**
     * @param groupType the groupType to set
     */
    public void setGroupType(final String groupType) {
        this.groupType = groupType;
    }

    /**
     * String set via meta data for search type to indicate
     * that windows launched from different metadatas can all be updated when
     * this search field type is selected.
     * 
     * e.g. support controller group type change, changing windows launched from CS nodes  and windows launched 
     * from Packet switched node
     * 
     * @param winMetaSupport   e.g. "CS,PS" or empty string
     */
    public void setSplitStringMetaDataKeys(final String winMetaSupport) {
        this.winMetaSupport = winMetaSupport;
    }

    /**
     * @return JsonObjectWrapper types supported,  e.g. "CS,PS" or empty String
     */
    public String getSplitStringMetaDataKeys() {
        return checkNull(winMetaSupport);
    }

    /**
     * @return the tip
     */
    public String getTip() {
        return checkNull(tip);
    }

    /**
     * @return the infoTip
     */
    public String getInfoTip() {
        return checkNull(infoTip);
    }

    /**
     * @return the loadGroupURL
     */
    public String getLoadGroupURL() {
        return checkNull(loadGroupURL);
    }

    /**
     * @return the emptyText
     */
    public String getEmptyText() {
        return checkNull(emptyText);
    }

    /**
     * @return the defaultIDType
     */
    public String getDefaultIDType() {
        return checkNull(defaultIDType);
    }

    /**
     * @return the groupType
     */
    public String getGroupType() {
        return checkNull(groupType);
    }

    /**
     * @return the group type text; can be <tt>null</tt>
     */
    public String getGroupTypeText() {
        if (groupTypeText != null && groupTypeText.length() > 0) {
            return groupTypeText;
        }
        return getGroupTypeTextFromEmptyText();
    }

    public String getGroupTypeTextFromEmptyText() {
        String emptyTextLoc = getEmptyText();
        final String enterTxt = "Enter ";
        int enterStart = emptyTextLoc.indexOf(enterTxt);
        if (enterStart != -1 && enterStart + enterTxt.length() < emptyTextLoc.length()) {
            return emptyTextLoc.substring(enterStart + enterTxt.length());
        } else {
            return null;
        }
    }

    public void setGroupTypeText(String groupTypeText) {
        this.groupTypeText = groupTypeText;
    }

    /**
     * Checks if the value is null then returns {@link com.ericsson.eniq.events.ui.client.common.Constants#EMPTY_STRING}
     */
    private String checkNull(final String value) {
        return (null == value) ? EMPTY_STRING : value;
    }

    /**
     * Checks if typeInfoMap is empty
     */
    public boolean checkIfTypeInfoMapIsEmpty() {
        return typeInfoMap.isEmpty();
    }

    /**
     * adds typeInfo is in the map
     */
    public void addTypeInfo(final String id, final TypeInfo typeInfo) {
        typeInfoMap.put(id, typeInfo);
    }

    /**
     * returns a typeInfo from the map
     */
    public TypeInfo getTypeInfo(final String typeSelected) {
        return typeInfoMap.get(typeSelected);
    }

    /**
     * Hold type information when tab has group search field 
     * for different types (APN, BSC, etc)
     */
    public final class TypeInfo {

        @SuppressWarnings("hiding")
        private final String loadGroupURL;

        @SuppressWarnings("hiding")
        private final String emptyText;

        private final String typeWinMetaSupport;

        /**
         * Construct a TypeInfo inner class when       
         * @param loadGroupURL    URL from where to fetch groups from for the type
         * @param emptyText       Empty text to display - e.g. "APN group....";
         * @param winMetaSupport  ("Cs,PS") String set via meta data for search type to indicate
         *                        that windows launched from different metadatas can all be updated when
         *                        this search field type is selected.
         */
        public TypeInfo(final String loadGroupURL, final String emptyText, final String winMetaSupport) {

            this.loadGroupURL = loadGroupURL;
            this.emptyText = emptyText;
            this.typeWinMetaSupport = winMetaSupport;
        }

        /**
         * @return the loadGroupURL
         */
        public String getLoadGroupURL() {
            return loadGroupURL;
        }

        /**
         * @return the emptyText
         */
        public String getEmptyText() {
            return emptyText;
        }

        /**
         * "CS,PS" support for group types (e.g. APN group, controller group)
         * @return the winMetaSupport (split String from meta data or empty text)
         */
        public String getSplitStringMetaDataKeys() {
            return typeWinMetaSupport;
        }
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder(192);
        sb.append("GroupSelectInfoDataType");
        sb.append("{tip='").append(tip).append('\'');
        sb.append(", infoTip='").append(infoTip).append('\'');
        sb.append(", loadGroupURL='").append(loadGroupURL).append('\'');
        sb.append(", emptyText='").append(emptyText).append('\'');
        sb.append(", defaultIDType='").append(defaultIDType).append('\'');
        sb.append(", groupType='").append(groupType).append('\'');
        sb.append(", winMetaSupport='").append(winMetaSupport).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
