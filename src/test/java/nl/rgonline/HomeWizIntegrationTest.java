package nl.rgonline;

import lombok.extern.slf4j.Slf4j;
import nl.rgonline.homewizardlib.HWSystem;

import org.junit.Test;

/**
 * High-level integration test fetching all info from the configured HW.
 * @author pdegeus
 */
@Slf4j
public class HomeWizIntegrationTest {

    @Test
    public void testGetSwitches() throws Exception {
        HWSystem hw = new HWSystem();

        log.info("Switches: {}", hw.getSwitchManager().getAll());
        log.info("Switch 1: {}", hw.getSwitchManager().get(1).isOn());

        log.info("Sensors: {}", hw.getSensorManager().getAll());
        log.info("Sensor 1: {}", hw.getSensorManager().get(1).isOn());

        log.info("Sleeping 2.5s to test reloading");
        Thread.sleep(2500);

        log.info("Switches: {}", hw.getSwitchManager().getAll());

    }

}
