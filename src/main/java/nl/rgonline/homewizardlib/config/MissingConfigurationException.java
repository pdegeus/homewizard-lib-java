package nl.rgonline.homewizardlib.config;

/**
 * Exception thrown when a required configuration value is missing.
 * @author pdegeus
 */
public class MissingConfigurationException extends ConfigurationException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * @param property Config property causing the exception.
     */
    public MissingConfigurationException(HWConfigItem<?> property) {
        super("Missing configuration value for " + property.getPropertyName());
    }

}
