package epc.epcsalesapi.sales.bean.vms;

import java.math.BigDecimal;

public class VmsDiscount {
    private String couponId;
    private String redeemId;
    private BigDecimal discount;
    private boolean valid;

    public VmsDiscount() {}

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getRedeemId() {
        return redeemId;
    }

    public void setRedeemId(String redeemId) {
        this.redeemId = redeemId;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    
}
