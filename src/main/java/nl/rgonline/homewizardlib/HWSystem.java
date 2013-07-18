package nl.rgonline.homewizardlib;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.rgonline.homewizardlib.exceptions.HWException;
import nl.rgonline.homewizardlib.sensors.SensorManager;
import nl.rgonline.homewizardlib.switches.SwitchManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * HWSystem is a, reversed engineered, interface to the HomeWizard system (http://www.homewizard.nl).
 * @version 0.2, tested with HomeWizard version 2.352
 * @author Ruud Greven
 * @author pdegeus
 */
@Slf4j
public final class HWSystem {

    @Getter
	private HWConnection connection;

    @Getter
    private SwitchManager switchManager;

    @Getter
    private SensorManager sensorManager;

    @Getter
    private double hwVersion;

    /**
     * Constructor.
     * @throws HWException On any initialization error.
     */
	public HWSystem() throws HWException {
		connection = new HWConnection();
        switchManager = new SwitchManager(connection);
        sensorManager = new SensorManager(connection);

        readStatus();
        log.info("HWSystem initialized, HW version: " + hwVersion);
	}


    /**
     * Read the current HomeWizard status and version.
     */
    private void readStatus() throws HWException {
        JSONObject status = connection.doGetResp(false, "/get-status");
        log.error("Retrieve status: {}", status);

        try {
            hwVersion = status.getDouble("version");
        } catch (JSONException e) {
            throw new HWException("Could not read HW version", e);
        }
    }

}
