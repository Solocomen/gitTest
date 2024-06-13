package epc.epcsalesapi.sales.bean;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcVmsVoucherListResponseDetailAssignTransaction {
    private String transactionId;
    private String quoteId;
    private String assignDate;
    private String assignedBy;
    private String smcOrderId;
    private String smcOrderReference;

    public EpcVmsVoucherListResponseDetailAssignTransaction() {
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(String assignDate) {
        this.assignDate = assignDate;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
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

    
}
