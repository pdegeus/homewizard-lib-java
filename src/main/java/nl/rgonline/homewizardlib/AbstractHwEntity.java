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
@EqualsAndHashCode(of={"id", "name"})
@ToString(exclude="connection")
public abstract class AbstractHwEntity {

    private HWConnection connection;

    private int id;
    private String name;
    private boolean favorite;
    protected long lastUpdate;

    /**
     * Constructor.
     * @param connection Connection to use.
     * @param id Entity ID.
     * @param name Entity name.
     * @param favorite Favorite flag.
     */
    protected AbstractHwEntity(HWConnection connection, int id, String name, boolean favorite) {
        this.connection = connection;
        this.id = id;
        this.name = name;
        this.favorite = favorite;
        this.lastUpdate = System.currentTimeMillis();
    }

    /**
     * Indicate this entity was updated. Updates the lastUpdate timestamp.
     */
    public void updated() {
        lastUpdate = System.currentTimeMillis();
    }

}
