package epc.epcsalesapi.sales.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcVmsVoucherAssignRedeemRes {
    private int statusCode;
    private String statusDesc;
    
    public EpcVmsVoucherAssignRedeemRes() {
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

    
}
