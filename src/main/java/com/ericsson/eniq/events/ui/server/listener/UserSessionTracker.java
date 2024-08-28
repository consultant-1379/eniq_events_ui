package com.ericsson.eniq.events.ui.server.listener;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Tracks user session count. 
 * Valid user login events are intercepted in EniqEventsUI.jsp by
 * accessing the credentials of the logged in user. If a valid user
 * name is found then a session attribute VALID_USER_LOGIN_EVENT_KEY 
 * is set with the session id. This event is captured here where a 
 * the count of valid sessions is tracked and a counter is updated 
 * in the servlet context (attribute key is USER_SESSION_COUNTER_KEY).
 * 
 * Note this tracks user sessions and not users. Users may be logged
 * in multiple times.
 * 
 * @author edeccox
 * @since 2010
 *
 */
public class UserSessionTracker implements ServletContextListener, HttpSessionAttributeListener {

    public static final String USER_SESSION_COUNTER_KEY = "userSessionCounter";

    public static final String VALID_USER_LOGIN_EVENT_KEY = "validUserLoginEvent";

    public static final String LOGGED_IN_SESSIONS_MAP_KEY = "loggedInSessionIds";

    private AtomicInteger counter = new AtomicInteger();

    private transient ServletContext servletContext;
    
    private Map<String,HttpSession> sessionMap;

    /**
     * Initialize the context
     *
     * @param sce the event
     */
    public synchronized void contextInitialized(final ServletContextEvent sce) {
        servletContext = sce.getServletContext();
        servletContext.setAttribute(USER_SESSION_COUNTER_KEY, 0);
    }

    /**
     * Set the servletContext, sessionIds and reset the counter
     *
     * @param event The servletContextEvent
     */
    public synchronized void contextDestroyed(final ServletContextEvent event) {
        servletContext = null;
        sessionMap = null;
        counter = new AtomicInteger();
    }

    @Override
    public void attributeAdded(final HttpSessionBindingEvent event) {
        final String attributeName = event.getName();
        final Object attributeValue = event.getValue();
        if (VALID_USER_LOGIN_EVENT_KEY.equals(attributeName)) {
            final HttpSession session = (HttpSession)attributeValue;
            addSession((HttpSession)attributeValue);
            System.out.println("Attribute added : " + attributeName + " : " + session.getId());
            System.out.println("Logged in session count = "+counter);
        }
    }

    @Override
    public void attributeRemoved(final HttpSessionBindingEvent event) {
        final String attributeName = event.getName();
        final Object attributeValue = event.getValue();
        if (VALID_USER_LOGIN_EVENT_KEY.equals(attributeName)) {
            final HttpSession session = (HttpSession)attributeValue;
            removeSession(session.getId());
            System.out.println("Attribute removed : " + attributeName + " : " + attributeValue);
            System.out.println("Logged in session count = "+counter);
        }
    }

    @Override
    public void attributeReplaced(final HttpSessionBindingEvent event) {
        // nothing to do here
        final String attributeName = event.getName();
        final Object attributeValue = event.getValue();
        System.out.println("Attribute replaced : " + attributeName + " : " + attributeValue);
        System.out.println("Logged in session count = "+counter);
    }


    @SuppressWarnings("unchecked")
    private synchronized void addSession(final HttpSession session) {
        sessionMap = (Map<String,HttpSession>) servletContext.getAttribute(LOGGED_IN_SESSIONS_MAP_KEY);

        if (sessionMap == null) {
            sessionMap = new ConcurrentHashMap<String,HttpSession>();
        }

        final String sessionId = session.getId();
        if (!sessionMap.containsKey(sessionId)) {
            sessionMap.put(sessionId,session);
            servletContext.setAttribute(LOGGED_IN_SESSIONS_MAP_KEY, sessionMap);
            incrementSessionCounter();
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized void removeSession(final String sessionId) {
        sessionMap = (Map<String,HttpSession>) servletContext.getAttribute(LOGGED_IN_SESSIONS_MAP_KEY);

        if (sessionMap != null) {
            sessionMap.remove(sessionId);
        }

        servletContext.setAttribute(LOGGED_IN_SESSIONS_MAP_KEY, sessionMap);
        decrementSessionCounter();
    }

    private synchronized void incrementSessionCounter() {
        counter.set((Integer)servletContext.getAttribute(USER_SESSION_COUNTER_KEY));
        counter.incrementAndGet();
        servletContext.setAttribute(USER_SESSION_COUNTER_KEY, counter.get());
    }

    private synchronized void decrementSessionCounter() {
        counter.set((Integer)servletContext.getAttribute(USER_SESSION_COUNTER_KEY));
        counter.decrementAndGet();

        if (counter.get() < 0) {
            counter.set(0);
        }

        servletContext.setAttribute(USER_SESSION_COUNTER_KEY, counter.get());
    }
}
