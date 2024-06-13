package epc.epcsalesapi.sales.bean.vms.cust;

import java.math.BigDecimal;

public class VmsCustVoucher {
    private String custId;
    private String custNum;
    private String subrNum;
    private String accountNum;
    private String masterCouponId;
    private String couponId;
    private String serialNo;
    private BigDecimal remainingAmount;
    private String startDate;
    private String endDate;
    private String name;
    private String nameZHHK;
    private String description;
    private String descriptionZHHK;
    private boolean mobilePlanSubscriptionCoupon;
    private String category;

    public VmsCustVoucher() {}

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getCustNum() {
        return custNum;
    }

    public void setCustNum(String custNum) {
        this.custNum = custNum;
    }

    public String getSubrNum() {
        return subrNum;
    }

    public void setSubrNum(String subrNum) {
        this.subrNum = subrNum;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
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

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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

    public boolean isMobilePlanSubscriptionCoupon() {
        return mobilePlanSubscriptionCoupon;
    }

    public void setMobilePlanSubscriptionCoupon(boolean mobilePlanSubscriptionCoupon) {
        this.mobilePlanSubscriptionCoupon = mobilePlanSubscriptionCoupon;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    
}
