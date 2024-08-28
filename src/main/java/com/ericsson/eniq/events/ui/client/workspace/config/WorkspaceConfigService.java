/*------------------------------------------------------------------------------
        *******************************************************************************
        * COPYRIGHT Ericsson 2014
        *
        * The copyright to the computer program(s) herein is the property of
        * Ericsson Inc. The programs may be used and/or copied only with written
        * permission from Ericsson Inc. or in accordance with the terms and
        * conditions stipulated in the agreement/contract under which the
        * program(s) have been supplied.
        *******************************************************************************
        *----------------------------------------------------------------------------*/

package com.ericsson.eniq.events.ui.client.workspace.config;

import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.datatype.LicenseInfoDataType;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceConfigAutoBeanFactory;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.*;
import com.ericsson.eniq.events.ui.client.workspace.datatype.PredefinedWorkspaceState;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WindowState;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;
import com.ericsson.eniq.events.ui.client.workspace.launch.WorkspaceLaunchMenuResourceBundle;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import javax.inject.Inject;
import java.util.*;

/**
 * @since 2012
 */
public class WorkspaceConfigService {
    private IWorkspaceConfiguration workspaceConfiguration;

    private final Map<String, IDimension> dimensionMap = new HashMap<String, IDimension>();

    private final Map<String, IWindow> windowMap = new HashMap<String, IWindow>();

    private final Map<String, String> windowCategoryMap = new HashMap<String, String>();

    private PredefinedWorkspaceState predefinedWorkspaceWrapper;

    private IMetaReader metaReader;

    @Inject
    public WorkspaceConfigService(WorkspaceLaunchMenuResourceBundle resources,
            WorkspaceConfigAutoBeanFactory autoBeanFactory, IMetaReader metaReader) {
        loadWorkspaceMetaData(resources, autoBeanFactory, metaReader);
        loadPredefinedWorkspaces(resources, autoBeanFactory, metaReader);
    }

    private void loadWorkspaceMetaData(WorkspaceLaunchMenuResourceBundle resources,WorkspaceConfigAutoBeanFactory autoBeanFactory, IMetaReader metaReader) {

        String configData = resources.workspaceLaunchConfig().getText();
        IWorkspaceConfigurationWrapper workspaceConfigurationWrapper = AutoBeanCodex.decode(autoBeanFactory, IWorkspaceConfigurationWrapper.class, configData).as();
        List<LicenseInfoDataType>  licenses = metaReader.getLicenses();
        List<String> supportedAccessGroups = metaReader.getSupportedAccessGroups();

        workspaceConfiguration = workspaceConfigurationWrapper.getWorkspaceConfiguration();

        for (IDimension dimension : workspaceConfiguration.getDimensions().getDimension()) {
            if (dimension.getSupportedAccessGroups() != null) {
                if (shouldShowDimension(licenses, supportedAccessGroups, dimension)) {
                    dimensionMap.put(dimension.getId(), dimension);
                }
            }
        }

        for (IWindow window : workspaceConfiguration.getWindows().getWindow()) {
            /** Only allow windows that are in the EE meta data, so unlicensed windows will not be available in the workspace manager **/
            if (metaReader.getMetaMenuItemDataType(window.getId()) != null) {
                if (window.getSupportedAccessGroups() != null) {
                    if (isGrantedAccess(supportedAccessGroups, window.getSupportedAccessGroups().getAccessGroup())) {
                        windowMap.put(window.getId(), window);
                    }
                }
            }
        }

        for (IWorkspaceSubMenu category : workspaceConfiguration.getWorkspaceMenu().getWorkspaceSubMenu()) {
            for (String windowId : category.getWindows().getWindowId()) {
                windowCategoryMap.put(windowId, category.getName());
            }
        }
    }

    private boolean shouldShowDimension(List<LicenseInfoDataType> licenses, List<String> supportedAccessGroups, IDimension dimension) {
        return isGrantedAccess(supportedAccessGroups, dimension.getSupportedAccessGroups().getAccessGroup())&& isLicensed(licenses, dimension.getSupportedLicenses().getLicenses());
    }

    private boolean isGrantedAccess(List<String> supportedUserAccessGroups, List<String> supportedItemAccessGroups){
        boolean result = false;
        
        for(String item: supportedItemAccessGroups){
            if (supportedUserAccessGroups.contains(item)){
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean isLicensed( List<LicenseInfoDataType>  licences, List<String> supportedLicenses){
        for(LicenseInfoDataType licence: licences){
            if ( supportedLicenses.contains(licence.getFeatureName())){
                return true;
            }
        }
        return false;
    }

    private void loadPredefinedWorkspaces(WorkspaceLaunchMenuResourceBundle resources,
            WorkspaceConfigAutoBeanFactory autoBeanFactory, IMetaReader metaReader) {
        String predefinedWorkspaces = resources.predefinedWorkspaces().getText();
        predefinedWorkspaceWrapper = AutoBeanCodex.decode(autoBeanFactory, PredefinedWorkspaceState.class,
                predefinedWorkspaces).as();

        for(WorkspaceState workspaceState : predefinedWorkspaceWrapper.getWorkspaces())   {
            ArrayList<WindowState> windowStates = new ArrayList<WindowState>();

            for(WindowState windowState : workspaceState.getWindows()) {
                if(metaReader.getMetaMenuItemDataType(windowState.getWindowId()) != null) {
                    windowStates.add(windowState);
                }
            }
            workspaceState.setWindows(windowStates);
        }
    }

    public Collection<WorkspaceState> getPredefinedWorkspaces() {
        if (predefinedWorkspaceWrapper != null && predefinedWorkspaceWrapper.getWorkspaces() != null) {
            return predefinedWorkspaceWrapper.getWorkspaces();
        }


        return Collections.emptyList();
    }

    public IDimensionMenu getDimensionMenu() {
        return workspaceConfiguration.getDimensionMenu();
    }

    /**
     * @param dimensionId
     * @return
     */
    public IDimension getDimension(String dimensionId) {
        return dimensionMap.get(dimensionId);
    }

    /**
     * @param windowId
     * @return
     */
    public IWindow getWindow(String windowId) {
        return windowMap.get(windowId);
    }

    /**
     * @return 
     * @return
     */
    public IWorkspaceMenu getWorkspaceMenu() {
        return workspaceConfiguration.getWorkspaceMenu();
    }

    /**
     * Return the windows applicable for this sub menu. This method will filter out any unlicensed windows
     * @param windowList
     * @return
     */
    public List<String> getLicensedWindowIdListForSubMenu(IWindowIdList windowList) {
        List<String> licensedWindowList = new ArrayList<String>();
        for (String windowId : windowList.getWindowId()) {
            if (windowMap.containsKey(windowId)) {
                licensedWindowList.add(windowId);
            }
        }
        return licensedWindowList;
    }

    public String getWindowCategory(String windowId) {
        String windowCategory =  windowCategoryMap.get(windowId);

        //if the windowCategory is null this might be because the window was launched from a config window.
        //in this case we just append the "_CONFIG" to the end of the windowID to check.
        if (windowCategory == null){
            windowCategory = windowCategoryMap.get(windowId + "_CONFIG");
        }
        return windowCategory;
    }
}
