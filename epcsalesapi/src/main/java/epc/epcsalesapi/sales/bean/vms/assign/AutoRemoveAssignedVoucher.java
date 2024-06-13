package epc.epcsalesapi.sales.bean.vms.assign;

public class AutoRemoveAssignedVoucher {
    private int orderId;
    private String caseId;
    private String itemId;
    private String voucherMasterId;
    private String assignId;
    private String transactionId;
    private String voucherCreateDate;
    private String orderReference;
    private String orderStatus;
    private String log;

    public AutoRemoveAssignedVoucher() {
    }
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
    public String getVoucherMasterId() {
        return voucherMasterId;
    }
    public void setVoucherMasterId(String voucherMasterId) {
        this.voucherMasterId = voucherMasterId;
    }
    public String getAssignId() {
        return assignId;
    }
    public void setAssignId(String assignId) {
        this.assignId = assignId;
    }
    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    public String getVoucherCreateDate() {
        return voucherCreateDate;
    }
    public void setVoucherCreateDate(String voucherCreateDate) {
        this.voucherCreateDate = voucherCreateDate;
    }
    public String getOrderReference() {
        return orderReference;
    }
    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }
    public String getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    public String getLog() {
        return log;
    }
    public void setLog(String log) {
        this.log = log;
    }

    
    
}
