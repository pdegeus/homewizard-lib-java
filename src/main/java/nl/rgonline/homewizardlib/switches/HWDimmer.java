package nl.rgonline.homewizardlib.switches;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import nl.rgonline.homewizardlib.HWConnection;
import nl.rgonline.homewizardlib.exceptions.HWException;

/**
 * Represents a dimmer in the HomeWizard system.
 * @author Ruud Greven
 * @author pdegeus
 */
@Data
@EqualsAndHashCode
@ToString(callSuper = true)
public class HWDimmer extends HWSwitch {

	private int dimLevel;

    /**
     * Constructor.
     * @param connection Connection to use.
     * @param id Dimmer ID.
     * @param name Dimmer name.
     * @param isOn Current on/off state.
     * @param dimLevel Current dim-level.
     */
    public HWDimmer(HWConnection connection, int id, String name, boolean isOn, int dimLevel) {
		super(connection, id, name, isOn);
		this.dimLevel = dimLevel;
	}

    /**
     * Sets dim-level of this dimmer.
     * @param dimLevel New dim-level.
     * @throws HWException On IO failures.
     */
    void setDimLevel(int dimLevel) throws HWException {
        getConnection().doGet("/sw/dim/", getId(), "/", dimLevel);
    }
	
}
