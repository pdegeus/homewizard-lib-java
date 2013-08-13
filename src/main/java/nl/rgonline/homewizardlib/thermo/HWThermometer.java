package nl.rgonline.homewizardlib.thermo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import nl.rgonline.homewizardlib.AbstractHwEntity;
import nl.rgonline.homewizardlib.HWConnection;

/**
 * Represents a thermometer in the HomeWizard system.
 * @author pdegeus
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HWThermometer extends AbstractHwEntity {

    private int channel;
    private Integer humidity;
    private Double temperature;

    /**
     * Constructor.
     * @param connection Connection to use.
     * @param id Thermometer ID.
     * @param name Thermometer name.
     * @param isFavorite True if thermometer is marked as favorite.
     * @param channel Communication channel.
     * @param humidity Current humidity, percentage.
     * @param temperature Current temperature.
     */
    protected HWThermometer(HWConnection connection, int id, String name, boolean isFavorite, int channel, Integer humidity, Double temperature) {
        super(connection, id, name, isFavorite);
        this.channel = channel;
        this.humidity = humidity;
        this.temperature = temperature;
    }

}
