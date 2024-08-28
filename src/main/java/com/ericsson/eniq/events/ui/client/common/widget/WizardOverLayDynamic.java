/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget;

import com.ericsson.eniq.events.common.client.json.IJSONArray;
import com.ericsson.eniq.events.common.client.json.IJSONObject;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.WizardInfoDataType;
import com.ericsson.eniq.events.ui.client.events.RefreshWindowEventHandler;
import com.ericsson.eniq.events.ui.client.events.SucessResponseEvent;
import com.ericsson.eniq.events.ui.client.events.SucessResponseEventHandler;
import com.ericsson.eniq.events.ui.client.events.TimeParameterValueChangeEventHandler;
import com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay;
import com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessageType;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.RequestTimeoutException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.web.bindery.event.shared.EventBus;

import java.util.*;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;

/**
 * Wizard Over Lay that
 * <li>uses server call to populate checkboxes </li>
 * <li> sends selection as parameters in call </li>
 * <p/>
 * Extracting WizardOverLay that was used for cause code
 *
 * @author eeicmsy (from eendmcm)
 * @see com.ericsson.eniq.events.ui.client.common.widget.WizardOverLayFixedResultSet
 * @since June 2011
 */
public class WizardOverLayDynamic<D extends IExtendedWidgetDisplay> extends AbstractWizardOverLay<D> {

    /* flag used in guard to determine if this control should
     * listen on the bus for success responses */
    private boolean listenForResponse = true;

    /* (change) preserve current selection for time change, don't revert to grid-char choice, but
     * attempt to redisplay previous selections if they still exist following time change 
     */
    private static Map<MultipleInstanceWinId, String> cachedCheckBoxSelection = new HashMap<MultipleInstanceWinId, String>();

    private ServerCallLaunchSelectionListener launchSelectionListener;

    public WizardOverLayDynamic(final MetaMenuItem metaMenu, final IWorkspaceController workspaceController,
            final WizardInfoDataType wizardInfo, final BaseWindowPresenter<? extends WidgetDisplay> basePresenter,
            final EventBus bus, final D display) {
        super(metaMenu, workspaceController, wizardInfo, basePresenter, bus, display);

        setUpWizardToUseLoadURL();

    }

    /* adding direct search field update for wizard panel 
       check-boxes if search field changes (when register) 
       (particularly for wizard (cause code) when options change for search field 
       as apposed to "fixed meta data"*/
    @Override
    public void submitSearchFieldInfo() {

        final SearchFieldDataType searchBeingSubmitted = workspaceController
                .getSearchComponentValue(((BaseWindow) baseWinPresenter.getView()).getBaseWindowID());

        if (searchBeingSubmitted != null && baseWinPresenter.isExcludedSearchType(searchBeingSubmitted.getType())) {
            return;
        }

        if (isSearchDataRequiredMissing()) {
            return;
        }

        // start again - new wizard  options required
        setFirstLaunch(true);
        listenForResponse = true;
        generateWizardRequest();

    }

    @Override
    public void loadWizardData() {
        generateWizardRequest();

    }

    @Override
    public ClickHandler getLaunchSelectionListener() {
        if (launchSelectionListener == null) {
            launchSelectionListener = new ServerCallLaunchSelectionListener();
        }
        return launchSelectionListener;
    }

    @Override
    public RefreshWindowEventHandler getWizardRefreshHandler() {
        return new DynamicWizardRefreshHandler();
    }

    @Override
    public TimeParameterValueChangeEventHandler getWizardTimeChangeHandler() {
        return new DynamicWizardTimeChangeHandler();
    }

    /*
     * Builds the REST URL including parameters and invokes
     * a call to the service for the required data 
     * to dynamically create check-boxes to select
     */
    private void generateWizardRequest() {

        final SearchFieldDataType searchData = baseWinPresenter.getSearchData();

        if (isSearchDataRequiredMissing()) {
            return;
        }

        final StringBuilder sParameters = new StringBuilder();

        final boolean isAddingSearch = isSearchFieldUser(); // not added isSearchUser to wizard
        if (isAddingSearch) {
            // launch button press will have checked for search field info
            if (searchData != null && !searchData.isEmpty()) {
                sParameters.append(searchData.getSearchFieldURLParams(true));
            }
        }

        final TimeInfoDataType timeData = baseWinPresenter.getTimeData();
        // final append time parameters (harmless if not used)
        sParameters.append((timeData == null ? EMPTY_STRING : timeData.getQueryString(!isAddingSearch)));
        // final append timeZone info
        sParameters.append(CommonParamUtil.getTimeZoneURLParameter());
        // LOAD URL is to fetch checkboxes
        getServerComms().makeServerRequest(baseWinPresenter.getMultipleInstanceWinId(), wizardInfo.getLoadURL(),
                sParameters.toString());

    }

    /*
     * register the handler for the response that will populate the bottom Panel
     */
    private void setUpWizardToUseLoadURL() {

        final WizardSuccessResponseHandler wizardDataHandler = new WizardSuccessResponseHandler(cpResponseHolder);
        baseWinPresenter.registerHandler(eventBus.addHandler(SucessResponseEvent.TYPE, wizardDataHandler));

    }

    @Override
    public void clearCachedChartData() {
        cachedCheckBoxSelection.remove(baseWinPresenter.getMultipleInstanceWinId());
    }

    @Override
    public boolean hasChartCache() {
        return cachedCheckBoxSelection.containsKey(baseWinPresenter.getMultipleInstanceWinId());
    }

    private List<String> getCachedCheckBoxSelection() {

        final String commaSepCachedCauseCodeIds = cachedCheckBoxSelection.get(baseWinPresenter
                .getMultipleInstanceWinId());

        return (commaSepCachedCauseCodeIds == null || commaSepCachedCauseCodeIds.isEmpty()) ? new ArrayList<String>()
                : new ArrayList<String>(Arrays.asList(commaSepCachedCauseCodeIds.split(COMMA)));
    }

    /**
     * Handle success response when event bus fired a SucessResponseEvent
     * Indicating server has returned with some success result for the wizard
     */
    private final class WizardSuccessResponseHandler implements SucessResponseEventHandler {

        private final ContentPanel holderPanel;

        public WizardSuccessResponseHandler(final ContentPanel pnl) {
            holderPanel = pnl;
        }

        @Override
        public void handleResponse(final MultipleInstanceWinId multiWinId, final String requestData,
                final Response response) {

            //guards 
            if (!listenForResponse) {
                return;
            }

            // Have to check search field data here when in multi-mode
            if (!baseWinPresenter.getMultipleInstanceWinId().equals(multiWinId)) {
                return;
            }

            /* Initial data for view received, don't listen handle response
             * in here anymore unless this flag is reset */
            listenForResponse = false;
            //Wizard Response Received.
            parseResponse(response);
            display.stopProcessing();
        }

        /*
         * Parses the response from the service call
         * and populates the holderPanel with the data
         */
        private void parseResponse(final Response response) {

            //Clean start
            resetToStarterState();

            if (STATUS_CODE_OK == response.getStatusCode()) {

                final JSONValue responseValue = checkAndParse(response.getText());
                //check for success
              if (responseValue != null && JSONUtils.checkData(responseValue)) {

                    final JsonObjectWrapper metadata = new JsonObjectWrapper(responseValue.isObject());
                    final IJSONArray arrCause = metadata.getArray(JSON_ROOTNODE);

                    final List<DynamicCheckboxesData> checkboxesDataList = new ArrayList<DynamicCheckboxesData>();

                    final List<String> cachedSelection = getCachedCheckBoxSelection();
                    if (!cachedSelection.isEmpty()) {
                        // not relyuing on grid raio button listener if was chart selection
                        btnLaunch.setEnabled(false);
                    }
                   List<String> topTenValues= new ArrayList<String>();

                    for (int i = 0; i < arrCause.size(); i++) {

                        final IJSONObject objCauseCode = arrCause.get(i);

                        DynamicCheckboxesData data ;
                        final String code = objCauseCode.getString(CC_WIZARD_ID);
                        final String desc = objCauseCode.getString(CC_WIZARD_DESC);
                        if(getWindowId().equalsIgnoreCase(NETWORK_CAUSE_CODE_ANALYSIS)) {
                            final String protType = objCauseCode.getString("3");
                            final String protTypeDesc = objCauseCode.getString("4");
                            data = new DynamicCheckboxesData(code,desc,protType, protTypeDesc);
                        }   else {
                            data = new DynamicCheckboxesData(code, desc);
                        }
                        checkboxesDataList.add(data);
                        if(i<10){
                            topTenValues.add(String.valueOf(data.getCode())+"_"+String.valueOf(data.getCauseProtType()));
                        }
                    }

                  List<CheckBox> topTenCheckbox = new ArrayList<CheckBox>();

                    Collections.sort(checkboxesDataList);

                    for (final DynamicCheckboxesData data : checkboxesDataList) {
                        final CheckBox chk = new CheckBox();
                        if(getWindowId().equalsIgnoreCase(NETWORK_CAUSE_CODE_ANALYSIS)){
                            chk.setText(CC_WIZARD_DESC_TEXT + String.valueOf(data.getCode()) +" ("+data.getCauseProtTypeDesc()+")");
                            chk.setFormValue(String.valueOf(data.getCode())+"_"+String.valueOf(data.getCauseProtType()));
                            chk.setTitle(data.getDescription()+" ("+data.getCauseProtTypeDesc()+")"); // tooltip
                            if(topTenValues.contains(String.valueOf(chk.getFormValue())))  {
                                topTenCheckbox.add(chk);
                            }
                        } else {
                            chk.setText(CC_WIZARD_DESC_TEXT + String.valueOf(data.getCode()));
                            chk.setFormValue(String.valueOf(data.getCode()));
                            chk.setTitle(data.getDescription()); // tooltip
                            if(topTenValues.contains(String.valueOf(data.getCode())))  {
                                topTenCheckbox.add(chk);
                            }
                        }
                        chkAll.registerChild(chk);

                        //  cached checkbox found (code must be unique) 
                        if (cachedSelection.contains(data.getCode())) {
                            chk.setValue(true, true);
                            btnLaunch.setEnabled(true);
                        }

                        checkboxes.add(chk);
                        holderPanel.add(chk);
                    }

                  chkAll.setTopTenCheckbox(topTenCheckbox);
                    // back to default start up
                    //If no CC for the provided Criteria disable options
                    cpWizardRadioGroup.enableRadioChart(arrCause.size() != 0);
                    chkAll.setEnabled(arrCause.size() != 0);

                    // cached cause coded (chart previously)
                    if (chkAll.isAnyChildSelected()) {

                        cpWizardRadioGroup.selectChartButton(true);
                        final ClickHandler handler = getLaunchSelectionListener();
                        if (handler != null) {
                            ((ServerCallLaunchSelectionListener) handler).launchAsChart();
                        }
                    } else {

                        toggleRadioStatus(cpWizardRadioGroup.isChartSelected());
                        setWizardFixed();
                    }

                    holderPanel.layout(true);
                }

                setWizardFixed();
            }

            if (checkboxes.isEmpty()) {
                if(response.getText().contains(LICENSE_ERROR_DESCRIPTION_SUBSTRING)){
                    showMessagePanel(ERROR, LICENSE_ERROR,ComponentMessageType.ERROR);
                }
                else{
                    showMessagePanel(WARNING, NO_CAUSE_CODES_FOUND_WIZARD_MESSAGE,ComponentMessageType.WARN);
                }

            }
        }

        /*
         * parses a string into a JSONValue
         */
        private JSONValue checkAndParse(final String text) {
            final JSONValue responseValue = null;
            ( (AbstractBaseWindowDisplay) baseWinPresenter.getView()).hideErrorMessage();

            try {
                if (text != null && text.length() > 0) {
                     return JSONUtils.parse(text);
                }
            } catch (final JSONException e) {
                // server should not have passed success, 
                // e.g. now trying to parse an error message (500 error)
                displayException(e, text);
            }
            return responseValue;

        }

        private void displayException(final Throwable exception, final String text) {
            AbstractBaseWindowDisplay display = (AbstractBaseWindowDisplay) baseWinPresenter.getView();
            if (exception instanceof RequestTimeoutException) {
                display.showErrorMessage(ComponentMessageType.ERROR, TIMEOUT_EXCEPTION, exception.getMessage());
            } else {
                /* text can be pure html (from glassfish response) */
                display.showErrorMessage(ComponentMessageType.ERROR, CHECK_GLASSFISH_LOG_MESSAGE + text, exception.getMessage());
            }
        }
    }

    /*
     * handles the end user changing the time on a window that contains a wizard
     * as the dynamic content of the wizard needs to be updated based on provided time criteria
     * 
     * Replacing existing time listener that the window will have - this only 
     * updates checkboxes (and by default would set all checkboxes as unchecked (which which will affect graph if open
     * 
     * 
     * TODO IDEALLY we have to cache previosly checked checkboxes if chart is open. Then if these
     * checkboxes still exist (or some of them exist), we should redraw the pie with them for the 
     * new time range
     * 
     */
    private final class DynamicWizardTimeChangeHandler implements TimeParameterValueChangeEventHandler {

        /* (non-Javadoc)
         * @see com.ericsson.eniq.events.ui.client.events.TimeParameterValueChangeEventHandler#handleTimeParamUpdate(com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId, com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType)
         */
        @Override
        public void handleTimeParamUpdate(final MultipleInstanceWinId multiWinId, final TimeInfoDataType time) {

            // Have to check search field data here when in multi-mode
            if (!baseWinPresenter.getMultipleInstanceWinId().equals(multiWinId)) {
                return;
            }
            handleTimeParamUpdate(time);
        }

        @Override
        public void handleTimeParamUpdate(final TimeInfoDataType time) {
            /*
             * Reset the wizard listening Flag
             * and invoke a request for updated wizard content
             */
            listenForResponse = true;
            if (isFirstLaunch()) {

                baseWinPresenter.setTimeData(time);
                generateWizardRequest(); // only replacing checkboxes (and now all will be unchecked when created again

            } else {

                /* for dynamic case (need to fetch new cause codes), 
                 * best to bring right back to the start (can even chose grid)
                 * relaunch with new time
                 */
                baseWinPresenter.setTimeData(time);
                reLaunchWizardChartSelected();
            }
        }

    }

    /*
     * TODO IDEALLY we have to cache previosly checked checkboxes
     */
    private final class DynamicWizardRefreshHandler implements RefreshWindowEventHandler {

        @Override
        public void handleWindowRefresh(final MultipleInstanceWinId multiWinID) {
            if (!baseWinPresenter.getMultipleInstanceWinId().equals(multiWinID)) {
                return;
            }
            handleWindowRefresh();
        }

        @Override
        public void handleWindowRefresh() {
            if (isFirstLaunch()) {

                submitSearchFieldInfo();

            } else {
                /* for dynamic case (need to fetch new cause codes), 
                 * best to bring right back to the start (can even chose grid)
                 */
                reLaunchWizardChartSelected();
            }

        }

    }

    private class ServerCallLaunchSelectionListener implements ClickHandler {

        @Override
        public void onClick(final ClickEvent event) {
            // guard for lack of searchfield  here not needed
            // for dynamic case as would not even load the checkboxes
            // if window did not own search field)

            if (cpWizardRadioGroup.isGridSelected()) {
                launchGrid();
            } else {
                launchAsChart();
            }
        }

        /*
         * generates the query parameters
         */
        private String getAnalysisQueryParameters(final String checkedCodes) {

            final StringBuilder parameterStr = new StringBuilder();
            // time, display, search
            parameterStr.append(WizardOverLayDynamic.this.getServerComms().getInternalRequestData());

            parameterStr.append(CC_WIZARD_QUERY_STRING);
            parameterStr.append(checkedCodes);

            return parameterStr.toString();
        }

        /* (new) For dynamic case want (on time change - refresh) )to both fetch new cause codes but also present old
         * ones that may have been selected
         */
        private void cacheCheckBoxSelection(final String commaSepCachedCauseCodeIds) {

            cachedCheckBoxSelection.put(baseWinPresenter.getMultipleInstanceWinId(), commaSepCachedCauseCodeIds);
        }

        private void launchAsChart() {

            final String checkedCauseCode = getCheckedCauseCodes();

            if (!checkedCauseCode.isEmpty()) {
                cacheCheckBoxSelection(checkedCauseCode);
                final String extraParameters = getAnalysisQueryParameters(checkedCauseCode);
                launchChart(extraParameters);

            }
        }

    }

    /*
     * gets query parameters relating to CC
     * end users chosen options
     * Iterates the checked CC (if any)
     */
    private String getCheckedCauseCodes() {
        final StringBuilder parameterStr = new StringBuilder();
        if (!checkboxes.isEmpty()) {
            for (final CheckBox chk : checkboxes) {
                if (chk != null && chk.getValue()) {
                    parameterStr.append(chk.getFormValue());
                    parameterStr.append(COMMA);
                }
            }

            if (parameterStr.length() > 0) {
                parameterStr.delete(parameterStr.length() - COMMA.length(), parameterStr.length());
            }

        }
        return parameterStr.toString();
    }

    private class DynamicCheckboxesData implements Comparable<DynamicCheckboxesData> {

        private final String code;

        private final String description;

        private final String causeProtType;

        private final String causeProtTypeDesc;

        private DynamicCheckboxesData(final String code, final String description,final String causeProtType, final String causeProtTypeDesc) {
            this.code = code;
            this.description = description;
            this.causeProtType = causeProtType;
            this.causeProtTypeDesc = causeProtTypeDesc;
        }

        private DynamicCheckboxesData(final String code, final String description) {
            this.code = code;
            this.description = description;
            this.causeProtType = null;
            this.causeProtTypeDesc = null;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public String getCauseProtType() {
            return causeProtType;
        }

        public String getCauseProtTypeDesc() {
            return causeProtTypeDesc;
        }

        @Override
        public int compareTo(final DynamicCheckboxesData o) {
            /* better to sort on number part for cause codes (e.g CC10, CC11, CC20)
               i.e. cause code CCNumber - sorted by number part */
            try {
                final int codeInt = Integer.parseInt(code);
                final int otherCodeInt = Integer.parseInt(o.getCode());
                return codeInt - otherCodeInt;

            } catch (final NumberFormatException e) {
                // tooltip or full text
                return description.compareTo(o.description);
            }

        }
    }
}
