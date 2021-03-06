package nl.rgonline.homewizardlib.thermo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nl.rgonline.homewizardlib.AbstractHwEntity;
import nl.rgonline.homewizardlib.connection.HWConnection;
import nl.rgonline.homewizardlib.config.HWConfig;
import nl.rgonline.homewizardlib.connection.Request;
import nl.rgonline.homewizardlib.exceptions.HWException;
import nl.rgonline.homewizardlib.util.UrlUtil;

import org.apache.commons.lang.BooleanUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a thermometer in the HomeWizard system.
 * @author pdegeus
 */
@EqualsAndHashCode(exclude={"temperature", "humidity", "humidityCache", "temperatureCache"}, callSuper = true)
@ToString(callSuper = true)
public class HWThermometer extends AbstractHwEntity {

    // Date parser for timestamp: 2013-08-13 21:25
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Getter @Setter
    private int channel;

    @Getter @Setter(AccessLevel.PROTECTED)
    private Integer humidity;
    
    @Getter @Setter(AccessLevel.PROTECTED)
    private Double temperature;

    @Getter @Setter(AccessLevel.PROTECTED)
    private Double minTemp;
    @Getter @Setter(AccessLevel.PROTECTED) 
    private String minTempTime;
    @Getter @Setter(AccessLevel.PROTECTED)
    private Double maxTemp;
    @Getter @Setter(AccessLevel.PROTECTED)
    private String maxTempTime;

    @Getter @Setter(AccessLevel.PROTECTED)
    private Integer minHumidity;
    @Getter @Setter(AccessLevel.PROTECTED)
    private String minHumidityTime;
    @Getter @Setter(AccessLevel.PROTECTED)
    private Integer maxHumidity;
    @Getter @Setter(AccessLevel.PROTECTED)
    private String maxHumidityTime;

    private Map<TimeSpan, TimeValueCache<Integer>> humidityCache = new HashMap<>();
    private Map<TimeSpan, TimeValueCache<Double>> temperatureCache = new HashMap<>();

    /**
     * Constructor.
     * @param connection Connection to use.
     * @param id Thermometer ID.
     * @param name Thermometer name.
     * @param isFavorite True if thermometer is marked as favorite.
     * @param channel Communication channel.
     */
    protected HWThermometer(HWConnection connection, int id, String name, boolean isFavorite, int channel) {
        super(connection, id, name, isFavorite);
        this.channel = channel;
    }

    /**
     * Retrieves the historic humidity data for the given time span. The returned data is cached, per timestamp,
     * according to configuration.
     * @param timeSpan Time span to retrieve data for.
     * @return List of timestamp/value data points.
     * @throws HWException On any data retrieval or parse error.
     */
    public List<TimeValue<Integer>> getHumidityHistory(TimeSpan timeSpan) throws HWException {
        loadData(timeSpan);
        return humidityCache.get(timeSpan).getData();
    }

    /**
     * Retrieves the historic temperature data for the given time span. The returned data is cached, per timestamp,
     * according to configuration.
     * @param timeSpan Time span to retrieve data for.
     * @return List of timestamp/value data points.
     * @throws HWException On any data retrieval or parse error.
     */
    public List<TimeValue<Double>> getTemperatureHistory(TimeSpan timeSpan) throws HWException {
        loadData(timeSpan);
        return temperatureCache.get(timeSpan).getData();
    }

    private synchronized void loadData(TimeSpan timeSpan) throws HWException {
        if (needsUpdate(timeSpan)) {
            Request request = new Request("/te/graph/", getId(), "/", timeSpan.getApiString()).setReturnResponse(false);
            JSONObject response = getConnection().request(request);

            // "response": [
            //   { "t": "2013-07-16 12:00", "te+": 26.9, "te-": 21.1, "hu+": 60, "hu-": 55},
            //   { "t": "2013-07-17 12:00", "te+": 36.9, "te-": 17.8, "hu+": 74, "hu-": 41}
            // ]
            //                     - OR -
            // "response": [
            //   { "t": "2013-08-13 00:10", "te": 15.5, "hu": 66},
            //   { "t": "2013-08-13 00:25", "te": 15.2, "hu": 66}
            // ]

            try {
                JSONArray jsonArr = response.getJSONArray("response");
                int numItems = jsonArr.length();

                List<TimeValue<Integer>> huList = new ArrayList<>(numItems);
                List<TimeValue<Double>> teList = new ArrayList<>(numItems);

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

                    Double te, teMax = null;
                    Integer hu, huMax = null;

                    //Single or min/max value?
                    if (item.has("te")) {
                        te = item.getDouble("te");
                        hu = item.getInt("hu");
                    } else {
                        te = item.getDouble("te-");
                        hu = item.getInt("hu-");
                        teMax = item.getDouble("te+");
                        huMax = item.getInt("hu+");
                    }

                    teList.add(new TimeValue<>(date, te, teMax));
                    huList.add(new TimeValue<>(date, hu, huMax));
                }

                humidityCache.get(timeSpan).setData(huList);
                temperatureCache.get(timeSpan).setData(teList);
            } catch (JSONException e) {
                throw new HWException("Could not read graph data from JSON:\n" + response, e);
            }
        }
    }

    private boolean needsUpdate(TimeSpan timeSpan) {
        TimeValueCache<Integer> cache = humidityCache.get(timeSpan);
        if (cache == null) {
            humidityCache.put(timeSpan, new TimeValueCache<Integer>());
            temperatureCache.put(timeSpan, new TimeValueCache<Double>());
            return true;
        }

        long expire;
        if (timeSpan == TimeSpan.DAY) {
            expire = HWConfig.THERMO_GRAPH_UPDATE_INTERVAL_DAY.getValue();
        } else {
            expire = HWConfig.THERMO_GRAPH_UPDATE_INTERVAL_OTHER.getValue();
        }

        Long cacheLastUp = cache.getLastUpdate();
        return (cacheLastUp == null || (System.currentTimeMillis() - cacheLastUp > expire));
    }

    @Override
    protected void saveInternal() throws HWException {
        String fav = BooleanUtils.toStringYesNo(isFavorite());

        // /te/edit/<id>/<name>/<channel>/<isFav>
        getConnection().request("/te/edit/", getId(), "/", UrlUtil.encode(getName()), "/", getChannel(), "/", fav);
    }

}
