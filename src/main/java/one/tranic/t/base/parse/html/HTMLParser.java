package one.tranic.t.base.parse.html;

import java.util.Base64;

public class HTMLParser {
    /**
     * Decodes a Base64 encoded string and removes any HTML tags from the decoded output.
     * <p>
     * Also trims leading whitespace from the resulting string.
     *
     * @param base64EncodedString the Base64 encoded string containing potential HTML content
     * @return the decoded string with HTML tags removed and leading whitespace trimmed
     */
    public static String decodeAndStripHtml(String base64EncodedString) {
        return new String(Base64.getDecoder().decode(base64EncodedString)).replaceAll("<[^>]+>", "").replaceFirst("^\\s+", "");
    }
}
