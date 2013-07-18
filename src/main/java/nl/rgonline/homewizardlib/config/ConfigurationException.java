package nl.rgonline.homewizardlib.config;

/**
 * Generic configuration exception.
 * @author pdegeus
 */
public class ConfigurationException extends RuntimeException {

    private static final long serialVersionUID = -8410899202039433617L;

    /**
     * Constructor.
     * @param message Message.
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param message Message.
     * @param cause Error cause.
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
