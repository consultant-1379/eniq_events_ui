<%--
    Document   : ValidateUser.jsp
    Created on : 29-Sep-2011, 10:57:00
    Author     : ecarsea
    JSP to handle AJAX request for Validation of a User for a Login Attempt
--%>
<%
    response.setHeader("Cache-Control", "max-age=0");
    response.setHeader("Pragma", "no-cache");
    response.setContentType("text/xml");
    response.setDateHeader("Expires", 0); //prevents caching at the proxy server
    if (request.getProtocol().equals("HTTP/1.1")) {
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Cache-Control", "no-store");
    }
%>

<%-- Instantiate the form validation bean and supply the error message map --%>
<%@ page
	import="com.ericsson.eniq.events.ui.login.shared.AuthenticationConstants,
	java.util.Map,com.ericsson.eniq.events.ui.server.config.*,com.ericsson.eniq.events.ui.server.listener.UserSessionTracker,static com.ericsson.eniq.events.ui.server.config.ApplicationConfigManager.*"%>

<jsp:useBean id="loginForm"
	class="com.ericsson.eniq.events.ui.server.LoginForm" scope="request">
</jsp:useBean>
<jsp:useBean id="credentialsHolder"
             class="com.ericsson.eniq.events.common.server.CredentialsHolder" scope="session">
</jsp:useBean>
<%
    final ServletContext context = config.getServletContext();
    ApplicationConfigManager appConfigMgr = (ApplicationConfigManager) context
            .getAttribute(ApplicationConfigManager.APP_CONFIG_CONTEXT_KEY);

    // check user session limit
    final Integer numberOfUserSessions = (Integer) context
            .getAttribute(UserSessionTracker.USER_SESSION_COUNTER_KEY);
    final boolean maximumConcurrentUserReached = loginForm
            .checkMaximumConcurrentUserSessionReached(numberOfUserSessions);
    boolean lockout = loginForm.checkUserLockout();
    
    final String contextRoot = application.getContextPath();
    final String appVersion = appConfigMgr.getEniqEventsAppVersion();
    final String appCopyright = "© " + appConfigMgr.getEniqEventsAppCopyright();
    final String appTheme = appConfigMgr.getEniqEventsTheme();
    final float kpiAmberThreshold = appConfigMgr.getKPIThresholdAmber();
    final float kpiRedThreshold = appConfigMgr.getKPIThresholdRed();

    session.setAttribute(ENIQ_EVENTS_UI_VERSION, appVersion);
    session.setAttribute(ENIQ_EVENTS_UI_COPYRIGHT, appCopyright);
    session.setAttribute(ENIQ_EVENTS_SERVICES_URI, appConfigMgr.getEniqEventsServicesURI());
    session.setAttribute(ENIQ_EVENTS_REQUEST_TIMEOUT_TIME_IN_MILLISECONDS,
            appConfigMgr.getEniqEventsRequestTime());
    session.setAttribute(ENIQ_EVENTS_BANNER_MESSAGE, appConfigMgr.getBannerMessage());
    session.setAttribute(ENIQ_EVENTS_GUI_THEME, appTheme);
    session.setAttribute(ENIQ_EVENTS_SUCCESS_RATIO_LESS_THAN_CUT_OFF_AMBER, kpiAmberThreshold);
    session.setAttribute(ENIQ_EVENTS_SUCCESS_RATIO_LESS_THAN_CUT_OFF_RED, kpiRedThreshold); 
    session.setAttribute(GEO_SERVER_URI, appConfigMgr.getJNDIValue(GEO_SERVER_URI));
    session.setAttribute(ENIQ_EVENTS_DASHBOARD_REFRESH_TIME_MINS, appConfigMgr.getStartDashBoardRefeshMinsFromMidnite());

    // Set the Success RAW toggle
    session.setAttribute(ENIQ_EVENTS_SUC_RAW, appConfigMgr.getJNDIValue(ENIQ_EVENTS_SUC_RAW));

    // Set the max row variables
    session.setAttribute(ENIQ_EVENTS_MAX_JSON_RESULT_SIZE,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_MAX_JSON_RESULT_SIZE));
    session.setAttribute(ENIQ_EVENTS_IMSI_EVENT_FAILURE_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_IMSI_EVENT_FAILURE_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_DURATION_CALLS_EVENT_FAILURE_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_DURATION_CALLS_EVENT_FAILURE_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_CFA_IMSI_CALL_SETUP_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_CFA_IMSI_CALL_SETUP_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_CFA_IMSI_CALL_DROP_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_CFA_IMSI_CALL_DROP_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_CFA_ENODEB_CALL_DROP_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_CFA_ENODEB_CALL_DROP_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_CFA_ENODEB_CALL_SETUP_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_CFA_ENODEB_CALL_SETUP_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_CFA_ACCESS_AREA_CALL_DROP_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_CFA_ACCESS_AREA_CALL_DROP_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_CFA_ACCESS_AREA_CALL_SETUP_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_CFA_ACCESS_AREA_CALL_SETUP_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_CFA_SUBSCRIBER_REOCCURING_RANKING_COUNT ,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_CFA_SUBSCRIBER_REOCCURING_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_CFA_CAUSE_CODE_CALL_SETUP_RANKING_COUNT ,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_CFA_CAUSE_CODE_CALL_SETUP_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_CFA_CAUSE_CODE_CALL_DROP_RANKING_COUNT ,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_CFA_CAUSE_CODE_CALL_DROP_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_CFA_TRACKING_AREA_CALL_SETUP_RANKING_COUNT ,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_CFA_TRACKING_AREA_CALL_SETUP_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_CFA_TRACKING_AREA_CALL_DROP_RANKING_COUNT ,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_CFA_TRACKING_AREA_CALL_DROP_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_UNANSWERED_CALLS_EVENT_FAILURE_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_UNANSWERED_CALLS_EVENT_FAILURE_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_TERMINAL_EVENT_FAILURE_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_TERMINAL_EVENT_FAILURE_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_CELL_EVENT_FAILURE_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_CELL_EVENT_FAILURE_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_MSC_EVENT_FAILURE_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_MSC_EVENT_FAILURE_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_BSC_EVENT_FAILURE_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_BSC_EVENT_FAILURE_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_CAUSE_CODE_EVENT_FAILURE_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_CAUSE_CODE_EVENT_FAILURE_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_APN_EVENT_FAILURE_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_APN_EVENT_FAILURE_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_ENODEB_EVENT_FAILURE_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_ENODEB_EVENT_FAILURE_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_TERMINAL_GROUP_ANALYSIS_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_TERMINAL_GROUP_ANALYSIS_COUNT));
    session.setAttribute(ENIQ_EVENTS_TERMINAL_ANALYSIS_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_TERMINAL_ANALYSIS_COUNT));
    session.setAttribute(ENIQ_EVENTS_RNC_EVENT_FAILURE_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_RNC_EVENT_FAILURE_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_SUBBI_COUNT, appConfigMgr.getJNDIValue(ENIQ_EVENTS_SUBBI_COUNT));
    session.setAttribute(ENIQ_EVENTS_PDP_STATISTICS_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_PDP_STATISTICS_COUNT));
    session.setAttribute(ENIQ_EVENTS_LIVE_LOAD_COUNT, appConfigMgr.getJNDIValue(ENIQ_EVENTS_LIVE_LOAD_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_HFA_SUBSCRIBER_EXEC_RANKING_COUNT, 
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_HFA_SUBSCRIBER_EXEC_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_HFA_SUBSCRIBER_PREP_RANKING_COUNT, 
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_HFA_SUBSCRIBER_PREP_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_ROAMING_ANALYSIS_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_ROAMING_ANALYSIS_COUNT));
    session.setAttribute(ENIQ_EVENTS_DATA_VOLUME_RANKING_COUNT,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_DATA_VOLUME_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_CFA_TERMINAL_CALL_SETUP_RANKING_COUNT ,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_CFA_TERMINAL_CALL_SETUP_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_CFA_TERMINAL_CALL_DROP_RANKING_COUNT ,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_CFA_TERMINAL_CALL_DROP_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_HFA_ENODEB_EXEC_RANKING_COUNT ,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_HFA_ENODEB_EXEC_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_HFA_ENODEB_PREP_RANKING_COUNT ,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_HFA_ENODEB_PREP_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_HFA_SOURCE_CELL_EXEC_RANKING_COUNT ,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_HFA_SOURCE_CELL_EXEC_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_HFA_SOURCE_CELL_PREP_RANKING_COUNT ,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_HFA_SOURCE_CELL_PREP_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_HFA_TARGET_CELL_EXEC_RANKING_COUNT ,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_HFA_TARGET_CELL_EXEC_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_HFA_TARGET_CELL_PREP_RANKING_COUNT ,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_HFA_TARGET_CELL_PREP_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_HFA_CAUSE_CODE_EXEC_RANKING_COUNT ,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_HFA_CAUSE_CODE_EXEC_RANKING_COUNT));
    session.setAttribute(ENIQ_EVENTS_LTE_HFA_CAUSE_CODE_PREP_RANKING_COUNT ,
            appConfigMgr.getJNDIValue(ENIQ_EVENTS_LTE_HFA_CAUSE_CODE_PREP_RANKING_COUNT));

    // User was already logged in so redirect to the context root
    if (session.getAttribute(UserSessionTracker.VALID_USER_LOGIN_EVENT_KEY) != null) {
        response.sendRedirect(contextRoot);

    }  
    else if (!lockout && !maximumConcurrentUserReached) {
        // If Users are not locked out and max concurrent users not reached
        final Map parametersMap = request.getParameterMap();

        // getting parameters from POST request
        loginForm.setUserName(((String[]) parametersMap.get("username"))[0]);
        loginForm.setUserPassword(((String[]) parametersMap.get("password"))[0]);
        if (loginForm.process()) {
        	session.setAttribute(USER_PASSWORD_SESSION_PARAM, loginForm.getUserPassword());
        	session.setAttribute(USER_PASSWORD_ENCRYPTED_SESSION_PARAM, loginForm.getEncryptedUserPassword());
        	credentialsHolder.setUserPassword(((String[]) parametersMap.get("username"))[0], ((String[])
                        parametersMap.get("password"))[0]);
        }
    }
    out.write(AuthenticationConstants.getXmlResponseString(loginForm.getLoginResponseCode()));
%>