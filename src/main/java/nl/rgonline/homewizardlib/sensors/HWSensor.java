package nl.rgonline.homewizardlib.sensors;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

import lombok.Getter;
import lombok.ToString;
import nl.rgonline.homewizardlib.AbstractHwEntity;
import nl.rgonline.homewizardlib.connection.HWConnection;
import nl.rgonline.homewizardlib.connection.Request;
import nl.rgonline.homewizardlib.exceptions.HWException;
import nl.rgonline.homewizardlib.util.UrlUtil;

import org.apache.commons.lang.BooleanUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a sensor in the HomeWizard system.
 * @author pdegeus
 */
@ToString(callSuper=true, exclude="log")
public class HWSensor extends AbstractHwEntity {

    // Date parser for timestamp: 2013-08-13 21:25:59
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Getter private SensorType type;
    @Getter private String lastEventTime;
    @Getter private boolean on;

    private TreeMap<Date, Boolean> log;
    private boolean logNeedsUpdate = false;

    /**
     * Constructor.
     * @param connection Connection to use.
     * @param id Sensor ID.
     * @param name Sensor name.
     * @param type Sensor type.
     * @param lastEventTime Last sensor event time string.
     * @param isFavorite True if switch is marked as favorite.
     * @param isOn True if sensor is currently 'on'.
     */
    public HWSensor(HWConnection connection, int id, String name, SensorType type, String lastEventTime, boolean isFavorite, boolean isOn) {
        super(connection, id, name, isFavorite);
        this.type = type;
        this.lastEventTime = lastEventTime;
        this.on = isOn;
    }

    /**
     * Sets the last event time string. Invalidates the stored log data (if any) if the time has changed.
     * @param lastEventTime New last event time string.
     */
    public void setLastEventTime(String lastEventTime) {
        if (!Objects.equals(this.lastEventTime, lastEventTime)) {
            logNeedsUpdate = true;
        }
        this.lastEventTime = lastEventTime;
    }

    /**
     * Sets the 'on' flag. Invalidates the stored log data (if any) if the flag has changed.
     * @param on New on status.
     */
    public void setOn(boolean on) {
        if (this.on != on) {
            logNeedsUpdate = true;
        }
        this.on = on;
    }

    /**
     * Retrieves the event log of this sensor. Results are cached until the 'on' status of last event time
     * are changed by the sensor manager. This method is synchronized to prevent simultaneous updates.
     * @return TreeMap of timestamp/status pairs, sorted from oldest to newer.
     * @throws HWException On any update IO or JSON error.
     */
    public TreeMap<Date, Boolean> getLog() throws HWException {
        loadLog();
        return log;
    }

    /**
     * Sets the log data and the flag indicating log data needs updating. Useful for mocking.
     * @param log Log data map.
     * @param logNeedsUpdate Update flag.
     */
    protected void setLog(TreeMap<Date, Boolean> log, boolean logNeedsUpdate) {
        this.log = log;
        this.logNeedsUpdate = logNeedsUpdate;
    }

    /**
     * Returns the last (newest) event from the event log.
     * @return Timestamp/status pair for the last log event.
     * @throws HWException On any log update IO or JSON error.
     */
    public Entry<Date, Boolean> getLastEvent() throws HWException {
        return getLog().lastEntry();
    }

    /**
     * (Re)loads the log data.
     * @throws HWException On any IO or JSON error.
     */
    private synchronized void loadLog() throws HWException {
        if (log == null || logNeedsUpdate) {
            Request request = new Request("/kks/get/", getId(), "/log").setReturnResponse(false);
            JSONObject response = getConnection().request(request);

            // "response": [
            //   { t: "2013-07-16 22:15:04", status: "no"  },
            //   { t: "2013-07-16 22:15:11", status: "yes" },
            // ]

            try {
                JSONArray jsonArr = response.getJSONArray("response");
                int numItems = jsonArr.length();
                log = new TreeMap<>();

                for (int i=0; i < numItems; i++) {
                    JSONObject item = jsonArr.getJSONObject(i);

                    //Parse timestamp
                    Date date;
                    String timestamp = item.getString("t");
                    try {
                        date = dateFormat.parse(timestamp);
                    } catch (ParseException e) {
                        throw new HWException("Could not parse timestamp: " + timestamp, e);
                    }

                    boolean status = BooleanUtils.toBoolean(item.getString("status"));
                    log.put(date, status);
                }
            } catch (JSONException e) {
                throw new HWException("Could not read sensor log data from JSON:\n" + response, e);
            }

            logNeedsUpdate = false;
        }
    }

    @Override
    protected void saveInternal() throws HWException {
        String fav = BooleanUtils.toStringYesNo(isFavorite());

        // /kks/edit/4/<name>/<isFav>
        getConnection().request("/kks/edit/", getId(), "/", UrlUtil.encode(getName()), "/", fav);
    }

}
