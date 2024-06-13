package epc.epcsalesapi.sales.bean;

import java.util.HashMap;
import java.util.List;

public class EpcCreateShortenUrlResult {
    private String status;
    private String errorCode;
    private String errorDetail;
    private List<HashMap<String, String>> url;

    public EpcCreateShortenUrlResult() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public List<HashMap<String, String>> getUrl() {
        return url;
    }

    public void setUrl(List<HashMap<String, String>> url) {
        this.url = url;
    }


    
}
