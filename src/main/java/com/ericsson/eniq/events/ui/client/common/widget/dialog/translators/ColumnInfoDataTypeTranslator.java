package com.ericsson.eniq.events.ui.client.common.widget.dialog.translators;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.widgets.client.dialog.translators.IRecordTranslator;
import com.extjs.gxt.ui.client.data.ModelData;

/**
 * @author ealeerm - Alexey Ermykin
 * @since 03 2012
 */
public class ColumnInfoDataTypeTranslator implements IRecordTranslator<ColumnInfoDataType> {

   final ModelData modelData;

   public ColumnInfoDataTypeTranslator(final ModelData modelData) {
      this.modelData = modelData;
   }

   @Override
   public boolean isSystem(final ColumnInfoDataType record) {
      return record.isSystem;
   }

   @Override
   public String getHeader(final ColumnInfoDataType record) {
      return record.columnHeader;
   }

   @Override
   public String getValue(final ColumnInfoDataType record) {
       return String.valueOf(modelData.get(record.columnID)); // To return string regardless of value type
   }
}
