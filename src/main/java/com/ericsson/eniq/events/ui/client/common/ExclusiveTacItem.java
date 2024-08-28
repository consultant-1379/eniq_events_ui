/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

import static com.ericsson.eniq.events.ui.client.common.CSSConstants.*;

import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType;

/**
 * ExclusiveTac menu item
 * 
 * @author ebelcha
 * @since May 2010
 *
 */
public final class ExclusiveTacItem extends MetaMenuItem {

    /**
     * ID which we will use in meta data to indicate a 
     * exclusiveTac menu item is being addded
     */
    public final static String EXC_TAC_ID = "EXC_TAC";

    /**
     * Construct with default icon
     * @param name  localised text to display (e.g. "Exc_Tac")
     */
    public ExclusiveTacItem(final String name) {
        this(name, EXC_TAC_BUTTON_STYLE);
    }

    /**
     * Constructor added for test code (i.e. can not add 
     * anything (like icon) that would run into java script so add this 
     * to pass empty string)
     * 
     * @param  name       - localised text to display (e.g. "Exc_Tac")
     * @param  iconStyle  - icon from CSS (empty for test)
     */
    public ExclusiveTacItem(final String name, final String iconStyle) {
        super(new MetaMenuItemDataType.Builder().text(name).id(EXC_TAC_ID).style(iconStyle).build());
    }
}
