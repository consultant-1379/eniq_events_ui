/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.ServerComms;
import com.ericsson.eniq.events.ui.client.common.widget.dialog.PropertiesDialogForKeyValuePairList;
import com.ericsson.eniq.events.ui.client.datatype.*;
import com.ericsson.eniq.events.ui.client.events.FailedEvent;
import com.ericsson.eniq.events.ui.client.events.ServerRequestEvent;
import com.ericsson.eniq.events.ui.client.events.SucessResponseEvent;
import com.ericsson.eniq.events.ui.client.events.SucessResponseEventHandler;
import com.ericsson.eniq.events.ui.client.events.handlers.ServerFailedResponseHandler;
import com.ericsson.eniq.events.ui.client.events.handlers.ServerRequestHandler;
import com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceUtils;
import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.EventBus;


import java.util.*;

import static com.ericsson.eniq.events.common.client.CommonConstants.FIRST_URL_PARAM_DELIMITOR;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;

/**
 * Displays Dialog with a grid
 *
 * @author esuslyn
 * @since August 2010
 */
public class TwoColumnGridDialogPresenter<D extends WidgetDisplay> extends BasePresenter<D> {

    private final IExtendedWidgetDisplay gridRef;

    private final MetaMenuItemDataType winData;

    final VerticalGridColumnHeaders keyValues;

    private final ServerSuccessResponseHandler gridDataHandler = new ServerSuccessResponseHandler();

    private final ServerFailedResponseHandler failedRequestHandler;

    private final ServerRequestHandler requestHandler;

    private SearchFieldDataType searchData;

    private final static String columnShouldBeDisplay = "1";

    public final static String DIALOG_ID = "DIALOG_WINDOW";

    private final MultipleInstanceWinId multiWinID;

    private ServerComms serverComms;

    private static final String EMPTY_STRING = "";

    static final String DATE_FORMAT_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    static final String DATE_FORMAT_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    static final String DATE_FORMAT_HH_MM = "yyyy-MM-dd HH:mm";


    /**
     * Construct grid - this does not initiate the call to the services layer for the actual data, requestGridData()
     * must be called independently
     *
     * @param display
     * @param eventBus
     * @param winData  - contains information about the dialog to be launched
     */
    public TwoColumnGridDialogPresenter(final D display, final EventBus eventBus, final MetaMenuItemDataType winData,
            final VerticalGridColumnHeaders keyValues) {
        super(display, eventBus);
        gridRef = (IExtendedWidgetDisplay) display;

        multiWinID = createMultipleInstanceWinId(); // NOPMD by eeicmsy on 05/07/11 14:07

        this.winData = winData;
        this.keyValues = keyValues;

        final MetaMenuItem viewSettings = gridRef.getViewSettings();
        requestHandler = new ServerRequestHandler(multiWinID, display);
        failedRequestHandler = new ServerFailedResponseHandler(multiWinID, display, viewSettings);

        initDisplay();
        super.bind();
    }

    //for test.
    public TwoColumnGridDialogPresenter(final D display, final EventBus eventBus) {
        super(display, eventBus);
        gridRef = null;
        winData = null;
        keyValues = null;
        failedRequestHandler = null;
        requestHandler = null;
        multiWinID = null;
    }

    private MultipleInstanceWinId createMultipleInstanceWinId() {
        /** 
         * TODO move id generation from workspace to common.
         * TODO long-term remove multiwinid and call server comms direct
         */
        MultipleInstanceWinId winId = new MultipleInstanceWinId(this.getTabOwnerId(), WorkspaceUtils.generateId());
        return winId; // no search data (not multiple instance window)
    }

    /*
    * Initialise and Display the Time Parameters Dialog
    *
    */
    private void initDisplay() {
        registerHandler(getEventBus().addHandler(ServerRequestEvent.TYPE, requestHandler));
        registerHandler(getEventBus().addHandler(SucessResponseEvent.TYPE, gridDataHandler));
        registerHandler(getEventBus().addHandler(FailedEvent.TYPE, failedRequestHandler));
    }

    /**
     * Method to call to deregistor gridHandler added
     * to eventbus
     */
    public void cleanUpOnClose() {
        super.unbind();
    }

    String getTabOwnerId() {
        return ((IExtendedWidgetDisplay) getView()).getWorkspaceController().getTabOwnerId();
    }

    protected Map<String, String> getAllGridColumnHeaderIds(final IExtendedWidgetDisplay gridView) {
        final Map<String, String> headerIdColumnsMap = new HashMap<String, String>();
        if (gridView instanceof IEventGridView) {
            final GridInfoDataType gridInfo = ((IEventGridView) gridView).getColumns();

            final ColumnInfoDataType[] allColumnInfos = gridInfo.columnInfo;

            for (final ColumnInfoDataType column : allColumnInfos) {
                headerIdColumnsMap.put(column.columnHeader, column.columnID);
            }
        }

        return headerIdColumnsMap;
    }

    /**
     * Creates a HTTPRequest for JSON data and reads the response using a ModelType
     * definition to create a store which provides input data for the grid
     *
     * @param record current row data - can be null
     */
    public void requestGridData(final ModelData record) {
        final String urlParams = getUrlParams(record);

        /* important all communication though main ServerComms */
        getServerCommHandler().makeServerRequest(multiWinID, winData.url, urlParams);
    }

    /*
    * Important all server communication goes through same methods
    * @return ServerComm handler to hit code that will be used for all server communication
    */
    ServerComms getServerCommHandler() {
        if (serverComms == null) {
            serverComms = new ServerComms(getEventBus());
        }
        return serverComms;
    }

    /*
    * Put together the url parameters for service call
    * @return  eg: some_url?display=grid&time=30&tzOffset=+0100
    */
    private String getUrlParams(final ModelData record) {
        /* search data on menuTaskBar is of no interest to current window (unless play was pressed)
        * so take it from the window itself - including what may be from drilldown */
        searchData = getSearchDataFromPresenter();

        searchData.clean(); // remove rogue key=SUM

        final String displayParam = DISPLAY_TYPE_PARAM + winData.display;
        final String dateTimeParam = gridRef.getPresenter().getWindowTimeDate().getDrillQueryString();

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(dateTimeParam);
        stringBuilder.append(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR);
        stringBuilder.append(displayParam);
        stringBuilder.append(CommonParamUtil.getTimeZoneURLParameter());
        if (searchData != null) {
            stringBuilder.append(searchData.getSearchFieldURLParams(false));
        }

        if (record != null) { // interested in row data
            final boolean hasDrilledIntoCell = (stringBuilder.toString().contains(SEARCH_FIELD_CELL_TYPE.toLowerCase()
                    + EQUAL_STRING));

            if (!hasDrilledIntoCell) {

                final Map<String, String> columnDetails = getAllGridColumnHeaderIds(gridRef);
                // currently only SAC (CONNECTED_CELLS - which contains "CELL") - i.e. not Sub details
                if (columnDetails.containsKey(ACCESS_AREA) && winData.url.contains(SEARCH_FIELD_CELL_TYPE)) {
                    final String idForHeader = columnDetails.get(ACCESS_AREA);
                    stringBuilder.append(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR);
                    stringBuilder.append(SEARCH_FIELD_CELL_TYPE.toLowerCase());
                    stringBuilder.append(EQUAL_STRING);
                    stringBuilder.append(record.get(idForHeader));
                }
            }
        }

        return stringBuilder.toString();
    }

    SearchFieldDataType getSearchDataFromPresenter() {
        return gridRef.getPresenter().getSearchData();
    }


    /**
     * Convert the dateString from one format (fromDateFormat) into another format (toDateFormat).
     * If the dateString does not match the fromDateFormat the method returns the unconverted
     * dateString.
     * @param dateString - the string to be converted, if it has the same format as fromDateFormat.
     * @param fromDateFormat - the format of the string to convert.
     * @param toDateFormat - reformat dateString to this format.
     * @return - newly formatted dateString (if it matches fromDateFormat) or the original dateString
     * if it does not match fromDateFormat.
     */
    public String convertTime(String dateString, final String fromDateFormat, final String toDateFormat) {
        dateString = dateString.replaceAll("\"", EMPTY_STRING);
        String result;
        DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(fromDateFormat);
        try{
            Date date = dateTimeFormat.parse(dateString);
            DateTimeFormat dateTimeShortFormat = DateTimeFormat.getFormat(toDateFormat);
            result =  dateTimeShortFormat.format(date);

        }catch(IllegalArgumentException illegalArgumentException){
            result = dateString;
        }
        return result;
    }


    class ServerSuccessResponseHandler implements SucessResponseEventHandler {
        @Override
        public void handleResponse(final MultipleInstanceWinId multiWinId, final String requestData,
                final Response response) {

            // guards
            if (!TwoColumnGridDialogPresenter.this.multiWinID.isThisWindowGuardCheck(multiWinId)) {
                return;
            }

            final JSONObject responseValues = createJSONObjectFromResponseText(response);
            if (verifyDataAndDisplayWarningForInvalidData(responseValues)) {
                final String[] values = getValuesForFirstElementInJsonArray(responseValues);
                final String[] keys = keyValues.keys;
                final List<KeyValuePair> gridData = new ArrayList<KeyValuePair>();
                for (int i = 0; i < keys.length; i++) {
                    gridData.add(new KeyValuePair(keys[i], convertTime(values[i], DATE_FORMAT_HH_MM_SS_SSS, DATE_FORMAT_HH_MM_SS))); // NOPMD by eeicmsy on 30/08/10 12:10
                }
                buildGrid(gridData);
            }
            getView().stopProcessing(); // unmask
        }
    }



    /*
    *  extracted out to help get under unit test
    * @param responseValues
    * @return
    */
    boolean verifyDataAndDisplayWarningForInvalidData(final JSONObject responseValues) {
        return JSONUtils.checkData(responseValues, getEventBus(), multiWinID);
    }

    /*
    * extracted to get class under unit test
    * @param response
    * @return
    */
    JSONObject createJSONObjectFromResponseText(final Response response) {
        return JSONUtils.parse(response.getText()).isObject();
    }

    /*
    * return the json response data from server call in a array
    * Will only examine the first element in the data field in the array
    * If there are no elements in the data array, then an string array of empty strings is returned
    * (of length the number of keys)
    *
    * extracted out to get under unit test
    */
    String[] getValuesForFirstElementInJsonArray(final JSONObject responseValues) {
        final JSONArray subParent = responseValues.get(JSON_ROOTNODE).isArray();
        final JSONValue firstElementInDataArray = subParent.get(0);
        if (firstElementInDataArray == null) {
            return createStringArrayOfEmptyStrings(keyValues.keys.length);
        }
        final List<String> keys = new ArrayList<String>();
        final JSONObject values = firstElementInDataArray.isObject();
        for (int k = 0; k < values.size(); k++) {
            final String tmp = String.valueOf((k + 1)); // e.g.  "1", "2"
            keys.add(values.get(tmp).toString());
        }
        return keys.toArray(new String[keys.size()]);
    }

    private String[] createStringArrayOfEmptyStrings(final int lengthOfStringArrayToCreate) {
        final String[] stringArray = new String[lengthOfStringArrayToCreate];
        for (int i = 0; i < stringArray.length; i++) {
            stringArray[i] = EMPTY_STRING;
        }
        return stringArray;
    }

    private void buildGrid(final List<KeyValuePair> gridData) {
        String dialogTitleFinish = EMPTY_STRING;
        if (keyValues.columnHeaderPartOfTitle != null && keyValues.columnHeaderPartOfTitle.length() > 0) {
            dialogTitleFinish = getDialogTitleFinish(gridData.remove(Integer
                    .parseInt(keyValues.columnHeaderPartOfTitle) - 1));
        }

        final boolean hasSearchData = searchData != null && (!searchData.isEmpty());

        final String dialogTitleStart = hasSearchData ? searchData.searchFieldVal + DASH : EMPTY_STRING;

        final String title = dialogTitleStart + winData.text + dialogTitleFinish;

        new PropertiesDialogForKeyValuePairList(title, gridData, this);
    }

    private String getDialogTitleFinish(final KeyValuePair columnHeaderPartOfTitle) {
        final boolean isColumnHeaderDisplayed = columnHeaderPartOfTitle.getValue().equals(columnShouldBeDisplay);
        return isColumnHeaderDisplayed ? SINGLE_SPACE + OPEN_BRACKET + columnHeaderPartOfTitle.getKey() + CLOSE_BRACKET
                : EMPTY_STRING;
    }
}
