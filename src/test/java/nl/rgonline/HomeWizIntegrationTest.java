package nl.rgonline;

import lombok.extern.slf4j.Slf4j;
import nl.rgonline.homewizardlib.HWSystem;
import nl.rgonline.homewizardlib.exceptions.HWException;
import nl.rgonline.homewizardlib.thermo.TimeSpan;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * High-level integration test fetching all info from the configured HW.
 * @author pdegeus
 */
@Slf4j
public class HomeWizIntegrationTest {

    private static HWSystem hw;

    @BeforeClass
    public static void init() throws HWException {
        hw = new HWSystem();
    }

    @Test
    public void testGetSwitches() throws Exception {
        log.info("Switches: {}", hw.getSwitchManager().getAll());
        log.info("Switch 1: {}", hw.getSwitchManager().get(1).isOn());

        log.info("Sleeping 2.5s to test reloading");
        Thread.sleep(2500);

        log.info("Switches: {}", hw.getSwitchManager().getAll());
    }

    @Test
    public void testGetSensors() throws HWException {
        log.info("Sensors: {}", hw.getSensorManager().getAll());
        log.info("Sensor 1: {}", hw.getSensorManager().get(1).isOn());
    }

    @Test
    public void testGetCameras() throws HWException {
        log.info("Cameras: {}", hw.getCameraManager().getAll());
    }

    @Test
    public void testGetScenes() throws HWException {
        log.info("Scenes: {}", hw.getSceneManager().getAll());
    }

    @Test
    public void testGetWeather() throws HWException {
        log.info("Thermos: {}", hw.getThermoManager().getAll());
        log.info("Humidity log (day, single value): {}", hw.getThermoManager().get(1).getHumidityHistory(TimeSpan.DAY));
        log.info("Humidity log (month, min/max): {}", hw.getThermoManager().get(1).getHumidityHistory(TimeSpan.MONTH));

        log.info("Thermo log (day, single value): {}", hw.getThermoManager().get(1).getTemperatureHistory(TimeSpan.DAY));
        log.info("Thermo log (month, min/max): {}", hw.getThermoManager().get(1).getTemperatureHistory(TimeSpan.MONTH));
    }

}
