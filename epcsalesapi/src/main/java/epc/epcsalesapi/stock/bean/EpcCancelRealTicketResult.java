package epc.epcsalesapi.stock.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcCancelRealTicketResult {
    private String responseTime;
    private List<EpcCancelRealTicketDetail> results;
    private int statusCode;
    private String statusMessage;

    public EpcCancelRealTicketResult() {}

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public List<EpcCancelRealTicketDetail> getResults() {
        return results;
    }

    public void setResults(List<EpcCancelRealTicketDetail> results) {
        this.results = results;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    

}
