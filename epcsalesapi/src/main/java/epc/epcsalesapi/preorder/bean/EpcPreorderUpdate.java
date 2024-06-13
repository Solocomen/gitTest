package epc.epcsalesapi.preorder.bean;

public class EpcPreorderUpdate {
    
    private String action;
    private int caseId;
    private String invoiceNo;
    private String receiptNo;
    private int updateUser;
    private int updateSalesman;
    private String invoiceHKID;

    public EpcPreorderUpdate() {}

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public int getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(int updateUser) {
        this.updateUser = updateUser;
    }

    public int getUpdateSalesman() {
        return updateSalesman;
    }

    public void setUpdateSalesman(int updateSalesman) {
        this.updateSalesman = updateSalesman;
    }

    public String getInvoiceHKID() {
        return invoiceHKID;
    }

    public void setInvoiceHKID(String invoiceHKID) {
        this.invoiceHKID = invoiceHKID;
    }

    
}
