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
    private SensorType type;

    @Getter @Setter
    private String lastEventTime;

    @Getter @Setter
    private boolean on;

    /**
     * Constructor.
     * @param connection Connection to use.
     * @param id Sensor ID.
     * @param name Sensor name.
     * @param type Sensor type.
     * @param lastEventTime Last sensor event time string.
     * @param isFavorite True if switch is marked as favorite.
     * @param isOn True if sensor is currently 'on'.
     */
    public HWSensor(HWConnection connection, int id, String name, SensorType type, String lastEventTime, boolean isFavorite, boolean isOn) {
        super(connection, id, name, isFavorite);
        this.type = type;
        this.lastEventTime = lastEventTime;
        this.on = isOn;
    }

}
