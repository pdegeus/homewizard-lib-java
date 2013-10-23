package nl.rgonline.homewizardlib.connection;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * HTTP request response cache.
 * @author pdegeus
 */
public class ResponseCache {

    private Map<String, CacheItem> cacheData = new HashMap<>();

    /**
     * Add data for the given URL.
     * @param url URL to save under.
     * @param data Data to save.
     */
    public void add(String url, String data) {
        cacheData.put(url, new CacheItem(data, System.currentTimeMillis()));
    }

    /**
     * Retrieve cached data for the given URL, without checking validity.
     * To verify if data is still valid, use {@link #hasResponse(String, long)}.
     * @param url URL to get data for.
     * @return Cached data, or null if not found.
     */
    public String get(String url) {
        if (!cacheData.containsKey(url)) {
            return null;
        }
        return cacheData.get(url).getData();
    }

    /**
     * Indicates whether a still valid, cached response is available for the given URL.
     * @param url URL to check.
     * @param maxAge Maximum age.
     * @return True if cached data exists and is not expired.
     */
    public boolean hasResponse(String url, long maxAge) {
        if (!cacheData.containsKey(url)) {
            return false;
        }

        CacheItem item = cacheData.get(url);
        return isValid(item, maxAge);
    }

    private boolean isValid(CacheItem item, long maxAge) {
        long age = System.currentTimeMillis() - item.getTimestamp();
        return (age <= maxAge);
    }

    @Data
    @AllArgsConstructor
    private static class CacheItem {
        private String data;
        private long timestamp;
    }

}
