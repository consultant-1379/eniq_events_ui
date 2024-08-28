/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.mvp;

import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public class LoginService {

    /**
     * @param userId
     * @param password
     * @return
     */
    public void validateUser(final String userId, final String password, final RequestCallback callback)
            throws RequestException {
        final RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, RootPanel.get("validateUserRequestUri")
                .getElement().getInnerText());
        rb.setHeader("Content-Type", "application/x-www-form-urlencoded");
        rb.setRequestData("username=" + URL.encode(userId) + "&password=" + URL.encode(password));
        rb.setCallback(callback);
        rb.send();
    }

    public void authenticate(final String userId, final String password) {
        InputElement.as(RootPanel.get("username").getElement()).setValue(userId);
        InputElement.as(RootPanel.get("password").getElement()).setValue(password);
        final FormElement form = FormElement.as(RootPanel.get("hiddenLogin").getElement());
        form.submit();
    }

    /**
     * @param userId
     * @param password
     * @param newPassword
     * @return
     */
    public void changePassword(final String userId, final String password, final String newPassword,
            final RequestCallback callback) throws RequestException {
        final RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, RootPanel.get("changePasswordRequestUri")
                .getElement().getInnerText());
        rb.setHeader("Content-Type", "application/x-www-form-urlencoded");
        rb.setRequestData("existingUserName=" + URL.encode(userId) + "&existingUserPassword=" + URL.encode(password)
                + "&newUserPassword=" + URL.encode(newPassword));
        rb.setCallback(callback);
        rb.send();
    }

}
