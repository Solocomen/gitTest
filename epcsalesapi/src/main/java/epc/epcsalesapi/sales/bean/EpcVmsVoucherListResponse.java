package epc.epcsalesapi.sales.bean;

public class EpcVmsVoucherListResponse {
    
    private EpcVmsVoucherListResponseDetail voucherListRes;
    private String sid;
    
    public EpcVmsVoucherListResponse(){}

    public EpcVmsVoucherListResponseDetail getVoucherListRes() {
        return voucherListRes;
    }

    public void setVoucherListRes(EpcVmsVoucherListResponseDetail voucherListRes) {
        this.voucherListRes = voucherListRes;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

}
