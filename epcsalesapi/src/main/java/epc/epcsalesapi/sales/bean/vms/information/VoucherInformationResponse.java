package epc.epcsalesapi.sales.bean.vms.information;

public class VoucherInformationResponse {
    private String sid;
    private VoucherInformationListRes voucherInformationListRes;
    
    public VoucherInformationResponse() {
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public VoucherInformationListRes getVoucherInformationListRes() {
        return voucherInformationListRes;
    }

    public void setVoucherInformationListRes(VoucherInformationListRes voucherInformationListRes) {
        this.voucherInformationListRes = voucherInformationListRes;
    }

    
}
