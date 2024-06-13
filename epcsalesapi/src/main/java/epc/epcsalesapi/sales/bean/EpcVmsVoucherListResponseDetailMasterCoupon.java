package epc.epcsalesapi.sales.bean;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcVmsVoucherListResponseDetailMasterCoupon {
    private String masterCouponId;
    private String description;
    private String descript_ZH_HK;
    private String name;
    private String name_ZH_HK;
    private BigDecimal validity;
    private BigDecimal faceValue;
    private BigDecimal quota;
    private int displayOrder;
    private String startDate;
    private String endDate;
    private boolean customerEntitlementCoupon;
    private boolean multipleUse;
    private String applyScope;
    private String faceValueType;
    private String chargeWaiver;
    private boolean notModelled;
    private String nature;
    private String category;
    private boolean assignSerialNumber;
    private String status;
    private boolean mobilePlanSubscriptionCoupon;
    private boolean allowSettleMonthlyFee;
    private Boolean sendSMS;
    private Boolean sendEmail;
    private String smsSenderAddress;
    private Boolean emailQRCodeFlag;
    

    private String emailFromAddress;
    private String emailFromDisplayName;
    private BigDecimal minimumSpending;
    private String spendingBy;
    private BigDecimal restrictionRedeem;
    private BigDecimal gracePeriod;
    private Boolean independent;
    private Boolean uniqueRedeem;
    private BigDecimal priority;


    	
    public EpcVmsVoucherListResponseDetailMasterCoupon() {
    }

    public String getMasterCouponId() {
        return masterCouponId;
    }

    public void setMasterCouponId(String masterCouponId) {
        this.masterCouponId = masterCouponId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescript_ZH_HK() {
        return descript_ZH_HK;
    }

    public void setDescript_ZH_HK(String descript_ZH_HK) {
        this.descript_ZH_HK = descript_ZH_HK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName_ZH_HK() {
        return name_ZH_HK;
    }

    public void setName_ZH_HK(String name_ZH_HK) {
        this.name_ZH_HK = name_ZH_HK;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
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

    public boolean isCustomerEntitlementCoupon() {
        return customerEntitlementCoupon;
    }

    public void setCustomerEntitlementCoupon(boolean customerEntitlementCoupon) {
        this.customerEntitlementCoupon = customerEntitlementCoupon;
    }

    public boolean isMultipleUse() {
        return multipleUse;
    }

    public void setMultipleUse(boolean multipleUse) {
        this.multipleUse = multipleUse;
    }

    public String getApplyScope() {
        return applyScope;
    }

    public void setApplyScope(String applyScope) {
        this.applyScope = applyScope;
    }

    public String getFaceValueType() {
        return faceValueType;
    }

    public void setFaceValueType(String faceValueType) {
        this.faceValueType = faceValueType;
    }

    public String getChargeWaiver() {
        return chargeWaiver;
    }

    public void setChargeWaiver(String chargeWaiver) {
        this.chargeWaiver = chargeWaiver;
    }

    public boolean isNotModelled() {
        return notModelled;
    }

    public void setNotModelled(boolean notModelled) {
        this.notModelled = notModelled;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isAssignSerialNumber() {
        return assignSerialNumber;
    }

    public void setAssignSerialNumber(boolean assignSerialNumber) {
        this.assignSerialNumber = assignSerialNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isMobilePlanSubscriptionCoupon() {
        return mobilePlanSubscriptionCoupon;
    }

    public void setMobilePlanSubscriptionCoupon(boolean mobilePlanSubscriptionCoupon) {
        this.mobilePlanSubscriptionCoupon = mobilePlanSubscriptionCoupon;
    }

    public boolean isAllowSettleMonthlyFee() {
        return allowSettleMonthlyFee;
    }

    public void setAllowSettleMonthlyFee(boolean allowSettleMonthlyFee) {
        this.allowSettleMonthlyFee = allowSettleMonthlyFee;
    }

	public BigDecimal getValidity() {
		return validity;
	}

	public void setValidity(BigDecimal validity) {
		this.validity = validity;
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

	public BigDecimal getQuota() {
		return quota;
	}

	public void setQuota(BigDecimal quota) {
		this.quota = quota;
	}

	public String getSmsSenderAddress() {
		return smsSenderAddress;
	}

	public void setSmsSenderAddress(String smsSenderAddress) {
		this.smsSenderAddress = smsSenderAddress;
	}

	public Boolean getEmailQRCodeFlag() {
		return emailQRCodeFlag;
	}

	public void setEmailQRCodeFlag(Boolean emailQRCodeFlag) {
		this.emailQRCodeFlag = emailQRCodeFlag;
	}

	public String getEmailFromAddress() {
		return emailFromAddress;
	}

	public void setEmailFromAddress(String emailFromAddress) {
		this.emailFromAddress = emailFromAddress;
	}

	public String getEmailFromDisplayName() {
		return emailFromDisplayName;
	}

	public void setEmailFromDisplayName(String emailFromDisplayName) {
		this.emailFromDisplayName = emailFromDisplayName;
	}

	public BigDecimal getMinimumSpending() {
		return minimumSpending;
	}

	public void setMinimumSpending(BigDecimal minimumSpending) {
		this.minimumSpending = minimumSpending;
	}

	public String getSpendingBy() {
		return spendingBy;
	}

	public void setSpendingBy(String spendingBy) {
		this.spendingBy = spendingBy;
	}

	public BigDecimal getRestrictionRedeem() {
		return restrictionRedeem;
	}

	public void setRestrictionRedeem(BigDecimal restrictionRedeem) {
		this.restrictionRedeem = restrictionRedeem;
	}

	public BigDecimal getGracePeriod() {
		return gracePeriod;
	}

	public void setGracePeriod(BigDecimal gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	public Boolean getIndependent() {
		return independent;
	}

	public void setIndependent(Boolean independent) {
		this.independent = independent;
	}

	public Boolean getUniqueRedeem() {
		return uniqueRedeem;
	}

	public void setUniqueRedeem(Boolean uniqueRedeem) {
		this.uniqueRedeem = uniqueRedeem;
	}

	public BigDecimal getPriority() {
		return priority;
	}

	public void setPriority(BigDecimal priority) {
		this.priority = priority;
	}

    
}
