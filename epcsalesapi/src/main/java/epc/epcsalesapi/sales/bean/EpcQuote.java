/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.io.Serializable;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author KerryTsang
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcQuote implements Serializable {
    private String id; // quote guid
    private String name;
    private String quoteNumber;
    private String created;
    private HashMap<String, Object> contextData;
    private String updated;
    private String customerRef;
    private EpcQuoteItem[] items;
    private int quoteType;
    private String orderId;
    private String cpqOrderId;
    private String cpqSupplementalOrderId;
    private String submissionDate;
    private String rootId;
    private String[] quoteHistory;
    private String pricingDate;
    private String lastUserName;
    private String status;
    private HashMap<String, Object> order;
    private boolean isConverted;
    private HashMap<String, Object> currentValidation;
    private boolean isLocked;

    public EpcQuote() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuoteNumber() {
        return quoteNumber;
    }

    public void setQuoteNumber(String quoteNumber) {
        this.quoteNumber = quoteNumber;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public HashMap<String, Object> getContextData() {
        return contextData;
    }

    public void setContextData(HashMap<String, Object> contextData) {
        this.contextData = contextData;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getCustomerRef() {
        return customerRef;
    }

    public void setCustomerRef(String customerRef) {
        this.customerRef = customerRef;
    }

    public EpcQuoteItem[] getItems() {
        return items;
    }

    public void setItems(EpcQuoteItem[] items) {
        this.items = items;
    }

    public int getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(int quoteType) {
        this.quoteType = quoteType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCpqOrderId() {
        return cpqOrderId;
    }

    public void setCpqOrderId(String cpqOrderId) {
        this.cpqOrderId = cpqOrderId;
    }

    public String getCpqSupplementalOrderId() {
        return cpqSupplementalOrderId;
    }

    public void setCpqSupplementalOrderId(String cpqSupplementalOrderId) {
        this.cpqSupplementalOrderId = cpqSupplementalOrderId;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    public String[] getQuoteHistory() {
        return quoteHistory;
    }

    public void setQuoteHistory(String[] quoteHistory) {
        this.quoteHistory = quoteHistory;
    }

    public String getPricingDate() {
        return pricingDate;
    }

    public void setPricingDate(String pricingDate) {
        this.pricingDate = pricingDate;
    }

    public String getLastUserName() {
        return lastUserName;
    }

    public void setLastUserName(String lastUserName) {
        this.lastUserName = lastUserName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public HashMap<String, Object> getOrder() {
        return order;
    }

    public void setOrder(HashMap<String, Object> order) {
        this.order = order;
    }

    public boolean isIsConverted() {
        return isConverted;
    }

    public void setIsConverted(boolean isConverted) {
        this.isConverted = isConverted;
    }

    public HashMap<String, Object> getCurrentValidation() {
        return currentValidation;
    }

    public void setCurrentValidation(HashMap<String, Object> currentValidation) {
        this.currentValidation = currentValidation;
    }

    public boolean isIsLocked() {
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    
}
