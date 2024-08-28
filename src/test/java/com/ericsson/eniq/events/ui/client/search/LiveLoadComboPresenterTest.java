/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import static org.junit.Assert.*;
import com.google.web.bindery.event.shared.EventBus;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.JsonPagingLoadResultReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.ScriptTagProxy;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.store.ListStore;

/**
 * @author eeicmsy
 * @since March 2010
 */
public class LiveLoadComboPresenterTest extends TestEniqEventsUI {

    private StubbedLiveLoadComboPresenter objtoTest;

    ILiveLoadComboView mockedView;

    ScriptTagProxy<PagingLoadResult<ModelData>> mockedScriptTagProxy;

    JsonPagingLoadResultReader<PagingLoadResult<ModelData>> mockedReader;

    PagingLoader<PagingLoadResult<ModelData>> mockedLoader;

    ListStore mockedStore1;

    /* NOTE  context.mock(ComboBox.class) "CAN NOT BE DONE" - which is whole 
     * reason for splitting out the interface to not just return ComboBox direct.
     */

    @Before
    public void setUp() {

        mockedView = context.mock(ILiveLoadComboView.class);

        mockedScriptTagProxy = context.mock(ScriptTagProxy.class);
        mockedReader = context.mock(JsonPagingLoadResultReader.class);
        mockedLoader = context.mock(PagingLoader.class);
        mockedStore1 = context.mock(ListStore.class);
        //XXXXmockedListView = context.mock(ListView.class);

        //NOTE  com.extjs.gxt.ui.client.widget.ListView  fails to be mocked

        objtoTest = new StubbedLiveLoadComboPresenter(mockedView, mockedEventBus);

    }

    private void setupExceptionsOnSetupLiveLoadFirstTime() {

        context.checking(new Expectations() {
            {
                one(mockedLoader).setRemoteSort(true);
                one(mockedLoader).setSortField(with(any(String.class)));
                one(mockedLoader).addListener(with(any(EventType.class)),
                        with(any(LiveLoadComboPresenter.LiveLoadListener.class)));
                one(mockedLoader).addLoadListener(with(any(LoadListener.class)));
                // one(mockedView).getLiveLoadCombo();   // NO CAN NOT DO

                one(mockedView).setDisplayField("id");
                one(mockedView).getStore();
                will(returnValue(null));
                one(mockedView).setStore(with(any(ListStore.class)));
            }
        });

    }

    private void setupExceptionsOnSetupLiveLoadChangeStore() {

        context.checking(new Expectations() {
            {
                one(mockedLoader).setRemoteSort(true);
                one(mockedLoader).setSortField(with(any(String.class)));
                one(mockedLoader).addListener(with(any(EventType.class)),
                        with(any(LiveLoadComboPresenter.LiveLoadListener.class)));
                one(mockedLoader).addLoadListener(with(any(LoadListener.class)));

                one(mockedView).setDisplayField("id");
                one(mockedView).getStore();
                will(returnValue(mockedStore1));

                exactly(1).of(mockedView).getPagingToolBar();
                one(mockedLoader).load(with(any(BasePagingLoadConfig.class)));

                allowing(mockedView).setStore(with(any(ListStore.class)));
                // ListView is unmockable here

            }
        });

    }

    @After
    public void tearDown() {
        objtoTest = null;

    }

    @Test
    public void setupLiveLoadFirstTime() {
        setupExceptionsOnSetupLiveLoadFirstTime();
        objtoTest.setupLiveLoad("some fake url", "SGSN");
        assertEquals("Called listview (even if can't mock it", false, objtoTest.isListViewCalled);
    }

    @Test
    public void setupsetupLiveLoadChangeStore() {
        setupExceptionsOnSetupLiveLoadChangeStore();
        objtoTest.setupLiveLoad("some fake url", "SGSN");
        assertEquals("Not Calling ListView since GXT 2.2.4", false, objtoTest.isListViewCalled);
    }

    private class StubbedLiveLoadComboPresenter extends LiveLoadComboPresenter {

        private boolean isListViewCalled;

        /**
         * @param display
         * @param eventBus
         */
        public StubbedLiveLoadComboPresenter(final ILiveLoadComboView display, final EventBus eventBus) {
            super(display, eventBus);
        }

        @Override
        String getMaxRowsProperty(final String propertyKey) {
            return "5000";
        }

        @Override
        public ScriptTagProxy<PagingLoadResult<ModelData>> createScriptTagProxy(final String url) {
            return mockedScriptTagProxy;
        }

        @Override
        public PagingLoader<PagingLoadResult<ModelData>> createPagingLoader() {
            return mockedLoader;
        }

        @Override
        public JsonPagingLoadResultReader<PagingLoadResult<ModelData>> createJsonPagingLoadResultReader(
                final ModelType type) {
            return mockedReader;
        }

        @Override
        public void resetListView(final ListStore<ModelData> store) {
            isListViewCalled = true;
        }

    }

}
