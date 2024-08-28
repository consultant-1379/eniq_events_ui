/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.eniq.events.ui.client.datatype.PairedSearchTypeComoBoxType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.events.MaskEventHandler;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.ui.client.resources.EniqResourceBundle;
import com.ericsson.eniq.events.widgets.client.button.ImageButton;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Abstract class to inherit for paired type search components
 * e.g. number and live load combo types in search field
 * <p/>
 * Pulling up code from original PairedTypeSearchComponent  class
 *
 * @author edivkir
 * @since December 2010
 */
public abstract class AbstractPairedTypeSearchComponent implements ISearchComponent, MaskEventHandler {

    protected final static String typeParam = "type=";

    protected String valParam;

    protected final SubmitSearchFieldButtonListener submitSearchFieldButtonListener = new SubmitSearchFieldButtonListener();

    protected final SelectionListener<MenuEvent> liveLoadTypelistener = new LiveLoadTypelistener();

    protected final HorizontalPanel searchCompPanel = new HorizontalPanel();

    protected final Button typeButton = createButton();

    protected String typeSelected;

    protected String typeTextSelected;

    protected String splitStringMetaDataKeys;

    protected List<LiveLoadTypeMenuItem> types;

    protected final int defaultTypeIndex;

    protected final EniqResourceBundle eniqResourceBundle = MainEntryPoint.getInjector().getEniqResourceBundle();

    protected final ImageButton submitButton = new ImageButton(eniqResourceBundle.launchIconToolbar());

    protected static boolean isPairedSubmitButtonAlreadyClicked = false;

    /* e.g. use combobox - not menu item,  for terminal makes*/
    protected final SearchComboBox typesCombo;

    protected final String submitButtonToolTip;

    /*
    * hack for terminals (can select any type, but
    * URL parameter out with always be type = TAC )
    */
    protected String permanentType;

    /*
    * HACK (temp perhaps) to get over what
    * PTMSI add ons have done for terminal tab
    */
    protected boolean isPermanentURLParam;

    /*
    * Can assume this will be set
    * to handle search input
    */
    private final List<ISubmitSearchHandler> searchSubmitHandlers = new ArrayList<ISubmitSearchHandler>();

    /*
    * Cache handlers this component adds to eventbus - for removal later
    */
    protected final List<HandlerRegistration> registeredHandlers = new ArrayList<HandlerRegistration>();

    protected EventBus eventBus;

    /*
    * Let metadata decide if using a Menu component or a combobox component for
    * type selection (they change minds alot and may suit to be different per tab)
    */
    protected final boolean isUsingMenuForType;

    protected final String typeEmptyText;

    protected String emptyText;

    protected final ListStore<ModelData> typesStore = new ListStore<ModelData>();

    /*
    * tab owner id (needed to respond correctly to
    * event on event bus) when search component shared accross tabs
    * (particularly toggling from group component to single component)
    */
    protected final String tabOwnerId;

    /** e.g. "CS" or "PS" or empty */
    private String metaDataRef = EMPTY_STRING;

    /*
    * Hack for as long as not reading liveload tpyes directly from meta data (i.e. for as long as require a call
    * to be made to populate types
    *
    * Exposed access for junit only
    *
    * @see {@link com.ericsson.eniq.events.ui.client.common.widget.LiveLoadTypeUnreadyHelper}
    */
    final static LiveLoadTypeMenuItem DUMMY_TYPE = new LiveLoadTypeMenuItem(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING,
            EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING);

    /**
     * Paired MenuTaskBar (e.g. for node and node type).
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
    public AbstractPairedTypeSearchComponent(final String tabOwnerId, final List<LiveLoadTypeMenuItem> types,
            final int defaultTypeIndex, final String submitButtonToolTip, final boolean isUsingMenuForType,
            final String typeEmptyText) {
        this.types = types;
        this.defaultTypeIndex = defaultTypeIndex;
        this.submitButtonToolTip = submitButtonToolTip;
        this.isUsingMenuForType = isUsingMenuForType;
        this.typeEmptyText = typeEmptyText;
        this.tabOwnerId = tabOwnerId;

        if (types.isEmpty()) {
            this.types.add(DUMMY_TYPE);
        }

        /* use when type selection is from a combobox
        * (NOTE this has to be created as soon as possibly like this for tooltip and
        * setting visible status  (only creating inside condition for junit
        * as junit can not mock GXT 2.1 combobox)*/
        typesCombo = (isUsingMenuForType) ? null : createTypeComboBox(); // NOPMD by eeicmsy on 07/07/10 12:09
    }

    /**
     * For terminal types services can not handle passing the
     * actual type selected (always wants type = TAC)
     *
     * @param permanentType - type from metadata we will always pass as the
     *                      search field  type regardless of type selected
     */
    public void setPermanentType(final String permanentType) {
        this.permanentType = permanentType;
    }

    /**
     * For terminal types services always pass "node="
     *
     * @param valParam - url param, e.g. so can have node=1211@type =TAC
     */
    public void setPermanentValParam(final String valParam) {
        this.isPermanentURLParam = true;
        this.valParam = valParam + EQUAL_STRING;

    }

    /**
     * Set "emptyText" value, which is not a child of the "type" subsection
     * but of the child of "parent" in JSON.
     * Example set - "emptyText = Enter Terminal" in search area of Terminal Tab.
     *
     * @param emptyText Text to be seen when there is no value
     */
    public void setEmptyTextValue(final String emptyText) {
        this.emptyText = emptyText;
    }

    /**
     * Utility to support hiding ENTIRE search field (and replacing with
     * group component. Used in case where NOT using the type component
     * part itself to select groups and single nodes (e.g. network
     * tab at time of release). Here will
     * have extra toggle component running show (e.g. terminal tab at
     * time of release)
     *
     * @param isVisible true to set component visible else false
     */
    public void setVisible(final boolean isVisible) {
        submitButton.setVisible(isVisible);
        if (typeButton != null) {
            typeButton.setVisible(isVisible);
        }
        if (typesCombo != null) {
            typesCombo.setVisible(isVisible);
        }
    }

    /**
     * Types combobox can take time to load
     * call this to mask (default empty text will be "loading"
     *
     * @param isMask true to mask, false to unmask after server call
     */
    public void maskTypesComboBox(final boolean isMask) {
        if (typesCombo == null) {
            return;
        }
        if (isMask) {
            typesCombo.mask();
        } else {

            typesCombo.unmask();
            typesCombo.setEmptyText(typeEmptyText);
        }
    }

    @Override
    public Component getSearchComponent() {
        return searchCompPanel;
    }

    @Override
    public void addSubmitSearchHandler(final ISubmitSearchHandler searchSubmitHandler) {
        searchSubmitHandlers.add(searchSubmitHandler);
    }

    @Override
    public void removeSubmitSearchHandler(final ISubmitSearchHandler searchSubmitHandler) {
        searchSubmitHandlers.remove(searchSubmitHandler);

        if (searchSubmitHandlers.isEmpty()) {
            this.unregistorWithEventBus();
        }
    }

    @Override
    public void setLicencedTypesVisibleOnly(final List<String> licencedTypes) {
        /* when dashboard (no voice data toggle shown) - hiding options when only one licence
        * (will include group types defined in "searchFields" tag */

        for (final LiveLoadTypeMenuItem type : types) {
            if (type.isSeperator()) {
                continue;
            }

            final List<String> supportTypes = type.getMetaDataKeysAsList();
            boolean isVisible = false;
            for (final String licencedType : licencedTypes) {
                if (supportTypes.contains(licencedType)) {
                    isVisible = true;
                    break;
                }
            }
            type.setVisible(isVisible); // i.e. if no PS licence dont show APN type
        }

    }

    @Override
    public String getMetaChangeComponentRef() {
        return metaDataRef;
    }

    @Override
    public void setMetaChangeComponentRef(final String metaDataRef) {
        this.metaDataRef = metaDataRef;

    }

    @Override
    public void unregistorWithEventBus() {
        for (final HandlerRegistration handler : registeredHandlers) {
            handler.removeHandler();
        }
    }

    /**
     * Return copy to try anf avoid any question of ConcurrentModificationException
     *
     * @return copy of search sumbit handlers
     */
    protected List<ISubmitSearchHandler> getSearchSubmitHandlersCopy() {
        return new ArrayList<ISubmitSearchHandler>(searchSubmitHandlers);
    }

    /**
     * Method called when need to set types when they
     * have to be loaded at runtime
     * instead of directly from loaded flat file.
     * Resets the type initially set in constructor
     *
     * @param typesFromServer Menu items for type drop down
     */
    public void setTypeMenuItems(final List<LiveLoadTypeMenuItem> typesFromServer) {
        if (!typesFromServer.isEmpty()) {
            this.types = typesFromServer; // relpace (remove dummy)

            if (isUsingMenuForType) {
                setupTypeSelectComponent();
            } else {
                updateTypeComboBoxStore();
            }
        }
    }

    protected boolean shouldHideSearchComps(final String selectionId) {
        return INPUT.equals(selectionId) || SearchFieldDataType.isSummaryType(selectionId);
    }

    private void updateTypeComboBoxStore() {
        final List<PairedSearchTypeComoBoxType> comboTypes = new ArrayList<PairedSearchTypeComoBoxType>();
        typesStore.removeAll();
        if (typesCombo != null) {
            for (final LiveLoadTypeMenuItem type : types) {
                if (!type.isSeperator()) {
                    comboTypes.add(new PairedSearchTypeComoBoxType(type)); // NOPMD by eeicmsy on 29/06/10 19:18
                }

            }
            typesStore.add(comboTypes);

        }
    }

    /**
     * Submit button to send full selection to server
     * for population of windows and grids etc with selection
     * Necessary because "enter" press is live load call
     */
    protected void setUpSubmitButton() {
        submitButton.setHoverImage(eniqResourceBundle.launchIconToolbarHover());
        submitButton.setDisabledImage(eniqResourceBundle.launchIconToolbarDisable());
        submitButton.setTitle(submitButtonToolTip);
        submitButton.addClickHandler(submitSearchFieldButtonListener);
        submitButton.getElement().setId(SELENIUM_TAG + "launchButton");
        submitButton.getElement().getStyle().setMarginLeft(3, Unit.PX);
        submitButton.getElement().getStyle().setMarginRight(3, Unit.PX);
        submitButton.getElement().getStyle().setPaddingTop(3, Unit.PX);
        submitButton.setEnabled(false);
    }

    protected abstract void setupTypeSelectComponent();

    /**
     * Abstract method for menu action for type changed.
     *
     * @param typeMenuItem selected type menu item
     */
    public abstract void performActionForTypeChanged(final LiveLoadTypeMenuItem typeMenuItem);

    /** @param submit selection from combobox direct or submit button */
    public abstract void performActionForSearchItemSelected(final boolean submit);

    /**
     * Get all values   - type set
     * - text from search field - should be number
     */
    @Override
    public SearchFieldDataType getSearchComponentValue() {
        final String fieldVal = getSearchFieldValue();
        if (fieldVal == null) {
            return null;
        }

        // Add the value of the combo box to the title

        final String typeComboRawValue = typesCombo == null ? null : typesCombo.getRawValue();
        final boolean isTypeComboEmpty = typeComboRawValue == null || typeComboRawValue.isEmpty();
        String searchFieldVal = isTypeComboEmpty ? EMPTY_STRING : typesCombo.getRawValue() + ", ";
        searchFieldVal += fieldVal;

        // "type=TAC" or "type=imsi" is a permanent type as won't change with type (make) selection
        final boolean isPermanentTypeSet = permanentType != null && permanentType.length() > 0;

        final String typeVal = isPermanentTypeSet ? permanentType : typeSelected;
        final String typeTextVal = typeTextSelected;

        final String[] urlParams = getUrlParams(fieldVal, typeVal);
        //Can't launch window without node value for TAC, issue DEFTFTS-496
        if (fieldVal.isEmpty() && typeVal.equals("TAC")) {
            return null;
        }
        return new SearchFieldDataType(searchFieldVal, urlParams, typeVal, typeTextVal, false, splitStringMetaDataKeys,
                null, false);
    }

    protected abstract String getSearchFieldValue();

    protected abstract String[] getUrlParams(String fieldVal, String typeVal);

    /** user changes the selected type */
    class LiveLoadTypelistener extends SelectionListener<MenuEvent> {
        @Override
        public void componentSelected(final MenuEvent ce) {
            performActionForTypeChanged((LiveLoadTypeMenuItem) ce.getItem());
        }
    }

    /* extracted for junit */
    Button createButton() {
        return new Button();
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

    SearchComboBox createTypeComboBox() {
        final SearchComboBox cmb = new SearchComboBox();
        /* set a style that can be manilpulated via css to widen the display area*/
        cmb.setListStyle("x-liveload");
        return cmb;
    }

    /** user selects item from search field button */
    class SubmitSearchFieldButtonListener implements ClickHandler {
        @Override
        public void onClick(final ClickEvent event) {
            performActionForSearchItemSelected(true);
            submitButton.setEnabled(false);
        }

    }
}
