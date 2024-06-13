package epc.epcsalesapi.sales.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcVmsVoucherCancelRedeemResponse {
    private String sid;
    private EpcVmsVoucherCancelRedeemRes voucherCancelRes;
    public EpcVmsVoucherCancelRedeemResponse() {
    }
    public String getSid() {
        return sid;
    }
    public void setSid(String sid) {
        this.sid = sid;
    }
    public EpcVmsVoucherCancelRedeemRes getVoucherCancelRes() {
        return voucherCancelRes;
    }
    public void setVoucherCancelRes(EpcVmsVoucherCancelRedeemRes voucherCancelRes) {
        this.voucherCancelRes = voucherCancelRes;
    }

}
