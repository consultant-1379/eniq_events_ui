/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.charts;

import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;

/**
 * Class  to directly associate checkboxes with line elements, 
 * where the checkbox role is to toggle on and off chart element display
 *  
 * (almost Data Type class)
 *
 *  
 * @author eeicmsy
 * @since March 2011
 */
public class CheckBoxForChartElement extends CheckMenuItem {

    /*
     * Identifies unique element on chart (e.g. line on a line chart)
     * This can be good enough for hashset comparison (instead of menu text)
     */
    private final String elementId; // e.g. "0"

    /**
      * Construct from existing "toggle chart element" checkbox
      * @param menu          - existing checkbox
      * @param elementId     - associated line id
      */
    public CheckBoxForChartElement(final CheckMenuItem menu, final String elementId) {
        this(menu.getText(), elementId);

    }

    /**
     * Construct check box containing line id, which a view 
     * to using for checkbox-line toggle functionality
     * @param text         - displayed in check box menu item
     * @param elementId    - associated line id
     */
    public CheckBoxForChartElement(final String text, final String elementId) {
        super(text);
        this.elementId = elementId;

    }

    /**
     * Custom utlity supporting fetching chart element id from checkbox
     * @return chart element id, e.g. "0", "1", etc, representing "line" on chart which is 
     *                           associated with the checkbox
     */
    public String getElementID() {
        return elementId;
    }

    // #equals is based on TEXT so can use with regular checkbox info

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CheckBoxForChartElement other = (CheckBoxForChartElement) obj;
        if (text == null) {
            if (other.text != null) {
                return false;
            }
        } else if (!text.equals(other.text)) {
            return false;
        }
        return true;
    }

}
