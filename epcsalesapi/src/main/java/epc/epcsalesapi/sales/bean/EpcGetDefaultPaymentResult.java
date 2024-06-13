package epc.epcsalesapi.sales.bean;

import java.util.List;

public class EpcGetDefaultPaymentResult {
    
    private String result;
    private String errorCode;
    private String errorMessage;
    //private List<EpcDefaultOfferPayment> offerPaymentList;
    private List<EpcDefaultPayment> paymentList;
     
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public String getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    //public List<EpcDefaultOfferPayment> getOfferPaymentList() {
    //    return offerPaymentList;
    //}
    //public void setOfferPaymentList(List<EpcDefaultOfferPayment> offerPaymentList) {
    //    this.offerPaymentList = offerPaymentList;
    //}
    public List<EpcDefaultPayment> getPaymentList() {
        return paymentList;
    }
    public void setPaymentList(List<EpcDefaultPayment> paymentList) {
        this.paymentList = paymentList;
    }

}
