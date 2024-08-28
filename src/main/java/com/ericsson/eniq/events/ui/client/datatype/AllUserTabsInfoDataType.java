/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import java.util.Map;

import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;

/**
 * The class represents all user tabs for "Poison pill"-like design pattern.
 *
 * @author ealeerm - Alexey Ermykin
 * @since 06/2012
 */
final class AllUserTabsInfoDataType extends TabInfoDataType {

    AllUserTabsInfoDataType() {
        super(null, null, null, null, null, true, false);
    }

    @Override
    public boolean isModule() {
        throw neverCall();
    }

    @Override
    public String getId() {
        throw neverCall();
    }

    @Override
    public String getName() {
        throw neverCall();
    }

    @Override
    public String getTip() {
        throw neverCall();
    }

    @Override
    public String getStyle() {
        throw neverCall();
    }

    @Override
    public String getTabItemCenterStyle() {
        throw neverCall();
    }

    @Override
    public boolean isRoleEnabled() {
        return true;
    }

    @Override
    public boolean isPlusTab() {
        return false;
    }

    @Override
    public void setPlusTab(boolean plusTab) {
        throw neverCall();
    }

    @Override
    public boolean isUserTab() {
        return true;
    }

    @Override
    public void setUserTab(boolean isUserTab) {
        throw neverCall();
    }

    @Override
    public void addParameter(String paramName, String value) {
        throw neverCall();
    }

    @Override
    public Map<String, String> getParametersMap() {
        throw neverCall();
    }

    @Override
    public void setWorkspaceState(WorkspaceState workspaceState) {
        throw neverCall();
    }

    @Override
    public WorkspaceState getWorkspaceState() {
        throw neverCall();
    }

    private UnsupportedOperationException neverCall() {
        return new UnsupportedOperationException("Must not be invoked on \"" + this + "\"");
    }

    @Override
    public String toString() {
        return "All-user-tabs";
    }
}
