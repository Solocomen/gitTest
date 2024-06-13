package epc.epcsalesapi.sales.bean;

import java.math.BigDecimal;

public class EpcCharge {
    private String chargeCode;
    private String chargeDesc;
    private String chargeDescChi;
    private BigDecimal chargeAmount;
    private String caseId;
    private String msisdn;
    private String itemId;
    private String parentItemId;
    private int seqId;
    private String needToPay;
    private String paid;
    private String waiveFormCode;
    private String discountFormCode;
    private BigDecimal originChargeAmount;
    private String discounted;
    private String waived;
    private String approveBy;
    private String waiveReason;
    private String itemCode;
    private BigDecimal catalogRRP;
    private String catalogItemDesc;
    private String voucherMasterId;

    public EpcCharge() {
    }

    public String getChargeCode() {
        return chargeCode;
    }

    public void setChargeCode(String chargeCode) {
        this.chargeCode = chargeCode;
    }

    public String getChargeDesc() {
        return chargeDesc;
    }

    public void setChargeDesc(String chargeDesc) {
        this.chargeDesc = chargeDesc;
    }

    public String getChargeDescChi() {
        return chargeDescChi;
    }

    public void setChargeDescChi(String chargeDescChi) {
        this.chargeDescChi = chargeDescChi;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getParentItemId() {
        return parentItemId;
    }

    public void setParentItemId(String parentItemId) {
        this.parentItemId = parentItemId;
    }

    public int getSeqId() {
        return seqId;
    }

    public void setSeqId(int seqId) {
        this.seqId = seqId;
    }

    public String getNeedToPay() {
        return needToPay;
    }

    public void setNeedToPay(String needToPay) {
        this.needToPay = needToPay;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getWaiveFormCode() {
        return waiveFormCode;
    }

    public void setWaiveFormCode(String waiveFormCode) {
        this.waiveFormCode = waiveFormCode;
    }

    public String getDiscountFormCode() {
        return discountFormCode;
    }

    public void setDiscountFormCode(String discountFormCode) {
        this.discountFormCode = discountFormCode;
    }

    public BigDecimal getOriginChargeAmount() {
        return originChargeAmount;
    }

    public void setOriginChargeAmount(BigDecimal originChargeAmount) {
        this.originChargeAmount = originChargeAmount;
    }

    public String getDiscounted() {
        return discounted;
    }

    public void setDiscounted(String discounted) {
        this.discounted = discounted;
    }

    public String getWaived() {
        return waived;
    }

    public void setWaived(String waived) {
        this.waived = waived;
    }

    public String getApproveBy() {
        return approveBy;
    }

    public void setApproveBy(String approveBy) {
        this.approveBy = approveBy;
    }

    public String getWaiveReason() {
        return waiveReason;
    }

    public void setWaiveReason(String waiveReason) {
        this.waiveReason = waiveReason;
    }

    
    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    /**
     * @return the catalogRRP
     */
    public BigDecimal getCatalogRRP() {
        return catalogRRP;
    }

    /**
     * @param catalogRRP the catalogRRP to set
     */
    public void setCatalogRRP(BigDecimal catalogRRP) {
        this.catalogRRP = catalogRRP;
    }

    public String getCatalogItemDesc() {
        return catalogItemDesc;
    }

    public void setCatalogItemDesc(String catalogItemDesc) {
        this.catalogItemDesc = catalogItemDesc;
    }

    public String getVoucherMasterId() {
        return voucherMasterId;
    }

    public void setVoucherMasterId(String voucherMasterId) {
        this.voucherMasterId = voucherMasterId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof EpcCharge)) {
            return false;
        }

        EpcCharge charge = (EpcCharge) o;

        try {
        return charge.getChargeCode().equals(this.chargeCode) &&
               charge.getChargeAmount().compareTo(this.chargeAmount) == 0 &&
               charge.getSeqId() == this.seqId;
        } catch (Exception e) {
            return false;
        }
    }

}
