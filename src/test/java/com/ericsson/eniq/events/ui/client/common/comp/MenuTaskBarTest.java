/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.ToolBarStateManager;
import com.ericsson.eniq.events.ui.client.common.service.WindowManagerImpl;
import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldUser;
import com.ericsson.eniq.events.ui.client.events.GraphDrillDownLaunchEvent;
import com.ericsson.eniq.events.ui.client.events.GraphDrillDownLaunchEventHandler;
import com.ericsson.eniq.events.ui.client.events.SearchFieldValueResetEvent;
import com.ericsson.eniq.events.ui.client.events.window.*;
import com.ericsson.eniq.events.ui.client.main.GenericTabView;
import com.ericsson.eniq.events.ui.client.search.ISearchComponent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.web.bindery.event.shared.EventBus;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.*;

/**
 * @author eeicmsy
 * @since March 2010
 */
public class MenuTaskBarTest extends TestEniqEventsUI {

   private MenuTaskBar objUnderTest;

   MenuTaskBarButton mockedMenuTaskBarButton;

   MultiInstanceTaskBarButton mockedMultiInstanceTaskBarButton;

   Button mockedButton;

   Menu mockedMenu;

   GenericTabView mockedGenericTabView;

   BaseWindow mockedWindow;

   ISearchComponent mockedSearchComponent;

   private final String defaultMenuItem = "NETWORK_EVENT_ANALYSIS";

   private final static String PS = "PS";

   private final static String CS = "CS";

   protected String dataType = "SGSN";

   protected boolean isReturnNull = false;

   @Before
   public void setUp() {

      mockedButton = context.mock(Button.class);
      mockedMenu = context.mock(Menu.class);
      mockedGenericTabView = context.mock(GenericTabView.class);
      mockedMenuTaskBarButton = context.mock(MenuTaskBarButton.class);
      mockedMultiInstanceTaskBarButton = context.mock(MultiInstanceTaskBarButton.class);

      mockedWindow = context.mock(BaseWindow.class);
      mockedSearchComponent = context.mock(ISearchComponent.class);

      objUnderTest = new StubMenuTaskBar(mockedGenericTabView, "NETWORK_TAB", mockedSearchComponent, defaultMenuItem);
      objUnderTest.setEventBus(mockedEventBus);
      objUnderTest.setWindowManager(new WindowManagerImpl(mockedEventBus, "NETWORK_TAB"));
   }

   @Test
   public void initiateWithEventBus() {
      context.checking(new Expectations() {
         {
            allowing(mockedGenericTabView).getMetaDataLicenceCount();
            allowing(mockedGenericTabView).getCenterPanel();
            one(mockedSearchComponent).registerWithEventBus(mockedEventBus);
            one(mockedEventBus).addHandler(with(same(GraphDrillDownLaunchEvent.TYPE)),
                    with(any(GraphDrillDownLaunchEventHandler.class)));

            one(mockedEventBus).addHandler(with(same(WindowOpenedEvent.TYPE)),
                    with(any(WindowOpenedEventHandler.class)));
            one(mockedEventBus).addHandler(with(same(WindowClosedEvent.TYPE)),
                    with(any(WindowClosedEventHandler.class)));
            one(mockedEventBus).addHandler(with(same(WindowLaunchButtonTitleUpdateEvent.TYPE)),
                    with(any(WindowLaunchButtonTitleUpdateEventHandler.class)));
         }
      });
      objUnderTest.initiateWithEventBus();
   }

   @Test
   public void addMenuTaskBarButtonStoresWindow() {

      final String myWinID = "someWin";

      context.checking(new Expectations() {
         {
            allowing(mockedGenericTabView).getMetaDataLicenceCount();
            one(mockedMenuTaskBarButton).getWindowID();
            will(returnValue(myWinID));
            one(mockedMenuTaskBarButton).setItemId(with(any(String.class)));

         }
      });
      objUnderTest.add(mockedMenuTaskBarButton);
      assertEquals(false, objUnderTest.getWindow(myWinID) != null);
   }

   @Test
   public void removeMenuTaskBarButtonDoesNotFireWindowShutDownEvent() { // no need to fire event call directly

      final String myWinID = "someWin";

      context.checking(new Expectations() {
         {
            allowing(mockedGenericTabView).getMetaDataLicenceCount();
            allowing(mockedGenericTabView).getCenterPanel();
            one(mockedSearchComponent).registerWithEventBus(mockedEventBus);
            one(mockedMenuTaskBarButton).getWindowID();

            will(returnValue(myWinID));

            one(mockedEventBus)
                    .addHandler(with(any(Type.class)), with(any(GraphDrillDownLaunchEventHandler.class)));
            one(mockedEventBus).addHandler(with(same(WindowOpenedEvent.TYPE)),
                    with(any(WindowOpenedEventHandler.class)));
            one(mockedEventBus).addHandler(with(same(WindowClosedEvent.TYPE)),
                    with(any(WindowClosedEventHandler.class)));
            one(mockedEventBus).addHandler(with(same(WindowLaunchButtonTitleUpdateEvent.TYPE)),
                    with(any(WindowLaunchButtonTitleUpdateEventHandler.class)));
         }
      });
      objUnderTest.initiateWithEventBus();

      objUnderTest.remove(mockedMenuTaskBarButton);
   }

   @Test
   public void addPlainButtonWithNoMenuItems() {

      final List<MetaMenuItem> menuItems = new ArrayList<MetaMenuItem>();

      menuItems.add(getGridMetaMenuItem("id1", SearchFieldUser.FALSE));
      menuItems.add(getRankingMetaMenuItem("id2", SearchFieldUser.FALSE));

      context.checking(new Expectations() {
         {
            one(mockedButton).getMenu();
         }
      });
      objUnderTest.addMenuButton(mockedButton, PS);

   }

   @Test
   public void addButtonWithMenuItemsUpdatesMetaMenuItemsStore() {
      context.checking(new Expectations() {
         {
            one(mockedButton).getMenu();
            will(returnValue(mockedMenu));

            one(mockedMenu).setShadow(false);

            one(mockedMenu).getItemCount();
            will(returnValue(new Integer(2)));
            one(mockedMenu).getItem(0);
            will(returnValue(getGridMetaMenuItem("id0", SearchFieldUser.FALSE)));
            one(mockedMenu).getItem(1);
            will(returnValue(getRankingMetaMenuItem("id1", SearchFieldUser.FALSE)));
         }
      });
      objUnderTest.addMenuButton(mockedButton, PS);
      assertEquals("Menu Item added", true, objUnderTest.metaMenuItems.containsKey("id0"));
      assertEquals("Menu Item added", true, objUnderTest.metaMenuItems.containsKey("id1"));
      assertEquals("Menu Item not added", false, objUnderTest.metaMenuItems.containsKey("id3"));
   }

   @Test
   public void addButtonWithSearchParamMenuItemsUpdatesSearchParamStore() {

      final MetaMenuItem menuNeedingSearchField = getGridMetaMenuItem("id0", SearchFieldUser.TRUE);

      context.checking(new Expectations() {
         {
            one(mockedButton).getMenu();
            will(returnValue(mockedMenu));

            one(mockedMenu).setShadow(false);

            one(mockedMenu).getItemCount();
            will(returnValue(new Integer(2)));
            one(mockedMenu).getItem(0);
            will(returnValue(menuNeedingSearchField)); // for other list too
            one(mockedMenu).getItem(1);
            will(returnValue(getRankingMetaMenuItem("id1", SearchFieldUser.FALSE)));
         }
      });
      objUnderTest.addMenuButton(mockedButton, CS);
      assertEquals("Menu Item added", true, objUnderTest.metaMenuItems.containsKey("id0"));
      assertEquals("Menu Item added", true, objUnderTest.metaMenuItems.containsKey("id1"));

      final Set<String> relatedWindowsForSearchFields = objUnderTest.getRelatedWindowsForSearchFieldMap().get(CS);

      assertEquals("Search menu item added", true, relatedWindowsForSearchFields.contains("id0"));
      assertEquals("Search menu item not added when not a search field tpye menuitem", false,
              relatedWindowsForSearchFields.contains("id2"));
   }

  /* @Test
   public void addMultiInstanceButtonCallWhenExistsOnlyAddsMenuItems() {

      objUnderTest.getInstanceButtons().put(InstanceWindowType.KPI, mockedMultiInstanceTaskBarButton);
      final String menuItemId = "nodeName";

      context.checking(new Expectations() {
         {
            allowing(mockedGenericTabView).getMetaDataLicenceCount();
            one(mockedMultiInstanceTaskBarButton).addInstance(menuItemId, mockedWindow);
         }
      });

      assertEquals("definately still size one", 1, objUnderTest.getInstanceButtons().size());
   }

   @Test
   public void addMultiInstanceButtonCreatesNewButtonWhenNoneExists() {

      objUnderTest.getInstanceButtons().clear(); // for clarity
      final String menuItemId = "nodeName";

      context.checking(new Expectations() {
         {
            allowing(mockedGenericTabView).getMetaDataLicenceCount();
            one(mockedMultiInstanceTaskBarButton).addInstance(menuItemId, mockedWindow);
            one(mockedMultiInstanceTaskBarButton).setItemId("KPIMULTI_IDENTIFERnodeName");
         }
      });

      assertEquals("button added as expected", true, objUnderTest.getInstanceButtons().containsKey(
              InstanceWindowType.KPI));
   }

   @Test
   public void removeMultiInstanceButtonDoesNotRemoveButtonItselfWhenMenuItemsRemain() {

      objUnderTest.getInstanceButtons().put(InstanceWindowType.KPI, mockedMultiInstanceTaskBarButton);
      final String menuItemId = "nodeName";
      final int numberOfMenuItemsInButton = 5;

      context.checking(new Expectations() {
         {
            allowing(mockedGenericTabView).getMetaDataLicenceCount();

            one(mockedMultiInstanceTaskBarButton).removeInstance(menuItemId);
            will(returnValue(true));
            one(mockedMultiInstanceTaskBarButton).getMenu();
            will(returnValue(mockedMenu));
            one(mockedMenu).getItemCount();
            will(returnValue(numberOfMenuItemsInButton));

         }
      });

      assertEquals("Taskbar no longer has a stored reference to window being removed", false, objUnderTest
              .getOwnedBaseWindowsMap().containsKey(menuItemId));
      assertEquals("button remains as expected", true, objUnderTest.getInstanceButtons().containsKey(
              InstanceWindowType.KPI));
   }

   @Test
   public void removeMultiInstanceButtonRemovesButtonWhenItsHoldsLastWindow() {

      objUnderTest.getInstanceButtons().put(InstanceWindowType.KPI, mockedMultiInstanceTaskBarButton);
      final String menuItemId = "nodeName";
      final int numberOfMenuItemsInButton = 0; // removed was last one

      context.checking(new Expectations() {
         {
            allowing(mockedGenericTabView).getMetaDataLicenceCount();

            one(mockedMultiInstanceTaskBarButton).removeInstance(menuItemId);
            will(returnValue(true));
            one(mockedMultiInstanceTaskBarButton).getMenu();
            will(returnValue(mockedMenu));
            one(mockedMenu).getItemCount();
            will(returnValue(numberOfMenuItemsInButton));

         }
      });

      assertEquals("Taskbar no longer has a stored reference to window being removed", false, objUnderTest
              .getOwnedBaseWindowsMap().containsKey(menuItemId));
      assertEquals("button is maud gone", true, objUnderTest.getInstanceButtons().isEmpty());
   }*/

   @Test
   public void testIsDefaultMenuItemExcludedSearchTypeNULL() {
      try {
         final Method methodUnderTest = MenuTaskBar.class.getDeclaredMethod("isDefaultMenuItemExcluded",
                 MetaMenuItem.class);
         methodUnderTest.setAccessible(true);
         isReturnNull = true;
         final MetaMenuItem menuNeedingSearchField = getGridMetaMenuItem("id0", SearchFieldUser.TRUE);

         assertFalse("The Search type is NULL, so the result should be false.", (Boolean) methodUnderTest.invoke(
                 objUnderTest, menuNeedingSearchField));
      } catch (final NoSuchMethodException e) {
         fail("The method isDefaultMenuItemExcluded is not found");
      } catch (final InvocationTargetException e) {
         fail("Couldn't invoke the private method: isDefaultMenuItemExcluded.");
      } catch (final IllegalAccessException e) {
         fail("Illegal access to private method: isDefaultMenuItemExcluded.");
      }
   }

   @Test
   public void testIsDefaultMenuItemExcludedSearchTypeTRAC() {
      try {
         final Method methodUnderTest = MenuTaskBar.class.getDeclaredMethod("isDefaultMenuItemExcluded",
                 MetaMenuItem.class);
         methodUnderTest.setAccessible(true);
         final String excludedSearchTypes = "TRAC, APN";
         final String searchType = "TRAC";

         setSearchFieldDataType(searchType);

         final MetaMenuItem menuNeedingSearchField = getGridMetaMenuItemWithExcludedSearch("id0",
                 SearchFieldUser.TRUE, searchType, excludedSearchTypes);

         assertTrue("The Search type is TRAC and TRAC is excluded, so true should be returned.",
                 (Boolean) methodUnderTest.invoke(objUnderTest, menuNeedingSearchField));

      } catch (final NoSuchMethodException e) {
         fail("The method isDefaultMenuItemExcluded is not found");
      } catch (final InvocationTargetException e) {
         fail("Couldn't invoke the private method: isDefaultMenuItemExcluded.");
      } catch (final IllegalAccessException e) {
         fail("Illegal access to private method: isDefaultMenuItemExcluded.");
      }
   }

   public void setSearchFieldDataType(final String searchType) {
      dataType = searchType;
   }

   public String getSearchFieldDataType() {
      return dataType;
   }

   /* @Test
   public void addCascadeMenuItemAddsSelectionListener() {

       context.checking(new Expectations() {
           {
               one(mockedButton).getMenu();
               will(returnValue(null));
               one(mockedButton).getItemId();
               will(returnValue("CASCADE"));
               one(mockedButton).addSelectionListener(with(any(SelectionListener.class)));
           }
       });
       objUnderTest.addMenuButton(mockedButton, PS);
   }*/

   /* @Test
   public void addTileMenuItemAddsSelectionListener() {

       context.checking(new Expectations() {
           {
               one(mockedButton).getMenu();
               will(returnValue(null));
               one(mockedButton).getItemId();
               will(returnValue("TILE"));
               one(mockedButton).addSelectionListener(with(any(SelectionListener.class)));
           }
       });
       objUnderTest.addMenuButton(mockedButton, PS);
   }*/

   /* @Test
   public void addExcTacAddsSelectionListener() {

       context.checking(new Expectations() {
           {
               one(mockedButton).getMenu();
               will(returnValue(null));
               one(mockedButton).getItemId();
               will(returnValue("EXC_TAC"));
               one(mockedButton).addSelectionListener(with(any(SelectionListener.class)));
           }
       });
       objUnderTest.addMenuButton(mockedButton, PS);
   }*/

   /*    @Test
   public void addUnknownDefinedMenuButtonWillNeverAddSelectionListener() {

       context.checking(new Expectations() {
           {
               one(mockedButton).getMenu();
               will(returnValue(null));
               one(mockedButton).getItemId();
               will(returnValue("RANDOM"));
               never(mockedButton).addSelectionListener(with(any(SelectionListener.class)));
           }
       });
       objUnderTest.addMenuButton(mockedButton, CS);
   }*/

   @Test
   public void submitSearchFieldInfoFiresSearchFieldValueResetEvent() {

      final int numSearchTypeWindows = 5;

      context.checking(new Expectations() {
         {
            allowing(mockedGenericTabView).getMetaDataLicenceCount();
            allowing(mockedGenericTabView).getCenterPanel();
            one(mockedSearchComponent).registerWithEventBus(mockedEventBus);
            allowing(mockedSearchComponent).getMetaChangeComponentRef();
            will(returnValue(CS));

            exactly(numSearchTypeWindows).of(mockedEventBus).fireEvent(with(any(SearchFieldValueResetEvent.class)));
            one(mockedEventBus)
                    .addHandler(with(any(Type.class)), with(any(GraphDrillDownLaunchEventHandler.class)));
            one(mockedEventBus).addHandler(with(same(WindowOpenedEvent.TYPE)),
                    with(any(WindowOpenedEventHandler.class)));
            one(mockedEventBus).addHandler(with(same(WindowClosedEvent.TYPE)),
                    with(any(WindowClosedEventHandler.class)));
            one(mockedEventBus).addHandler(with(same(WindowLaunchButtonTitleUpdateEvent.TYPE)),
                    with(any(WindowLaunchButtonTitleUpdateEventHandler.class)));
            exactly(5).of(mockedWindow).toFront();
            allowing(mockedWindow).getBaseWindowID();
         }
      });

      for (int i = 0; i < numSearchTypeWindows; i++) {
         objUnderTest.addRelatedWindowsForSearchField("winId_" + i, CS);
         objUnderTest.getOwnedBaseWindowsMap().put("winId_" + i, mockedWindow);
      }

      objUnderTest.initiateWithEventBus();
      objUnderTest.submitSearchFieldInfo();
   }

   @Test
   public void submitSearchFieldInfoLaunchesDefaultWin() {

      /*Prove that a default window will be launched if not window is open within a tab when a search is invoked */

      //no windows open
      objUnderTest.getOwnedBaseWindowsMap().clear();
      context.checking(new Expectations() {
         {
            allowing(mockedSearchComponent).getMetaChangeComponentRef();
            will(returnValue(PS));
            one(mockedGenericTabView).getCenterPanel();
         }
      });

      objUnderTest.submitSearchFieldInfo();
   }

   /////////////////    STUBS AND PRIVATES  //////////////////////////////////////

   private MetaMenuItem getGridMetaMenuItem(final String winId, final SearchFieldUser needSearchParm) {
      final MetaMenuItemDataType.Type windowType = MetaMenuItemDataType.convertType("GRID");

      final MetaMenuItem menuItem = new MetaMenuItem(new MetaMenuItemDataType.Builder().text("Cause Code Analysis")
              .id(winId).isSearchFieldUser(needSearchParm).url("RestTest/WHATEVER").windowType(windowType).display(
                      "grid").type("IMSI").key("SUM").toolBarHandler(
                      new ToolBarStateManager("TOOLBAR2", ToolBarStateManager.BottomToolbarType.PAGING, "TOOLBAR1"))
              .maxRowsParam("MAXROWSPARAM").minimizedButtonName("Miminised name").build());
      return menuItem;
   }

   private MetaMenuItem getRankingMetaMenuItem(final String winId, final SearchFieldUser needSearchParm) {

      final MetaMenuItemDataType.Type windowType = MetaMenuItemDataType.convertType("RANKING");
      final MetaMenuItem menuItem = new MetaMenuItem(new MetaMenuItemDataType.Builder().text("Cause Code Analysis")
              .id(winId).isSearchFieldUser(needSearchParm).url("RestTest/WHATEVER").windowType(windowType).display(
                      "grid").type("IMSI").key("SUM").toolBarHandler(
                      new ToolBarStateManager("TOOLBAR2", ToolBarStateManager.BottomToolbarType.PAGING, "TOOLBAR1"))
              .maxRowsParam("MAXROWSPARAM").minimizedButtonName("Miminised name").build());

      return menuItem;
   }

   private MetaMenuItem getGridMetaMenuItemWithExcludedSearch(final String winId,
                                                              final SearchFieldUser needSearchParm, final String type, final String excludedSearch) {
      final MetaMenuItemDataType.Type windowType = MetaMenuItemDataType.convertType("GRID");

      final MetaMenuItem menuItem = new MetaMenuItem(new MetaMenuItemDataType.Builder().text("Cause Code Analysis")
              .id(winId).isSearchFieldUser(needSearchParm).url("RestTest/WHATEVER").windowType(windowType).display(
                      "grid").type(type).key("SUM").excludedSearchTypes(excludedSearch).toolBarHandler(
                      new ToolBarStateManager("TOOLBAR2", ToolBarStateManager.BottomToolbarType.PAGING, "TOOLBAR1"))
              .maxRowsParam("MAXROWSPARAM").minimizedButtonName("Miminised name").build());
      return menuItem;
   }

   private class StubMenuTaskBar extends MenuTaskBar {

      protected String searchFieldDataType = null;

      public StubMenuTaskBar(final GenericTabView parentView, final String tabId, final ISearchComponent searchComp,
                             final String defaultMenu) {
         super(parentView, tabId, searchComp, null, null, defaultMenu);
      }

      @Deprecated
      public void setSearchFieldDataType(final String dataType) {
         searchFieldDataType = dataType;
      }

      @Override
      public ISearchComponent getSearchComp() {
         return mockedSearchComponent;
      }

      @Override
      public EventBus getEventBus() {
         return mockedEventBus;
      }

      @Override
      boolean callToRealToolBarAdd(final MenuTaskBarButton launchButton) {
         return true;
      }

      @Override
      boolean callToRealToolBarRemove(final MenuTaskBarButton launchButton) {
         return true;
      }

      @Override
      boolean callToRealAddButton(final Component button) {
         return true;
      }

      @Override
      boolean callToRealRemoveButton(final Component button) {
         return true;
      }

      @Override
      String getOrigionalMenuItemURL(final String winId) {
         return "";
      }

     /* @Override
      MultiInstanceTaskBarButton createMultiInstanceTaskBarButton(*//*final InstanceWindowType type*//*) {
         return mockedMultiInstanceTaskBarButton;
      }*/

      @Override
      public SearchFieldDataType getSearchComponentValue() {
         //want to be able to test if the return type was null.
         if (isReturnNull) {
            return null;
         }
         return new SearchFieldDataType("searchFieldValue", new String[]{"node=myNode", "nodeType=SGSN"},
                 getSearchFieldDataType(), null, false, "", null, false);
      }

      @Override
      MetaMenuItem getDefaultWindowMenuItemById() {

         final MetaMenuItemDataType.Type windowType = MetaMenuItemDataType.convertType("GRID");

         final MetaMenuItem menuItem = new MetaMenuItem(new MetaMenuItemDataType.Builder().text(
                 "Cause Code Analysis").id("someWinId").url("RestTest/WHATEVER").windowType(windowType).display(
                 "grid").type("IMSI").key("SUM").toolBarHandler(
                 new ToolBarStateManager("TOOLBAR2", ToolBarStateManager.BottomToolbarType.PAGING, "TOOLBAR1"))
                 .maxRowsParam("MAXROWSPARAM").minimizedButtonName("Miminised name").build());
         return menuItem;
      }

      @Override
      void launchDefaultMenuItem(final MetaMenuItem defaultMnuItem, final ContentPanel holder) {
         //stubbed out logic
      }

   }
}
