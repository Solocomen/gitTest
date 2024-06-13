package epc.epcsalesapi.sales.bean.receipt;

import java.util.ArrayList;

public class EpcGetPaymentReceiptResult {
    
    private String result;
    private String errorMessage;
    private ArrayList<EpcReceipt> receiptList;

    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public ArrayList<EpcReceipt> getReceiptList() {
        return receiptList;
    }
    public void setReceiptList(ArrayList<EpcReceipt> receiptList) {
        this.receiptList = receiptList;
    }
    
}
