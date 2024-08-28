/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.web.bindery.event.shared.EventBus;

import java.util.List;

/**
 * Interface for search field component to appear
 * in menu taskbar
 *
 * @author eeicmsy
 * @since March 2010
 */
public interface ISearchComponent {

   /**
    * Inject default event bus for purpose of
    * creating presenters etc
    *
    * @param eventBus
    */
   void registerWithEventBus(final EventBus eventBus);

   /**
    * when remove search component completly from UI
    * (as do for PS/CS switch} ensure not still receiving and firing
    */
   void unregistorWithEventBus();

   /**
    * Fetch component wigit
    *
    * @return search component to appear in the menu task bar
    */
   Component getSearchComponent();

   /**
    * Convert value in search field to a SearchFieldDataType
    * (i.e. including context information for url
    * (imsi or node etc)
    *
    * @return value in search component ready to pass to server
    *         as a parameter for a query
    */
   SearchFieldDataType getSearchComponentValue();

   /**
    * Pass implementor of ISubmitSearchHandler
    * methods into search component
    *
    * @param handler handler of ISubmitSearchHandler methods
    */
   void addSubmitSearchHandler(ISubmitSearchHandler searchSubmitHandler);

   /**
    * Remove handler (which will need if have other hanlder beside the
    * main menu task bar itself
    *
    * @param searchSubmitHandler handle to remove
    */
   void removeSubmitSearchHandler(ISubmitSearchHandler searchSubmitHandler);

   /**
    * Because we have changed code so that only the search field
    * or the group component is visible at the one time
    * (previously when both components were visible
    * the direct typing into searchComp took precedence over group),
    * the component to take precedence is the one which is currently visible
    *
    * @return true if the component is considered visible
    */
   boolean isVisible();

   /**
    * Because introduced Circuit Switched and Packet Switch windows on the same
    * taskbar but do not want search field changes for PS to affect CS windows and vice versa,
    * then we will need a unique constant id for the search field (to change when switch from PS to CS)
    *
    * @return id for search field which should be immutable
    */
   String getMetaChangeComponentRef();

   /**
    * Setter to make search field submit handler not say update an
    * APN on a circuit swithed event analysis window
    *
    * @param metaDataRef CS or PS
    */
   void setMetaChangeComponentRef(final String metaDataRef);

   /**
    * Introducing for dashboard ()No Voice-Data toggle present on the tab)
    * Hide search field types (APN, MSC, etc) if they are not suitable for available licences
    * No Voice-Data toggle present on the tab - but still using the MetaDataChangeComponent
    * information for licences as that part of JsonObjectWrapper services writes to for CS and PS licences
    *
    * @param licencedTypes String list of available licences (CS or PS - i.e. MSS or data - circuit switched, packet switched)
    */
   void setLicencedTypesVisibleOnly(List<String> licencedTypes);

}
