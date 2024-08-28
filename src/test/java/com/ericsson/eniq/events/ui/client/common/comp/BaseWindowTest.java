package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.service.WindowManager;
import com.ericsson.eniq.events.ui.client.common.widget.EniqWindow;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test for {@link com.ericsson.eniq.events.ui.client.common.comp.BaseWindow}.
 *
 * @author ealeerm
 * @since Jun 11, 2012
 */
@RunWith(MockitoJUnitRunner.class)
public class BaseWindowTest extends TestEniqEventsUI {

    BaseWindow window;

    @Mock
    MultipleInstanceWinId mockMultiWinId;

    @Mock
    ContentPanel mockConstrainArea;

    @Mock
    EventBus mockEventBus;

    @Mock
    WindowState mockWindowState;

    @Mock
    WindowManager mockWindowManager;

    @Mock
    EniqWindow mockEniqWindow;

    @Before
    public void setUp() {
        window = new TestBaseWindow("[titleBase]", "[icon]", true, mockWindowState);
        window = spy(window);
        when(window.createWindowManagerForTab()).thenReturn(mockWindowManager);
    }

    @After
    public void tearDown() {
        window = null;
    }

    /**
     *
     */
    @Test
    public void baseWindowTestConstructor() {
        assertEquals("[titleBase]", window.titleBase);
        assertEquals("[titleBase]", window.fullTitle);
        assertNull(window.titlePrePrefix);
        assertNull(window.titlePrefix);
    }

  /*  *//**
     * Tested method: {@link BaseWindow#updateSearchFieldDataType(SearchFieldDataType searchInfo)}
     *//*
    @Test
    public void updateSearchFieldDataType_TitlesAreNull_ReturnsFalse() {
        SearchFieldDataType oldMockDataType = mock(SearchFieldDataType.class);
        SearchFieldDataType newMockDataType = mock(SearchFieldDataType.class);

        // Recording and stubbing mock object(s) behaviour
        when(newMockDataType.isEmpty()).thenReturn(false);
        when(mockMultiWinId.getSearchInfo()).thenReturn(oldMockDataType);

        assertFalse(window.updateSearchFieldDataType(newMockDataType));

        assertEquals("[titleBase]", window.titleBase);
        assertEquals("[titleBase]", window.fullTitle);
        assertNull(window.titlePrePrefix);
        assertNull(window.titlePrefix);

        // Selective and explicit verification
        verify(mockMultiWinId).setSearchInfo(eq(newMockDataType));
        verify(mockEniqWindow, never()).updateTitle(anyString());
    }*/

  /*  *//**
     * Tested method: {@link BaseWindow#updateSearchFieldDataType(SearchFieldDataType searchInfo)}
     *//*
    @Test
    public void updateSearchFieldDataType_NotDrilledDownWindow_ReturnsTrue() {
        SearchFieldDataType oldMockDataType = mock(SearchFieldDataType.class);
        SearchFieldDataType newMockDataType = mock(SearchFieldDataType.class);

        // Recording and stubbing mock object(s) behaviour
        when(newMockDataType.isEmpty()).thenReturn(false);
        when(mockMultiWinId.getSearchInfo()).thenReturn(oldMockDataType);
        when(mockWindowState.isDrillDown()).thenReturn(false);
        when(newMockDataType.getSearchFieldVal()).thenReturn(" [SearchFieldVal]");
        when(newMockDataType.getTitlePrefix()).thenReturn(" \r\n [TitlePrefix] \t ");

        assertTrue(window.updateSearchFieldDataType(newMockDataType));

        assertEquals("[titleBase]", window.titleBase);
        assertEquals("[SearchFieldVal] - [TitlePrefix] - [titleBase]", window.fullTitle);
        assertEquals("[SearchFieldVal]", window.titlePrePrefix);
        assertEquals("[TitlePrefix]", window.titlePrefix);

        // Selective and explicit verification
        verify(mockMultiWinId).setSearchInfo(eq(newMockDataType));
        verify(mockEniqWindow).updateTitle(eq("[SearchFieldVal] - [TitlePrefix] - [titleBase]"));
    }
*/
 /*   *//**
     * Tested method: {@link BaseWindow#updateSearchFieldDataType(SearchFieldDataType searchInfo)}
     *//*
    @Test
    public void updateSearchFieldDataType_identicalSearchInfoArrived_ReturnsTrue() {
        SearchFieldDataType oldMockDataType = mock(SearchFieldDataType.class);

        // Recording and stubbing mock object(s) behaviour
        when(oldMockDataType.isEmpty()).thenReturn(false);
        when(mockMultiWinId.getSearchInfo()).thenReturn(oldMockDataType);
        when(mockWindowState.isDrillDown()).thenReturn(false);
        when(oldMockDataType.getSearchFieldVal()).thenReturn(" [SearchFieldVal]");
        when(oldMockDataType.getTitlePrefix()).thenReturn(" \r\n [TitlePrefix] \t ");

        assertTrue(window.updateSearchFieldDataType(oldMockDataType));

        assertEquals("[titleBase]", window.titleBase);
        assertEquals("[SearchFieldVal] - [TitlePrefix] - [titleBase]", window.fullTitle);
        assertEquals("[SearchFieldVal]", window.titlePrePrefix);
        assertEquals("[TitlePrefix]", window.titlePrefix);

        // Selective and explicit verification
        verify(mockMultiWinId, never()).setSearchInfo(eq(oldMockDataType));
        verify(mockEniqWindow).updateTitle(eq("[SearchFieldVal] - [TitlePrefix] - [titleBase]"));
    }
*/
   /* *//**
     * Tested method: {@link BaseWindow#updateSearchFieldDataType(SearchFieldDataType searchInfo)}
     *//*
    @Test
    public void updateSearchFieldDataType_emptyPrefixes_ReturnsFalse() {
        SearchFieldDataType oldMockDataType = mock(SearchFieldDataType.class);
        SearchFieldDataType newMockDataType = mock(SearchFieldDataType.class);

        // Recording and stubbing mock object(s) behaviour
        when(newMockDataType.isEmpty()).thenReturn(false);
        when(mockMultiWinId.getSearchInfo()).thenReturn(oldMockDataType);
        when(mockWindowState.isDrillDown()).thenReturn(false);
        when(newMockDataType.getSearchFieldVal()).thenReturn("");
        when(newMockDataType.getTitlePrefix()).thenReturn(" \r\n  \t ");

        assertFalse(window.updateSearchFieldDataType(newMockDataType));

        assertEquals("[titleBase]", window.titleBase);
        assertEquals("[titleBase]", window.fullTitle);
        assertNull(window.titlePrePrefix);
        assertNull(window.titlePrefix);

        // Selective and explicit verification
        verify(mockMultiWinId).setSearchInfo(newMockDataType);
        verify(mockEniqWindow, never()).updateTitle(anyString());
    }
*/
  /*  *//**
     * Tested method: {@link BaseWindow#updateSearchFieldDataType(SearchFieldDataType searchInfo)}
     *//*
    @Test
    public void updateSearchFieldDataType_newDataTypeIsNull_ReturnsFalse() {
        SearchFieldDataType oldMockDataType = mock(SearchFieldDataType.class);

        // Recording and stubbing mock object(s) behaviour
        when(mockMultiWinId.getSearchInfo()).thenReturn(oldMockDataType);

        assertFalse(window.updateSearchFieldDataType(null));

        // Selective and explicit verification
        verify(mockMultiWinId, never()).setSearchInfo(any(SearchFieldDataType.class));
        verify(mockEniqWindow, never()).updateTitle(anyString());
    }*/

  /*  *//**
     * Tested method: {@link BaseWindow#updateSearchFieldDataType(SearchFieldDataType searchInfo)}
     *//*
    @Test
    public void updateSearchFieldDataType_newDataTypeIsEmpty_ReturnsFalse() {
        SearchFieldDataType newMockDataType = mock(SearchFieldDataType.class);

        // Recording and stubbing mock object(s) behaviour
        when(newMockDataType.isEmpty()).thenReturn(true);

        assertFalse(window.updateSearchFieldDataType(newMockDataType));

        // Selective and explicit verification
        verify(mockMultiWinId, never()).setSearchInfo(any(SearchFieldDataType.class));
        verify(mockEniqWindow, never()).updateTitle(anyString());
    }*/

    /**
     * Tested method: {@link BaseWindow#getWindowState()}
     */
    @Test
    public void getWindowState_JustAfterCreate_ReturnsNotDrilledDownState() {
        WindowState windowState = window.getWindowState();
        assertEquals(0, windowState.getDrillDepth());
        assertFalse(windowState.isDrillDown());
    }

  /* *//**
    * Tested method: {@link BaseWindow#resetSearchData(SearchFieldDataType data)}
    *//*
    @Test
    public void resetSearchData_onNewDataTypeArrival() {
        SearchFieldDataType newMockDataType = mock(SearchFieldDataType.class);

        // Recording and stubbing mock object(s) behaviour

        window.resetSearchData(newMockDataType);

        assertEquals("[titleBase]", window.titleBase);
        assertEquals("[titleBase]", window.fullTitle);
        assertNull(window.titlePrePrefix);
        assertNull(window.titlePrefix);

        // Selective and explicit verification
        verify(mockMultiWinId,times(2)).setSearchInfo(eq(newMockDataType));
        verify(mockEniqWindow, never()).updateTitle(anyString());
    }*/

  /*  *//**
     * Tested method: {@link BaseWindow#resetSearchData(SearchFieldDataType data)}
     *//*
    @Test
    public void resetSearchData_onNewDataTypeArrivalWithNullInstanceType() {
        SearchFieldDataType newMockDataType = mock(SearchFieldDataType.class);

        window.resetSearchData(newMockDataType);

        assertEquals("[titleBase]", window.titleBase);
        assertEquals("[titleBase]", window.fullTitle);
        assertNull(window.titlePrePrefix);
        assertNull(window.titlePrefix);

        // Selective and explicit verification
        verify(mockMultiWinId,times(2)).setSearchInfo(eq(newMockDataType));
        verify(mockEniqWindow, never()).updateTitle(anyString());
    }*/

    /**
     * Tested method: {@link BaseWindow#putWindowToFront()}
     */
    @Test
    public void putWindowToFront_Usual() {
        window.putWindowToFront();

        assertEquals("[titleBase]", window.titleBase);
        assertEquals("[titleBase]", window.fullTitle);

        // Selective and explicit verification
        verify(mockEniqWindow).putWindowToFront();
    }

    /**
     * Tested method: {@link BaseWindow#addLaunchButton()}
     */
    @Test
    public void addLaunchButton_Usual() {
        // Recording and stubbing mock object(s) behaviour
        when(mockWindowManager.openWindow(eq(window), anyString(), anyString())).thenReturn(false);

        assertFalse(window.addLaunchButton());

        assertEquals("[titleBase]", window.titleBase);
        assertEquals("[titleBase]", window.fullTitle);

        // Selective and explicit verification
        verify(mockWindowManager, only()).openWindow(eq(window), eq("[titleBase]"), eq("[icon]"));
        verify(mockWindowManager, never()).closeWindow(eq(window), eq("[titleBase]"), eq("[icon]"));
    }

    /**
     * Tested method: {@link BaseWindow#removeLaunchButton()}
     */
    @Test
    public void removeLaunchButton_Usual() {
        window.removeLaunchButton();

        assertEquals("[titleBase]", window.titleBase);
        assertEquals("[titleBase]", window.fullTitle);

        // Selective and explicit verification
        verify(mockWindowManager, only()).closeWindow(eq(window), eq("[titleBase]"), eq("[icon]"));
        verify(mockWindowManager, never()).openWindow(eq(window), eq("[titleBase]"), eq("[icon]"));
    }

    @Test
    public void getConstraintArea_Usual() {
        assertEquals(mockConstrainArea, window.getConstraintArea());
    }

    /**
     * Tested method: {@link BaseWindow#getBaseWindowID()}
     */
    @Test
    public void getBaseWindowID_Usual() {
        // Recording and stubbing mock object(s) behaviour
        when(mockMultiWinId.getWinId()).thenReturn("WinId-123");

        assertEquals("WinId-123", window.getBaseWindowID());
    }

    /**
     * Tested method: {@link BaseWindow#generateCompositeId()}
     */
    @Test
    public void generateCompositeId_Usual() {
        // Recording and stubbing mock object(s) behaviour
        when(mockMultiWinId.generateCompositeId()).thenReturn("GenId-231");

        assertEquals("GenId-231", window.generateCompositeId());
    }

    /**
     * Tested method: {@link BaseWindow#updateTitle(String title)}
     */
    @Test
    public void updateTitle_onNewTitle() {
        window.updateTitle("<new title>");

        assertEquals("<new title>", window.fullTitle);
        assertNull(window.titlePrePrefix);
        assertNull(window.titlePrefix);

        // Selective and explicit verification
        verify(mockEniqWindow).updateTitle(eq("<new title>"));
    }

    /**
     * Tested method: {@link BaseWindow#applyTitle(String newTitle)}
     */
    @Test
    public void applyTitle_onNewTitle() {
        assertEquals("Very new title", window.applyTitle(" Very new title "));

        assertEquals("Very new title", window.fullTitle);
        assertEquals("Very new title", window.titleBase);
        assertNull(window.titlePrePrefix);
        assertNull(window.titlePrefix);

        // Selective and explicit verification
        verify(mockEniqWindow).updateTitle(eq("Very new title"));
    }

    /**
     * Tested method: {@link BaseWindow#appendTitle(String elementClickedForTitleBar)}
     */
    @Test
    public void appendTitle_Usual() {
        // Recording and stubbing mock object(s) behaviour
        when(mockEniqWindow.getHeading()).thenReturn("Heading");

        window.appendTitle("Appended title");

        // Selective and explicit verification
        verify(mockEniqWindow).updateTitle(eq("Heading (Appended title)"));
    }

    /**
     * Tested method: {@link BaseWindow#appendTitle(String elementClickedForTitleBar)}
     */
    @Test
    public void appendTitle_onEmptyTitle() {
        window.appendTitle("");

        // Selective and explicit verification
        verify(mockEniqWindow, never()).updateTitle(anyString());
        verify(mockEniqWindow, never()).getHeading();
    }

    /**
     * Tested method: {@link BaseWindow#appendTitle(String elementClickedForTitleBar)}
     */
    @Test
    public void appendTitle_onNullTitle() {
        window.appendTitle(null);

        // Selective and explicit verification
        verify(mockEniqWindow, never()).updateTitle(anyString());
        verify(mockEniqWindow, never()).getHeading();
    }

    /**
     * Tested method: {@link BaseWindow#getBaseWindowTitle()}
     */
    @Test
    public void getBaseWindowTitle_Usual() {
        window.getBaseWindowTitle();

        // Selective and explicit verification
        verify(mockEniqWindow).getHeading();
    }

    /**
     * Tested method: {@link BaseWindow#onBeforeHide()}
     */
    @Test
    public void onBeforeHide_Usual() {
        int count = ((TestBaseWindow) window).stopProcessingInvokedTimes;
        window.onBeforeHide();
        assertEquals(count + 1, ((TestBaseWindow) window).stopProcessingInvokedTimes);
    }

    /**
     * Tested method: {@link BaseWindow#onHide()}
     */
    @Test
    public void onHide_Usual() {
        window.onHide();

        assertEquals("[titleBase]", window.titleBase);
        assertEquals("[titleBase]", window.fullTitle);

        // Selective and explicit verification
        verify(mockWindowManager, only()).closeWindow(eq(window), eq("[titleBase]"), eq("[icon]"));
        verify(mockWindowManager, never()).openWindow(eq(window), eq("[titleBase]"), eq("[icon]"));
    }

    /**
     * Tested method: {@link BaseWindow#getBaseWindowTitleWithoutParams()}
     */
    @Test
    public void getBaseWindowTitleWithoutParams_ReturnsEniqWindowTitle() {
        window.getBaseWindowTitleWithoutParams();

        // Selective and explicit verification
        verify(mockEniqWindow).getWindowTitle();
    }

    /**
     * Tested method: {@link BaseWindow#getWidget()}
     */
    @Test
    public void getWidget_Usual_ReturnsEniqWindow() {
        assertEquals(mockEniqWindow, window.getWidget());
    }

    /**
     * Tested method: {@link BaseWindow#handleEvent(WindowEvent event)}
     */
    @Test
    public void handleEvent_onMinimizeEvent() {
        window = new TestBaseWindowForEvents();

        WindowEvent mockEvent = mock(WindowEvent.class);

        // Recording and stubbing mock object(s) behaviour
        when(mockEvent.getType()).thenReturn(Events.Minimize);

        assertEquals("", window.toString());
        window.handleEvent(mockEvent);
        assertEquals("Min", window.toString());

        verify(mockWindowManager, never()).closeWindow(eq(window), eq("[titleBase]"), eq("[icon]"));
        verify(mockWindowManager, never()).openWindow(eq(window), eq("[titleBase]"), eq("[icon]"));
    }

    /**
     * Tested method: {@link BaseWindow#handleEvent(WindowEvent event)}
     */
    @Test
    public void handleEvent_onRestoreEvent() {
        window = new TestBaseWindowForEvents();

        WindowEvent mockEvent = mock(WindowEvent.class);

        // Recording and stubbing mock object(s) behaviour
        when(mockEvent.getType()).thenReturn(Events.Restore);

        assertEquals("", window.toString());
        window.handleEvent(mockEvent);
        assertEquals("Rest", window.toString());

        verify(mockWindowManager, never()).closeWindow(eq(window), eq("[titleBase]"), eq("[icon]"));
        verify(mockWindowManager, never()).openWindow(eq(window), eq("[titleBase]"), eq("[icon]"));
    }

    /**
     * Tested method: {@link BaseWindow#handleEvent(WindowEvent event)}
     */
    @Test
    public void handleEvent_onBeforeHideEvent() {
        window = new TestBaseWindowForEvents();

        WindowEvent mockEvent = mock(WindowEvent.class);

        // Recording and stubbing mock object(s) behaviour
        when(mockEvent.getType()).thenReturn(Events.BeforeHide);

        assertEquals("", window.toString());
        window.handleEvent(mockEvent);
        assertEquals("Stop", window.toString());

        verify(mockWindowManager, never()).closeWindow(eq(window), eq("[titleBase]"), eq("[icon]"));
        verify(mockWindowManager, never()).openWindow(eq(window), eq("[titleBase]"), eq("[icon]"));
    }

    /**
     * Tested method: {@link BaseWindow#handleEvent(WindowEvent event)}
     */
    @Test
    public void handleEvent_onHideEvent() {
        window = new TestBaseWindowForEvents();

        WindowEvent mockEvent = mock(WindowEvent.class);

        // Recording and stubbing mock object(s) behaviour
        when(mockEvent.getType()).thenReturn(Events.Hide);

        assertEquals("", window.toString());
        window.handleEvent(mockEvent);
        assertEquals("Hide", window.toString());

        verify(mockWindowManager, only()).closeWindow(eq(window), eq("[titleBase]"), eq("[icon]"));
        verify(mockWindowManager, never()).openWindow(eq(window), eq("[titleBase]"), eq("[icon]"));
    }

    /**
     * Tested method: {@link BaseWindow#handleEvent(WindowEvent event)}
     */
    @Test
    public void handleEvent_onShowEvent() {
        window = new TestBaseWindowForEvents();

        WindowEvent mockEvent = mock(WindowEvent.class);

        // Recording and stubbing mock object(s) behaviour
        when(mockEvent.getType()).thenReturn(Events.Show);

        assertEquals("", window.toString());
        window.handleEvent(mockEvent);
        assertEquals("Show", window.toString());

        verify(mockWindowManager, never()).closeWindow(eq(window), eq("[titleBase]"), eq("[icon]"));
    }

    /**
     * Tested method: {@link BaseWindow#hide()}
     */
    @Test
    public void hide_Usual() {
        window.hide();

        // Selective and explicit verification
        verify(mockEniqWindow).hide();
    }

    /**
     * Tested method: {@link BaseWindow#toFront()}
     */
    @Test
    public void toFront_Usual() {
        window.toFront();

        // Selective and explicit verification
        verify(mockEniqWindow).toFront();
    }

    /**
     * Tested method: {@link BaseWindow#toBack()}
     */
    @Test
    public void toBack_Usual() {
        window.toBack();

        // Selective and explicit verification
        verify(mockEniqWindow).toBack();
    }

    /**
     * Tested method: {@link BaseWindow#isMinimised()}
     */
    @Test
    public void isMinimised_Usual() {
        window.isMinimised();

        // Selective and explicit verification
        verify(mockEniqWindow).isMinimised();
    }

    /**
     * Tested method: {@link BaseWindow#bringToFront()}
     */
    @Test
    public void bringToFront_Usual() {
        window.bringToFront();

        // Selective and explicit verification
        verify(mockEniqWindow).bringToFront();
    }

    /**
     * Tested method: {@link BaseWindow#focus()}
     */
    @Test
    public void focus_Usual() {
        window.focus();

        // Selective and explicit verification
        verify(mockEniqWindow).focus();
    }

    /**
     * Tested method: {@link BaseWindow#setPositionAndSize(int x, int y, int width, int height)}
     */
    @Test
    public void setPositionAndSize_Usual() {
        window.setPositionAndSize(1, 2, 3, 4);

        // Selective and explicit verification
        verify(mockEniqWindow).setPositionAndSize(eq(1), eq(2), eq(3), eq(4));
    }

    /**
     * Tested method: {@link BaseWindow#setUrl(String url)}
     */
    @Test
    public void setUrl_Usual() {
        window.setUrl("http://URL----Special---site.ie/Finest");

        // Selective and explicit verification
        verify(mockEniqWindow).setUrl(eq("http://URL----Special---site.ie/Finest"));
    }

    /**
     * Tested method: {@link BaseWindow#setSize(int width, int height)}
     */
    @Test
    public void setSize_Usual() {
        window.setSize(7, 9);

        // Selective and explicit verification
        verify(mockEniqWindow).setSize(eq(7), eq(9));
    }

    private class TestBaseWindow extends BaseWindow {

        int stopProcessingInvokedTimes;

        int startProcessingInvokedTimes;

        public TestBaseWindow(final String titleBase, final String icon, final boolean hideToolBar,
                final WindowState windowState /*,final InstanceWindowType windowType*/) {
            super(/*windowType,*/ BaseWindowTest.this.mockMultiWinId, BaseWindowTest.this.mockConstrainArea, titleBase,
                    icon, BaseWindowTest.this.mockEventBus, hideToolBar, windowState);
        }

        @Override
        public void stopProcessing() {
            stopProcessingInvokedTimes++;
        }

        @Override
        public void startProcessing() {
            startProcessingInvokedTimes++;
        }

        @Override
        public Widget asWidget() {
            return null;
        }

        @Override
        WindowManager createWindowManagerForTab() {
            return mockWindowManager;
        }

        @Override
        EniqWindow createEniqWindow(final ContentPanel constrainArea, final String icon) {
            return mockEniqWindow;
        }
    }

    private class TestBaseWindowForEvents extends TestBaseWindow {
        private final StringBuffer state = new StringBuffer();

        @Override
        protected void onMinimize() {
            state.append("Min");
            super.onMinimize();
        }

        @Override
        protected void onRestore() {
            state.append("Rest");
            super.onRestore();
        }

        @Override
        public void stopProcessing() {
            state.append("Stop");
            super.stopProcessing();
        }

        @Override
        protected void onHide() {
            state.append("Hide");
            super.onHide();
        }

        @Override
        protected void onShow() {
            state.append("Show");
            super.onShow();
        }

        public TestBaseWindowForEvents() {
            super("[titleBase]", "[icon]", true, BaseWindowTest.this.mockWindowState/*,
                    InstanceWindowType.TERMINAL_EVENT_ANALYSIS_WCDMA_CFA_SUMMARY*/);
        }

        @Override
        public String toString() {
            return state.toString();
        }
    }
}
