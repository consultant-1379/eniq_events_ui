/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

/**
 *   
 * @author evidbab
 * @since February 2012
 *
 */
public final class KpiPanelType {

    private final String id;

    private final String name;

    private final String url;

    private final String style;

    private final boolean isEnabled;

    private final KpiPanelSeverityType[] KpiPanelSeverityType;

    private KpiPanelType(final String id, final String name, final String url, final String style,
            final boolean isEnabled, final KpiPanelSeverityType[] KpiPanelSeverityType) {

        this.id = id;
        this.name = name;
        this.url = url;
        this.style = style;
        this.isEnabled = isEnabled;
        this.KpiPanelSeverityType = KpiPanelSeverityType;

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
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the style
     */
    public String getStyle() {
        return style;
    }

    /**
     * @return the isEnabled
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * @return the kpiPanelSeverityType
     */
    public KpiPanelSeverityType[] getKpiPanelSeverityType() {
        return KpiPanelSeverityType;
    }

    /**
     * Create KPi Panel.
     * 
     * @param id
     * @param name
     * @param url
     * @param style
     * @param isEnabled
     * @param KpiPanelSeverityType
     * 
     */
    public static KpiPanelType createKpiPanelType(final String id, final String name, final String url,
            final String style, final boolean isEnabled, final KpiPanelSeverityType[] KpiPanelSeverityType) {
        return new KpiPanelType(id, name, url, style, isEnabled, KpiPanelSeverityType);
    }

    /**
     * Create Disabled KPI Panel.
     * 
     */
    public static KpiPanelType createDisabledPanelType() {
        return new KpiPanelType("", "", "", "", false, new KpiPanelSeverityType[] {});
    }

}
