package epc.epcsalesapi.sales.bean;

public class EpcValidatePaymentError {
    public static final String E0001 = "E0001"; // System Error
    public static final String E1001 = "E1001"; //
    public static final String E1002 = "E1002";
    public static final String E1003 = "E1003";
    public static final String E1004 = "E1004";
    public static final String E1005 = "E1005";
    public static final String E1006 = "E1006";
    public static final String E1007 = "E1007";
    public static final String E1008 = "E1008";
    public static final String E2001 = "E2001";
    public static final String E2002 = "E2002";
    public static final String E2003 = "E2003";
    public static final String E2004 = "E2004";
    public static final String E2005 = "E2005";
    public static final String E2006 = "E2006";
    public static final String E2007 = "E2007";
    public static final String E2008 = "E2008";
    public static final String E2009 = "E2009";
    public static final String E2010 = "E2010";
    public static final String E2011 = "E2011";
    public static final String E2012 = "E2012";
    public static final String E2013 = "E2013";
    public static final String E2014 = "E2014";

    private String errorCode;
    private String errorMessage;
    private int errorPaymentId;
    
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
    public int getErrorPaymentId() {
        return errorPaymentId;
    }
    public void setErrorPaymentId(int errorPaymentId) {
        this.errorPaymentId = errorPaymentId;
    }
}
