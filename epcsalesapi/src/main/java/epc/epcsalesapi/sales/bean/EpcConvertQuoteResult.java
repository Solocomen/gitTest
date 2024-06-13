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
public class EpcConvertQuoteResult {
    private String id; // new quote guid (type 3)
    private String quoteReference;
    private String quoteLastUpdated;
    private String orderLastUpdated;
    private String orderReference;
    private String status;
    private String orderCreationDate;

    public EpcConvertQuoteResult() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuoteReference() {
        return quoteReference;
    }

    public void setQuoteReference(String quoteReference) {
        this.quoteReference = quoteReference;
    }

    public String getQuoteLastUpdated() {
        return quoteLastUpdated;
    }

    public void setQuoteLastUpdated(String quoteLastUpdated) {
        this.quoteLastUpdated = quoteLastUpdated;
    }

    public String getOrderLastUpdated() {
        return orderLastUpdated;
    }

    public void setOrderLastUpdated(String orderLastUpdated) {
        this.orderLastUpdated = orderLastUpdated;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderCreationDate() {
        return orderCreationDate;
    }

    public void setOrderCreationDate(String orderCreationDate) {
        this.orderCreationDate = orderCreationDate;
    }
    
    
}
