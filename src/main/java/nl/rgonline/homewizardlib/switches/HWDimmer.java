package nl.rgonline.homewizardlib.switches;

import lombok.Getter;
import lombok.ToString;
import nl.rgonline.homewizardlib.HWConnection;
import nl.rgonline.homewizardlib.exceptions.HWException;

/**
 * Represents a dimmer in the HomeWizard system.
 * @author Ruud Greven
 * @author pdegeus
 */
@ToString(callSuper = true)
public class HWDimmer extends HWSwitch {

    @Getter
	private int dimLevel;

    /**
     * Constructor.
     * @param connection Connection to use.
     * @param id Dimmer ID.
     * @param name Dimmer name.
     * @param isFavorite True if switch is marked as favorite.
     * @param isOn Current on/off state.
     * @param dimLevel Current dim-level.
     */
    public HWDimmer(HWConnection connection, int id, String name, boolean isFavorite, boolean isOn, int dimLevel) {
		super(connection, id, name, isFavorite, isOn);
		this.dimLevel = dimLevel;
	}

    /**
     * Sets dim-level of this dimmer.
     * @param dimLevel New dim-level.
     * @throws HWException On IO failures.
     */
    void setDimLevel(int dimLevel) throws HWException {
        this.dimLevel = dimLevel;
        getConnection().doGet("/sw/dim/", getId(), "/", dimLevel);
    }
	
}
