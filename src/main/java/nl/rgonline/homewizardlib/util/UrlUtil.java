package nl.rgonline.homewizardlib.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import nl.rgonline.homewizardlib.exceptions.HWException;

/**
 * URL utility class.
 * @author pdegeus
 */
public class UrlUtil {

    /**
     * URL-encode a text. Simple wrapper mapping UnsupportedEncodingException to HWException.
     * @param text Text to encode.
     * @return Encoded text.
     * @throws HWException On encoding error.
     */
    public static String encode(String text) throws HWException {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new HWException("Could not URL-encode text", e);
        }
    }

    private UrlUtil() {
    }

}
