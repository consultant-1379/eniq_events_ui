package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.ui.shared.enums.LicenceGroupType;
import com.ericsson.eniq.events.widgets.client.overlay.translators.GroupTranslator;

/**
 * Singleton.
 *
 * @author ealeerm - Alexey Ermykin
 * @since 03 2012
 */
public class LicenceGroupTranslator implements GroupTranslator<LicenceGroupType> {

    private final static LicenceGroupTranslator INSTANCE = new LicenceGroupTranslator();

    private LicenceGroupTranslator() {
    }

    public static LicenceGroupTranslator getInstance() {
        return INSTANCE;
    }

    @Override
    public String getId(final LicenceGroupType group) {
        return group.getId();
    }

    @Override
    public String getName(final LicenceGroupType group) {
        return group.getFullVisibleName();
    }
}
