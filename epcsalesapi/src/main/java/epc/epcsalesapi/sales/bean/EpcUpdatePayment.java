package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcUpdatePayment {
    private String custId;
    private int orderId;
    private ArrayList<EpcPayment> paymentList;
    private ArrayList<EpcPayment> resultPaymentList;
    private String saveStatus;
    private String errMsg;

    public EpcUpdatePayment() {
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public ArrayList<EpcPayment> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(ArrayList<EpcPayment> paymentList) {
        this.paymentList = paymentList;
    }

    public ArrayList<EpcPayment> getResultPaymentList() {
        return resultPaymentList;
    }

    public void setResultPaymentList(ArrayList<EpcPayment> resultPaymentList) {
        this.resultPaymentList = resultPaymentList;
    }

    public String getSaveStatus() {
        return saveStatus;
    }

    public void setSaveStatus(String saveStatus) {
        this.saveStatus = saveStatus;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
    
    
}
