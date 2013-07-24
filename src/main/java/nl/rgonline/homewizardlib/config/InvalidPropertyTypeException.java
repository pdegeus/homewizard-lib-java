package nl.rgonline.homewizardlib.config;

/**
 * Exception thrown when a configuration property could not be read because the value
 * type is incorrect. 
 * @author pdegeus
 */
public class InvalidPropertyTypeException extends ConfigurationException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor without cause.
     * @param property {@link HWConfigItem} for which a invalid value was found.
     */
    public InvalidPropertyTypeException(HWConfigItem<?> property) {
        super(getMessage(property));
    }
    
    /**
     * Constructor with cause {@link Exception}.
     * @param property {@link HWConfigItem} for which a invalid value was found.
     * @param cause Cause of the problem.
     */
    public InvalidPropertyTypeException(HWConfigItem<?> property, Exception cause) {
        super(getMessage(property), cause);
    }

    /**
     * Constructor with custom error message and cause.
     * @param message Custom error message.
     * @param cause Cause of the problem.
     */
    public InvalidPropertyTypeException(String message, Exception cause) {
        super(message, cause);
    }

    private static String getMessage(HWConfigItem<?> property) {
        return String.format(
            "Configuration property value for '%s' is of incorrect type, should be %s.",
            property.getPropertyName(), property.getType().getName()
        );
    }

}
