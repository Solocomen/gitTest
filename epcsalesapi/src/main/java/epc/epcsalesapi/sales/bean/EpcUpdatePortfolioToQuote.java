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
public class EpcUpdatePortfolioToQuote {
    private String portfolioItemId;
    private String itemAction;

    public EpcUpdatePortfolioToQuote() {
    }

    public String getPortfolioItemId() {
        return portfolioItemId;
    }

    public void setPortfolioItemId(String portfolioItemId) {
        this.portfolioItemId = portfolioItemId;
    }

    public String getItemAction() {
        return itemAction;
    }

    public void setItemAction(String itemAction) {
        this.itemAction = itemAction;
    }
    
    
}
