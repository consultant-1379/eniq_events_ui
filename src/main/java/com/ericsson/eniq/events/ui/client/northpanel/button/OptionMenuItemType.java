/*
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.northpanel.button;

import java.util.ArrayList;
import java.util.List;

/**
 * Id type defined for items in options menu
 * (to support individual menu items enabling - and general menu identification)
 *
 * @author eeicmsy
 * @since Feb 2012
 */
public enum OptionMenuItemType {

    /*
    * Should be visible only when you have at least one of ("CXC4010925", "CXC4010926", "CXC4010927", "CXC4010929") licenses
    */
    DEFAULT_TIME("ENIQ Default Time", "CXC4010925", "CXC4010926", "CXC4010927", "CXC4010929"),

    /*Group Management depends on license(s):
    * 01. CXC4010925 (subscriber),
    * 02. CXC4010926 (terminal),
    * 03. CXC4010927 (Network),
    * 04. CXC4010929 (Ranking)
    */
    GROUP_MANAGEMENT("Group Management", "CXC4010925", "CXC4010926", "CXC4010927", "CXC4010929"),

    /*Import Groups depends on license(s):
    * 01. CXC4010925 (subscriber),
    * 02. CXC4010926 (terminal),
    * 03. CXC4010927 (Network),
    * 04. CXC4010929 (Ranking)
    */
    IMPORT_GROUPS("Import Groups", "CXC4010925", "CXC4010926", "CXC4010927", "CXC4010929"),

    /*Delete Groups depends on license(s):
    * 01. CXC4010925 (subscriber),
    * 02. CXC4010926 (terminal),
    * 03. CXC4010927 (Network),
    * 04. CXC4010929 (Ranking)
    */
    DELETE_GROUPS("Delete Groups", "CXC4010925", "CXC4010926", "CXC4010927", "CXC4010929"),

    /*User Guide depends on license(s):
    * 01. CXC4010925 (subscriber),
    * 02. CXC4010926 (terminal),
    * 03. CXC4010927 (Network),
    * 04. CXC4010929 (Ranking)
    */
    EE_USER_GUIDE("ENIQ Events User Guide", "CXC4010925", "CXC4010926", "CXC4010927", "CXC4010929"),

    ABOUT("About"),

    LOG_OUT("Log Out");

    private String name;

    private List<String> licenses;

    /**
     * This sets up the options in the option menu.     *
     *
     * @param name     Menu item name
     * @param licenses A list of licenses i.e. "CXC4010925", "CXC4010926" etc...
     */
    OptionMenuItemType(final String name, final String... licenses) {
        this.name = name;

        for (final String license : licenses) {
            addLicense(license);
        }
    }

    public String getName() {
        return name;
    }

    public boolean hasLicense(final String license) {
        return hasLicenses() && licenses.contains(license);
    }

    public boolean hasLicenses() {
        return licenses != null && !licenses.isEmpty();
    }

    private void addLicense(final String license) {
        if (licenses == null) {
            licenses = new ArrayList<String>();
        }

        licenses.add(license);
    }
}
