package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.common.client.json.IJSONObject;
import com.ericsson.eniq.events.widgets.client.overlay.translators.ItemTranslator;

/**
 * @author ealeerm - Alexey Ermykin
 * @since 03 2012
 */
class JsonObjectWizardItemTranslator implements ItemTranslator<IJSONObject> {

    protected static final JsonObjectWizardItemTranslator INSTANCE = new JsonObjectWizardItemTranslator();

    private JsonObjectWizardItemTranslator() {
    }

    static JsonObjectWizardItemTranslator getInstance() {
        return INSTANCE;
    }

    @Override
    public String getLabel(final IJSONObject item) {
        return item.getString("1");
    }

    @Override
    public String getTooltip(final IJSONObject item) {
        return item.getString("2");
    }

    @Override
    public String getGroupId(final IJSONObject item) {
        return item.getString("3");
    }

    @Override public String getId(IJSONObject item) {
        return "";
    }
}
