/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.events.FailedEvent;
import com.ericsson.eniq.events.ui.client.events.SucessResponseEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Implements methods when response set by server. Using Event bus to pass on
 * the response
 * 
 * (There is only one EventBus)
 * 
 * @author eeicmsy
 * @since Feb 2010
 */
public class RequestCallbackImpl implements RequestCallback {

    private static final String CONTACT_SYS_ADMIN ="If issue persists contact system administrator";

    private static final String SERVER_ERROR_CONTACT_SYS_ADMIN = "server error. " +CONTACT_SYS_ADMIN;

    private static final String SERVER_ERROR_RESOURCE_NOT_FOUND = "server error, resource not found.  " +CONTACT_SYS_ADMIN;

    private static final String UNEXPECTED_SERVER_RESPONSE = "Unexpected server response. Please login again.  " +CONTACT_SYS_ADMIN;

    public static class RequestCallbackException extends Exception {

    public RequestCallbackException(final String s) {
      super(s);
    }

  }

  private String requestData = null;

  private final MultipleInstanceWinId multiWinID;

  protected final EventBus eventBus;

  /* by the fact that this is being created means call is on-going */
  private boolean isServerCallOnGoing = true;

  /**
   * Construct. The queryId and tab id uniquely identify windows, to send
   * reponses to.
   * 
   * @param multiWinID
   *          - id of window been updated - can contain multi-instance window
   *          information
   * @param eventBus
   *          - (the singleton)
   * @param requestData
   *          - Optional - pass in request parameter data (can be null)
   */
  public RequestCallbackImpl(final MultipleInstanceWinId multiWinID, final EventBus eventBus, final String requestData) {
    this.eventBus = eventBus;
    this.requestData = requestData;
    this.multiWinID = multiWinID;
  }

  /**
   * Utility for passing request data without using Construct
   * 
   * @param text
   *          any test sent with the request
   */
  public void setRequestData(final String text) {
    this.requestData = text;
  }

  @Override
  public void onError(final Request request, final Throwable exception) {
    // Couldn't connect to server (could be
    // timeout, SOP violation, etc.)
    eventBus.fireEvent(new FailedEvent(multiWinID, requestData, exception));
    isServerCallOnGoing = false;
  }

  @Override
  public void onResponseReceived(final Request request, final Response response) {
      final int responseCode = response.getStatusCode();

      switch (responseCode) {
          case Response.SC_OK:  //200 - response OK
              if(!response.getText().contains(CommonConstants.LOGIN)){   //valid response but session has timed out
                  eventBus.fireEvent(new SucessResponseEvent(multiWinID, requestData, response));  }
              else  {
                  eventBus.fireEvent(new FailedEvent(multiWinID, requestData, new RequestCallbackException(Constants.LOGIN_AGAIN)));
              }
              break;

          case Response.SC_SERVICE_UNAVAILABLE:  //503
              eventBus.fireEvent(new FailedEvent(multiWinID, requestData, new RequestCallbackException(responseCode + " " + SERVER_ERROR_CONTACT_SYS_ADMIN)));
              break;

          case Response.SC_INTERNAL_SERVER_ERROR: //500
              eventBus.fireEvent(new FailedEvent(multiWinID, requestData, new RequestCallbackException(responseCode + " " + SERVER_ERROR_CONTACT_SYS_ADMIN)));
              break;

          case Response.SC_NOT_FOUND: //404
              eventBus.fireEvent(new FailedEvent(multiWinID, requestData, new RequestCallbackException(responseCode + " " + SERVER_ERROR_RESOURCE_NOT_FOUND)));
              break;

          default: //anything else!    we have only encountered "0" or "Aborted", as a result of glassfish restarting in terms of unexpected
              eventBus.fireEvent(new FailedEvent(multiWinID, requestData, new RequestCallbackException(UNEXPECTED_SERVER_RESPONSE)));
              break;
      }

      isServerCallOnGoing = false;
  }

}
