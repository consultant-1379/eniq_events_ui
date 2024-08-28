/*
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.northpanel.button;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface EniqOptionsMenuResources extends ClientBundle {

   @Source("arrow.png")
   ImageResource arrow();

   interface EniqOptionsMenuCss extends CssResource {

      String open();

      String optionsMenu();
   }

   @Source("EniqOptionsMenu.css")
   EniqOptionsMenuCss css();

}
