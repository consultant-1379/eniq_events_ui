/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 ******************************************************************************* */

package com.ericsson.eniq.events.ui.client.wcdmauertt;

import java.util.Map;

public class CollapsibleSection {

    private final ICollapsibleSectionState sectionState;

    private final Map<String, String> details;

    public CollapsibleSection(final ICollapsibleSectionState sectionState, final Map<String, String> details) {
        this.sectionState = sectionState;
        this.details = details;
    }

    public ICollapsibleSectionState getSectionState() {
        return sectionState;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setCollapsed(boolean isCollapsed) {
        sectionState.setCollapsed(isCollapsed);
    }
}
