/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.Event;

/**
 * Plain search field that takes string input in a text box
 * Included for completion of SearchComponent series (same as 
 * IMSI seach field except its not for entering numbers)
 * 
 * @author eeicmsy
 * @since March 2010
 */
public class StringTypeSearchComponent extends AbstractSingleTypeSearchComponent {

    private final TextField<String> searchField = createTextField();

    /**
     * Plain search field that takes string input in a text box
     * 
     * @param tabOwnerId      - tab owner id (needed for event bus listening) 
     * @param emptyText       - Empty text for component, e.g. "Enter myString"
     * @param param           - parameter for url e.g. "myString
     */
    public StringTypeSearchComponent(final String tabOwnerId, final String emptyText, final String param) {
        super(tabOwnerId);
        searchField.setEmptyText(emptyText);
        paramString = param + EQUAL_STRING;
        searchField.addKeyListener(searchFieldSelectionMadeListener);
        searchField.sinkEvents(Event.ONPASTE); // attach the ONPASTE event to the groupComboBox component
        searchField.addListener(Events.OnPaste, mouseListener);
    }

    /**
     * Utility to support hiding single search field (and replacing with 
     * Group component)
     * @param isVisible  true to set component visible else false
     */
    public void setVisible(final boolean isVisible) {
        searchField.setVisible(isVisible); // string search field (not number one)
    }

    @Override
    public boolean isVisible() {
        return searchField.isVisible(); // search field is a string field
    }

    @Override
    public Component getSearchComponent() {
        return searchField;
    }

    @Override
    public String getSearchFieldString() {
        return searchField.getValue();
    }

    /* extracted for junit */
    TextField<String> createTextField() {
        return new TextField<String>();
    }

    ////////////////////////////////////////////
    //////   Implement GroupSingleToggleEventHandler
    ////////////////////////////////////////////

    @Override
    public void toggleGroupSingleDisplay(final String tabId, final boolean setGroupVisible) {

        // guard
        if (!tabOwnerId.equals(tabId)) {
            return;
        }
        StringTypeSearchComponent.this.setVisible(!setGroupVisible);
    }
}
