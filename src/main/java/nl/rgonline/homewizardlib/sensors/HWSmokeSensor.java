package nl.rgonline.homewizardlib.sensors;

import nl.rgonline.homewizardlib.connection.HWConnection;
import nl.rgonline.homewizardlib.exceptions.HWException;

/**
 * The smoke sensor has the ability to be triggered manually.
 * @author pdegeus
 */
public class HWSmokeSensor extends HWSensor {

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
    public HWSmokeSensor(
        HWConnection connection, int id, String name, SensorType type, String lastEventTime, boolean isFavorite, boolean isOn
    ) {
        super(connection, id, name, type, lastEventTime, isFavorite, isOn);
    }

    /**
     * Triggers this smoke detector.<br/>
     * <b>Note: </b> The detector will behave just like when it really detects smoke. This means it will beep (of course),
     * but also continuously send out an RF signal, preventing any other communication on the frequency.
     * @throws HWException On any IO or JSON error.
     */
    public void trigger() throws HWException {
        getConnection().request("/kks/testsmoke/", getId());
    }

}
