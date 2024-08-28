/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget.dialog;

import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.ui.client.common.widget.dialog.translators.KeyValuePairTranslator;
import com.ericsson.eniq.events.ui.client.datatype.KeyValuePair;
import com.ericsson.eniq.events.widgets.client.dialog.APropertiesDialog;

import java.util.List;

/**
 * PropertiesDialogForKeyValuePairList is a non modal dialog used to display rows of {@link
 * com.ericsson.eniq.events.ui.client.datatype.KeyValuePair}
 * <p/>
 * Note: assuming that the dialog has two columns only: key and value pairs.
 *
 * @see com.ericsson.eniq.events.ui.client.datatype.KeyValuePair
 */
public class PropertiesDialogForKeyValuePairList extends APropertiesDialog<KeyValuePair> {

   private final List<KeyValuePair> recordsList;

   /**
    * PropertiesWindow is a non modal dialog used to display a row a data selected in the grid.
    *
    * @param strTitle    window title
    * @param recordsList data for grid
    */
   public PropertiesDialogForKeyValuePairList(final String strTitle, final List<KeyValuePair> recordsList,
                                              final BasePresenter basePresenter) {
      super(strTitle, KeyValuePairTranslator.getInstance(), basePresenter);
      this.recordsList = recordsList;

      init();
      setWidthInPx(430);
   }

   @Override
   protected StringBuilder iterateRecords(final StringBuilder sb) {
      if (recordsList != null) {
         for (final KeyValuePair record : recordsList) {
            processRecord(record, sb);
         }
      }
      return sb;
   }
}
