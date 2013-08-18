package nl.rgonline.homewizardlib.scenes;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import nl.rgonline.homewizardlib.AbstractHwEntity;
import nl.rgonline.homewizardlib.HWAction;
import nl.rgonline.homewizardlib.HWConnection;
import nl.rgonline.homewizardlib.Refreshable;
import nl.rgonline.homewizardlib.exceptions.HWException;
import nl.rgonline.homewizardlib.timers.Day;
import nl.rgonline.homewizardlib.timers.HWTimer;
import nl.rgonline.homewizardlib.timers.TimerSubject;
import nl.rgonline.homewizardlib.timers.TimerTrigger;

import org.apache.commons.lang.BooleanUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a scene in the HomeWizard system.
 * @author pdegeus
 */
public class HWScene extends AbstractHwEntity implements Refreshable {

    private boolean initialized = false;

    private List<HWSceneSwitch> switches;
    private List<HWTimer> timers;
    private List<String> codes;

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

    @Override
    public void init(boolean forceReload) throws HWException {
        if (!initialized || forceReload) {
            try {

                // /gp/get/1/codes
                // "response": [ "J1"]
                codes = new LinkedList<>();
                JSONObject response = getConnection().doGetResp(false, "/gp/get/", getId(), "/codes");
                JSONArray arr = response.getJSONArray("response");
                for (int i=0; i < arr.length(); i++) {
                    codes.add(arr.getString(i));
                }

                // Switches: /gp/get/1/switches
                switches = new LinkedList<>();
                response = getConnection().doGetResp(false, "/gp/get/", getId(), "/switches");
                arr = response.getJSONArray("response");
                for (int i=0; i < arr.length(); i++) {
                    switches.add(readSwitch(arr.getJSONObject(i)));
                }

                // Timers: /gp/get/1/timers
                timers = new LinkedList<>();
                response = getConnection().doGetResp(false, "/gp/get/", getId(), "/timers");
                arr = response.getJSONArray("response");
                for (int i=0; i < arr.length(); i++) {
                    timers.add(readTimer(arr.getJSONObject(i)));
                }

            } catch (JSONException e) {
                throw new HWException("Error initializing timers", e);
            }

            initialized = true;
        }
    }

    private HWSceneSwitch readSwitch(JSONObject switchJson) throws JSONException, HWException {
        // "response": [ {"type":"switch","id":3,"name":"TestSch","onstatus":1,"offstatus":0,"dimmer":"no"} ]

        //Check type
        String type = switchJson.getString("type");
        if (!type.equals("switch")) {
            throw new HWException("Unknown switch type: " + type);
        }

        //Read data
        int id = switchJson.getInt("id");
        String name = switchJson.getString("name");
        HWAction sceneOnAction = HWAction.forNumber(switchJson.getInt("onstatus"));
        HWAction sceneOffAction = HWAction.forNumber(switchJson.getInt("offstatus"));
        boolean isDimmer = BooleanUtils.toBoolean(switchJson.getString("dimmer"));

        return new HWSceneSwitch(id, name, sceneOnAction, sceneOffAction, isDimmer);
    }

    private HWTimer readTimer(JSONObject timerJson) throws JSONException {
        // "response": [
        //   {"id":0,"action":"off","trigger":"sunrise","time":"-0","days":[0, 6],"active":"yes"},
        //   {"id":1,"action":"on","trigger":"sunset","time":"-183","days":[2],"active":"no"},
        //   {"id":2,"action":"off","trigger":"time","time":"10:01","days":[7],"active":"yes"}
        // ]

        int id = timerJson.getInt("id");
        TimerTrigger trigger = TimerTrigger.forString(timerJson.getString("trigger"));
        HWAction action = HWAction.forString(timerJson.getString("action"));
        boolean active = BooleanUtils.toBoolean(timerJson.getString("active"));
        String timeOrOffset = timerJson.getString("time");
        Set<Day> days = Day.readArray(timerJson.getJSONArray("days"));

        return new HWTimer(getConnection(), id, trigger, action, TimerSubject.SCENE, getId(), active, timeOrOffset, days);
    }

    /**
     * Retrieve all switches for this scene.
     * Data is loaded the first time any getter of this scene is used.
     * @return List of switch data.
     * @throws HWException On any IO or JSON error.
     */
    public List<HWSceneSwitch> getSwitches() throws HWException {
        init(false);
        return switches;
    }

    /**
     * Retrieve all timers for this scene.
     * Data is loaded the first time any getter of this scene is used.
     * @return List of timer data.
     * @throws HWException On any IO or JSON error.
     */
    public List<HWTimer> getTimers() throws HWException {
        init(false);
        return timers;
    }

    /**
     * Retrieve all codes for this scene.
     * Data is loaded the first time any getter of this scene is used.
     * @return List of codes.
     * @throws HWException On any IO or JSON error.
     */
    public List<String> getCodes() throws HWException {
        init(false);
        return codes;
    }

    /**
     * Turn on this scene.
     * @throws HWException On IO failures.
     */
    public void turnOn() throws HWException {
        toggle(true);
    }

    /**
     * Turn off this scene.
     * @throws HWException On IO failures.
     */
    public void turnOff() throws HWException {
        toggle(false);
    }

    /**
     * Toggles this scene. Equivalent to http://<ip>/<password>/gp/<id>/<on|off>
     * @param turnOn True to toggle on, false for off.
     * @throws HWException On IO failures.
     */
    private void toggle(boolean turnOn) throws HWException {
        String onOrOff = (turnOn) ? "on" : "off";
        getConnection().doGet("/gp/", getId(), "/", onOrOff);
    }

    @Override
    public String toString() {
        return String.format("[HwScene#%d '%s']", getId(), getName());
    }

}
