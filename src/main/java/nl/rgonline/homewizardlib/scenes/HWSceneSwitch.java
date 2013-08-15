package nl.rgonline.homewizardlib.scenes;

import lombok.AllArgsConstructor;
import lombok.Data;
import nl.rgonline.homewizardlib.HWAction;
import nl.rgonline.homewizardlib.switches.SwitchManager;

/**
 * Data object representing information about a switch assigned to a {@link HWScene}.
 * The actual switch can be obtained from the {@link SwitchManager} using the ID.
 * @author pdegeus
 */
@Data
@AllArgsConstructor
public class HWSceneSwitch {

    private int id;
    private String name;
    private HWAction sceneOnAction;
    private HWAction sceneOffAction;
    private boolean isDimmer;

}
