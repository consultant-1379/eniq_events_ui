/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.widgets.client.button.ImageButton;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.events.GroupSingleToggleEvent;
import com.ericsson.eniq.events.ui.client.events.MaskEvent;
import com.ericsson.eniq.events.ui.client.events.SearchFieldTypeChangeEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.web.bindery.event.shared.EventBus;
import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/** @author eeicmsy */
public class PairedTypeSearchComponentTest extends TestEniqEventsUI {

   PairedTypeSearchComponent objectToTest;

   ILiveLoadComboView mockedLiveLoadComboView;

   ISubmitSearchHandler mockedSubmitSearchHandler;

   LiveLoadComboPresenter mockedLiveLoadComboPresenter;

   Button mockedButton;
   ImageButton mockedImageButton;

   Menu mockedMenu;

   @Before
   public void setUp() {

      mockedLiveLoadComboView = context.mock(ILiveLoadComboView.class);
      mockedLiveLoadComboPresenter = context.mock(LiveLoadComboPresenter.class);
      mockedButton = context.mock(Button.class);
      mockedImageButton = context.mock(ImageButton.class);
      mockedMenu = context.mock(Menu.class);
      mockedSubmitSearchHandler = context.mock(ISubmitSearchHandler.class);
      expectAddHandlerForGroupSingleToggleEventOnEventBus();
      createObjectToTest();
   }

   /**
    *
    */
   void expectAddHandlerForGroupSingleToggleEventOnEventBus() {
      context.checking(new Expectations() {
         {
            one(mockedEventBus).addHandler(with(same(GroupSingleToggleEvent.TYPE)),
                    with(any(PairedTypeSearchComponent.class)));
            one(mockedEventBus).addHandler(with(same(MaskEvent.TYPE)),
                    with(any(AbstractPairedTypeSearchComponent.class)));
         }
      });
   }

   @After
   public void tearDown() {
      objectToTest = null;
   }

   @Test
   @Ignore
   public void initiateWithEventBusSetsUpLiveLoad() {

      checkExceptionsForInitEventBusCall();
      expectAddHandlerForGroupSingleToggleEventOnEventBus();
      objectToTest.registerWithEventBus(mockedEventBus);

      assertEquals("got to call live load setup", true,
              ((StubbedSearchFieldPairedType) objectToTest).isLiveLoadCalled);

   }

   //@Test
   @Ignore
   public void getSearchComponentValueReturnsExpectedValue() {

      final String testVal = "hello momma";

      context.checking(new Expectations() {
         {
            one(mockedLiveLoadComboPresenter).getRawValue();
            will(returnValue(testVal));

         }
      });
      final SearchFieldDataType actualVal = objectToTest.getSearchComponentValue();
      final SearchFieldDataType expectedVal = new SearchFieldDataType(testVal, new String[]{"type=CELL",
                    "node=" + testVal}, null, null, false, "", null, false);
      assertEquals(
              "got expected searchField parameters (warning toString of SearchFieldDataType may interfer with compare message)",
              expectedVal.searchFieldVal, actualVal.searchFieldVal);
   }

   @Ignore
   public void getSearchComponentReturnsExpectedValue() {
      final Component comp = objectToTest.getSearchComponent();
      assertEquals("got expected component", true, comp != null);
   }

   @Ignore
   public void performActionForTypeChangedClearsSelection() {

      final LiveLoadTypeMenuItem liveLoadTypeMenuItem = new LiveLoadTypeMenuItem("SGSN", "", "SGSN", "liveLoadURL",
              "", "Enter RNC...", "");

      context.checking(new Expectations() {
         {
            one(mockedLiveLoadComboPresenter).setToolTip(with(any(String.class)));
            one(mockedButton).setVisible(with(any(Boolean.class)));
            one(mockedLiveLoadComboPresenter).setVisible(with(any(Boolean.class)));
            one(mockedLiveLoadComboPresenter).setEmptyText("Enter RNC...");

            one(mockedLiveLoadComboPresenter).clearSelections();
            one(mockedButton).setText("SGSN");
            one(mockedButton).setEnabled(false);
            one(mockedEventBus).fireEvent(with(any(SearchFieldTypeChangeEvent.class)));

         }
      });
      objectToTest.performActionForTypeChanged(liveLoadTypeMenuItem);
   }


   @Ignore
   public void performActionForSearchItemSelectsWithTrueSubmitsCall() {

      context.checking(new Expectations() {
         {

            one(mockedLiveLoadComboPresenter).getRawValue();
            one(mockedLiveLoadComboPresenter).setToolTip("");
            one(mockedSubmitSearchHandler).submitSearchFieldInfo();

         }
      });

      objectToTest.addSubmitSearchHandler(mockedSubmitSearchHandler);
      objectToTest.performActionForSearchItemSelected(true);
   }

   @Ignore
   public void performActionForSearchItemSelectsWithFalseSubmitsCall() {

      context.checking(new Expectations() {
         {

            one(mockedLiveLoadComboPresenter).getRawValue();
            one(mockedLiveLoadComboPresenter).setToolTip("");
         }
      });

      objectToTest.addSubmitSearchHandler(mockedSubmitSearchHandler);
      objectToTest.performActionForSearchItemSelected(false);
   }

   //@Test
   @Ignore
   public void directSetTypeMenuItemsResetsTypes() {

      final List<LiveLoadTypeMenuItem> typesMenuItems = new ArrayList<LiveLoadTypeMenuItem>();
      final LiveLoadTypeMenuItem liveLoadTypeMenuItem1 = new LiveLoadTypeMenuItem("CELL", "", "Cell", "liveLoadURL",
              "", "Enter cell...", "CS,PS");
      final LiveLoadTypeMenuItem liveLoadTypeMenuItem2 = new LiveLoadTypeMenuItem("RNC", "", "RNC", "liveLoadURL2",
              "", "Enter RNC...", "CS,PS");
      final LiveLoadTypeMenuItem liveLoadTypeMenuItem3 = new LiveLoadTypeMenuItem("BSC", "", "BSC", "liveLoadURL3",
              "", "Enter BSC...", "CS,PS");

      typesMenuItems.add(liveLoadTypeMenuItem1);
      typesMenuItems.add(liveLoadTypeMenuItem2);
      typesMenuItems.add(liveLoadTypeMenuItem3);

      context.checking(new Expectations() {
         {
            one(mockedLiveLoadComboPresenter).setToolTip(with(any(String.class)));
            one(mockedMenu).setShadow(false);
            one(mockedLiveLoadComboPresenter).setEnable(with(any(Boolean.class)));
            one(mockedButton).setVisible(with(any(Boolean.class)));
            one(mockedLiveLoadComboPresenter).setVisible(with(any(Boolean.class)));
            exactly(3).of(mockedMenu).add(with(any(LiveLoadTypeMenuItem.class)));
            one(mockedButton).setMenu(mockedMenu);
            one(mockedButton).setWidth(140);
            one(mockedLiveLoadComboPresenter).setEmptyText("Enter cell...");
            one(mockedLiveLoadComboPresenter).clearSelections();
            one(mockedButton).setText("Cell");
            one(mockedButton).setEnabled(false);
            one(mockedEventBus).fireEvent(with(any(SearchFieldTypeChangeEvent.class)));

         }
      });
      objectToTest.setTypeMenuItems(typesMenuItems);

   }

   private void createObjectToTest() {
      final List<LiveLoadTypeMenuItem> typesMenuItems = new ArrayList<LiveLoadTypeMenuItem>();
      final LiveLoadTypeMenuItem liveLoadTypeMenuItem = new LiveLoadTypeMenuItem("CELL", "", "Cell", "liveLoadURL",
              "", "Enter cell...", "CS,PS");
      typesMenuItems.add(liveLoadTypeMenuItem);

      // avoid comboes and choose menu item for type combo
      objectToTest = new StubbedSearchFieldPairedType("NETWORK_TAB", typesMenuItems, 0, "submit button tooltip",
              true, "");
      /* everything to so with set up is from here */
      checkExceptionsForInitEventBusCall();
      objectToTest.registerWithEventBus(mockedEventBus);

   }

   private void checkExceptionsForInitEventBusCall() {
      context.checking(new Expectations() {
         {

            one(mockedLiveLoadComboPresenter).setToolTip(with(any(String.class)));
            one(mockedLiveLoadComboPresenter).setEnable(with(any(Boolean.class)));
            one(mockedLiveLoadComboView).addSelectionChangedListener(
                    with(any(PairedTypeSearchComponent.SubmitSearchFieldComboListener.class)));

            one(mockedMenu).setShadow(false);

            one(mockedLiveLoadComboPresenter).setVisible(with(any(Boolean.class)));
            one(mockedButton).setVisible(with(any(Boolean.class)));

            allowing(mockedButton).setIconStyle(with(any(String.class)));
            one(mockedButton).setToolTip(with(any(String.class)));
            one(mockedImageButton).addClickHandler(with(any(PairedTypeSearchComponent.SubmitSearchFieldButtonListener.class)));
            /* one(mockedButton).addSelectionListener(
            with(any(PairedTypeSearchComponent.SubmitSearchFieldButtonListener.class)));*/

            // only  menu item
            one(mockedMenu).add(with(any(LiveLoadTypeMenuItem.class)));
            one(mockedButton).setMenu(mockedMenu);

            one(mockedLiveLoadComboPresenter).setEmptyText("Enter cell...");

            one(mockedLiveLoadComboPresenter).clearSelections();
            one(mockedButton).setText("Cell");

            one(mockedLiveLoadComboView).addKeyListener(with(any(KeyListener.class)));
            one(mockedLiveLoadComboView).addMouseListener(with(any(PairedTypeSearchComponent.MouseListener.class)));
            one(mockedButton).setEnabled(false);
            // selenium ids
            one(mockedLiveLoadComboView).setId(with(any(String.class)));
            allowing(mockedButton).setId(with(any(String.class)));
            one(mockedButton).setWidth(140);

            one(mockedEventBus).fireEvent(with(any(SearchFieldTypeChangeEvent.class)));

         }
      });

   }

   private class StubbedSearchFieldPairedType extends PairedTypeSearchComponent {
      /**
       * @param tabOwnerId
       * @param types
       * @param defaultTypeIndex
       * @param submitButtonToolTip
       * @param isUsingMenuForType
       * @param typeEmptyText
       */
      public StubbedSearchFieldPairedType(final String tabOwnerId, final List<LiveLoadTypeMenuItem> types,
                                          final int defaultTypeIndex, final String submitButtonToolTip, final boolean isUsingMenuForType,
                                          final String typeEmptyText) {
         super(tabOwnerId, types, defaultTypeIndex, submitButtonToolTip, isUsingMenuForType, typeEmptyText);

      }

      public boolean isLiveLoadCalled;

      @Override
      SearchComboBox createTypeComboBox() {
         return null; // Can not mock a combobox;
      }

      @Override
      public void addComponentsToPanel(final ILiveLoadComboView searchDisplay) {
         // nothing we want to get into
      }

      @Override
      public ILiveLoadComboView createLiveLoadComboView() {
         return mockedLiveLoadComboView;
      }

      @Override
      LiveLoadComboPresenter createLiveLoadComboPresenter(final ILiveLoadComboView searchDisplay,
                                                          final EventBus eventBus) {
         return mockedLiveLoadComboPresenter;
      }

      @Override
      public Button createButton() {
         return mockedButton;
      }

      @Override
      public Menu createMenu() {
         return mockedMenu;
      }

      @Override
      public void setupLiveLoad(final String url, final String root) {
         this.isLiveLoadCalled = true;
      }

   }

}
