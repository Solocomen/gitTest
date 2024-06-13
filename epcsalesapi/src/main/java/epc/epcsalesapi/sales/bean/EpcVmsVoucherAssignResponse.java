package epc.epcsalesapi.sales.bean;

public class EpcVmsVoucherAssignResponse {
    private String sid;
    private EpcVmsVoucherAssignRes voucherAssignRes;

    public EpcVmsVoucherAssignResponse() {}

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public EpcVmsVoucherAssignRes getVoucherAssignRes() {
        return voucherAssignRes;
    }

    public void setVoucherAssignRes(EpcVmsVoucherAssignRes voucherAssignRes) {
        this.voucherAssignRes = voucherAssignRes;
    }

    
}
