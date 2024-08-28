/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.server.config;

/**
 * Application config manager merges retrieves application 
 * properties from JNDI and databases sources on demand
 * and provides convenient accessors
 * 
 * @author edeccox
 */
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles access to application configuration for ENIQ events application.
 * Class is essentially a singleton providing static methods for property access
 * on demand. This is not the most efficient method but is a conscious design
 * decision based on the number of properties and the likelihood that access
 * will be infrequent.
 * 
 * Configuration comes from two sources: 1. Application server JNDI tree 2. ENIQ
 * repository database table ENIQ_EVENTS_ADMIN_PROPERTIES
 * 
 * Application properties are retrieved from both sources and merged into a
 * single properties object.
 * 
 * Accessor methods are provided to get and coerce properties to the correct
 * type.
 * 
 * @author edeccox
 * @since 2010
 * 
 */
public final class ApplicationConfigManagerImpl implements ApplicationConfigManager {

    private static final Logger LOGGER = Logger.getLogger(ApplicationConfigManagerImpl.class.getName());

    /**
     * Application config default values
     */
    private static final int MAX_USER_SESSIONS = 150;

    private final static float DEFAULT_SUCCESS_RATIO_LESS_THAN_CUT_OFF_AMBER = 97;

    private final static float DEFAULT_SUCCESS_RATIO_LESS_THAN_CUT_OFF_RED = 95;

    /**
     * Repository database configuration
     */
    private static final String PARAM_VALUE = "PARAM_VALUE";

    private static final String PARAM_NAME = "PARAM_NAME";

    private static final String ADMIN_PROPERTIES_SQL = "select " + PARAM_NAME + "," + PARAM_VALUE
            + " from ENIQ_EVENTS_ADMIN_PROPERTIES";

    private final DataSource dataSource;

    private ApplicationConfigManagerImpl(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Application version info
     * 
     * @return version string
     */
    @Override
    public String getEniqEventsAppVersion() {
        return getApplicationConfig().getProperty(ENIQ_EVENTS_UI_VERSION);
    }

    /**
     * Application copyright message
     * 
     * @return copyright message
     */
    @Override
    public String getEniqEventsAppCopyright() {
        return getApplicationConfig().getProperty(ENIQ_EVENTS_UI_COPYRIGHT);
    }

    /**
     * Bootstrap URL for UI metadata
     * 
     * @return uri
     */
    @Override
    public String getEniqEventsServicesURI() {
        return getApplicationConfig().getProperty(ENIQ_EVENTS_SERVICES_URI);
    }

    @Override
    public String getLdapURI() {
        return getApplicationConfig().getProperty(ENIQ_EVENTS_LDAP_URI);
    }

    @Override
    public float getKPIThresholdAmber() {
        return getFloatThreshold(DEFAULT_SUCCESS_RATIO_LESS_THAN_CUT_OFF_AMBER,
                ENIQ_EVENTS_SUCCESS_RATIO_LESS_THAN_CUT_OFF_AMBER);

    }

    @Override
    public float getKPIThresholdRed() {
        return getFloatThreshold(DEFAULT_SUCCESS_RATIO_LESS_THAN_CUT_OFF_RED,
                ENIQ_EVENTS_SUCCESS_RATIO_LESS_THAN_CUT_OFF_RED);
    }

    @Override
    public int getStartDashBoardRefeshMinsFromMidnite() {
        /* default 6 am */
        return getIntValue(360, ENIQ_EVENTS_DASHBOARD_REFRESH_TIME_MINS);
    }

    /*
     * Fetch an int value 
     */
    private int getIntValue(final int defaultVal, final String propertyName) {

        final String valueText = getApplicationConfig().getProperty(propertyName);
        int returnVal = defaultVal;
        try {
            if (valueText != null) {
                returnVal = Integer.valueOf(valueText);
            }
        } catch (final NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid value for property " + propertyName + " (" + valueText
                    + "), setting to default value of " + defaultVal + "", e);
        }
        return returnVal;
    }

    /*
     * Fetch thresholds vals (float - e.g. KPI)
     * 
     * @param defaultVal default vlaue to assign if JNDI setting is not a number
     * 
     * @param propertyName name of property in glassfish config
     * 
     * @return value if correct or the default
     */
    private float getFloatThreshold(final float defaultVal, final String propertyName) {

        final String thresholdText = getApplicationConfig().getProperty(propertyName);
        float returnCuttOff = defaultVal;
        try {
            returnCuttOff = Float.valueOf(thresholdText);
        } catch (final NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid value for property " + propertyName + " (" + thresholdText
                    + "), setting to default value of " + defaultVal + "", e);
        }
        return returnCuttOff;
    }

    /**
     * How long should Ajax requests hang run
     * 
     * @return timeout in ms
     */
    @Override
    public String getEniqEventsRequestTime() {
        return getApplicationConfig().getProperty(ENIQ_EVENTS_REQUEST_TIMEOUT_TIME_IN_MILLISECONDS);
    }

    /**
     * The Theme to apply to the UI
     * 
     * @return theme
     */
    @Override
    public String getEniqEventsTheme() {
        return getApplicationConfig().getProperty(ENIQ_EVENTS_GUI_THEME);
    }

    /**
     * Get UI banner message
     * 
     * @return
     */
    @Override
    public String getBannerMessage() {
        final String message = getApplicationConfig().getProperty(ENIQ_EVENTS_BANNER_MESSAGE);

        if (message == null) {
            return null;
        }

        // To avoid unterminated string literal error in javascript
        return message.replaceAll("\\n", " ").replaceAll("\\r", "").replaceAll("\\\"", "\\\\\"");
    }

    /**
     * Get value from JNDI tree
     * 
     * @return
     */
    @Override
    public String getJNDIValue(final String key) {
        return getApplicationConfig().getProperty(key);
    }

    /**
     * Global user session lock out flag
     * 
     * @return true if set false otherwise
     */
    @Override
    public boolean lockoutUsers() {
        final String userLockoutFlag = getApplicationConfig().getProperty(ENIQ_EVENTS_LOCK_USERS);
        return "true".equalsIgnoreCase(userLockoutFlag);
    }

    /**
     * Get max user sessions property.
     * 
     * @return max user sessions from config db or default to MAX_USER_SESSIONS if
     *         invalid value
     */
    @Override
    public int getMaxUserSessions() {

        final String maxUserSessionText = getApplicationConfig().getProperty(ENIQ_EVENTS_MAX_USER_SESSIONS);
        int maxUserSessions = MAX_USER_SESSIONS;
        try {
            maxUserSessions = Integer.valueOf(maxUserSessionText);
        } catch (final NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid value for property " + ENIQ_EVENTS_MAX_USER_SESSIONS + " ("
                    + maxUserSessionText + "), setting default value of " + MAX_USER_SESSIONS + "", e);

        }

        return maxUserSessions;
    }

    /**
     * Merge app config properties from all sources.
     * 
     * @return properties object with all properties merged
     */
    private Properties getApplicationConfig() {
        final Properties p = new Properties();
        p.putAll(getPropertiesFromDatabase());
        p.putAll(getPropertiesFromJndi());

        return p;
    }

    /**
     * Get application properties object from JNDI tree
     * 
     * @return properties object or an empty properties object
     */
    private static Properties getPropertiesFromJndi() {

        Properties p = null;
        try {
            p = (Properties) (new InitialContext()).lookup(ENIQ_EVENTS_JNDI_NAME);
        } catch (final NamingException ex) {
            LOGGER.log(Level.WARNING, "Error accessing JNDI object " + ENIQ_EVENTS_JNDI_NAME + ": ", ex);
        }

        return (p == null ? new Properties() : p);
    }

    /**
     * Get application properties from repository database
     * 
     * @return properties object populated with admin properties or an empty
     *         properties object if an exception occured
     */
    private Properties getPropertiesFromDatabase() {

        Statement pstmt = null;
        ResultSet rs = null;
        Connection conn = null;

        final Properties p = new Properties();
        try {
            conn = this.dataSource.getConnection();
            pstmt = conn.createStatement();
            rs = pstmt.executeQuery(ADMIN_PROPERTIES_SQL);
            while (rs.next()) {
                p.put(rs.getString(PARAM_NAME), rs.getString(PARAM_VALUE));
            }
        } catch (final SQLException ex) {
            LOGGER.log(Level.WARNING, "Error accessing data source "
                    + ApplicationConfigManager.DWHREP_DATA_SOURCE_JNDI_NAME + ": ", ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing data source connection to "
                        + ApplicationConfigManager.DWHREP_DATA_SOURCE_JNDI_NAME + ": ", e);
            }
        }
        return p;
    }

}
