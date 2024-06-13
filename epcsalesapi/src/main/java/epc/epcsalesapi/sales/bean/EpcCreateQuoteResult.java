/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

/**
 *
 * @author KerryTsang
 */
public class EpcCreateQuoteResult {
    private String custId;
    private String cpqQuoteGUID;
    private String orderReference;
    private String result;
    private String errMsg;
    private EpcQuote epcQuote;

    public EpcCreateQuoteResult() {
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getCpqQuoteGUID() {
        return cpqQuoteGUID;
    }

    public void setCpqQuoteGUID(String cpqQuoteGUID) {
        this.cpqQuoteGUID = cpqQuoteGUID;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
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

    public EpcQuote getEpcQuote() {
        return epcQuote;
    }

    public void setEpcQuote(EpcQuote epcQuote) {
        this.epcQuote = epcQuote;
    }

    
    
}
