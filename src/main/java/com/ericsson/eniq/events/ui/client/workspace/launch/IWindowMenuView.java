/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch;

import java.util.List;

import com.ericsson.eniq.events.ui.client.workspace.launch.AbstractWindowMenuPresenter.DimensionGroup;
import com.ericsson.eniq.events.ui.client.workspace.launch.AbstractWindowMenuPresenter.PairedSuggestion;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface IWindowMenuView {

    void setGroups(List<DimensionGroup> groups);

    void unmask();

    void setInitalSuggestions(List<PairedSuggestion> suggestions);

}
