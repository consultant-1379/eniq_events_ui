<%--
    Document   : ChangePassword.jsp
    Created on : 30-Sep-2011, 12:00:00
    Author     : ecarsea
    JSP to handle an AJAX call to change the user password
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
<%@ page
	import="com.ericsson.eniq.events.ui.login.shared.AuthenticationConstants,
	java.util.Map,com.ericsson.eniq.events.ui.server.config.*, 
	static com.ericsson.eniq.events.ui.server.config.ApplicationConfigManager.*"%>

<jsp:useBean id="changePasswordForm" class="com.ericsson.eniq.events.ui.server.ChangePasswordForm" scope="request">
</jsp:useBean>


<%
    final String contextRoot = application.getContextPath();

    ApplicationConfigManager appConfigMgr = (ApplicationConfigManager) config.getServletContext().getAttribute(
            ApplicationConfigManager.APP_CONFIG_CONTEXT_KEY);

    final String appVersion = appConfigMgr.getEniqEventsAppVersion();
    final String appCopyright = "\u00A9 " + appConfigMgr.getEniqEventsAppCopyright();
%>
<%
    // Attempt to validate and process the form
        final Map parametersMap = request.getParameterMap();
        // getting parameters from POST request
        changePasswordForm.setExistingUserName(((String[]) parametersMap.get("existingUserName"))[0]);
        changePasswordForm.setExistingUserPassword(((String[]) parametersMap.get("existingUserPassword"))[0]);
        changePasswordForm.setNewUserPassword(((String[]) parametersMap.get("newUserPassword"))[0]);
        // Attempt to process the form
        if (changePasswordForm.process()) {
            session.setAttribute(USER_PASSWORD_SESSION_PARAM,
                    changePasswordForm.getNewUserPassword());
            session.setAttribute(USER_PASSWORD_ENCRYPTED_SESSION_PARAM,
                    changePasswordForm.getEncryptedNewUserPassword());

        }
        out.write(AuthenticationConstants.getXmlResponseString(changePasswordForm.getChangePasswordResponseCode()));
        changePasswordForm.clearChangePasswordFormDetails();
%>