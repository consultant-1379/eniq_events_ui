/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

/**
 * DataType to retain the data relating to
 * a Wizard Overlay. 
 * 
 * @author eendmcm
 * @author eeicmsy
 * @since Aug 2011
 */
public class WizardInfoDataType {

    private final String wizardID;

    /**
     * URL to call (if any) when the
     * wizard overlay screen is been loaded.
     * Used for "Dynamic" wizard only,
     * if not read (and empty String), considering is
     * a "fized wizard"
     */
    private final String loadURL;

    private final String wizardContentCSS;

    private final String wsUrl;

    /**
     * 
     * @param wizardID           id of wizard (see meta data wizard section)
     * @param loadURL            empty for WizardOverlayFixedResultSet else address 
     *                           of where checkboxes can be found
     * @param wsUrl              "main" web service URL to populate window container (after make checkbox selection)
     * @param wizardContentCSS   addition style to add to checkboxes if required
     */
    public WizardInfoDataType(final String wizardID, final String loadURL, final String wsUrl,
            final String wizardContentCSS) {

        this.wizardID = wizardID;
        this.loadURL = loadURL;
        this.wsUrl = wsUrl;
        this.wizardContentCSS = wizardContentCSS;
    }

    /**
     * Gets URL to call (if any) when the
     * wizard overlay screen is been loaded.
     * (dynamic wizard overlay needs this to know what checkboxes to load)
     * 
     * If this is empty - we treat the wizard overlay as "fixed" type
     * 
     * @return   address for checkboxes to put on wizard for a "dynamic" wizard
     *           (or empty String for a "fixed" wizard)
     */
    public String getLoadURL() {
        return loadURL;
    }

    /**
     * Fetch "main" URL
     * @return url to call on base window (base window presenter) when 
     *         when populating main window (which user selection from wizard overlay)
     */
    public String getWSURL() {
        return wsUrl;
    }

    /**
     * Because we want different spacing on the checkboxes in cause code 
     * and line charts we are reading which one to use via the meta data
     * @see "wizardContentStyle" in "wizards" section
     * 
     * 
     * @return CSS to use for wizard content body
     */
    public String getWizardContentCSS() {
        return wizardContentCSS;
    }

    /**
     * For fixed wizard (no server call) checkboxes - the wizard id
     * must be found in the meta data (to avoid a separate load url to
     * different json
     * @return   assigned wizardId  - see wizards section
     */
    public String getWizardID() {
        return wizardID;
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder(160);
        sb.append("WizardInfoDataType");
        sb.append("{wizardID='").append(wizardID).append('\'');
        sb.append(", loadURL='").append(loadURL).append('\'');
        sb.append(", wizardContentCSS='").append(wizardContentCSS).append('\'');
        sb.append(", wsUrl='").append(wsUrl).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
