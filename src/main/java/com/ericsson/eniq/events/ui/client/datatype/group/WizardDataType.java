/**
 * -----------------------------------------------------------------------
  *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype.group;

/**
 * @author ekurshi
 * @since 2012
 *
 */
public class WizardDataType {
    private String header;

    private boolean localFiltering;

    private String resultRoot;

    private String itemSelectURL;

    private String urlParam;

    private String itemsLoadURL;

    private WizardDataType wizard;

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(final String header) {
        this.header = header;
    }

    /**
     * @return the liveLoadURL
     */
    public boolean isLocalFiltering() {
        return localFiltering;
    }

    /**
     * @param liveLoadURL the liveLoadURL to set
     */
    public void setLocalFiltering(final boolean liveLoadNeeded) {
        this.localFiltering = liveLoadNeeded;
    }

    /**
     * @return the liveLoadRoot
     */
    public String getResultRoot() {
        return resultRoot;
    }

    /**
     * @param liveLoadRoot the liveLoadRoot to set
     */
    public void setResultRoot(final String liveLoadRoot) {
        this.resultRoot = liveLoadRoot;
    }

    /**
     * @return the itemSelectURL
     */
    public String getItemSelectURL() {
        return itemSelectURL;
    }

    /**
     * @param itemSelectURL the itemSelectURL to set
     */
    public void setItemSelectURL(final String itemSelectURL) {
        this.itemSelectURL = itemSelectURL;
    }

    /**
     * @return the urlParam
     */
    public String getUrlParam() {
        return urlParam;
    }

    /**
     * @param urlParam the urlParam to set
     */
    public void setUrlParam(final String urlParam) {
        this.urlParam = urlParam;
    }

    /**
     * @return the itemsLoadURL
     */
    public String getItemsLoadURL() {
        return itemsLoadURL;
    }

    /**
     * @param itemsLoadURL the itemsLoadURL to set
     */
    public void setItemsLoadURL(final String itemsLoadURL) {
        this.itemsLoadURL = itemsLoadURL;
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

}
