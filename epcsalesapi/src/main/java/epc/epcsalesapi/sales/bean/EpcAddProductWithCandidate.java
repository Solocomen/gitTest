/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.util.Map;

/**
 *
 * @author KerryTsang
 */
public class EpcAddProductWithCandidate {
    private String productId;
    private String linkedItemId;
    private String itemAction;
    private String quoteLastUpdated;
    private Map<String, Object> productCandidate;

    public EpcAddProductWithCandidate() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getLinkedItemId() {
        return linkedItemId;
    }

    public void setLinkedItemId(String linkedItemId) {
        this.linkedItemId = linkedItemId;
    }

    public String getItemAction() {
        return itemAction;
    }

    public void setItemAction(String itemAction) {
        this.itemAction = itemAction;
    }

    public String getQuoteLastUpdated() {
        return quoteLastUpdated;
    }

    public void setQuoteLastUpdated(String quoteLastUpdated) {
        this.quoteLastUpdated = quoteLastUpdated;
    }

    public Map<String, Object> getProductCandidate() {
        return productCandidate;
    }

    public void setProductCandidate(Map<String, Object> productCandidate) {
        this.productCandidate = productCandidate;
    }
    
    
}
