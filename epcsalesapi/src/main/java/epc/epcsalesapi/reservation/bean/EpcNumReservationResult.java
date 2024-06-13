package epc.epcsalesapi.reservation.bean;

public class EpcNumReservationResult {
    
    private String message;
    private int errorCode;
    private String errorMessage;
    

    public EpcNumReservationResult() {}

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

