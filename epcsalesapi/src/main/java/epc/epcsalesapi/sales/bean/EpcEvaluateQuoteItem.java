package epc.epcsalesapi.sales.bean;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcEvaluateQuoteItem {
    private HashMap<String, Object> productCandidate;
    private HashMap<String, Object> metaDataLookup;
    private EpcEvaluateConfiguration configuration;
    private HashMap<String, Object> candidateConfiguration;
    private HashMap<String, Object> currentPricing;
    private HashMap<String, Object> currentValidation;
    private String result;
    private String errMsg;
    private HashMap<String, Object> errMsg2;

    public EpcEvaluateQuoteItem() {}

    public HashMap<String, Object> getProductCandidate() {
        return productCandidate;
    }

    public void setProductCandidate(HashMap<String, Object> productCandidate) {
        this.productCandidate = productCandidate;
    }

    public HashMap<String, Object> getMetaDataLookup() {
        return metaDataLookup;
    }

    public void setMetaDataLookup(HashMap<String, Object> metaDataLookup) {
        this.metaDataLookup = metaDataLookup;
    }

    public EpcEvaluateConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(EpcEvaluateConfiguration configuration) {
        this.configuration = configuration;
    }

    public HashMap<String, Object> getCandidateConfiguration() {
        return candidateConfiguration;
    }

    public void setCandidateConfiguration(HashMap<String, Object> candidateConfiguration) {
        this.candidateConfiguration = candidateConfiguration;
    }

    public HashMap<String, Object> getCurrentPricing() {
        return currentPricing;
    }

    public void setCurrentPricing(HashMap<String, Object> currentPricing) {
        this.currentPricing = currentPricing;
    }

    public HashMap<String, Object> getCurrentValidation() {
        return currentValidation;
    }

    public void setCurrentValidation(HashMap<String, Object> currentValidation) {
        this.currentValidation = currentValidation;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public HashMap<String, Object> getErrMsg2() {
        return errMsg2;
    }

    public void setErrMsg2(HashMap<String, Object> errMsg2) {
        this.errMsg2 = errMsg2;
    }


    
}
