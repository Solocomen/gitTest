/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.util.List;

/**
 *
 * @author williamtam
 */
public class EpcGetChargeResult {
    private String result;
    private String errorCode;
    private String errorMessage;
    private List<EpcOfferCharge> offerChargeList;

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

    public List<EpcOfferCharge> getOfferChargeList() {
        return offerChargeList;
    }

    public void setOfferChargeList(List<EpcOfferCharge> offerChargeList) {
        this.offerChargeList = offerChargeList;
    }

}
