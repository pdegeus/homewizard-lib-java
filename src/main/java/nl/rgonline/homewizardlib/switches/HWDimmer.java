package nl.rgonline.homewizardlib.switches;

import lombok.Getter;
import lombok.ToString;
import nl.rgonline.homewizardlib.connection.HWConnection;
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

    @Override
    public SwitchType getType() {
        return SwitchType.DIMMER;
    }

    @Override
    public boolean isDimmer() {
        return true;
    }

    /**
     * Sets dim-level of this dimmer. Updates HomeWizard immediately (no need to call {@link #saveChanges()}.
     * Equivalent of http://[ip]/[password]/sw/dim/[id]/[dim-level].
     * @param dimLevel New dim-level.
     * @throws HWException On IO failures.
     */
    public void setDimLevel(int dimLevel) throws HWException {
        this.dimLevel = dimLevel;
        getConnection().request("/sw/dim/", getId(), "/", dimLevel);
    }
	
}
