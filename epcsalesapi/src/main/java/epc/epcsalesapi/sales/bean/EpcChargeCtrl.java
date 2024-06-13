package epc.epcsalesapi.sales.bean;

public class EpcChargeCtrl {
    
    private int recId;
    private String chargeGuid;
    private String chargeCpqDesc;
    private String chargeCode;
    private String configDesc;
    private boolean displayProductDesc;
    private boolean displayConfigDesc;
    private boolean displayChargeDesc;
    private boolean displayParentDesc;
    private String discountFormCode;
    private String itemCode;
    private String checkParentItemCode;
    private String checkParentItemDesc;
    private String catalogRrp;
    private String paymentCode;

    @Override
    public String toString() {
        return "EpcChargeCtrl [recId=" + recId + ", chargeGuid=" + chargeGuid + ", chargeCpqDesc=" + chargeCpqDesc
                + ", chargeCode=" + chargeCode + ", configDesc=" + configDesc + ", displayProductDesc="
                + displayProductDesc + ", displayConfigDesc=" + displayConfigDesc + ", displayChargeDesc="
                + displayChargeDesc + ", displayParentDesc=" + displayParentDesc + ", discountFormCode="
                + discountFormCode + ", itemCode=" + itemCode + ", checkParentItemCode=" + checkParentItemCode
                + ", checkParentItemDesc=" + checkParentItemDesc + ", catalogRrp=" + catalogRrp + ", paymentCode="
                + paymentCode + "]";
    }

    public EpcChargeCtrl() {
        super();
    }

    public int getRecId() {
        return recId;
    }

    public void setRecId(int recId) {
        this.recId = recId;
    }

    public String getChargeGuid() {
        return chargeGuid;
    }

    public void setChargeGuid(String chargeGuid) {
        this.chargeGuid = chargeGuid;
    }

    public String getChargeCpqDesc() {
        return chargeCpqDesc;
    }

    public void setChargeCpqDesc(String chargeCpqDesc) {
        this.chargeCpqDesc = chargeCpqDesc;
    }

    public String getChargeCode() {
        return chargeCode;
    }

    public void setChargeCode(String chargeCode) {
        this.chargeCode = chargeCode;
    }

    public String getConfigDesc() {
        return configDesc;
    }

    public void setConfigDesc(String configDesc) {
        this.configDesc = configDesc;
    }

    public boolean isDisplayProductDesc() {
        return displayProductDesc;
    }

    public void setDisplayProductDesc(boolean displayProductDesc) {
        this.displayProductDesc = displayProductDesc;
    }

    public boolean isDisplayConfigDesc() {
        return displayConfigDesc;
    }

    public void setDisplayConfigDesc(boolean displayConfigDesc) {
        this.displayConfigDesc = displayConfigDesc;
    }

    public boolean isDisplayChargeDesc() {
        return displayChargeDesc;
    }

    public void setDisplayChargeDesc(boolean displayChargeDesc) {
        this.displayChargeDesc = displayChargeDesc;
    }

    public boolean isDisplayParentDesc() {
        return displayParentDesc;
    }

    public void setDisplayParentDesc(boolean displayParentDesc) {
        this.displayParentDesc = displayParentDesc;
    }

    public String getDiscountFormCode() {
        return discountFormCode;
    }

    public void setDiscountFormCode(String discountFormCode) {
        this.discountFormCode = discountFormCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getCheckParentItemCode() {
        return checkParentItemCode;
    }

    public void setCheckParentItemCode(String checkParentItemCode) {
        this.checkParentItemCode = checkParentItemCode;
    }

    public String getCheckParentItemDesc() {
        return checkParentItemDesc;
    }

    public void setCheckParentItemDesc(String checkParentItemDesc) {
        this.checkParentItemDesc = checkParentItemDesc;
    }

    public String getCatalogRrp() {
        return catalogRrp;
    }

    public void setCatalogRrp(String catalogRrp) {
        this.catalogRrp = catalogRrp;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

}
