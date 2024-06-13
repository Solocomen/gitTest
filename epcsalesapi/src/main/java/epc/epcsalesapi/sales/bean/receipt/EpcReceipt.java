package epc.epcsalesapi.sales.bean.receipt;

import java.math.BigDecimal;

public class EpcReceipt {
    
    private String receiptNo;
    private java.sql.Date receiptDate;
    private String timeIssued;
    private String binNumber;
    private String cellular;
    private String subscriber;
    private BigDecimal receAmount;
    private String userId;
    private String stat;

    public String getReceiptNo() {
        return receiptNo;
    }
    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }
    public java.sql.Date getReceiptDate() {
        return receiptDate;
    }
    public void setReceiptDate(java.sql.Date receiptDate) {
        this.receiptDate = receiptDate;
    }
    public String getTimeIssued() {
        return timeIssued;
    }
    public void setTimeIssued(String timeIssued) {
        this.timeIssued = timeIssued;
    }
    public String getBinNumber() {
        return binNumber;
    }
    public void setBinNumber(String binNumber) {
        this.binNumber = binNumber;
    }
    public String getCellular() {
        return cellular;
    }
    public void setCellular(String cellular) {
        this.cellular = cellular;
    }
    public String getSubscriber() {
        return subscriber;
    }
    public void setSubscriber(String subscriber) {
        this.subscriber = subscriber;
    }
    public BigDecimal getReceAmount() {
        return receAmount;
    }
    public void setReceAmount(BigDecimal receAmount) {
        this.receAmount = receAmount;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getStat() {
        return stat;
    }
    public void setStat(String stat) {
        this.stat = stat;
    }
    
}
