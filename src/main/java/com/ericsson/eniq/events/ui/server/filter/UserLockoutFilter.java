package com.ericsson.eniq.events.ui.server.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.events.ui.server.config.ApplicationConfigManager;
import com.ericsson.eniq.events.ui.server.listener.UserSessionTracker;

/**
 * Filter checks user lockout flag and redirects all subsequent requests 
 * to logout page if flag is set to true.
 * @author edeccox
 * @since 2010
 *
 */
public class UserLockoutFilter implements Filter {

    private final java.util.logging.Logger log = java.util.logging.Logger.getLogger(UserLockoutFilter.class.getName());

    private static final String LOCKOUT_PAGE_URL = "lockoutPageUrl";

    private String lockoutPageUrl = null;

    //A list of exclude pages that we want to pass straight through the filter.
    private final String[] excludedPages = { "LoginUI", "Login.jsp", "ValidateUser.jsp" };

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.lockoutPageUrl = filterConfig.getInitParameter(LOCKOUT_PAGE_URL);
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;

        final ApplicationConfigManager appConfigManager = getApplicationConfigManager(httpRequest);

        final String requestPath = httpRequest.getRequestURI();
        final String contextRoot = httpRequest.getContextPath();
        //Check to see if the request passing through the filter are allowed to be exclude or not
        boolean requestExcluded = false;
        int i = 0;
        for (i = 0; i < excludedPages.length; i++) {
            if (requestPath.contains(excludedPages[i])) {
                requestExcluded = true;
            }
        }

        if (appConfigManager != null && appConfigManager.lockoutUsers() && !requestExcluded) {
            log.info("user lockout flag is set");
            invalidateUserSessions(httpRequest);

            ((HttpServletResponse) response).sendRedirect(contextRoot + this.lockoutPageUrl);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Invalidate all logged in user sessions
     * @param httpRequest
     */
    private void invalidateUserSessions(final HttpServletRequest httpRequest) {
        final Map<String, HttpSession> sessionMap = getUserSessions(httpRequest);
        if (sessionMap != null && !sessionMap.isEmpty()) {
            final Set<String> sessionIds = sessionMap.keySet();
            for (final String sessionId : sessionIds) {
                HttpSession session = sessionMap.get(sessionId);
                if (session != null) {
                    log.info("invalidating session with ID: " + sessionId);
                    session.invalidate();
                    session = null;
                }
                sessionMap.remove(sessionId);
            }
        }
    }

    /**
     * Get application config manager from servlet context
     * @param request servlet request from which we get the ServletContext
     * @see com.ericsson.eniq.events.ui.server.listener.StartupListener
     * @return application config manager object or null
     */
    private ApplicationConfigManager getApplicationConfigManager(final HttpServletRequest request) {
        final ServletContext context = getServletContext(request);
        return context != null ? (ApplicationConfigManager) context
                .getAttribute(ApplicationConfigManager.APP_CONFIG_CONTEXT_KEY) : null;
    }

    /**
     * Retrieve valid user login sessions from context. 
     * @see com.ericsson.eniq.events.ui.server.listener.UserSessionTracker
     * @param request
     * @return map of user sessions or an empty map 
     */
    @SuppressWarnings("unchecked")
    private Map<String, HttpSession> getUserSessions(final HttpServletRequest request) {
        final ServletContext context = getServletContext(request);
        return (Map<String, HttpSession>) (context != null ? (Map<String, HttpSession>) context
                .getAttribute(UserSessionTracker.LOGGED_IN_SESSIONS_MAP_KEY) : Collections.emptyMap());
    }

    private ServletContext getServletContext(final HttpServletRequest request) {
        return request.getSession() != null ? request.getSession().getServletContext() : null;
    }

    @Override
    public void destroy() {
    }
}