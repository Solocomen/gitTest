package epc.epcsalesapi.sales.bean;

import java.math.BigDecimal;
import java.util.ArrayList;

public class EpcDefaultPayment {
    
    private int paymentId;
    private String paymentType;
    private String paymentCode;
    private BigDecimal paymentAmount;
    private String reference1;
    private String reference2;
    private boolean readOnly;
    private String caseId;
    private ArrayList<EpcPaymentInfo> paymentInfoList;

    public EpcDefaultPayment() {
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getReference1() {
        return reference1;
    }

    public void setReference1(String reference1) {
        this.reference1 = reference1;
    }

    public String getReference2() {
        return reference2;
    }

    public void setReference2(String reference2) {
        this.reference2 = reference2;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public ArrayList<EpcPaymentInfo> getPaymentInfoList() {
        return paymentInfoList;
    }

    public void setPaymentInfoList(ArrayList<EpcPaymentInfo> paymentInfoList) {
        this.paymentInfoList = paymentInfoList;
    }

}
