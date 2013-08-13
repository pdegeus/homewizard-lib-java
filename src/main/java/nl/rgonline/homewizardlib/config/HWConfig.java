package nl.rgonline.homewizardlib.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;

/**
 * Dcore configuration class.
 * @author pdegeus
 */
@Slf4j
public class HWConfig {

    /* ConfigItems */

    /** Config item for HW hostname/IP */
    public static final HWConfigItem<String> HOST = new HWConfigItem<>("host", String.class);

    /** Config item for HW connection port */
    public static final HWConfigItem<Integer> PORT = new HWConfigItem<>("port", Integer.class, 80);

    /** Config item for HW password */
    public static final HWConfigItem<String> PASSWORD = new HWConfigItem<>("password", String.class);

    /** Config item for switches status update interval */
    public static final HWConfigItem<Integer> SWITCH_UPDATE_INTERVAL = new HWConfigItem<>("updateinterval.switch", Integer.class, 2000);

    /** Config item for sensors status update interval */
    public static final HWConfigItem<Integer> SENSOR_UPDATE_INTERVAL = new HWConfigItem<>("updateinterval.sensor", Integer.class, 4000);

    /** Config item for thermometer data update interval */
    public static final HWConfigItem<Integer> THERMO_UPDATE_INTERVAL = new HWConfigItem<>("updateinterval.thermo", Integer.class, 20000);


    /* Config loader */

    private static final String DEFAULT_CFG_FILE = "homewizard.cfg";
    private static final String CFG_OVERRIDE_PROPERTY = "hwconfig";

    // Interval in seconds at which config is reloaded by default
    private static final int RELOAD_INTERVAL = 30;
    
    private static long lastUpdate = -1;
    
    private static Properties properties = null;

    /**
     * Reloads the configuration from JNDI if needed
     * @throws IllegalStateException When no JNDI name is set
     */
    private static void reloadConfig() throws IllegalStateException {
        reloadConfig(false);
    }

    /**
     * Reloads the configuration from JNDI if needed or forces
     * @param force Set to true to force a reload
     * @throws IllegalStateException When no JNDI name is set
     */
    private static synchronized void reloadConfig(boolean force) throws IllegalStateException {
        
        if (force || lastUpdate == -1 || (System.currentTimeMillis() - lastUpdate) / 1000 > RELOAD_INTERVAL) {

            properties = null;

            String systemProp = System.getProperty(CFG_OVERRIDE_PROPERTY);
            if (StringUtils.isNotEmpty(systemProp)) {
                properties = loadConfig(new File(systemProp));
            }

            if (properties == null) {
                HWConfig.log.debug("No readable config file in system property " + CFG_OVERRIDE_PROPERTY);
                properties = loadConfig(new File(DEFAULT_CFG_FILE));
                if (properties == null) {
                    HWConfig.log.debug("No readable config file at ./" + DEFAULT_CFG_FILE);
                    properties = loadConfig(new File("../" + DEFAULT_CFG_FILE));
                }
            }

            if (properties == null) {
                log.warn(
                    "Could not find suitable config file. Set the system property '{}' to point to your config file, " +
                    "or place a file named '{}' in your working dir.", CFG_OVERRIDE_PROPERTY, DEFAULT_CFG_FILE
                );
            }

            // Save new last-update time
            lastUpdate = System.currentTimeMillis();
        }
        
    }

    private static Properties loadConfig(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }

        Properties props = new Properties();
        try {
            InputStream is = new FileInputStream(file);
            props.load(is);
        } catch (IOException e) {
            throw new ConfigurationException("Could not read from file " + file, e);
        }
        return props;
    }

    /**
     * Retrieves the raw value from the properties as an Object.
     * @param propertyName Property name to find.
     * @return Raw property value, or null.
     */
    public static Object getRaw(String propertyName) {
        return getProjectSettings().get(propertyName);
    }
    
    /**
     * @return Cached project settings Properties map.
     */
    protected static Properties getProjectSettings() {
        reloadConfig();
        return properties;
    }

    /**
     * Hidden constructor.
     */
    private HWConfig() {
    }

}
