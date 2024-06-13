package epc.epcsalesapi.login.bean;

public class EpcValidateDwpResult {
    private boolean valid;
    private String message;
    private String resultCode;

    public EpcValidateDwpResult() {
        super();
    }
    public boolean isValid() {
        return valid;
    }
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getResultCode() {
        return resultCode;
    }
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

}
