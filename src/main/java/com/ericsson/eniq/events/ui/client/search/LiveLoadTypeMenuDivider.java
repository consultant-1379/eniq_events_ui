package com.ericsson.eniq.events.ui.client.search;


import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class LiveLoadTypeMenuDivider extends Component {

    public LiveLoadTypeMenuDivider() {
        getAriaSupport().setPresentation(true);
  }

  @Override
  protected void onRender(final Element target, final int index) {
      final Element span = DOM.createSpan();
      span.addClassName("x-menu-sep");
      span.setInnerHTML("&#160;");

      target.addClassName("x-menu-sep-li");

      setElement(span, target, index);
  }

}
