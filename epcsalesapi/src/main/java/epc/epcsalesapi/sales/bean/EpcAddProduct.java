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
public class EpcAddProduct {
    private String productId;
    private String linkedItemId;
    private String itemAction;
    private String quoteLastUpdated;

    public EpcAddProduct() {
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
    
    
}
