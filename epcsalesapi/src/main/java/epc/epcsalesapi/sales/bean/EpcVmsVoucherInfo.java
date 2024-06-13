package epc.epcsalesapi.sales.bean;

import java.math.BigDecimal;

public class EpcVmsVoucherInfo {
    
    private String masterVoucherId;
    private BigDecimal remainingAmount;
    private String isFound;
    private String isFrom;
    private String couponId; // record pk (which coupon marked to customer)
    private String voucherItemId; // item instance id
    private String voucherTransactionId; // transaction id
    private String quoteGuid;
    private String quoteItemGuid;
    private String scope;
    private boolean multipleUse;
    private String faceValueType;
    private String name;
    private String nameZHHK;
    private String description;
    private String descriptionZHHK;
    private String chargeWaiver;
    private String notModelled;
    private String mobilePlanSubscriptionCoupon;

    private String serialNo;
    private BigDecimal validity;
    private String startDate;
    private String endDate;
    private Boolean sendSMS;
    private Boolean sendEmail;
    private String category;
    
    public EpcVmsVoucherInfo() {}

    public String getMasterVoucherId() {
        return masterVoucherId;
    }

    public void setMasterVoucherId(String masterVoucherId) {
        this.masterVoucherId = masterVoucherId;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public String getIsFound() {
        return isFound;
    }

    public void setIsFound(String isFound) {
        this.isFound = isFound;
    }

    public String getIsFrom() {
        return isFrom;
    }

    public void setIsFrom(String isFrom) {
        this.isFrom = isFrom;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getVoucherItemId() {
        return voucherItemId;
    }

    public void setVoucherItemId(String voucherItemId) {
        this.voucherItemId = voucherItemId;
    }

    public String getVoucherTransactionId() {
        return voucherTransactionId;
    }

    public void setVoucherTransactionId(String voucherTransactionId) {
        this.voucherTransactionId = voucherTransactionId;
    }

    public String getQuoteGuid() {
        return quoteGuid;
    }

    public void setQuoteGuid(String quoteGuid) {
        this.quoteGuid = quoteGuid;
    }

    public String getQuoteItemGuid() {
        return quoteItemGuid;
    }

    public void setQuoteItemGuid(String quoteItemGuid) {
        this.quoteItemGuid = quoteItemGuid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isMultipleUse() {
        return multipleUse;
    }

    public void setMultipleUse(boolean multipleUse) {
        this.multipleUse = multipleUse;
    }

    public String getFaceValueType() {
        return faceValueType;
    }

    public void setFaceValueType(String faceValueType) {
        this.faceValueType = faceValueType;
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

    public String getMobilePlanSubscriptionCoupon() {
        return mobilePlanSubscriptionCoupon;
    }

    public void setMobilePlanSubscriptionCoupon(String mobilePlanSubscriptionCoupon) {
        this.mobilePlanSubscriptionCoupon = mobilePlanSubscriptionCoupon;
    }

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public BigDecimal getValidity() {
		return validity;
	}

	public void setValidity(BigDecimal validity) {
		this.validity = validity;
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

	public Boolean getSendSMS() {
		return sendSMS;
	}

	public void setSendSMS(Boolean sendSMS) {
		this.sendSMS = sendSMS;
	}

	public Boolean getSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(Boolean sendEmail) {
		this.sendEmail = sendEmail;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}


    

}
