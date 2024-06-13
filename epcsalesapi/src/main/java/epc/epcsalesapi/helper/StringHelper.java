package epc.epcsalesapi.helper;

public class StringHelper {

    /**
     * Trims a string.
     *
     * @param s the string
     * @param defaultValue the value to be returned if the string is null or empty after trim.
     * @return the trimmed string
     */
    public static String trim(String s, String defaultValue) {
        if (s == null || s.trim().equals("")) {
            return defaultValue;
        } else {
            return s.trim();
        }
    }

    /**
     * Trims a string and converts null value to empty string.
     *
     * @param s the string
     * @return the trimmed string
     */
    public static String trim(String s) {
        return trim(s, "");
    }
    
    /**
     * Convert a string from encoding ISO-8859-1 to another encoding
     *
     * @param inputStr string
     *        encoding string
     * @return the converted string
     */
    public static String convertEncodingFromISO88591(String inputStr, String encoding) throws Exception{
        if (!encoding.equals("") && encoding != null) {
            return new String(StringHelper.trim(inputStr).getBytes("ISO-8859-1"), encoding);
        } else {
            throw new Exception("Missing encoding");
        }
    }
    
}
