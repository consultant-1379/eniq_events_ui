package com.ericsson.eniq.events.ui.client.main;

import com.ericsson.eniq.events.common.client.module.IModule;

public interface IEniqEventsModuleRegistry {
    /**
     * @param moduleId
     * @return
     */
    IModule getModule(String moduleId);

    /**
     * @param id
     * @return
     */
    boolean containsModule(String id);
}
