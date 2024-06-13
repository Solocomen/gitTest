/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author KerryTsang
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcSubmitQuoteResult {
    private String quoteId;
    private String quoteReference;
    private String orderId; // return to sales flow
    private String orderStatus;
    private String orderSubmissionDate;
    private String quoteLastUpdated;
    private String orderReference;

    public EpcSubmitQuoteResult() {
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getQuoteReference() {
        return quoteReference;
    }

    public void setQuoteReference(String quoteReference) {
        this.quoteReference = quoteReference;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderSubmissionDate() {
        return orderSubmissionDate;
    }

    public void setOrderSubmissionDate(String orderSubmissionDate) {
        this.orderSubmissionDate = orderSubmissionDate;
    }

    public String getQuoteLastUpdated() {
        return quoteLastUpdated;
    }

    public void setQuoteLastUpdated(String quoteLastUpdated) {
        this.quoteLastUpdated = quoteLastUpdated;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }
    
    
}
