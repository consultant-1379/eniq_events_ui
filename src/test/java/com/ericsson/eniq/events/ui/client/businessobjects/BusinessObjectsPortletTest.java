/*
 * *
 *  * -----------------------------------------------------------------------
 *  *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 *  * -----------------------------------------------------------------------
 *
 */

package com.ericsson.eniq.events.ui.client.businessobjects;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.ui.client.businessobjects.resources.BusinessObjectsResourceBundle;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.web.bindery.event.shared.EventBus;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;
import static org.mockito.Mockito.*;

/**
 * @author ekurshi
 * @since 2011
 */
public class BusinessObjectsPortletTest {
    private static final String BIS_SERVICE_SUCCESS = "bisServiceSuccess";

    private BusinessObjectsPortlet objToTest;

    private JsonObjectWrapper metaData;

    private FlowPanel imagePanel;

    private Image image;

    @BeforeClass
    public static void init() {
        GWTMockUtilities.disarm();
    }

    @Before
    public void setUp() throws Exception {
        final BusinessObjectsResourceBundle resourceBundle = mock(BusinessObjectsResourceBundle.class);
        imagePanel = mock(FlowPanel.class);
        objToTest = new BusinessObjectsPortletStub(mock(EventBus.class), resourceBundle,
                mock(BusinessObjectsPresenter.class));
        metaData = mock(JsonObjectWrapper.class);
        image = mock(Image.class);
    }

    @Test
    public void testUpdateWithLoginSuccess() throws Exception {
        final JSONObject data = createDummyData();
        when(metaData.getString(BIS_SERVICE_SUCCESS)).thenReturn("true");
        doNothing().when(imagePanel).clear();
        doNothing().when(imagePanel).add(image);
        objToTest.update(data, null, null);
        verify(imagePanel).clear();
        verify(imagePanel).add(image);
    }

    @Test
    public void testUpdateWithLoginFail() throws Exception {
        final JSONObject data = createDummyData();
        when(metaData.getString(BIS_SERVICE_SUCCESS)).thenReturn("false");
        doNothing().when(imagePanel).clear();
        when(metaData.getString(CommonConstants.ERROR_DESCRIPTION)).thenReturn("Connection Failure");
        objToTest.update(data, null, null);
        verify(imagePanel).clear();
        verifyNoMoreInteractions(imagePanel);
    }

    private JSONObject createDummyData() {
        return mock(JSONObject.class);
    }

    class BusinessObjectsPortletStub extends BusinessObjectsPortlet {

        public BusinessObjectsPortletStub(final EventBus eventBus, final BusinessObjectsResourceBundle resourceBundle,
                final BusinessObjectsPresenter businessObjectsPresenter) {
            super(eventBus, resourceBundle, businessObjectsPresenter);
        }

        @Override
        void injectResources(final BusinessObjectsResourceBundle resourceBundle) {
        }

        @Override
        JsonObjectWrapper createMetaData(final JSONValue data) {
            return metaData;
        }

        @Override
        FlowPanel createImagePanel() {
            return imagePanel;
        }

        @Override
        Image getPortletImage() {
            return image;
        }

    }
}
