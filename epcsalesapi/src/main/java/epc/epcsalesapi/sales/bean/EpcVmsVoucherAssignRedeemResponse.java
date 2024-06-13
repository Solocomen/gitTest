package epc.epcsalesapi.sales.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcVmsVoucherAssignRedeemResponse {
    private String sid;
    private EpcVmsVoucherAssignRedeemRes voucherAssignRedeemRes;
    public EpcVmsVoucherAssignRedeemResponse() {
    }
    public String getSid() {
        return sid;
    }
    public void setSid(String sid) {
        this.sid = sid;
    }
    public EpcVmsVoucherAssignRedeemRes getVoucherAssignRedeemRes() {
        return voucherAssignRedeemRes;
    }
    public void setVoucherAssignRedeemRes(EpcVmsVoucherAssignRedeemRes voucherAssignRedeemRes) {
        this.voucherAssignRedeemRes = voucherAssignRedeemRes;
    }

    
}
