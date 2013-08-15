package nl.rgonline.homewizardlib.timers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import nl.rgonline.homewizardlib.AbstractManager;
import nl.rgonline.homewizardlib.HWAction;
import nl.rgonline.homewizardlib.HWConnection;
import nl.rgonline.homewizardlib.exceptions.HWException;
import nl.rgonline.homewizardlib.util.JsonUtil;

import org.apache.commons.lang.BooleanUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Switch manager.
 * @author pdegeus
 */
@Slf4j
public class TimerManager extends AbstractManager<HWTimer> {

    private final HWConnection connection;

    private Map<Integer, HWTimer> timers;
    private volatile boolean initialized = false;

    /**
     * Constructor.
     * @param connection Connection to use.
     */
    public TimerManager(HWConnection connection) {
        this.connection = connection;
    }

    @Override
    public void init(boolean forceReload) throws HWException {
        if (!initialized || forceReload) {
            JSONObject response = connection.doGetResp(false, "/timers");

            // "response": [
            //   {"id":0,"gpid":1,"type":"scene","sensor_id":1,"action":"off","trigger":"sunrise","time":"-0","days":[0, 6],"active":"yes"},
            //   {"id":1,"gpid":1,"type":"scene","sensor_id":1,"action":"on","trigger":"sunset","time":"-183","days":[2],"active":"no"},
            //   {"id":2,"gpid":1,"type":"scene","sensor_id":1,"action":"off","trigger":"time","time":"10:01","days":[7],"active":"yes"},
            //   {"id":3,"swid":3,"type":"switch","sensor_id":3,"action":"off","trigger":"time","time":"16:30","days":[1],"active":"yes"}
            // ]

            try {
                JSONArray jsonSwitches = response.getJSONArray("response");
                int numSwitches = jsonSwitches.length();

                timers = new HashMap<>();

                for (int i = 0; i < numSwitches; i++) {
                    JSONObject timerJson = jsonSwitches.getJSONObject(i);

                    //Simple data
                    int id = timerJson.getInt("id");
                    TimerTrigger trigger = TimerTrigger.forString(timerJson.getString("trigger"));
                    HWAction action = HWAction.forString(timerJson.getString("action"));
                    TimerSubject subject = TimerSubject.forString(timerJson.getString("type"));

                    String subjectIdField = (subject == TimerSubject.SCENE) ? "gpid" : "swid";
                    int subjectId = JsonUtil.readInteger(timerJson, subjectIdField);

                    boolean active = BooleanUtils.toBoolean(timerJson.getString("active"));
                    String timeOrOffset = timerJson.getString("time");
                    Set<Day> days = Day.readArray(timerJson.getJSONArray("days"));

                    //Create & add timer
                    HWTimer timer = new HWTimer(connection, id, trigger, action, subject, subjectId, active, timeOrOffset, days);
                    timers.put(id, timer);

                }
            } catch (JSONException e) {
                throw new HWException("Error initializing timers", e);
            }

            initialized = true;
        }
    }

    @Override
    protected void updateStatus() throws HWException {
        //Nothing to do
    }

    @Override
    public Map<Integer, HWTimer> getEntityMap() {
        return timers;
    }

    @Override
    protected int getStatusUpdateInterval() {
        return -1;
    }
}
