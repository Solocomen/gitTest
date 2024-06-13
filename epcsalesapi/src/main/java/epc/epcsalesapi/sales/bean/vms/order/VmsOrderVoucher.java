package epc.epcsalesapi.sales.bean.vms.order;

import java.math.BigDecimal;

public class VmsOrderVoucher {
    private int orderId;
    private String quoteGuid;
    private String quoteItemGuid;
    private String itemId;
    private String voucherGuid;
    private String voucherMasterId;
    private String voucherCode;
    private BigDecimal voucherAmount;
    private String couponId;
    private String transactionId;
    private String name;
    private String nameZHHK;
    private String description;
    private String descriptionZHHK;
    private String rejectReason;
    private boolean mobilePlanSubscriptionCoupon;
    private String chargeWaiver;
    private String notModelled;
    private String applyLevel;
    private String faceValueType;
    private String category;
    

    public VmsOrderVoucher() {}

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getQuoteItemGuid() {
        return quoteItemGuid;
    }

    public void setQuoteItemGuid(String quoteItemGuid) {
        this.quoteItemGuid = quoteItemGuid;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getVoucherGuid() {
        return voucherGuid;
    }

    public void setVoucherGuid(String voucherGuid) {
        this.voucherGuid = voucherGuid;
    }

    public String getVoucherMasterId() {
        return voucherMasterId;
    }

    public void setVoucherMasterId(String voucherMasterId) {
        this.voucherMasterId = voucherMasterId;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public BigDecimal getVoucherAmount() {
        return voucherAmount;
    }

    public void setVoucherAmount(BigDecimal voucherAmount) {
        this.voucherAmount = voucherAmount;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameZHHK() {
        return nameZHHK;
    }

    public void setNameZHHK(String nameZHHK) {
        this.nameZHHK = nameZHHK;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionZHHK() {
        return descriptionZHHK;
    }

    public void setDescriptionZHHK(String descriptionZHHK) {
        this.descriptionZHHK = descriptionZHHK;
    }

    public String getQuoteGuid() {
        return quoteGuid;
    }

    public void setQuoteGuid(String quoteGuid) {
        this.quoteGuid = quoteGuid;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public boolean isMobilePlanSubscriptionCoupon() {
        return mobilePlanSubscriptionCoupon;
    }

    public void setMobilePlanSubscriptionCoupon(boolean mobilePlanSubscriptionCoupon) {
        this.mobilePlanSubscriptionCoupon = mobilePlanSubscriptionCoupon;
    }

    public String getChargeWaiver() {
        return chargeWaiver;
    }

    public void setChargeWaiver(String chargeWaiver) {
        this.chargeWaiver = chargeWaiver;
    }

    public String getNotModelled() {
        return notModelled;
    }

    public void setNotModelled(String notModelled) {
        this.notModelled = notModelled;
    }

    public String getApplyLevel() {
        return applyLevel;
    }

    public void setApplyLevel(String applyLevel) {
        this.applyLevel = applyLevel;
    }

    public String getFaceValueType() {
        return faceValueType;
    }

    public void setFaceValueType(String faceValueType) {
        this.faceValueType = faceValueType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    
}
