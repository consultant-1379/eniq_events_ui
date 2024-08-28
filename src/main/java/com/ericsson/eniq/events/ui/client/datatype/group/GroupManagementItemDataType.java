/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype.group;

import java.util.List;

/**
 * Data Type for each Group Type configured in Meta Data
 * @author ecarsea
 * @since 2011
 *
 */
public class GroupManagementItemDataType {
    private final String id;

    private final String name;

    private final String loadGroupUrl;

    private final String liveloadUrl;

    private final String liveloadRoot;
 
    private final List<String> groupElementKeyNameList;

    private WizardDataType wizard;

    private final String groupEditUrl;

    private final String header;

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    private final boolean localFiltering;

    /**
     * @param id
     * @param name
     * @param loadGroupUrl
     * @param liveloadUrl
     * @param liveloadRoot
     * @param groupEditUrl 
     * @param groupElementParserList
     */
    public GroupManagementItemDataType(final String id, final String name, final String header,
            final String loadGroupUrl, final String liveloadUrl, final String liveloadRoot,
            final List<String> groupElementKeyNameList, final WizardDataType wizard, final String groupEditUrl,
            final boolean localFiltering) {
        super();
        this.id = id;
        this.name = name;
        this.header = header;
        this.loadGroupUrl = loadGroupUrl;
        this.liveloadUrl = liveloadUrl;
        this.liveloadRoot = liveloadRoot;
        this.groupElementKeyNameList = groupElementKeyNameList;
        this.wizard = wizard;
        this.groupEditUrl = groupEditUrl;
        this.localFiltering = localFiltering;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the loadGroupUrl
     */
    public String getLoadGroupUrl() {
        return loadGroupUrl;
    }

    /**
     * @return the liveloadUrl
     */
    public String getLiveloadUrl() {
        return liveloadUrl;
    }

    /**
     * @return the liveloadRoot
     */
    public String getLiveloadRoot() {
        return liveloadRoot;
    }

    /**
     * @return the groupElementKeyNameList
     */
    public List<String> getGroupElementKeyNameList() {
        return groupElementKeyNameList;
    }

    /**
     * @return the wizard
     */
    public WizardDataType getWizard() {
        return wizard;
    }

    /**
     * @param wizard the wizard to set
     */
    public void setWizard(final WizardDataType wizard) {
        this.wizard = wizard;
    }

    /**
     * @return the localFiltering
     */
    public boolean isLocalFiltering() {
        return localFiltering;
    }

    /**
     * @return
     */
    public String getGroupEditUrl() {
        return groupEditUrl;
    }

}
