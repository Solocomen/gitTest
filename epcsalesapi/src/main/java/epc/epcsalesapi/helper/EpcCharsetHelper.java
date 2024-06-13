package epc.epcsalesapi.helper;

public class EpcCharsetHelper {
    
    public static String convert(String str, String charsetFrom, String charsetTo) {
        String rtnStr = "";
        try  {
            rtnStr = new String(StringHelper.trim(str).getBytes(charsetFrom),charsetTo);
        } catch (Exception ex)  {
            ex.printStackTrace();
        } finally  {
        }
        return rtnStr;
    } // end of convert()
    

    // convert utf-8 from jsp to big5 for DB
    public static String convertCharset2DB(String str) {
        return convert(str, "big5", "iso-8859-1");
    } // end of convertCharset2DB()
    
    
    // convert big5 in DB for view(jsp)
    public static String convertCharset2View(String str) {
        return convert(str, "iso-8859-1", "big5");
    } // end of convertCharset2View()
}
