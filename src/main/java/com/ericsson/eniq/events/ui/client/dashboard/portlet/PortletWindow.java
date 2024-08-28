/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.ui.client.common.Maskable;
import com.ericsson.eniq.events.ui.client.common.ReadLoginSessionProperties;
import com.ericsson.eniq.events.ui.client.common.ServerComms;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessagePanel;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessageType;
import com.ericsson.eniq.events.ui.client.dashboard.threshold.ThresholdsPresenter;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldUser;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.events.component.ComponentMessageEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletMaskEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletRefreshEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletRefreshEventHandler;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletRemoveEvent;
import com.ericsson.eniq.events.widgets.client.mask.MaskHelper;
import com.ericsson.eniq.events.widgets.client.window.title.TitleWindowResourceBundle;
import com.ericsson.eniq.events.widgets.client.window.title.TitleWindowResourceBundleHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import java.util.Date;
import java.util.logging.Logger;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

/**
 * PortletWindow  - basic class for Dashboard porlet
 *
 * !IMPORTANT!
 * This class is sharing it's resources with TitleWindow for SessionBrowser feature.
 * DO NOT DELETE ANY IMAGES AND CSS USED BY TitleWindow!
 *
 * @author evyagrz
 * @author edavboj
 * @author eeicmsy
 * @since September 2011
 */
public class PortletWindow extends Composite implements HasPortletId, Maskable, ClickHandler,
        PortletRefreshEventHandler, HasRowIndex {

    private static final String GROUP_AS_CSV_URL = "groupAsCsvUrl";

    public static final int BODY_CONTAINER_TOP_OFFSET = 24;

    // TODO loose  maxRows may have to be in porletData (real calls won't need display=chart) ? 
    private final static String EXTRA_URL_PARAMS = "&display=chart&maxRows=500"; // TODO loose

    private static PortletWindowUiBinder uiBinder = GWT.create(PortletWindowUiBinder.class);

    @UiField
    FocusPanel dragHandle;

    @UiField
    HorizontalPanel topContainer;

    @UiField
    SimplePanel buttonContainer;

    @UiField
    SimplePanel bodyContainer;

    @UiField
    SimplePanel messageContainer;

    @UiField
    Label titleLabel;

    @UiField
    SimplePanel closeButtonContainer;

    private static final Logger LOGGER = Logger.getLogger(PortletWindow.class.getName());

    private final String portletId;

    private final PortletDataType portletData;

    private final MultipleInstanceWinId multipleInstanceWinId;

    private String wsURL;

    private final String baseWsURL;

    private final EventBus eventBus;

    private SearchFieldDataType searchData = null;

    private final boolean isPathSearchFieldUser;

    private TimeInfoDataType currentTimeData = null;

    /* URL parameters for time, like 
     * dateFrom=15102011&dateTo=17102011&timeFrom=0200&timeTo=0200
     */
    private String timeDataURLParam = null;

    @UiField(provided = true)
    TitleWindowResourceBundle resourceBundle = TitleWindowResourceBundleHelper.getBundle();

    private MaskHelper maskHelper;

    private int portalHeight;

    private String portletTitle;

    private final ThresholdsPresenter thresholdsPresenter;

    private final boolean isGroupAsCsv;

    private HandlerRegistration portletRefreshHandlerRegistration;

    /**
     * PortletWindow basic class for Dashboard
     *
     * @param porletData          meta data for portlet
     * @param eventBus            default event bus
     * @param thresholdsPresenter thresholds functionality
     */
    // TODO This should be coming from WidgetFactory and Gin instead of passing dependencies
    public PortletWindow(final PortletDataType porletData, final EventBus eventBus,
                         final ThresholdsPresenter thresholdsPresenter) {
        this.portletData = porletData;
        this.eventBus = eventBus;
        this.thresholdsPresenter = thresholdsPresenter;

        initWidget(); // NOPMD by eeicmsy on 09/11/11 17:40

        this.portletId = porletData.getPortletId();

        this.multipleInstanceWinId = getMultipleInstanceWinId(); // NOPMD by eeicmsy on 09/11/11 17:40

        this.wsURL = getCompleteURL(porletData.getURL()); // base url // NOPMD by eeicmsy on 09/11/11 17:40
        this.baseWsURL = wsURL;

        this.isPathSearchFieldUser = SearchFieldUser.PATH == porletData.getSearchFieldUserInfo();

        /** This should be a temporary parameter which is why i am not putting in the portlet data type and just checking for it in the parameters of the portlet **/
        this.isGroupAsCsv = porletData.getParameters().getParameter(GROUP_AS_CSV_URL) != null
                && porletData.getParameters().getParameter(GROUP_AS_CSV_URL).equalsIgnoreCase("TRUE");

        setId(portletId);
        setTitle(porletData.getPortletTitle()); // NOPMD by eeicmsy on 09/11/11 17:40
        setPortalHeight(porletData); // NOPMD by eeicmsy on 09/11/11 17:40

        final Image closeButton = createCloseButton(portletId); // NOPMD by eeicmsy on 11/11/11 19:16
        getCloseButtonContainer().add(closeButton);

        portletRefreshHandlerRegistration = eventBus.addHandler(PortletRefreshEvent.TYPE, this);
    }

    void setId(final String portletId) {
        getElement().setId(portletId);
    }

    void setPortalHeight(final PortletDataType porletData) {
        portalHeight = porletData.getPortletHeight();

        // Height for portlet body container
        getBodyContainer().setHeight(portalHeight - BODY_CONTAINER_TOP_OFFSET + "px");
        getMessageContainer().setHeight(portalHeight - BODY_CONTAINER_TOP_OFFSET + "px");
    }

    @Override
    public void setTitle(final String title) {
        this.portletTitle = title;
        getTitleLabel().setText(title);
    }

    @Override
    public void onRefresh(final PortletRefreshEvent event) {
        if (portletId.equals(event.getComponentId())) { //TODO beaware this means we can not support same porlet in different tabs (i.e. better pass tabif too
            refresh();
        }
    }

    /**
     * Utility for guard checks for external events
     * Dashboard portlets not caring about multiple instances (search data extra)
     *
     * @param other other porlet id
     * @return true if porlethas same id
     */
    public boolean isSamePorlet(final MultipleInstanceWinId other) {

        return this.multipleInstanceWinId.getTabId().equals(other.getTabId())
                && multipleInstanceWinId.getWinId().equals(other.getWinId());
    }

    /**
     * Utility to return current search data in force by porlet window
     * (needed because can not read search component directly as it could be out of synch
     * with other porlets if play button not pressed)
     *
     * @return current search data
     */
    public SearchFieldDataType getCurrentSearchData() {
        return searchData;
    }

    /**
     * Utility returning current Time data (intended that may be used for drilldowns)
     *
     * @return current time data for porlet
     */
    public TimeInfoDataType getCurrentTimeData() {
        return currentTimeData;
    }

    /**
     * Only called on windows interested in search field (so not checking)
     * <p/>
     * Make new request to services for porlet
     * with new search field data, assuming this window
     * is interested in search field updates
     * <p/>
     * Resets WS URL if SearchFieldUser is PATH driven
     * <p/>
     * Assumes already have Time data - other wise will not make a call
     *
     * @param data search field data (can not be null)
     */
    public void refreshSearchData(final SearchFieldDataType data) {
        setSearchDataAndWsURL(data);

        if (this.currentTimeData != null) {
            refreshTimeData(this.currentTimeData); // search data affects time
        }
    }

    /**
     * Store new time data. If have enough information (search data)
     * make new request to services for portlet with new time field data
     * <p/>
     * The node type (search data) can affect the dateFrom parameter passed in URL
     * (the interval back from the selected time). So this method adjusts dataFrom
     *
     * @param timeData Time data to set on portlet window
     */
    public void refreshTimeData(final TimeInfoDataType timeData) {

        // update timeFrom data if set in meta data
        final String nodeType = (searchData == null) ? null : searchData.getType();
        final String minsBack = portletData.getTimeFromValFromNodeType(nodeType);

        if (minsBack != null) {
            timeData.dateFrom = getDateFrom(timeData.dateTo, minsBack);
        }

        if (timeData != null) {
            /* clone copy */
            this.currentTimeData = TimeInfoDataType.copyInstance(timeData);
        }

        this.timeDataURLParam = currentTimeData.getQueryString(true);

        // cached time update but can not proceed - no search data
        if (portletData.isSearchFieldUser() && searchData == null) {
            return;
        }

        refresh();
    }

    /**
     * When a window is closed and opened again
     * It can not expect that can reuse its existing time and search data to automatically
     * populate - have to read in or get a new search data and time data again
     * as they may have changed
     *
     * @param srchData null if not a search field user
     * @param timeData current time data
     */
    public void refresh(final SearchFieldDataType srchData, final TimeInfoDataType timeData) {

        setSearchDataAndWsURL(srchData);
        refreshTimeData(timeData);
    }

    public void destroy() {
        if (portletRefreshHandlerRegistration != null) {
            portletRefreshHandlerRegistration.removeHandler();
            portletRefreshHandlerRegistration = null;
        }
        buttonContainer.clear();
        getMessageContainer().clear();
        getBodyContainer().clear();
        getCloseButtonContainer().clear();
        searchData = null;
        currentTimeData = null;
        timeDataURLParam = null;
    }

    @Override protected void onDetach() {
        super.onDetach();
        destroy();
    }

    // TODO Button instantiation should be done with UIBinder, events are handled via @UIHandler
    private void attachButton() {
        final ImageResource resource = resourceBundle.arrowForActionMenu();
        final Image arrowImage = new Image(resource);
        arrowImage.getElement().getStyle().setPropertyPx("marginTop", 5);
        arrowImage.getElement().getStyle().setPropertyPx("marginLeft", 5);
        arrowImage.getElement().getStyle().setProperty("cursor", "pointer");
        setButton(arrowImage, resource.getWidth() + 5);
        arrowImage.addClickHandler(this);
    }

    @Override
    public void onClick(final ClickEvent event) {
        if (portletData.getThresholds().isEmpty()) {
            return;
        }

        // should open menu, but opening threshold window for now
        thresholdsPresenter.addThresholdsSection(portletData);

        if (!thresholdsPresenter.isBound()) {
            thresholdsPresenter.bind();
        }

        thresholdsPresenter.showDialog();
    }

    private void setButton(final Widget button, final int width) {
        if (button == null) {
            buttonContainer.clear();
            topContainer.addStyleName(resourceBundle.style().noButton());
            return;
        }

        buttonContainer.setWidget(button);

        topContainer.removeStyleName(resourceBundle.style().noButton());
        topContainer.setCellWidth(buttonContainer, width + "px");
    }

    public void setBody(final Widget body) {
        bodyContainer.setWidget(body);
    }

    public FocusPanel getDragablePart() {
        return dragHandle;
    }

    @Override
    public String getPortletId() {
        return portletId;
    }

    @Override
    public void mask() {
        if (maskHelper == null) {
            maskHelper = new MaskHelper();
        }

        // +10 because of paddings
        maskHelper.mask(getElement(), "Loading " + portletTitle + " ...", portalHeight + 10);
    }

    @Override
    public void unmask() {
        if (maskHelper != null) {
            maskHelper.unmask();
        }
    }

    @Override
    public int getRowIndex() {
        return portletData.getRowIndex();
    }

    public void setMessage(final ComponentMessagePanel messagePanel) {
        if (messagePanel == null) {
            return;
        }

        getBodyContainer().setVisible(false);
        getMessageContainer().setVisible(true);

        getMessageContainer().setWidget(messagePanel);
    }

    interface PortletWindowUiBinder extends UiBinder<Widget, PortletWindow> {
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //////    private methods
    /////////////////////////////////////////////////////////////////////////////////////

    /*
     *  Server call from Porlet using its search data and time data
     *  This is ONLY place make server call to populate porlet 
     */
    private void refresh() {

        // Clear up after message has been shown
        if (getMessageContainer().isVisible()) {
            getMessageContainer().clear();
            getMessageContainer().setVisible(false);
            getBodyContainer().setVisible(true);
        }

        if (portletData.getURL().isEmpty()) {
            // e.g. the Map meta data has no URL - not interested in making a call
            return;
        }

        if (isSearchDataGoodForServerCall()) {

            final StringBuilder urlParamBuff = new StringBuilder();

            urlParamBuff.append(timeDataURLParam);

            if (searchData != null) { // so search field user
                urlParamBuff.append(searchData.getSearchFieldURLParams(false));
            }

            urlParamBuff.append(CommonParamUtil.getTimeZoneURLParameter());

            urlParamBuff.append(EXTRA_URL_PARAMS); // TODO get these via meta data etc

            // Show loading mask before call
            if (isAttached()) {
                eventBus.fireEvent(new PortletMaskEvent(portletId));
            }
            //TODO maybe want to inject
            getServerComms().makeServerRequest(multipleInstanceWinId, wsURL, urlParamBuff.toString());

        } else {
            // Needed scheduled command, since events are tried to be added, but since event handlers are added when
            // another event (guess it's metadata read event) is dispatched, event handler adding is delayed.
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    eventBus.fireEvent(new ComponentMessageEvent(portletId, ComponentMessageType.WARN,
                            DIMENSION_NOT_SUPPORTED_MESSAGE_DASHBOARD));
                }
            });
        }
    }

    /*
     * If window not a search field user then always good 
     * 
     * guard: paired search data can be empty even if type selected (e.g. select input)
     * guard: (add scenario) if called when only time data available
     * guard: when node type not valid(excluded) for porlet
     * 
     */
    private boolean isSearchDataGoodForServerCall() {

        if (portletData.isSearchFieldUser()) {

            if (searchData == null || searchData.isEmpty()) {
                return false;
            }
            if (portletData.isExcludedNodeType(searchData.getType())) {

                LOGGER.info(searchData.getType()
                        + " not a suitable search field node type (not making server call) for : " + portletId);
                return false;
            }
        }
        return true;
    }

    /*
    * Set data and adjust for Path driven search user based on current node selection
    *
    * e.g. /DASHBOARD/HOMER_ROAMER/APN  or
    *      /DASHBOARD/HOMER_ROAMER/SUMMARY or
    *      /DASHBOARD/HOMER_ROAMER/APN_GROUP
    *
    * not /DASHBOARD/HOMER_ROAMER?time=bla@type=APN
    */
    private void setSearchDataAndWsURL(final SearchFieldDataType srchData) {

        // needed a clone
        // TODO pass something immutable instead (need new instance as path mode change will currupt nest porlet call)
        this.searchData = SearchFieldDataType.newInstance(srchData);

        if (searchData != null) {
            this.searchData.setPathMode(isPathSearchFieldUser);
            this.searchData.setIsGroupAsCsv(isGroupAsCsv);
            if (isPathSearchFieldUser) {
                final String groupAppendage = (searchData.isGroupMode()) ? UNDER_SCORE_GROUP : EMPTY_STRING;
                this.wsURL = baseWsURL + "/" + searchData.getType() + groupAppendage;
            }
        }
    }

    /*
     * Calculate individual dateFrom for current user time selection
     * Fall over if meta data not configured a correct int in "dateFrom" field
     *  
     * @param dateTo       selected date to at 00:00 hrs
     * @param minsBack     from meta data mins (split string)
     * @return             relative data for dateFrom
     */
    private Date getDateFrom(final Date dateTo, final String minsBack) {
        final long timeBack = Integer.parseInt(minsBack) * CommonConstants.MS_IN_MIN;
        return new Date(dateTo.getTime() - timeBack);
    }

    /* junit over-ride */
    @SuppressWarnings("hiding")
    Image createCloseButton(final String portletId) {
        final Image closeButton = new Image(resourceBundle.closeButton());
        closeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                PortletWindow.this.eventBus.fireEvent(new PortletRemoveEvent(portletId));
            }
        });
        closeButton.setAltText("Close");
        return closeButton;
    }

    MultipleInstanceWinId getMultipleInstanceWinId() {
        final String tabOwnerId = portletData.getTabOwnerId();
        return new MultipleInstanceWinId(tabOwnerId, portletId);
    }

    ///////////////////////////////////////////////////////////////

    // junit overrides

    void initWidget() {
        initWidget(uiBinder.createAndBindUi(this));
        attachButton();
    }

    ServerComms getServerComms() {
        return new ServerComms(eventBus);
    }

    String getCompleteURL(final String url) {
        return ReadLoginSessionProperties.getEniqEventsServicesURI() + url;
    }

    Label getTitleLabel() {
        return titleLabel;
    }

    SimplePanel getBodyContainer() {
        return bodyContainer;
    }

    SimplePanel getMessageContainer() {
        return messageContainer;
    }

    SimplePanel getCloseButtonContainer() {
        return closeButtonContainer;
    }
}