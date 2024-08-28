package com.ericsson.eniq.events.ui.client.search;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

class SearchComboBox<T extends BaseModelData> extends ComboBox<T> {

    @Override
    protected void onTypeAhead() {
        super.onTypeAhead();
        super.setToolTip(this.getRawValue());
    }

    public void clearLastQuery() {
        lastQuery = null;
    }
}
