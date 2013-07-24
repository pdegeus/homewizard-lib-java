package nl.rgonline.homewizardlib.exceptions;

/**
 * Exception thrown when a request is made 'to early'.
 */
public class HWToEarlyException extends HWException {

    /**
     * Constructor.
     */
	public HWToEarlyException() {
		super("You're a bit to early, the timeout isn't passed yet!");
	}

}
