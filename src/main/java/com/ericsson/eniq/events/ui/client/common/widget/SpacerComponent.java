/*
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.common.widget;

import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class SpacerComponent extends Component {

    private final int width;

    public SpacerComponent(final int width) {
        this.width = width;
        getAriaSupport().setPresentation(true);
  }

  @Override
  protected void onRender(final Element target, final int index) {
      final Element div = DOM.createDiv();
      div.getStyle().setWidth(width, Style.Unit.PX);

      setElement(div, target, index);
  }

}
