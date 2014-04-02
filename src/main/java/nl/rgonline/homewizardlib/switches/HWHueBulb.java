package nl.rgonline.homewizardlib.switches;

import lombok.Getter;
import lombok.ToString;
import nl.rgonline.homewizardlib.connection.HWConnection;
import nl.rgonline.homewizardlib.exceptions.HWException;
import nl.rgonline.homewizardlib.util.HueColor;

/**
 * Represents a connected Hue bulb in the HomeWizard system.
 * @author pdegeus
 */
@ToString(callSuper = true)
public class HWHueBulb extends HWSwitch {

    @Getter
	private HueColor color;

    /**
     * Constructor.
     * @param connection Connection to use.
     * @param id Dimmer ID.
     * @param name Dimmer name.
     * @param isFavorite True if switch is marked as favorite.
     * @param isOn Current on/off state.
     * @param color Current color.
     */
    public HWHueBulb(HWConnection connection, int id, String name, boolean isFavorite, boolean isOn, HueColor color) {
		super(connection, id, name, isFavorite, isOn);
        this.color = color;
	}

    @Override
    public SwitchType getType() {
        return SwitchType.HUE_BULB;
    }

    /**
     * Sets the bulb color. Updates HomeWizard immediately (no need to call {@link #saveChanges()}.
     * Equivalent of http://[ip]/[password]/sw/[id]/[on|off]/[hue]/[sat]/[bri]
     * @param color Color to set.
     * @throws HWException On IO failures.
     */
    public void setColor(HueColor color) throws HWException {
        this.color = color;
        String onOff = isOn() ? "on" : "off";
        getConnection().request("/sw/", getId(), "/", onOff, "/", color.getHue(), "/", color.getSaturation(), "/", color.getBrightness());
    }

}
