/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean.vms.information;

/**
 *
 * @author DannyChan
 */
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashMap;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VmsVoucherInformation implements Serializable {
	private String masterCouponId;
	private String nameEng;
	private String nameChi;
	private String descriptionEng;
	private String descriptionChi;
	private String status;
	private String startDate;
	private String endDate;
	private int validity;
	private int amount;
	private int quota;
	private String nature;
	private String category;
	private boolean sendSMS;
	private String smsSenderAddress;
	private boolean sendEmail;
	private boolean emailQRCodeFlag;
	private String emailFromAddress;
	private String emailFromDisplayName;
	private boolean customerEntitlementCoupon;
	private boolean assignSerialNumber;
	private BigDecimal minimumSpending;
	private int displayOrder;
	private HashMap<String, Object> orderChannel;
	private String faceValueType;
	private String applyScope;
	private String spendingBy;
	private BigDecimal restrictionRedeem;
	private int gracePeriod;
	private String chargeWaiver;
	private boolean notModelled;
	private boolean independent ;
	private boolean multipleUse;
	private boolean allowSettleMonthlyFee;
	private BigDecimal faceValue;
	private boolean mobilePlanSubscriptionCoupon;


	/**
	 * @return the masterCouponId
	 */
	public String getMasterCouponId() {
		return masterCouponId;
	}

	/**
	 * @param masterCouponId the masterCouponId to set
	 */
	public void setMasterCouponId(String masterCouponId) {
		this.masterCouponId = masterCouponId;
	}

	/**
	 * @return the nameEng
	 */
	public String getNameEng() {
		return nameEng;
	}

	/**
	 * @param nameEng the nameEng to set
	 */
	public void setNameEng(String nameEng) {
		this.nameEng = nameEng;
	}

	/**
	 * @return the nameChi
	 */
	public String getNameChi() {
		return nameChi;
	}

	/**
	 * @param nameChi the nameChi to set
	 */
	public void setNameChi(String nameChi) {
		this.nameChi = nameChi;
	}

	/**
	 * @return the descriptionEng
	 */
	public String getDescriptionEng() {
		return descriptionEng;
	}

	/**
	 * @param descriptionEng the descriptionEng to set
	 */
	public void setDescriptionEng(String descriptionEng) {
		this.descriptionEng = descriptionEng;
	}

	/**
	 * @return the descriptionChi
	 */
	public String getDescriptionChi() {
		return descriptionChi;
	}

	/**
	 * @param descriptionChi the descriptionChi to set
	 */
	public void setDescriptionChi(String descriptionChi) {
		this.descriptionChi = descriptionChi;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the validity
	 */
	public int getValidity() {
		return validity;
	}

	/**
	 * @param validity the validity to set
	 */
	public void setValidity(int validity) {
		this.validity = validity;
	}

	/**
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * @return the quota
	 */
	public int getQuota() {
		return quota;
	}

	/**
	 * @param quota the quota to set
	 */
	public void setQuota(int quota) {
		this.quota = quota;
	}

	/**
	 * @return the nature
	 */
	public String getNature() {
		return nature;
	}

	/**
	 * @param nature the nature to set
	 */
	public void setNature(String nature) {
		this.nature = nature;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the sendSMS
	 */
	public boolean isSendSMS() {
		return sendSMS;
	}

	/**
	 * @param sendSMS the sendSMS to set
	 */
	public void setSendSMS(boolean sendSMS) {
		this.sendSMS = sendSMS;
	}

	/**
	 * @return the smsSenderAddress
	 */
	public String getSmsSenderAddress() {
		return smsSenderAddress;
	}

	/**
	 * @param smsSenderAddress the smsSenderAddress to set
	 */
	public void setSmsSenderAddress(String smsSenderAddress) {
		this.smsSenderAddress = smsSenderAddress;
	}

	/**
	 * @return the sendEmail
	 */
	public boolean isSendEmail() {
		return sendEmail;
	}

	/**
	 * @param sendEmail the sendEmail to set
	 */
	public void setSendEmail(boolean sendEmail) {
		this.sendEmail = sendEmail;
	}

	/**
	 * @return the emailQRCodeFlag
	 */
	public boolean isEmailQRCodeFlag() {
		return emailQRCodeFlag;
	}

	/**
	 * @param emailQRCodeFlag the emailQRCodeFlag to set
	 */
	public void setEmailQRCodeFlag(boolean emailQRCodeFlag) {
		this.emailQRCodeFlag = emailQRCodeFlag;
	}

	/**
	 * @return the emailFromAddress
	 */
	public String getEmailFromAddress() {
		return emailFromAddress;
	}

	/**
	 * @param emailFromAddress the emailFromAddress to set
	 */
	public void setEmailFromAddress(String emailFromAddress) {
		this.emailFromAddress = emailFromAddress;
	}

	/**
	 * @return the emailFromDisplayName
	 */
	public String getEmailFromDisplayName() {
		return emailFromDisplayName;
	}

	/**
	 * @param emailFromDisplayName the emailFromDisplayName to set
	 */
	public void setEmailFromDisplayName(String emailFromDisplayName) {
		this.emailFromDisplayName = emailFromDisplayName;
	}

	/**
	 * @return the customerEntitlementCoupon
	 */
	public boolean isCustomerEntitlementCoupon() {
		return customerEntitlementCoupon;
	}

	/**
	 * @param customerEntitlementCoupon the customerEntitlementCoupon to set
	 */
	public void setCustomerEntitlementCoupon(boolean customerEntitlementCoupon) {
		this.customerEntitlementCoupon = customerEntitlementCoupon;
	}

	/**
	 * @return the assignSerialNumber
	 */
	public boolean isAssignSerialNumber() {
		return assignSerialNumber;
	}

	/**
	 * @param assignSerialNumber the assignSerialNumber to set
	 */
	public void setAssignSerialNumber(boolean assignSerialNumber) {
		this.assignSerialNumber = assignSerialNumber;
	}

	/**
	 * @return the minimumSpending
	 */
	public BigDecimal getMinimumSpending() {
		return minimumSpending;
	}

	/**
	 * @param minimumSpending the minimumSpending to set
	 */
	public void setMinimumSpending(BigDecimal minimumSpending) {
		this.minimumSpending = minimumSpending;
	}

	/**
	 * @return the displayOrder
	 */
	public int getDisplayOrder() {
		return displayOrder;
	}

	/**
	 * @param displayOrder the displayOrder to set
	 */
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	/**
	 * @return the orderChannel
	 */
	public HashMap<String, Object> getOrderChannel() {
		return orderChannel;
	}

	/**
	 * @param orderChannel the orderChannel to set
	 */
	public void setOrderChannel(HashMap<String, Object> orderChannel) {
		this.orderChannel = orderChannel;
	}

	/**
	 * @return the faceValueType
	 */
	public String getFaceValueType() {
		return faceValueType;
	}

	/**
	 * @param faceValueType the faceValueType to set
	 */
	public void setFaceValueType(String faceValueType) {
		this.faceValueType = faceValueType;
	}

	/**
	 * @return the applyScope
	 */
	public String getApplyScope() {
		return applyScope;
	}

	/**
	 * @param applyScope the applyScope to set
	 */
	public void setApplyScope(String applyScope) {
		this.applyScope = applyScope;
	}

	/**
	 * @return the spendingBy
	 */
	public String getSpendingBy() {
		return spendingBy;
	}

	/**
	 * @param spendingBy the spendingBy to set
	 */
	public void setSpendingBy(String spendingBy) {
		this.spendingBy = spendingBy;
	}

	/**
	 * @return the restrictionRedeem
	 */
	public BigDecimal getRestrictionRedeem() {
		return restrictionRedeem;
	}

	/**
	 * @param restrictionRedeem the restrictionRedeem to set
	 */
	public void setRestrictionRedeem(BigDecimal restrictionRedeem) {
		this.restrictionRedeem = restrictionRedeem;
	}

	/**
	 * @return the gracePeriod
	 */
	public int getGracePeriod() {
		return gracePeriod;
	}

	/**
	 * @param gracePeriod the gracePeriod to set
	 */
	public void setGracePeriod(int gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	/**
	 * @return the chargeWaiver
	 */
	public String getChargeWaiver() {
		return chargeWaiver;
	}

	/**
	 * @param chargeWaiver the chargeWaiver to set
	 */
	public void setChargeWaiver(String chargeWaiver) {
		this.chargeWaiver = chargeWaiver;
	}

	/**
	 * @return the notModelled
	 */
	public boolean isNotModelled() {
		return notModelled;
	}

	/**
	 * @param notModelled the notModelled to set
	 */
	public void setNotModelled(boolean notModelled) {
		this.notModelled = notModelled;
	}

	/**
	 * @return the independent
	 */
	public boolean isIndependent() {
		return independent;
	}

	/**
	 * @param independent the independent to set
	 */
	public void setIndependent(boolean independent) {
		this.independent = independent;
	}

	/**
	 * @return the multipleUse
	 */
	public boolean isMultipleUse() {
		return multipleUse;
	}

	/**
	 * @param multipleUse the multipleUse to set
	 */
	public void setMultipleUse(boolean multipleUse) {
		this.multipleUse = multipleUse;
	}

	/**
	 * @return the allowSettleMonthlyFee
	 */
	public boolean isAllowSettleMonthlyFee() {
		return allowSettleMonthlyFee;
	}

	/**
	 * @param allowSettleMonthlyFee the allowSettleMonthlyFee to set
	 */
	public void setAllowSettleMonthlyFee(boolean allowSettleMonthlyFee) {
		this.allowSettleMonthlyFee = allowSettleMonthlyFee;
	}

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public boolean isMobilePlanSubscriptionCoupon() {
        return mobilePlanSubscriptionCoupon;
    }

    public void setMobilePlanSubscriptionCoupon(boolean mobilePlanSubscriptionCoupon) {
        this.mobilePlanSubscriptionCoupon = mobilePlanSubscriptionCoupon;
    }

}
