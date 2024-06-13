package epc.epcsalesapi.sales.bean.vms.information;

import java.util.List;

public class VoucherInformationReq {
    private List<VoucherInformationReqCoupon> coupon;
    private String status;
    private String category;

    public VoucherInformationReq() {
    }

    public List<VoucherInformationReqCoupon> getCoupon() {
        return coupon;
    }

    public void setCoupon(List<VoucherInformationReqCoupon> coupon) {
        this.coupon = coupon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    
}
