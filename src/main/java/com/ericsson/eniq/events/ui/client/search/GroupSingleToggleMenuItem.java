/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import com.extjs.gxt.ui.client.widget.menu.MenuItem;

/**
 * Data type used as menu item 
 * in GroupSingleToggle component
 * @author eeicmsy
 * @since July 2010
 *
 */
public class GroupSingleToggleMenuItem extends MenuItem {

    private final String id, name, style;

    /**
     * Expected we are offering the option to toggle between a 
     * group and a single selection (e.g. Terminal or Terminal Group
     */
    private final boolean isGroup;

    public GroupSingleToggleMenuItem(final String id, final String name, final String style, final boolean isGroup) {

        super(name);

        if (style.length() > 0) {
            addStyleName(style);
        }
        this.id = id;
        this.name = name;
        this.style = style;
        this.isGroup = isGroup;
    }

    public String getName() {
        return name;
    }

    public String getStyle() {
        return style;
    }

    @Override
    public String getId() {
        return id;
    }

    public boolean isGroup() {
        return isGroup;
    }

}
