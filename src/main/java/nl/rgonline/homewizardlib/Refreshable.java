package nl.rgonline.homewizardlib;

import nl.rgonline.homewizardlib.exceptions.HWException;
import nl.rgonline.homewizardlib.scenes.HWScene;
import nl.rgonline.homewizardlib.switches.SwitchManager;

/**
 * Interface used by all classes the can be refreshed, such as the {@link SwitchManager} (and other
 * {@link AbstractHwEntity} managers) and {@link HWScene HWScenes}.
 * @author pdegeus
 */
public interface Refreshable {

    /**
     * Initializes or reloads the data of this Refreshable. If {@code forceReload == false}, only the
     * first call to this method will trigger API usage.
     * @param forceReload True to enforce reloading.
     * @throws HWException On any IO or JSON error.
     */
    void init(boolean forceReload) throws HWException;

}
