/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import com.extjs.gxt.ui.client.widget.menu.MenuItem;

/**
 * 
 * Configure data for MetaDataChangeComponent (which is also a menu item) 
 *  
 * Menu Data type read from "master" meta data to allow us locate other meta data, 
 * so we can offer meta data toggle feature (e.g. switch from circuit switched meta data to 
 * packet switched meta data)
 *    
 * @author eeicmsy
 * @since April 2011
 */
public class MetaDataChangeDataType extends MenuItem {

    private final String metaDataPath;

    private final String style;

    private final String tip;

    private final String shortText;

    private final boolean isLicenced;

    private final String key;

    /**
     * Menu Data type read from "master" meta data to allow us locate other meta data, 
     * so we can offer meta data toggle feature (e.g. switch from circuit switched meta data to 
     * packet switched meta data)
     * 
     * @param name         - menu name, e.g. "Circuit Switched"    
     * @param shortText    - closed menu name when selected (shorter), e.g.  "CS"
     * @param style        - icon style linked to CSS
     * @param tip          - tool-tip on menu item, e.g. "Circuit switched menu options"
     * @param metaDataPath - Meta data route path, e.g. UI_METADATA_PATH or UI_METADATA_PATH_MSS
     * @param key          - e.g. "CS" - support changing short text or name without affecting "CS" key
     * @param isLicenced   - true if user has licence to view this menu option
     */
    public MetaDataChangeDataType(final String name, final String shortText, final String style, final String tip,
            final String metaDataPath, final String key, final boolean isLicenced) {
        super(name);

        this.metaDataPath = metaDataPath;
        this.style = style;
        this.tip = tip;
        this.shortText = shortText;
        this.key = key;
        this.isLicenced = isLicenced;

        setId(SELENIUM_TAG + name);

        if (style != null && style.length() > 0) {
            addStyleName(style);
        }
        setToolTip(tip);

    }

    public boolean isLicenced() {
        return isLicenced;
    }

    public String getMetaDataPath() {
        return metaDataPath;
    }

    public String getStyle() {
        return style == null ? EMPTY_STRING : style;
    }

    public String getTip() {
        return tip == null ? EMPTY_STRING : tip;
    }

    public String getShortText() {
        return shortText;
    }

    public String getKey() {
        return key;
    }
}
