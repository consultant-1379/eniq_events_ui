/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.multiselect;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import com.ericsson.eniq.events.ui.client.datatype.group.GroupMgmtConfigDataType;
import com.ericsson.eniq.events.ui.client.groupmanagement.GroupElementRetriever;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class MultiSelectGroupRegistryImpl implements MultiSelectGroupRegistry {

    private static final String PLMN = "MCC_MNC";

    private final Map<String, Provider<? extends AbstractMultiSelectPresenter>> templateMap = new HashMap<String, Provider<? extends AbstractMultiSelectPresenter>>();

    private final EventBus eventBus;

    @Inject
    public MultiSelectGroupRegistryImpl(final Provider<PlmnMultiSelectPresenter> plmnMultiSelectPresenterProvider,
            EventBus eventBus) {
        templateMap.put(PLMN, plmnMultiSelectPresenterProvider);
        this.eventBus = eventBus;
    }

    @Override
    public AbstractMultiSelectPresenter getMultiSelectPresenter(final String name) {
        final Provider<? extends AbstractMultiSelectPresenter> provider = templateMap.get(name);
        if (provider == null) {
            throw new IllegalArgumentException("Multi Select Presenter name " + name + " was not found.");
        }
        return provider.get();
    }

    @Override
    public GroupElementRetriever getGroupElementRetriever(final String name, GroupMgmtConfigDataType configDataType) {
        if (name.equals(PLMN)) {
            return new PlmnGroupElementRetriever(eventBus, configDataType);
        }
        return new GroupElementRetriever(eventBus, configDataType);
    }
}
