/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import com.ericsson.eniq.events.ui.client.events.MaskEvent;
import com.ericsson.eniq.events.ui.client.events.SearchFieldTypeChangeEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.user.client.Event;
import com.google.web.bindery.event.shared.EventBus;

import java.util.List;

import static com.ericsson.eniq.events.ui.client.common.Constants.EQUAL_STRING;
import static com.ericsson.eniq.events.ui.client.common.Constants.SELENIUM_TAG;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_ENTER;

/**
 * Number Field (for MenuTask bar) supporting a paired combination
 * <p/>
 * <li>type menu</li>
 * <li>search Number Field </li>
 * <p/>
 * To represent the paired number type search field component.
 * The search field is getting its input via number Field,inputed
 * data is ready for server query to populate tables etc..
 *
 * @author edivkir
 * @since December 2010
 */
public class PairedNumberTypeSearchComponent extends AbstractPairedTypeSearchComponent {

   private NumberField numberField;

   private final KeyListener keyListener = new NumberKeyListener();

   private final MouseListener mouseListener = new MouseListener();

   boolean isMasked;

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
   public PairedNumberTypeSearchComponent(final String tabOwnerId, final List<LiveLoadTypeMenuItem> types,
                                          final int defaultTypeIndex, final String submitButtonToolTip, final boolean isUsingMenuForType,
                                          final String typeEmptyText) {
      super(tabOwnerId, types, defaultTypeIndex, submitButtonToolTip, isUsingMenuForType, typeEmptyText);
   }

   /** @param isVisible true to set component visible. False for invisible. */
   protected void setSearchFieldSelectionVisible(final boolean isVisible) {
      numberField.setVisible(isVisible);
      submitButton.setVisible(isVisible);
   }

   /** @param isVisible true to set component visible else false */
   @Override
   public void setVisible(final boolean isVisible) {
      super.setVisible(isVisible);
      numberField.setVisible(isVisible);
   }

   @Override
   public boolean isVisible() {
      return numberField.isVisible(); // good enough check once this visible all visible)
   }

    /////////////////////////////////////////////////////////////

   /////////////////////////////////////////////////////////////

   @Override protected String[] getUrlParams(String fieldVal, String typeVal) {
        /* e.g {"type=IMSI", "imsi=12345"}) */
        return new String[]{(typeParam + typeVal), valParam + fieldVal};
   }

    ///////////      Implement ISearchFieldComponent
    @Override
    public void registerWithEventBus(final EventBus eventBus) {
        this.eventBus = eventBus;
        /* support hiding component in favour of group component */
        if (this.eventBus != null) {
            // TODO too much duplication when NumberTypeSearch added  (need to use base class more)
            registeredHandlers.add(this.eventBus.addHandler(MaskEvent.TYPE, this));
        }
        init();
    }

    @Override
    protected String getSearchFieldValue() {
        return numberField.getRawValue();
    }

    /*
    * Carry out initialisations now that
    * event bus ready now from presenter
    */
   private void init() {
      setUpSearchCombo();
      setupTypeSelectComponent();
      setUpSubmitButton();
      addComponentsToPanel();
   }

   /** Set up search field view. */
   private void setUpSearchCombo() {
      numberField = new NumberField();
      numberField.addKeyListener(keyListener);
      numberField.sinkEvents(Event.ONPASTE); // attach the ONPASTE event to the groupComboBox component
      numberField.addListener(Events.OnPaste, mouseListener);
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
         final LiveLoadTypeMenuItem defaultItem = types.get(defaultTypeIndex);
         if (defaultItem != null) {
            performActionForTypeChanged(defaultItem);
         }
      }
   }

   /**
    * Menu action for type changed
    *
    * @param typeMenuItem selected type menu item
    */
   @Override
   public void performActionForTypeChanged(final LiveLoadTypeMenuItem typeMenuItem) {
      final boolean isVisible = (!typeMenuItem.isGroupType());
      setSearchFieldSelectionVisible(isVisible);

      typeSelected = typeMenuItem.getType(); // change to support sharing type box with groups
      typeTextSelected = typeMenuItem.getGroupTypeText();

      if (typeSelected != null && typeSelected.equals(typeTextSelected)) { // in case of double menu
          typeTextSelected = typeMenuItem.getGroupTypeFromEmptyText();
      }

      splitStringMetaDataKeys = typeMenuItem.getSplitStringMetaDataKeys();

      if (!isPermanentURLParam) {
         valParam = typeMenuItem.getValParam() + EQUAL_STRING;
      }

      if (typeButton != null) {
         typeButton.setText(typeMenuItem.name);
         typeButton.setIconStyle(typeMenuItem.style);
      }
      if (isVisible) { //using type to display group choices
         numberField.setEmptyText(typeMenuItem.emptyText);
         submitButton.setEnabled(false);
         numberField.clear();
         numberField.setToolTip(typeMenuItem.emptyText);
      }
      if (typesCombo != null) {
         typesCombo.setToolTip(typeMenuItem.name);
      }

      /* Fire search field change info for group component regardless of visibility
     for any type dependent group component in same tab to react
      */
      if (eventBus != null) {
          eventBus.fireEvent(new SearchFieldTypeChangeEvent(tabOwnerId, typeSelected, !isVisible, typeTextSelected));
      }
      if (shouldHideSearchComps(typeMenuItem.id)) {
         numberField.setVisible(false);
         submitButton.setVisible(false);
      }
   }

   /** Action for selection from search field direct or submit button */
   @Override
   public void performActionForSearchItemSelected(final boolean submit) {
      numberField.setToolTip(getSearchFieldValue());
      if (submit) {
         submitSearchFieldInfo();

      }
   }

   /*
   * submit that search field has changed to all handlers (mostly MenuTaskBar)
   */
   private void submitSearchFieldInfo() {
      final List<ISubmitSearchHandler> searchSubmitHandlers = getSearchSubmitHandlersCopy();
      for (final ISubmitSearchHandler searchSubmitHandler : searchSubmitHandlers) {
         searchSubmitHandler.submitSearchFieldInfo();
      }
   }

   /** Key listener to enable-disable submit button for key user input into number search field */
   class NumberKeyListener extends KeyListener {
      @Override
      public void componentKeyUp(final ComponentEvent event) {
         if (getSearchFieldValue().isEmpty()) {
            numberField.setToolTip(emptyText);
         }
         handleEnterPress(event.getKeyCode());

         if ((!getSearchFieldValue().isEmpty()) && (!isMasked)) {
            submitButton.setEnabled(true);
         } else if ((getSearchFieldValue().isEmpty()) || (isMasked)) {
            submitButton.setEnabled(false);
         }
      }

      /* extracted for junit  : added and exposed for junit coverage */
      void handleEnterPress(final int keyCode) {
         if (keyCode == KEY_ENTER && !isMasked) {
            submitSearchFieldInfo(); // single search field has precedence
         }
      }
   }

   /**
    * Listener for Mouse Paste Click event.
    * To enable the Submit/Play button on Paste.
    */
   private class MouseListener implements Listener<ComponentEvent> {
      @Override
      public void handleEvent(final ComponentEvent be) {
         if (!isMasked) {
            submitButton.setEnabled(true);
         }
      }
   }

   /** Add all the component button, search field, type to the panel. */
   void addComponentsToPanel() {
      if (isUsingMenuForType) {
         searchCompPanel.add(typeButton);
      } else {
         searchCompPanel.add(typesCombo);
      }
      searchCompPanel.add(numberField);
      searchCompPanel.add(submitButton);
      searchCompPanel.setTableWidth("75%"); // an attempt at padding (for IE)
   }

   @Override
   public void handleMaskEvent(final boolean isMasked, final String tabOwner) {
      if ((tabOwner.equals(tabOwnerId)) && (submitButton != null)) {
         this.isMasked = isMasked;
         if ((!getSearchFieldValue().isEmpty()) && (!isMasked)) {
            submitButton.setEnabled(true);
         } else if ((getSearchFieldValue().isEmpty()) || (isMasked)) {
            submitButton.setEnabled(false);
         }
      }
   }
}
