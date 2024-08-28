<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<%@ page
	import="com.ericsson.eniq.events.ui.server.listener.*,com.ericsson.eniq.events.ui.server.config.*,static com.ericsson.eniq.events.ui.server.config.ApplicationConfigManager.*"%>


<%
    // This is a way of detecting a valid login
    // By updating the session attribute it is picked up
    // by UserSessionTracker which tracks logged 
    // in session counts
    // 
    final String userName = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
    if (userName != null) {
        session.setAttribute(UserSessionTracker.VALID_USER_LOGIN_EVENT_KEY, session);
    }
%>
 
<html>
<head>
<title>Ericsson OSS</title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<link rel="shortcut icon" href="resources/icons/favicon.ico" />

<link href="./gxt/css/gxt-all.css" type="text/css" rel="stylesheet"/>
<link href="./EniqEventsUI.css" type="text/css" rel="stylesheet"/>
<link href="./resources/css/whiteTheme.css" type="text/css" rel="stylesheet"/>
<link href="./resources/css/dashboard.css" type="text/css" rel="stylesheet"/>
<script type="text/javascript">
/* console.log('page loading start..'+new Date().getTime()); */ 
        /* set the start time as this is used in profiling to detail the length of time loadup takes */
        window.startTime = new Date().getTime();
        window.userName = "<%= request.getUserPrincipal().getName() %>";

        /* Get the proxy ip when the client is behind a proxy.
         The proxy may include the requesting client IP in a special HTTP header. */
        window.ipBehindProxy = "<%= request.getHeader("x-forwarded-for") %>";

        /* Get client's ip address */
        window.ipAddress = "<%= request.getRemoteAddr() %>";

        window.appCopyright = "<%=session.getAttribute(ENIQ_EVENTS_UI_COPYRIGHT)%>";
        window.appVersion = "<%=session.getAttribute(ENIQ_EVENTS_UI_VERSION)%>";
        window.appServicesURI = "<%=session.getAttribute(ENIQ_EVENTS_SERVICES_URI)%>";
        window.geoServerURI = "<%=session.getAttribute(GEO_SERVER_URI)%>";
        window.appRequestTimoutTime = "<%=session.getAttribute(ENIQ_EVENTS_REQUEST_TIMEOUT_TIME_IN_MILLISECONDS)%>";
        window.adminMsg = "<%=session.getAttribute(ENIQ_EVENTS_BANNER_MESSAGE)%>";
        window.kpiAmberThreshold = "<%=session.getAttribute(ENIQ_EVENTS_SUCCESS_RATIO_LESS_THAN_CUT_OFF_AMBER)%>";
        window.kpiRedThreshold = "<%=session.getAttribute(ENIQ_EVENTS_SUCCESS_RATIO_LESS_THAN_CUT_OFF_RED)%>";
        window.dashboardRefreshFromMidniteMins = "<%=session.getAttribute(ENIQ_EVENTS_DASHBOARD_REFRESH_TIME_MINS)%>";
        window.successRAWToggle = "<%=session.getAttribute(ENIQ_EVENTS_SUC_RAW)%>";
        
        // Max row counts
        window.maxRowCounts = new Array();
        window.maxRowCounts["ENIQ_EVENTS_MAX_JSON_RESULT_SIZE"] = "<%=session.getAttribute(ENIQ_EVENTS_MAX_JSON_RESULT_SIZE)%>";
        window.maxRowCounts["ENIQ_EVENTS_IMSI_EVENT_FAILURE_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_IMSI_EVENT_FAILURE_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_CFA_IMSI_CALL_SETUP_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_CFA_IMSI_CALL_SETUP_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_CFA_IMSI_CALL_DROP_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_CFA_IMSI_CALL_DROP_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_CFA_ACCESS_AREA_CALL_DROP_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_CFA_ACCESS_AREA_CALL_DROP_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_CFA_ACCESS_AREA_CALL_SETUP_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_CFA_ACCESS_AREA_CALL_SETUP_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_CFA_ENODEB_CALL_DROP_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_CFA_ENODEB_CALL_DROP_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_CFA_ENODEB_CALL_SETUP_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_CFA_ENODEB_CALL_SETUP_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_CFA_SUBSCRIBER_REOCCURING_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_CFA_SUBSCRIBER_REOCCURING_RANKING_COUNT)%>";        
        window.maxRowCounts["ENIQ_EVENTS_LTE_CFA_CAUSE_CODE_CALL_SETUP_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_CFA_CAUSE_CODE_CALL_SETUP_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_CFA_CAUSE_CODE_CALL_DROP_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_CFA_CAUSE_CODE_CALL_DROP_RANKING_COUNT)%>";      
        window.maxRowCounts["ENIQ_EVENTS_LTE_CFA_TRACKING_AREA_CALL_SETUP_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_CFA_TRACKING_AREA_CALL_SETUP_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_CFA_TRACKING_AREA_CALL_DROP_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_CFA_TRACKING_AREA_CALL_DROP_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_CFA_TERMINAL_CALL_SETUP_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_CFA_TERMINAL_CALL_SETUP_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_CFA_TERMINAL_CALL_DROP_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_CFA_TERMINAL_CALL_DROP_RANKING_COUNT)%>";
       	window.maxRowCounts["ENIQ_EVENTS_TERMINAL_EVENT_FAILURE_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_TERMINAL_EVENT_FAILURE_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_UNANSWERED_CALLS_EVENT_FAILURE_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_UNANSWERED_CALLS_EVENT_FAILURE_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_DURATION_CALLS_EVENT_FAILURE_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_DURATION_CALLS_EVENT_FAILURE_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_CELL_EVENT_FAILURE_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_CELL_EVENT_FAILURE_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_MSC_EVENT_FAILURE_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_MSC_EVENT_FAILURE_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_BSC_EVENT_FAILURE_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_BSC_EVENT_FAILURE_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_CAUSE_CODE_EVENT_FAILURE_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_CAUSE_CODE_EVENT_FAILURE_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_APN_EVENT_FAILURE_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_APN_EVENT_FAILURE_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_ENODEB_EVENT_FAILURE_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_ENODEB_EVENT_FAILURE_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_TERMINAL_GROUP_ANALYSIS_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_TERMINAL_GROUP_ANALYSIS_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_TERMINAL_ANALYSIS_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_TERMINAL_ANALYSIS_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_RNC_EVENT_FAILURE_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_RNC_EVENT_FAILURE_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_SUBBI_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_SUBBI_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_PDP_STATISTICS_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_PDP_STATISTICS_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LIVE_LOAD_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LIVE_LOAD_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_HFA_SUBSCRIBER_EXEC_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_HFA_SUBSCRIBER_EXEC_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_HFA_SUBSCRIBER_PREP_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_HFA_SUBSCRIBER_PREP_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_ROAMING_ANALYSIS_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_ROAMING_ANALYSIS_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_DATA_VOLUME_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_DATA_VOLUME_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_HFA_ENODEB_EXEC_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_HFA_ENODEB_EXEC_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_HFA_ENODEB_PREP_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_HFA_ENODEB_PREP_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_HFA_SOURCE_CELL_EXEC_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_HFA_SOURCE_CELL_EXEC_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_HFA_SOURCE_CELL_PREP_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_HFA_SOURCE_CELL_PREP_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_HFA_TARGET_CELL_EXEC_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_HFA_TARGET_CELL_EXEC_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_HFA_TARGET_CELL_PREP_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_HFA_TARGET_CELL_PREP_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_HFA_CAUSE_CODE_EXEC_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_HFA_CAUSE_CODE_EXEC_RANKING_COUNT)%>";
        window.maxRowCounts["ENIQ_EVENTS_LTE_HFA_CAUSE_CODE_PREP_RANKING_COUNT"] = "<%=session.getAttribute(ENIQ_EVENTS_LTE_HFA_CAUSE_CODE_PREP_RANKING_COUNT)%>";

        function onLogout() {
            window.location = "<%= application.getContextPath() %>/login/Logout.jsp";
        }

        var sessionXMLDefindedTimeOutTimeInMilliSeconds = '<%= session.getMaxInactiveInterval() %>' * 1000;
        /**
         * Utility Javascript Methods
         * for use with dynamically adding the
         * css style sheets based on theme and image manipulations
         * based on browser resizing
         *
         * @author eendmcm
         * @since Oct 2010
         */

            //Global vars moved from EniqEventsUI\SessionTimeOutManagment.js
        var sessionNextTimeToExpireEpochDateTime = 0;
        var dateTimeNow;
        var epochDateTimeNow;

        /* eendmcm - NOTE: MOVED FROM EniqEventsUI\SessionTimeOutManagment.js as this is a compiled folder */
        //Handles session timeout interval. The function updates the session timeout time
        //each time a user presses the mouse button on the page.
        //If the current time has exceeded the time session set timeout time "sessionNextTimeToExpireEpochDateTime"
        //then it is deemed that the session is expired and the user need to login.
        function checkSessionTimeoutHasExpired() {

            dateTimeNow = new Date();

            epochDateTimeNow = dateTimeNow.getTime();

            if (sessionNextTimeToExpireEpochDateTime == 0 || epochDateTimeNow < sessionNextTimeToExpireEpochDateTime) {
                sessionNextTimeToExpireEpochDateTime = epochDateTimeNow + sessionXMLDefindedTimeOutTimeInMilliSeconds;
            }
            else {
                onLogout();
            }
        }

    </script>

<!-- OpenLayers -->
<script type="text/javascript" src="./resources/openlayers/OpenLayers.js"></script>

<!-- This script loads your compiled module. No meta tags after this one. -->
<script type="text/javascript" language="javascript"
	src="EniqEventsUI/EniqEventsUI.nocache.js"></script>
	<!-- <script type="text/javascript"> console.log('EniqEventsUI.nocache.js end..'+new Date().getTime()); </script> -->
</head>

<body onClick=checkSessionTimeoutHasExpired(); class="eniq">
	<div class="fakeTabBackground"></div>
	<div id='floatLogo' class='floatLogo'></div>
	<div id='headerPnl' class='headerPnl'></div>

	<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1'
		style="position: absolute; width: 0; height: 0; border: 0"></iframe>
	<noscript>
		<div
			style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
			Your web browser must have JavaScript enabled in order for this
			application to display correctly.</div>
	</noscript>

	<div id="loading"  class="loadingDisplay">
		<div class="background">
			<div class="indicatorBackground">
				<img src='./resources/images/whiteTheme/nightRider.gif' />
			</div>
			<div class="loadingMsg">Loading...</div>
		</div>
	</div>
</body>
</html>