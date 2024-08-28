/**
/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.json.IJSONArray;
import com.ericsson.eniq.events.common.client.json.IJSONObject;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.ui.client.common.Constants;
import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.ericsson.eniq.events.ui.client.common.RequestCallbackImpl;
import com.ericsson.eniq.events.ui.client.common.ServerComms;
import com.ericsson.eniq.events.ui.client.datatype.GroupSelectInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchGroupModelData;
import com.ericsson.eniq.events.ui.client.events.*;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.ui.client.resources.EniqResourceBundle;
import com.ericsson.eniq.events.widgets.client.button.ImageButton;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.ID;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.JSON_ROOTNODE;

/**
 * Class introduced to give user option to select a group of IMSIs or APNs etc.
 * An info button must be available to view the IMSIs in the group.
 * The operator can either select the IMSI group to make a call or
 * type in an IMSI.
 * <p/>
 * There are additional considerations when node type is involved also with
 * search selection, i.e. in this case this group component will also need to
 * be aware of type changes in the search component. When search component type is
 * not group this component must disappear and vice versa).
 *
 * @author eeicmsy
 * @since May 2010
 */
public class GroupTypeSearchComponent extends Component implements ISearchComponent, SearchFieldTypeChangeEventHandler,
        GroupSingleToggleEventHandler, MaskEventHandler {

    private final static Logger LOGGER = Logger.getLogger(GroupTypeSearchComponent.class.getName());

    private final GroupSelectInfoDataType setupInfo;

    private final SearchComboBox<SearchGroupModelData> groupComboBox = createGroupComboBox();

    private final ListStore<SearchGroupModelData> groupStore = new ListStore<SearchGroupModelData>();

    private final EniqResourceBundle eniqResourceBundle = MainEntryPoint.getInjector().getEniqResourceBundle();

    private final ImageButton submitButton = new ImageButton(eniqResourceBundle.launchIconToolbar());

    private final ImageButton infoButton = new ImageButton(eniqResourceBundle.infoIconToolbar());

    private final ClickHandler submitGroupButtonListener = new SubmitGroupButtonListener();

    private final ClickHandler submitInfoButtonListener = new SubmitInfoButtonListener();

    private final SelectionChangedListener<SearchGroupModelData> groupSelectionChangedListener = new ComboSelectionChangedListener();

    private final HorizontalPanel groupCompPanel = new HorizontalPanel();

    private String remoteGroupURL;

    private String emptyText;

    /* (when required) The unique type (e.g. SGSN, APN, CELL) -
    *  ONLY applicable when the search component is a "paired" search type
    */
    private String nodeType;

    /*
    * Supporting CS and PS from search tpye selection
    */
    private String splitStringMetaDataKeys;

    /** e.g. "CS" or "PS" or empty */
    private String metaDataRef = EMPTY_STRING;

    /**
     * Can assume this will be set
     * to handle search input
     */
    private final List<ISubmitSearchHandler> groupSubmitHandlers = new ArrayList<ISubmitSearchHandler>();

    /*
    * tab owner id (needed to respond correctly to
    * event on event bus) when search component shared accross tabs
    * (particularly toggling from group component to single component)
    */
    private final String tabOwnerId;

    private final KeyListener comboKeyListener = new ComboKeyListener();

    private final MouseListener mouseListener = new MouseListener();

    private EventBus eventBus = null;

    private final static String QUERY_ID = "GroupTypeSearchComponent";

    /*
    * Though we are not a window, reusing same signature
    * for server calls
    */
    private MultipleInstanceWinId multiWinId = null;

    /**
     * boolean used to track whether UI is masked - set in handleMaskEvent method and used in handleControlButtonEnabling method
     * default access because of junit GroupTypeSearchComponent
     */
    boolean isMasked;

    /*
    * Cache handlers this component adds to eventbus - for removal later
    */
    private final List<HandlerRegistration> registoredHandlers = new ArrayList<HandlerRegistration>();

    /**
     * Set up Group type search component where user
     * selects a group of items to submit to a window query
     * or else views information for a group name
     *
     * @param setupInfo data required to set up component
     */
    public GroupTypeSearchComponent(final String tabOwnerId, final GroupSelectInfoDataType setupInfo) {

        this.setupInfo = setupInfo;
        this.tabOwnerId = tabOwnerId;

        submitButton.setEnabled(false);
        infoButton.setEnabled(false);

        if (setupInfo.checkIfTypeInfoMapIsEmpty()) { // no types
            remoteGroupURL = setupInfo.getLoadGroupURL();
            emptyText = setupInfo.getEmptyText();
            splitStringMetaDataKeys = setupInfo.getSplitStringMetaDataKeys();
            nodeType = null;
        } else {
            // fall over if default not set correctly in meta data
            String type = setupInfo.getDefaultIDType();
            setupForType(type, setupInfo.getGroupTypeText()); //NOPMD (eemecoy 1/6/10, required to get under unit test)
        }

    }

    /*
    * Change load URL, etc.,  for type change
    * (do nothing if type selected is not in types map,
    * e.g only for APNGroup, BSCGroup, CELLGroup, SGSNGroup )
    */
    private void setupForType(final String typeSelected, String typeText) {

        final GroupSelectInfoDataType.TypeInfo groupTypeInfo = setupInfo.getTypeInfo(typeSelected);

        if (groupTypeInfo != null) { // in correct group component
            remoteGroupURL = groupTypeInfo.getLoadGroupURL();
            emptyText = groupTypeInfo.getEmptyText();
            splitStringMetaDataKeys = groupTypeInfo.getSplitStringMetaDataKeys();
            nodeType = typeSelected;
            groupComboBox.setEmptyText(emptyText);
        }

        setupInfo.setGroupTypeText(typeText);
    }

    ////////////////////////////////////////////
    //////   Implement ISearchComponent
    ////////////////////////////////////////////

    @Override
    public void setLicencedTypesVisibleOnly(final List<String> licencedTypes) {

        /* NOT applicable for GroupSelectInfoDataType.TypeInfo, i.e. we will add "winMetaSupport": "PS" to
        "searchFields" tag (combined search omponent and group component) to ensure group type menu items are
        shown or not shown */

    }

    @Override
    public SearchFieldDataType getSearchComponentValue() {

        /* e.g.  type=APN&groupname=SomeGroup */
        /* NOTE NOTE NOTE - (there be a hack) - very important for drilldown on groups that this order
        * is maintained (type first with groupname at end - search users of GROUP_VALUE_PARAM )
        */

        final String val = groupComboBox.getRawValue();
        if (val == null) {
            return null;
        }

        if (nodeType == null) { // TODO don't know why it would be yet (IMSI group)
            nodeType = setupInfo.getGroupType();
        }

        String nodeTypeText = setupInfo.getGroupTypeText();
        if ("TERMINAL_TAB".equals(tabOwnerId)) {
            nodeTypeText = setupInfo.getGroupTypeTextFromEmptyText();
        }

        final String[] urlParams = new String[] { Constants.GROUP_TYPE_PARAM + nodeType,
                Constants.GROUP_VALUE_PARAM + val };

        Collection<String> groupValues = null;
        if (groupComboBox.getValue() != null) {
            groupValues = groupComboBox.getValue().getGroupValues();
        }

        return new SearchFieldDataType(val, urlParams, nodeType, nodeTypeText, true, splitStringMetaDataKeys,
                groupValues, false);
    }

    @Override
    public Component getSearchComponent() {
        return groupCompPanel;
    }

    @Override
    public void registerWithEventBus(final EventBus eventBus) {
        /* no more use of event bus as can talk
        direct to MenuTaskBar using ISubmitSearchHandler */
        this.eventBus = eventBus;
        registoredHandlers.add(this.eventBus.addHandler(SearchFieldTypeChangeEvent.TYPE, this));
        registoredHandlers.add(this.eventBus.addHandler(GroupSingleToggleEvent.TYPE, this));
        registoredHandlers.add(this.eventBus.addHandler(MaskEvent.TYPE, this));
        registoredHandlers.add(this.eventBus.addHandler(FailedEvent.TYPE, new FailedResponseHandler()));

        init();
    }

    @Override
    public void unregistorWithEventBus() {
        for (final HandlerRegistration handler : registoredHandlers) {
            handler.removeHandler();
        }

    }

    @Override
    public void addSubmitSearchHandler(final ISubmitSearchHandler searchSubmitHandler) {
        groupSubmitHandlers.add(searchSubmitHandler);

    }

    @Override
    public void removeSubmitSearchHandler(final ISubmitSearchHandler searchSubmitHandler) {
        groupSubmitHandlers.remove(searchSubmitHandler);

        if (groupSubmitHandlers.isEmpty()) {
            unregistorWithEventBus();
        }

    }

    /* extra for only allowing group or search on display at same time */
    @Override
    public void setVisible(final boolean visible) {
        if (groupComboBox != null) {
            groupComboBox.setVisible(visible);
            submitButton.setVisible(visible);
            infoButton.setVisible(visible);
        }
    }

    /* important not to use default - i.e. using to know if single or group has precedence */
    @Override
    public boolean isVisible() {
        return (groupComboBox != null && groupComboBox.isVisible());
    }

    @Override
    public String getMetaChangeComponentRef() {
        return metaDataRef;
    }

    @Override
    public void setMetaChangeComponentRef(final String metaDataRef) {
        this.metaDataRef = metaDataRef;

    }

    ////////////////////////////////////////////
    //////   Implement SearchFieldTypeChangeEventHandler
    ////////////////////////////////////////////

    /* takes no other action than set visibility (for group-single toggle)
    * if group search component does not share this type,
    * only performed when has a type option - refreshing if called before
    */
    @Override
    public void handleTypeChanged(final String tabId, final String typeSelected, final boolean isGroup,
            final String typeText) {

        // guard
        if (!tabOwnerId.equals(tabId)) { // neccessary as tab changes can activate listeners
            return;
        }

        /* combining search and group components (for when used without specific
         * toggle component - i.e. when include groups in type component */
        GroupTypeSearchComponent.this.setVisible(isGroup);

        if (isGroup && nodeType != null) { // group component with type
            setupForType(typeSelected, typeText);
            sendCallToPopulateStore();
        } else {
            setupInfo.setGroupTypeText(typeText);
        }
    }

    ////////////////////////////////////////////
    //////   Implement GroupSingleToggleEventHandler
    ////////////////////////////////////////////

    @Override
    public void toggleGroupSingleDisplay(final String tabId, final boolean setGroupVisible) {
        // guard
        if (!tabOwnerId.equals(tabId)) {
            return;
        }
        GroupTypeSearchComponent.this.setVisible(setGroupVisible);
    }

    ///////////////////////////////////////////////////////////////////

    /* initialise internal components */
    private void init() {

        setUpSubmitButton();
        setUpInfoButton();
        setUpGroupComboBox();

        // slight hack ( we know our meta data default is not a group) */
        setVisible(false);

        addComponentsToPanel();

        sendCallToPopulateStore();

    }

    /* access for junit (as can not mock combobox) group box holding group names */
    void setUpGroupComboBox() {

        groupComboBox.setEmptyText(emptyText);
        groupComboBox.setWidth(200);
        groupComboBox.setDisplayField(SearchGroupModelData.DISPLAY_FIELD);
        groupComboBox.setTypeAhead(true);
        groupComboBox.setTriggerAction(TriggerAction.ALL);
        groupComboBox.setStore(groupStore); // (not populated but needs something to start with)
        groupComboBox.setTemplate(getGroupComboTemplate(GWT.getHostPageBaseURL()));
        groupComboBox.addSelectionChangedListener(groupSelectionChangedListener);
        groupComboBox.addKeyListener(comboKeyListener);
        groupComboBox.sinkEvents(Event.ONPASTE); // attach the ONPASTE event to the groupComboBox component
        groupComboBox.addListener(Events.OnPaste, mouseListener);
        groupComboBox.setId(SELENIUM_TAG + "groupComboBox");
        groupComboBox.setListStyle("x-liveload");

    }

    /*
    * Submit button to send full selection to server
    * for population of windows and grids etc with selection
    * Necessary because "enter" press is live load call
    */
    private void setUpSubmitButton() {
        submitButton.setHoverImage(eniqResourceBundle.launchIconToolbarHover());
        submitButton.setDisabledImage(eniqResourceBundle.launchIconToolbarDisable());

        submitButton.setTitle(setupInfo.getTip());
        submitButton.addClickHandler(submitGroupButtonListener);
        submitButton.getElement().setId(SELENIUM_TAG + "launchButton");
        submitButton.getElement().getStyle().setMarginLeft(3, Unit.PX);
        submitButton.getElement().getStyle().setPaddingTop(3, Unit.PX);

    }

    /*
    * Pressing info button will display content of selected group
    */
    private void setUpInfoButton() {
        infoButton.setHoverImage(eniqResourceBundle.infoIconToolbarHover());
        infoButton.setDisabledImage(eniqResourceBundle.infoIconToolbarDisable());

        infoButton.setTitle(setupInfo.getInfoTip());
        infoButton.addClickHandler(submitInfoButtonListener);
        infoButton.getElement().setId(SELENIUM_TAG + "infoButton");
        infoButton.getElement().getStyle().setMarginLeft(2, Unit.PX);
        infoButton.getElement().getStyle().setMarginTop(3, Unit.PX);

    }

    /*
    * Native code for combobox icons using GXT templates
    * Set VIP icon on groups that a VIPs
    * @param hostLocation  (Want images inside EniqEventsUI - and needed this to find them)
    */
    private native String getGroupComboTemplate(final String hostLocation) /*-{
		return [
				'<tpl for=".">',
				'<div class="x-liveload-item"><img width="16px" height="11px" src="'
						+ hostLocation
						+ 'resources/images/{[values.VIP_ICON]}.png"> {[values.name]}</div>',
				'</tpl>' ].join("");
    }-*/;

    /* replace content of combo store (for new type) */
    private void refreshStore(final List<SearchGroupModelData> groupList) {
        clearStoreSilent();
        groupComboBox.getStore().add(groupList);
        groupComboBox.fireEvent(Store.Update);
    }

    /* not firing store update (yet) */
    private void clearStoreSilent() {
        groupComboBox.clearSelections();
        groupComboBox.removeToolTip();
        handleControlButtonEnabling();
        groupComboBox.getStore().removeAll();
    }

    /* clear combo store (for new type) - failed */
    private void clearGroupStore() {
        clearStoreSilent();
        groupComboBox.fireEvent(Store.Update);
    }

    /* access for junit - remote load the group store once */
    void sendCallToPopulateStore() {
        /* ensure all server communication though same methods */
        getServerCommHandler().makeServerRequest(getMultipleInstanceWinId(), remoteGroupURL, EMPTY_STRING);

    }

    private MultipleInstanceWinId getMultipleInstanceWinId() {
        if (multiWinId == null) {
            multiWinId = new MultipleInstanceWinId(tabOwnerId, QUERY_ID); //not for multi-instance no need for search data
        }
        return multiWinId;
    }

    /* server error display */
    private void displayGroupLoadServerError(final Throwable ex) {
        LOGGER.log(Level.WARNING, "Group not found (in display method)", ex);
        // ONLY comment back below in when actually expect groups to handle all types (and you actually
        // want to show dialog
        //XX final MessageDialog errorDialog = new MessageDialog();
        //XXX errorDialog.displayErrorDialog("Failure receiving group data from server!", ex);
    }

    private void handleControlButtonEnabling() {
        final String searchString = groupComboBox.getRawValue();
        final boolean searchStringLengthZero = searchString == null || searchString.length() == 0;

        if ((submitButton != null) && (infoButton != null)) {
            if (isMasked) {
                AbstractPairedTypeSearchComponent.isPairedSubmitButtonAlreadyClicked = true;
            } else {
                AbstractPairedTypeSearchComponent.isPairedSubmitButtonAlreadyClicked = false;
            }
            if (!searchStringLengthZero && !isMasked) {
                submitButton.setEnabled(true);
                infoButton.setEnabled(true);
            } else {
                submitButton.setEnabled(false);
                infoButton.setEnabled(false);
            }
        }
    }

    /* extracted for junit */
    Button createButton() {
        return new Button();
    }

    /* extracted for junit
    * apply specific style to allow
    * for widen the display area
    * */
    SearchComboBox<SearchGroupModelData> createGroupComboBox() {

        return new SearchComboBox<SearchGroupModelData>();

    }

    /* extracted for junit */
    void addComponentsToPanel() {

        groupCompPanel.add(groupComboBox);
        groupCompPanel.add(infoButton);
        groupCompPanel.add(submitButton);
    }

    /* extracted (to override) for junit */
    JSONValue parseText(final String s) {
        if (s != null && s.length() > 0) {
            return JSONUtils.parseLenient(s);
        }
        return null;
    }

    @Override
    public void handleMaskEvent(final boolean isMasked, final String tabOwner) {
        if (tabOwner.equals(tabOwnerId)) {
            this.isMasked = isMasked;

            handleControlButtonEnabling();
        }
    }

    /*
    * Addition to ensure all server communication goes though one class
    *
    * @return ServerComms  server communication helper
    */
    @SuppressWarnings("unchecked")
    ServerComms getServerCommHandler() {
        return new ServerCommsHandler(eventBus);
    }

    @SuppressWarnings("unchecked")
    private class ServerCommsHandler extends ServerComms {

        public ServerCommsHandler(final EventBus eventBus) {
            super(eventBus);
        }

        @Override
        public RequestCallbackImpl getRequestCallbackImpl(final MultipleInstanceWinId multiWinID,
                final String requestData) {
            return new GroupRequestCallbackImpl(getMultipleInstanceWinId(), eventBus, EMPTY_STRING);
        }

    }

    /**
     * Handle success response when event bus fired a SucessResponseEvent
     * Indicating server has returned with some success result
     */
    private final class FailedResponseHandler implements FailedEventHandler {

        /* (non-Javadoc)
        * @see com.ericsson.eniq.events.ui.client.events.FailedEventHandler#handleFail(java.lang.String, java.lang.String, java.lang.String, java.lang.Throwable)
        */
        @Override
        public void handleFail(final MultipleInstanceWinId multiWinID, final String requestData,
                final Throwable exception) {

            if (!GroupTypeSearchComponent.this.getMultipleInstanceWinId().isThisWindowGuardCheck(multiWinID)) {
                return;
            }

            LOGGER.log(Level.WARNING, "Bad response receiving group data!", exception);
            displayGroupLoadServerError(exception);

        }

    }

    /**
     * Class included to support all server communication to
     * pass through ServerComms method
     */
    private class GroupRequestCallbackImpl extends RequestCallbackImpl {

        public GroupRequestCallbackImpl(final MultipleInstanceWinId multiWinID, final EventBus eventBus,
                final String requestData) {

            super(multiWinID, eventBus, requestData);

        }

        ////////////////////////////////////////////
        //////   Override RequestCallback
        ////////////////////////////////////////////

        @Override
        public void onError(final Request request, final Throwable exception) {

            clearGroupStore();
            LOGGER.log(Level.WARNING, "Group combobox failed returing from server", exception);
            displayGroupLoadServerError(exception);

        }

        @Override
        public void onResponseReceived(final Request request, final Response response) {
            if (STATUS_CODE_OK == response.getStatusCode()) {

                final JSONValue responseValue = parseText(response.getText());

                if (responseValue != null && JSONUtils.checkData(responseValue)) {
                    final JsonObjectWrapper metaData = new JsonObjectWrapper(responseValue.isObject());
                    final IJSONArray data = metaData.getArray(JSON_ROOTNODE);

                    final List<SearchGroupModelData> groupList = new ArrayList<SearchGroupModelData>();
                    for (int i = 0; i < data.size(); i++) {
                        final IJSONObject parent = data.get(i);
                        final String name = parent.getString(SearchGroupModelData.DISPLAY_FIELD);
                        final boolean isVIP = CommonConstants.TRUE.equals(parent.getString(SearchGroupModelData.VIP));
                        final IJSONArray values = parent.getArray(SearchGroupModelData.VALUES);

                        final Collection<String> stringVals = new ArrayList<String>(); // NOPMD by eeicmsy on 12/05/10 22:33

                        for (int v = 0; v < values.size(); v++) {
                            final IJSONObject vparent = values.get(v);
                            stringVals.add(vparent.getString(ID));
                        }
                        groupList.add((new SearchGroupModelData(name, isVIP, stringVals))); // NOPMD by eeicmsy on 12/05/10 22:33
                    }
                    Collections.sort(groupList);
                    refreshStore(groupList);
                }

            } else {
                LOGGER.log(Level.WARNING, "Bad response receiving group data!");
                clearGroupStore();
                displayGroupLoadServerError(null);

            }

        }
    }

    /** Info button press for group launches gruop information dialog */
    private class SubmitInfoButtonListener implements ClickHandler {

        @Override
        public void onClick(final ClickEvent event) {
            performActionForInfoSelected();
        }

        private void performActionForInfoSelected() {

            final ModelData val = groupComboBox.getValue();

            if (val != null) {
                if (val instanceof SearchGroupModelData) {
                    final SearchGroupModelData group = (SearchGroupModelData) val;
                    final Collection<String> elements = group.getGroupValues();

                    if (elements != null) {
                        final StringBuilder sb = new StringBuilder();
                        for (final String groupValue : group.getGroupValues()) {
                            sb.append(groupValue);
                            sb.append("\n");
                        }

                        final MessageDialog dialog = new MessageDialog();
                        dialog.setGlassEnabled(true);
                        dialog.getElement().getStyle().setZIndex(Math.max(Constants.ENIQ_EVENTS_BASE_ZINDEX + 3,
                                XDOM.getTopZIndex()));
                        dialog.show(group.getGroupName() + " Group", sb.toString(),
                                MessageDialog.DialogType.INFO);
                    } else {
                        LOGGER.log(Level.WARNING, "No values to display for group info press");
                    }
                }
            }
        }
    }

    /** user selects item from group combobox and presses play button */
    private class SubmitGroupButtonListener implements ClickHandler {
        @Override
        public void onClick(final ClickEvent event) {
            performActionForGroupItemSelected();
        }

        /* this is more for submit button press with group (bit late for tool tip) */
        private void performActionForGroupItemSelected() {
            AbstractPairedTypeSearchComponent.isPairedSubmitButtonAlreadyClicked = true;
            groupComboBox.setToolTip(groupComboBox.getRawValue());

            for (final ISubmitSearchHandler groupSubmitHandler : groupSubmitHandlers) {
                groupSubmitHandler.submitSearchFieldInfo(); // group takes precedence
            }
        }

    }

    private class ComboSelectionChangedListener extends SelectionChangedListener<SearchGroupModelData> {
        @Override
        public void selectionChanged(final SelectionChangedEvent<SearchGroupModelData> se) {
            handleControlButtonEnabling(); // has values (from selection list ) - so enable
            groupComboBox.setToolTip(groupComboBox.getRawValue());
        }
    }

    /**
     * Listener for Mouse Paste Click event.
     * To enable the Submit/Play button on Paste.
     */
    private class MouseListener implements Listener<ComponentEvent> {
        @Override
        public void handleEvent(final ComponentEvent be) {
            if (!isMasked) {
                submitButton.setEnabled(true);
            }
        }
    }

    class ComboKeyListener extends KeyListener {

        String lastSearchFieldTextValue;

        @Override
        public void componentKeyUp(final ComponentEvent event) {
            handleControlButtonEnabling();
            groupComboBox.setToolTip(groupComboBox.getRawValue());

            if (lastSearchFieldTextValue == null || !lastSearchFieldTextValue.equals(groupComboBox.getRawValue())) {
                submitButton.setEnabled(false);
            }

            lastSearchFieldTextValue = groupComboBox.getRawValue();
        }
    }

}
