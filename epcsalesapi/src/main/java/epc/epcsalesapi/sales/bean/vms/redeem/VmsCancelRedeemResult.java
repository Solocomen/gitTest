package epc.epcsalesapi.sales.bean.vms.redeem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VmsCancelRedeemResult {
    private int statusCode;
    private String statusDesc;

    public VmsCancelRedeemResult() {}

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
