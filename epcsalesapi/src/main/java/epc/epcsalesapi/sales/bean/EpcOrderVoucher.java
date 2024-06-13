package epc.epcsalesapi.sales.bean;

public class EpcOrderVoucher {
    private int orderId;
    private String caseId;
    private String itemId;
    private String voucherGuid;
    private String voucherMasterId;
    private String transactionId;
    private String voucherCode;
    private String assignId;

    public EpcOrderVoucher() {}

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getAssignId() {
        return assignId;
    }

    public void setAssignId(String assignId) {
        this.assignId = assignId;
    }
    
}
