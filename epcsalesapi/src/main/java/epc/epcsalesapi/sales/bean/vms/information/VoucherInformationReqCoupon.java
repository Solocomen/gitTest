package epc.epcsalesapi.sales.bean.vms.information;

public class VoucherInformationReqCoupon {
    private String masterCouponId;

    public VoucherInformationReqCoupon() {
    }

    public VoucherInformationReqCoupon(String masterCouponId) {
        this.masterCouponId = masterCouponId;
    }

    public String getMasterCouponId() {
        return masterCouponId;
    }

    public void setMasterCouponId(String masterCouponId) {
        this.masterCouponId = masterCouponId;
    }

    
}
