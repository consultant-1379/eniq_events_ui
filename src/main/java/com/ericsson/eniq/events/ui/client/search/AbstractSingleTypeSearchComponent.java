/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import com.ericsson.eniq.events.widgets.client.button.ImageButton;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.events.GroupSingleToggleEvent;
import com.ericsson.eniq.events.ui.client.events.GroupSingleToggleEventHandler;
import com.ericsson.eniq.events.ui.client.events.MaskEvent;
import com.ericsson.eniq.events.ui.client.events.MaskEventHandler;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.ui.client.resources.EniqResourceBundle;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import java.util.ArrayList;
import java.util.List;

import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_ENTER;

/**
 * Abstract class to inherit for plain search fields
 * e.g. number and string types in single text box or number field
 *
 * @author eeicmsy
 * @since March 2010
 */
public abstract class AbstractSingleTypeSearchComponent implements ISearchComponent, GroupSingleToggleEventHandler,
        MaskEventHandler {

   /** Default GXT events bus */
   protected EventBus eventBus;

   /**
    * Tab owner of search field
    * (need to be known when listening to
    * event bus)
    */
   protected String tabOwnerId;

   /**
    * String to use to specify what is being selected in query, e.g.
    * "node", "TAC", "IMSI" etc.
    */
   protected String paramString;

   /** Listener on search field input */
   protected SearchFieldUpDatedListener searchFieldSelectionMadeListener = new SearchFieldUpDatedListener();

   protected MouseListener mouseListener = new MouseListener();

   /**
    * boolean used to track whether UI is masked - set in handleMaskEvent method and used in handleSubmitButtonEnabling method
    * default access because of junit StringTypeSearchComponent
    */
   boolean isMasked;

   /** e.g. "CS" or "PS" or empty */
   private String metaDataRef = EMPTY_STRING;

   /*
   * Handler to submit search field input to server
   * for query calls
   */
   private final List<ISubmitSearchHandler> searchSubmitHandlers = new ArrayList<ISubmitSearchHandler>();

   /*
   * add submit button for IMSI (numbers) too.
   *
   * default access for JUnit
   */
   ImageButton submitButton;

   protected final EniqResourceBundle eniqResourceBundle = MainEntryPoint.getInjector().getEniqResourceBundle();

   /*
   * Cache handlers this component adds to eventbus - for removal later
   */
   private final List<HandlerRegistration> registoredHandlers = new ArrayList<HandlerRegistration>();

   /**
    * Tab owner passed to all components to enable listening
    * to specific event bus events
    *
    * @param tabOwnerId owner of search field component
    */
   public AbstractSingleTypeSearchComponent(final String tabOwnerId) {
      this.tabOwnerId = tabOwnerId;
   }

   /**
    * Support template pattern style implementation of
    * required #getSearchComponentValue.
    * i.e. keeping access local
    *
    * @return String in search field
    */
   abstract String getSearchFieldString();

   @Override
   public void addSubmitSearchHandler(final ISubmitSearchHandler searchSubmitHandler) {
      searchSubmitHandlers.add(searchSubmitHandler);
   }

   @Override
   public void removeSubmitSearchHandler(final ISubmitSearchHandler searchSubmitHandler) {
      searchSubmitHandlers.remove(searchSubmitHandler);

      if (searchSubmitHandlers.isEmpty()) {
         unregistorWithEventBus();
      }
   }

   @Override
   public void registerWithEventBus(final EventBus eventBus) {
      this.eventBus = eventBus;
      /* support hiding component in favour of group component */
      if (this.eventBus != null) {
         registoredHandlers.add(this.eventBus.addHandler(GroupSingleToggleEvent.TYPE, this));
         registoredHandlers.add(this.eventBus.addHandler(MaskEvent.TYPE, this));
      }
   }

   @Override
   public void unregistorWithEventBus() {
      for (final HandlerRegistration handler : registoredHandlers) {
         handler.removeHandler();
      }
   }

   @Override
   public void setLicencedTypesVisibleOnly(final List<String> licencedTypes) {
      // not applicable when no type (as in paired search field)

   }

   @Override
   public SearchFieldDataType getSearchComponentValue() {
      final String val = getSearchFieldString();
      if (val != null) {
         final String[] urlParms = new String[]{(paramString + val)};
         return new SearchFieldDataType(val, urlParms, null, null, false, EMPTY_STRING, null, false);
      }
      return null;
   }

   @Override
   public String getMetaChangeComponentRef() {
      return metaDataRef;
   }

   @Override
   public void setMetaChangeComponentRef(final String metaDataRef) {
      this.metaDataRef = metaDataRef;

   }

   /**
    * The IMSI (number) search field does not really need a submit
    * button as can ENTER press directly but adding one so as not to confuse when
    * have group selection
    *
    * @param submitButtonToolTip - tooltip for submit button (from meta data)
    *
    * @return submitButton for single type search component (e.g. number field)
    */
   protected ImageButton getSubmitButton(final String submitButtonToolTip) {

      submitButton = createButton();
      final ClickHandler submitSearchFieldButtonListener = new SubmitSearchFieldButtonListener();
      submitButton.setTitle(submitButtonToolTip);
      submitButton.addClickHandler(submitSearchFieldButtonListener);
      submitButton.setEnabled(false);
      submitButton.getElement().getStyle().setPaddingTop(3, Unit.PX);
      return submitButton;
   }

   /**
    * Enter press Listener on search field driving
    * full submit call being made to server
    * (i.e. not suitable for live load scenario where
    * key sensitive calls SQL calls are being made.
    * <p/>
    * (visibility increased for junit)
    */
   class SearchFieldUpDatedListener extends KeyListener {

      @Override
      public void componentKeyUp(final ComponentEvent event) {

         handleEnterPress(event.getKeyCode());

         handleSubmitButtonEnabling();
      }

      /* extracted for junit  : added and exposed for junit coverage */
      void handleEnterPress(final int keyCode) {
         if (keyCode == KEY_ENTER && !isMasked) {
            for (final ISubmitSearchHandler searchSubmitHandler : searchSubmitHandlers) {
               searchSubmitHandler.submitSearchFieldInfo();
            }

         }
      }
   }

   /**
    * Listener for Mouse Paste Click event.
    * To enable the Submit/Play button on Paste.
    */
   class MouseListener implements Listener<ComponentEvent> {
      @Override
      public void handleEvent(final ComponentEvent be) {
         if (!isMasked) {
            submitButton.setEnabled(true);
         }
      }
   }

   /** user selects submit button */
   class SubmitSearchFieldButtonListener implements ClickHandler {
      @Override
      public void onClick(final ClickEvent event) {
         for (final ISubmitSearchHandler searchSubmitHandler : searchSubmitHandlers) {
            searchSubmitHandler.submitSearchFieldInfo();
         }
         handleSubmitButtonEnabling();
      }
   }

   private void handleSubmitButtonEnabling() {
      final String searchString = getSearchFieldString();
      final boolean searchStringLengthZero = (searchString == null) ? true : searchString.length() == 0;

      if (submitButton != null) {

         if ((!searchStringLengthZero) && (!isMasked)) {
            submitButton.setEnabled(true);
         } else if ((searchStringLengthZero) || (isMasked)) {
            submitButton.setEnabled(false);
         }
      }
   }

   @Override
   public void handleMaskEvent(final boolean isMasked, final String tabOwner) {
      if (tabOwner.equals(tabOwnerId)) {
         this.isMasked = isMasked;
         handleSubmitButtonEnabling();
      }
   }

   /* extracted for junit */
   /*Button createButton() {
       return new Button();
   }*/

   ImageButton createButton() {
      return new ImageButton(eniqResourceBundle.launchIconToolbar());
   }

}
