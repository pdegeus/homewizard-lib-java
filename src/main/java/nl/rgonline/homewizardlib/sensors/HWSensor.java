package nl.rgonline.homewizardlib.sensors;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nl.rgonline.homewizardlib.AbstractHwEntity;
import nl.rgonline.homewizardlib.HWConnection;

/**
 * Represents a sensor in the HomeWizard system.
 * @author pdegeus
 */
@ToString(callSuper = true)
public class HWSensor extends AbstractHwEntity {

    @Getter @Setter
    protected boolean on;

    /**
     * Constructor.
     * @param connection Connection to use.
     * @param id Sensor ID.
     * @param name Sensor name.
     * @param isOn True if sensor is currently 'on'.
     */
    protected HWSensor(HWConnection connection, int id, String name, boolean isOn) {
        super(connection, id, name);
        this.on = isOn;
    }

}
