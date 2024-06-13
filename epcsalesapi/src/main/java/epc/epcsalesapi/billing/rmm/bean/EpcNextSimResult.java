package epc.epcsalesapi.billing.rmm.bean;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class EpcNextSimResult {
    
    private String sim;
    private String imsi;
    private String status;
    private String message;
    private int errorCode;
    private String errorMessage;
    

    public EpcNextSimResult() {}

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getSim() {
        return sim;
    }
    
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImsi() {
        return imsi;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    
}

