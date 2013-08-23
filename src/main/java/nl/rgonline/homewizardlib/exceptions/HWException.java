package nl.rgonline.homewizardlib.exceptions;

/**
 * HomeWizard exception.
 * @author Ruud Greven
 */
public class HWException extends Exception {

    private static final long serialVersionUID = 6255355579277358724L;

    /**
     * Constructor.
     * @param message Error message.
     */
	public HWException(String message) {
		super(message);
	}

    /**
     * Constructor.
     * @param message Error message.
     * @param cause Error cause.
     */
	public HWException(String message, Exception cause) {
		super(message, cause);
	}

}
