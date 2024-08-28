/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType.Type;

import java.util.HashMap;
import java.util.Map;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

/**
 * Class to hold toolbar states for a window at a given time.
 * Window will have upper toolbar and bottom toolbar (paging for grid, plain for graph)
 * <p/>
 * There can be up to three upper (top) toolbars for the same window i.e.
 * <p/>
 * <li>toolbar when window is a displaying a graph <li>
 * <li>supporting top toolbar toggling when switch from graph to grid (with option to toggle back)</li>
 * <li>top toolbar changing when drill on a chart (don't want to loose existing toggleToolBarType reference which will want back)</li>
 *
 * @author eeicmsy
 * @since Aug 2010
 */
public class ToolBarStateManager {

   /**
    * Specify on grid creation which bottom
    * toolbar to use (this need to be a pre-rendered decision,
    */
   public enum BottomToolbarType {
      PAGING, PLAIN
   }

   ;

   /*
   * Type of Toolbar associated with window
   * launched from this menu item
   * We can change this at run time for toggle scenario.
   * (so not accessed directly - use method)
   */
   private final String toolBarType;

   /*
   * When toggle a graph to a grid, set
   * the toolbar to this type
   */
   private final String toggleToolBarType;

   /*
   * Because we now also support top toolbar
   * changing when drill on a chart (on top of supporting top toolbar toggling
   * when switch from graph to grid) - we have more than two toolbar types to support
   * state for while user switching between toggled views and
   * chart drilled views (don't want to loose existing toggleToolBarType reference which will want back)
   */
   private String tempToggleToolBar = null;

   /*
   * initial state of upper toolbar type for new MetaMenuItem instance
   */
   private String currentToolBarType = EMPTY_STRING;

   /*
   * initial state of lower (bottom, footer) toolbar type
   */
   private BottomToolbarType currentBottomToolBarType = null;

   /*
   * Utility to specify bottom toolbar via meta data prior to rendering.
   * Defaults to paging type for most grids - except cause code.
   */
   private final BottomToolbarType bottomToolbarType;

   private static Map<String, BottomToolbarType> bottomToolbarTypes;

   /**
    * Hold toolbar states for a window at a given time.
    *
    * @param toolBarType       - Type of uppper Toolbar associated with window
    * @param bottomToolbarType - Bottom toolbar associated with window
    * @param toggleToolBarType - Upper toolbar when toggle from a chart to a grid
    */
   public ToolBarStateManager(final String toolBarType, final BottomToolbarType bottomToolbarType,
                              final String toggleToolBarType) {

      this.toolBarType = toolBarType;
      this.toggleToolBarType = toggleToolBarType;
      this.bottomToolbarType = bottomToolbarType;

      this.tempToggleToolBar = toggleToolBarType;
   }

   /**
    * Utility to call for example on new MetaMenuItem construct.
    * We will have to reset toolbars on a window, otherwise when say open a chart window and toggle
    * content to a grid (such that toolbar changed), then if close the whole window
    * and open again, you want the toolbar to be in its original setting.
    */
   public void reset() {
      currentToolBarType = toolBarType;
      currentBottomToolBarType = null;
      tempToggleToolBar = null;
   }

   /**
    * Reset toggle toolbar type temporarily to support
    * toggling for chart drill-down. Set to null to switch back
    * to regular toolbar-toggle toolbar
    *
    * @param tempToggleToolBar reset to null when finished
    */
   public void setTempToggleToolBarType(final String tempToggleToolBar) {
      this.tempToggleToolBar = tempToggleToolBar;
      if (tempToggleToolBar == null) { //resetting
         currentToolBarType = toggleToolBarType;

      }
   }

   /**
    * Utility to toggle upper toolbar type associated with the meta item
    * when switching from a graph to a grid (only for use when have
    * established the window is being toggled - i.e. has a preset response
    * from a previous call)
    */
   public void toggleToolBarType() {

      if (tempToggleToolBar == null) {
         tempToggleToolBar = toggleToolBarType;//reset
      }
      if (!currentToolBarType.equals(tempToggleToolBar) && !currentToolBarType.equals(toolBarType)) {
         currentToolBarType = toolBarType;
      } else {
         currentToolBarType = tempToggleToolBar.equals(currentToolBarType) ? toolBarType : tempToggleToolBar;
      }
   }

   /**
    * BOTTOM (footer) toolbar utility for toggling between graph and grid
    * Because we have introduces a different type of non-paging bottom
    * toolbar and we use this for charts, it will be necessary to be able to
    * toggle bottom toolbars when converting from graph to grid (though
    * in near all cases can assume toggling from PAGING toolbar to PLAIN toolbar
    */
   public void toggleBottomToolBarType() {

      if (currentBottomToolBarType == null) {
         currentBottomToolBarType = bottomToolbarType;
      }
      currentBottomToolBarType = (currentBottomToolBarType == BottomToolbarType.PAGING) ? BottomToolbarType.PLAIN
              : BottomToolbarType.PAGING;

   }

   /**
    * Do not access bottomToolbarType directly to support toggling
    * between the two types (assumes never going to be more for bottom tool bar,
    * else would need similar support to that provided for top toolbar)
    */
   public BottomToolbarType getCurrentBottomToolBarType() {
      return (currentBottomToolBarType == null) ? bottomToolbarType : currentBottomToolBarType;
   }

   /**
    * Do not access toolBarType variable directly.
    * Use this method instead to ensure that
    * toggle toolbar functionality can be maintained
    * (when change from a chart to a grid and vice version
    * the window should be initiailised with a different toolbar
    *
    * @return toolbar type to apply
    */
   public String getCurrentToolBarType() {
      return (currentToolBarType.length() == 0) ? toolBarType : currentToolBarType;
   }

   public void setCurrentToolBarType(final String currentToolBar) {
      this.currentToolBarType = currentToolBar;
   }

   //////////////////////////////   static utilities

   /**
    * Default BOTTOM toolbar for grid is PAGING, default for charts is PLAIN.
    * Support defaults or return as specially set though metadata (e.g.
    * cause code will not be the default grid so it will define its
    * bottom toolbar in meta data)
    *
    * @param bottomToolbarStr String in meta data or an empty string
    * @param windowType       window type (chart, grid) - used to determine a
    *                         suitable default bottom window type should no metadata
    *                         be set (it can be null - e.g. for menuItems holding submenu items)
    *
    * @return Bottom toolbar type for grid or chart
    */
   public static BottomToolbarType convertBottomToolbarType(final String bottomToolbarStr, final Type windowType) {

      if (bottomToolbarTypes == null) {
         bottomToolbarTypes = new HashMap<String, BottomToolbarType>();
         bottomToolbarTypes.put("PLAIN", BottomToolbarType.PLAIN);
         bottomToolbarTypes.put("PAGING", BottomToolbarType.PAGING);
      }
      BottomToolbarType result = bottomToolbarTypes.get(bottomToolbarStr);

      // use defaults as most times won't have to specify any bottom toolbar in metadata)
      if (result == null) {
         if (windowType == Type.CHART) { // windowType can be null
            result = BottomToolbarType.PLAIN;
         } else {
            result = BottomToolbarType.PAGING;
         }
      }
      return result;
   }

   @Override
   public String toString() {
      String toolBarStr = String.valueOf(toolBarType);
      String bottomToolbarStr = String.valueOf(bottomToolbarType);
      final StringBuilder sb = new StringBuilder(156 + bottomToolbarStr.length() + toolBarStr.length());
      sb.append("ToolBarStateManager");
      sb.append("{toolBarType='").append(toolBarStr).append('\'');
      sb.append(", toggleToolBarType='").append(toggleToolBarType).append('\'');
      sb.append(", tempToggleToolBar='").append(tempToggleToolBar).append('\'');
      sb.append(", currentToolBarType='").append(currentToolBarType).append('\'');
      sb.append(", currentBottomToolBarType=").append(currentBottomToolBarType);
      sb.append(", bottomToolbarType=").append(bottomToolbarStr);
      sb.append('}');
      return sb.toString();
   }
}