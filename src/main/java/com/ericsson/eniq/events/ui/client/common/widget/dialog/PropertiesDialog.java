/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget.dialog;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.ui.client.common.widget.IEventGridView;
import com.ericsson.eniq.events.ui.client.common.widget.dialog.translators.ColumnInfoDataTypeTranslator;
import com.ericsson.eniq.events.widgets.client.dialog.APropertiesDialog;
import com.extjs.gxt.ui.client.data.ModelData;

/**
 * PropertiesWindow is a non modal dialog used to display a row a data
 * selected in the grid
 */
public class PropertiesDialog extends APropertiesDialog<ColumnInfoDataType> {

   private final GridInfoDataType gridMetaData;

   /**
    * PropertiesWindow is a non modal dialog used to display a row a data
    * selected in the grid
    *
    * @param gridRef provides a handler to the methods in EventGridView
    */
   public PropertiesDialog(final IEventGridView gridRef) {
      this(gridRef, null);

      init();
   }

   /**
    * PropertiesWindow is a non modal dialog used to display a row a data
    * selected in the grid
    *
    * @param gridRef   provides a handler to the methods in EventGridView
    * @param presenter presenter to unload on close of the dialog
    */
   public PropertiesDialog(final IEventGridView gridRef, final BasePresenter presenter) {
      this(gridRef.getViewSettings().getTaskBarButtonAndInitialTitleBarName(), gridRef.getGridRecordSelected(),
              gridRef.getColumns(), presenter);
   }

   /**
    * PropertiesWindow is a non modal dialog used to display a row a data selected in the grid.
    *
    * @param strTitle     dialog title
    * @param modelData    model data
    * @param gridMetaData data for grid
    * @param presenter    presenter to unload on close of the dialog
    */
   public PropertiesDialog(final String strTitle, final ModelData modelData, final GridInfoDataType gridMetaData,
                           final BasePresenter presenter) {
      super(strTitle, new ColumnInfoDataTypeTranslator(modelData), presenter);
      this.gridMetaData = gridMetaData;
   }

   @Override
   protected StringBuilder iterateRecords(StringBuilder sb) {
      if (gridMetaData.columnInfo != null) {
         for (final ColumnInfoDataType record : gridMetaData.columnInfo) {
            processRecord(record, sb);
         }
      }
      return sb;
   }
}