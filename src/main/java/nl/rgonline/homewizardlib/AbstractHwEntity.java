package nl.rgonline.homewizardlib;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Abstract parent of all entities known by the HomeWizard. An entity is everything the HW interacts with, such as
 * sensors, switches and cameras.
 * @author pdegeus
 */
@Data
@EqualsAndHashCode(of="id")
@ToString(exclude="connection")
public abstract class AbstractHwEntity {

    private HWConnection connection;

    private int id;
    private String name;
    protected long lastUpdate;

    /**
     * Constructor.
     * @param connection Connection to use.
     * @param id Entity ID.
     * @param name Entity name.
     */
    protected AbstractHwEntity(HWConnection connection, int id, String name) {
        this.connection = connection;
        this.id = id;
        this.name = name;
        this.lastUpdate = System.currentTimeMillis();
    }

    /**
     * Indicate this entity was updated. Updates the lastUpdate timestamp.
     */
    public void updated() {
        lastUpdate = System.currentTimeMillis();
    }

}
