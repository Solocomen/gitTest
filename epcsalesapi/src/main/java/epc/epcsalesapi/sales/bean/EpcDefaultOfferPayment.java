package epc.epcsalesapi.sales.bean;

import java.util.List;

public class EpcDefaultOfferPayment {
    
    private String caseId;
    private String offerDescription;
    private String msisdn;
    private List<EpcDefaultPayment> paymentList;

    public EpcDefaultOfferPayment() {

    }
    public String getCaseId() {
        return caseId;
    }
    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }
    public String getOfferDescription() {
        return offerDescription;
    }
    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }
    public String getMsisdn() {
        return msisdn;
    }
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
    public List<EpcDefaultPayment> getPaymentList() {
        return paymentList;
    }
    public void setPaymentList(List<EpcDefaultPayment> paymentList) {
        this.paymentList = paymentList;
    }

}
