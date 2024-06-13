package epc.epcsalesapi.sales.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcVmsVoucherCancelRedeemRes {
    private int statusCode;
    private String statusDesc;
    private String smcOrderId;
    
    public EpcVmsVoucherCancelRedeemRes() {
    }
    public int getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public String getStatusDesc() {
        return statusDesc;
    }
    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }
    public String getSmcOrderId() {
        return smcOrderId;
    }
    public void setSmcOrderId(String smcOrderId) {
        this.smcOrderId = smcOrderId;
    }

    
}
