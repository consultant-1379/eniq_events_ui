<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<%--
    Document   : Login.jsp
    Created on : 28-Apr-2010, 10:57:00
    Author     : estepdu, ericker, ecarsea
--%>

<%@ page
	import="com.ericsson.eniq.events.ui.server.config.ApplicationConfigManager,com.ericsson.eniq.events.ui.server.listener.UserSessionTracker,
	static com.ericsson.eniq.events.ui.login.shared.AuthenticationConstants.VALIDATION_RESPONSE.*"%>

<%
    response.setHeader("Cache-Control", "max-age=0");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0); //prevents caching at the proxy server
    if (request.getProtocol().equals("HTTP/1.1")) {
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Cache-Control", "no-store");
    }

    final ServletContext context = config.getServletContext();

    final String contextRoot = application.getContextPath();

    ApplicationConfigManager appConfigMgr = (ApplicationConfigManager) context
            .getAttribute(ApplicationConfigManager.APP_CONFIG_CONTEXT_KEY);
    final String appVersion = appConfigMgr.getEniqEventsAppVersion();
    final String appCopyright = "\u00A9 " + appConfigMgr.getEniqEventsAppCopyright();
    // User was already logged in so redirect to the context root
    if (session.getAttribute(UserSessionTracker.VALID_USER_LOGIN_EVENT_KEY) != null) {
        response.sendRedirect(contextRoot);

    }
    String errorMessage = "";
    if (appConfigMgr.lockoutUsers()) {
        errorMessage = USER_LOCKOUT_MESSAGE.getResponseMessage();
    } else if (request.getParameter("error") != null) {
        errorMessage = AUTHENTICATION_ERROR_MESSAGE.getResponseMessage();
    }
%>

<html>
	<head>
	<link rel="shortcut icon"
		href="<%=contextRoot%>/resources/icons/favicon.ico" />
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Ericsson OSS Log In</title>
	<style type="text/css">
        html, body {
            height: 100%;
        }

        body.loginScreen {
            /* added .loginScreen to override gwt default theme style */
            background-color: #f7f7f7;
            background-image:
                url(resources/images/whiteTheme/top_gradient.png),
                /*url(resources/images/whiteTheme/unique_globes_blur.png),*/
                url(resources/images/whiteTheme/OSS_text_blur.png),
                url(resources/images/whiteTheme/logo_blur.png);
                /*url(resources/images/whiteTheme/unique_background_NO_gradient.png);*/

            background-position: left top, /*right 40px,*/ left bottom, right bottom/*, center center*/;
            background-repeat: no-repeat;
            background-size: 100% 3px ,/* 30%*/ 10%, 4%/*, 100% 100%*/;
        }
	</style>
	<script type="text/javascript" language="javascript"
		src="<%=contextRoot%>/LoginUI/LoginUI.nocache.js"></script>
	</head>
	
	<body class="loginScreen">
		<form id="hiddenLogin" action="<%=contextRoot%>/j_security_check"
			method="POST">
			<input type="HIDDEN" id="username" name="j_username" value="">
			<input type="HIDDEN" id="password" name="j_password" value="">
		</form>
		<div id="validateUserRequestUri" style="display: none"><%=contextRoot%>/login/ValidateUser.jsp
		</div>
		<div id="changePasswordRequestUri" style="display: none"><%=contextRoot%>/login/ChangePassword.jsp
		</div>
		<input type="hidden" id="errorMessage" value="<%=errorMessage%>">
		<input type="hidden" id="appVersion" value="<%=appVersion%>">
		<input type="hidden" id="appCopyright" value="<%=appCopyright%>">
	</body>
</html>