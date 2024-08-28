/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;

import java.util.List;

import com.google.web.bindery.event.shared.EventBus;

import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.events.GroupSingleToggleEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.user.client.Element;

/**
 * Requirement only display single search component or group component 
 * at the same time. This toggle component will be used on the 
 * main menu task bar to let the operator choose which component he wishes to display.
 * 
 * 
 * @author eeicmsy
 * @since July 2010
 *
 */
public class GroupSingleToggleComponent {

    private final Button toggleGroupButton = createButton();

    private final List<GroupSingleToggleMenuItem> elementData;

    private EventBus eventBus;

    private final String tabId;

    private PairedTypeSearchComponent pairedSearchComponent;

    private LiveLoadTypeUnreadyHelper typeFetcher;

    private GroupTypeSearchComponent groupSelectComponent;

    private final SelectionListener<MenuEvent> selectGroupSinglelistener = new GroupSingleListener();

    /**
     * Component (set up from metadata), specificaly to allow 
     * toggling the search field display from "group" mode to single mode
     * 
     * @param tabId   tab owner id
     * @param data    toggle choices (e.g. terminal, terminal group)
     */
    public GroupSingleToggleComponent(final String tabId, final List<GroupSingleToggleMenuItem> data) {
        this.elementData = data;
        this.tabId = tabId;
    }

    /**
     * @param comp
     * @param typeFetcher2
     * @param groupSelectComponent2
     */
    private boolean needServerCall;

    public void setComponents(final PairedTypeSearchComponent comp, final LiveLoadTypeUnreadyHelper typeFetcher,
            final GroupTypeSearchComponent groupSelectComponent, final boolean needServerCall) {
        this.pairedSearchComponent = comp;
        this.typeFetcher = typeFetcher;
        this.groupSelectComponent = groupSelectComponent;
        this.needServerCall = needServerCall;
    }

    public void initiateWithEventBus(final EventBus bus) {
        this.eventBus = bus;
        init();
    }

    private final void init() {
        final Menu itemMenu = createMenu();

        for (final GroupSingleToggleMenuItem item : elementData) {
            itemMenu.add(item);
            item.addSelectionListener(selectGroupSinglelistener);
        }
        itemMenu.setShadow(false);
        toggleGroupButton.setMenu(itemMenu);
        toggleGroupButton.setId(SELENIUM_TAG + "toggleGroupButton");
        toggleGroupButton.setWidth(140);
        final GroupSingleToggleMenuItem defaultItem = elementData.get(0);
        if (defaultItem != null) {

            performActionForItemSelected(defaultItem);
        }
    }

    public Component getComponent() {
        return toggleGroupButton;
    }

    /**
     * When says "-Input-" no component is visible 
     * (Neither the group component or the paired type component)
     * 
     * We want to ask this coponent (if has one) if it contains
     * Input text (implying usually that we do not have a node type to be 
     * even attempting to search for grids with in meta data (multi result sets, 
     * e.g. fixedId "bla_APN".  Can not ask a searchfield data directly for 
     * type being "INPUT" to support this request because of "perminant" type
     * (TAC) added in terminal component to support live load for terminals
     * 
     * @return   true if one of the components being toggled is visible
     *           (we hided its visiblitly with the "INPUT" option
     */
    public boolean isAnyComponentVisible() {
        return (pairedSearchComponent != null && pairedSearchComponent.isVisible())
                || ((groupSelectComponent != null) && groupSelectComponent.isVisible());

    }

    private void performActionForItemSelected(final GroupSingleToggleMenuItem selection) {
        toggleGroupButton.setText(selection.getName());
        toggleGroupButton.setIconStyle(selection.getStyle());

        /* fire search field change info for group component regardless of visiblity
        for any type dependent group component in same tab to react
        */

        if (shouldHideSearchComps(selection.getId())) {
            pairedSearchComponent.setVisible(false);
            groupSelectComponent.setVisible(false);

        } else if (eventBus != null) {
            if (needServerCall && (TERMINAL.equals(selection.getId()))) {
                typeFetcher.searchComponentReference.maskTypesComboBox(true);
                typeFetcher.makeServerRequestForTypesData(eventBus);
            }
            eventBus.fireEvent(new GroupSingleToggleEvent(tabId, selection.isGroup()));
        }
    }

    private boolean shouldHideSearchComps(final String selectionId) {
        return INPUT.equals(selectionId) || SearchFieldDataType.isSummaryType(selectionId);

    }

    /** user changes the selected type */
    class GroupSingleListener extends SelectionListener<MenuEvent> {
        @Override
        public void componentSelected(final MenuEvent ce) {
            performActionForItemSelected((GroupSingleToggleMenuItem) ce.getItem());
        }
    }

    /* extracted for junit */
    Menu createMenu() {
        return new Menu() {
            @Override
            public void show(final Element elem, final String pos, final int[] offsets) {
                offsets[0] = 7;
                offsets[1] = -3;
                super.show(elem, pos, offsets);
            }

        };
    }

    /* extracted for junit */
    Button createButton() {
        return new Button();
    }
}
