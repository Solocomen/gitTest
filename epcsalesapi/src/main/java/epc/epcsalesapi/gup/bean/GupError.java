package epc.epcsalesapi.gup.bean;

import java.util.HashMap;

public class GupError {

    private String errorCode;
    private String errorMessage;
    private static final HashMap<String, GupError> errorMap = new HashMap<String, GupError>();

    public static final GupError ERR00 = new GupError("ERR00", "Problem in getting XML message from transported packet");
    public static final GupError ERR01 = new GupError("ERR01", "Problem in interpreting the XML message");
    public static final GupError ERR02 = new GupError("ERR02", "Problem in processing the request message");
    public static final GupError ERR03 = new GupError("ERR03", "Problem in validating field format");
    public static final GupError ERR04 = new GupError("ERR04", "Problem in validating dependency");
    public static final GupError ERR05 = new GupError("ERR05", "Problem in updating");
    public static final GupError G000 = new GupError("G000", "General exception");
    public static final GupError G001 = new GupError("G001", "Missing key ie Subscriber");
    public static final GupError G002 = new GupError("G002", "Record not found");
    public static final GupError G003 = new GupError("G003", "Record already exists");
    public static final GupError G004 = new GupError("G004", "No new data to update new value equal to old value");
    public static final GupError G005 = new GupError("G005", "Input timestamp expired compare to attribute timestamp");
    public static final GupError F000 = new GupError("F000", "Field not known");
    public static final GupError F001 = new GupError("F001", "Field value cannot be null");
    public static final GupError F002 = new GupError("F002", "Invalid format");
    public static final GupError M000 = new GupError("M000", "Mysql error cannot connect to DB server");
    public static final GupError M001 = new GupError("M001", "Mysql error cannot select to DB");
    public static final GupError M002 = new GupError("M002", "Mysql error cannot select from table");
    public static final GupError M003 = new GupError("M003", "Mysql error cannot insert into table");
    public static final GupError M004 = new GupError("M004", "Mysql error cannot complete transaction");
    public static final GupError M005 = new GupError("M005", "Mysql error cannot update to table");
    public static final GupError M006 = new GupError("M006", "Mysql error cannot delete from table");
    public static final GupError AUTHENTICATION_FAIL = new GupError("AUTHENTICATION_FAIL", "Authentication fail");
    public static final GupError UNKNOWN_ERROR = new GupError("UNKNOWN_ERROR", "Unknown error");
    public static final GupError SYSTEM_ERROR = new GupError("SYSTEM_ERROR", "System error");
    public static final GupError INPUT_ERROR = new GupError("INPUT_ERROR", "Input error");
    public static final GupError DECRYPTION_ERROR = new GupError("DECRYPTION_ERROR", "Decryption error");
    
    
    public GupError(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public static GupError getGupError(String code) {
        return errorMap.get(code);
    }
    
    static {
        errorMap.put(ERR00.getErrorCode(), ERR00);
        errorMap.put(ERR01.getErrorCode(), ERR01);
        errorMap.put(ERR02.getErrorCode(), ERR02);
        errorMap.put(ERR03.getErrorCode(), ERR03);
        errorMap.put(ERR04.getErrorCode(), ERR04);
        errorMap.put(ERR05.getErrorCode(), ERR05);
        errorMap.put(G000.getErrorCode(), G000);
        errorMap.put(G001.getErrorCode(), G001);
        errorMap.put(G002.getErrorCode(), G002);
        errorMap.put(G003.getErrorCode(), G003);
        errorMap.put(G004.getErrorCode(), G004);
        errorMap.put(G005.getErrorCode(), G005);
        errorMap.put(F000.getErrorCode(), F000);
        errorMap.put(F001.getErrorCode(), F001);
        errorMap.put(F002.getErrorCode(), F002);
        errorMap.put(M000.getErrorCode(), M000);
        errorMap.put(M001.getErrorCode(), M001);
        errorMap.put(M002.getErrorCode(), M002);
        errorMap.put(M003.getErrorCode(), M003);
        errorMap.put(M004.getErrorCode(), M004);
        errorMap.put(M005.getErrorCode(), M005);
        errorMap.put(M006.getErrorCode(), M006);
        errorMap.put(AUTHENTICATION_FAIL.getErrorCode(), AUTHENTICATION_FAIL);
    }
}
