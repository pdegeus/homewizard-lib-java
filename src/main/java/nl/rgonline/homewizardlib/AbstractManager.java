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

    private Long lastStatusUpdate = null;

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
     * @return The interval (in milliseconds) between reloading the current entity statuses.
     * Return -1 if status updates are not required or supported.
     */
    protected abstract int getStatusUpdateInterval();

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
     * Returns the list with all the switches known in the HomeWizard system.
     * @return A (copy of the) list with switches.
     * @throws HWException On HomeWizard communication errors.
     */
    public List<T> getAll() throws HWException {
        return new ArrayList<>(getAllById().values());
    }

    /**
     * Returns the ID/switch mapping of all switches known in the HomeWizard system.
     * @return A (copy of the) map with switches.
     * @throws HWException On HomeWizard communication errors.
     */
    public Map<Integer, T> getAllById() throws HWException {
        init(false);

        long now = System.currentTimeMillis();
        if (getStatusUpdateInterval() > -1 && (lastStatusUpdate == null || (now - lastStatusUpdate >= getStatusUpdateInterval()))) {
            updateStatus();
            lastStatusUpdate = now;
        }

        return getEntityMap();
    }

    /**
     * Find the switch with the given id.
     * @param id The ID to search for.
     * @return The right HWSwitch if the switch is found, or null.
     * @throws HWException On HomeWizard communication errors.
     */
    public T get(int id) throws HWException {
        return getAllById().get(id);
    }

    /**
     * Find the switch with the given name.
     * @param name The name to search for.
     * @return The right HWSwitch if the switch is found, or null.
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
