package nl.rgonline.homewizardlib.switches;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import nl.rgonline.homewizardlib.AbstractHwEntity;
import nl.rgonline.homewizardlib.HWConnection;
import nl.rgonline.homewizardlib.exceptions.HWException;

/**
 * Represents a switch in the HomeWizard system.
 * @author Ruud Greven
 * @author pdegeus
 */
@Slf4j
@ToString(callSuper=true)
public class HWSwitch extends AbstractHwEntity {

    @Getter @Setter
    protected boolean on;

    /**
     * Constructor.
     * @param connection Connection to use.
     * @param id Switch ID.
     * @param name Switch name.
     * @param isFavorite True if switch is marked as favorite.
     * @param isOn True if switch is on.
     */
    public HWSwitch(HWConnection connection, int id, String name, boolean isFavorite, boolean isOn) {
        super(connection, id, name, isFavorite);
		this.on = isOn;
	}

    /**
     * @return True if this switch is turned off.
     * @throws HWException On IO failures.
     */
	public boolean isOff() throws HWException {
		return !isOn();
	}

    /**
     * Turn on this switch.
     * @throws HWException On IO failures.
     */
	public void turnOn() throws HWException {
        toggle(true);
        on = true;
    }

    /**
     * Turn off this switch.
     * @throws HWException On IO failures.
     */
	public void turnOff() throws HWException {
        toggle(false);
        on = false;
	}

    /**
     * Toggles this switch.
     * @throws HWException On IO failures.
     */
	public void toggle() throws HWException {
		if (isOn()) {
			turnOff();
		} else {
			turnOn();
		}
	}

    /**
     * Toggles this switch. Equivalent to http://<ip>/<password>/sw/<id>/<on|off>
     * @param turnOn True to toggle on, false for off.
     * @throws HWException On IO failures.
     */
    private void toggle(boolean turnOn) throws HWException {
        String onOrOff = (turnOn) ? "on" : "off";
        getConnection().doGet("/sw/", getId(), "/", onOrOff);
    }

}
