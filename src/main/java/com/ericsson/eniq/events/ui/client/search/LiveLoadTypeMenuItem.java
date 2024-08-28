/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.Element;

import java.util.Arrays;
import java.util.List;

import static com.ericsson.eniq.events.ui.client.common.Constants.COMMA;
import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;

/**
 * Menu items fed from JsonObjectWrapper readup, 
 * support the Live load Type selection via a menuitem
 * 
 * Hijacked slightly for groups, whereby the network tab type field is
 * being shared with group selection
 * 
 * @author eeicmsy
 * @since March 2010
 *
 */
public class LiveLoadTypeMenuItem extends MenuItem {

    /*
     * id used be used for type (type=APN), but with reuse of 
     * type combobox xomponent for groups we can not share the same id so 
     * intructing a type in search field section of metadata
     */

    public final String id, groupType, name, liveLoadURL, style, emptyText, winMetaSupport;

    /* valParam - value passed to URL (along with type), e.g. "node", "imsi", "ptimsi" etc. */
    private String valParam = EMPTY_STRING;

    /** 
     * Seperator to seperate live load types from group item types
     * @return LiveLoadTypeMenuItem behaving as seperator
     */
    public static LiveLoadTypeMenuItem getSeperator() {
        return new LiveLoadTypeMenuItem() {
            @Override
            protected void onRender(final Element target, final int index) {
                final Element span = XDOM.create("<span class=x-menu-sep>&#160;</span>");
                setElement(span, target, index);
                fly(target).addStyleName("x-menu-sep-li");
            }

            @Override
            public boolean isSeperator() {
                return true;
            }
        };
    }

    /* private for seperator */
    private LiveLoadTypeMenuItem() {
        this(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING);
        hideOnClick = false;

    }

    /**
     * MenuItems for a "type" selection built up 
     * from JsonObjectWrapper (so no real gain in having a separate datatype for 
     * the constructor parameter)
     * 
     * Data type for a type menu option (e.g. APN type, Cell type, etc). 
     * (Liveload search field may have a type menu option).
     * 
     * @param id             - unique reference to item
     * @param groupType      - e.g. APN, SGSN (not using Id to support using component for groups)
     * @param name           - display name (possibly localised)
     * @param liveLoadURL    - URL associated with this type when used for live loading 
     *                       - the type (e.g. Fetch APNs into store from this location)
     * @param style          - Icon for menu item
     * @param emptyText      - Use as holder for empty text to appear in paired search field
     * @param winMetaSupport - Support CS and PS (Circuit switched and Packet Switched). Empty if
     *                         only supporting one (handled a search component creation), e.g set for 
     *                         controller to indicate controller updates must update both PS and CS windows. 
     */
    public LiveLoadTypeMenuItem(final String id, final String groupType, final String name, final String liveLoadURL,
            final String style, final String emptyText, final String winMetaSupport) {
        super(name);

        setId(id);
        if (style.length() > 0) {
            addStyleName(style);
        }

        this.id = id;
        this.groupType = groupType;
        this.name = name;
        this.liveLoadURL = liveLoadURL;
        this.style = style;
        this.emptyText = emptyText;
        this.winMetaSupport = winMetaSupport;
    }

    /**
    * Supporting displaying only the search field or the 
    *                  group component at one time on UI. If isGroup is set 
    *                  display the group component (and hide search component minus 
    *                  the type combo-menu item). Default false - 
    *                  when group set liveload string is not relevant and can be empty) 
    *                  
    * @return  true if liveload combo must be replaced with the group component
    */
    public boolean isGroupType() {
        return groupType.length() > 0;
    }

    /**
     * @return the group type text; can be <tt>null</tt>
     */
    public String getGroupTypeText() {
        if (name != null && name.length() > 0) {
            return name;
        } else {
            return getGroupTypeFromEmptyText();
        }
    }

    /**
     * @return parses group type from empty text; can be <tt>null</tt>
     */
    public String getGroupTypeFromEmptyText() {
        // Note: the method is instead of significant refactoring to pass the group type for
        // selected terminal in terminal tab
        if (emptyText == null) {
            return null;
        }
        final String enterTxt = "Enter ";
        int enterStart = emptyText.indexOf(enterTxt);
        if (enterStart != -1 && enterStart + enterTxt.length() < emptyText.length()) {
            return emptyText.substring(enterStart + enterTxt.length());
        } else {
            return null;
        }
    }

    public boolean isSeperator() {
        return false;
    }

    /**
     * Metadata for regular types passed id as the type (e.g. type=APN), 
     * but since sharing the component tpye comopnent with groups (and 
     * still want to pass ({type = APN) for a different id, we 
     * use the groupType flag in metadata instead
     * @return     string to pass for type (e.g. APN)
     */
    public String getType() {
        return (groupType.length() > 0) ? groupType : id;
    }

    /**
     * Get value passed to URL
     * @return - value passed to URL (along with type), e.g. "node", "imsi", "ptimsi" etc. 
     */
    public String getValParam() {
        return valParam;
    }

    /**
     * Setter for value passed to outbound parameter
     * @param valParam  - value passed to URL (along with type), e.g. "node", "imsi", "ptimsi" etc. 
     */
    public void setValParam(final String valParam) {
        this.valParam = valParam;
    }

    /**
     * String set via meta data for search type to indicate
     * that windows launched from different metadatas can all be updated when
     * this search field type is selected.
     * 
     * e.g. support controller type change, changing windows launched from CS nodes  and windows launched 
     * from Packet switched node
     * 
     * @return   JsonObjectWrapper types supported,  e.g. "CS,PS" or empty String
     */
    public String getSplitStringMetaDataKeys() {
        return winMetaSupport;
    }

    /**
     * Utility returning supported keys (e.g. used in dashboard) 
     * when expect meta data will not pass blank keys as a default (used only be used to know 
     * if what windows needs to be updated - e.g. voice and data ones for a node type "controller") 
     * i.e. now want to use when have no meta data component to know to hide   
     * types entirely when no licence  - so expect meta data for this search field (dashboard) 
     * will show "winMetaSupport": "PS" instead of being blank
     *  
     * @return winMetaSupport as List  (expecting full winMetaSupport use as above)
     */
    public List<String> getMetaDataKeysAsList() {
        return Arrays.asList((winMetaSupport.split(COMMA)));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        final LiveLoadTypeMenuItem other = (LiveLoadTypeMenuItem) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
}
