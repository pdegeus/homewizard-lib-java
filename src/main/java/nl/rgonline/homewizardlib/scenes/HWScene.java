package nl.rgonline.homewizardlib.scenes;

import lombok.ToString;
import nl.rgonline.homewizardlib.AbstractHwEntity;
import nl.rgonline.homewizardlib.HWConnection;

/**
 * Represents a scene in the HomeWizard system.
 * @author pdegeus
 */
@ToString(callSuper = true)
public class HWScene extends AbstractHwEntity {

    /**
     * Constructor.
     * @param connection Connection to use.
     * @param id Scene ID.
     * @param name Scene name.
     * @param isFavorite True if scene is marked as favorite.
     */
    protected HWScene(HWConnection connection, int id, String name, boolean isFavorite) {
        super(connection, id, name, isFavorite);
    }

}
