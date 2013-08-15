package nl.rgonline.homewizardlib;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.rgonline.homewizardlib.cameras.CameraManager;
import nl.rgonline.homewizardlib.config.HWConfig;
import nl.rgonline.homewizardlib.exceptions.HWException;
import nl.rgonline.homewizardlib.scenes.SceneManager;
import nl.rgonline.homewizardlib.sensors.SensorManager;
import nl.rgonline.homewizardlib.switches.SwitchManager;
import nl.rgonline.homewizardlib.thermo.ThermoManager;
import nl.rgonline.homewizardlib.timers.TimerManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * HWSystem is a, reversed engineered, interface to the HomeWizard system (http://www.homewizard.nl).
 * @version 0.2, tested with HomeWizard version 2.41
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
    private ThermoManager thermoManager;

    @Getter
    private CameraManager cameraManager;

    @Getter
    private SceneManager sceneManager;

    @Getter
    private TimerManager timerManager;

    @Getter
    private double hwVersion;

    /**
     * Constructor reading connection details from config file..
     * @throws HWException On any initialization error.
     */
    public HWSystem() throws HWException {
        this(HWConfig.HOST.getValue(), HWConfig.PORT.getValue(), HWConfig.PASSWORD.getValue());
    }

    /**
     * Constructor using provided connection details.
     * @param host Host to connect to.
     * @param port Port to connect to.
     * @param password Password to use.
     * @throws HWException On any initialization error.
     */
	public HWSystem(String host, int port, String password) throws HWException {
		connection = new HWConnection(host, port, password);
        switchManager = new SwitchManager(connection);
        sensorManager = new SensorManager(connection);
        thermoManager = new ThermoManager(connection);
        cameraManager = new CameraManager(connection);
        sceneManager = new SceneManager(connection);
        timerManager = new TimerManager(connection);

        readStatus();
        log.info("HWSystem initialized, HW version: " + hwVersion);
	}


    /**
     * Read the current HomeWizard status and version.
     */
    private void readStatus() throws HWException {
        JSONObject status = connection.doGetResp(false, "/get-status");
        try {
            hwVersion = status.getDouble("version");
        } catch (JSONException e) {
            throw new HWException("Could not read HW version", e);
        }
    }

}
