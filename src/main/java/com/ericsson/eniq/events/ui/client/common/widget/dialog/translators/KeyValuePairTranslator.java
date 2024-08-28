package com.ericsson.eniq.events.ui.client.common.widget.dialog.translators;

import com.ericsson.eniq.events.ui.client.datatype.KeyValuePair;
import com.ericsson.eniq.events.widgets.client.dialog.translators.IRecordTranslator;

/**
 * Singleton.
 *
 * @author ealeerm - Alexey Ermykin
 * @since 03 2012
 */
public final class KeyValuePairTranslator implements IRecordTranslator<KeyValuePair> {

   private final static KeyValuePairTranslator INSTANCE = new KeyValuePairTranslator();

   private KeyValuePairTranslator() {
   }

   public static KeyValuePairTranslator getInstance() {
      return INSTANCE;
   }

   @Override
   public boolean isSystem(final KeyValuePair record) {
      return false;
   }

   @Override
   public String getHeader(final KeyValuePair record) {
      return record.getKey();
   }

   @Override
   public String getValue(final KeyValuePair record) {
      return record.getValue();
   }
}