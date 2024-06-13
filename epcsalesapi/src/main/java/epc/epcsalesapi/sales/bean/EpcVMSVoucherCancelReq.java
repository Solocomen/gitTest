package epc.epcsalesapi.sales.bean;

public class EpcVMSVoucherCancelReq {
    private String couponId;
    private String customerId;
    private String quoteId;
    private String transactionId;
    private String smcOrderReference;
    private boolean assignCancel;
    private boolean remove;

    public EpcVMSVoucherCancelReq() {}

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSmcOrderReference() {
        return smcOrderReference;
    }

    public void setSmcOrderReference(String smcOrderReference) {
        this.smcOrderReference = smcOrderReference;
    }

    public boolean isAssignCancel() {
        return assignCancel;
    }

    public void setAssignCancel(boolean assignCancel) {
        this.assignCancel = assignCancel;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    
}
