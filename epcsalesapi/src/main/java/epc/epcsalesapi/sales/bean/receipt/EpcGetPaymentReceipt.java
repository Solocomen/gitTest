package epc.epcsalesapi.sales.bean.receipt;

import java.util.ArrayList;

public class EpcGetPaymentReceipt {
    
    private int orderId;
    private String caseId;
    private ArrayList<String> receiptNo;

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
    public ArrayList<String> getReceiptNo() {
        return receiptNo;
    }
    public void setReceiptNo(ArrayList<String> receiptNo) {
        this.receiptNo = receiptNo;
    }

}
