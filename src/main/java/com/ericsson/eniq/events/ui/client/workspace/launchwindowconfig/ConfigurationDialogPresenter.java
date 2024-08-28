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
package com.ericsson.eniq.events.ui.client.workspace.launchwindowconfig;

import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.ui.client.common.Constants;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.MetaReader;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.workspace.IWindowContainer;
import com.ericsson.eniq.events.ui.client.workspace.WindowParameters;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceUtils;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceWindowController;
import com.ericsson.eniq.events.ui.client.workspace.config.WorkspaceConfigService;
import com.ericsson.eniq.events.ui.client.workspace.launchwindowconfig.datatype.LaunchTypeMenuItem;
import com.ericsson.eniq.events.widgets.client.button.ImageButton;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import java.util.*;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.workspace.WindowPositionHelper.WindowPositionInfo;

public class ConfigurationDialogPresenter extends BasePresenter<ConfigurationDialogView> {

    private static final String EVENT_ID = "eventID=";

    private static final String CALL_DROP = "438";

    private static final String SETUP_FAILURE = "456";

    private WorkspaceWindowController workspaceWindowController;

    private WindowParameters originalWindowParameters;

    private TimeInfoDataType timeInfoDataType;

    private final Map<ImageButton, ConfigWidgetView> configWidgets = new HashMap<ImageButton, ConfigWidgetView>();

    private static final String TITLE_ACCESS_AREA = "Call Failure Analysis : By Access Area";

    private static final String TITLE_CONTROLLER = "Call Failure Analysis : By Controller";

    private int left = 0;

    private int top = 0;

    private MetaMenuItem metaMenuItem;

    private String configWindowId;

    private MetaReader metaReader;

    private WorkspaceConfigService configService;

    private static final String WINDOW_ID_FOR_ACCESS_AREA = "NETWORK_CELL_RANKING_RAN_WCDMA_CFA_CONFIG";

    /**
     * @param view
     *        * - view
     * @param eventBus
     *        * - eventBus
     * @param metaReader
     *        * - metaReader
     * @param configService
     *        * - configService
     */
    @Inject
    public ConfigurationDialogPresenter(ConfigurationDialogView view, EventBus eventBus, MetaReader metaReader, WorkspaceConfigService configService) {
        super(view, eventBus);
        this.metaReader = metaReader;
        this.configService = configService;
        bind();
    }

    public void launch(final TimeInfoDataType timeInfoDataType, final MetaMenuItem item, final WindowParameters windowParameters,
                       final WindowPositionInfo windowPosition, final WorkspaceWindowController workspaceWindowController,
                       IWindowContainer windowContainer, String windowId,boolean restoring) {
        this.configWindowId = windowId; //want to be able to tile/cascade with other windows
        configWidgets.clear();
        this.metaMenuItem = item;
        this.workspaceWindowController = workspaceWindowController;
        this.originalWindowParameters = windowParameters;
        this.timeInfoDataType = timeInfoDataType;
        this.left = windowPosition.getX();
        this.top = windowPosition.getY();

        //todo remove when groups stories for this feature is complete, they all launch same service request!
        if (this.originalWindowParameters.getSearchData().isGroupMode()) {
            this.workspaceWindowController.removeConfigWindowFromMap(this.configWindowId);
            continueWithLaunch(windowPosition);
        }
        else if(restoring){
            launchWindow(windowPosition, windowParameters.getExtraURLParams(),windowId);
        }else{
            getView().launch(windowContainer);
            setWindowTitle(item);
        }
    }

    private void continueWithLaunch(WindowPositionInfo windowPosition) {

        String windowID = WorkspaceUtils.generateId();

        SearchFieldDataType originalSFDT = this.originalWindowParameters.getSearchData();
        SearchFieldDataType searchFieldDataType = createNewSearchFieldDataType(originalSFDT, originalSFDT.urlParams, "");

        WindowParameters newWindowParams = createNewWindowParameters(this.originalWindowParameters, this.metaMenuItem.getID().replace("_CONFIG", ""),
                searchFieldDataType, "", "");
        this.workspaceWindowController.addWindowFromConfig(windowID, newWindowParams);

        MetaMenuItem gridMetaMenuItem = metaReader.getMetaMenuItemFromID(this.metaMenuItem.getID().replace("_CONFIG", ""));
        this.workspaceWindowController.launchFromConfig(timeInfoDataType, gridMetaMenuItem, windowPosition, true, windowID);
    }

    public void onLaunchWindows() {
        List<String> launchList = getLaunchSelections();

        WindowPositionInfo windowPosition = new WindowPositionInfo(0, 0, this.top, this.left);
        this.workspaceWindowController.removeConfigWindowFromMap(this.configWindowId);
        for (String url : launchList) {
            String windowID = WorkspaceUtils.generateId();
            launchWindow(windowPosition, url,windowID);
        }
        workspaceWindowController.tile(workspaceWindowController.isPinned());
    }

    private void launchWindow(WindowPositionInfo windowPosition, String url,String windowID) {


        int urlArraySize = this.originalWindowParameters.getSearchData().urlParams.length;

        String[] newUrlArray = new String[urlArraySize + 1];
        for (int i = 0; i < urlArraySize; i++) {
            newUrlArray[i] = this.originalWindowParameters.getSearchData().urlParams[i];
        }
        newUrlArray[urlArraySize] = url;

        SearchFieldDataType originalSFDT = this.originalWindowParameters.getSearchData();

        SearchFieldDataType searchFieldDataType = createNewSearchFieldDataType(originalSFDT, newUrlArray,
                workspaceWindowController.translateCategory(url));

        WindowParameters newWindowParams = createNewWindowParameters(this.originalWindowParameters,
                this.metaMenuItem.getID(), searchFieldDataType, url, "CONFIG");

        this.workspaceWindowController.addWindowFromConfig(windowID, newWindowParams);
        MetaMenuItem gridMetaMenuItem = metaReader.getMetaMenuItemFromID(this.metaMenuItem.getID().replace("_CONFIG", ""));
        this.workspaceWindowController.launchFromConfig(timeInfoDataType, gridMetaMenuItem, windowPosition, true, windowID);
    }

    public double getLeft() {
        return this.left;
    }

    public double getTop() {
        return this.top;
    }

    private void setWindowTitle(MetaMenuItem item) {
        if (WINDOW_ID_FOR_ACCESS_AREA.contains(item.getID())) {
            getView().setWindowTitle(TITLE_ACCESS_AREA);
        } else {
            getView().setWindowTitle(TITLE_CONTROLLER);
        }
    }

    //so the view can add the widgets to the list as they hit the plus button
    public Map<ImageButton, ConfigWidgetView> getConfigWidgets() {
        return configWidgets;
    }

    public List<String> getLaunchSelections() {
        List<String> launchOptions = new ArrayList<String>();
        for (ConfigWidgetView configWidget : configWidgets.values()) {
            LaunchTypeMenuItem launchMenuItem = configWidget.getSelectedLaunchType();
            String launchParam;

            if (configWidget.getDrops().getValue()) {
                launchParam = buildURLParams(EVENT_ID, CALL_DROP, AMPERSAND, CATEGORY_ID, launchMenuItem.getLaunchType(), AMPERSAND);
                String drillType = buildURLParams(DRILL_CAT, CALL_DROP, UNDERSCORE, launchMenuItem.getLaunchType());
                launchOptions.add(buildURLParams(launchParam, drillType));
            }

            if (configWidget.getSetup().getValue()) {
                //launchParam Example:             eventID=456&categoryID=2&
                launchParam = buildURLParams(EVENT_ID, SETUP_FAILURE, AMPERSAND, CATEGORY_ID, launchMenuItem.getLaunchType(), AMPERSAND);
                //drillType Example:               drillType=456_2
                String drillType = buildURLParams(DRILL_CAT, SETUP_FAILURE, UNDERSCORE, launchMenuItem.getLaunchType());
                launchOptions.add(buildURLParams(launchParam, drillType));
            }
        }
        return launchOptions;
    }

    public boolean checkDuplicateValues() {
        boolean existDuplicate = checkExistingValues();
        if (existDuplicate) {
            getView().changeStatusIcon(true);
            return true;
        } else {
            getView().changeStatusIcon(false);
            getView().updateLaunchButtonText();
            return false;
        }
    }

    public boolean checkExistingValues() {
        List<String> failuresList = new ArrayList<String>();
        for (ConfigWidgetView configWidget : getConfigWidgets().values()) {
            failuresList.add(configWidget.getSelectedLaunchType().toString());
        }
        for (int i = 0; i < configWidgets.size() - 1; i++) {
            int total = Collections.frequency(failuresList, failuresList.get(i));
            if (total > 1) {
                return true;
            }
        }
        return false;
    }

    /*
     * buildURLParams converts numerous strings to a single String Example buildURLParams( EVENT_ID, CALL_DROP, AMPERSAND) sb.append (EVENT_ID);
     * //eventID= sb= eventID sb.append (CALL_DROP); //438 sb= eventID=438 sb.append(AMPERSAND): //& sb= eventID=438& returned "eventID=438&"
     */
    private String buildURLParams(String... params) {
        StringBuilder sb = new StringBuilder();

        for (String param : params) {
            sb.append(param);
        }
        return sb.toString();
    }

    private SearchFieldDataType createNewSearchFieldDataType(SearchFieldDataType originalSFDT, String[] newUrlArray, String category) {

        /* SearchField Data Type seens as nulll if the field searchFieldValue has no value */
        String searchFieldValue = originalSFDT.getSearchFieldVal();
        if (searchFieldValue == "" || searchFieldValue == null) {
            searchFieldValue = Constants.NOT_FOR_DISPLAY;
        }

        SearchFieldDataType searchFieldDataType = new SearchFieldDataType(searchFieldValue, newUrlArray, originalSFDT.getType(),
                originalSFDT.getTitlePrefix(), category, originalSFDT.isGroupMode(), "CS,PS", originalSFDT.getGroupValues(),
                originalSFDT.isPathMode());

        return searchFieldDataType;
    }

    /* Remove Config & wrap up parameters today */
    private WindowParameters createNewWindowParameters(WindowParameters originalWindowParams, String newWindowID,
                                                       SearchFieldDataType serachFieldData, String url, String extraUrlType) {

        WindowParameters newWindowParams = new WindowParameters(this.originalWindowParameters.getDimension(), originalWindowParams.getTimePeriod(),
                originalWindowParams.getFrom(), originalWindowParams.getTo(), originalWindowParams.getPrimarySelection(),
                originalWindowParams.getPairedSelectionUrl(), originalWindowParams.getSecondarySelection(), configService.getWindow(newWindowID),
                serachFieldData, originalWindowParams.getCategory(), originalWindowParams.getTechnology(), url, extraUrlType);
        return newWindowParams;
    }
}
