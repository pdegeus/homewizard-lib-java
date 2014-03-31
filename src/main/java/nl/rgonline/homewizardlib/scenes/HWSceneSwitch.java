package nl.rgonline.homewizardlib.scenes;

import lombok.Data;
import nl.rgonline.homewizardlib.HWAction;
import nl.rgonline.homewizardlib.switches.SwitchManager;
import nl.rgonline.homewizardlib.switches.SwitchType;
import nl.rgonline.homewizardlib.util.HueColor;

/**
 * Data object representing information about a switch assigned to a {@link HWScene}.
 * The actual switch can be obtained from the {@link SwitchManager} using the ID.
 * @author pdegeus
 */
@Data
public class HWSceneSwitch {

    private int id;
    private SwitchType type;
    private String name;
    private HWAction sceneOnAction;
    private HWAction sceneOffAction;
    private HueColor sceneOnColor;
    private HueColor sceneOffColor;

    @Deprecated
    private boolean isDimmer;

    /**
     * Constructor.
     * @param id Switch ID.
     * @param type Switch type.
     * @param name Switch name.
     * @param sceneOnAction Action to perform when scene is turned on.
     * @param sceneOffAction Action to perform when scene is turned off.
     */
    public HWSceneSwitch(int id, SwitchType type, String name, HWAction sceneOnAction, HWAction sceneOffAction) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.sceneOnAction = sceneOnAction;
        this.sceneOffAction = sceneOffAction;
    }

    /**
     * Constructor.
     * @param id Switch ID.
     * @param name Switch name.
     * @param sceneOnAction Action to perform when scene is turned on.
     * @param sceneOffAction Action to perform when scene is turned off.
     * @param isDimmer True if the switch is a dimmer.
     * @deprecated Use the constructor with a {@link SwitchType}.
     */
    @Deprecated
    public HWSceneSwitch(int id, String name, HWAction sceneOnAction, HWAction sceneOffAction, boolean isDimmer) {
        this(id, isDimmer ? SwitchType.DIMMER : SwitchType.STANDARD, name, sceneOnAction, sceneOffAction);
    }

    /**
     * @return True if this switch is a dimmer.
     * @deprecated Use {@link #getType()} to determine the type.
     */
    @Deprecated
    public boolean isDimmer() {
        return isDimmer;
    }

}
