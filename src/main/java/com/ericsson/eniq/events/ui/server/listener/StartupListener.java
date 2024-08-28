package com.ericsson.eniq.events.ui.server.listener;

import javax.servlet.*;

import com.ericsson.eniq.events.ui.server.config.*;

/**
 * Servlet context listener for web application 
 * startup tasks.
 * 
 * ApplicationConfigManager is initialised here for the first time
 * and placed in the servlet context
 * 
 * @author edeccox
 * @since 2010
 *
 */
public class StartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent event) {
        final ServletContext context = event.getServletContext();
        context.setAttribute(ApplicationConfigManager.APP_CONFIG_CONTEXT_KEY, ApplicationConfigManagerFactory.getApplicationConfigManager());
    }
    
    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
    }
}
