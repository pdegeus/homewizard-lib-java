package nl.rgonline.homewizardlib.switches;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import nl.rgonline.homewizardlib.AbstractHwEntity;
import nl.rgonline.homewizardlib.connection.HWConnection;
import nl.rgonline.homewizardlib.connection.Request;
import nl.rgonline.homewizardlib.exceptions.HWException;
import nl.rgonline.homewizardlib.util.UrlUtil;

import org.apache.commons.lang.BooleanUtils;

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
     * @return True if this switch is a dimmer.
     * @deprecated  Use {@link #getType()} to determine the switch type.
     */
    @Deprecated
    public boolean isDimmer() {
        return false;
    }

    /**
     * Indicates the switch type. All switches support the standard on/off actions. For more specific actions, case the switch to
     * the right class:
     * <ul>
     *     <li>{@link SwitchType#DIMMER}: {@link HWDimmer}</li>
     *     <li>{@link SwitchType#HUE_BULB}: {@link HWHueBulb}</li>
     * </ul>
     * @return The type of this switch.
     */
    public SwitchType getType() {
        return SwitchType.STANDARD;
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
     * Toggles this switch. Equivalent to http://[ip]/[password]/sw/[id]/[on|off]
     * @param turnOn True to toggle on, false for off.
     * @throws HWException On IO failures.
     */
    private void toggle(boolean turnOn) throws HWException {
        String onOrOff = (turnOn) ? "on" : "off";
        Request request = new Request("/sw/", getId(), "/", onOrOff);
        getConnection().request(request);
    }

    @Override
    protected void saveInternal() throws HWException {
        String fav = BooleanUtils.toStringYesNo(isFavorite());
        String dim = BooleanUtils.toStringYesNo(isDimmer());

        // /sw/edit/<id>/<name>/<isFav>/<isDim>
        Request request = new Request("/sw/edit/", getId(), "/", UrlUtil.encode(getName()), "/", fav, "/", dim);
        getConnection().request(request);
    }

}
