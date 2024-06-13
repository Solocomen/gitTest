package epc.epcsalesapi.sales.bean.vms;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VmsVoucher2 {
    private String customerId;
    private String custNum;
    private String msisdn;
    private String acctNum;
    private String quoteId;
    private String itemId;
    private String masterCouponId;
    private String couponId;
    private String serialNumber;
    private BigDecimal redeemAmount;
    private BigDecimal lumpSum;
    private String transactionId;
    private boolean isValid;
    private String rejectReason;
    private String instanceId;
    private String smcOrderId;
    private String smcOrderReference;
    private String desc;
    private String descChi;

    public VmsVoucher2() {}

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustNum() {
        return custNum;
    }

    public void setCustNum(String custNum) {
        this.custNum = custNum;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getAcctNum() {
        return acctNum;
    }

    public void setAcctNum(String acctNum) {
        this.acctNum = acctNum;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public BigDecimal getRedeemAmount() {
        return redeemAmount;
    }

    public void setRedeemAmount(BigDecimal redeemAmount) {
        this.redeemAmount = redeemAmount;
    }

    public BigDecimal getLumpSum() {
        return lumpSum;
    }

    public void setLumpSum(BigDecimal lumpSum) {
        this.lumpSum = lumpSum;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getSmcOrderId() {
        return smcOrderId;
    }

    public void setSmcOrderId(String smcOrderId) {
        this.smcOrderId = smcOrderId;
    }

    public String getSmcOrderReference() {
        return smcOrderReference;
    }

    public void setSmcOrderReference(String smcOrderReference) {
        this.smcOrderReference = smcOrderReference;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDescChi() {
        return descChi;
    }

    public void setDescChi(String descChi) {
        this.descChi = descChi;
    }

    
    
}
