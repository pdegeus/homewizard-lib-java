package nl.rgonline.homewizardlib;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import nl.rgonline.homewizardlib.exceptions.HWException;

/**
 * Abstract parent of all entities known by the HomeWizard. An entity is everything the HW interacts with, such as
 * sensors, switches and cameras.
 * @author pdegeus
 */
@Data
@EqualsAndHashCode(of={"id", "name"})
@ToString(exclude="connection")
public abstract class AbstractHwEntity {

    @Setter(AccessLevel.PRIVATE)
    private HWConnection connection;

    @Setter(AccessLevel.PROTECTED)
    private int id;

    @Setter(AccessLevel.PROTECTED)
    protected long lastUpdate;

    private String name;
    private boolean favorite;

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
     * Saves any changes to this entity, such as name and favorite flag, to the HomeWizard.
     * @throws HWException On any HomeWizard update error.
     */
    public void saveChanges() throws HWException {
        saveInternal();
        updated();
    }

    /**
     * Saves any changes to this entity, such as name and favorite flag, to the HomeWizard.
     * @throws HWException On any HomeWizard update error.
     */
    protected abstract void saveInternal() throws HWException;

    /**
     * Indicate this entity was updated. Updates the lastUpdate timestamp.
     */
    public void updated() {
        lastUpdate = System.currentTimeMillis();
    }

}
