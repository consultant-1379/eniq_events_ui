/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.component;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class ImageTextButton extends Button {
    private String text;

    private final Element textElement;

    public ImageTextButton() {
        super();
        textElement = DOM.createElement("span");
        textElement.setAttribute("style", "padding-left:20px; vertical-align:middle;");
        DOM.appendChild(getElement(), textElement);
    }

    public void setResource(ImageResource imageResource) {
        Image img = new Image(imageResource);
        String definedStyles = img.getElement().getAttribute("style");
        img.getElement().setAttribute("style", definedStyles + "; vertical-align:middle;");
        DOM.insertBefore(getElement(), img.getElement(), DOM.getFirstChild(getElement()));
    }

    @Override
    public void setText(String text) {
        this.text = text;
        textElement.setInnerText(text);
    }

    @Override
    public String getText() {
        return this.text;
    }
}