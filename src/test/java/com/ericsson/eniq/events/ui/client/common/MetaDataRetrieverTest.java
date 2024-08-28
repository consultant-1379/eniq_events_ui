/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.common;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Map;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.events.MetaDataReadyEvent;
import com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.web.bindery.event.shared.EventBus;

public class MetaDataRetrieverTest extends TestEniqEventsUI {

    private static final String ENIQ_EVENTS_SERVICES_BASE_URI = "baseURI/";

    Request mockedRequest;

    Throwable mockedThrowbale;

    String metaDataJson = "the meta data from the services layer";

    String errorResponseString = "Licensing Error:No Valid License!";

    JSONObject mockedJSONObject;

    WidgetDisplay mockedWidgetDisplay;

    ServerComms mockedServerComms;

    Response mockedResponse;

    MessageDialog mockedMessageDialog;

    @Before
    public void setUpTestPropertiesAndMocks() {
        mockedEventBus = context.mock(EventBus.class, "another event bus");

        mockedRequest = context.mock(Request.class);
        mockedThrowbale = context.mock(Throwable.class);
        mockedMessageDialog = context.mock(MessageDialog.class);
        mockedServerComms = context.mock(ServerComms.class);

        mockedJSONObject = context.mock(JSONObject.class);
        mockedWidgetDisplay = context.mock(WidgetDisplay.class);
        mockedResponse = context.mock(Response.class);
    }

    @Test
    //cannot test that onResponseReceived actually sets the meta data in the MetaReader class without
    //introducing a new method just for use from a test class (due to problems with testing gwt/gxt stuff)
    public void testOnResponseReceivedFiresEventReadyEvent() throws Exception {

        expectUseOfServerComms();
        final StubbedMetaDataRetriever metaDataRetriever = new StubbedMetaDataRetriever(mockedEventBus, "METADATA/UI");

        expectGetStatusCodeAndGetTextOnSuccessfulHttpResponse(mockedResponse);
        expectFireEventOnEventBus();
        metaDataRetriever.onResponseReceived(null, mockedResponse);
    }

    @Test
    public void testErrorMessageComposition_NullException_Response() throws Exception {

        expectUseOfServerComms();
        final StubbedMetaDataRetriever metaDataRetriever = new StubbedMetaDataRetriever(mockedEventBus, "METADATA/UI");

        expectGetStatusCode_500_NoValidLicenseResponse();
        final Map<String, String> result = metaDataRetriever.getTitleAndMessage(null, mockedResponse);

        assertEquals("Didn't Expect This title", "Licensing Error", result.get("title"));
        assertEquals("Didn't Expect This message", "No Valid License!", result.get("message"));
    }

    @Test
    public void testErrorMessageComposition_Exception_NullResponse() throws Exception {

        expectUseOfServerComms();
        final StubbedMetaDataRetriever metaDataRetriever = new StubbedMetaDataRetriever(mockedEventBus, "METADATA/UI");

        expectGetStatusCode_500_NoValidLicenseResponse();
        final Map<String, String> result = metaDataRetriever.getTitleAndMessage(new Exception("Some Message"), null);

        assertEquals("Didn't Expect This title", "Failure receiving initial meta data from server!", result.get("title"));
        assertEquals("Didn't Expect This message", "Some Message", result.get("message"));
    }

    @Test
    public void testErrorMessageComposition_NullException_NoTitleInResponse() throws Exception {

        errorResponseString = "Some response with no title set";
        expectUseOfServerComms();
        final StubbedMetaDataRetriever metaDataRetriever = new StubbedMetaDataRetriever(mockedEventBus, "METADATA/UI");

        expectGetStatusCode_500_NoValidLicenseResponse();
        final Map<String, String> result = metaDataRetriever.getTitleAndMessage(null, mockedResponse);

        assertEquals("Didn't Expect This title", "HTTP CODE 500", result.get("title"));
        assertEquals("Didn't Expect This message", "Some response with no title set", result.get("message"));
    }

    private void expectFireEventOnEventBus() {
        context.checking(new Expectations() {
            {
                one(mockedEventBus).fireEvent(with(any(MetaDataReadyEvent.class)));

            }
        });
    }

    private void expectUseOfServerComms() {
        context.checking(new Expectations() {
            {
                one(mockedServerComms).makeSerialServerRequest(createMultipleInstanceWinId("", ""), "baseURI/METADATA/UI", "");
            }
        });

    }

    private final MultipleInstanceWinId createMultipleInstanceWinId(final String tabId, final String winID) {
        return new MultipleInstanceWinId(tabId, winID);
    }

    private void expectGetStatusCodeAndGetTextOnSuccessfulHttpResponse(final Response mockedResponse) {
        context.checking(new Expectations() {
            {
                one(mockedResponse).getStatusCode();
                will(returnValue(Constants.STATUS_CODE_OK));
                one(mockedResponse).getText();
                will(returnValue(metaDataJson));

            }
        });

    }

    private void expectGetStatusCode_500_NoValidLicenseResponse() {
        context.checking(new Expectations() {
            {
                allowing(mockedResponse).getText();
                will(returnValue(errorResponseString));//"Licensing Error:No Valid License!"));
                allowing(mockedResponse).getStatusCode();
                will(returnValue(Response.SC_INTERNAL_SERVER_ERROR));
            }
        });
    }

    @Test
    public void testCallingForMetaData() {
        expectUseOfServerComms();
        new StubbedMetaDataRetriever(mockedEventBus, "METADATA/UI");
    }

    class StubbedMetaDataRetriever extends MetaDataRetriever {

        StubbedMetaDataRetriever(final EventBus eventBus, final String metaDataPath) {
            super(eventBus, metaDataPath);
        }

        @Override
        String getEniqEventsServicesURIFromReadLoginSessionProperties() {
            return ENIQ_EVENTS_SERVICES_BASE_URI;
        }

        @Override
        public ServerComms getServerCommHandler() {
            return mockedServerComms;
        }

        @Override
        JsonObjectWrapper createMetaDataObject(final String jsonMetadata) {
            assertThat(jsonMetadata, is(metaDataJson));
            return null;
        }

        void displayMetaServerError(final Throwable ex, final Response response) {
            final int q = 0;
        }

        @Override
        protected boolean userPreferencesAreReady() {
            return true;
        }
    }
}
