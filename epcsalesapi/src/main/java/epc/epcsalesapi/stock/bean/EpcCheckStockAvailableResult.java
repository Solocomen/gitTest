package epc.epcsalesapi.stock.bean;

import java.util.ArrayList;

public class EpcCheckStockAvailableResult {
    private int statusCode;
    private String statusMessage;
    private ArrayList<EpcCheckStockAvailableResultDetail> result;
    
    public EpcCheckStockAvailableResult() {
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

    public ArrayList<EpcCheckStockAvailableResultDetail> getResult() {
        return result;
    }

    public void setResult(ArrayList<EpcCheckStockAvailableResultDetail> result) {
        this.result = result;
    }

    
}
