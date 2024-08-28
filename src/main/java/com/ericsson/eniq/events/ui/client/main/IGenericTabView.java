/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.main;

import java.util.List;

import com.ericsson.eniq.events.ui.client.common.comp.MenuTaskBar;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.DashBoardDataType;
import com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;

/**
 * Generic tab view interface. There is one of these for each tab.
 *
 * @author eeicmsy
 * @since Feb 2010
 */
public interface IGenericTabView extends WidgetDisplay {

   /**
    * Getter for area where windows get displayed for tab
    * This should also be the constrain area for window dragging,
    * (i.e. should not include taskbar)
    *
    * @return Content panel for tab
    */
   ContentPanel getCenterPanel();

   /**
    * Custom taskbar. Holds menu items for the tab. Has
    * area where windows can mimise to (as buttons).
    * Has reference to search field.
    *
    * @return custom taskbar for tab
    */
   MenuTaskBar getMenuTaskBar();

   /**
    * Create dahsboard component information for tab
    * (via meta data)
    *
    * @return Dashboard information for tab or null if no dashboard for tab
    */
   DashBoardDataType getDashBoardData();

   /**
    * Get current licences in force for circuit swith and packet switch
    *
    * @return CS or PS or CS,PS
    */
   List<String> getAllCurrentLicencesVoiceData();

   /** @return the window container */
   ContentPanel getWindowContainer();

   /** @return the container tab item */
   TabItem getTabItem();

   /** @param windowContainer  */
   void setWindowContainer(ContentPanel windowContainer);

   void handleMetaDataUpdate();

   DashBoardDataType getDashBoardDataLTE();
}