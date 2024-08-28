/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.component.einputbox;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;

/**
 * @author egallou
 * @since 2011
 *
 */
public interface EInputBoxResources extends ClientBundle {

    @Source({ "EInputBox.css", "EInputBox-light.css" })
    @NotStrict
    CssResource css();

}