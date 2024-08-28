/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.eniq.events.ui.client.common.widget.WizardOverLayDynamic;
import com.ericsson.eniq.events.ui.client.datatype.PairedSearchTypeComoBoxType;
import com.ericsson.eniq.events.ui.client.events.GroupSingleToggleEvent;
import com.ericsson.eniq.events.ui.client.events.GroupSingleToggleEventHandler;
import com.ericsson.eniq.events.ui.client.events.MaskEvent;
import com.ericsson.eniq.events.ui.client.events.SearchFieldTypeChangeEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.ui.client.common.CSSConstants.PAIRED_SEARCH_COMBO_GAP;
import static com.ericsson.eniq.events.ui.client.common.Constants.EQUAL_STRING;
import static com.ericsson.eniq.events.ui.client.common.Constants.SELENIUM_TAG;

/**
 * Live Load Search Field (for MenuTask bar) supporting a paired
 * combination
 * <p/>
 * <li>type menu</li>
 * <li>search combobox </li>
 * <p/>
 * to represent the search field component.
 * The search Combo is getting its imput via liveload, which is
 * why also have a submit button for use when user decided the inputted
 * data is ready for server query to populate tables etc..
 * <p/>
 * <p>Note, one member of the pair will be drop down menu which always
 * contains a type (e.g. node type, or handset make),
 * such that for example you select "SGSN" type and means that
 * whatever string you are selecting in the main search combobox is considered
 * to be of type "SGSN"
 *
 * @author eeicmsy
 * @since March 2010
 */
public class PairedTypeSearchComponent extends AbstractPairedTypeSearchComponent implements GroupSingleToggleEventHandler {

    private static final String LOADING = "Loading...";

    public LiveLoadComboPresenter searchField;

    private final SelectionChangedListener<ModelData> submitSearchFieldComboListener = new SubmitSearchFieldComboListener();

    private final KeyListener comboKeyListener = new ComboKeyListener();

    private final SelectionChangedListener<ModelData> typeChangedListener = new TypeComboSelectionListener();

    private final KeyListener typeComboKeyListener = new TypeComboKeyListener();

    private final MouseListener mouseListener = new MouseListener();

    private String lastSearchFieldTextValue;

    private String lastTypesComboTextValue;

    /**
     * Paired MenuTaskBar (e.g. for node and node type).
     * Can always assume we always have a type parameter
     *
     * @param tabOwnerId          - Unique identification of where this search component is (typicaly
     *                            tab owner) to support listening to events on bus
     * @param types               - LiveLoadTypeMenuItem types with URLs for types
     * @param defaultTypeIndex    - Default selection type
     * @param submitButtonToolTip - Tooltip on submit button
     * @param isUsingMenuForType  - true if use a Menu component to display the types,
     *                            else use a combobox component to display the types
     *                            (at time of release terminals tab uses comboboxes for makes,
     *                            and network tab uses menu item for tpyes (and groups)).
     * @param typeEmptyText       - when displaying type as a combobox may need something to put in for empty text
     */
    public PairedTypeSearchComponent(final String tabOwnerId, final List<LiveLoadTypeMenuItem> types,
            final int defaultTypeIndex, final String submitButtonToolTip, final boolean isUsingMenuForType,
            final String typeEmptyText) {
        super(tabOwnerId, types, defaultTypeIndex, submitButtonToolTip, isUsingMenuForType, typeEmptyText);

    }

    /*
    * Extra for only allowing group or search on display at same time
    * Leave the type selection (adding groups to the type selection, such
    * that when its a group the search field part of this component is invisible,
    * and vice versa)
    *
    * @param  isVisible true to set component visible. False for invisible.
    */
    private void setSearchFieldSelectionVisible(final boolean isVisible) {
        searchField.setVisible(isVisible);
        submitButton.setVisible(isVisible);
    }

    /**
     * @param isVisible true to set component visible else false
     */
    @Override
    public void setVisible(final boolean isVisible) {
        super.setVisible(isVisible);
        searchField.setVisible(isVisible);
    }

    @Override
    public boolean isVisible() {
        return searchField.isVisible(); // good enough check once this visible all visible)
    }

    /////////////////////////////////////////////////////////////
    ///////////      Implement ISearchFieldComponent
    /////////////////////////////////////////////////////////////

    @Override
    public void registerWithEventBus(final EventBus eventBus) {
        this.eventBus = eventBus;
        /* support hiding component in favour of group component */
        if (this.eventBus != null) {
            // TODO too much duplication when NumberTypeSearch added  (need to use base class more)
            registeredHandlers.add(this.eventBus.addHandler(GroupSingleToggleEvent.TYPE, this));
            registeredHandlers.add(this.eventBus.addHandler(MaskEvent.TYPE, this));
        }
        init();
    }

    @Override
    protected String getSearchFieldValue() {
        return searchField.getRawValue();
    }

    @Override
    protected String[] getUrlParams(String fieldVal, String typeVal) {
        /* e.g {"type=APN", "node=MyNode"}) */
        String[] urlParams;
        if (fieldVal.trim().isEmpty()) {
            urlParams = new String[] { (typeParam + typeVal) };
        } else {
            urlParams = new String[] { (typeParam + typeVal), valParam + fieldVal };
        }
        return urlParams;
    }

    ////////////////////////////////////////////
    //////   Implement GroupSingleToggleEventHandler
    ////////////////////////////////////////////
    /*
    * When paired search field is used in scenrio when type combobox is
    * not supporting groups also (i.e. not the network tab), then
    * we must also listen to toggle component (e.g. terminal tab)
    */
    @Override
    public void toggleGroupSingleDisplay(final String tabId, final boolean setGroupVisible) {
        // guard
        if (!tabOwnerId.equals(tabId)) {
            return;
        }
        PairedTypeSearchComponent.this.setVisible(!setGroupVisible);
    }

    private void init() {
        final ILiveLoadComboView searchDisplay = createLiveLoadComboView();
        setUpSearchCombo(searchDisplay);
        setupTypeSelectComponent();
        setUpSubmitButton();
        addComponentsToPanel(searchDisplay);
    }

    /*
    * Set up liveload combobox view - presenter
    * @param searchDisplay   view class part of live load combobox
    */
    private final void setUpSearchCombo(final ILiveLoadComboView searchDisplay) {
        searchField = createLiveLoadComboPresenter(searchDisplay, eventBus);
        searchDisplay.addSelectionChangedListener(submitSearchFieldComboListener);
        searchDisplay.addKeyListener(comboKeyListener);
        searchDisplay.setValidator(new Validator() {
            @Override
            public String validate(Field<?> field, String value) {
                if (field.getValue() == null) {
                    submitButton.setEnabled(false);
                    return null;
                }

                final ModelData modelData = (ModelData) field.getValue();
                final String selectedComboDisplay = modelData.get(searchDisplay.getDisplayField());

                if (value.equals(selectedComboDisplay)) {
                    submitButton.setEnabled(true);
                    return null;
                } else {
                    submitButton.setEnabled(false);
                }
                return null;
            }
        });
        searchDisplay.addMouseListener(mouseListener);
        searchDisplay.setId(SELENIUM_TAG + "searchField");
    }

    @Override
    public void setupTypeSelectComponent() {
        if (isUsingMenuForType) {
            final Menu typeMenu = createMenu();
            for (final LiveLoadTypeMenuItem type : types) {
                if (type.isSeperator()) {
                    typeMenu.add(new LiveLoadTypeMenuDivider());
                } else {
                    typeMenu.add(type);
                    type.addSelectionListener(liveLoadTypelistener);
                }
            }
            typeMenu.setShadow(false);
            typeButton.setMenu(typeMenu);
            typeButton.setId(SELENIUM_TAG + "typeButton");
            typeButton.setWidth(140);

            searchField.setEnable(true);
            final LiveLoadTypeMenuItem defaultItem = types.get(defaultTypeIndex);
            if (defaultItem != null) {
                performActionForTypeChanged(defaultItem);
            }

        } else {
            searchField.setEnable(false);
            final List<PairedSearchTypeComoBoxType> comboTypes = new ArrayList<PairedSearchTypeComoBoxType>();
            for (final LiveLoadTypeMenuItem type : types) {
                comboTypes.add(new PairedSearchTypeComoBoxType(type)); // NOPMD by eeicmsy on 29/06/10 19:18
            }
            setUpTypeCombo(comboTypes);
        }
    }

    /*
    * Setup types combo (when using a combobox to display types,
    * e.g. from meta data at time of this release use a combo for termnial tab types,
    * but a menu selection for network tab types)
    * (e.g. terminal tab search field at time of this release)
    */
    private final void setUpTypeCombo(final List<PairedSearchTypeComoBoxType> types) {

        // if size more than 1 (dummy) already set up (PS/CS switch)
        typesCombo.setEmptyText((types.size() == 1) ? LOADING : typeEmptyText);

        typesStore.add(types);
        typesCombo.setStore(typesStore); // initially one empty item

        // neccessary for IE
        typesCombo.setValueField(PairedSearchTypeComoBoxType.VALUE_FIELD);
        typesCombo.setDisplayField(PairedSearchTypeComoBoxType.DISPLAY_FIELD);
        typesCombo.setHideTrigger(true);
        typesCombo.setTriggerAction(TriggerAction.ALL);
        typesCombo.setValidator(new Validator() {
            @Override
            public String validate(Field<?> field, String value) {

                if (field.getValue() == null) {
                    searchField.setEnable(false);
                    return null;
                }

                final ModelData modelData = (ModelData) field.getValue();
                final String selectedComboDisplay = modelData.get(typesCombo.getDisplayField());

                if (value.equals(selectedComboDisplay)) {
                    searchField.setEnable(true);
                    return null;
                }

                searchField.setEnable(false);
                return null;
            }
        });
        typesCombo.setTypeAhead(true);
        typesCombo.addStyleName(PAIRED_SEARCH_COMBO_GAP); // space between combos
        typesCombo.addSelectionChangedListener(typeChangedListener);
        typesCombo.addKeyListener(typeComboKeyListener);
        typesCombo.setId(SELENIUM_TAG + "typesCombo");
        typesCombo.setSelectOnFocus(true);

        /* the paired live load combobox
* will need a store set up up via action listener or fall over
* (will get nullpointer on twined liveload combo initList if don't do this ) */

        // fall over if you have not added an intial blank item prior to this
        typesCombo.setValue(typesStore.getAt(0));
        typesCombo.setToolTip(typeEmptyText); // so initial tooltip not blank
    }

    /**
     * Listener on type when type combobox (to change search field store or clear etc)
     */
    private class TypeComboSelectionListener extends SelectionChangedListener<ModelData> {

        @Override
        public void selectionChanged(final SelectionChangedEvent<ModelData> se) {
            final ModelData data = se.getSelectedItem();

            lastTypesComboTextValue = data.get(typesCombo.getDisplayField());

            if (data instanceof PairedSearchTypeComoBoxType) {
                final PairedSearchTypeComoBoxType selection = (PairedSearchTypeComoBoxType) data;
                performActionForTypeChanged(selection.getMenuItem());
            }
        }
    }

    private class TypeComboKeyListener extends KeyListener {

        @Override
        public void componentKeyUp(final ComponentEvent event) {

            if (event.isShiftKey() || event.isAltKey() || event.isNavKeyPress()) {
                return;
            }

            if (lastTypesComboTextValue == null || !lastTypesComboTextValue.equals(typesCombo.getRawValue())) {
                searchField.setEnable(false);
                searchField.clearSelections();
                submitButton.setEnabled(false);
            }

            final String typesComboRawValue = typesCombo.getRawValue();
            typesCombo.setToolTip(typesComboRawValue);
            lastTypesComboTextValue = typesComboRawValue;

        }
    }

    /*
    * Menu action for type changed
    * (exposed for junit)
    * @param typeMenuItem  selected type menu item
    */
    @Override
    public void performActionForTypeChanged(final LiveLoadTypeMenuItem typeMenuItem) {
        final boolean isVisible = (!typeMenuItem.isGroupType());
        setSearchFieldSelectionVisible(isVisible);

        typeSelected = typeMenuItem.getType(); // change to support sharting type combox with groups
        typeTextSelected = typeMenuItem.getGroupTypeText();

        if (typeSelected != null && typeSelected.equals(typeTextSelected)) { // in case of double menu
            typeTextSelected = typeMenuItem.getGroupTypeFromEmptyText();
        }
        splitStringMetaDataKeys = typeMenuItem.getSplitStringMetaDataKeys();

        // revisit - this is work around fix
        if (!isPermanentURLParam) {
            valParam = typeMenuItem.getValParam() + EQUAL_STRING;
        }
        if (typeButton != null) {
            typeButton.setText(typeMenuItem.name);
            typeButton.setIconStyle(typeMenuItem.style);
        }
        if (isVisible) { //using type combo to display group choices
            searchField.setEmptyText(typeMenuItem.emptyText);
            submitButton.setEnabled(false); // for empty text
            searchField.clearSelections();

            setupLiveLoad(typeMenuItem.liveLoadURL, typeSelected);
            searchField.setToolTip(typeMenuItem.emptyText); // initial setting request by AT
        }

        if (typesCombo != null) {
            typesCombo.setToolTip(typeMenuItem.name);
        }

        if (typesCombo != null && typeSelected.length() != 0) {
            searchField.setEnable(true);
        }
        /* Fire search field change info for group component regardless of
           visibility for any type dependent group component in same tab to react */
        if (eventBus != null) {
            // firing if group selected
            eventBus.fireEvent(new SearchFieldTypeChangeEvent(tabOwnerId, typeSelected, !isVisible, typeTextSelected));
        }
        if (shouldHideSearchComps(typeMenuItem.id)) {
            searchField.setVisible(false);
            submitButton.setVisible(false);
        }
    }

    @Override
    public void performActionForSearchItemSelected(final boolean submit) {
        searchField.setToolTip(getSearchFieldValue());
        if (submit) {
            final List<ISubmitSearchHandler> searchSubmitHandlers = getSearchSubmitHandlersCopy();
            for (final ISubmitSearchHandler searchSubmitHandler : searchSubmitHandlers) { // single searchfield has precedence
                // Put a check for excluding WizardOverLayDynamic for wizard refresh to work in multiple mode
                if (!(searchSubmitHandler instanceof WizardOverLayDynamic)) {
                    searchSubmitHandler.submitSearchFieldInfo();
                }
            }
        }
    }

    class SubmitSearchFieldComboListener extends SelectionChangedListener<ModelData> {
        @Override
        public void selectionChanged(final SelectionChangedEvent<ModelData> se) {
            final ModelData data = se.getSelectedItem();

            lastSearchFieldTextValue = data.get(searchField.getView().getDisplayField());

            if (!isPairedSubmitButtonAlreadyClicked) {
                submitButton.setEnabled(true);
            }
            performActionForSearchItemSelected(false);
        }
    }

    private class ComboKeyListener extends KeyListener {

        @Override
        public void componentKeyUp(final ComponentEvent event) {

            searchField.setToolTip(searchField.getRawValue());

            if (lastSearchFieldTextValue == null || !lastSearchFieldTextValue.equals(searchField.getRawValue())) {
                submitButton.setEnabled(false);
            }


            lastSearchFieldTextValue = searchField.getRawValue();

        }
    }

    /**
     * Listener for Mouse Paste Click event.
     * To enable the Submit/Play button on Paste.
     */
    class MouseListener implements Listener<ComponentEvent> {
        @Override
        public void handleEvent(final ComponentEvent be) {
            if (!isPairedSubmitButtonAlreadyClicked) {
                submitButton.setEnabled(true);
            }
        }
    }

    /////////////////////////////////////////////////////////////
    ///////////      Extracted for junit
    /////////////////////////////////////////////////////////////

    /* extracted for junit */
    ILiveLoadComboView createLiveLoadComboView() {
        return new LiveLoadComboView();
    }

    /* extracted for junit */
    LiveLoadComboPresenter createLiveLoadComboPresenter(final ILiveLoadComboView searchDisplay,
            final EventBus eventBus) {
        return new LiveLoadComboPresenter(searchDisplay, eventBus);
    }

    /* extracted for junit */
    void setupLiveLoad(final String url, final String root) {
        searchField.setupLiveLoad(url, root);
    }

    /* extracted for junit */
    void addComponentsToPanel(final ILiveLoadComboView searchDisplay) {
        if (isUsingMenuForType) {
            searchCompPanel.add(typeButton);
        } else {
            searchCompPanel.add(typesCombo);
        }
        searchCompPanel.add(searchDisplay.asWidget());
        searchCompPanel.add(submitButton);
        searchCompPanel.setTableWidth("75%"); // an attempt at padding (for IE)
    }

    @Override
    public void handleMaskEvent(final boolean isMasked, final String tabOwner) {
        if ((tabOwner.equals(tabOwnerId)) && (submitButton != null)) {
            if ((!searchField.isEmpty()) && (!isMasked)) {
                submitButton.setEnabled(true);
            } else if ((searchField.isEmpty()) || (isMasked)) {
                submitButton.setEnabled(false);
            }
        }
    }
}
