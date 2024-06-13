package epc.epcsalesapi.sales.bean;

public class EpcSparseAddResult {
    private String responseCode;
    private String httpStatus;
    private String responseText;
    private EpcSparseAddResultResponseBody responseBody;

    public EpcSparseAddResult() {}

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public EpcSparseAddResultResponseBody getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(EpcSparseAddResultResponseBody responseBody) {
        this.responseBody = responseBody;
    }


}
