package nl.rgonline.homewizardlib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.rgonline.homewizardlib.exceptions.HWException;

/**
 * Abstract parent of all HomeWizard entity managers.
 * @param <T> Type of the managed entities.
 * @author pdegeus
 */
public abstract class AbstractManager<T extends AbstractHwEntity> implements Refreshable {

    private final long updateInterval;
    private Long lastStatusUpdate = null;

    /**
     * Constructor.
     * @param updateInterval The interval (in milliseconds) between reloading the current entity statuses.
     * Return -1 if status updates are not required or supported.
     */
    public AbstractManager(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    /**
     * Updates the status of all loaded entities.
     * @throws HWException On any IO or JSON error.
     */
    protected abstract void updateStatus() throws HWException;

    /**
     * @return The map containing all entities for this manager by ID.
     */
    protected abstract Map<Integer, T> getEntityMap();

    /**
     * @return Update interval in milliseconds.
     */
    protected long getUpdateInterval() {
        return updateInterval;
    }

    /**
     * Force a refresh of all data managed by this manager. This does not only update the
     * status of the entities (such as sensor state, which is done automatically), but also
     * reloads the entity list.
     * @throws HWException On any IO or JSON error.
     */
    public void refresh() throws HWException {
        init(true);
    }

    /**
     * Updates the status of this manager if the current state has expired, as indicated by the update interval.
     * Updating the status does not reload all data (as {@link #refresh()} does), but only reloads the state of currently
     * known HW entities, such as sensors. Hence, entities added to the HW in the meantime will not be seen.
     * <p/>
     * Access to this method is synchronized to ensure calls from different threads will not result in duplicate updates.
     * @throws HWException On any IO or JSON error.
     */
    public synchronized void updateStatusIfExpired() throws HWException {
        init(false);

        if (updateInterval > -1) {
            long now = System.currentTimeMillis();
            if (lastStatusUpdate == null || (now - lastStatusUpdate >= updateInterval)) {
                updateStatus();
                lastStatusUpdate = now;
            }
        }
    }

    /**
     * Returns the list with all the entities known by this manager.
     * @return A (copy of the) list of entities of type {@code T}.
     * @throws HWException On HomeWizard communication errors.
     */
    public List<T> getAll() throws HWException {
        return new ArrayList<>(getAllById().values());
    }

    /**
     * Returns the ID/entity mapping of all entities known by this manager.
     * @return A (copy of the) map containing all entities of type {@code T}.
     * @throws HWException On HomeWizard communication errors.
     */
    public Map<Integer, T> getAllById() throws HWException {
        updateStatusIfExpired();
        return getEntityMap();
    }

    /**
     * Find the entity with the given ID.
     * @param id The ID to search for.
     * @return Entity instance of type {@code T} if it's found, or null.
     * @throws HWException On HomeWizard communication errors.
     */
    public T get(int id) throws HWException {
        return getAllById().get(id);
    }

    /**
     * Find the entity with the given name.
     * @param name The name to search for.
     * @return Entity instance of type {@code T} if it's found, or null.
     * @throws HWException On HomeWizard communication errors.
     */
    public T getByName(String name) throws HWException {
        for (T item : getAllById().values()) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

}
