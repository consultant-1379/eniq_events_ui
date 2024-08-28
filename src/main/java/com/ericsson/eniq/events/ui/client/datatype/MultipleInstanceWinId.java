/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.datatype;

import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;

public class MultipleInstanceWinId {

   private final String winId;

   private final String tabId;

   private SearchFieldDataType searchInfo;

   /**
    * Construct MultipleInstanceWinId typically at window launch
    *
    * @param tabId      - current tab where window resides
    * @param winId      - same as query id (id which can be found in meta data)
    */
   public MultipleInstanceWinId(final String tabId, final String winId) {
      this.tabId = tabId;
      this.winId = winId;
      this.searchInfo = searchInfo;
   }

   /**
    * @param multiWinId                a window id to check
    *
    * @return true if this MultipleInstanceWinId is from the
    *         same window as the input parameter
    */
   public boolean isThisWindowGuardCheck(final MultipleInstanceWinId multiWinId) {
      if (!tabId.equals(multiWinId.getTabId())) {
         return false;
      }

      if (!winId.equals(multiWinId.getWinId())) {
         return false;
      }
      return true;
   }

   /**
    * Return ID used in meta data to identify the window and menu item
    *
    * @return winID sames as queryId in BaseWindowPresenter
    */
   public String getWinId() {
      return winId;
   }

   /**
    * Tab Owner ID from meta data, tab where window was launched from
    * to ensure only call back that window
    *
    * @return tabOwner id to support same window (winId) being used across differnet tabs
    */
   public String getTabId() {
      return tabId;
   }

   /**
    * Search field data associated with the window
    * used to create unique id for "multi-instance windows"
    *
    * @return search data associated with the window (as apposed to
    *         search data currently in the menu task bar search field component)
    */
   public SearchFieldDataType getSearchInfo() {
      return searchInfo;
   }

   /**
    * Extremely unfortunate immutablity loss
    * Setter on search info due to
    * drilldowns, etc changing search data for window
    * <p/>
    * @param searchInfo
    */
   public void setSearchInfo(final SearchFieldDataType searchInfo) {
      this.searchInfo = searchInfo;
   }

   public String generateCompositeId() {
       int searchFieldHashCode = -1;
       if (searchInfo != null) {
          searchFieldHashCode = searchInfo.hashCode();
       }
       return tabId + winId + ((searchFieldHashCode != -1) ? searchFieldHashCode : EMPTY_STRING);
    }

   /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
   @Override
   public int hashCode() {
      // don't really use in sets - compoiste has to be run time for drilldown search field change
      final String compositeId = generateCompositeId();
      final int prime = 31;
      int result = 1;
      result = prime * result + ((compositeId == null) ? 0 : compositeId.hashCode());
      return result;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
    @Override
    public boolean equals(final Object obj) {
       if (this == obj) {
          return true;
       }
       if (obj == null) {
          return false;
       }
       if (!(obj instanceof MultipleInstanceWinId)) {
          return false;
       }
       final MultipleInstanceWinId other = (MultipleInstanceWinId) obj;
       final String compositeId = generateCompositeId();
       final String orherCompositeId = other.generateCompositeId();
       if (compositeId == null) {
          if (orherCompositeId != null) {
             return false;
          }
       } else if (!compositeId.equals(orherCompositeId)) {
          return false;
       }
       return true;
    }
}
