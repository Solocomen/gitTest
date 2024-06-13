package epc.epcsalesapi.fes.waiving.bean;

public class EpcWaiveAuthorityFormResult {
    private String result;
    private String errorMessage;
    private String waiveAuthorityFormCode;

    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public String getWaiveAuthorityFormCode() {
        return waiveAuthorityFormCode;
    }
    public void setWaiveAuthorityFormCode(String waiveAuthorityFormCode) {
        this.waiveAuthorityFormCode = waiveAuthorityFormCode;
    }
}
