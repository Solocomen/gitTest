/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.util.HashMap;

/**
 *
 * @author KerryTsang
 */
public class EpcQuoteContext {
    private String quoteLastUpdated;
    private HashMap<String, Object> contextData;

    public EpcQuoteContext() {
    }

    public String getQuoteLastUpdated() {
        return quoteLastUpdated;
    }

    public void setQuoteLastUpdated(String quoteLastUpdated) {
        this.quoteLastUpdated = quoteLastUpdated;
    }

    public HashMap<String, Object> getContextData() {
        return contextData;
    }

    public void setContextData(HashMap<String, Object> contextData) {
        this.contextData = contextData;
    }
    
    
}
