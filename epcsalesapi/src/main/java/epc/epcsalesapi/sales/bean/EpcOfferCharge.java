package epc.epcsalesapi.sales.bean;

import java.util.List;

public class EpcOfferCharge {
    
    private String caseId;
    private String offerDescription;
    private String msisdn;
    private List<EpcCharge> chargeList;

    public EpcOfferCharge() {
        super();
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
    public List<EpcCharge> getChargeList() {
        return chargeList;
    }
    public void setChargeList(List<EpcCharge> chargeList) {
        this.chargeList = chargeList;
    }
}
