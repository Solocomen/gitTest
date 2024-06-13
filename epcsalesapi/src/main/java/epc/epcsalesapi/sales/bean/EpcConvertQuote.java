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
public class EpcConvertQuote {
    private String quoteLastUpdated;
    private String externalOrderId;
    private String activationDate;

    public EpcConvertQuote() {
    }

    public String getQuoteLastUpdated() {
        return quoteLastUpdated;
    }

    public void setQuoteLastUpdated(String quoteLastUpdated) {
        this.quoteLastUpdated = quoteLastUpdated;
    }

    public String getExternalOrderId() {
        return externalOrderId;
    }

    public void setExternalOrderId(String externalOrderId) {
        this.externalOrderId = externalOrderId;
    }

    public String getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }
    
    
}
