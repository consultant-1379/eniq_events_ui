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

public class CollapsibleSectionState implements ICollapsibleSectionState{
    String Id;
    boolean isCollapsed;

    @Override
    public String getId() {
        return Id;
    }

    @Override
    public void setId(String id) {
        this.Id = id;
    }

    @Override
    public boolean isCollapsed() {
        return isCollapsed;
    }

    @Override
    public void setCollapsed(boolean isCollapsed) {
        this.isCollapsed = isCollapsed;
    }
}
