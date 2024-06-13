package epc.epcsalesapi.sales.bean.vms;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VmsVoucher {
    private String itemId;
    private String masterCouponId;
    private String couponId;
    private String transactionId;
    private BigDecimal redeemAmount;
    private String redeemInstanceId;
    private String serialNumber;
    private String rejectReason;
    private boolean isValid;
    private boolean cleanCPQEntity;

    public VmsVoucher() {}

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getMasterCouponId() {
        return masterCouponId;
    }

    public void setMasterCouponId(String masterCouponId) {
        this.masterCouponId = masterCouponId;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getRedeemAmount() {
        return redeemAmount;
    }

    public void setRedeemAmount(BigDecimal redeemAmount) {
        this.redeemAmount = redeemAmount;
    }

    public String getRedeemInstanceId() {
        return redeemInstanceId;
    }

    public void setRedeemInstanceId(String redeemInstanceId) {
        this.redeemInstanceId = redeemInstanceId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public boolean isCleanCPQEntity() {
        return cleanCPQEntity;
    }

    public void setCleanCPQEntity(boolean cleanCPQEntity) {
        this.cleanCPQEntity = cleanCPQEntity;
    }

    
}
