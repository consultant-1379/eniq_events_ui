/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import static com.ericsson.eniq.events.ui.client.common.Constants.EQUAL_STRING;
import static com.ericsson.eniq.events.ui.client.common.Constants.SELENIUM_TAG;

import com.ericsson.eniq.events.widgets.client.button.ImageButton;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Event;

/**
 * Plain search field that takes integer  
 * input in a text box(e.g. imsi)
 * 
 * @author eeicmsy
 * @since Feb 2010
 */
public class NumberTypeSearchComponent extends AbstractSingleTypeSearchComponent {

    private final NumberField searchField = createNumberField();

    private final ImageButton submitButton;

    private final HorizontalPanel searchCompPanel = new HorizontalPanel();

    /**
     * Create search field for inputing numbers chars only.
     * Enter press will submit number for inclusion in query
     * 
     * @param tabOwnerId      - tab owener id (needed for event bus listening) 
     * @param emptyText       - Empty text for component, e.g. "Enter imsi"
     * @param param           - parameter for url e.g. imsi
     * @param submitButtonTip - tooltip for submit button
     */
    public NumberTypeSearchComponent(final String tabOwnerId, final String emptyText, final String param,
            final String submitButtonTip) {

        super(tabOwnerId);

        mouseListener = new MouseListener();
        searchField.setEmptyText(emptyText);
        paramString = param + EQUAL_STRING;
        submitButton = getSubmitButton(submitButtonTip);
        searchField.addKeyListener(searchFieldSelectionMadeListener);
        searchField.sinkEvents(Event.ONPASTE); // attach the ONPASTE event to the groupComboBox component
        searchField.addListener(Events.OnPaste, mouseListener);
        searchField.setId(SELENIUM_TAG + "searchField");
        submitButton.getElement().setId(SELENIUM_TAG + "searchField_submitButton");
        submitButton.getElement().getStyle().setPaddingTop(3, Unit.PX);

        addComponentsToPanel(); // NOPMD by eeicmsy on 19/05/10 17:11
    }

    /**
     * Utility to support hiding single search field (and replacing with 
     * group component)
     * @param isVisible  true to set comopnent visible else false
     */
    public void setVisible(final boolean isVisible) {

        searchField.setVisible(isVisible);
        submitButton.setVisible(isVisible);
    }

    @Override
    public boolean isVisible() {
        return searchField.isVisible(); // search field is a number field
    }

    @Override
    public Component getSearchComponent() {
        return searchCompPanel; //searchField;
    }

    @Override
    String getSearchFieldString() {
        /* number field but could paste any string in */
        return (searchField.isValid()) ? searchField.getRawValue() : null;

    }

    /* extracted for junit */
    NumberField createNumberField() {
        return new NumberField();
    }

    /* extracted for junit */
    void addComponentsToPanel() {

        searchCompPanel.add(searchField);
        searchCompPanel.add(submitButton);
        searchCompPanel.setTableWidth("75%"); // an attempt at padding (for IE)

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

        NumberTypeSearchComponent.this.setVisible(!setGroupVisible);
    }

}
