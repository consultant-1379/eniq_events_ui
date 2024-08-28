/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ericsson.eniq.events.ui.client.workspace.WorkspaceUtils;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;
import com.ericsson.eniq.events.ui.client.workspace.launch.WorkspaceFilterUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Perform filtering of the Window Display Panel based on selected dimensions, query String, supported Technologies
 * @author ecarsea
 * @since 2012
 *
 */
public class WorkspaceFilter {

    private final IWorkspaceFilterView workspaceFilterView;

    List<WorkspaceState> workspaceStates = new ArrayList<WorkspaceState>();

    private String header;

    private Collection<String> startupItems;

    /**
     * @param workspaceFilterView
     */
    public WorkspaceFilter(IWorkspaceFilterView workspaceFilterView) {
        this.workspaceFilterView = workspaceFilterView;
    }

    public void updateWorkspaceList(List<WorkspaceState> workspaceList, Collection<String> startupItems, String header) {
        this.workspaceStates = workspaceList;
        this.startupItems = startupItems;
        this.header = header;
        filter("");
    }

    public void filter(String query) {
        final ListDataProvider<WorkspaceStateItem> startupDataProvider = new ListDataProvider<WorkspaceStateItem>();
        final ListDataProvider<WorkspaceStateItem> workspacesDataProvider = new ListDataProvider<WorkspaceStateItem>();
        workspaceFilterView.clearFilter();
        for (WorkspaceState workspaceState : workspaceStates) {

            if (WorkspaceUtils.isStartupItem(startupItems, workspaceState)) {
                doFilter(query, startupDataProvider, workspaceState);
            }
            doFilter(query, workspacesDataProvider, workspaceState);
        }
        sort(startupDataProvider);
        sort(workspacesDataProvider);
        workspaceFilterView.addStartupItems(startupDataProvider);
        workspaceFilterView.addWorkspacesItems(workspacesDataProvider, header);
    }

    private void sort(ListDataProvider<WorkspaceStateItem> dataProvider) {
        Collections.sort(dataProvider.getList(), new Comparator<WorkspaceStateItem>() {

            @Override
            public int compare(WorkspaceStateItem w1, WorkspaceStateItem w2) {
                return w1.getFormattedName().asString().compareToIgnoreCase(w2.getFormattedName().asString());
            }
        });
    }

    private void doFilter(String query, final ListDataProvider<WorkspaceStateItem> dataProvider,
            WorkspaceState workspaceState) {
        if (WorkspaceFilterUtils.containsQuery(query, workspaceState.getName())) {
            dataProvider.getList().add(
                    new WorkspaceStateItem(WorkspaceFilterUtils.getFormattedName(workspaceState.getName()),
                            workspaceState, WorkspaceUtils.isStartupItem(startupItems, workspaceState)));
        }
    }

    public static class WorkspaceStateItem {
        private final SafeHtml formattedName;

        private final WorkspaceState workspaceState;

        private final boolean launchOnStartup;

        /**
         * @param formattedName
         * @param workspaceState
         */
        public WorkspaceStateItem(SafeHtml formattedName, WorkspaceState workspaceState, boolean launchOnStartup) {
            super();
            this.formattedName = formattedName;
            this.workspaceState = workspaceState;
            this.launchOnStartup = launchOnStartup;
        }

        /**
         * @return the formattedName
         */
        public SafeHtml getFormattedName() {
            return formattedName;
        }

        /**
         * @return the workspaceState
         */
        public WorkspaceState getWorkspaceState() {
            return workspaceState;
        }

        public boolean isLaunchOnStartup() {
            return launchOnStartup;
        }
    }

    public IWorkspaceFilterView getView() {
        return workspaceFilterView;
    }

}
