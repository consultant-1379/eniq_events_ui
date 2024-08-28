package com.ericsson.eniq.events.ui.server.config;

import java.lang.reflect.*;

import javax.naming.*;
import javax.sql.*;

public class ApplicationConfigManagerFactory {
    private static ApplicationConfigManager appConfigManager;
    
    @SuppressWarnings("unchecked")
    public static synchronized ApplicationConfigManager getApplicationConfigManager() {
        if (appConfigManager == null) {
            try {
                final DataSource ds = (DataSource)(new InitialContext()).lookup(ApplicationConfigManager.DWHREP_DATA_SOURCE_JNDI_NAME);
                final Constructor[] cxtor = ApplicationConfigManagerImpl.class.getDeclaredConstructors();
                cxtor[0].setAccessible(true);
                appConfigManager = (ApplicationConfigManager)cxtor[0].newInstance(ds);
            } catch (NamingException e) { 
                throw new ApplicationConfigInitException("ApplicationConfigManager init failed, lookup of "+ApplicationConfigManager.DWHREP_DATA_SOURCE_JNDI_NAME+" failed:",e);
            } catch (SecurityException e) { 
                throw new ApplicationConfigInitException("ApplicationConfigManager init failed, failed to get constructor:",e);
            } catch (Exception e) {
                throw new ApplicationConfigInitException("Could not instantiate ApplicationConfigManager:",e);
            } 
        }
        return appConfigManager;
    }
    
    private ApplicationConfigManagerFactory () {}
}
