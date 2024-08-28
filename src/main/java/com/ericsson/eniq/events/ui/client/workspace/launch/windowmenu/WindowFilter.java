/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.TechnologyType;
import com.ericsson.eniq.events.ui.client.workspace.config.WorkspaceConfigService;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IDimension;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IWindow;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IWindowIdList;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IWorkspaceSubMenu;
import com.ericsson.eniq.events.ui.client.workspace.launch.WorkspaceFilterUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Perform filtering of the Window Display Panel based on selected dimensions, query String, supported Technologies
 * @author ecarsea
 * @since 2012
 *
 */
public class WindowFilter {

    private final WorkspaceConfigService configService;

    private final IWindowFilterView windowFilterView;

    private IDimension selectedDimension;

    private TechnologyType currentTechnologyType;

    /**
     * @param configService
     * @param windowFilterView
     */
    public WindowFilter(WorkspaceConfigService configService, IWindowFilterView windowFilterView) {
        this.configService = configService;
        this.windowFilterView = windowFilterView;
    }

    /**
     * Query updated. update filter with currently selected dimension and technology and latest query string
     * @param query
     */
    public void updateFilter(String query) {
        filterItems(selectedDimension, currentTechnologyType, query);
    }

    /**
     * Dimension selected, reset technology and query string
     * @param dimension
     */
    public void updateFilter(IDimension dimension) {
        filterItems(dimension, null, "");
    }

    public void updateFilter(IDimension dimension, TechnologyType technology) {
        filterItems(dimension, technology, "");
    }

    private void filterItems(IDimension dimension,TechnologyType technologyType, String query) {
        this.selectedDimension = dimension;
        this.currentTechnologyType = technologyType;
        windowFilterView.clearFilter();
        windowFilterView.removeAllCategoryPanels();

        for (IWorkspaceSubMenu subMenu : configService.getWorkspaceMenu().getWorkspaceSubMenu()) {
            IWindowIdList windowIdList = subMenu.getWindows();
            List<String> licensedWindows = configService.getLicensedWindowIdListForSubMenu(windowIdList);
            final ListDataProvider<WindowItem> dataProvider = new ListDataProvider<WindowItem>();
            for (String windowId : licensedWindows) {
                IWindow window = configService.getWindow(windowId);
                if(supportsDimension(window) && supportsTechnology(window)) {
                    if(WorkspaceFilterUtils.containsQuery(query, subMenu.getName()) ||
                            WorkspaceFilterUtils.containsQuery(query, window.getWindowTitle())) {
                        dataProvider.getList().add(new WindowItem(WorkspaceFilterUtils.getFormattedName(window.getWindowTitle()), window));
                    }
                }
            }
            Collections.sort(dataProvider.getList(), new Comparator<WindowItem>() {

                @Override
                public int compare(WindowItem w1, WindowItem w2) {
                    return w1.getWindow().getWindowTitle().compareToIgnoreCase(w2.getWindow().getWindowTitle());
                }
            });
            if (dataProvider.getList().size() > 0) {
                windowFilterView.addItem(subMenu.getName(), dataProvider);
            }
        }
        windowFilterView.addAllCategoryPanels();
    }

    private boolean supportsTechnology(IWindow window) {
        if (currentTechnologyType == null) {
            /** no technology specified **/
            return true;
        }
        for (String supportedTechnologyName : window.getSupportedTechnologies().getTechnology()) {
            if (supportedTechnologyName.equals(currentTechnologyType.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Dimension is supported by this window if it is either:
     * 1) In the list of supported dimensions
     * 2) Absent from the list of excluded dimensions.
     * and if a technology is specified as part of the dimension, it must be supported.
     * @param window
     * @return
     */
    private boolean supportsDimension(IWindow window) {
        List<String> supportedDimensions = (window.getSupportedDimensions() != null) ? window.getSupportedDimensions()
                .getDimensionId() : new ArrayList<String>();
        List<String> excludedDimensions = (window.getExcludedDimensions() != null) ? window.getExcludedDimensions()
                .getDimensionId() : new ArrayList<String>();
        if(selectedDimension!=null){
        String dimensionId = selectedDimension.getId();
        if (!isEmptyList(supportedDimensions) && supportsDimensionTechnology(window)) {
            if (!isEmptyList(excludedDimensions) && excludedDimensions.contains(dimensionId)) {
                return false;
            }
            return supportedDimensions.contains(dimensionId) || supportedDimensions.contains(selectedDimension.getGroupType());
        }
       }
        return false;
    }

    private boolean isEmptyList(List<String> list) {
        return list == null || list.isEmpty();
    }

    /**
     * Some dimensions support particular technologies. Check if there any supported technologies for the current selected dimension and if
     * so, check if the window supports those technologies.
     * @param window
     * @return
     */
    protected boolean supportsDimensionTechnology(IWindow window) {
        if (selectedDimension.getSupportedTechnologies() != null) {
            List<String> requiredTechnologies = selectedDimension.getSupportedTechnologies().getTechnology();
            if (window.getSupportedTechnologies() != null) {
                for (String supportedTechnology : window.getSupportedTechnologies().getTechnology()) {
                    if (requiredTechnologies.contains(supportedTechnology)) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
        return true;
    }

    public List<WindowItem> getSelectedItems() {
        return windowFilterView.getSelectedItems();
    }

    public static class WindowItem {
        private final SafeHtml formattedName;

        private final IWindow window;

        public WindowItem(SafeHtml formattedName, IWindow window) {
            this.formattedName = formattedName;
            this.window = window;
        }

        public SafeHtml getFormattedName() {
            return formattedName;
        }

        public IWindow getWindow() {
            return window;
        }
    }
}