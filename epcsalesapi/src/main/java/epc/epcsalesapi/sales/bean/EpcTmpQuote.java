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
public class EpcTmpQuote {
    private HashMap<String, Object> contextData;
    private String customerRef;
//    private EpcQuoteItem[] items;
    private int quoteType;

    public EpcTmpQuote() {
    }

    public HashMap<String, Object> getContextData() {
        return contextData;
    }

    public void setContextData(HashMap<String, Object> contextData) {
        this.contextData = contextData;
    }

    public String getCustomerRef() {
        return customerRef;
    }

    public void setCustomerRef(String customerRef) {
        this.customerRef = customerRef;
    }

//    public EpcQuoteItem[] getItems() {
//        return items;
//    }
//
//    public void setItems(EpcQuoteItem[] items) {
//        this.items = items;
//    }

    public int getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(int quoteType) {
        this.quoteType = quoteType;
    }
    
    
}
